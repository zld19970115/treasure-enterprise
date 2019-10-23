package io.treasure.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import io.treasure.common.constant.Constant;
import io.treasure.common.page.PageData;
import io.treasure.common.service.impl.CrudServiceImpl;
import io.treasure.common.utils.Result;
import io.treasure.dao.MerchantCouponDao;
import io.treasure.dto.MerchantCouponDTO;
import io.treasure.dto.MerchantRoomDTO;
import io.treasure.entity.MerchantCouponEntity;
import io.treasure.entity.MerchantRoomEntity;
import io.treasure.service.MerchantCouponService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.*;

/**
 * 商户端优惠卷
 *
 * @author Super 63600679@qq.com
 * @since 1.0.0 2019-08-23
 */
@Service
public class MerchantCouponServiceImpl extends CrudServiceImpl<MerchantCouponDao, MerchantCouponEntity, MerchantCouponDTO> implements MerchantCouponService {

    @Override
    public QueryWrapper<MerchantCouponEntity> getWrapper(Map<String, Object> params) {
        String id = (String) params.get("id");
        String status = (String) params.get("status");
        //商户编号
        String merchantId = (String) params.get("merchantId");
        QueryWrapper<MerchantCouponEntity> wrapper = new QueryWrapper<>();
        wrapper.eq(StringUtils.isNotBlank(id), "id", id);
        wrapper.eq(StringUtils.isNotBlank(status), "status", status);
        wrapper.eq(StringUtils.isNotBlank(merchantId), "merchant_id", merchantId);
        return wrapper;
    }


    @Override
    public void updateStatusById(long id, int status) {
        baseDao.updateStatusById(id, status);
    }

    @Override
    public MerchantCouponEntity getAllById(Long id) {
        return baseDao.getAllById(id);
    }

    @Override
    public List<MerchantCouponDTO> getMoneyOffByMerchantId(long merchantId,long userId) {
        return baseDao.getMoneyOffByMerchantId(merchantId,userId);
    }

    @Override
    public Result<MerchantCouponDTO> getDifference(long merchantId, BigDecimal totalMoney,long userId) {
        BigDecimal ends = new BigDecimal(0);
        List<MerchantCouponDTO> moneyOffByMerchantId = getMoneyOffByMerchantId(merchantId,userId);

        List<BigDecimal> a = new ArrayList<>();
        for (int i = 0; i < moneyOffByMerchantId.size(); i++) {
            Double money = moneyOffByMerchantId.get(i).getMoney();
            BigDecimal moneys = new BigDecimal(money);
            a.add(moneys);
        }
        a.add(totalMoney);
        Collections.sort(a);
        for (int i = 0; i < a.size(); i++) {
            BigDecimal num = a.get(i);
            if (num.equals(totalMoney)) {
                if (i == a.size() - 1 ||i==0) {
                    if(i==0){
                        ends = ends.add(a.get(i));
                    }else {
                        ends = ends.add(a.get(i - 1));
                    }

                    break;
                }
                BigDecimal up = a.get(i - 1);
                BigDecimal down = a.get(i + 1);
                if (i != 0) {
                    BigDecimal uend = up.subtract(totalMoney);
                    uend=uend.abs();

                    BigDecimal dend = down.subtract(totalMoney);
                    dend=dend.abs();
                    if (uend.compareTo(dend) >= 0) {
                        ends = ends.add(a.get(i + 1));
                        break;
                    } else {
                        ends = ends.add(a.get(i - 1));
                        break;
                    }
                } else {
                    ends = ends.add(a.get(i));
                    break;
                }
            }
        }
        MerchantCouponDTO mce = new MerchantCouponDTO();
        for (int i = 0; i < moneyOffByMerchantId.size(); i++) {
            Double money = moneyOffByMerchantId.get(i).getMoney();
            BigDecimal moneys = new BigDecimal(money);

            if (moneys.equals(ends)) {
                mce = moneyOffByMerchantId.get(i);
                break;
            }
        }
        return new Result<MerchantCouponDTO>().ok(mce);
    }

    @Override
    public PageData<MerchantCouponDTO> listPage(Map<String, Object> param) {
        IPage<MerchantCouponEntity> pages=getPage(param, Constant.CREATE_DATE,false);
        String merchantId=(String)param.get("merchantId");
        if (StringUtils.isNotBlank(merchantId) && StringUtils.isNotEmpty(merchantId)) {
            String[] str = merchantId.split(",");
            param.put("merchantIdStr", str);
        }else{
            param.put("merchantId",null);
        }
        List<MerchantCouponDTO> list=baseDao.listPage(param);
        return getPageData(list,pages.getTotal(), MerchantCouponDTO.class);
    }
}

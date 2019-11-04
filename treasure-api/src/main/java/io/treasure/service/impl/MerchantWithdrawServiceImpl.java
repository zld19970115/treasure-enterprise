package io.treasure.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import io.treasure.common.constant.Constant;
import io.treasure.common.page.PageData;
import io.treasure.common.service.impl.CrudServiceImpl;
import io.treasure.dao.MerchantWithdrawDao;
import io.treasure.dto.GoodDTO;
import io.treasure.dto.MerchantWithdrawDTO;
import io.treasure.entity.GoodEntity;
import io.treasure.entity.MasterOrderEntity;
import io.treasure.entity.MerchantWithdrawEntity;
import io.treasure.service.MerchantWithdrawService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * 提现表
 *
 * @author Super 63600679@qq.com
 * @since 1.0.0 2019-08-20
 */
@Service
public class MerchantWithdrawServiceImpl extends CrudServiceImpl<MerchantWithdrawDao, MerchantWithdrawEntity, MerchantWithdrawDTO> implements MerchantWithdrawService {

    @Override
    public QueryWrapper<MerchantWithdrawEntity> getWrapper(Map<String, Object> params){
        String id = (String)params.get("id");
        //状态
        String status=(String)params.get("status");
        //审核状态
        String verifyState=(String)params.get("verifyState");

        QueryWrapper<MerchantWithdrawEntity> wrapper = new QueryWrapper<>();
        wrapper.eq(StringUtils.isNotBlank(id), "id", id);
        wrapper.eq(StringUtils.isNotBlank(status),"status",status);
        wrapper.eq(StringUtils.isNotBlank(verifyState),"verify_state",verifyState);
        return wrapper;
    }

    /**
     * 修改状态
     * @param id
     * @param status
     */
    @Override
    public void updateStatusById(long id, int status) {
        baseDao.updateStatusById(id,status);
    }

    @Override
    public BigDecimal selectTotalCath(long martId) {
        return baseDao.selectTotalCath(martId);
    }

    @Override
    public Double selectByMartId(long martId) {
        return baseDao.selectByMartId(martId);
    }

    @Override
    public  List<MerchantWithdrawEntity>  selectPoByMartID(long martId) {
        return baseDao.selectPoByMartID(martId);
    }

    @Override
    public Double selectAlreadyCash(long martId) {
        return baseDao.selectAlreadyCash(martId);
    }

    /**
     * 列表查询
     * @param params
     * @return
     */
    @Override
    public PageData<MerchantWithdrawDTO> listPage(Map<String, Object> params) {
        IPage<MerchantWithdrawEntity> pages=getPage(params, Constant.CREATE_DATE,false);
        String merchantId=(String)params.get("merchantId");
        if (StringUtils.isNotBlank(merchantId) && StringUtils.isNotEmpty(merchantId)) {
            String[] str = merchantId.split(",");
            params.put("merchantIdStr", str);
        }else{
            params.put("merchantId",null);
        }
        List<MerchantWithdrawDTO> list=baseDao.listPage(params);
        return getPageData(list,pages.getTotal(), MerchantWithdrawDTO.class);
    }

    @Override
    public List<MasterOrderEntity> selectOrderByMartID(Long martId) {
        return baseDao.selectOrderByMartID(martId);
    }

    @Override
    public void verify(long id,long verify, int verifyStatus, String verifyReason, Date verifyDate) {
        baseDao.verify(id,verify,verifyStatus,verifyReason,verifyDate);
    }

    @Override
    public String selectWithStatus() {
        String s = baseDao.selectWithStatus();

        if ("1".equals(s)){
            return "只能适应支付宝提现";
        }
        if ("2".equals(s)){
            return "只能适应微信提现";
        }
        if ("3".equals(s)){
            return "支付宝微信都可以使用";
        }
        return  null;
    }
}
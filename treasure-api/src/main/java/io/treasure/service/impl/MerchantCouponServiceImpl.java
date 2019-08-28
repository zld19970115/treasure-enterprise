package io.treasure.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import io.treasure.common.service.impl.CrudServiceImpl;
import io.treasure.dao.MerchantCouponDao;
import io.treasure.dto.MerchantCouponDTO;
import io.treasure.entity.MerchantCouponEntity;
import io.treasure.service.MerchantCouponService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.util.Map;

/**
 * 商户端优惠卷
 *
 * @author Super 63600679@qq.com
 * @since 1.0.0 2019-08-23
 */
@Service
public class MerchantCouponServiceImpl extends CrudServiceImpl<MerchantCouponDao, MerchantCouponEntity, MerchantCouponDTO> implements MerchantCouponService {

    @Override
    public QueryWrapper<MerchantCouponEntity> getWrapper(Map<String, Object> params){
        String id = (String)params.get("id");
        String status=(String)params.get("status");
        //商户编号
        String merchantId=(String)params.get("merchantId");
        QueryWrapper<MerchantCouponEntity> wrapper = new QueryWrapper<>();
        wrapper.eq(StringUtils.isNotBlank(id), "id", id);
        wrapper.eq(StringUtils.isNotBlank(status),"status",status);
        wrapper.eq(StringUtils.isNotBlank(merchantId),"merchant_id",merchantId);
        return wrapper;
    }


    @Override
    public void updateStatusById(long id, int status) {
        baseDao.updateStatusById(id,status);
    }
}
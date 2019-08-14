package io.treasure.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import io.treasure.common.service.impl.CrudServiceImpl;
import io.treasure.dao.MerchantOrderDao;
import io.treasure.dto.MerchantOrderDTO;
import io.treasure.enm.Common;
import io.treasure.entity.MerchantOrderEntity;
import io.treasure.service.MerchantOrderService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.util.Map;

/**
 * 商户订单管理
 *
 * @author Super 63600679@qq.com
 * @since 1.0.0 2019-08-09
 */
@Service
public class MerchantOrderServiceImpl extends CrudServiceImpl<MerchantOrderDao, MerchantOrderEntity, MerchantOrderDTO> implements MerchantOrderService {

    @Override
    public QueryWrapper<MerchantOrderEntity> getWrapper(Map<String, Object> params){
        String id = (String)params.get("id");
        //状态
        String status=(String)params.get("status");
        //订单支付状态
        String payStatus=(String)params.get("payStatus");
        //商户
        String merchantId=(String)params.get("merchantId");
        QueryWrapper<MerchantOrderEntity> wrapper = new QueryWrapper<>();
        if(StringUtils.isNotBlank(payStatus) && payStatus.indexOf(",")>-1){
            wrapper.in(StringUtils.isNotBlank(payStatus),"pay_status",payStatus);
        }else{
            wrapper.eq(StringUtils.isNotBlank(payStatus), "pay_status",payStatus);
        }
        wrapper.eq(StringUtils.isNotBlank(status),"status", status);
        wrapper.eq(StringUtils.isNotBlank(id), "id", id);
        wrapper.eq(StringUtils.isNotBlank(merchantId),"merchant_id",merchantId);
        return wrapper;
    }
    /**
     * 删除订单
     * @param id
     */
    @Override
    public void remove(long id,int status) {
        baseDao.updateStatus(id,status);
    }
}
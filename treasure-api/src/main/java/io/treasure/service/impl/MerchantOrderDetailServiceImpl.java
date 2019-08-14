package io.treasure.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import io.treasure.common.service.impl.CrudServiceImpl;
import io.treasure.dao.MerchantOrderDetailDao;
import io.treasure.dto.MerchantOrderDetailDTO;
import io.treasure.entity.MerchantOrderDetailEntity;
import io.treasure.service.MerchantOrderDetailService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

/**
 * 商户订单明细管理
 *
 * @author Super 63600679@qq.com
 * @since 1.0.0 2019-08-09
 */
@Service
public class MerchantOrderDetailServiceImpl extends CrudServiceImpl<MerchantOrderDetailDao, MerchantOrderDetailEntity, MerchantOrderDetailDTO> implements MerchantOrderDetailService {

    @Override
    public QueryWrapper<MerchantOrderDetailEntity> getWrapper(Map<String, Object> params){
        String id = (String)params.get("id");
        QueryWrapper<MerchantOrderDetailEntity> wrapper = new QueryWrapper<>();
        wrapper.eq(StringUtils.isNotBlank(id), "id", id);

        return wrapper;
    }

    /**
     * 删除订单
     * @param orderId
     * @param status
     */
    @Override
    public void remove(long orderId, int status) {
        baseDao.updateStatus(orderId,status);
    }

    /**
     * 根据订单号查询订单明细
     * @param orderId
     * @return
     */
    @Override
    public List<MerchantOrderDetailEntity> getByOrderId(long orderId,int status) {
        return baseDao.getByOrderId(orderId, status);
    }
}
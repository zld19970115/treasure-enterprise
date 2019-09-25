package io.treasure.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import io.treasure.common.constant.Constant;
import io.treasure.common.page.PageData;
import io.treasure.common.service.impl.CrudServiceImpl;
import io.treasure.dao.RefundOrderDao;
import io.treasure.dto.RefundOrderDTO;
import io.treasure.entity.RefundOrderEntity;
import io.treasure.service.RefundOrderService;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Service;


import java.util.List;
import java.util.Map;
@Service
public class RefundOrderServiceImpl  extends CrudServiceImpl<RefundOrderDao, RefundOrderEntity, RefundOrderDTO> implements RefundOrderService {
    @Override
    public QueryWrapper<RefundOrderEntity> getWrapper(Map<String, Object> params) {
        String refundId = (String)params.get("refundId");

        QueryWrapper<RefundOrderEntity> wrapper = new QueryWrapper<>();
        wrapper.eq(StringUtils.isNotBlank(refundId), "refundId", refundId);

        return wrapper;
    }

    @Override
    public void insertRefundOrder(RefundOrderEntity refundOrderDTO) {
        baseDao.insertRefundOrder(refundOrderDTO);
    }

    /**
     * 更新refund_order中主键ID
     * @param refundId
     * @param orderId
     * @param goodId
     */
    @Override
    public void updateRefundId(String refundId, String orderId, Long goodId) {
        baseDao.updateRefundId(refundId,orderId,goodId);
    }

    /**
     * 通过商户id查询此商户有多少退款信息
     * @param params
     * @return
     */
    @Override
    public PageData<RefundOrderEntity> getRefundOrderByMerchantId(Map<String, Object> params) {
        IPage<RefundOrderEntity> pages=getPage(params, Constant.CREATE_DATE,false);
        List<RefundOrderEntity> list=baseDao.getRefundOrderByMerchantId(params);
        return getPageData(list,pages.getTotal(), RefundOrderEntity.class);
    }

    @Override
    public void updateDispose(int dispose ,String orderId, Long goodId) {
        baseDao.updateDispose(dispose,orderId,goodId);
    }


}

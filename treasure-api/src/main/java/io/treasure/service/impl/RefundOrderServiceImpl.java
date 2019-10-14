package io.treasure.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import io.treasure.common.constant.Constant;
import io.treasure.common.page.PageData;
import io.treasure.common.service.impl.CrudServiceImpl;
import io.treasure.dao.RefundOrderDao;
import io.treasure.dto.MerchantRoomParamsSetDTO;
import io.treasure.dto.OrderDTO;
import io.treasure.dto.RefundOrderDTO;
import io.treasure.dto.SlaveOrderDTO;
import io.treasure.entity.MasterOrderEntity;
import io.treasure.entity.RefundOrderEntity;
import io.treasure.service.MasterOrderService;
import io.treasure.service.MerchantRoomParamsSetService;
import io.treasure.service.RefundOrderService;
import io.treasure.service.SlaveOrderService;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@Service
public class RefundOrderServiceImpl extends CrudServiceImpl<RefundOrderDao, RefundOrderEntity, RefundOrderDTO> implements RefundOrderService {
    @Autowired
    private MasterOrderService masterOrderService;

    @Autowired
    private SlaveOrderService slaveOrderService;

    @Autowired
    private MerchantRoomParamsSetService merchantRoomParamsSetService;

    @Override
    public QueryWrapper<RefundOrderEntity> getWrapper(Map<String, Object> params) {
        String refundId = (String) params.get("refundId");

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
     *
     * @param refundId
     * @param orderId
     * @param goodId
     */
    @Override
    public void updateRefundId(String refundId, String orderId, Long goodId) {
        baseDao.updateRefundId(refundId, orderId, goodId);
    }

    /**
     * 通过商户id查询此商户有多少退款信息
     *
     * @param params
     * @return
     */
    @Override
    public PageData<RefundOrderDTO> getRefundOrderByMerchantId(Map<String, Object> params) {
        IPage<RefundOrderEntity> pages = getPage(params, Constant.CREATE_DATE, false);
        List<RefundOrderDTO> list = baseDao.getRefundOrderByMerchantId(params);
        for (RefundOrderDTO s:list) {
            String orderId = s.getOrderId();
            MasterOrderEntity order = masterOrderService.selectByOrderId(orderId);
            if(order.getReservationId()!=null){
                MerchantRoomParamsSetDTO merchantRoomParamsSetDTO = this.merchantRoomParamsSetService.get(order.getReservationId());
                s.setRoomName(merchantRoomParamsSetDTO.getRoomName());
            }
        }
        return getPageData(list, pages.getTotal(), RefundOrderDTO.class);
    }

    @Override
    public void updateDispose(int dispose, String orderId, Long goodId) {
        baseDao.updateDispose(dispose, orderId, goodId);
    }

    /**
     * 商户同意退款后，更新主订单实付金额
     *
     * @param orderId
     * @param goodId
     */
    @Override
    public void updateMasterOrderPayMoney(String orderId, Long goodId) {
        //获取主订单信息
        OrderDTO order = masterOrderService.getOrder(orderId);
        // 获取主订单实付金额
        BigDecimal payMoney = order.getPayMoney();
        //获取订单菜品表退菜信息
        SlaveOrderDTO allGoods = slaveOrderService.getAllGoods(orderId, goodId);
        //退菜金额=退菜数量*退菜单价
        BigDecimal  totalRefundMoney=(allGoods.getQuantity().multiply(allGoods.getPrice())).setScale(2,BigDecimal.ROUND_DOWN);
        masterOrderService.updatePayMoney(totalRefundMoney,orderId);

    }


}

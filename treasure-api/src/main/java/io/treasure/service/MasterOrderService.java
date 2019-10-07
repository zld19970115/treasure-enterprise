package io.treasure.service;

import io.treasure.common.page.PageData;
import io.treasure.common.utils.Result;
import io.treasure.dto.*;
import io.treasure.enm.Constants;
import io.treasure.entity.ClientUserEntity;
import io.treasure.entity.MasterOrderEntity;

import io.treasure.common.service.CrudService;
import io.treasure.entity.SlaveOrderEntity;
import org.apache.ibatis.annotations.Param;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Map;


/**
 * 订单表
 *
 * @author Super 63600679@qq.com
 * @since 1.0.0 2019-08-23
 */
public interface MasterOrderService extends CrudService<MasterOrderEntity, MasterOrderDTO> {

    //取消订单
    Result caleclUpdate(long id,int status, long verify, Date verify_date, String refundReason) throws Exception;
    //接受订单
    Result acceptUpdate(long id,int status, long verify, Date verify_date, String refundReason);
    //完成订单
    Result finishUpdate(long id,int status, long verify, Date verify_date, String refundReason);
    //同意退款
    Result refundYesUpdate(long id,int status, long verify, Date verify_date, String refundReason) throws Exception;
    //拒绝退款订单
    Result refundNoUpdate(long id,int status, long verify, Date verify_date, String refundReason) throws Exception;
    //Object updateStatusAndReason(long id,int status, long verify, Date verify_date, String refundReason) throws Exception;

    OrderDTO getOrder(String orderId);
    MasterOrderEntity selectByOrderId(String orderId);
    Result orderSave(OrderDTO dto, List<SlaveOrderEntity> dtoList, ClientUserEntity user);
    Result saveOrder(OrderDTO dto, List<SlaveOrderEntity> dtoList, ClientUserEntity user);
    PageData<OrderDTO> listPage(Map<String, Object> params);
    //商户端订单列表
    PageData<MerchantOrderDTO> listMerchantPage(Map<String, Object> params);
    Result updateByCheck(Long id);
    List<MasterOrderEntity>  selectPOrderId(String orderId);
    Result updateByCancel(Map<String, Object> params);

    Result updateByApplyRefund(Map<String, Object> params);

    Map<String, String> getNotify(Constants.PayMode alipay, BigDecimal bigDecimal, String out_trade_no);


    /**
     * 优惠卷
     * @param dct
     * @return
     */
    DesignConditionsDTO calculateCoupon(DesignConditionsDTO dct);

    /**
     * 计算赠送金
     * @param dct
     * @return
     */
    DesignConditionsDTO calculateGift(DesignConditionsDTO dct);

    /**
     * 优惠卷与赠送金
     * @param dct
     * @return
     */
    DesignConditionsDTO calculateGiftCoupon(DesignConditionsDTO dct);

    void updateOrderStatus(int status,  String orderId);

    void updatePayMode(@Param("payMode") String payMode,@Param("orderId") String orderId);
    MasterOrderDTO getOrderById(long id);

    Result caleclUpdate(long id, long verify, Date date, String verify_reason);

    Result cancelOrder(long id);
}
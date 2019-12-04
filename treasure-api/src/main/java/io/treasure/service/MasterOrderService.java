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
    Result                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                     saveOrder(OrderDTO dto, List<SlaveOrderEntity> dtoList, ClientUserEntity user);
    PageData<OrderDTO> listPage(Map<String, Object> params);
    //商户端订单列表
    PageData<MerchantOrderDTO> listMerchantPage(Map<String, Object> params);
    //预约列表
    PageData<MerchantOrderDTO> listMerchantPage2(Map<String, Object> params);
    Result updateByCheck(Long id);
    List<MasterOrderEntity>  selectPOrderId(String orderId);
    Result updateByCancel(Map<String, Object> params);

    Result updateByApplyRefund(Map<String, Object> params);

    Map<String, String> getNotify(Constants.PayMode alipay, BigDecimal bigDecimal, String out_trade_no);

    PageData<MerchantOrderDTO> listMerchantPages(Map<String, Object> params);
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
    DesignConditionsDTO notDiscounts(DesignConditionsDTO dct);

    void updateOrderStatus(int status,  String orderId);

    void updatePayMode(@Param("payMode") String payMode,@Param("orderId") String orderId);
    MasterOrderDTO getOrderById(long id);

    Result caleclUpdate(long id, long verify, Date date, String verify_reason);

    Result cancelOrder(long id);

    void updatePayMoney( BigDecimal PayMoney, String orderId);

    /**
     *
     * 下单后预定包房
     * @Author: Zhangguanglin
     * @Description:
     * @Date: 2019/10/11
     * @param dto: 主订单信息
     * @param user: 用户信息
     * @Return:
     */
    Result reserveRoom(OrderDTO dto, ClientUserEntity user,String orderId);

    MasterOrderEntity getRoomOrderByPorderId(String orderId);

    Result orderFoodByRoom(OrderDTO dto,List<SlaveOrderEntity> dtoList, ClientUserEntity user, String mainOrderId);

    List<MasterOrderEntity> getOrderByPOrderId(String orderId);

    PageData<OrderDTO> getAllMainOrder(Map<String, Object> params);

    List<MasterOrderEntity>getAuxiliaryOrderByOrderId(String orderId);

    List<OrderDTO>getAuxiliaryOrder(Map params);

    PageData<OrderDTO> pageGetAuxiliaryOrder(Map<String, Object> params);
    List<MasterOrderEntity> getStatus4Order(Map<String, Object> params);
    OrderDTO getMasterOrder(String orderId);
    /***
     *订单详情
     * @Author: Zhangguanglin
     * @Description:
     * @Date: 2019/10/23
     * @param orderId:
     * @Return:
     */
    OrderDTO orderParticulars(String orderId);
    List<OrderDTO> orderParticulars1(String orderId);
    List<OrderDTO> refundOrder( Map<String, Object> params);
    List<MasterOrderEntity>  selectPOrderIdHavePaid(String orderId);

    List<MasterOrderEntity>  selectAgreeRefundOrder(String orderId);

    PageData<OrderDTO> selectPOrderIdHavePaids(Map<String, Object> params);

    PageData<OrderDTO> selectAgreeRefundOrders(Map<String, Object> params);

    List<MasterOrderEntity>getAuxiliaryPayOrders(String orderId);

    List<MasterOrderEntity>getAuxiliaryPayOrderss(String orderId);

    void updateSlaveOrderPointDeduction(BigDecimal mp,BigDecimal pb, String orderId);

    MasterOrderEntity getOrderByReservationId(long reservationId);

    List<MasterOrderEntity>getAuxiliaryPayOrder(String orderId,int status);
    //设置包房
    Result setRoom(long id,long roomSetId);
    List<OrderDTO> getAffiliateOrde(String orderId);

    boolean judgeRockover(String orderId,Date date);

    boolean judgeEvaluate(String orderId);
}
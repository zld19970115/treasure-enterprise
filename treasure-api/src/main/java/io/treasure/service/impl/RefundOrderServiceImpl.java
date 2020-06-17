package io.treasure.service.impl;

import com.alipay.api.java_websocket.WebSocket;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import io.treasure.common.constant.Constant;
import io.treasure.common.page.PageData;
import io.treasure.common.service.impl.CrudServiceImpl;
import io.treasure.common.sms.SMSConfig;
import io.treasure.common.utils.ConvertUtils;
import io.treasure.common.utils.Result;
import io.treasure.config.ISMSConfig;
import io.treasure.dao.RefundOrderDao;
import io.treasure.dto.*;
import io.treasure.enm.Constants;
import io.treasure.enm.MerchantRoomEnm;
import io.treasure.entity.MasterOrderEntity;
import io.treasure.entity.MerchantEntity;
import io.treasure.entity.RefundOrderEntity;
import io.treasure.entity.SlaveOrderEntity;
import io.treasure.push.AppPushUtil;
import io.treasure.service.*;
import io.treasure.utils.BitMessageUtil;
import io.treasure.utils.SendSMSUtil;
import io.treasure.utils.WsPool;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@Service
public class RefundOrderServiceImpl extends CrudServiceImpl<RefundOrderDao, RefundOrderEntity, RefundOrderDTO> implements RefundOrderService {
    @Autowired
    private MasterOrderService masterOrderService;
    @Autowired
    private SMSConfig smsConfig;
    @Autowired
    private SlaveOrderService slaveOrderService;
    @Autowired
    private MerchantRoomParamsSetService merchantRoomParamsSetService;

    @Autowired
    private ClientUserService clientUserService;

    @Autowired
    private  PayServiceImpl payService;

    @Autowired
    private MerchantService merchantService;
    @Autowired
    BitMessageUtil bitMessageUtil;
    @Autowired
    WsPool wsPool;

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
            SlaveOrderDTO allGoods = slaveOrderService.getAllGoods(s.getOrderId(), s.getGoodId());
            //s.setPayMode(order.getPayMode());
            if(order!=null){
            if(order.getReservationId()!=null){
                MerchantRoomParamsSetDTO merchantRoomParamsSetDTO = this.merchantRoomParamsSetService.get(order.getReservationId());
                s.setRoomName(merchantRoomParamsSetDTO.getRoomName());
                 }
                BigDecimal freeGold = allGoods.getFreeGold();
                BigDecimal totalMoney = s.getTotalMoney();
                s.setTotalMoney(totalMoney.add(freeGold));
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
    public Result updateMasterOrderPayMoney(String orderId, Long goodId) {
        Result result=new Result();
        //获取主订单信息
        OrderDTO order = masterOrderService.getOrder(orderId);
        // 获取主订单实付金额
        BigDecimal payMoney = order.getPayMoney();
        //获取订单菜品表退菜信息
        SlaveOrderDTO allGoods = slaveOrderService.getAllGoods(orderId, goodId);
        //退菜金额=退菜数量*退菜单价
        BigDecimal  totalRefundMoney=allGoods.getPayMoney();
        result=payService.refundByGood(orderId,totalRefundMoney.toString(),goodId);
        Boolean data = (Boolean) result.getData();
        if(data){
            BigDecimal newPayMoney = payMoney.subtract(totalRefundMoney);
            masterOrderService.updatePayMoney(newPayMoney,orderId);
        }
        return result;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result agreeToARefund(String orderId, Long goodId) {
        slaveOrderService.updateSlaveOrderStatus(8,orderId,goodId);
        //获取订单菜品表退菜信息
        SlaveOrderDTO allGoods = slaveOrderService.getAllGoods(orderId, goodId);
            baseDao.updateDispose(2,orderId,goodId);
//            this.updateMasterOrderPayMoney(orderId,goodId);
            //获取主订单信息
            OrderDTO order = masterOrderService.getOrder(orderId);
            // 获取主订单实付金额
            BigDecimal payMoney = order.getPayMoney();
            //退菜金额=退菜数量*退菜单价
            BigDecimal  totalRefundMoney=allGoods.getPayMoney();
            Result result = payService.refundByGood(orderId, totalRefundMoney.toString(), goodId);
            Boolean data = (Boolean) result.getData();
        System.out.println("zhangguanglin"+data);
            if(data){
                BigDecimal newPayMoney = payMoney.subtract(totalRefundMoney);
                masterOrderService.updatePayMoney(newPayMoney,orderId);
            }
            List<SlaveOrderEntity> orderGoods = slaveOrderService.getOrderGoods(orderId);
            int num=0;
            for (SlaveOrderEntity soe:orderGoods) {
                if(soe.getStatus()==Constants.OrderStatus.MERCHANTAGREEREFUNDORDER.getValue()){
                    num=num+1;
                }
            }
            if(num==orderGoods.size()){
                System.out.println("zhangguanglin"+num);
                System.out.println("zhangguanglin"+orderGoods.size());
                MasterOrderEntity masterOrderEntity = masterOrderService.selectByOrderId(orderId);
                masterOrderEntity.setPayMoney(masterOrderEntity.getTotalMoney());
                masterOrderService.updateById(masterOrderEntity);
                masterOrderService.updateOrderStatus(Constants.OrderStatus.MERCHANTAGREEREFUNDORDER.getValue(),orderId);
                if(order.getReservationId()!=null){
                    merchantRoomParamsSetService.updateStatus(order.getReservationId(),0);
                }else{
                    MasterOrderEntity roomOrderByPorderId = masterOrderService.getRoomOrderByPorderId(orderId);
                    if(roomOrderByPorderId!=null){
                        merchantRoomParamsSetService.updateStatus(roomOrderByPorderId.getReservationId(),0);
                        masterOrderService.updateOrderStatus(8,roomOrderByPorderId.getOrderId());
                    }
                }
            }
            BigDecimal n=new BigDecimal("0");
            ClientUserDTO clientUserDTO = clientUserService.get(order.getCreator());
            BigDecimal gift = clientUserDTO.getGift();
            clientUserDTO.setGift(gift.add(allGoods.getFreeGold()));
            BigDecimal freeGold = allGoods.getFreeGold();
            OrderDTO order1 = masterOrderService.getOrder(orderId);
            BigDecimal giftMoney = order1.getGiftMoney();
            BigDecimal subtract = giftMoney.subtract(freeGold);
            order1.setGiftMoney(subtract);
            //退菜更新此订单中平台所得和用户所得金额
            BigDecimal platformBrokerage = allGoods.getPlatformBrokerage();
            BigDecimal merchantProceeds = allGoods.getMerchantProceeds();
            BigDecimal platformBrokerage1 = order1.getPlatformBrokerage();
            BigDecimal merchantProceeds1 = order1.getMerchantProceeds();
            if(merchantProceeds1.compareTo(n)>1 && platformBrokerage1.compareTo(n)>1){
                order1.setPlatformBrokerage(platformBrokerage1.subtract(platformBrokerage));
                order1.setMerchantProceeds(merchantProceeds1.subtract(merchantProceeds));
            }
            masterOrderService.update(ConvertUtils.sourceToTarget(order1, MasterOrderDTO.class));
            clientUserService.update(clientUserDTO);
            allGoods.setMerchantProceeds(n);
            allGoods.setPlatformBrokerage(n);
            slaveOrderService.update(allGoods);
            String clientId = clientUserDTO.getClientId();
            if(StringUtils.isNotBlank(clientId)){
                AppPushUtil.pushToSingleClient("商家同意退菜", "您的退菜申请已通过", "", clientId);
            }
        WebSocket wsByUser = wsPool.getWsByUser(order.getCreator().toString());
        System.out.println("wsByUser+++++++++++++++++++++++++++++:"+wsByUser
        );
        wsPool.sendMessageToUser(wsByUser, 1+"");
            MerchantDTO merchantDTO = merchantService.get(order.getMerchantId());
            SendSMSUtil.sendMerchantAgreeRefusal(clientUserDTO.getMobile(), merchantDTO.getName(), smsConfig);
            List<OrderDTO> affiliateOrde = masterOrderService.getAffiliateOrde(orderId);
            for (OrderDTO orderDTO : affiliateOrde) {
                if (affiliateOrde.size()==1 && orderDTO.getReservationType()==Constants.ReservationType.ONLYROOMRESERVATION.getValue() ){
                    merchantRoomParamsSetService.updateStatus(orderDTO.getReservationId(), MerchantRoomEnm.STATE_USE_NO.getType());
                    orderDTO.setStatus(Constants.OrderStatus.MERCHANTAGREEREFUNDORDER.getValue());
                    masterOrderService.updateById(ConvertUtils.sourceToTarget(orderDTO, MasterOrderEntity.class));
                }
            }
            //维护赠送金记录表
        return new Result();
    }

    @Override
    public void DoNotAgreeToRefund(String orderId, Long goodId) {
        slaveOrderService.updateSlaveOrderStatus(7,orderId,goodId);
        baseDao.updateDispose(2,orderId,goodId);
        OrderDTO order = masterOrderService.getOrder(orderId);
        ClientUserDTO clientUserDTO = clientUserService.get(order.getCreator());
        String clientId = clientUserDTO.getClientId();
        if(StringUtils.isNotBlank(clientId)){
            AppPushUtil.pushToSingleClient("商家不同意退菜", "您的退菜申请未通过", "", clientId);
        }
        MerchantEntity merchantById = merchantService.getMerchantById(order.getMerchantId());
        SendSMSUtil.sendMerchantRefusalFood(clientUserDTO.getMobile(), merchantById.getName(), smsConfig);
        WebSocket wsByUser = wsPool.getWsByUser(order.getCreator().toString());
        System.out.println("wsByUser+++++++++++++++++++++++++++++:"+wsByUser
        );
        wsPool.sendMessageToUser(wsByUser, 1+"");
    }


}

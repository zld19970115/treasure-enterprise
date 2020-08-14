package io.treasure.service.impl;

import com.alipay.api.AlipayApiException;
import com.alipay.api.AlipayClient;
import com.alipay.api.domain.AlipayTradeRefundModel;
import com.alipay.api.java_websocket.WebSocket;
import com.alipay.api.request.AlipayTradeRefundRequest;
import com.alipay.api.response.AlipayTradeRefundResponse;
import io.treasure.common.constant.WXPayConstants;
import io.treasure.common.sms.SMSConfig;
import io.treasure.common.utils.ConvertUtils;
import io.treasure.common.utils.Result;
import io.treasure.config.IWXConfig;
import io.treasure.config.IWXPay;
import io.treasure.dao.MasterOrderDao;
import io.treasure.dao.SlaveOrderDao;
import io.treasure.dto.*;
import io.treasure.enm.Constants;
import io.treasure.enm.EMessageUpdateType;
import io.treasure.enm.MerchantRoomEnm;
import io.treasure.entity.*;
import io.treasure.jra.impl.MerchantMessageJRA;
import io.treasure.jro.MerchantMessage;
import io.treasure.push.AppPushUtil;
import io.treasure.service.*;
import io.treasure.task.item.ClientMemberGradeAssessment;
import io.treasure.utils.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static io.treasure.enm.EIncrType.ADD;

/**
 * @author super
 * @since 1.0.0 2019-09-08
 */
@Slf4j
@Service
public class PayServiceImpl implements PayService {

    @Resource
    MasterOrderDao masterOrderDao;

    @Resource
    SlaveOrderDao slaveOrderDao;
    @Autowired
    MerchantMessageJRA merchantMessageJRA;
    @Autowired
    MasterOrderService masterOrderService;
    @Autowired
    MerchantClientService merchantClientService;
    @Autowired
    StimmeService stimmeService;
    @Autowired
    ClientUserServiceImpl clientUserService;
    @Autowired
    ChargeCashService chargeCashService;
    @Autowired
    SlaveOrderService slaveOrderService;
    @Autowired
    private RecordGiftServiceImpl recordGiftService;

    @Autowired
    private AlipayClient alipayClient;
    @Autowired
    private ChargeCashSetService chargeCashSetService;

    @Autowired
    private IWXConfig wxPayConfig;

    @Autowired
    MerchantServiceImpl merchantService;

    @Autowired
    private IWXPay wxPay;

    @Autowired
    MerchantUserService merchantUserService;
    @Autowired
    private RefundOrderService refundOrderService;

    @Autowired
    private MerchantRoomParamsSetService merchantRoomParamsSetService;

    @Autowired
    WsPool wsPool;
    @Autowired
    private SMSConfig smsConfig;

    @Autowired
    private UserTransactionDetailsService userTransactionDetailsService;
    @Autowired
    private ClientMemberGradeAssessment clientMemberGradeAssessment;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Map<String, String> wxNotify(BigDecimal total_amount, String out_trade_no) {
        Map<String, String> mapRtn = new HashMap<>(2);
        System.out.println("当前回调的数值为："+total_amount);
        MasterOrderEntity masterOrderEntity = masterOrderDao.selectByOrderId(out_trade_no);
        //        if(masterOrderEntity.getStatus()!=1){
////            mapRtn.put("return_code", "SUCCESS");
////            return mapRtn;
////        }

        if (masterOrderEntity == null || "".equals(masterOrderEntity)) {
            mapRtn.put("return_code", "FAIL");
            mapRtn.put("return_msg", "支付失败！请联系管理员！【没有此订单：" + out_trade_no + "】");
            return mapRtn;
        }

        if (masterOrderEntity.getStatus() != Constants.OrderStatus.NOPAYORDER.getValue() && masterOrderEntity.getStatus() != Constants.OrderStatus.MERCHANTTIMEOUTORDER.getValue()) {
            System.out.println("wxNotify02===============masterOrderEnitity.getStatus():" + masterOrderEntity.getStatus());
            mapRtn.put("return_code", "SUCCESS");
            mapRtn.put("return_msg", "OK");
            return mapRtn;
        }
        //db option flag 1
        if (masterOrderEntity.getStatus() == Constants.OrderStatus.MERCHANTTIMEOUTORDER.getValue()) {
            if (masterOrderEntity.getReservationId() != null && masterOrderEntity.getReservationId() > 0) {
                merchantRoomParamsSetService.updateStatus(masterOrderEntity.getReservationId(), MerchantRoomEnm.STATE_USE_YEA.getType());
            }
        }
        /****************************************************************************************/
        try {

            BigDecimal payMoney = masterOrderEntity.getPayMoney();
            BigDecimal payCoins = masterOrderEntity.getPayCoins();
            payMoney = payMoney.subtract(payCoins).setScale(2,BigDecimal.ROUND_HALF_DOWN);
            System.out.println("与发来的值进行对比(payMoney,total_amount)："+payMoney+","+total_amount);
            if (payMoney.compareTo(total_amount) != 0) {
                System.out.println("微信支付：支付失败！请联系管理员！【支付金额不一致】");
                throw new Exception("wx_pay_fail:code01");
            }

        } catch (Exception e) {
            mapRtn.put("return_code", "FAIL");
            mapRtn.put("return_msg", "支付失败！请联系管理员！【支付金额不一致】");
            return mapRtn;

        }
        try {
            if (masterOrderEntity == null) {
                System.out.println("微信支付：支付失败！请联系管理员！【未找到订单】");
                throw new Exception("wx_pay_fail:code02");
            }
        } catch (Exception e) {
            mapRtn.put("return_code", "FAIL");
            mapRtn.put("return_msg", "支付失败！请联系管理员！【未找到订单】");
            return mapRtn;
        }
        /****************************************************************************************/
        if (masterOrderEntity.getStatus() != 1) {
            mapRtn.put("return_code", "SUCCESS");
            mapRtn.put("return_msg", "OK");
            return mapRtn;
        }
        masterOrderEntity.setStatus(Constants.OrderStatus.PAYORDER.getValue());
        masterOrderEntity.setPayMode(Constants.PayMode.WXPAY.getValue());
        masterOrderEntity.setResponseStatus(1);//排序提前
        masterOrderEntity.setPayDate(new Date());
        masterOrderDao.updateById(masterOrderEntity);
        Long creator = masterOrderEntity.getCreator();

        List<SlaveOrderEntity> slaveOrderEntities = slaveOrderService.selectByOrderId(masterOrderEntity.getOrderId());
        //System.out.println("position 5 : "+slaveOrderEntities.toString());

        BigDecimal a = new BigDecimal(0);
        for (SlaveOrderEntity slaveOrderEntity : slaveOrderEntities) {
            a = a.add(slaveOrderEntity.getFreeGold());
        }
        ClientUserEntity clientUserEntity = clientUserService.selectById(creator);
        //System.out.println("position 6 : "+clientUserEntity.toString());
        BigDecimal gift = clientUserEntity.getGift();
        System.out.println("wxNotify03==============gift+" + gift);
        gift = gift.subtract(a);
        clientUserEntity.setGift(gift);
        System.out.println("wxNotify04==============gift+" + gift);
        clientUserService.updateById(clientUserEntity);
        Date date = new Date();
        recordGiftService.insertRecordGift2(clientUserEntity.getId(), date, gift, a);
        //System.out.println("position 1 : "+masterOrderDao.toString()+"===reservationType:"+masterOrderEntity.getReservationType());
        //至此
        //  int i = bitMessageUtil.attachMessage(EMsgCode.ADD_DISHES);
//        System.out.println("i+++++++++++++++++++++++++++++:"+i
//        );

        MerchantUserEntity merchantUserEntity = merchantUserService.selectByMerchantId(masterOrderEntity.getMerchantId());
        if (merchantUserEntity != null) {
            SendSMSUtil.sendNewOrder(merchantUserEntity.getMobile(), smsConfig);
        }
        WebSocket wsByUser = wsPool.getWsByUser(masterOrderEntity.getMerchantId().toString());
        System.out.println("wsByUser+++++++++++++++++++++++++++++:" + wsByUser
        );
        MerchantMessage merchantMessage = merchantMessageJRA.updateSpecifyField(masterOrderEntity.getMerchantId().toString(), EMessageUpdateType.CREATE_ORDER, ADD);

        wsPool.sendMessageToUser(wsByUser, merchantMessage.toString());
        if (masterOrderEntity.getReservationType() != Constants.ReservationType.ONLYROOMRESERVATION.getValue()) {
            List<SlaveOrderEntity> slaveOrderEntitys = slaveOrderService.selectByOrderId(out_trade_no);
            //System.out.println("position 2 : "+slaveOrderEntitys);
            /****************************************************************************************/
            if (slaveOrderEntitys == null) {
                try {
                    System.out.println("支付失败！请联系管理员！【未找到订单菜品】");
                    throw new Exception("wx_pay_fail:code03");
                } catch (Exception e) {
                    mapRtn.put("return_code", "FAIL");
                    mapRtn.put("return_msg", "支付失败！请联系管理员！【未找到订单菜品】");
                    return mapRtn;
                }
            } else {
                slaveOrderEntitys.forEach(slaveOrderEntity -> {
                    slaveOrderEntity.setStatus(Constants.OrderStatus.PAYORDER.getValue());
                });
                boolean b = slaveOrderService.updateBatchById(slaveOrderEntitys);

                if (!b) {
                    try {
                        System.out.println("支付失败！请联系管理员！【更新菜品】");
                        throw new Exception("wx_pay_fail:code04");
                    } catch (Exception e) {
                        mapRtn.put("return_code", "FAIL");
                        mapRtn.put("return_msg", "支付失败！请联系管理员！【更新菜品】");
                        return mapRtn;
                    }
                }
            }
        }

        MerchantDTO merchantDto = merchantService.get(masterOrderEntity.getMerchantId());
        //System.out.println("position 3 : "+merchantDto.toString());
        List<MerchantClientDTO> list = merchantClientService.getMerchantUserClientByMerchantId(merchantUserEntity.getId());
        String clientId ="";
        if (list.size() == 0){
            System.out.println("PayService 229");
        }else {
            clientId = list.get(0).getClientId();
        }
        if (null != merchantDto) {
            MerchantUserDTO userDto = merchantUserService.get(merchantDto.getCreator());
            if (null != userDto) {
                if (StringUtils.isNotBlank(clientId)) {
                    for (int i = 0; i < list.size(); i++) {
                        AppPushUtil.pushToSingleMerchant("订单管理", "您有新的订单，请注意查收！",  list.get(i).getClientId());
                    }
                    StimmeEntity stimmeEntity = new StimmeEntity();
                    Date date1 = new Date();
                    stimmeEntity.setCreateDate(date1);
                    stimmeEntity.setOrderId(masterOrderEntity.getOrderId());
                    stimmeEntity.setType(1);
                    stimmeEntity.setMerchantId(masterOrderEntity.getMerchantId());
                    stimmeEntity.setCreator(masterOrderEntity.getCreator());
                    stimmeService.insert(stimmeEntity);
                } else {
                    try {
                        System.out.println("支付失败！请联系管理员！【无法获取商户会员无clientId信息】");
                        throw new Exception("wx_pay_fail:code05");
                    } catch (Exception e) {
                        mapRtn.put("return_code", "FAIL");
                        mapRtn.put("return_msg", "支付失败！请联系管理员！【无法获取商户会员无clientId信息】");
                        return mapRtn;
                    }
                }
            } else {

                try {
                    System.out.println("支付失败！请联系管理员！【无法获取商户会员信息】");
                    throw new Exception("wx_pay_fail:code06");
                } catch (Exception e) {
                    mapRtn.put("return_code", "FAIL");
                    mapRtn.put("return_msg", "支付失败！请联系管理员！【无法获取商户会员信息】");
                    return mapRtn;
                }
            }
        } else {
            try {
                System.out.println("支付失败！请联系管理员！【无法获取商户信息】");
                throw new Exception("wx_pay_fail:code07");
            } catch (Exception e) {
                mapRtn.put("return_code", "FAIL");
                mapRtn.put("return_msg", "支付失败！请联系管理员！【无法获取商户信息】");
                return mapRtn;
            }
        }
        //System.out.println("position 4 : "+masterOrderEntity.toString());
        //更新用户宝币数量
        BigDecimal balance = clientUserEntity.getBalance();
        balance = balance.subtract(masterOrderEntity.getPayCoins());
        clientUserEntity.setBalance(balance);
        clientUserService.updateById(clientUserEntity);

        mapRtn.put("return_code", "SUCCESS");
        mapRtn.put("return_msg", "OK");
        return mapRtn;
    }

    @Override
    public Map<String, String> cashWxNotify(BigDecimal total_amount, String out_trade_no) {
        Map<String, String> mapRtn = new HashMap<>(2);

        ChargeCashDTO chargeCashDTO = chargeCashService.selectByCashOrderId(out_trade_no);
        //        if(masterOrderEntity.getStatus()!=1){
////            mapRtn.put("return_code", "SUCCESS");
////            return mapRtn;
////        }

        if (chargeCashDTO == null || "".equals(chargeCashDTO)) {
            mapRtn.put("return_code", "FAIL");
            mapRtn.put("return_msg", "支付失败！请联系管理员！【没有此订单：" + out_trade_no + "】");
            return mapRtn;
        }

        if (chargeCashDTO.getStatus() != 1) {
            //=========================
            mapRtn.put("return_code", "SUCCESS");
            mapRtn.put("return_msg", "OK");
            return mapRtn;
        }

        /****************************************************************************************/
        try {
            if (chargeCashDTO.getCash().compareTo(total_amount) != 0) {
                System.out.println("微信支付：支付失败！请联系管理员！【支付金额不一致】");
                throw new Exception("wx_pay_fail:code01");
            }

        } catch (Exception e) {
            mapRtn.put("return_code", "FAIL");
            mapRtn.put("return_msg", "支付失败！请联系管理员！【支付金额不一致】");
            return mapRtn;

        }
        try {
            if (chargeCashDTO == null) {
                System.out.println("微信支付：支付失败！请联系管理员！【未找到订单】");
                throw new Exception("wx_pay_fail:code02");
            }
        } catch (Exception e) {
            mapRtn.put("return_code", "FAIL");
            mapRtn.put("return_msg", "支付失败！请联系管理员！【未找到订单】");
            return mapRtn;
        }

        chargeCashDTO.setStatus(2);
        chargeCashDTO.setSaveTime(new Date());
        chargeCashService.update(chargeCashDTO);
        ClientUserEntity clientUserEntity = clientUserService.selectById(chargeCashDTO.getUserId());
        BigDecimal balance = clientUserEntity.getBalance();
        balance = balance.add(total_amount);
        ChargeCashSetEntity chargeCashSetEntity = chargeCashSetService.selectByCash(chargeCashDTO.getCash());
        BigDecimal gift = clientUserEntity.getGift();
        gift = gift.add(chargeCashSetEntity.getChangeGift());
        clientUserEntity.setGift(gift);
        clientUserEntity.setBalance(balance);
        clientUserService.updateById(clientUserEntity);

        mapRtn.put("return_code", "SUCCESS");
        mapRtn.put("return_msg", "OK");

        UserTransactionDetailsEntity entiry = new UserTransactionDetailsEntity();
        entiry.setCreateDate(new Date());
        entiry.setMobile(clientUserEntity.getMobile());
        entiry.setMoney(total_amount);
        entiry.setPayMode(3);
        entiry.setType(1);
        entiry.setBalance(balance);
        entiry.setUserId(clientUserEntity.getId());
        userTransactionDetailsService.insert(entiry);

        //1----更新用户分级内容
        clientMemberGradeAssessment.growUpGrade(clientUserEntity.getId());

        return mapRtn;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result aliRefund(String orderNo, String refund_fee, Long goodId) {
        AlipayTradeRefundRequest alipayRequest = new AlipayTradeRefundRequest();
        Result result = new Result();

        MasterOrderEntity masterOrderEntity = masterOrderDao.selectByOrderId(orderNo);
        // 实付金额，单位为分，只能为整数
        BigDecimal payMoney = masterOrderEntity.getPayMoney();
        //退款金额
        BigDecimal nu = new BigDecimal("0");
        BigDecimal pay_coins = masterOrderEntity.getPayCoins();
        BigDecimal refundAmount = new BigDecimal(refund_fee);
        if (pay_coins.compareTo(nu)==1){
            refundAmount = refundAmount.subtract(pay_coins);
        }
        SlaveOrderDTO slaveOrderDTO = null;
        Long userId = masterOrderEntity.getCreator();
        if (masterOrderEntity.getCheckStatus() == 1) {
            return result.error("已结算不可以退款！");
        }
        //判断是否可以退款

        //判断是否退菜
        if (goodId != null) {
            //退菜
            if (masterOrderEntity.getStatus() != Constants.OrderStatus.PAYORDER.getValue() && masterOrderEntity.getStatus() != Constants.OrderStatus.MERCHANTRECEIPTORDER.getValue()) {
                return result.error("无法退菜！【订单状态错误】");
            }
            slaveOrderDTO = slaveOrderService.getAllGoods(orderNo, goodId);
            if (slaveOrderDTO.getStatus() != Constants.OrderStatus.MERCHANTREFUSALORDER.getValue() && slaveOrderDTO.getStatus() != Constants.OrderStatus.MERCHANTAGREEREFUNDORDER.getValue()) {
                return result.error("无法退菜！【订单菜品状态错误】");
            }
            if (slaveOrderDTO.getPayMoney().compareTo(refundAmount) != 0) {
                return result.error("退款金额不一致，无法退款！");
            }
        } else {
            //退单
//            if(masterOrderEntity.getStatus()!=Constants.OrderStatus.USERAPPLYREFUNDORDER.getValue()&&masterOrderEntity.getStatus()!=Constants.OrderStatus.MERCHANTTIMEOUTORDER.getValue()&&masterOrderEntity.getStatus()!=Constants.OrderStatus.PAYORDER.getValue()){
//                return result.error("不是退款订单,无法退款！");
//            }
//            if(masterOrderEntity.getStatus()!=Constants.OrderStatus.MERCHANTREFUSALORDER.getValue()&&masterOrderEntity.getStatus()!=Constants.OrderStatus.MERCHANTAGREEREFUNDORDER.getValue()&&masterOrderEntity.getStatus()!=Constants.OrderStatus.MERCHANTTIMEOUTORDER.getValue()){
//                return result.error("不是退款订单,无法退款！");
//            }

           BigDecimal apayMoney = payMoney.subtract(pay_coins);
            if (apayMoney.compareTo(refundAmount) != 0) {
                return result.error("退款金额不一致，无法退款！");
            }
        }
        AlipayTradeRefundModel model = new AlipayTradeRefundModel();
        // 商户订单号
        model.setOutTradeNo(orderNo);
        // 退款金额
        model.setRefundAmount(String.valueOf(refundAmount));
        // 退款原因
        model.setRefundReason("无理由退货");
        // 退款订单号(同一个订单可以分多次部分退款，当分多次时必传)
        String refundNo = OrderUtil.getRefundOrderIdByTime(userId);
        model.setOutRequestNo(refundNo);
        alipayRequest.setBizModel(model);
        System.out.println(alipayRequest);
        AlipayTradeRefundResponse alipayResponse = null;
        try {
            alipayResponse = alipayClient.execute(alipayRequest);
            System.out.println("alipayResponse:" + alipayResponse);
            System.out.println("alipayResponse.getCode():" + alipayResponse.getCode());
        } catch (AlipayApiException e) {
            e.printStackTrace();
        }
        if (alipayResponse.getCode().equals("10000")) {
            if (goodId != null) {
                //将退款ID更新到refundOrder表中refund_id
                refundOrderService.updateRefundId(refundNo, orderNo, goodId);
                //将退款ID更新到订单菜品表中
                slaveOrderService.updateRefundId(refundNo, orderNo, goodId);
                BigDecimal bigDecimal = payMoney.subtract(refundAmount);
                masterOrderEntity.setPayMoney(bigDecimal);
//                masterOrderEntity.setRefundedAmount(refundAmount);
                masterOrderService.update(ConvertUtils.sourceToTarget(masterOrderEntity, MasterOrderDTO.class));
                SlaveOrderDTO allGoods = slaveOrderService.getAllGoods(orderNo, goodId);
                OrderDTO order = masterOrderService.getOrder(orderNo);
                BigDecimal platformBrokerage = order.getPlatformBrokerage();
                BigDecimal merchantProceeds = order.getMerchantProceeds();
                BigDecimal mp = merchantProceeds.subtract(allGoods.getMerchantProceeds());
                BigDecimal pb = platformBrokerage.subtract(allGoods.getPlatformBrokerage());
                //退菜后将平台扣点金额和商户所得更新到主订单表中
                masterOrderService.updateSlaveOrderPointDeduction(mp, pb, orderNo);
                BigDecimal a = new BigDecimal("0");

                //退菜后将订单菜品表中对应菜品平台扣点和商户所得金额清除掉


//                    //返还赠送金
//                    slaveOrderService.updateSlaveOrderPointDeduction(a, a, orderNo, goodId);
//                    ClientUserEntity clientUser = clientUserService.getClientUser(masterOrderEntity.getCreator());
//                    BigDecimal gift = clientUser.getGift();
//                    clientUser.setGift(gift.add(allGoods.getFreeGold()));
//                    clientUserService.updateById(clientUser);

                return result.ok(true);
            } else {
                masterOrderEntity.setRefundId(refundNo);
                masterOrderService.update(ConvertUtils.sourceToTarget(masterOrderEntity, MasterOrderDTO.class));
                List<SlaveOrderEntity> slaveOrderEntityList = slaveOrderService.selectByOrderId(orderNo);
                BigDecimal a = new BigDecimal("0");
                for (int i = 0; i < slaveOrderEntityList.size(); i++) {
                    SlaveOrderEntity slaveOrderEntity = slaveOrderEntityList.get(i);
                    if (slaveOrderEntity.getRefundId() == null || slaveOrderEntity.getRefundId().length() == 0) {
                        slaveOrderEntity.setRefundId(refundNo);
                    }
                }

//                    //返还赠送金
//                    ClientUserEntity clientUser = clientUserService.getClientUser(masterOrderEntity.getCreator());
//                    BigDecimal gift = clientUser.getGift();
//                    clientUser.setGift(gift.add(masterOrderEntity.getGiftMoney()));
//                    clientUserService.updateById(clientUser);
//                    slaveOrderService.updateSlaveOrderPointDeduction(a, a, orderNo, goodId);
//                    masterOrderService.updateSlaveOrderPointDeduction(a, a, orderNo);

                return result.ok(true);
            }
        }
        return result.ok(false);
    }

    /**
     * 微信订单退款功能
     *
     * @param orderNo    订单号
     * @param refund_fee 退款金额
     * @return
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result wxRefund(String orderNo, String refund_fee) {
        return wxRefund(orderNo, refund_fee, null);
    }

    /**
     * 订单退款功能
     *
     * @param payMode    支付方式
     * @param orderNo    订单号
     * @param refund_fee 退菜金额
     * @return
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result refundByOrder(String payMode, String orderNo, String refund_fee) {
        if (payMode.equals(Constants.PayMode.WXPAY.getValue())) {
            return this.wxRefund(orderNo, refund_fee, null);
        }
        if (payMode.equals(Constants.PayMode.ALIPAY.getValue())) {
            return this.aliRefund(orderNo, refund_fee, null);
        }
        return new Result().error("退款失败！【无支付方式】");
    }

    /**
     * 订单退款功能
     *
     * @param orderNo    订单号
     * @param refund_fee 退菜金额
     * @return
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result refundByOrder(String orderNo, String refund_fee) {
        MasterOrderEntity masterOrderEntity = masterOrderDao.selectByOrderId(orderNo);
        String payMode = masterOrderEntity.getPayMode();
        if (payMode.equals(Constants.PayMode.WXPAY.getValue())) {
            return this.wxRefund(orderNo, refund_fee, null);
        }
        if (payMode.equals(Constants.PayMode.BALANCEPAY.getValue())) {
            return this.CashRefund(orderNo, refund_fee, null);
        }
        if (payMode.equals(Constants.PayMode.ALIPAY.getValue())) {
            return this.aliRefund(orderNo, refund_fee, null);
        }
        return new Result().error("退款失败！【无支付方式】");
    }

    /**
     * 菜品退款功能
     *
     * @param orderNo    订单号
     * @param refund_fee 退菜金额
     * @param goodId     退菜ID
     * @return
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result refundByGood(String orderNo, String refund_fee, Long goodId) {
        MasterOrderEntity masterOrderEntity = masterOrderDao.selectByOrderId(orderNo);
        String payMode = masterOrderEntity.getPayMode();
        if (payMode.equals(Constants.PayMode.WXPAY.getValue())) {
            return this.wxRefund(orderNo, refund_fee, goodId);
        }
        if (payMode.equals(Constants.PayMode.BALANCEPAY.getValue())) {
            return this.CashRefund(orderNo, refund_fee, goodId);
        }
        if (payMode.equals(Constants.PayMode.ALIPAY.getValue())) {
            return this.aliRefund(orderNo, refund_fee, goodId);
        }
        return new Result().error("退款失败！【无支付方式】");
    }

    /**
     * 菜品退款功能
     *
     * @param payMode    支付方式
     * @param orderNo    订单号
     * @param refund_fee 退菜金额
     * @param goodId     退菜ID
     * @return
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result refundByGood(String payMode, String orderNo, String refund_fee, Long goodId) {
        if (payMode.equals(Constants.PayMode.WXPAY.getValue())) {
            return this.wxRefund(orderNo, refund_fee, goodId);
        }
        if (payMode.equals(Constants.PayMode.ALIPAY.getValue())) {
            return this.aliRefund(orderNo, refund_fee, goodId);
        }
        return new Result().error("退款失败！【无支付方式】");
    }

    /**
     * 微信菜品退款功能
     *
     * @param orderNo 订单号
     * @param goodId  退菜编号
     * @return
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result wxRefund(String orderNo, String refund_fee, Long goodId) {
        Map<String, String> reqData = new HashMap<>();
        Result result = new Result();
        MasterOrderEntity masterOrderEntity = masterOrderDao.selectByOrderId(orderNo);
        // 订单总金额，单位为分，只能为整数
        BigDecimal nu = new BigDecimal("0");
        BigDecimal totalAmount = masterOrderEntity.getTotalMoney();
        BigDecimal payMoney = masterOrderEntity.getPayMoney();
        //退款金额
        BigDecimal pay_coins = masterOrderEntity.getPayCoins();
        System.out.println(pay_coins+"pay_coins");
        BigDecimal refundAmount = new BigDecimal(refund_fee);
        System.out.println(refundAmount+"未减");
        if (pay_coins.compareTo(nu)==1){
            refundAmount = refundAmount.subtract(pay_coins);
        }
        System.out.println(refundAmount+"已经减");
        SlaveOrderDTO slaveOrderDTO = null;
        Long userId = masterOrderEntity.getCreator();
        if (masterOrderEntity.getCheckStatus() == 1) {
            return result.error("已结算不可以退款！");
        }
        //判断是否可以退款

        //判断是否退菜
        if (goodId != null) {
            //退菜
            if (masterOrderEntity.getStatus() != Constants.OrderStatus.PAYORDER.getValue() && masterOrderEntity.getStatus() != Constants.OrderStatus.MERCHANTRECEIPTORDER.getValue()) {
                return result.error("无法退菜！【订单状态错误】");
            }
            slaveOrderDTO = slaveOrderService.getAllGoods(orderNo, goodId);
            if (slaveOrderDTO.getStatus() != Constants.OrderStatus.MERCHANTREFUSALORDER.getValue() && slaveOrderDTO.getStatus() != Constants.OrderStatus.MERCHANTAGREEREFUNDORDER.getValue()) {
                return result.error("无法退菜！【订单菜品状态错误】");
            }
            if (slaveOrderDTO.getPayMoney().compareTo(refundAmount) != 0) {
                return result.error("退款金额不一致，无法退款！");
            }
        } else {
            //退单
            BigDecimal apayMoney = payMoney.subtract(pay_coins);
            if (apayMoney.compareTo(refundAmount) != 0) {
                return result.error("退款金额不一致，无法退款！");
            }
        }
        // 商户订单号
        reqData.put("out_trade_no", orderNo);
        // 授权码
        String refundNo = OrderUtil.getRefundOrderIdByTime(userId);
        reqData.put("out_refund_no", refundNo);


        BigDecimal total = refundAmount.multiply(new BigDecimal(100));  //接口中参数支付金额单位为【分】，参数值不能带小数，所以乘以100
        java.text.DecimalFormat df = new java.text.DecimalFormat("0");
        reqData.put("total_fee", df.format(total));
//        refundAmount = new BigDecimal("1.50");
        BigDecimal refund = refundAmount.multiply(new BigDecimal(100));  //接口中参数支付金额单位为【分】，参数值不能带小数，所以乘以100
        reqData.put("refund_fee", df.format(refund));
        // 退款异步通知地址
        reqData.put("notify_url", wxPayConfig.getNotifyUrl());
        reqData.put("refund_fee_type", "CNY");
        reqData.put("op_user_id", wxPayConfig.getMchID());
        System.out.println("reqData" + reqData);
        Map<String, String> resultMap = null;
        try {
            resultMap = wxPay.refund(reqData);
            System.out.println("resultMap:" + resultMap);
        } catch (Exception e) {
            return result.error("退款失败！");
        }
        boolean rtn = resultMap.get("return_code").equals(WXPayConstants.SUCCESS) && resultMap.get("result_code").equals(WXPayConstants.SUCCESS);
        System.out.println("rtn:" + rtn);
        if (rtn) {

            if (goodId != null) {
                //将退款ID更新到refundOrder表中refund_id
                refundOrderService.updateRefundId(refundNo, orderNo, goodId);

                //将退款ID更新到订单菜品表中
                slaveOrderService.updateRefundId(refundNo, orderNo, goodId);
                BigDecimal bigDecimal = totalAmount.subtract(refundAmount);
                masterOrderEntity.setPayMoney(bigDecimal);
                masterOrderService.update(ConvertUtils.sourceToTarget(masterOrderEntity, MasterOrderDTO.class));
                SlaveOrderDTO allGoods = slaveOrderService.getAllGoods(orderNo, goodId);
                OrderDTO order = masterOrderService.getOrder(orderNo);
                BigDecimal platformBrokerage = order.getPlatformBrokerage();
                BigDecimal merchantProceeds = order.getMerchantProceeds();
                //退菜后将平台扣点金额和商户所得更新到主订单表中
                masterOrderService.updateSlaveOrderPointDeduction(merchantProceeds.subtract(allGoods.getMerchantProceeds()), platformBrokerage.subtract(allGoods.getPlatformBrokerage()), orderNo);
                BigDecimal a = new BigDecimal("0");
                //退菜后将订单菜品表中对应菜品平台扣点和商户所得金额清除掉
                slaveOrderService.updateSlaveOrderPointDeduction(a, a, orderNo, goodId);

                return result.ok(true);
            } else {
                masterOrderEntity.setRefundId(refundNo);
                masterOrderEntity.setPayMoney(masterOrderEntity.getPayMoney());
                masterOrderService.update(ConvertUtils.sourceToTarget(masterOrderEntity, MasterOrderDTO.class));
                List<SlaveOrderEntity> slaveOrderEntityList = slaveOrderService.selectByOrderId(orderNo);
                BigDecimal a = new BigDecimal("0");
                for (int i = 0; i < slaveOrderEntityList.size(); i++) {
                    SlaveOrderEntity slaveOrderEntity = slaveOrderEntityList.get(i);
                    if (slaveOrderEntity.getRefundId() == null || slaveOrderEntity.getRefundId().length() == 0) {
                        slaveOrderEntity.setRefundId(refundNo);
                    }
                    slaveOrderService.updateSlaveOrderPointDeduction(a, a, orderNo, goodId);
                }
                masterOrderService.updateSlaveOrderPointDeduction(a, a, orderNo);
                return result.ok(true);
            }

        }
        return result.ok(false);
    }

    @Override
    public Result CashRefund(String orderNo, String refund_fee, Long goodId) {
        Map<String, String> reqData = new HashMap<>();
        Result result = new Result();
        MasterOrderEntity masterOrderEntity = masterOrderDao.selectByOrderId(orderNo);
        // 订单总金额，单位为分，只能为整数
        BigDecimal totalAmount = masterOrderEntity.getTotalMoney();
        BigDecimal payMoney = masterOrderEntity.getPayMoney();
        //退款金额
//        BigDecimal nu = new BigDecimal("0");
//        BigDecimal pay_coins = masterOrderEntity.getPayCoins();
        BigDecimal refundAmount = new BigDecimal(refund_fee);
//        if (pay_coins.compareTo(nu)==1){
//            refundAmount = refundAmount.subtract(pay_coins);
//        }
        SlaveOrderDTO slaveOrderDTO = null;
        Long userId = masterOrderEntity.getCreator();
        if (masterOrderEntity.getCheckStatus() == 1) {
            return result.error("已结算不可以退款！");
        }
        //判断是否可以退款

        //判断是否退菜
        if (goodId != null) {
            //退菜
            if (masterOrderEntity.getStatus() != Constants.OrderStatus.PAYORDER.getValue() && masterOrderEntity.getStatus() != Constants.OrderStatus.MERCHANTRECEIPTORDER.getValue()) {
                return result.error("无法退菜！【订单状态错误】");
            }
            slaveOrderDTO = slaveOrderService.getAllGoods(orderNo, goodId);
            if (slaveOrderDTO.getStatus() != Constants.OrderStatus.MERCHANTREFUSALORDER.getValue() && slaveOrderDTO.getStatus() != Constants.OrderStatus.MERCHANTAGREEREFUNDORDER.getValue()) {
                return result.error("无法退菜！【订单菜品状态错误】");
            }
            if (slaveOrderDTO.getPayMoney().compareTo(refundAmount) != 0) {
                return result.error("退款金额不一致，无法退款！");
            }
        } else {
            //退单
            if (payMoney .compareTo(refundAmount) != 0) {
                return result.error("退款金额不一致，无法退款！");
            }
        }

        String refundNo = OrderUtil.getRefundOrderIdByTime(userId);
        ClientUserEntity clientUserEntity = clientUserService.selectById(userId);
        BigDecimal balance = clientUserEntity.getBalance();
        BigDecimal add = balance.add(refundAmount);
        clientUserEntity.setBalance(add);
        clientUserService.updateById(clientUserEntity);
        if (goodId != null) {
            //将退款ID更新到refundOrder表中refund_id
            refundOrderService.updateRefundId(refundNo, orderNo, goodId);

            //将退款ID更新到订单菜品表中
            slaveOrderService.updateRefundId(refundNo, orderNo, goodId);
            BigDecimal bigDecimal = totalAmount.subtract(refundAmount);
            masterOrderEntity.setPayMoney(bigDecimal);
            masterOrderService.update(ConvertUtils.sourceToTarget(masterOrderEntity, MasterOrderDTO.class));
            SlaveOrderDTO allGoods = slaveOrderService.getAllGoods(orderNo, goodId);
            OrderDTO order = masterOrderService.getOrder(orderNo);
            BigDecimal platformBrokerage = order.getPlatformBrokerage();
            BigDecimal merchantProceeds = order.getMerchantProceeds();
            //退菜后将平台扣点金额和商户所得更新到主订单表中
            masterOrderService.updateSlaveOrderPointDeduction(merchantProceeds.subtract(allGoods.getMerchantProceeds()), platformBrokerage.subtract(allGoods.getPlatformBrokerage()), orderNo);
            BigDecimal a = new BigDecimal("0");
            //退菜后将订单菜品表中对应菜品平台扣点和商户所得金额清除掉
            slaveOrderService.updateSlaveOrderPointDeduction(a, a, orderNo, goodId);

            return result.ok(true);
        } else {
            masterOrderEntity.setRefundId(refundNo);
            masterOrderEntity.setPayMoney(masterOrderEntity.getPayMoney());
            masterOrderService.update(ConvertUtils.sourceToTarget(masterOrderEntity, MasterOrderDTO.class));
            List<SlaveOrderEntity> slaveOrderEntityList = slaveOrderService.selectByOrderId(orderNo);
            BigDecimal a = new BigDecimal("0");
            for (int i = 0; i < slaveOrderEntityList.size(); i++) {
                SlaveOrderEntity slaveOrderEntity = slaveOrderEntityList.get(i);
                if (slaveOrderEntity.getRefundId() == null || slaveOrderEntity.getRefundId().length() == 0) {
                    slaveOrderEntity.setRefundId(refundNo);
                }
                slaveOrderService.updateSlaveOrderPointDeduction(a, a, orderNo, goodId);
            }
            masterOrderService.updateSlaveOrderPointDeduction(a, a, orderNo);
            return result.ok(true);
        }
    }


    @Override
    @Transactional(propagation = Propagation.REQUIRED,rollbackFor = Exception.class)
    public Map<String, String> execAliCallBack(BigDecimal total_amount, String out_trade_no) {

        Map<String, String> mapRtn = new HashMap<>(2);
        //System.out.println("position==========0" + total_amount + "," + out_trade_no);
        //0、检查单号是否存在
        MasterOrderEntity masterOrderEntity = masterOrderDao.selectByOrderId(out_trade_no);
       // System.out.println("position==========0" + masterOrderEntity.toString());
        if (masterOrderEntity == null) {
            //System.out.println("position==========1");
            mapRtn.put("return_code", "FAIL");
            mapRtn.put("return_msg", "主单不存在，无法完成后续处理");
            return mapRtn;
        }
        Long clientId = masterOrderEntity.getCreator();     //获取clientId，留给后面调用
        //System.out.println("position==========2" + clientId);
        //1、更新主单（支付方式及消费日期）
        if (masterOrderEntity.getStatus() == Constants.OrderStatus.NOPAYORDER.getValue()) {
            //System.out.println("position==========3");
            BigDecimal payMoney = masterOrderEntity.getPayMoney();
            BigDecimal payCoins = masterOrderEntity.getPayCoins();
            payMoney = payMoney.subtract(payCoins).setScale(2,BigDecimal.ROUND_HALF_DOWN);

            if (payMoney.compareTo(total_amount)==0){

                masterOrderEntity.setOrderId(out_trade_no);
                masterOrderEntity.setPayMode("2");
                masterOrderEntity.setResponseStatus(1);
                masterOrderEntity.setPayDate(new Date());
                masterOrderEntity.setStatus(Constants.OrderStatus.PAYORDER.getValue());
                masterOrderDao.updateById(masterOrderEntity);
            }else{
                //支付金额不一致,支付失败
                mapRtn.put("return_code", "FAIL");
                mapRtn.put("return_msg", "订单金额与支付金额不一致，支付失败");
            }
        } else {
            //System.out.println("position==========4");
            mapRtn.put("return_code", "SUCCESS");               //代表此单已经支付完成了，不需要进行二次支付
            mapRtn.put("return_msg", "OK");
            return mapRtn;
        }

        //2、更新从单状态，将从单对应的状态由1改为4
        if (masterOrderEntity.getReservationType() != Constants.ReservationType.ONLYROOMRESERVATION.getValue()) {
            //List<SlaveOrderEntity> slaveOrderEntitys=slaveOrderService.selectByOrderId(orderId);//应该包含从单
            //System.out.println("position==========5");
            if (slaveOrderService.selectCountOfNoPayOrderByOrderId(out_trade_no) == 0){
                TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
                mapRtn.put("return_code", "FAIL");
                mapRtn.put("return_msg", "无法获取从单需更新的支付信息，支付失败");
                return mapRtn;
            }
            //有从单--更新从单状态为已支付status 1-->4
            slaveOrderService.updateStatusByOrderId(masterOrderEntity.getOrderId(), Constants.OrderStatus.NOPAYORDER.getValue(), Constants.OrderStatus.PAYORDER.getValue());
           // System.out.println("position==========8");
            //3、扣除花费的赠送金
            BigDecimal totalFreeGold = slaveOrderService.getTotalFreeGoldByMasterOrderId(masterOrderEntity.getOrderId());
            if (totalFreeGold.compareTo(new BigDecimal("0")) > 0) {
               // System.out.println("position==========9");
                //注意此处是否会报错，特别注意
                clientUserService.subtractGiftByMasterOrderCreate(clientId, totalFreeGold.toString());
                //更新赠送金消费记录(clientUserId,当前时间，赠送金余额【有点麻烦】，花费的数量)
                recordGiftService.insertRecordGift2(masterOrderEntity.getCreator(), new Date(), clientUserService.selectById(clientId).getGift(), totalFreeGold);
            }
        }

        //4-1、检查商户信息
        MerchantDTO merchantDto = merchantService.get(masterOrderEntity.getMerchantId());

        if (merchantDto == null){

            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            mapRtn.put("return_code", "FAIL");
            mapRtn.put("return_msg", "支付失败！请联系管理员！【无法获取商户信息】");
            // System.out.println("exec004");
            return mapRtn;
        }

        //4-2、检查商户会员信息
        MerchantUserDTO userDto = merchantUserService.get(merchantDto.getCreator());
        if (userDto == null) {

           // System.out.println("position==========13");
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            mapRtn.put("return_code", "FAIL");
            mapRtn.put("return_msg", "支付失败！请联系管理员！【无法获取商户会员信息】");

            return mapRtn;

        } else {
            List<MerchantClientDTO> list = merchantClientService.getMerchantUserClientByMerchantId(masterOrderEntity.getMerchantId());
            String cId = list.size() > 0?list.get(0).getClientId():null;

           // System.out.println("position==========14");
            if (StringUtils.isNotBlank(cId)) {
                //System.out.println("position==========15");
                for (int i = 0; i < list.size(); i++) {
                    AppPushUtil.pushToSingleMerchant("订单管理", "您有新的订单，请注意查收！",  list.get(i).getClientId());
                }

                StimmeEntity stimmeEntity = new StimmeEntity();
                stimmeEntity.setCreateDate(new Date());                     //创建时间
                stimmeEntity.setOrderId(masterOrderEntity.getOrderId());    //订单号
                stimmeEntity.setType(1);                                    //新订单
                stimmeEntity.setMerchantId(masterOrderEntity.getMerchantId());//商家id
                stimmeEntity.setCreator(masterOrderEntity.getCreator());    //创建者

                stimmeService.insert(stimmeEntity);
               // System.out.println("position==========16");

            } else {

               // System.out.println("position==========17");
                TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
                mapRtn.put("return_code", "FAIL");
                mapRtn.put("return_msg", "支付失败！请联系管理员！【无法获取商户会员无clientId信息】");
                return mapRtn;

            }
        }

        MerchantUserEntity merchantUserEntity = merchantUserService.selectByMerchantId(masterOrderEntity.getMerchantId());
        if (merchantUserEntity != null) {
            SendSMSUtil.sendNewOrder(merchantUserEntity.getMobile(), smsConfig);
        }

        WebSocket wsByUser = wsPool.getWsByUser(masterOrderEntity.getMerchantId().toString());
        //System.out.println("wsByUser+++++++++++++++++++++++++++++:" + wsByUser);
        wsPool.sendMessageToUser(wsByUser, 2 + "");
        mapRtn.put("return_code", "SUCCESS");
        mapRtn.put("return_msg", "OK");
        return mapRtn;
    }

    @Override
    public Map<String, String> cashExecAliCallBack(BigDecimal total_amount, String out_trade_no) {
        Map<String, String> mapRtn = new HashMap<>(2);

        ChargeCashDTO chargeCashDTO = chargeCashService.selectByCashOrderId(out_trade_no);
        //        if(masterOrderEntity.getStatus()!=1){
////            mapRtn.put("return_code", "SUCCESS");
////            return mapRtn;
////        }

        if (chargeCashDTO == null || "".equals(chargeCashDTO)) {
            mapRtn.put("return_code", "FAIL");
            mapRtn.put("return_msg", "支付失败！请联系管理员！【没有此订单：" + out_trade_no + "】");
            return mapRtn;
        }

        if (chargeCashDTO.getStatus() != 1) {
            mapRtn.put("return_code", "SUCCESS");
            mapRtn.put("return_msg", "OK");
            return mapRtn;
        }

        /****************************************************************************************/
        try {
            if (chargeCashDTO.getCash().compareTo(total_amount) != 0) {
                System.out.println("支付失败！请联系管理员！【支付金额不一致】");
                throw new Exception("wx_pay_fail:code01");
            }

        } catch (Exception e) {
            mapRtn.put("return_code", "FAIL");
            mapRtn.put("return_msg", "支付失败！请联系管理员！【支付金额不一致】");
            return mapRtn;

        }
        try {
            if (chargeCashDTO == null) {
                System.out.println("支付失败！请联系管理员！【未找到订单】");
                throw new Exception("wx_pay_fail:code02");
            }
        } catch (Exception e) {
            mapRtn.put("return_code", "FAIL");
            mapRtn.put("return_msg", "支付失败！请联系管理员！【未找到订单】");
            return mapRtn;
        }

        chargeCashDTO.setStatus(2);
        chargeCashDTO.setSaveTime(new Date());
        chargeCashService.update(chargeCashDTO);
        ClientUserEntity clientUserEntity = clientUserService.selectById(chargeCashDTO.getUserId());
        BigDecimal balance = clientUserEntity.getBalance();
        balance = balance.add(total_amount);
        ChargeCashSetEntity chargeCashSetEntity = chargeCashSetService.selectByCash(chargeCashDTO.getCash());
        BigDecimal gift = clientUserEntity.getGift();
        gift = gift.add(chargeCashSetEntity.getChangeGift());
        clientUserEntity.setGift(gift);
        clientUserEntity.setBalance(balance);
        clientUserService.updateById(clientUserEntity);

        mapRtn.put("return_code", "SUCCESS");
        mapRtn.put("return_msg", "OK");

        UserTransactionDetailsEntity entiry = new UserTransactionDetailsEntity();
        entiry.setCreateDate(new Date());
        entiry.setMobile(clientUserEntity.getMobile());
        entiry.setMoney(total_amount);
        entiry.setPayMode(2);
        entiry.setType(1);
        entiry.setBalance(balance);
        entiry.setUserId(clientUserEntity.getId());
        userTransactionDetailsService.insert(entiry);

        //1----更新用户分级内容
        clientMemberGradeAssessment.growUpGrade(clientUserEntity.getId());
        return mapRtn;

    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Map<String, String> getAliNotify(BigDecimal total_amount, String out_trade_no) {

        System.out.println("ali" + out_trade_no);
        Map<String, String> mapRtn = new HashMap<>(2);

        MasterOrderEntity masterOrderEntity = masterOrderDao.selectByOrderId(out_trade_no);
        System.out.println("ali" + masterOrderEntity);
        System.out.println("ali" + masterOrderEntity.getPayMoney());
        System.out.println("ali" + masterOrderEntity.getPayMoney().compareTo(total_amount));
        if (masterOrderEntity.getPayMoney().compareTo(total_amount) != 0) {
            mapRtn.put("return_code", "FAIL");
            mapRtn.put("return_msg", "支付失败！请联系管理员！【支付金额不一致】");
            return mapRtn;
        }
        if (masterOrderEntity == null) {
            mapRtn.put("return_code", "FAIL");
            mapRtn.put("return_msg", "支付失败！请联系管理员！【未找到订单】");
            return mapRtn;
        }
        if (masterOrderEntity.getStatus() != 1) {
            mapRtn.put("return_code", "SUCCESS");
            mapRtn.put("return_msg", "OK");
            return mapRtn;
        }
        masterOrderEntity.setStatus(Constants.OrderStatus.PAYORDER.getValue());
        masterOrderEntity.setPayMode(Constants.PayMode.ALIPAY.getValue());

        System.out.println("ali" + masterOrderEntity.getReservationId());


        System.out.println("masterOrderEntity:" + masterOrderEntity);
        masterOrderEntity.setPayDate(new Date());
        masterOrderDao.updateById(masterOrderEntity);
        System.out.println("masterOrderEntity:" + masterOrderEntity);
        System.out.println("ali" + masterOrderEntity.getReservationType());
        if (masterOrderEntity.getReservationType() != Constants.ReservationType.ONLYROOMRESERVATION.getValue()) {
            List<SlaveOrderEntity> slaveOrderEntitys = slaveOrderService.selectByOrderId(out_trade_no);
            if (slaveOrderEntitys == null) {
                mapRtn.put("return_code", "FAIL");
                mapRtn.put("return_msg", "支付失败！请联系管理员！【未找到订单菜品】");
                return mapRtn;
            } else {
                slaveOrderEntitys.forEach(slaveOrderEntity -> {
                    slaveOrderEntity.setStatus(Constants.OrderStatus.PAYORDER.getValue());
                });
                boolean b = slaveOrderService.updateBatchById(slaveOrderEntitys);
                if (!b) {
                    mapRtn.put("return_code", "FAIL");
                    mapRtn.put("return_msg", "支付失败！请联系管理员！【更新菜品】");
                    return mapRtn;
                }
            }
        }

        MerchantDTO merchantDto = merchantService.get(masterOrderEntity.getMerchantId());
        if (null != merchantDto) {
            MerchantUserDTO userDto = merchantUserService.get(merchantDto.getCreator());
            List<MerchantClientDTO> list = merchantClientService.getMerchantUserClientByMerchantId(masterOrderEntity.getMerchantId());
            String clientId = null;
            if (list.size() == 0){
                System.out.println("PayService 1076");
            }else {
                clientId = list.get(0).getClientId();
            }
            if (null != userDto) {

                if (StringUtils.isNotBlank(clientId)) {
                    for (int i = 0; i < list.size(); i++) {
                        AppPushUtil.pushToSingleMerchant("订单管理", "您有新的订单，请注意查收！",  list.get(i).getClientId());
                    }
                } else {
                    mapRtn.put("return_code", "FAIL");
                    mapRtn.put("return_msg", "支付失败！请联系管理员！【无法获取商户会员无clientId信息】");
                    return mapRtn;
                }
            } else {
                mapRtn.put("return_code", "FAIL");
                mapRtn.put("return_msg", "支付失败！请联系管理员！【无法获取商户会员信息】");
                return mapRtn;
            }
        } else {
            mapRtn.put("return_code", "FAIL");
            mapRtn.put("return_msg", "支付失败！请联系管理员！【无法获取商户信息】");
            return mapRtn;
        }

        mapRtn.put("return_code", "SUCCESS");
        mapRtn.put("return_msg", "OK");
        return mapRtn;
    }
}
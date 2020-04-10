package io.treasure.service.impl;

import com.alipay.api.AlipayApiException;
import com.alipay.api.AlipayClient;
import com.alipay.api.domain.AlipayTradeRefundModel;
import com.alipay.api.request.AlipayTradeRefundRequest;
import com.alipay.api.response.AlipayTradeRefundResponse;
import io.treasure.annotation.LoginUser;
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
import io.treasure.enm.MerchantRoomEnm;
import io.treasure.entity.ClientUserEntity;
import io.treasure.entity.MasterOrderEntity;
import io.treasure.entity.SlaveOrderEntity;
import io.treasure.entity.StimmeEntity;
import io.treasure.push.AppPushUtil;
import io.treasure.service.*;
import io.treasure.utils.OrderUtil;
import io.treasure.utils.SendSMSUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
    MasterOrderService masterOrderService;
    @Autowired
    StimmeService stimmeService;
    @Autowired
    ClientUserServiceImpl clientUserService;

    @Autowired
    SlaveOrderService slaveOrderService;
    @Autowired
    private RecordGiftServiceImpl recordGiftService;
    @Autowired
    private AlipayClient alipayClient;

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
    private SMSConfig smsConfig;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Map<String, String> wxNotify(BigDecimal total_amount, String out_trade_no) {
        Map<String, String> mapRtn = new HashMap<>(2);
        MasterOrderEntity masterOrderEntity=masterOrderDao.selectByOrderId(out_trade_no);
//        if(masterOrderEntity.getStatus()!=1){
////            mapRtn.put("return_code", "SUCCESS");
////            return mapRtn;
////        }
        if(masterOrderEntity==null||"".equals(masterOrderEntity)){
            mapRtn.put("return_code", "FAIL");
            mapRtn.put("return_msg", "支付失败！请联系管理员！【没有此订单："+out_trade_no+"】");
            return mapRtn;
        }
        if(masterOrderEntity.getStatus()!=Constants.OrderStatus.NOPAYORDER.getValue()&&masterOrderEntity.getStatus()!=Constants.OrderStatus.MERCHANTTIMEOUTORDER.getValue()){
            mapRtn.put("return_code", "SUCCESS");
            mapRtn.put("return_msg", "OK");
            return mapRtn;
        }
        if(masterOrderEntity.getStatus()==Constants.OrderStatus.MERCHANTTIMEOUTORDER.getValue()){
            if(masterOrderEntity.getReservationId()!=null&&masterOrderEntity.getReservationId().longValue()>0){
                merchantRoomParamsSetService.updateStatus(masterOrderEntity.getReservationId(), MerchantRoomEnm.STATE_USE_YEA.getType());
            }
        }
        if(masterOrderEntity.getPayMoney().compareTo(total_amount)!=0){
            mapRtn.put("return_code", "FAIL");
            mapRtn.put("return_msg", "支付失败！请联系管理员！【支付金额不一致】");
            return mapRtn;
        }
        if(masterOrderEntity==null){
            mapRtn.put("return_code", "FAIL");
            mapRtn.put("return_msg", "支付失败！请联系管理员！【未找到订单】");
            return mapRtn;
        }
        if(masterOrderEntity.getStatus()!=1){
            mapRtn.put("return_code", "SUCCESS");
            mapRtn.put("return_msg", "OK");
            return mapRtn;
        }
        masterOrderEntity.setStatus(Constants.OrderStatus.PAYORDER.getValue());
        masterOrderEntity.setPayMode(Constants.PayMode.WXPAY.getValue());
        masterOrderEntity.setPayDate(new Date());
        masterOrderDao.updateById(masterOrderEntity);
        if(masterOrderEntity.getReservationType()!=Constants.ReservationType.ONLYROOMRESERVATION.getValue()){
            List<SlaveOrderEntity> slaveOrderEntitys=slaveOrderService.selectByOrderId(out_trade_no);
            if(slaveOrderEntitys==null){
                mapRtn.put("return_code", "FAIL");
                mapRtn.put("return_msg", "支付失败！请联系管理员！【未找到订单菜品】");
                return mapRtn;
            }else{
                slaveOrderEntitys.forEach(slaveOrderEntity -> {
                    slaveOrderEntity.setStatus(Constants.OrderStatus.PAYORDER.getValue());
                });
                boolean b=slaveOrderService.updateBatchById(slaveOrderEntitys);
                if(!b){
                    mapRtn.put("return_code", "FAIL");
                    mapRtn.put("return_msg", "支付失败！请联系管理员！【更新菜品】");
                    return mapRtn;
                }
            }
        }

        MerchantDTO merchantDto=merchantService.get(masterOrderEntity.getMerchantId());
        if(null!=merchantDto){
            MerchantUserDTO userDto= merchantUserService.get(merchantDto.getCreator());
            if(null!=userDto){
                String clientId=userDto.getClientId();
                if(StringUtils.isNotBlank(clientId)){
                    AppPushUtil.pushToSingleMerchant("订单管理","您有新的订单，请注意查收！","",userDto.getClientId());
                    StimmeEntity stimmeEntity = new StimmeEntity();
                    Date date = new Date();
                    stimmeEntity.setCreateDate(date);
                    stimmeEntity.setOrderId(masterOrderEntity.getOrderId());
                    stimmeEntity.setType(1);
                    stimmeEntity.setMerchantId(masterOrderEntity.getMerchantId());
                    stimmeEntity.setCreator(masterOrderEntity.getCreator());
                    stimmeService.insert(stimmeEntity);
                }else{
                    mapRtn.put("return_code", "FAIL");
                    mapRtn.put("return_msg", "支付失败！请联系管理员！【无法获取商户会员无clientId信息】");
                    return mapRtn;
                }
            }else{
                mapRtn.put("return_code", "FAIL");
                mapRtn.put("return_msg", "支付失败！请联系管理员！【无法获取商户会员信息】");
                return mapRtn;
            }
        }else{
            mapRtn.put("return_code", "FAIL");
            mapRtn.put("return_msg", "支付失败！请联系管理员！【无法获取商户信息】");
            return mapRtn;
        }
        Long creator = masterOrderEntity.getCreator();

        List<SlaveOrderEntity> slaveOrderEntities = slaveOrderService.selectByOrderId(masterOrderEntity.getOrderId());
        BigDecimal a = new BigDecimal(0);
        for (SlaveOrderEntity slaveOrderEntity : slaveOrderEntities) {
            a = a.add(slaveOrderEntity.getFreeGold());
        }
        ClientUserEntity clientUserEntity = clientUserService.selectById(creator);
        BigDecimal gift = clientUserEntity.getGift();
        gift=gift.subtract(a);
        clientUserEntity.setGift(gift);
        clientUserService.updateById(clientUserEntity);
        Date date = new Date();
        recordGiftService.insertRecordGift2(clientUserEntity.getId(),date,gift,a);
        if(null != merchantDto.getMobile()){
            SendSMSUtil.sendNewOrder(merchantDto.getMobile(),smsConfig);
        }
        mapRtn.put("return_code", "SUCCESS");
        mapRtn.put("return_msg", "OK");
        return mapRtn;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result aliRefund(String orderNo, String refund_fee, Long goodId) {
        AlipayTradeRefundRequest alipayRequest = new AlipayTradeRefundRequest();
        Result result=new Result();

        MasterOrderEntity masterOrderEntity=masterOrderDao.selectByOrderId(orderNo);
        // 实付金额，单位为分，只能为整数
        BigDecimal payMoney = masterOrderEntity.getPayMoney();
        //退款金额
        BigDecimal refundAmount = new BigDecimal(refund_fee);
        SlaveOrderDTO slaveOrderDTO=null;
        Long userId=masterOrderEntity.getCreator();
        if(masterOrderEntity.getCheckStatus()==1) {
            return result.error("已结算不可以退款！");
        }
        //判断是否可以退款

        //判断是否退菜
        if(goodId!=null){
            //退菜
            if(masterOrderEntity.getStatus()!=Constants.OrderStatus.PAYORDER.getValue()&&masterOrderEntity.getStatus()!=Constants.OrderStatus.MERCHANTRECEIPTORDER.getValue()){
                return result.error("无法退菜！【订单状态错误】");
            }
            slaveOrderDTO=slaveOrderService.getAllGoods(orderNo,goodId);
            if(slaveOrderDTO.getStatus()!=Constants.OrderStatus.MERCHANTREFUSALORDER.getValue()&&slaveOrderDTO.getStatus()!=Constants.OrderStatus.MERCHANTAGREEREFUNDORDER.getValue()){
                return result.error("无法退菜！【订单菜品状态错误】");
            }
            if(slaveOrderDTO.getPayMoney().compareTo(refundAmount)!=0){
                return result.error("退款金额不一致，无法退款！");
            }
        }else{
            //退单
            if(masterOrderEntity.getStatus()!=Constants.OrderStatus.MERCHANTREFUSALORDER.getValue()&&masterOrderEntity.getStatus()!=Constants.OrderStatus.MERCHANTAGREEREFUNDORDER.getValue()&&masterOrderEntity.getStatus()!=Constants.OrderStatus.MERCHANTTIMEOUTORDER.getValue()){
                return result.error("不是退款订单,无法退款！");
            }
            if(payMoney.compareTo(refundAmount)!=0){
                return result.error("退款金额不一致，无法退款！");
            }
        }
        AlipayTradeRefundModel model = new AlipayTradeRefundModel();
        // 商户订单号
        model.setOutTradeNo(orderNo);
        // 退款金额
        model.setRefundAmount(refund_fee);
        // 退款原因
        model.setRefundReason("无理由退货");
        // 退款订单号(同一个订单可以分多次部分退款，当分多次时必传)
        String refundNo=OrderUtil.getRefundOrderIdByTime(userId);
        model.setOutRequestNo(refundNo);
        alipayRequest.setBizModel(model);
        System.out.println(alipayRequest);
        AlipayTradeRefundResponse alipayResponse = null;
        try {
            alipayResponse = alipayClient.execute(alipayRequest);
            System.out.println("alipayResponse:"+alipayResponse);
            System.out.println("alipayResponse.getCode():"+alipayResponse.getCode());
        } catch (AlipayApiException e) {
            e.printStackTrace();
        }
        if(alipayResponse.getCode().equals("10000")){
            if (goodId != null) {
                //将退款ID更新到refundOrder表中refund_id
                refundOrderService.updateRefundId(refundNo, orderNo, goodId);
                //将退款ID更新到订单菜品表中
                slaveOrderService.updateRefundId(refundNo, orderNo, goodId);
                BigDecimal bigDecimal=payMoney.subtract(refundAmount);
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
                masterOrderService.updateSlaveOrderPointDeduction(mp,pb,orderNo);
                BigDecimal a=new BigDecimal("0");
                //退菜后将订单菜品表中对应菜品平台扣点和商户所得金额清除掉
                slaveOrderService.updateSlaveOrderPointDeduction(a,a,orderNo,goodId);
                ClientUserEntity clientUser = clientUserService.getClientUser(masterOrderEntity.getCreator());
                BigDecimal gift = clientUser.getGift();
                clientUser.setGift(gift.add(allGoods.getFreeGold()));
                clientUserService.updateById(clientUser);
                return result.ok(true);
            }else{
                masterOrderEntity.setRefundId(refundNo);
                masterOrderService.update(ConvertUtils.sourceToTarget(masterOrderEntity, MasterOrderDTO.class));
                List<SlaveOrderEntity> slaveOrderEntityList=slaveOrderService.selectByOrderId(orderNo);
                BigDecimal a=new BigDecimal("0");
                for(int i=0;i<slaveOrderEntityList.size();i++){
                    SlaveOrderEntity slaveOrderEntity=slaveOrderEntityList.get(i);
                    if(slaveOrderEntity.getRefundId()==null||slaveOrderEntity.getRefundId().length()==0){
                        slaveOrderEntity.setRefundId(refundNo);
                    }
                }
                ClientUserEntity clientUser = clientUserService.getClientUser(masterOrderEntity.getCreator());
                BigDecimal gift = clientUser.getGift();
                clientUser.setGift(gift.add(masterOrderEntity.getGiftMoney()));
                clientUserService.updateById(clientUser);
                slaveOrderService.updateSlaveOrderPointDeduction(a,a,orderNo,goodId);
                masterOrderService.updateSlaveOrderPointDeduction(a,a,orderNo);
                return result.ok(true);
            }
        }
        return result.ok(false);
    }

    /**
     *  微信订单退款功能
     * @param orderNo 订单号
     * @param refund_fee  退款金额
     * @return
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result wxRefund(String orderNo, String refund_fee){
        return wxRefund(orderNo,refund_fee,null);
    }

    /**
     * 订单退款功能
     * @param payMode  支付方式
     * @param orderNo   订单号
     * @param refund_fee  退菜金额
     * @return
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result refundByOrder(String payMode, String orderNo, String refund_fee) {
        if(payMode.equals(Constants.PayMode.WXPAY.getValue())){
            return this.wxRefund(orderNo,refund_fee,null);
        }
        if(payMode.equals(Constants.PayMode.ALIPAY.getValue())){
            return this.aliRefund(orderNo,refund_fee,null);
        }
        return new Result().error("退款失败！【无支付方式】");
    }

    /**
     * 订单退款功能
     * @param orderNo   订单号
     * @param refund_fee  退菜金额
     * @return
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result refundByOrder(String orderNo, String refund_fee) {
        MasterOrderEntity masterOrderEntity=masterOrderDao.selectByOrderId(orderNo);
        String payMode=masterOrderEntity.getPayMode();
        if(payMode.equals(Constants.PayMode.WXPAY.getValue())){
            return this.wxRefund(orderNo,refund_fee,null);
        }
        if(payMode.equals(Constants.PayMode.ALIPAY.getValue())){
            return this.aliRefund(orderNo,refund_fee,null);
        }
        return new Result().error("退款失败！【无支付方式】");
    }

    /**
     * 菜品退款功能
     * @param orderNo   订单号
     * @param refund_fee  退菜金额
     * @param goodId    退菜ID
     * @return
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result refundByGood(String orderNo, String refund_fee,Long goodId) {
        MasterOrderEntity masterOrderEntity=masterOrderDao.selectByOrderId(orderNo);
        String payMode=masterOrderEntity.getPayMode();
        if(payMode.equals(Constants.PayMode.WXPAY.getValue())){
            return this.wxRefund(orderNo,refund_fee,goodId);
        }
        if(payMode.equals(Constants.PayMode.ALIPAY.getValue())){
            return this.aliRefund(orderNo,refund_fee,goodId);
        }
        return new Result().error("退款失败！【无支付方式】");
    }

    /**
     * 菜品退款功能
     * @param payMode  支付方式
     * @param orderNo   订单号
     * @param refund_fee  退菜金额
     * @param goodId    退菜ID
     * @return
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result refundByGood(String payMode, String orderNo, String refund_fee,Long goodId) {
        if(payMode.equals(Constants.PayMode.WXPAY.getValue())){
            return this.wxRefund(orderNo,refund_fee,goodId);
        }
        if(payMode.equals(Constants.PayMode.ALIPAY.getValue())){
            return this.aliRefund(orderNo,refund_fee,goodId);
        }
        return new Result().error("退款失败！【无支付方式】");
    }

    /**
     *  微信菜品退款功能
     * @param orderNo 订单号
     * @param goodId    退菜编号
     * @return
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result wxRefund(String orderNo, String refund_fee, Long goodId){
        Map<String, String> reqData = new HashMap<>();
        Result result=new Result();
        MasterOrderEntity masterOrderEntity=masterOrderDao.selectByOrderId(orderNo);
        // 订单总金额，单位为分，只能为整数
        BigDecimal totalAmount = masterOrderEntity.getTotalMoney();
        BigDecimal payMoney = masterOrderEntity.getPayMoney();
        //退款金额
        BigDecimal refundAmount = new BigDecimal(refund_fee);
        SlaveOrderDTO slaveOrderDTO=null;
        Long userId=masterOrderEntity.getCreator();
        if(masterOrderEntity.getCheckStatus()==1) {
            return result.error("已结算不可以退款！");
        }
        //判断是否可以退款

        //判断是否退菜
        if(goodId!=null){
            //退菜
            if(masterOrderEntity.getStatus()!=Constants.OrderStatus.PAYORDER.getValue()&&masterOrderEntity.getStatus()!=Constants.OrderStatus.MERCHANTRECEIPTORDER.getValue()){
                return result.error("无法退菜！【订单状态错误】");
            }
            slaveOrderDTO=slaveOrderService.getAllGoods(orderNo,goodId);
            if(slaveOrderDTO.getStatus()!=Constants.OrderStatus.MERCHANTREFUSALORDER.getValue()&&slaveOrderDTO.getStatus()!=Constants.OrderStatus.MERCHANTAGREEREFUNDORDER.getValue()){
                return result.error("无法退菜！【订单菜品状态错误】");
            }
            if(slaveOrderDTO.getPayMoney().compareTo(refundAmount)!=0){
                return result.error("退款金额不一致，无法退款！");
            }
        }else{
            //退单
            if(masterOrderEntity.getStatus()!=Constants.OrderStatus.MERCHANTREFUSALORDER.getValue()&&masterOrderEntity.getStatus()!=Constants.OrderStatus.MERCHANTAGREEREFUNDORDER.getValue()&&masterOrderEntity.getStatus()!=Constants.OrderStatus.MERCHANTTIMEOUTORDER.getValue()){
                return result.error("不是退款订单,无法退款！");
            }
            if(payMoney.compareTo(refundAmount)!=0){
                return result.error("退款金额不一致，无法退款！");
            }
        }
        // 商户订单号
        reqData.put("out_trade_no", orderNo);
        // 授权码
        String refundNo=OrderUtil.getRefundOrderIdByTime(userId);
        reqData.put("out_refund_no", refundNo);


        BigDecimal total = totalAmount.multiply(new BigDecimal(100));  //接口中参数支付金额单位为【分】，参数值不能带小数，所以乘以100
        java.text.DecimalFormat df=new java.text.DecimalFormat("0");
        reqData.put("total_fee", df.format(total));

        BigDecimal refund = refundAmount.multiply(new BigDecimal(100));  //接口中参数支付金额单位为【分】，参数值不能带小数，所以乘以100
        reqData.put("refund_fee", df.format(refund));
        // 退款异步通知地址
        reqData.put("notify_url", wxPayConfig.getNotifyUrl());
        reqData.put("refund_fee_type", "CNY");
        reqData.put("op_user_id", wxPayConfig.getMchID());
        System.out.println("reqData"+reqData);
        Map<String, String> resultMap = null;
        try {
            resultMap = wxPay.refund(reqData);
            System.out.println("resultMap:"+resultMap);
        } catch (Exception e) {
            return result.error("退款失败！");
        }
        boolean rtn = resultMap.get("return_code").equals(WXPayConstants.SUCCESS) && resultMap.get("result_code").equals(WXPayConstants.SUCCESS);
        System.out.println("rtn:"+rtn);
        if(rtn) {

            if (goodId != null) {
                //将退款ID更新到refundOrder表中refund_id
                refundOrderService.updateRefundId(refundNo, orderNo, goodId);

                //将退款ID更新到订单菜品表中
                slaveOrderService.updateRefundId(refundNo, orderNo, goodId);
                BigDecimal bigDecimal=totalAmount.subtract(refundAmount);
                masterOrderEntity.setPayMoney(bigDecimal);
                masterOrderService.update(ConvertUtils.sourceToTarget(masterOrderEntity, MasterOrderDTO.class));
                SlaveOrderDTO allGoods = slaveOrderService.getAllGoods(orderNo, goodId);
                OrderDTO order = masterOrderService.getOrder(orderNo);
                BigDecimal platformBrokerage = order.getPlatformBrokerage();
                BigDecimal merchantProceeds = order.getMerchantProceeds();
                //退菜后将平台扣点金额和商户所得更新到主订单表中
                masterOrderService.updateSlaveOrderPointDeduction(merchantProceeds.subtract(allGoods.getMerchantProceeds()),platformBrokerage.subtract(allGoods.getPlatformBrokerage()),orderNo);
                BigDecimal a=new BigDecimal("0");
                //退菜后将订单菜品表中对应菜品平台扣点和商户所得金额清除掉
                slaveOrderService.updateSlaveOrderPointDeduction(a,a,orderNo,goodId);
                return result.ok(true);
            }else{
                masterOrderEntity.setRefundId(refundNo);
                masterOrderEntity.setPayMoney(masterOrderEntity.getPayMoney());
                masterOrderService.update(ConvertUtils.sourceToTarget(masterOrderEntity, MasterOrderDTO.class));
                List<SlaveOrderEntity> slaveOrderEntityList=slaveOrderService.selectByOrderId(orderNo);
                BigDecimal a=new BigDecimal("0");
                for(int i=0;i<slaveOrderEntityList.size();i++){
                    SlaveOrderEntity slaveOrderEntity=slaveOrderEntityList.get(i);
                    if(slaveOrderEntity.getRefundId()==null||slaveOrderEntity.getRefundId().length()==0){
                        slaveOrderEntity.setRefundId(refundNo);
                    }
                    slaveOrderService.updateSlaveOrderPointDeduction(a,a,orderNo,goodId);
                }
                masterOrderService.updateSlaveOrderPointDeduction(a,a,orderNo);
                return result.ok(true);
            }

        }
        return result.ok(false);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Map<String, String> getAliNotify(BigDecimal total_amount, String out_trade_no) {

        System.out.println("ali"+out_trade_no);
        Map<String, String> mapRtn = new HashMap<>(2);

        MasterOrderEntity masterOrderEntity=masterOrderDao.selectByOrderId(out_trade_no);
        System.out.println("ali"+masterOrderEntity);
        System.out.println("ali"+masterOrderEntity.getPayMoney());
        System.out.println("ali"+masterOrderEntity.getPayMoney().compareTo(total_amount));
        if(masterOrderEntity.getPayMoney().compareTo(total_amount)!=0){
            mapRtn.put("return_code", "FAIL");
            mapRtn.put("return_msg", "支付失败！请联系管理员！【支付金额不一致】");
            return mapRtn;
        }
        if(masterOrderEntity==null){
            mapRtn.put("return_code", "FAIL");
            mapRtn.put("return_msg", "支付失败！请联系管理员！【未找到订单】");
            return mapRtn;
        }
        if(masterOrderEntity.getStatus()!=1){
            mapRtn.put("return_code", "SUCCESS");
            mapRtn.put("return_msg", "OK");
            return mapRtn;
        }
        masterOrderEntity.setStatus(Constants.OrderStatus.PAYORDER.getValue());
        masterOrderEntity.setPayMode(Constants.PayMode.ALIPAY.getValue());

        System.out.println("ali"+masterOrderEntity.getReservationId());


        System.out.println("masterOrderEntity:"+masterOrderEntity);
        masterOrderEntity.setPayDate(new Date());
        masterOrderDao.updateById(masterOrderEntity);
        System.out.println("masterOrderEntity:"+masterOrderEntity);
        System.out.println("ali"+masterOrderEntity.getReservationType());
        if(masterOrderEntity.getReservationType()!=Constants.ReservationType.ONLYROOMRESERVATION.getValue()){
            List<SlaveOrderEntity> slaveOrderEntitys=slaveOrderService.selectByOrderId(out_trade_no);
            if(slaveOrderEntitys==null){
                mapRtn.put("return_code", "FAIL");
                mapRtn.put("return_msg", "支付失败！请联系管理员！【未找到订单菜品】");
                return mapRtn;
            }else{
                slaveOrderEntitys.forEach(slaveOrderEntity -> {
                    slaveOrderEntity.setStatus(Constants.OrderStatus.PAYORDER.getValue());
                });
                boolean b=slaveOrderService.updateBatchById(slaveOrderEntitys);
                if(!b){
                    mapRtn.put("return_code", "FAIL");
                    mapRtn.put("return_msg", "支付失败！请联系管理员！【更新菜品】");
                    return mapRtn;
                }
            }
        }

        MerchantDTO merchantDto=merchantService.get(masterOrderEntity.getMerchantId());
        if(null!=merchantDto){
            MerchantUserDTO userDto= merchantUserService.get(merchantDto.getCreator());
            if(null!=userDto){
                String clientId=userDto.getClientId();
                if(StringUtils.isNotBlank(clientId)){
                    AppPushUtil.pushToSingleMerchant("订单管理","您有新的订单，请注意查收！","",userDto.getClientId());
                }else{
                    mapRtn.put("return_code", "FAIL");
                    mapRtn.put("return_msg", "支付失败！请联系管理员！【无法获取商户会员无clientId信息】");
                    return mapRtn;
                }
            }else{
                mapRtn.put("return_code", "FAIL");
                mapRtn.put("return_msg", "支付失败！请联系管理员！【无法获取商户会员信息】");
                return mapRtn;
            }
        }else{
            mapRtn.put("return_code", "FAIL");
            mapRtn.put("return_msg", "支付失败！请联系管理员！【无法获取商户信息】");
            return mapRtn;
        }

        mapRtn.put("return_code", "SUCCESS");
        mapRtn.put("return_msg", "OK");
        return mapRtn;
    }
}
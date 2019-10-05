package io.treasure.service.impl;

import com.alipay.api.AlipayApiException;
import com.alipay.api.AlipayClient;
import com.alipay.api.domain.AlipayTradeRefundModel;
import com.alipay.api.request.AlipayTradeRefundRequest;
import com.alipay.api.response.AlipayTradeRefundResponse;
import io.treasure.dao.MasterOrderDao;
import io.treasure.dao.SlaveOrderDao;
import io.treasure.dto.ClientUserDTO;
import io.treasure.dto.MerchantDTO;
import io.treasure.dto.MerchantUserDTO;
import io.treasure.enm.Constants;
import io.treasure.entity.ClientUserEntity;
import io.treasure.entity.MasterOrderEntity;
import io.treasure.entity.SlaveOrderEntity;
import io.treasure.push.AppPushUtil;
import io.treasure.service.MerchantUserService;
import io.treasure.service.PayService;
import io.treasure.service.RefundOrderService;
import io.treasure.service.SlaveOrderService;
import io.treasure.utils.OrderUtil;
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
    SlaveOrderService slaveOrderService;

    @Autowired
    private AlipayClient alipayClient;



    @Autowired
    MerchantServiceImpl merchantService;
    @Autowired
    MerchantUserService merchantUserService;

    @Autowired
    private RefundOrderService refundOrderService;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Map<String, String> wxNotify(BigDecimal total_amount, String out_trade_no) {
        Map<String, String> mapRtn = new HashMap<>(2);
        System.out.println("---out_trade_no------------"+out_trade_no);
        MasterOrderEntity masterOrderEntity=masterOrderDao.selectByOrderId(out_trade_no);
        System.out.println("---masterOrderEntity------------"+masterOrderEntity);
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

    @Override
    @Transactional(rollbackFor = Exception.class)
    public String aliRefund(String orderNo, String refund_fee, Long goodId, ClientUserEntity user) {
        AlipayTradeRefundRequest alipayRequest = new AlipayTradeRefundRequest();

        AlipayTradeRefundModel model = new AlipayTradeRefundModel();
        // 商户订单号
        model.setOutTradeNo(orderNo);
        // 退款金额
        model.setRefundAmount(refund_fee);
        // 退款原因
        model.setRefundReason("无理由退货");
        // 退款订单号(同一个订单可以分多次部分退款，当分多次时必传)
        model.setOutRequestNo(OrderUtil.getRefundOrderIdByTime(user.getId()));
        alipayRequest.setBizModel(model);

        AlipayTradeRefundResponse alipayResponse = null;
        try {
            alipayResponse = alipayClient.execute(alipayRequest);
        } catch (AlipayApiException e) {
            e.printStackTrace();
        }
        System.out.println(alipayResponse.getBody());
        if(alipayResponse.getCode()=="10000"){
            if(goodId!=null) {
                //将退款ID更新到refundOrder表中refund_id
                refundOrderService.updateRefundId(OrderUtil.getRefundOrderIdByTime(user.getId()), orderNo, goodId);

                //将退款ID更新到订单菜品表中
                slaveOrderService.updateRefundId(OrderUtil.getRefundOrderIdByTime(user.getId()), orderNo, goodId);

            }
        }
        return alipayResponse.getBody();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Map<String, String> getAliNotify(BigDecimal total_amount, String out_trade_no) {
        Map<String, String> mapRtn = new HashMap<>(2);
        System.out.println("---out_trade_no------------"+out_trade_no);
        MasterOrderEntity masterOrderEntity=masterOrderDao.selectByOrderId(out_trade_no);
        System.out.println("---masterOrderEntity------------"+masterOrderEntity);
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
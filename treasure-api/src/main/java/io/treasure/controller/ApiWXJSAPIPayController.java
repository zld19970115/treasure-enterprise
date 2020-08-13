package io.treasure.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import io.treasure.common.constant.WXPayConstants;
import io.treasure.common.utils.Result;
import io.treasure.common.utils.WXPayUtil;
import io.treasure.config.MiniWXConfig;
import io.treasure.config.MiniWXPay;
import io.treasure.dto.ChargeCashDTO;
import io.treasure.dto.OrderDTO;
import io.treasure.enm.Constants;
import io.treasure.entity.MasterOrderEntity;
import io.treasure.entity.SlaveOrderEntity;
import io.treasure.service.ChargeCashService;
import io.treasure.service.MasterOrderService;
import io.treasure.service.PayService;
import io.treasure.service.SlaveOrderService;
import io.treasure.utils.AdressIPUtil;
import io.treasure.utils.OrderUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api/jsapiPay")
@Api(tags="微信支付-小程序支付.")
public class ApiWXJSAPIPayController {

    @Autowired
    private MiniWXPay wxPay;

    @Autowired
    private MiniWXConfig miniWXConfig;
    @Autowired
    private ChargeCashService chargeCashService;
    @Autowired
    private PayService payService;

    @Autowired
    private MasterOrderService masterOrderService;
    @Autowired
    private SlaveOrderService slaveOrderService;
    /**
     * @param total_fee    支付金额
     * @param description    描述
     * @param request
     * @return
     */
//    @Login
    @PostMapping("/wxpay")
    @ApiOperation(value="微信支付")
    @ApiImplicitParams({
            @ApiImplicitParam(name="orderNo",value="订单编号",required=true,paramType="query"),
            @ApiImplicitParam(name="total_fee",value="订单总金额(元)",required=true,paramType="query"),
            @ApiImplicitParam(name="openid",value="用户标识（openid）",required=true,paramType="query"),
            @ApiImplicitParam(name="description",value="产品描述",required=true,paramType="query"),
    })
    public Result wxpay(HttpServletRequest request, String total_fee,String openid, String orderNo, String description) throws Exception {
        Result result = new Result();
        OrderDTO orderDTO=masterOrderService.getOrder(orderNo);
        MasterOrderEntity masterOrderEntity = masterOrderService.selectByOrderId(orderNo);
        if(orderDTO.getStatus().intValue()!= Constants.OrderStatus.NOPAYORDER.getValue()){
            return result.error(-1,"非未支付订单，请选择未支付订单支付！");
        }
        Integer payMode = masterOrderService.selectByPayMode(orderDTO.getOrderId());
        if (payMode!=null){
            if (payMode!=2){
                orderDTO.setOrderId(OrderUtil.getOrderIdByTime(orderDTO.getCreator()));
                List<SlaveOrderEntity> slaveOrderEntities = slaveOrderService.selectByOrderId(orderNo);
                for (SlaveOrderEntity slaveOrderEntity : slaveOrderEntities) {
                    slaveOrderEntity.setOrderId(orderDTO.getOrderId());
                    slaveOrderService.updateById(slaveOrderEntity);
                }
                masterOrderEntity.setOrderId(orderDTO.getOrderId());
                masterOrderService.updateById(masterOrderEntity);
            }
        }

        HashMap<String, String> data = new HashMap<>();
        data.put("body", description);
        data.put("out_trade_no", orderDTO.getOrderId());
//        data.put("device_info", "");//调用接口提交的终端设备号
        data.put("fee_type", "CNY");
        //因为是外币，这里做汇率转换
        BigDecimal totalAmount = new BigDecimal(total_fee);
        BigDecimal payCoins = masterOrderEntity.getPayCoins();
        totalAmount = totalAmount.subtract(payCoins).setScale(2,BigDecimal.ROUND_HALF_DOWN);
        System.out.println("jsapi:"+totalAmount);

        BigDecimal total = totalAmount.multiply(new BigDecimal(100));  //接口中参数支付金额单位为【分】，参数值不能带小数，所以乘以100
        java.text.DecimalFormat df=new java.text.DecimalFormat("0");
        data.put("total_fee",df.format(total));
        data.put("spbill_create_ip", AdressIPUtil.getClientIpAddress(request));
        data.put("notify_url", miniWXConfig.getNotifyUrl());
        data.put("trade_type", "JSAPI");//支付类型
        data.put("openid",openid);
        data.put("attach","ZF");//订单附加信息 (业务需要数据，自定义的)

        Map<String, String> orderInfo= wxPay.unifiedOrder(data);
        boolean rtn = orderInfo.get("return_code").equals(WXPayConstants.SUCCESS) && orderInfo.get("result_code").equals(WXPayConstants.SUCCESS);

        if(rtn) {

            HashMap<String, String> params = new HashMap<String, String>();
            params.put("appId", miniWXConfig.getAppID());
            Long time = (System.currentTimeMillis() / 1000);
            params.put("timeStamp", time.toString());
            params.put("nonceStr", orderInfo.get("nonce_str"));
            params.put("package", "prepay_id="+orderInfo.get("prepay_id"));
            params.put("signType", "MD5");
            String sign = WXPayUtil.generateSignature(params, miniWXConfig.getKey(), WXPayConstants.SignType.MD5);

            Map<String, String> reslutMap = new HashMap<>();
            reslutMap.put("appId",miniWXConfig.getAppID());
            reslutMap.put("timeStamp",time.toString());
            reslutMap.put("nonceStr",orderInfo.get("nonce_str"));
            reslutMap.put("package","prepay_id="+orderInfo.get("prepay_id"));
            reslutMap.put("signType","MD5");
            reslutMap.put("paySign", sign);
            result.ok(reslutMap);
        }else
        {
            result.error("code:["+orderInfo.get("err_code")+"],"+orderInfo.get("err_code_des"));
        }
        return result;
    }


    /**
     * @param total_fee    支付金额
     * @param description    描述
     * @param request
     * @return
     */
//    @Login
    @PostMapping("/wxCashpay")
    @ApiOperation(value="小程序现金充值微信支付")
    @ApiImplicitParams({
            @ApiImplicitParam(name="orderNo",value="订单编号",required=true,paramType="query"),
            @ApiImplicitParam(name="total_fee",value="订单总金额(元)",required=true,paramType="query"),
            @ApiImplicitParam(name="openid",value="用户标识（openid）",required=true,paramType="query"),
            @ApiImplicitParam(name="description",value="产品描述",required=true,paramType="query"),
    })
    public Result wxCashpay(HttpServletRequest request, String total_fee,String openid, String orderNo, String description) throws Exception {
        Result result = new Result();
        ChargeCashDTO chargeCashDTO = chargeCashService.selectByCashOrderId(orderNo);
        if(chargeCashDTO.getStatus().intValue()!= 1){
            return result.error(-1,"非未支付订单，请选择未支付订单支付！");
        }
        HashMap<String, String> data = new HashMap<>();
        data.put("body", description);
        data.put("out_trade_no", orderNo);
//        data.put("device_info", "");//调用接口提交的终端设备号
        data.put("fee_type", "CNY");
        //因为是外币，这里做汇率转换
        BigDecimal totalAmount = new BigDecimal(total_fee);
        BigDecimal total = totalAmount.multiply(new BigDecimal(100));  //接口中参数支付金额单位为【分】，参数值不能带小数，所以乘以100
        java.text.DecimalFormat df=new java.text.DecimalFormat("0");
        data.put("total_fee",df.format(total));
        data.put("spbill_create_ip", AdressIPUtil.getClientIpAddress(request));
        data.put("notify_url", miniWXConfig.getNotifyUrl());
        data.put("trade_type", "JSAPI");//支付类型
        data.put("openid",openid);
        data.put("attach","ZF");//订单附加信息 (业务需要数据，自定义的)

        Map<String, String> orderInfo= wxPay.unifiedOrder(data);
        boolean rtn = orderInfo.get("return_code").equals(WXPayConstants.SUCCESS) && orderInfo.get("result_code").equals(WXPayConstants.SUCCESS);

        if(rtn) {

            HashMap<String, String> params = new HashMap<String, String>();
            params.put("appId", miniWXConfig.getAppID());
            Long time = (System.currentTimeMillis() / 1000);
            params.put("timeStamp", time.toString());
            params.put("nonceStr", orderInfo.get("nonce_str"));
            params.put("package", "prepay_id="+orderInfo.get("prepay_id"));
            params.put("signType", "MD5");
            String sign = WXPayUtil.generateSignature(params, miniWXConfig.getKey(), WXPayConstants.SignType.MD5);

            Map<String, String> reslutMap = new HashMap<>();
            reslutMap.put("appId",miniWXConfig.getAppID());
            reslutMap.put("timeStamp",time.toString());
            reslutMap.put("nonceStr",orderInfo.get("nonce_str"));
            reslutMap.put("package","prepay_id="+orderInfo.get("prepay_id"));
            reslutMap.put("signType","MD5");
            reslutMap.put("paySign", sign);
            result.ok(reslutMap);
        }else
        {
            result.error("code:["+orderInfo.get("err_code")+"],"+orderInfo.get("err_code_des"));
        }
        return result;
    }
}


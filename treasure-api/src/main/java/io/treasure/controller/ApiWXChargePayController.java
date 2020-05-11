package io.treasure.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import io.treasure.common.constant.WXPayConstants;
import io.treasure.common.utils.Result;
import io.treasure.common.utils.WXPayUtil;
import io.treasure.config.IWXConfig;
import io.treasure.config.IWXPay;
import io.treasure.dto.ChargeCashDTO;
import io.treasure.service.ChargeCashService;
import io.treasure.service.PayService;
import io.treasure.service.SlaveOrderService;
import io.treasure.utils.AdressIPUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/cashWxPay")
@Api(tags="微信支付-现金充值")
public class ApiWXChargePayController {

    @Autowired
    private IWXPay wxPay;

    @Autowired
    private IWXConfig wxPayConfig;

    @Autowired
    private PayService payService;

    @Autowired
    private ChargeCashService chargeCashService;
    @Autowired
    private SlaveOrderService slaveOrderService;

    /**
     * （防止过多更改原程序
     * @param originalOrderId
     * @return 同位数参数尾数自增1的值
     */
    public String generalGrowUpOrderId(String originalOrderId){
        String prefixOrder = originalOrderId.substring(0,originalOrderId.length()-3);                                   //截取前缀
        String suffixOrder = "000"+(Integer.parseInt(originalOrderId.substring(originalOrderId.length()-3))+1)+"";      //截取后缀
        suffixOrder = suffixOrder.substring((suffixOrder.length()-3));

        return prefixOrder+suffixOrder;
    }
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
            @ApiImplicitParam(name="description",value="产品描述",required=true,paramType="query"),
    })
    public Result wxpay(HttpServletRequest request, String total_fee, String orderNo, String description) throws Exception {
        Result result = new Result();
        ChargeCashDTO chargeCashDTO = chargeCashService.selectByCashOrderId(orderNo);
//        // 防止微信支付失败重新支付失败
//        String tmpOrderId =generalGrowUpOrderId(orderNo);
//        List<SlaveOrderEntity> slaveOrderEntities = slaveOrderService.selectByOrderId(orderNo);
//        orderDTO.setOrderId(tmpOrderId);
//        for (SlaveOrderEntity slaveOrderEntity : slaveOrderEntities) {
//            slaveOrderEntity.setOrderId(tmpOrderId);
//            slaveOrderService.updateById(slaveOrderEntity);
//        }
//
//        MasterOrderEntity masterOrderEntity = masterOrderService.selectByOrderId(orderNo);
//        masterOrderEntity.setOrderId(tmpOrderId);
//        masterOrderService.updateById(masterOrderEntity);
//
//        if(orderDTO.getStatus().intValue()!= Constants.OrderStatus.NOPAYORDER.getValue()){
//            return result.error(-1,"非未支付订单，请选择未支付订单支付！");
//        }
        HashMap<String, String> data = new HashMap<>();
        data.put("body", description);
        data.put("out_trade_no",chargeCashDTO.getCashOrderId()); //更改为新订单号
//        data.put("device_info", "");//调用接口提交的终端设备号
        data.put("fee_type", "CNY");
        //因为是外币，这里做汇率转换
        BigDecimal totalAmount = new BigDecimal(total_fee);
        BigDecimal total = totalAmount.multiply(new BigDecimal(100));  //接口中参数支付金额单位为【分】，参数值不能带小数，所以乘以100
        java.text.DecimalFormat df=new java.text.DecimalFormat("0");
        data.put("total_fee",df.format(total));
        data.put("spbill_create_ip", AdressIPUtil.getClientIpAddress(request));
        data.put("notify_url", wxPayConfig.getNotifyUrl());
        data.put("trade_type", "APP");//支付类型
        data.put("attach","ZF");//订单附加信息 (业务需要数据，自定义的)

        Map<String, String> orderInfo= wxPay.unifiedOrder(data);
        boolean rtn = orderInfo.get("return_code").equals(WXPayConstants.SUCCESS) && orderInfo.get("result_code").equals(WXPayConstants.SUCCESS);

        if(rtn) {

            HashMap<String, String> params = new HashMap<String, String>();
            params.put("appid", wxPayConfig.getAppID());
            params.put("partnerid", wxPayConfig.getMchID());
            Long time = (System.currentTimeMillis() / 1000);
            params.put("timestamp", time.toString());
            params.put("noncestr", orderInfo.get("nonce_str"));
            params.put("prepayid", orderInfo.get("prepay_id"));
            params.put("package", "Sign=WXPay");
            String sign = WXPayUtil.generateSignature(params, wxPayConfig.getKey());
            Map<String, String> reslutMap = new HashMap<>();
            reslutMap.put("appid",wxPayConfig.getAppID());
            reslutMap.put("partnerid",wxPayConfig.getMchID());
            reslutMap.put("timestamp",time.toString());
            reslutMap.put("noncestr",orderInfo.get("nonce_str"));
            reslutMap.put("prepayid",orderInfo.get("prepay_id"));
            reslutMap.put("package","Sign=WXPay");
            reslutMap.put("sign", sign);
            result.ok(reslutMap);
        }
        return result;
    }
}

package io.treasure.controller;

import com.alipay.api.AlipayApiException;
import com.alipay.api.AlipayClient;
import com.alipay.api.DefaultAlipayClient;
import com.alipay.api.domain.AlipayTradeAppPayModel;
import com.alipay.api.request.AlipayTradeAppPayRequest;
import com.alipay.api.response.AlipayTradeAppPayResponse;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import io.treasure.annotation.Login;
import io.treasure.common.utils.Result;
import io.treasure.config.AlipayProperties;
import io.treasure.dto.OrderDTO;
import io.treasure.enm.Constants;
import io.treasure.service.MasterOrderService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

/**
 * 支付宝-APP支付 控制器.
 */
@Slf4j
@RestController
@RequestMapping("/api/aliApp")
@Api(tags = "支付宝-app支付")
public class ApiAliAppPayController  {
    @Autowired
    private AlipayProperties aliPayProperties;

    @Autowired
    private MasterOrderService masterOrderService;

    /**
     *app支付
     *
     *@author lp
     *@date 2019/1/4 16:32
     */
    @Login
    @PostMapping("/appPay")
    @ApiOperation(value="当面付-条码付")
    @ApiImplicitParams({
            @ApiImplicitParam(name="orderNo",value="订单编号",required=true,paramType="query"),
            @ApiImplicitParam(name="body",value="订单描述",required=true,paramType="query"),
            @ApiImplicitParam(name="subject",value="订单标题",required=true,paramType="query"),
            @ApiImplicitParam(name="totalAmount",value="订单金额",required=true,paramType="query")
    })
    public Result appPay(String body, String subject, String orderNo, String totalAmount) {
        System.out.println("1---ApiAliAppPayController/(orderNo,totalAmount):"+orderNo+","+totalAmount);
        // 获取项目中实际的订单的信息
        // 此处是相关业务代码
        OrderDTO orderDTO=masterOrderService.getOrder(orderNo);
        if(orderDTO.getStatus().intValue()!= Constants.OrderStatus.NOPAYORDER.getValue()){
            return new Result().error(-1,"非未支付订单，请选择未支付订单支付！");
        }

        // 获取配置文件中支付宝相关信息(可以使用自己的方式获取)
        String aliPayGateway = aliPayProperties.getGatewayUrl();
        String aliPayAppId = aliPayProperties.getAppid();
        String rsaPrivatKey = aliPayProperties.getAppPrivateKey();
        String rsaAlipayPublicKey = aliPayProperties.getAlipayPublicKey();
        String signType = aliPayProperties.getSignType();
        String alipayFormat = aliPayProperties.getFormate();
        String alipayCharset = aliPayProperties.getCharset();

        // 开始使用支付宝SDK中提供的API
        AlipayClient alipayClient = new DefaultAlipayClient(aliPayGateway, aliPayAppId, rsaPrivatKey, alipayFormat, alipayCharset, rsaAlipayPublicKey, signType);
        // 注意：不同接口这里的请求对象是不同的，这个可以查看蚂蚁金服开放平台的API文档查看
        AlipayTradeAppPayRequest alipayRequest = new AlipayTradeAppPayRequest();
        AlipayTradeAppPayModel model = new AlipayTradeAppPayModel();
        model.setBody(body);
        model.setSubject(subject);
        // 唯一订单号 根据项目中实际需要获取相应的
        model.setOutTradeNo(orderNo);
        // 支付超时时间（根据项目需要填写）
        model.setTimeoutExpress("30m");
        // 支付金额（项目中实际订单的需要支付的金额，金额的获取与操作请放在服务端完成，相对安全）
        model.setTotalAmount(totalAmount);
        model.setProductCode("QUICK_MSECURITY_PAY");
        alipayRequest.setBizModel(model);
        // 支付成功后支付宝异步通知的接收地址url
        alipayRequest.setNotifyUrl(aliPayProperties.getNotifyUrl());

        // 注意：每个请求的相应对象不同，与请求对象是对应。
        AlipayTradeAppPayResponse alipayResponse = null;
        try {
            alipayResponse = alipayClient.sdkExecute(alipayRequest);
        } catch (AlipayApiException e) {
            e.printStackTrace();
        }
        // 返回支付相关信息(此处可以直接将getBody中的内容直接返回，无需再做一些其他操作)
        return new Result().ok(alipayResponse.getBody());
    }
}

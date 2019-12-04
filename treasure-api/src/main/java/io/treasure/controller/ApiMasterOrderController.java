package io.treasure.controller;

import io.treasure.annotation.Login;
import io.treasure.annotation.LoginUser;
import io.treasure.config.IWXConfig;
import io.treasure.config.IWXPay;
import io.treasure.dto.*;
import io.treasure.enm.Constants;
import io.treasure.enm.MerchantRoomEnm;
import io.treasure.entity.ClientUserEntity;
import io.treasure.entity.MasterOrderEntity;
import io.treasure.entity.SlaveOrderEntity;
import io.treasure.push.AppInfo;
import io.treasure.push.AppPushUtil;
import io.treasure.service.ClientUserService;
import io.treasure.service.MasterOrderService;


import io.treasure.common.constant.Constant;
import io.treasure.common.page.PageData;

import io.treasure.common.utils.Result;
import io.treasure.common.validator.AssertUtils;
import io.treasure.common.validator.ValidatorUtils;
import io.treasure.common.validator.group.AddGroup;
import io.treasure.common.validator.group.DefaultGroup;
import io.treasure.common.validator.group.UpdateGroup;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;

import io.treasure.service.MerchantRoomParamsSetService;
import io.treasure.utils.OrderUtil;
import oracle.jdbc.driver.Const;
import org.apache.commons.lang3.StringUtils;
import org.apache.ibatis.io.ResolverUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.annotation.Transient;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

import java.math.BigDecimal;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * 订单表
 *
 * @author Super 63600679@qq.com
 * @since 1.0.0 2019-08-23
 */
@RestController
@RequestMapping("/api/masterOrder")
@Api(tags="订单表")
public class ApiMasterOrderController {
    @Autowired
    private MasterOrderService masterOrderService;
    @Autowired
    private MerchantRoomParamsSetService merchantRoomParamsSetService;
    //会员
    @Autowired
    private ClientUserService clientUserService;



    @Login
    @GetMapping("page")
    @ApiOperation("分页")
    @ApiImplicitParams({
        @ApiImplicitParam(name = Constant.PAGE, value = "当前页码，从1开始", paramType = "query", required = true, dataType="int") ,
        @ApiImplicitParam(name = Constant.LIMIT, value = "每页显示记录数", paramType = "query",required = true, dataType="int") ,
        @ApiImplicitParam(name = Constant.ORDER_FIELD, value = "排序字段", paramType = "query", dataType="String") ,
        @ApiImplicitParam(name = Constant.ORDER, value = "排序方式，可选值(asc、desc)", paramType = "query", dataType="String")
    })
    public Result<PageData<MasterOrderDTO>> page(@ApiIgnore @RequestParam Map<String, Object> params){
        PageData<MasterOrderDTO> page = masterOrderService.page(params);

        return new Result<PageData<MasterOrderDTO>>().ok(page);
    }
    @CrossOrigin
    @Login
    @GetMapping("appointmentPage")
    @ApiOperation("商户端-预约列表")
    @ApiImplicitParams({
            @ApiImplicitParam(name = Constant.PAGE, value = "当前页码，从1开始", paramType = "query", required = true, dataType="int") ,
            @ApiImplicitParam(name = Constant.LIMIT, value = "每页显示记录数", paramType = "query",required = true, dataType="int") ,
            @ApiImplicitParam(name = Constant.ORDER_FIELD, value = "排序字段", paramType = "query", dataType="String") ,
            @ApiImplicitParam(name = Constant.ORDER, value = "排序方式，可选值(asc、desc)", paramType = "query", dataType="String"),
            @ApiImplicitParam(name = "merchantId", value = "商户编号", paramType = "query",required=true, dataType="String"),
            @ApiImplicitParam(name = "orderId", value = "订单编号", paramType = "query", dataType="String")
    })
    public Result<PageData<MerchantOrderDTO>> appointmentPage(@ApiIgnore @RequestParam Map<String, Object> params){
        params.put("status", Constants.OrderStatus.PAYORDER.getValue()+"");
        PageData<MerchantOrderDTO> page = masterOrderService.listMerchantPage2(params);
        return new Result<PageData<MerchantOrderDTO>>().ok(page);
    }
    @CrossOrigin
    @Login
    @GetMapping("chargePage")
    @ApiOperation("商户端-已退单列表")
    @ApiImplicitParams({
            @ApiImplicitParam(name = Constant.PAGE, value = "当前页码，从1开始", paramType = "query", required = true, dataType="int") ,
            @ApiImplicitParam(name = Constant.LIMIT, value = "每页显示记录数", paramType = "query",required = true, dataType="int") ,
            @ApiImplicitParam(name = Constant.ORDER_FIELD, value = "排序字段", paramType = "query", dataType="String") ,
            @ApiImplicitParam(name = Constant.ORDER, value = "排序方式，可选值(asc、desc)", paramType = "query", dataType="String"),
            @ApiImplicitParam(name = "merchantId", value = "商户编号", paramType = "query",required=true, dataType="String"),
            @ApiImplicitParam(name = "orderId", value = "订单编号", paramType = "query", dataType="String")
    })
    public Result<PageData<MerchantOrderDTO>> chargePage(@ApiIgnore @RequestParam Map<String, Object> params){
        params.put("status", Constants.OrderStatus.MERCHANTAGREEREFUNDORDER.getValue()+"");
        PageData<MerchantOrderDTO> page = masterOrderService.listMerchantPage(params);
        return new Result<PageData<MerchantOrderDTO>>().ok(page);
    }
    @CrossOrigin
    @Login
    @GetMapping("ongPage")
    @ApiOperation("商户端-进行中列表(已接受订单)")
    @ApiImplicitParams({
            @ApiImplicitParam(name = Constant.PAGE, value = "当前页码，从1开始", paramType = "query", required = true, dataType="int") ,
            @ApiImplicitParam(name = Constant.LIMIT, value = "每页显示记录数", paramType = "query",required = true, dataType="int") ,
            @ApiImplicitParam(name = Constant.ORDER_FIELD, value = "排序字段", paramType = "query", dataType="String") ,
            @ApiImplicitParam(name = Constant.ORDER, value = "排序方式，可选值(asc、desc)", paramType = "query", dataType="String"),
            @ApiImplicitParam(name = "merchantId", value = "商户编号", paramType = "query",required=true, dataType="String"),
            @ApiImplicitParam(name = "orderId", value = "订单编号", paramType = "query", dataType="String")
    })
    public Result<PageData<MerchantOrderDTO>> ongPage(@ApiIgnore @RequestParam Map<String, Object> params){
        params.put("status", Constants.OrderStatus.MERCHANTRECEIPTORDER.getValue()+","+Constants.OrderStatus.MERCHANTAGREEREFUNDORDER.getValue()+","+Constants.OrderStatus.MERCHANTREFUSESREFUNDORDER.getValue());
        PageData<MerchantOrderDTO> page = masterOrderService.listMerchantPages(params);
        return new Result<PageData<MerchantOrderDTO>>().ok(page);
    }
    @CrossOrigin
    @Login
    @GetMapping("finishPage")
    @ApiOperation("商户端-已完成列表(翻台)")
    @ApiImplicitParams({
            @ApiImplicitParam(name = Constant.PAGE, value = "当前页码，从1开始", paramType = "query", required = true, dataType="int") ,
            @ApiImplicitParam(name = Constant.LIMIT, value = "每页显示记录数", paramType = "query",required = true, dataType="int") ,
            @ApiImplicitParam(name = Constant.ORDER_FIELD, value = "排序字段", paramType = "query", dataType="String") ,
            @ApiImplicitParam(name = Constant.ORDER, value = "排序方式，可选值(asc、desc)", paramType = "query", dataType="String"),
            @ApiImplicitParam(name = "merchantId", value = "商户编号", paramType = "query",required=true, dataType="String"),
            @ApiImplicitParam(name = "orderId", value = "订单编号", paramType = "query", dataType="String")
    })
    public Result<PageData<MerchantOrderDTO>> finishPage(@ApiIgnore @RequestParam Map<String, Object> params){
        params.put("status", Constants.OrderStatus.MERCHANTAGFINISHORDER.getValue()+"");
        PageData<MerchantOrderDTO> page = masterOrderService.listMerchantPage(params);
        return new Result<PageData<MerchantOrderDTO>>().ok(page);
    }
    @CrossOrigin
    @Login
    @GetMapping("calcelPage")
    @ApiOperation("商户端-拒绝订单列表")
    @ApiImplicitParams({
            @ApiImplicitParam(name = Constant.PAGE, value = "当前页码，从1开始", paramType = "query", required = true, dataType="int") ,
            @ApiImplicitParam(name = Constant.LIMIT, value = "每页显示记录数", paramType = "query",required = true, dataType="int") ,
            @ApiImplicitParam(name = Constant.ORDER_FIELD, value = "排序字段", paramType = "query", dataType="String") ,
            @ApiImplicitParam(name = Constant.ORDER, value = "排序方式，可选值(asc、desc)", paramType = "query", dataType="String"),
            @ApiImplicitParam(name = "merchantId", value = "商户编号", paramType = "query",required=true, dataType="String"),
            @ApiImplicitParam(name = "orderId", value = "订单编号", paramType = "query", dataType="String")
    })
    public Result<PageData<MerchantOrderDTO>> calcelPage(@ApiIgnore @RequestParam Map<String, Object> params){
        params.put("status", Constants.OrderStatus.MERCHANTREFUSALORDER.getValue()+"");
        PageData<MerchantOrderDTO> page = masterOrderService.listMerchantPage(params);
        return new Result<PageData<MerchantOrderDTO>>().ok(page);
    }
    @CrossOrigin
    @Login
    @GetMapping("applRefundPage")
    @ApiOperation("商户端-申请退款列表")
    @ApiImplicitParams({
            @ApiImplicitParam(name = Constant.PAGE, value = "当前页码，从1开始", paramType = "query", required = true, dataType="int") ,
            @ApiImplicitParam(name = Constant.LIMIT, value = "每页显示记录数", paramType = "query",required = true, dataType="int") ,
            @ApiImplicitParam(name = Constant.ORDER_FIELD, value = "排序字段", paramType = "query", dataType="String") ,
            @ApiImplicitParam(name = Constant.ORDER, value = "排序方式，可选值(asc、desc)", paramType = "query", dataType="String"),
            @ApiImplicitParam(name = "merchantId", value = "商户编号", paramType = "query",required=true, dataType="String"),
            @ApiImplicitParam(name = "orderId", value = "订单编号", paramType = "query", dataType="String")
    })
    public Result<PageData<MerchantOrderDTO>> applRefundPage(@ApiIgnore @RequestParam Map<String, Object> params){
        params.put("status", Constants.OrderStatus.USERAPPLYREFUNDORDER.getValue()+"");
        PageData<MerchantOrderDTO> page = masterOrderService.listMerchantPage(params);
        return new Result<PageData<MerchantOrderDTO>>().ok(page);
    }
    @CrossOrigin
    @Login
    @GetMapping("allPage")
    @ApiOperation("商户端-全部列表")
    @ApiImplicitParams({
            @ApiImplicitParam(name = Constant.PAGE, value = "当前页码，从1开始", paramType = "query", required = true, dataType="int") ,
            @ApiImplicitParam(name = Constant.LIMIT, value = "每页显示记录数", paramType = "query",required = true, dataType="int") ,
            @ApiImplicitParam(name = Constant.ORDER_FIELD, value = "排序字段", paramType = "query", dataType="String") ,
            @ApiImplicitParam(name = Constant.ORDER, value = "排序方式，可选值(asc、desc)", paramType = "query", dataType="String"),
            @ApiImplicitParam(name = "merchantId", value = "商户编号", paramType = "query",required=true, dataType="String"),
            @ApiImplicitParam(name = "orderId", value = "订单编号", paramType = "query", dataType="String")
    })
    public Result<PageData<MerchantOrderDTO>> allPage(@ApiIgnore @RequestParam Map<String, Object> params){
        params.put("status",Constants.OrderStatus.MERCHANTRECEIPTORDER.getValue()+","+Constants.OrderStatus.MERCHANTREFUSALORDER.getValue()+","+
                Constants.OrderStatus.PAYORDER.getValue()+","+Constants.OrderStatus.USERAPPLYREFUNDORDER.getValue()+","+Constants.OrderStatus.MERCHANTREFUSESREFUNDORDER.getValue()+","
                +Constants.OrderStatus.MERCHANTAGREEREFUNDORDER.getValue()+","+Constants.OrderStatus.MERCHANTAGFINISHORDER.getValue());
        PageData page = masterOrderService.listMerchantPage(params);
        return new Result<PageData<MerchantOrderDTO>>().ok(page);
    }

    @Login
    @GetMapping("order/{orderId}")
    @ApiOperation("用户端订单详情")
    public Result<OrderDTO> getOrderInfo(@PathVariable("orderId") String orderId){

        OrderDTO data = masterOrderService.orderParticulars(orderId);
            return new Result<OrderDTO>().ok(data);
    }

    @Login
    @GetMapping("order1/{orderId}")
    @ApiOperation("商户端订单详情")
    public Result<List<OrderDTO>> getOrderInfo1(@PathVariable("orderId") String orderId){

      List<OrderDTO> data = masterOrderService.orderParticulars1(orderId);
        return new Result<List<OrderDTO>>().ok(data);
    }

    @Login
    @PostMapping("generateOrder")
    @ApiOperation("生成订单")
    public Result generateOrder(@RequestBody OrderDTO dto, @LoginUser ClientUserEntity user){
        ValidatorUtils.validateEntity(dto, AddGroup.class, DefaultGroup.class);
        List<SlaveOrderEntity> dtoList=dto.getSlaveOrder();
        return  masterOrderService.orderSave(dto,dtoList,user);
    }
    @Login
    @PostMapping("generatePorder")
    @ApiOperation("加菜")
    public Result generatePorder(@RequestBody OrderDTO dto, @LoginUser ClientUserEntity user){
        ValidatorUtils.validateEntity(dto, AddGroup.class, DefaultGroup.class);
        List<SlaveOrderEntity> dtoList=dto.getSlaveOrder();
        return  masterOrderService.saveOrder(dto,dtoList,user);
}
    @Login
    @GetMapping("allOrderPage")
    @ApiOperation("全部订单列表")
    @ApiImplicitParams({
            @ApiImplicitParam(name = Constant.PAGE, value = "当前页码，从1开始", paramType = "query", required = true, dataType="int") ,
            @ApiImplicitParam(name = Constant.LIMIT, value = "每页显示记录数", paramType = "query",required = true, dataType="int") ,
            @ApiImplicitParam(name = Constant.ORDER_FIELD, value = "排序字段", paramType = "query", dataType="String") ,
            @ApiImplicitParam(name = Constant.ORDER, value = "排序方式，可选值(asc、desc)", paramType = "query", dataType="String"),
            @ApiImplicitParam(name = "userId", value = "用户编码", paramType = "query",required=true, dataType="Long")
    })
    public Result<PageData<OrderDTO>> allOrderPage(@ApiIgnore @RequestParam Map<String, Object> params){
        params.put("pOrderId","0");
        PageData<OrderDTO> page = masterOrderService.getAllMainOrder(params);
        return new Result<PageData<OrderDTO>>().ok(page);
    }

    @Login
    @GetMapping("noPayOrderPage")
    @ApiOperation("未支付订单列表")
    @ApiImplicitParams({
            @ApiImplicitParam(name = Constant.PAGE, value = "当前页码，从1开始", paramType = "query", required = true, dataType="int") ,
            @ApiImplicitParam(name = Constant.LIMIT, value = "每页显示记录数", paramType = "query",required = true, dataType="int") ,
            @ApiImplicitParam(name = Constant.ORDER_FIELD, value = "排序字段", paramType = "query", dataType="String") ,
            @ApiImplicitParam(name = Constant.ORDER, value = "排序方式，可选值(asc、desc)", paramType = "query", dataType="String"),
            @ApiImplicitParam(name = "userId", value = "用户编码", paramType = "query",required=true, dataType="Long")
    })
    public Result<PageData<OrderDTO>> noPayOrderPage(@ApiIgnore @RequestParam Map<String, Object> params){
        params.put("status", Constants.OrderStatus.NOPAYORDER.getValue()+"");
        params.put("pOrderId","0");
        PageData<OrderDTO> page = masterOrderService.listPage(params);
        return new Result<PageData<OrderDTO>>().ok(page);
    }

    @Login
    @GetMapping("receiptOrderPage")
    @ApiOperation("商家已接单列表")
    @ApiImplicitParams({
            @ApiImplicitParam(name = Constant.PAGE, value = "当前页码，从1开始", paramType = "query", required = true, dataType="int") ,
            @ApiImplicitParam(name = Constant.LIMIT, value = "每页显示记录数", paramType = "query",required = true, dataType="int") ,
            @ApiImplicitParam(name = Constant.ORDER_FIELD, value = "排序字段", paramType = "query", dataType="String") ,
            @ApiImplicitParam(name = Constant.ORDER, value = "排序方式，可选值(asc、desc)", paramType = "query", dataType="String"),
            @ApiImplicitParam(name = "userId", value = "用户编码", paramType = "query",required=true, dataType="Long")
    })
    public Result<PageData<OrderDTO>> receiptOrderPage(@ApiIgnore @RequestParam Map<String, Object> params){
        params.put("status", Constants.OrderStatus.MERCHANTRECEIPTORDER.getValue()+"");
        params.put("pOrderId","0");
        PageData<OrderDTO> page = masterOrderService.listPage(params);
        return new Result<PageData<OrderDTO>>().ok(page);
    }

    @Login
    @GetMapping("refusalOrderPage")
    @ApiOperation("商户拒接单列表")
    @ApiImplicitParams({
            @ApiImplicitParam(name = Constant.PAGE, value = "当前页码，从1开始", paramType = "query", required = true, dataType="int") ,
            @ApiImplicitParam(name = Constant.LIMIT, value = "每页显示记录数", paramType = "query",required = true, dataType="int") ,
            @ApiImplicitParam(name = Constant.ORDER_FIELD, value = "排序字段", paramType = "query", dataType="String") ,
            @ApiImplicitParam(name = Constant.ORDER, value = "排序方式，可选值(asc、desc)", paramType = "query", dataType="String"),
            @ApiImplicitParam(name = "userId", value = "用户编码", paramType = "query",required=true, dataType="Long")
    })
    public Result<PageData<OrderDTO>> refusalOrderPage(@ApiIgnore @RequestParam Map<String, Object> params){
        params.put("status", Constants.OrderStatus.MERCHANTREFUSALORDER.getValue()+"");
        params.put("pOrderId","0");
        PageData<OrderDTO> page = masterOrderService.listPage(params);
        return new Result<PageData<OrderDTO>>().ok(page);
    }

    @Login
    @GetMapping("payFinishOrderPage")
    @ApiOperation("支付完成订单列表")
    @ApiImplicitParams({
            @ApiImplicitParam(name = Constant.PAGE, value = "当前页码，从1开始", paramType = "query", required = true, dataType="int") ,
            @ApiImplicitParam(name = Constant.LIMIT, value = "每页显示记录数", paramType = "query",required = true, dataType="int") ,
            @ApiImplicitParam(name = Constant.ORDER_FIELD, value = "排序字段", paramType = "query", dataType="String") ,
            @ApiImplicitParam(name = Constant.ORDER, value = "排序方式，可选值(asc、desc)", paramType = "query", dataType="String"),
            @ApiImplicitParam(name = "userId", value = "用户编码", paramType = "query",required=true, dataType="Long")
    })
    public Result<PageData<OrderDTO>> payFinishOrderPage(@ApiIgnore @RequestParam Map<String, Object> params){
        params.put("status", Constants.OrderStatus.PAYORDER.getValue()+"");
        params.put("pOrderId","0");
        PageData<OrderDTO> page = masterOrderService.selectPOrderIdHavePaids(params);
        return new Result<PageData<OrderDTO>>().ok(page);
    }

    @Login
    @GetMapping("cancelNopayOrderPage")
    @ApiOperation("取消未支付订单列表")
    @ApiImplicitParams({
            @ApiImplicitParam(name = Constant.PAGE, value = "当前页码，从1开始", paramType = "query", required = true, dataType="int") ,
            @ApiImplicitParam(name = Constant.LIMIT, value = "每页显示记录数", paramType = "query",required = true, dataType="int") ,
            @ApiImplicitParam(name = Constant.ORDER_FIELD, value = "排序字段", paramType = "query", dataType="String") ,
            @ApiImplicitParam(name = Constant.ORDER, value = "排序方式，可选值(asc、desc)", paramType = "query", dataType="String"),
            @ApiImplicitParam(name = "userId", value = "用户编码", paramType = "query",required=true, dataType="Long")
    })
    public Result<PageData<OrderDTO>> cancelNopayOrderPage(@ApiIgnore @RequestParam Map<String, Object> params){
        params.put("status", Constants.OrderStatus.CANCELNOPAYORDER.getValue()+"");
        params.put("pOrderId","0");
        PageData<OrderDTO> page = masterOrderService.listPage(params);
        return new Result<PageData<OrderDTO>>().ok(page);
    }

    @Login
    @GetMapping("userApplyRefundOrderPage")
    @ApiOperation("消费者申请退款订单列表")
    @ApiImplicitParams({
            @ApiImplicitParam(name = Constant.PAGE, value = "当前页码，从1开始", paramType = "query", required = true, dataType="int") ,
            @ApiImplicitParam(name = Constant.LIMIT, value = "每页显示记录数", paramType = "query",required = true, dataType="int") ,
            @ApiImplicitParam(name = Constant.ORDER_FIELD, value = "排序字段", paramType = "query", dataType="String") ,
            @ApiImplicitParam(name = Constant.ORDER, value = "排序方式，可选值(asc、desc)", paramType = "query", dataType="String"),
            @ApiImplicitParam(name = "userId", value = "用户编码", paramType = "query",required=true, dataType="Long")
    })
    public Result<PageData<OrderDTO>> userApplyRefundOrderPage(@ApiIgnore @RequestParam Map<String, Object> params){
        params.put("status", Constants.OrderStatus.USERAPPLYREFUNDORDER.getValue()+"");
        params.put("pOrderId","0");
        PageData<OrderDTO> page = masterOrderService.listPage(params);

        return new Result<PageData<OrderDTO>>().ok(page);
    }

    @Login
    @GetMapping("refusesRefundOrderPage")
    @ApiOperation("商户拒绝退款订单列表")
    @ApiImplicitParams({
            @ApiImplicitParam(name = Constant.PAGE, value = "当前页码，从1开始", paramType = "query", required = true, dataType="int") ,
            @ApiImplicitParam(name = Constant.LIMIT, value = "每页显示记录数", paramType = "query",required = true, dataType="int") ,
            @ApiImplicitParam(name = Constant.ORDER_FIELD, value = "排序字段", paramType = "query", dataType="String") ,
            @ApiImplicitParam(name = Constant.ORDER, value = "排序方式，可选值(asc、desc)", paramType = "query", dataType="String"),
            @ApiImplicitParam(name = "userId", value = "用户编码", paramType = "query",required=true, dataType="Long")
    })
    public Result<PageData<OrderDTO>> refusesRefundOrderPage(@ApiIgnore @RequestParam Map<String, Object> params){
        params.put("status", Constants.OrderStatus.MERCHANTREFUSESREFUNDORDER.getValue()+"");
        params.put("pOrderId","0");
        PageData<OrderDTO> page = masterOrderService.listPage(params);
        return new Result<PageData<OrderDTO>>().ok(page);
    }

    @Login
    @GetMapping("agreeRefundOrderPage")
    @ApiOperation("商家同意退款订单列表")
    @ApiImplicitParams({
            @ApiImplicitParam(name = Constant.PAGE, value = "当前页码，从1开始", paramType = "query", required = true, dataType="int") ,
            @ApiImplicitParam(name = Constant.LIMIT, value = "每页显示记录数", paramType = "query",required = true, dataType="int") ,
            @ApiImplicitParam(name = Constant.ORDER_FIELD, value = "排序字段", paramType = "query", dataType="String") ,
            @ApiImplicitParam(name = Constant.ORDER, value = "排序方式，可选值(asc、desc)", paramType = "query", dataType="String"),
            @ApiImplicitParam(name = "userId", value = "用户编码", paramType = "query",required=true, dataType="Long")
    })
    public Result<PageData<OrderDTO>> agreeRefundOrderPage(@ApiIgnore @RequestParam Map<String, Object> params){
        params.put("status", Constants.OrderStatus.MERCHANTAGREEREFUNDORDER.getValue()+"");
        params.put("pOrderId","0");
        PageData<OrderDTO> page = masterOrderService.selectAgreeRefundOrders(params);
        return new Result<PageData<OrderDTO>>().ok(page);
    }

    @Login
    @GetMapping("deleteOrderPage")
    @ApiOperation("删除订单列表")
    @ApiImplicitParams({
            @ApiImplicitParam(name = Constant.PAGE, value = "当前页码，从1开始", paramType = "query", required = true, dataType="int") ,
            @ApiImplicitParam(name = Constant.LIMIT, value = "每页显示记录数", paramType = "query",required = true, dataType="int") ,
            @ApiImplicitParam(name = Constant.ORDER_FIELD, value = "排序字段", paramType = "query", dataType="String") ,
            @ApiImplicitParam(name = Constant.ORDER, value = "排序方式，可选值(asc、desc)", paramType = "query", dataType="String"),
            @ApiImplicitParam(name = "userId", value = "用户编码", paramType = "query",required=true, dataType="Long")
    })
    public Result<PageData<OrderDTO>> deleteOrderPage(@ApiIgnore @RequestParam Map<String, Object> params){
        params.put("status", Constants.OrderStatus.DELETEORDER.getValue()+"");
        params.put("pOrderId","0");
        PageData<OrderDTO> page = masterOrderService.listPage(params);
        return new Result<PageData<OrderDTO>>().ok(page);
    }

    @Login
    @PutMapping("orderCancel")
    @ApiOperation("未支付订单取消")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "refundReason", value = "取消订单原因", paramType = "query", dataType="String"),
            @ApiImplicitParam(name = "id", value = "主订单ID", paramType = "query",required=true, dataType="Long")
    })
    public Result orderCancel(@ApiIgnore @RequestParam Map<String, Object> params) throws Exception {
        Long id = Long.valueOf(params.get("id").toString());
        return masterOrderService.cancelOrder(id);
    }

    @Login
    @PutMapping("userCheck")
    @ApiOperation("用户结账")
    public Result userCheck(Long id){
        return  masterOrderService.updateByCheck(id);
    }

    @Login
    @PutMapping("userApplyRefund")
    @ApiOperation("消费者申请退款")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "refundReason", value = "取消订单原因", paramType = "query", dataType="String"),
            @ApiImplicitParam(name = "id", value = "主订单ID", paramType = "query",required=true, dataType="Long")
    })
    public Result userApplyRefund(@ApiIgnore @RequestParam Map<String, Object> params){
        return  masterOrderService.updateByApplyRefund(params);
    }

    @Login
    @GetMapping("{id}")
    @ApiOperation("信息")
    public Result<MasterOrderDTO> get(@PathVariable("id") Long id){
        MasterOrderDTO data = masterOrderService.get(id);

        return new Result<MasterOrderDTO>().ok(data);
    }

    @Login
    @PostMapping
    @ApiOperation("保存")
    public Result save(@RequestBody MasterOrderDTO dto){
        //效验数据
        ValidatorUtils.validateEntity(dto, AddGroup.class, DefaultGroup.class);
        masterOrderService.save(dto);
        return new Result();
    }
    @Login
    @PutMapping
    @ApiOperation("修改")
    public Result update(@RequestBody MasterOrderDTO dto){
        //效验数据
        ValidatorUtils.validateEntity(dto, UpdateGroup.class, DefaultGroup.class);
        masterOrderService.update(dto);
        return new Result();
    }
    @Login
    @DeleteMapping
    @ApiOperation("删除")
    public Result delete(@RequestBody Long[] ids){
        //效验数据
        AssertUtils.isArrayEmpty(ids, "id");
        masterOrderService.delete(ids);
        return new Result();
    }

    @CrossOrigin
    @Login
    @PutMapping("refuseUpdate")
    @ApiOperation("商户端-取消/拒绝订单(删除)")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "编号", paramType = "query", required = true, dataType="long"),
            @ApiImplicitParam(name = "verify", value = "取消人", paramType = "query", required = true, dataType="long"),
            @ApiImplicitParam(name="verify_reason",value="取消原因",paramType = "query",required = true,dataType = "String")
    })
    public Result refuseUpdate(@RequestParam  long id,@RequestParam  long verify, @RequestParam  String verify_reason) throws Exception {
        return masterOrderService.caleclUpdate(id,Constants.OrderStatus.MERCHANTREFUSALORDER.getValue(),verify,new Date(),verify_reason);
    }
    @CrossOrigin
    @Login
    @PutMapping("cancelUpdate")
    @ApiOperation("商户端-取消/拒绝订单")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "编号", paramType = "query", required = true, dataType="long"),
            @ApiImplicitParam(name = "verify", value = "取消人", paramType = "query", required = true, dataType="long"),
            @ApiImplicitParam(name="verify_reason",value="取消原因",paramType = "query",required = true,dataType = "String")
    })
    public Result calcelUpdate(@RequestParam  long id,@RequestParam(value = "verify",defaultValue = "")  long verify, @RequestParam(value = "verify_reason",defaultValue = "")  String verify_reason) throws Exception {
       return masterOrderService.caleclUpdate(id,verify,new Date(),verify_reason);
    }

    @CrossOrigin
    @Login
    @PutMapping("refuseOrder")
    @ApiOperation("商户端-拒绝订单")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "编号", paramType = "query", required = true, dataType="long"),
            @ApiImplicitParam(name = "verify", value = "取消人", paramType = "query", required = true, dataType="long"),
            @ApiImplicitParam(name="verify_reason",value="取消原因",paramType = "query",required = true,dataType = "String")
    })
    public Result refuseOrder(@RequestParam  long id,@RequestParam(value = "verify",defaultValue = "")  long verify, @RequestParam(value = "verify_reason",defaultValue = "")  String verify_reason) throws Exception {
        return masterOrderService.refuseOrder(id,verify,new Date(),verify_reason);
    }

    @CrossOrigin
    @Login
    @PutMapping("acceptUpdate")
    @ApiOperation("商户端-接受订单")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "编号", paramType = "query", required = true, dataType="long"),
            @ApiImplicitParam(name = "verify", value = "接受人", paramType = "query", required = true, dataType="long")
    })
    public Result acceptUpdate(@RequestParam  long id,@RequestParam   long verify) throws Exception {
       return  masterOrderService.acceptUpdate(id,Constants.OrderStatus.MERCHANTRECEIPTORDER.getValue(),verify,new Date(),"接受订单");
    }
    @CrossOrigin
    @Login
    @Transient
    @PutMapping("finishUpdate")
    @ApiOperation("商户端-完成订单(翻台)")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "编号", paramType = "query", required = true, dataType="long"),
            @ApiImplicitParam(name = "verify", value = "操作人", paramType = "query", required = true, dataType="long")
    })
    public Result finishUpdate(@RequestParam  long id,@RequestParam  long verify) throws Exception {
        return masterOrderService.finishUpdate(id,Constants.OrderStatus.MERCHANTAGFINISHORDER.getValue(),verify,new Date(),"完成订单");
    }
    @CrossOrigin
    @Login
    @Transient
    @PutMapping("refundYesUpdate")
    @ApiOperation("商户端-同意退款")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "编号", paramType = "query", required = true, dataType="long"),
            @ApiImplicitParam(name = "verify", value = "审核人", paramType = "query", required = true, dataType="long")
    })
    public Result refundYesUpdate(@RequestParam  long id,@RequestParam  long verify) throws Exception {
        return masterOrderService.refundYesUpdate(id, Constants.OrderStatus.MERCHANTAGREEREFUNDORDER.getValue(), verify, new Date(), "同意退款");

    }
    @CrossOrigin
    @Login
    @PutMapping("refundNoUpdate")
    @ApiOperation("商户端-拒绝退款")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "编号", paramType = "query", required = true, dataType="long"),
            @ApiImplicitParam(name = "verify", value = "拒绝人", paramType = "query", required = true, dataType="long"),
            @ApiImplicitParam(name="verify_reason",value="拒绝原因",paramType = "query",required = true,dataType = "String")
    })
    public Result refundNoUpdate(@RequestParam long id,@RequestParam long verify,@RequestParam String verify_reason) throws Exception {
       return masterOrderService.refundNoUpdate(id,Constants.OrderStatus.MERCHANTREFUSESREFUNDORDER.getValue(),verify,new Date(),verify_reason);
    }

    @Login
    @PostMapping("calculateGift")
    @ApiOperation("客户端-使用赠送金")
    public Result<DesignConditionsDTO> calculateGift(@RequestBody DesignConditionsDTO dct){
        return new Result<DesignConditionsDTO>().ok(masterOrderService.calculateGift(dct));
    }

    @Login
    @PostMapping("calculateCoupon")
    @ApiOperation("客户端-使用优惠卷")
    public Result<DesignConditionsDTO> calculateCoupon(@RequestBody DesignConditionsDTO dct){
        return new Result<DesignConditionsDTO>().ok(masterOrderService.calculateCoupon(dct));
    }

    @Login
    @PostMapping("notDiscounts")
    @ApiOperation("客户端-无任何优惠")
    public Result<DesignConditionsDTO> notDiscounts(@RequestBody DesignConditionsDTO dct){
        return new Result<DesignConditionsDTO>().ok(masterOrderService.notDiscounts(dct));
    }

    @Login
    @PostMapping("calculateGiftCoupon")
    @ApiOperation("客户端-使用优惠卷与赠送金")
    public Result<DesignConditionsDTO> calculateGiftCoupon(@RequestBody DesignConditionsDTO dct){
        return new Result<DesignConditionsDTO>().ok(masterOrderService.calculateGiftCoupon(dct));
    }
    @CrossOrigin
    @Login
    @PutMapping("setRoom")
    @ApiOperation("商户端-设置包房")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "订单编号", paramType = "query", required = true, dataType="long"),
            @ApiImplicitParam(name = "roomSetId", value = "预约包房编号", paramType = "query", required = true, dataType="long")
    })
    public Result setRoom(@RequestParam  long id, @RequestParam long roomSetId) throws Exception {
        return masterOrderService.setRoom(id,roomSetId);
    }
    @Login
    @PostMapping("reserveRoom")
    @ApiOperation("客户端-下单支付成功后，预定包房")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "mainOrderId", value = "编号", paramType = "query", required = true, dataType="String")
    })
    public Result reserveRoom(@RequestBody OrderDTO dto, @LoginUser ClientUserEntity user,String mainOrderId){
        ValidatorUtils.validateEntity(dto, AddGroup.class, DefaultGroup.class);
        return  masterOrderService.reserveRoom(dto,user,mainOrderId);
    }

    @Login
    @PostMapping("orderFoodByRoom")
    @ApiOperation("预订包房后，再订餐")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "mainOrderId", value = "预订包房订单号", paramType = "query", required = true, dataType="String")
    })
    public Result orderFoodByRoom(@RequestBody OrderDTO dto, @LoginUser ClientUserEntity user,String mainOrderId){
        ValidatorUtils.validateEntity(dto, AddGroup.class, DefaultGroup.class);
        List<SlaveOrderEntity> dtoList=dto.getSlaveOrder();
        return  masterOrderService.orderFoodByRoom(dto,dtoList,user,mainOrderId);
    }
    @Login
    @GetMapping("getAuxiliaryOrder")
    @ApiOperation("获取关联订单")
    @ApiImplicitParams({
            @ApiImplicitParam(name = Constant.PAGE, value = "当前页码，从1开始", paramType = "query", required = true, dataType="int") ,
            @ApiImplicitParam(name = Constant.LIMIT, value = "每页显示记录数", paramType = "query",required = true, dataType="int") ,
            @ApiImplicitParam(name = Constant.ORDER_FIELD, value = "排序字段", paramType = "query", dataType="String") ,
            @ApiImplicitParam(name = Constant.ORDER, value = "排序方式，可选值(asc、desc)", paramType = "query", dataType="String"),
            @ApiImplicitParam(name = "orderId", value = "订单ID", paramType = "query",required=true, dataType="String")
    })
    public Result<PageData<OrderDTO>> getAuxiliaryOrder(@ApiIgnore @RequestParam Map<String, Object> params){
        PageData<OrderDTO> page = masterOrderService.pageGetAuxiliaryOrder(params);
        return new Result<PageData<OrderDTO>>().ok(page);
    }
    @CrossOrigin
    @Login
    @GetMapping("getStatus4Order")
    @ApiOperation("语音推送接口")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "merchantId", value = "编号", paramType = "query", required = true, dataType="String")
    })
    public Result getStatus4Order(@ApiIgnore @RequestParam Map<String, Object> params){
        List<MasterOrderEntity> list = masterOrderService.getStatus4Order(params);
        return new Result().ok(list.size());
    }


    @Login
    @GetMapping("getRefundGoods")
    @ApiOperation("商户端退款订单详情")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "orderId", value = "主订单编号", paramType = "query", required = true, dataType="string"),
            @ApiImplicitParam(name = "goodId", value = "退菜菜品id", paramType = "query", required = true, dataType="Long")
    })
    public Result<List<OrderDTO>> getRefundGoods (@ApiIgnore @RequestParam Map<String, Object> params){
        List<OrderDTO> data = masterOrderService.refundOrder(params);
        return new Result<List<OrderDTO>>().ok(data);
    }
}
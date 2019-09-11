package io.treasure.controller;

import io.treasure.annotation.Login;
import io.treasure.annotation.LoginUser;
import io.treasure.dto.MasterOrderDTO;
import io.treasure.dto.MerchantOrderDTO;
import io.treasure.dto.OrderDTO;
import io.treasure.enm.Constants;
import io.treasure.enm.MerchantRoomEnm;
import io.treasure.entity.ClientUserEntity;
import io.treasure.entity.SlaveOrderEntity;
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
import oracle.jdbc.driver.Const;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.annotation.Transient;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

import java.util.Date;
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
            @ApiImplicitParam(name = "merchantId", value = "商户编号", paramType = "query",required=true, dataType="Long")
    })
    public Result<PageData<MerchantOrderDTO>> appointmentPage(@ApiIgnore @RequestParam Map<String, Object> params){
        params.put("status", Constants.OrderStatus.PAYORDER.getValue()+"");
        PageData<MerchantOrderDTO> page = masterOrderService.listMerchantPage(params);
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
            @ApiImplicitParam(name = "merchantId", value = "商户编号", paramType = "query",required=true, dataType="Long")
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
            @ApiImplicitParam(name = "merchantId", value = "商户编号", paramType = "query",required=true, dataType="Long")
    })
    public Result<PageData<MerchantOrderDTO>> ongPage(@ApiIgnore @RequestParam Map<String, Object> params){
        params.put("status", Constants.OrderStatus.MERCHANTRECEIPTORDER.getValue()+"");
        PageData<MerchantOrderDTO> page = masterOrderService.listMerchantPage(params);
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
            @ApiImplicitParam(name = "merchantId", value = "商户编号", paramType = "query",required=true, dataType="Long")
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
            @ApiImplicitParam(name = "merchantId", value = "商户编号", paramType = "query",required=true, dataType="Long")
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
            @ApiImplicitParam(name = "merchantId", value = "商户编号", paramType = "query",required=true, dataType="Long")
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
            @ApiImplicitParam(name = "merchantId", value = "商户编号", paramType = "query",required=true, dataType="Long")
    })
    public Result<PageData<MerchantOrderDTO>> allPage(@ApiIgnore @RequestParam Map<String, Object> params){
        PageData page = masterOrderService.listMerchantPage(params);
        return new Result<PageData<MerchantOrderDTO>>().ok(page);
    }

    @Login
    @GetMapping("order/{orderId}")
    @ApiOperation("订单详情")
    public Result<OrderDTO> getOrderInfo(@PathVariable("orderId") String orderId){
            OrderDTO data = masterOrderService.getOrder(orderId);
            return new Result<OrderDTO>().ok(data);
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
    @GetMapping("allOrderPage")
    @ApiOperation("全部订单列表")
    @ApiImplicitParams({
            @ApiImplicitParam(name = Constant.PAGE, value = "当前页码，从1开始", paramType = "query", required = true, dataType="int") ,
            @ApiImplicitParam(name = Constant.LIMIT, value = "每页显示记录数", paramType = "query",required = true, dataType="int") ,
            @ApiImplicitParam(name = Constant.ORDER_FIELD, value = "排序字段", paramType = "query", dataType="String") ,
            @ApiImplicitParam(name = Constant.ORDER, value = "排序方式，可选值(asc、desc)", paramType = "query", dataType="String"),
            @ApiImplicitParam(name = "userId", value = "用户编码", paramType = "query",required=true, dataType="Long")
    })
    public Result<PageData<MasterOrderDTO>> allOrderPage(@ApiIgnore @RequestParam Map<String, Object> params){
        PageData<MasterOrderDTO> page = masterOrderService.listPage(params);
        return new Result<PageData<MasterOrderDTO>>().ok(page);
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
    public Result<PageData<MasterOrderDTO>> noPayOrderPage(@ApiIgnore @RequestParam Map<String, Object> params){
        params.put("status", Constants.OrderStatus.NOPAYORDER.getValue()+"");
        PageData<MasterOrderDTO> page = masterOrderService.listPage(params);
        return new Result<PageData<MasterOrderDTO>>().ok(page);
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
    public Result<PageData<MasterOrderDTO>> receiptOrderPage(@ApiIgnore @RequestParam Map<String, Object> params){
        params.put("status", Constants.OrderStatus.MERCHANTRECEIPTORDER.getValue()+"");
        PageData<MasterOrderDTO> page = masterOrderService.listPage(params);
        return new Result<PageData<MasterOrderDTO>>().ok(page);
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
    public Result<PageData<MasterOrderDTO>> refusalOrderPage(@ApiIgnore @RequestParam Map<String, Object> params){
        params.put("status", Constants.OrderStatus.MERCHANTREFUSALORDER.getValue()+"");
        PageData<MasterOrderDTO> page = masterOrderService.listPage(params);
        return new Result<PageData<MasterOrderDTO>>().ok(page);
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
    public Result<PageData<MasterOrderDTO>> payFinishOrderPage(@ApiIgnore @RequestParam Map<String, Object> params){
        params.put("status", Constants.OrderStatus.PAYORDER.getValue()+"");
        PageData<MasterOrderDTO> page = masterOrderService.listPage(params);
        return new Result<PageData<MasterOrderDTO>>().ok(page);
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
    public Result<PageData<MasterOrderDTO>> cancelNopayOrderPage(@ApiIgnore @RequestParam Map<String, Object> params){
        params.put("status", Constants.OrderStatus.CANCELNOPAYORDER.getValue()+"");
        PageData<MasterOrderDTO> page = masterOrderService.listPage(params);
        return new Result<PageData<MasterOrderDTO>>().ok(page);
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
    public Result<PageData<MasterOrderDTO>> userApplyRefundOrderPage(@ApiIgnore @RequestParam Map<String, Object> params){
        params.put("status", Constants.OrderStatus.USERAPPLYREFUNDORDER.getValue()+"");
        PageData<MasterOrderDTO> page = masterOrderService.listPage(params);
        return new Result<PageData<MasterOrderDTO>>().ok(page);
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
    public Result<PageData<MasterOrderDTO>> refusesRefundOrderPage(@ApiIgnore @RequestParam Map<String, Object> params){
        params.put("status", Constants.OrderStatus.MERCHANTREFUSESREFUNDORDER.getValue()+"");
        PageData<MasterOrderDTO> page = masterOrderService.listPage(params);
        return new Result<PageData<MasterOrderDTO>>().ok(page);
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
    public Result<PageData<MasterOrderDTO>> agreeRefundOrderPage(@ApiIgnore @RequestParam Map<String, Object> params){
        params.put("status", Constants.OrderStatus.MERCHANTAGREEREFUNDORDER.getValue()+"");
        PageData<MasterOrderDTO> page = masterOrderService.listPage(params);
        return new Result<PageData<MasterOrderDTO>>().ok(page);
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
    public Result<PageData<MasterOrderDTO>> deleteOrderPage(@ApiIgnore @RequestParam Map<String, Object> params){
        params.put("status", Constants.OrderStatus.DELETEORDER.getValue()+"");
        PageData<MasterOrderDTO> page = masterOrderService.listPage(params);
        return new Result<PageData<MasterOrderDTO>>().ok(page);
    }

    @Login
    @PutMapping("orderCancel")
    @ApiOperation("未支付订单取消")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "refundReason", value = "取消订单原因", paramType = "query", dataType="String"),
            @ApiImplicitParam(name = "id", value = "主订单ID", paramType = "query",required=true, dataType="Long")
    })
    public Result orderCancel(@ApiIgnore @RequestParam Map<String, Object> params){
        return  masterOrderService.updateByCancel(params);
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
    @Transient
    @PutMapping("cancelUpdate")
    @ApiOperation("商户端-取消/拒绝订单")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "编号", paramType = "query", required = true, dataType="long"),
            @ApiImplicitParam(name = "verify", value = "取消人", paramType = "query", required = true, dataType="long"),
            @ApiImplicitParam(name="verify_reason",value="取消原因",paramType = "query",required = true,dataType = "String")
    })
    public Result calcelUpdate(@RequestParam  long id,@RequestParam  long verify, @RequestParam  String verify_reason){
        masterOrderService.updateStatusAndReason(id,Constants.OrderStatus.MERCHANTREFUSALORDER.getValue(),verify,new Date(),verify_reason);
        MasterOrderDTO dto = masterOrderService.get(id);
        //同时将包房或者桌设置成未使用状态
        merchantRoomParamsSetService.updateStatus(dto.getRoomId(), MerchantRoomEnm.STATE_USE_NO.getType());
        return new Result();
    }
    @CrossOrigin
    @Login
    @PutMapping("acceptUpdate")
    @ApiOperation("商户端-接受订单")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "编号", paramType = "query", required = true, dataType="long"),
            @ApiImplicitParam(name = "verify", value = "接受人", paramType = "query", required = true, dataType="long")
    })
    public Result acceptUpdate(@RequestParam  long id,@RequestParam   long verify){
        masterOrderService.updateStatusAndReason(id,Constants.OrderStatus.MERCHANTRECEIPTORDER.getValue(),verify,new Date(),"接受订单");
        return new Result();
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
    public Result finishUpdate(@RequestParam  long id,@RequestParam  long verify){
        masterOrderService.updateStatusAndReason(id,Constants.OrderStatus.MERCHANTAGFINISHORDER.getValue(),verify,new Date(),"完成订单");
        MasterOrderDTO dto = masterOrderService.get(id);
        //同时将包房或者桌设置成未使用状态
        merchantRoomParamsSetService.updateStatus(dto.getReservationId(), MerchantRoomEnm.STATE_USE_NO.getType());
        return new Result();
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
    public Result refundYesUpdate(@RequestParam  long id,@RequestParam  long verify){
        MasterOrderDTO dto = masterOrderService.get(id);
        masterOrderService.updateStatusAndReason(id,Constants.OrderStatus.MERCHANTAGREEREFUNDORDER.getValue(),verify,new Date(),"同意退款");
        //同时将包房或者桌设置成未使用状态
        merchantRoomParamsSetService.updateStatus(dto.getReservationId(), MerchantRoomEnm.STATE_USE_NO.getType());
        return new Result();
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
    public Result refundNoUpdate(@RequestParam long id,@RequestParam long verify,@RequestParam String verify_reason){
        masterOrderService.updateStatusAndReason(id,Constants.OrderStatus.MERCHANTREFUSESREFUNDORDER.getValue(),verify,new Date(),verify_reason);
        return new Result();
    }
    
}
package io.treasure.controller;

import io.treasure.annotation.Login;
import io.treasure.annotation.LoginUser;
import io.treasure.dto.MasterOrderDTO;
import io.treasure.dto.OrderDTO;
import io.treasure.dto.SlaveOrderDTO;
import io.treasure.enm.MerchantRoomEnm;
import io.treasure.enm.Order;
import io.treasure.entity.ClientUserEntity;
import io.treasure.entity.UserEntity;
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
import io.treasure.service.SlaveOrderService;
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
    @Login
    @GetMapping("appointmentPage")
    @ApiOperation("预约列表")
    @ApiImplicitParams({
            @ApiImplicitParam(name = Constant.PAGE, value = "当前页码，从1开始", paramType = "query", required = true, dataType="int") ,
            @ApiImplicitParam(name = Constant.LIMIT, value = "每页显示记录数", paramType = "query",required = true, dataType="int") ,
            @ApiImplicitParam(name = Constant.ORDER_FIELD, value = "排序字段", paramType = "query", dataType="String") ,
            @ApiImplicitParam(name = Constant.ORDER, value = "排序方式，可选值(asc、desc)", paramType = "query", dataType="String"),
            @ApiImplicitParam(name = "merchantId", value = "商户编号", paramType = "query",required=true, dataType="Long")
    })
    public Result<PageData<MasterOrderDTO>> appointmentPage(@ApiIgnore @RequestParam Map<String, Object> params){
        params.put("status", Order.PAY_STATUS_8+"");
        PageData<MasterOrderDTO> page = masterOrderService.page(params);
        return new Result<PageData<MasterOrderDTO>>().ok(page);
    }
    @Login
    @GetMapping("chargePage")
    @ApiOperation("已退单列表")
    @ApiImplicitParams({
            @ApiImplicitParam(name = Constant.PAGE, value = "当前页码，从1开始", paramType = "query", required = true, dataType="int") ,
            @ApiImplicitParam(name = Constant.LIMIT, value = "每页显示记录数", paramType = "query",required = true, dataType="int") ,
            @ApiImplicitParam(name = Constant.ORDER_FIELD, value = "排序字段", paramType = "query", dataType="String") ,
            @ApiImplicitParam(name = Constant.ORDER, value = "排序方式，可选值(asc、desc)", paramType = "query", dataType="String"),
            @ApiImplicitParam(name = "merchantId", value = "商户编号", paramType = "query",required=true, dataType="Long")
    })
    public Result<PageData<MasterOrderDTO>> chargePage(@ApiIgnore @RequestParam Map<String, Object> params){
        params.put("status", Order.PAY_STTAUS_7);
        PageData<MasterOrderDTO> page = masterOrderService.page(params);
        return new Result<PageData<MasterOrderDTO>>().ok(page);
    }
    @Login
    @GetMapping("ongPage")
    @ApiOperation("进行中列表")
    @ApiImplicitParams({
            @ApiImplicitParam(name = Constant.PAGE, value = "当前页码，从1开始", paramType = "query", required = true, dataType="int") ,
            @ApiImplicitParam(name = Constant.LIMIT, value = "每页显示记录数", paramType = "query",required = true, dataType="int") ,
            @ApiImplicitParam(name = Constant.ORDER_FIELD, value = "排序字段", paramType = "query", dataType="String") ,
            @ApiImplicitParam(name = Constant.ORDER, value = "排序方式，可选值(asc、desc)", paramType = "query", dataType="String"),
            @ApiImplicitParam(name = "merchantId", value = "商户编号", paramType = "query",required=true, dataType="Long")
    })
    public Result<PageData<MasterOrderDTO>> ongPage(@ApiIgnore @RequestParam Map<String, Object> params){
        params.put("status", Order.PAY_STTAUS_2.getStatus()+","+Order.PAY_STTAUS_3+","+Order.PAY_STTAUS_6);
        PageData<MasterOrderDTO> page = masterOrderService.page(params);
        return new Result<PageData<MasterOrderDTO>>().ok(page);
    }
    @Login
    @GetMapping("finishPage")
    @ApiOperation("已完成列表")
    @ApiImplicitParams({
            @ApiImplicitParam(name = Constant.PAGE, value = "当前页码，从1开始", paramType = "query", required = true, dataType="int") ,
            @ApiImplicitParam(name = Constant.LIMIT, value = "每页显示记录数", paramType = "query",required = true, dataType="int") ,
            @ApiImplicitParam(name = Constant.ORDER_FIELD, value = "排序字段", paramType = "query", dataType="String") ,
            @ApiImplicitParam(name = Constant.ORDER, value = "排序方式，可选值(asc、desc)", paramType = "query", dataType="String"),
            @ApiImplicitParam(name = "merchantId", value = "商户编号", paramType = "query",required=true, dataType="Long")
    })
    public Result<PageData<MasterOrderDTO>> finishPage(@ApiIgnore @RequestParam Map<String, Object> params){
        params.put("status", Order.PAY_STTAUS_4.getStatus()+"");
        PageData<MasterOrderDTO> page = masterOrderService.page(params);
        return new Result<PageData<MasterOrderDTO>>().ok(page);
    }
    @Login
    @GetMapping("calcelPage")
    @ApiOperation("已取消列表")
    @ApiImplicitParams({
            @ApiImplicitParam(name = Constant.PAGE, value = "当前页码，从1开始", paramType = "query", required = true, dataType="int") ,
            @ApiImplicitParam(name = Constant.LIMIT, value = "每页显示记录数", paramType = "query",required = true, dataType="int") ,
            @ApiImplicitParam(name = Constant.ORDER_FIELD, value = "排序字段", paramType = "query", dataType="String") ,
            @ApiImplicitParam(name = Constant.ORDER, value = "排序方式，可选值(asc、desc)", paramType = "query", dataType="String"),
            @ApiImplicitParam(name = "merchantId", value = "商户编号", paramType = "query",required=true, dataType="Long")
    })
    public Result<PageData<MasterOrderDTO>> calcelPage(@ApiIgnore @RequestParam Map<String, Object> params){
        params.put("status", Order.PAY_STTAUS_5.getStatus()+"");
        PageData<MasterOrderDTO> page = masterOrderService.page(params);
        return new Result<PageData<MasterOrderDTO>>().ok(page);
    }
    @Login
    @GetMapping("allPage")
    @ApiOperation("全部列表")
    @ApiImplicitParams({
            @ApiImplicitParam(name = Constant.PAGE, value = "当前页码，从1开始", paramType = "query", required = true, dataType="int") ,
            @ApiImplicitParam(name = Constant.LIMIT, value = "每页显示记录数", paramType = "query",required = true, dataType="int") ,
            @ApiImplicitParam(name = Constant.ORDER_FIELD, value = "排序字段", paramType = "query", dataType="String") ,
            @ApiImplicitParam(name = Constant.ORDER, value = "排序方式，可选值(asc、desc)", paramType = "query", dataType="String"),
            @ApiImplicitParam(name = "merchantId", value = "商户编号", paramType = "query",required=true, dataType="Long")
    })
    public Result<PageData<MasterOrderDTO>> allPage(@ApiIgnore @RequestParam Map<String, Object> params){
        PageData<MasterOrderDTO> page = masterOrderService.page(params);
        return new Result<PageData<MasterOrderDTO>>().ok(page);
    }

    @Login
    @GetMapping("{orderId}")
    @ApiOperation("订单详情")
    public Result<OrderDTO> getOrderInfo(@PathVariable("order_id") String orderId){
        OrderDTO data = masterOrderService.getOrder(orderId);

        return new Result<OrderDTO>().ok(data);
    }

    @Login
    @PostMapping("generateOrder")
    @ApiOperation("生成订单")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "dto", value = "订单", paramType = "body", required = true, dataType="MasterOrderDTO"),
            @ApiImplicitParam(name = "dtoList", value = "订单菜品列表", paramType = "body", required = true, dataType="List<<SlaveOrderDTO>>"),
    })
    public Result generateOrder(MasterOrderDTO dto, List<SlaveOrderDTO> dtoList,@LoginUser ClientUserEntity user){
        ValidatorUtils.validateEntity(dto, AddGroup.class, DefaultGroup.class);
        ValidatorUtils.validateEntity(dtoList, AddGroup.class, DefaultGroup.class);
        return  masterOrderService.orderSave(dto,dtoList,user);
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
    @Login
    @Transient
    @PutMapping("cancelUpdate")
    @ApiOperation("取消订单")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "编号", paramType = "query", required = true, dataType="long"),
            @ApiImplicitParam(name = "verify", value = "取消人", paramType = "query", required = true, dataType="long"),
            @ApiImplicitParam(name="verify_reason",value="取消原因",paramType = "query",required = true,dataType = "String")
    })
    public Result calcelUpdate(long id,long verify,String verify_reason){
        masterOrderService.updateStatusAndReason(id,Order.PAY_STTAUS_5.getStatus(),verify,new Date(),verify_reason);
        MasterOrderDTO dto = masterOrderService.get(id);
        //同时将包房或者桌设置成未使用状态
        merchantRoomParamsSetService.updateStatus(dto.getRoomId(), MerchantRoomEnm.STATE_USE_NO.getType());
        return new Result();
    }
    @Login
    @PutMapping("acceptUpdate")
    @ApiOperation("接受订单")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "编号", paramType = "query", required = true, dataType="long"),
            @ApiImplicitParam(name = "verify", value = "接受人", paramType = "query", required = true, dataType="long")
    })
    public Result acceptUpdate(long id,long verify){
        masterOrderService.updateStatusAndReason(id,Order.PAY_STTAUS_3.getStatus(),verify,new Date(),"接受订单");
        return new Result();
    }
    @Login
    @Transient
    @PutMapping("finishUpdate")
    @ApiOperation("完成订单")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "编号", paramType = "query", required = true, dataType="long"),
            @ApiImplicitParam(name = "verify", value = "操作人", paramType = "query", required = true, dataType="long")
    })
    public Result finishUpdate(long id,long verify){
        masterOrderService.updateStatusAndReason(id,Order.PAY_STTAUS_4.getStatus(),verify,new Date(),"完成订单");
        MasterOrderDTO dto = masterOrderService.get(id);
        //同时将包房或者桌设置成未使用状态
        merchantRoomParamsSetService.updateStatus(dto.getRoomId(), MerchantRoomEnm.STATE_USE_NO.getType());
        return new Result();
    }
    @Login
    @Transient
    @PutMapping("refundYesUpdate")
    @ApiOperation("同意退款")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "编号", paramType = "query", required = true, dataType="long"),
            @ApiImplicitParam(name = "verify", value = "审核人", paramType = "query", required = true, dataType="long")
    })
    public Result refundYesUpdate(long id,long verify){
        MasterOrderDTO dto = masterOrderService.get(id);
        masterOrderService.updateStatusAndReason(id,Order.PAY_STTAUS_7.getStatus(),verify,new Date(),"同意退款");
        //同时将包房或者桌设置成未使用状态
        merchantRoomParamsSetService.updateStatus(dto.getRoomId(), MerchantRoomEnm.STATE_USE_NO.getType());
        return new Result();
    }
    @Login
    @PutMapping("refundNoUpdate")
    @ApiOperation("拒绝退款")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "编号", paramType = "query", required = true, dataType="long"),
            @ApiImplicitParam(name = "verify", value = "拒绝人", paramType = "query", required = true, dataType="long"),
            @ApiImplicitParam(name="verify_reason",value="拒绝原因",paramType = "query",required = true,dataType = "String")
    })
    public Result refundNoUpdate(long id,long verify,String verify_reason){
        masterOrderService.updateStatusAndReason(id,Order.PAY_STATUS_9.getStatus(),verify,new Date(),verify_reason);
        return new Result();
    }
    
}
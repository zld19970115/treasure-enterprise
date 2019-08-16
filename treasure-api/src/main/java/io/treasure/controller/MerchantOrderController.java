package io.treasure.controller;


import io.treasure.annotation.Login;
import io.treasure.common.constant.Constant;
import io.treasure.common.page.PageData;
import io.treasure.common.utils.Result;
import io.treasure.common.validator.AssertUtils;
import io.treasure.common.validator.ValidatorUtils;
import io.treasure.common.validator.group.AddGroup;
import io.treasure.common.validator.group.DefaultGroup;
import io.treasure.common.validator.group.UpdateGroup;
import io.treasure.dto.MerchantOrderDTO;
import io.treasure.enm.Common;
import io.treasure.enm.Order;
import io.treasure.entity.MerchantOrderDetailEntity;
import io.treasure.service.MerchantOrderDetailService;
import io.treasure.service.MerchantOrderService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.annotation.Transient;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

import javax.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.Map;


/**
 * 商户订单管理
 *
 * @author Super 63600679@qq.com
 * @since 1.0.0 2019-08-09
 */
@RestController
@RequestMapping("/merchantorder")
@Api(tags="订单管理")
public class MerchantOrderController {
    @Autowired
    private MerchantOrderService merchantOrderService;
    @Autowired
    private MerchantOrderDetailService merchantOrderDetailService;

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
    public Result<PageData<MerchantOrderDTO>> appointmentPage(@ApiIgnore @RequestParam Map<String, Object> params){
        params.put("payStatus", Order.PAY_STATUS_8+"");
        params.put("status",Common.STATUS_ON.getStatus()+"");
        PageData<MerchantOrderDTO> page = merchantOrderService.page(params);
        return new Result<PageData<MerchantOrderDTO>>().ok(page);
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
    public Result<PageData<MerchantOrderDTO>> chargePage(@ApiIgnore @RequestParam Map<String, Object> params){
        params.put("payStatus", Order.PAY_STTAUS_7);
        params.put("status",Common.STATUS_ON.getStatus()+"");
        PageData<MerchantOrderDTO> page = merchantOrderService.page(params);
        return new Result<PageData<MerchantOrderDTO>>().ok(page);
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
    public Result<PageData<MerchantOrderDTO>> ongPage(@ApiIgnore @RequestParam Map<String, Object> params){
        params.put("payStatus", Order.PAY_STTAUS_2.getStatus()+","+Order.PAY_STTAUS_3+","+Order.PAY_STTAUS_6);
        params.put("status",Common.STATUS_ON.getStatus()+"");
        PageData<MerchantOrderDTO> page = merchantOrderService.page(params);
        return new Result<PageData<MerchantOrderDTO>>().ok(page);
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
    public Result<PageData<MerchantOrderDTO>> finishPage(@ApiIgnore @RequestParam Map<String, Object> params){
        params.put("payStatus", Order.PAY_STTAUS_4.getStatus()+"");
        params.put("status",Common.STATUS_ON.getStatus()+"");
        PageData<MerchantOrderDTO> page = merchantOrderService.page(params);
        return new Result<PageData<MerchantOrderDTO>>().ok(page);
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
    public Result<PageData<MerchantOrderDTO>> calcelPage(@ApiIgnore @RequestParam Map<String, Object> params){
        params.put("payStatus", Order.PAY_STTAUS_5.getStatus()+"");
        params.put("status",Common.STATUS_ON.getStatus()+"");
        PageData<MerchantOrderDTO> page = merchantOrderService.page(params);
        return new Result<PageData<MerchantOrderDTO>>().ok(page);
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
    public Result<PageData<MerchantOrderDTO>> allPage(@ApiIgnore @RequestParam Map<String, Object> params){
        params.put("status",Common.STATUS_ON.getStatus()+"");
        PageData<MerchantOrderDTO> page = merchantOrderService.page(params);
        return new Result<PageData<MerchantOrderDTO>>().ok(page);
    }
    @Login
    @GetMapping("getById")
    @ApiOperation("详细信息")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "编号", paramType = "query", required = true, dataType="long")
    })
    public Result<MerchantOrderDTO> get(long id){
        MerchantOrderDTO data = merchantOrderService.get(id);
        List<MerchantOrderDetailEntity> detailList= merchantOrderDetailService.getByOrderId(id,Common.STATUS_ON.getStatus());
        data.setDetailList(detailList);
        return new Result<MerchantOrderDTO>().ok(data);
    }

//    @PostMapping
//    @ApiOperation("保存")
//    public Result save(@RequestBody MerchantOrderDTO dto){
//        //效验数据
//        ValidatorUtils.validateEntity(dto, AddGroup.class, DefaultGroup.class);
//
//        merchantOrderService.save(dto);
//
//        return new Result();
//    }
//
//    @PutMapping
//    @ApiOperation("修改")
//    public Result update(@RequestBody MerchantOrderDTO dto){
//        //效验数据
//        ValidatorUtils.validateEntity(dto, UpdateGroup.class, DefaultGroup.class);
//
//        merchantOrderService.update(dto);
//
//        return new Result();
//    }
    @Login
    @Transient
    @DeleteMapping("delete")
    @ApiOperation("删除")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "编号", paramType = "query", required = true, dataType="long")
    })
    public Result delete(long id){
        merchantOrderService.remove(id, Common.STATUS_OFF.getStatus());
        //删除订单明细
        merchantOrderDetailService.remove(id,Common.STATUS_OFF.getStatus());
        return new Result();
    }

}
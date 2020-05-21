package io.treasure.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import io.treasure.annotation.Login;
import io.treasure.annotation.LoginUser;
import io.treasure.common.constant.Constant;
import io.treasure.common.page.PageData;
import io.treasure.common.utils.Result;
import io.treasure.common.validator.ValidatorUtils;
import io.treasure.common.validator.group.AddGroup;
import io.treasure.common.validator.group.DefaultGroup;
import io.treasure.dto.ChargeCashDTO;
import io.treasure.dto.ChargeCashSetDTO;
import io.treasure.entity.ClientUserEntity;
import io.treasure.service.ChargeCashService;
import io.treasure.service.ChargeCashSetService;
import io.treasure.utils.OrderUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

import java.math.BigDecimal;
import java.text.ParseException;
import java.util.List;
import java.util.Map;

/**
 * 现金充值表
 * 2019.8.21
 */
@RestController
@RequestMapping("/chargrCash")
@Api(tags="现金充值表")
public class ChargeCashController {
    @Autowired
    private ChargeCashSetService chargeCashSetService;
    @Autowired
    private ChargeCashService chargeCashService;
    @GetMapping("chargrCash")
    @ApiOperation("现金支付回调业务")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "orderNo", value = "订单编号", required = true, paramType = "query"),
            @ApiImplicitParam(name = "total_fee", value = "订单总金额(元)", required = true, paramType = "query")
    })
    public Result chargrCash(String total_fee, String orderNo) {

        // 调用业务
        String out_trade_no = orderNo;
        //单位分变成元
        BigDecimal total_amount = new BigDecimal(total_fee);
        Map<String, String> responseMap = chargeCashService.cashNotify(total_amount, out_trade_no);
        return new Result().ok(responseMap);
    }
    @Login
    @PostMapping("getCashId")
    @ApiOperation("获取充值订单Id")
    public Result getCashId(@RequestParam Long userId ){

        //获取充值订单Id
        String orderId = OrderUtil.getOrderIdByTime(userId);
        return new Result().ok(orderId);
    }
    @Login
    @PostMapping("generateOrder")
    @ApiOperation("生成订单")
    public Result generateOrder(@RequestBody ChargeCashDTO dto, @LoginUser ClientUserEntity user) throws ParseException {
        ValidatorUtils.validateEntity(dto, AddGroup.class, DefaultGroup.class);
        return  chargeCashService.orderSave(dto,user);
    }
    @Login
    @GetMapping("getCash")
    @ApiOperation("获取可充值金额")
    public Result getCash() {
        List<ChargeCashSetDTO> select = chargeCashSetService.select();
        return  new Result().ok(select);
    }

    @PostMapping("/getChargeCashAll")
    @ApiOperation("全部充值记录")
    @ApiImplicitParams({
            @ApiImplicitParam(name = Constant.PAGE, value = "当前页码，从1开始", paramType = "query", required = true, dataType="int") ,
            @ApiImplicitParam(name = Constant.LIMIT, value = "每页显示记录数", paramType = "query",required = true, dataType="int") ,
            @ApiImplicitParam(name = Constant.ORDER_FIELD, value = "排序字段", paramType = "query", dataType="String") ,
            @ApiImplicitParam(name = Constant.ORDER, value = "排序方式，可选值(asc、desc)", paramType = "query", dataType="String"),
    })
    public Result getChargeCashAll(@ApiIgnore @RequestParam Map<String, Object> params){
        PageData<ChargeCashDTO> page = chargeCashService.getChargeCashAll(params);
        return new Result().ok(page);
    }

    @PostMapping("/getChargeCashByCreateDate")
    @ApiOperation("根据手机号或者日期查询充值记录")
    @ApiImplicitParams({
            @ApiImplicitParam(name = Constant.PAGE, value = "当前页码，从1开始", paramType = "query", required = true, dataType="int") ,
            @ApiImplicitParam(name = Constant.LIMIT, value = "每页显示记录数", paramType = "query",required = true, dataType="int") ,
            @ApiImplicitParam(name = Constant.ORDER_FIELD, value = "排序字段", paramType = "query", dataType="String") ,
            @ApiImplicitParam(name = Constant.ORDER, value = "排序方式，可选值(asc、desc)", paramType = "query", dataType="String"),
            @ApiImplicitParam(name = "phone", value = "手机号", paramType = "query", dataType="String"),
            @ApiImplicitParam(name = "createDateTop", value = "记录开始日期", paramType = "query", dataType="String"),
            @ApiImplicitParam(name = "createDateDown", value = "记录截止日期", paramType = "query", dataType="String"),

    })
    public Result getChargeCashByCreateDate(@ApiIgnore @RequestParam Map<String, Object> params){
        PageData<ChargeCashDTO> page = chargeCashService.getChargeCashByCreateDate(params);
        return new Result().ok(page);
    }




}

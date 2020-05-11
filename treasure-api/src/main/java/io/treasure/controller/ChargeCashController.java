package io.treasure.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import io.treasure.annotation.Login;
import io.treasure.annotation.LoginUser;
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
        BigDecimal total_amount = new BigDecimal(total_fee).divide(new BigDecimal("100"));
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
}

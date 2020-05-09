package io.treasure.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.treasure.annotation.Login;
import io.treasure.annotation.LoginUser;
import io.treasure.common.utils.Result;
import io.treasure.common.validator.ValidatorUtils;
import io.treasure.common.validator.group.AddGroup;
import io.treasure.common.validator.group.DefaultGroup;
import io.treasure.dto.ChargeCashDTO;
import io.treasure.dto.ChargeCashSetDTO;
import io.treasure.entity.ChargeCashEntity;
import io.treasure.entity.ChargeCashSetEntity;
import io.treasure.entity.ClientUserEntity;
import io.treasure.service.ChargeCashService;
import io.treasure.service.ChargeCashSetService;
import io.treasure.utils.OrderUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.text.ParseException;
import java.util.Date;
import java.util.List;

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
    @PostMapping("/add")
    @ApiOperation("现金充值")
    public Result chargrCash(@RequestParam Long userId ,@RequestParam BigDecimal cash){

        ChargeCashSetEntity chargeCashSetEntity = chargeCashSetService.selectByCash(cash);
        BigDecimal changeGift = chargeCashSetEntity.getChangeGift();
        ChargeCashEntity chargeCashEntity = new ChargeCashEntity();
        chargeCashEntity.setCash(cash);
        chargeCashEntity.setChangeGift(changeGift);
        Date date = new Date();
        chargeCashEntity.setSaveTime(date);
        chargeCashEntity.setUserId(userId);
        chargeCashService.insert(chargeCashEntity);
        return new Result().ok("现金充值成功");
    }
    @PostMapping("/getCashId")
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

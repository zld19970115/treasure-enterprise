package io.treasure.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import io.treasure.common.utils.Result;
import io.treasure.entity.TakeoutOrdersEntity;
import io.treasure.jra.impl.MerchantMessageJRA;
import io.treasure.jro.MerchantMessage;
import io.treasure.service.CouponForActivityService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import springfox.documentation.annotations.ApiIgnore;

import java.math.BigDecimal;

@RestController
@RequestMapping("/notify_test")
@Api(tags="商户提醒数量测试")
public class TestController {

    @Autowired
    MerchantMessageJRA merchantMessageJRA;


    @GetMapping("/ntest")
    @ApiOperation("查询提醒数量")
    public Result requireOrder(){

        MerchantMessage counterByOrderDao = merchantMessageJRA.getCounterByOrderDao(1271232511760064513L);

        return new Result().ok(counterByOrderDao);

    }



    @GetMapping("mmsg")
    @ApiOperation("查询消息数量")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "martId", value = "主订单编号", paramType = "query", required = true, dataType="long")
    })
    public Result queryOrderNotify(@ApiIgnore @RequestParam long martId) {
        MerchantMessage counterByOrderDao = merchantMessageJRA.getCounterByOrderDao(martId);

        return new Result().ok(counterByOrderDao);

    }

    @Autowired
    CouponForActivityService couponForActivityService;

    @GetMapping("scoins")
    @ApiOperation("扣除coins测试")
    public void subCoins(){
        couponForActivityService.updateCoinsConsumeRecord(1296633686118285313L,new BigDecimal("50"),"coins-test");
    }
    @GetMapping("acoins")
    @ApiOperation("恢复coins测试")
    public void subaCoins(){
        couponForActivityService.resumeAllCoinsRecord(1296633686118285313L,"coins-test");
    }



}

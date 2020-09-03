package io.treasure.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import io.treasure.common.utils.Result;
import io.treasure.service.CouponForActivityService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;

@RestController
@RequestMapping("/about_coins")
@Api(tags="宝币相关内容")
public class CouponForActivityController {


    @Autowired
    private CouponForActivityService couponForActivityService;

    @GetMapping("can_use_coins")
    @ApiOperation("查询可用的宝币数量")
    @ApiImplicitParam(name="clientId",value = "用户表id",dataType = "long",paramType = "query",required = true)
    public Result getCanUseCoinsAmount(Long clientId){
        //取得指定的用户可用的宝币数量
        BigDecimal clientCanUseTotalCoinsVolume = couponForActivityService.getClientCanUseTotalCoinsVolume(clientId);
        return new Result().ok(clientCanUseTotalCoinsVolume);
    }


    @GetMapping("consume_test")
    @ApiOperation("宝币扣除测试")
    @ApiImplicitParams({
            @ApiImplicitParam(name="clientId",value = "用户表id",dataType = "long",paramType = "query",required = true),
            @ApiImplicitParam(name="consumeValue",value = "宝币消费值",dataType = "double",paramType = "query",required = true),
    })
    public Result getRequestWithDrawList(Long clientId, Double consumeValue){
        try{
            couponForActivityService.updateCoinsConsumeRecord(clientId,new BigDecimal(consumeValue));
            new Result().ok("success");
        }catch (Exception e){
            new Result().error("failure");
        }
        return new Result().ok("success");
    }
}

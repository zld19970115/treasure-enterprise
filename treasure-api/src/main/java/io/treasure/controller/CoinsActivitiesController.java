package io.treasure.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import io.treasure.common.utils.Result;
import io.treasure.service.CoinsActivitiesService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.text.ParseException;

@RestController
@RequestMapping("/coins_activity")
@Api(tags="宝币活动(当前)")
public class CoinsActivitiesController {

    @Autowired
    private CoinsActivitiesService coinsActivitiesService;

    @GetMapping("draw_coins")
    @ApiOperation("抢宝币")
    @ApiImplicitParam(name="clientId",value = "用户表id",dataType = "long",paramType = "query",required = false)
    public Result clientDraw(Long clientId) throws ParseException {
        return coinsActivitiesService.clientDraw(clientId);
    }

    @GetMapping("info")
    @ApiOperation("活动信息")
    public Result getCoinsActivityInfo(){
        return coinsActivitiesService.getCoinsActivityInfo();
    }

    @GetMapping("count_down")
    @ApiOperation("活动倒计时")
    public Result getCountDownInfo() throws ParseException {
        return coinsActivitiesService.getCountDownInfo();
    }


}

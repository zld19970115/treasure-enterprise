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

/**
 * 还差抢宝币机器人===============================================================
 * 虚拟奖池，=================================================================
 * 奖金剩余值=====================================================================
 * 另外微信key过期需要处理=============================================================
 * 测宝币上限是否有问题=====================================================================
 */


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
    public Result getCoinsActivityInfo() throws ParseException {
        return coinsActivitiesService.getCoinsActivityInfo();
    }

    @GetMapping("count_down")
    @ApiOperation("活动倒计时")
    public Result getCountDownInfo() throws ParseException {
        return coinsActivitiesService.getCountDownInfo();
    }

    @GetMapping("first_prize_list")
    @ApiOperation("头奖列表")
    public Result getFirstPrizeList() throws ParseException {
        return coinsActivitiesService.getFirstPrizeList();
    }

}

package io.treasure.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import io.treasure.common.utils.Result;
import io.treasure.dao.CoinsActivitiesDao;
import io.treasure.entity.CoinsActivitiesEntity;
import io.treasure.entity.SignedRewardSpecifyTimeEntity;
import io.treasure.service.CoinsActivitiesService;
import io.treasure.utils.TimeUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.text.ParseException;

@RestController
@RequestMapping("/coins_activity")
@Api(tags="宝币活动(当前)")
public class CoinsActivitiesController {

    @Autowired
    private CoinsActivitiesService coinsActivitiesService;

    @Autowired(required = false)
    private CoinsActivitiesDao coinsActivitiesDao;

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

    @GetMapping("coins_params")
    @ApiOperation("取得活动参数(重写的)")
    @ApiImplicitParam(name="id",value = "参数id",dataType = "long",paramType = "query",required = false)
    public Result getCoinsActivityParams(Long id){
        CoinsActivitiesEntity coinsActivityById = coinsActivitiesService.getCoinsActivityById(2L, false);
        return new Result().ok(coinsActivityById);
    }

    /**
     * 修改签到领宝币参数
     * @param entity
     * @return
     */
    @PostMapping("coins_params")
    @ApiOperation("修改活动参数(重写的)")
    public Result updateCoinActivityParams(@RequestBody CoinsActivitiesEntity entity) throws ParseException {

        entity.setId(2L);
        try{
            coinsActivitiesDao.updateById(entity);
            return new Result().ok("success");
        }catch (Exception e){
            return new Result().error("failure");
        }
    }




}

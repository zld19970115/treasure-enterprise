package io.treasure.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import io.treasure.annotation.Login;
import io.treasure.common.constant.Constant;
import io.treasure.common.page.PageData;
import io.treasure.common.utils.Result;
import io.treasure.dao.ClientUserDao;
import io.treasure.dao.MulitCouponBoundleDao;
import io.treasure.dao.SignedRewardSpecifyTimeDao;
import io.treasure.enm.ESharingRewardGoods;
import io.treasure.entity.ActivityEntity;
import io.treasure.entity.ClientUserEntity;
import io.treasure.entity.MulitCouponBoundleEntity;
import io.treasure.entity.SignedRewardSpecifyTimeEntity;
import io.treasure.service.CouponForActivityService;
import io.treasure.service.impl.SignedRewardSpecifyTimeServiceImpl;
import io.treasure.utils.SharingActivityRandomUtil;
import io.treasure.utils.TimeUtil;
import io.treasure.vo.SignedRewardSpecifyTimeVo;
import io.treasure.vo.SignedRewardVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

import java.math.BigDecimal;
import java.text.ParseException;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static cn.hutool.core.date.SystemClock.now;

@RestController
@RequestMapping("/about_coins")
@Api(tags="宝币相关内容")
public class CouponForActivityController {


    @Autowired
    private CouponForActivityService couponForActivityService;
    @Autowired
    private SignedRewardSpecifyTimeServiceImpl signedRewardSpecifyTimeService;
    @Autowired(required = false)
    private SignedRewardSpecifyTimeDao signedRewardSpecifyTimeDao;

    @Autowired(required = false)
    private MulitCouponBoundleDao mulitCouponBoundleDao;

    @GetMapping("can_use_coins")
    @ApiOperation("查询可用的宝币数量")
    @ApiImplicitParam(name="clientId",value = "用户表id",dataType = "long",paramType = "query",required = true)
    public Result getCanUseCoinsAmount(Long clientId){
        //取得指定的用户可用的宝币数量
        BigDecimal clientCanUseTotalCoinsVolume = couponForActivityService.getClientCanUseTotalCoinsVolume(clientId);
        return new Result().ok(clientCanUseTotalCoinsVolume);
    }

    @GetMapping("activity_coins_list")
    @ApiOperation("查询活动宝币列表")
    @ApiImplicitParams({
            @ApiImplicitParam(name="clientId",value = "用户表id",dataType = "long",paramType = "query",required = false),
            @ApiImplicitParam(name = "page", value = "当前页码，从1开始", paramType = "query", required = true, dataType="int") ,
            @ApiImplicitParam(name = "index", value = "每页显示记录数", paramType = "query",required = true, dataType="int") ,
            @ApiImplicitParam(name = "enableOnly", value = "1仅有效记录，2全部", paramType = "query", dataType="int")
    })
    public Result getActivityCoinsList(Long clientId,Integer page,Integer index,Integer enableOnly){
        //取得指定的用户可用的宝币数量
        boolean only = false;
        if(enableOnly == 1)
            only = true;

        IPage<MulitCouponBoundleEntity> recordByClientId = couponForActivityService.getRecordByClientId(clientId, only, page, index);
        return new Result().ok(recordByClientId);
    }

    @Login
    @GetMapping("getActivityCoinsListNew")
    @ApiOperation("分页查询")
    @ApiImplicitParams({
            @ApiImplicitParam(name = Constant.PAGE, value = "当前页码，从1开始", paramType = "query", required = true, dataType="int") ,
            @ApiImplicitParam(name = Constant.LIMIT, value = "每页显示记录数", paramType = "query",required = true, dataType="int")
    })
    public Result<PageData> getActivityCoinsListNew(@ApiIgnore @RequestParam Map<String, Object> params) {
        return new Result<PageData>().ok(couponForActivityService.pageList(params));
    }

    @Autowired(required = false)
    private ClientUserDao clientUserDao;
    @GetMapping("signed_reward")
    @ApiOperation("签到领宝币")
    @ApiImplicitParam(name="clientId",value = "用户表id",dataType = "long",paramType = "query",required = true)
    public Result signedReward(Long clientId) throws ParseException {
        String value = "value";//剩侠宝币的值
        String count = "count";//剩余数量的值
        Result result = new Result();
        ClientUserEntity clientUserEntity = clientUserDao.selectById(clientId);


        Map<String, String> signedActivityCoinsNumberInfo = couponForActivityService.getSignedActivityCoinsNumberInfo();
        int bdCount = Integer.parseInt(signedActivityCoinsNumberInfo.get(count));
        BigDecimal dbValue = new BigDecimal(signedActivityCoinsNumberInfo.get(value));

        SignedRewardSpecifyTimeEntity signedParamsById = couponForActivityService.getParamsById(null);
        Date start_pmt = signedParamsById.getStartPmt();
        Date ending_pmt = signedParamsById.getEndingPmt();
        boolean betweenTime = TimeUtil.isBetweenTime(start_pmt, ending_pmt);
        SignedRewardSpecifyTimeVo vo = new SignedRewardSpecifyTimeVo();
        vo.setSignedRewardSpecifyTimeEntity(signedParamsById);

        Integer validityLong = signedParamsById.getValidityLong();
        Integer validityUnit = signedParamsById.getValidityUnit();
        ESharingRewardGoods.ActityValidityUnit currentUnit = ESharingRewardGoods.ActityValidityUnit.UNIT_DAYS;
        if(validityUnit == ESharingRewardGoods.ActityValidityUnit.UNIT_WEEKS.getCode()){
            currentUnit = ESharingRewardGoods.ActityValidityUnit.UNIT_WEEKS;
        }else if(validityUnit == ESharingRewardGoods.ActityValidityUnit.UNIT_MONTHS.getCode()){
            currentUnit = ESharingRewardGoods.ActityValidityUnit.UNIT_MONTHS;
        }

        if (clientUserEntity == null){
            vo.setRewardValue(new BigDecimal("0"));
            vo.setComment("用户id无效,请先登录或注册！");
            result.setCode(500);
            result.setData(vo);
            return result;
        }

        long now = new Date().getTime();
        boolean onTimeRange = false;
        long stime = start_pmt.getTime();
        long etime = ending_pmt.getTime();

        if(now>=stime && now <= etime){
            onTimeRange = true;
        }

        if(!betweenTime||!onTimeRange){
            vo.setRewardValue(new BigDecimal("0"));
            vo.setComment("本次本日活动已到期！");
            result.setCode(500);
            result.setData(vo);
            return result;
        }


        Integer activityMode = signedParamsById.getActivityMode();

        Boolean rBoolean = couponForActivityService.clientCheckForSignedForReward(clientId);//客户参加本活动次数是否未超限
        if(rBoolean){

            switch(activityMode){
                case 1://数量模式

                    //常规模式
                    if(bdCount>0 && dbValue.doubleValue()>0){
                        BigDecimal randomCoins = SharingActivityRandomUtil.getRandomCoins(dbValue, bdCount);
                        if(bdCount == 1)
                            randomCoins = dbValue;
                        vo.setRewardValue(randomCoins);
                        vo.setComment("恭喜获得"+randomCoins+"宝币！");
                        try{
                            couponForActivityService.insertClientActivityRecord(clientId,randomCoins,3,validityLong,currentUnit);
                            result.setCode(200);
                            result.setData(vo);
                            return result;
                        }catch (Exception e){
                            e.printStackTrace();
                            vo.setComment("服务器忙，请稍候重试！");
                            result.setCode(500);
                            result.setData(vo);
                            return result;
                        }
                    }else{
                        vo.setRewardValue(new BigDecimal("0"));
                        vo.setComment("红包已经抢完了，下次要点来！");
                        result.setCode(500);
                        result.setData(vo);
                        return result;
                    }
                default://仅范围模式
                    Integer maxValue = signedParamsById.getMaxValue();
                    Integer minValue = signedParamsById.getMinValue();
                    BigDecimal rewardCoins = new BigDecimal("0");
                    if(dbValue.doubleValue()>maxValue){
                        rewardCoins = SharingActivityRandomUtil.getRandomCoinsInRange(new BigDecimal(maxValue+""),new BigDecimal(minValue+""));

                        try{
                            vo.setRewardValue(rewardCoins);
                            vo.setComment("恭喜获得"+rewardCoins+"宝币！");
                            couponForActivityService.insertClientActivityRecord(clientId,rewardCoins,3,validityLong,currentUnit);
                            result.setCode(200);
                            result.setData(vo);
                            return result;

                        }catch (Exception e){
                            e.printStackTrace();
                            vo.setRewardValue(new BigDecimal("0"));
                            vo.setComment("服务器忙，请稍候重试！");
                            result.setCode(500);
                            result.setData(vo);
                            return result;
                        }

                    }else if(dbValue.doubleValue()>=minValue){
                        rewardCoins = dbValue;

                        try{
                            vo.setRewardValue(rewardCoins);
                            vo.setComment("恭喜获得"+rewardCoins+"宝币！");
                            couponForActivityService.insertClientActivityRecord(clientId,rewardCoins,3,validityLong,currentUnit);
                            result.setCode(200);
                            result.setData(vo);
                            return result;

                        }catch (Exception e){
                            e.printStackTrace();
                            vo.setRewardValue(new BigDecimal("0"));
                            vo.setComment("服务器忙，请稍候重试！");
                            result.setCode(500);
                            result.setData(vo);
                            return result;
                        }
                    }else{
                        vo.setRewardValue(new BigDecimal("0"));
                        vo.setComment("红包已经抢完了，下次要点来！");
                        result.setCode(500);
                        result.setData(vo);
                        return result;
                    }
            }

        }else{
            vo.setRewardValue(new BigDecimal("0"));
            vo.setComment("已经参与过本活动了，下次再来吧！");
            result.setCode(500);
            result.setData(vo);
            return result;
        }
    }

    @GetMapping("sr_info")
    @ApiOperation("签到领宝币信息")
    public Result signedRewardInfo() throws ParseException {
        SignedRewardSpecifyTimeEntity signedParamsById = couponForActivityService.getParamsById(null);

        return new Result().ok(signedParamsById);
    }

    @GetMapping("sr_info_plus")
    @ApiOperation("签到领宝币信息")
    public Result signedRewardInfoPlus() throws ParseException {
        SignedRewardSpecifyTimeEntity signedParamsById = couponForActivityService.getParamsById(null);

        String value = "value";//剩侠宝币的值
        String count = "count";//剩余数量的值

        Map<String, String> signedActivityCoinsNumberInfo = couponForActivityService.getSignedActivityCoinsNumberInfo();
        int bdCount = Integer.parseInt(signedActivityCoinsNumberInfo.get(count));
        BigDecimal dbValue = new BigDecimal(signedActivityCoinsNumberInfo.get(value));
        SignedRewardVo s = new SignedRewardVo();
        s.setSignedRewardSpecifyTimeEntity(signedParamsById);
        s.setValue(dbValue);
        s.setCount(bdCount);

        return new Result().ok(s);
    }

    @GetMapping("is_ontime")
    @ApiOperation("是否活动中")
    public Result isOnTime() throws ParseException {
        SignedRewardSpecifyTimeEntity signedParamsById = couponForActivityService.getParamsById(null);
        Date start_pmt = signedParamsById.getStartPmt();
        Date ending_pmt = signedParamsById.getEndingPmt();
        boolean betweenTime = TimeUtil.isBetweenTime(start_pmt, ending_pmt);
        Result result = new Result();

        long now = new Date().getTime();
        boolean onTimeRange = false;
        long stime = start_pmt.getTime();
        long etime = ending_pmt.getTime();

        if(now>=stime && now <= etime){
            onTimeRange = true;
        }

        if(!betweenTime||!onTimeRange){
            result.setMsg("expire");
            result.setCode(200);
            result.setData(-1);
            return result;
        }
        result.setMsg("inProcess");
        result.setCode(200);
        result.setData(1);
        return result;
       /*
        String value = "value";//剩侠宝币的值
        String count = "count";//剩余数量的值

        Map<String, String> signedActivityCoinsNumberInfo = couponForActivityService.getSignedActivityCoinsNumberInfo();
        int bdCount = Integer.parseInt(signedActivityCoinsNumberInfo.get(count));
        BigDecimal dbValue = new BigDecimal(signedActivityCoinsNumberInfo.get(value));

        Integer activityMode = signedParamsById.getActivityMode();
        Integer minValue = signedParamsById.getMinValue();

        switch(activityMode){
            case 1://数量模式
                if(bdCount <= 0 || dbValue.doubleValue() <= 0){
                    result.setMsg("expire");
                    result.setCode(200);
                    result.setData(-1);
                    return result;
                }
                break;
            case 2: //范围模式

                if(dbValue.doubleValue()<minValue){
                    result.setMsg("expire");
                    result.setCode(200);
                    result.setData(-1);
                    return result;
                }
                break;
        }
        */

    }

//======================================================================================================================
//总后台       ======      活动参数设置

    /**
     * 取得签到领宝币
     * @return
     */
    @GetMapping("coins_params")
    @ApiOperation("取得活动参数")
    @ApiImplicitParam(name="id",value = "参数id",dataType = "long",paramType = "query",required = false)
    public Result getCoinsActivityParams(Long id){
        SignedRewardSpecifyTimeEntity signedParamsById = signedRewardSpecifyTimeService.getSignedParamsById(null);
        return new Result().ok(signedParamsById);
    }
    /**
     * 修改签到领宝币参数
     * @param entity
     * @return
     */
    @PostMapping("coins_params")
    @ApiOperation("修改活动参数,id只能为1")
    public Result updateCoinActivityParams(@RequestBody SignedRewardSpecifyTimeEntity entity) throws ParseException {
        if(entity.getMinValue()==null)
            entity.setMinValue(1);
        if(entity.getMaxValue()==null)
            entity.setMaxValue(5);
        if(entity.getActivityMode() != 1 && entity.getActivityMode() != 2)
            entity.setActivityMode(1);
        if(entity.getTimes() == null)
            entity.setTimes(1);
        if(entity.getEndingPmt()==null)
            entity.setEndingPmt(TimeUtil.simpleDateFormat.parse("2020-12-30 10:00:00"));
        if(entity.getStartPmt()==null)
            entity.setStartPmt(TimeUtil.simpleDateFormat.parse("2020-09-01 10:00:00"));
        if(entity.getPersonAmount() == null)
            entity.setPersonAmount(50);
        if(entity.getRewardValue() == null)
            entity.setRewardValue(50);
        if(entity.getRewardType()==null)
            entity.setRewardType(1);
        if(entity.getId()== null)
            entity.setId(1L);
        try{
            signedRewardSpecifyTimeDao.updateById(entity);
            return new Result().ok("success");
        }catch (Exception e){
            return new Result().error("failure");
        }
    }


}

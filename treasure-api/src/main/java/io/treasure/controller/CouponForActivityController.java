package io.treasure.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import io.treasure.common.constant.Constant;
import io.treasure.common.utils.Result;
import io.treasure.dao.MulitCouponBoundleDao;
import io.treasure.entity.MulitCouponBoundleEntity;
import io.treasure.entity.SignedRewardSpecifyTimeEntity;
import io.treasure.service.CouponForActivityService;
import io.treasure.service.impl.SignedRewardSpecifyTimeServiceImpl;
import io.treasure.utils.SharingActivityRandomUtil;
import io.treasure.utils.TimeUtil;
import io.treasure.vo.SignedRewardSpecifyTimeVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.text.ParseException;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/about_coins")
@Api(tags="宝币相关内容")
public class CouponForActivityController {


    @Autowired
    private CouponForActivityService couponForActivityService;
    @Autowired
    private SignedRewardSpecifyTimeServiceImpl signedRewardSpecifyTimeService;

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
            @ApiImplicitParam(name="clientId",value = "用户表id",dataType = "long",paramType = "query",required = true),
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

    @GetMapping("signed_reward")
    @ApiOperation("签到领宝币")
    @ApiImplicitParam(name="clientId",value = "用户表id",dataType = "long",paramType = "query",required = true)
    public Result signedReward(Long clientId) throws ParseException {
        String value = "value";//剩侠宝币的值
        String count = "count";//剩余数量的值

        Map<String, String> signedActivityCoinsNumberInfo = couponForActivityService.getSignedActivityCoinsNumberInfo();
        int bdCount = Integer.parseInt(signedActivityCoinsNumberInfo.get(count));
        BigDecimal dbValue = new BigDecimal(signedActivityCoinsNumberInfo.get(value));

        SignedRewardSpecifyTimeEntity signedParamsById = couponForActivityService.getParamsById(null);
        Date start_pmt = signedParamsById.getStartPmt();
        Date ending_pmt = signedParamsById.getEndingPmt();
        boolean betweenTime = TimeUtil.isBetweenTime(start_pmt, ending_pmt);
        SignedRewardSpecifyTimeVo vo = new SignedRewardSpecifyTimeVo();
        vo.setSignedRewardSpecifyTimeEntity(signedParamsById);

        if(!betweenTime){
            vo.setRewardValue(new BigDecimal("0"));
            vo.setComment("今日活动已结束，请明天再来吧！");
            return new Result().ok(vo);
        }

        Boolean rBoolean = couponForActivityService.clientCheckForSignedForReward(clientId);//客户参加本活动次数是否未超限
        if(rBoolean){
            if(bdCount>0 && dbValue.doubleValue()>0){
                BigDecimal randomCoins = SharingActivityRandomUtil.getRandomCoins(dbValue, bdCount);
                if(bdCount == 1)
                    randomCoins = dbValue;
                vo.setRewardValue(randomCoins);
                vo.setComment("恭喜获得"+randomCoins+"宝币！");
                try{
                    couponForActivityService.insertClientActivityRecord(clientId,randomCoins,3);
                    return new Result().ok(vo);
                }catch (Exception e){
                    e.printStackTrace();
                    vo.setComment("服务器忙，请稍候重试！");
                    return new Result().ok(vo);
                }
            }else{
                vo.setRewardValue(new BigDecimal("0"));
                vo.setComment("红包已经抢完了，下次要点来！");
                return new Result().ok(vo);
            }

        }else{
            vo.setRewardValue(new BigDecimal("0"));
            vo.setComment("已经参与过本活动了，下次再来吧！");
            return new Result().ok(vo);
        }

    }

    @GetMapping("sr_info")
    @ApiOperation("签到领宝币信息")
    public Result signedRewardInfo() throws ParseException {
        SignedRewardSpecifyTimeEntity signedParamsById = couponForActivityService.getParamsById(null);
        return new Result().ok(signedParamsById);
    }

    @GetMapping("is_ontime")
    @ApiOperation("是否活动中")
    public Result isOnTime() throws ParseException {
        SignedRewardSpecifyTimeEntity signedParamsById = couponForActivityService.getParamsById(null);
        Date start_pmt = signedParamsById.getStartPmt();
        Date ending_pmt = signedParamsById.getEndingPmt();
        boolean betweenTime = TimeUtil.isBetweenTime(start_pmt, ending_pmt);
        Result result = new Result();
        if(betweenTime){
            result.setMsg("inprocess");
            result.setCode(200);
            result.setData(1);
            return result;
        }
        result.setMsg("expire");
        result.setCode(200);
        result.setData(-1);
        return result;
    }

//======================================================================================================================


}

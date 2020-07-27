package io.treasure.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import io.treasure.common.constant.Constant;
import io.treasure.common.utils.Result;
import io.treasure.dao.DistributionRewardLogDao;
import io.treasure.entity.ClientUserEntity;
import io.treasure.entity.DistributionRewardLogEntity;
import io.treasure.service.ClientUserService;
import io.treasure.service.ClientUserVersionService;
import io.treasure.service.impl.DistributionRewardServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/distribution_reward")
@Api(tags="分销")
public class DistributionRewardController {

    @Autowired
    private DistributionRewardServiceImpl distributionRewardService;
    @Autowired
    private DistributionRewardLogDao distributionRewardLogDao;
    @Autowired
    private ClientUserService clientUserService;
    @GetMapping("/reward")
    @ApiOperation("分销奖励")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "mobile", value = "消费者手机号", paramType = "query", required = true, dataType="String"),
            @ApiImplicitParam(name = "paytotal", value = "实付金额", paramType = "query",required = true, dataType="Integer"),
    })
    public Result testVersion(String mobile, int paytotal) {

        if (distributionRewardService.distribution(mobile, paytotal)) {
            return new Result().error("failure");
        }

        return new Result().ok("success");
    }
    @GetMapping("/rewardList")
    @ApiOperation("分销奖励列表")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "userId", value = "用户id", paramType = "query", required = true, dataType="Long")
    })
    public Result rewardList(Long userId) {

        ClientUserEntity clientUserEntity = clientUserService.selectById(userId);
        if (clientUserEntity==null){
            return new Result().error("没有该用户，请稍后再试");
        }
        List<DistributionRewardLogEntity> distributionRewardLogEntities = distributionRewardLogDao.selectByMaterMobile(clientUserEntity.getMobile());

        return new Result().ok(distributionRewardLogEntities);
    }
}

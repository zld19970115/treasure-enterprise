package io.treasure.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import io.treasure.common.constant.Constant;
import io.treasure.common.utils.Result;
import io.treasure.service.ClientUserVersionService;
import io.treasure.service.impl.DistributionRewardServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/distribution_reward")
@Api(tags="分销")
public class DistributionRewardController {

    @Autowired
    private DistributionRewardServiceImpl distributionRewardService;

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

}

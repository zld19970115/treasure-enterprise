package io.treasure.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import io.treasure.annotation.Login;
import io.treasure.common.constant.Constant;
import io.treasure.common.page.PageData;
import io.treasure.common.utils.Result;
import io.treasure.dto.*;
import io.treasure.entity.ActivityEntity;
import io.treasure.service.ActivityService;
import io.treasure.vo.ActivityRartakeVo;
import io.treasure.vo.ActivityRewardVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

import javax.servlet.http.HttpServletRequest;
import java.util.Arrays;
import java.util.Map;

@RestController
@RequestMapping("/activity")
@Api(tags="平台活动")
public class ActivityController {

    @Autowired
    private ActivityService activityService;

    //@Login
    @GetMapping("page")
    @ApiOperation("分页查询")
    @ApiImplicitParams({
            @ApiImplicitParam(name = Constant.PAGE, value = "当前页码，从1开始", paramType = "query", required = true, dataType="int") ,
            @ApiImplicitParam(name = Constant.LIMIT, value = "每页显示记录数", paramType = "query",required = true, dataType="int") ,
            @ApiImplicitParam(name = "statrDate", value = "开始time", paramType = "query",dataType="String") ,
            @ApiImplicitParam(name = "endDate", value = "结束time", paramType = "query",dataType="String")
    })
    public Result<PageData<ActivityEntity>> page(@ApiIgnore @RequestParam Map<String, Object> params) {
        return new Result<PageData<ActivityEntity>>().ok(activityService.activityPage(params));
    }

    @Login
    @GetMapping("del")
    @ApiOperation("删除")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "id", paramType = "query",dataType="String") ,
    })
    public Result<Integer> del(@ApiIgnore @RequestParam Long id) {
        return new Result<Integer>().ok(activityService.del(id));
    }

    @Login
    @PostMapping("update")
    @ApiOperation("更新")
    public Result<Integer> update(@RequestBody ActivityDto dto) {
        if(activityService.update(dto) == 200) {
            return new Result<Integer>().ok(0);
        }
        return new Result<Integer>().error(1);
    }

    @Login
    @PostMapping("insert")
    @ApiOperation("新增")
    public Result<Integer> insert(@RequestBody ActivityDto dto) {
        if(activityService.insert(dto) == 200) {
            return new Result<Integer>().ok(0);
        }
        return new Result<Integer>().error(1);
    }

    @Login
    @PostMapping("receiveGift")
    @ApiOperation("活动奖励获取")
    public Result<String> receiveGift(@RequestBody ReceiveGiftDto dto, HttpServletRequest request) {
        dto.setToken(request.getHeader("token"));
        int code = activityService.receiveGift(dto);
        if(code == 200) {
            return new Result<String>().ok("领取成功");//显示领取了多少
        } else if(code == 0) {
            return new Result<String>().error("参数错误");
        } else if(code == 1) {
            return new Result<String>().error("请登录");
        } else if(code == 2) {
            return new Result<String>().error("活动暂未开始");
        } else if(code == 3) {
            return new Result<String>().error("活动以结束");
        } else if(code == 4) {
            return new Result<String>().error("奖品已经领取完了");
        } else if(code == 5) {
            return new Result<String>().error("您已参加过本次活动");
        }
        return new Result<String>().error("系统繁忙");
    }

    //尽量保持原接口不动，所以重新copy了一份新的接口
    @Login
    @PostMapping("receiveGiftCopy")
    @ApiOperation("活动奖励获取copy")
    public Result<String> receiveGiftCopy(@RequestBody ReceiveGiftDto dto, HttpServletRequest request) {
        dto.setToken(request.getHeader("token"));
        ActivityRewardVo activityRewardVo = activityService.receiveGiftCopy(dto);

        int code = activityRewardVo.getCode();
        if(code == 200) {
            return new Result<String>().ok("成功领取代付金"+activityRewardVo.getReward()+"元");//显示领取了多少
        } else if(code == 0) {
            return new Result<String>().error("参数错误");
        } else if(code == 1) {
            return new Result<String>().error("请登录");
        } else if(code == 2) {
            return new Result<String>().error("活动暂未开始");
        } else if(code == 3) {
            return new Result<String>().error("活动以结束");
        } else if(code == 4) {
            return new Result<String>().error("奖品已经领取完了");
        } else if(code == 5) {
            return new Result<String>().error("您已参加过本次活动");
        }
        return new Result<String>().error("系统繁忙");
    }

    @Login
    @GetMapping("selectById")
    @ApiOperation("根据id查询")
    public ActivityDto selectById(Long id) {
        return activityService.selectById(id);
    }


    @Login
    @PostMapping("activityRartake")
    @ApiOperation("是否参加过活动")
    public Result<ActivityRartakeVo> activityRartake(@RequestBody ActivityRartakeDto dto, HttpServletRequest request) {
        dto.setToken(request.getHeader("token"));
        return activityService.activityRartake(dto);
    }

    //@Login
    @GetMapping("hot")
    @ApiOperation("获取热推活动")
    public Result<ActivityRartakeVo> hot(HttpServletRequest request) {
        return activityService.hot(request.getHeader("token"));
    }

    @Login
    @GetMapping("hot_bk")
    @ApiOperation("获取热推活动")
    public Result<ActivityRartakeVo> hot_bk(HttpServletRequest request) {
        return activityService.hot_bk(request.getHeader("token"));
    }

    @Login
    @PostMapping("updateHot")
    @ApiOperation("更新热推活动")
    public Result<String> updateHot(@RequestBody UpdateHotDto dto) {
        return activityService.updateHot(dto);
    }

}

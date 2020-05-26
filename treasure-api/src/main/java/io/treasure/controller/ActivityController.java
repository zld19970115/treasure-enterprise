package io.treasure.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import io.treasure.annotation.Login;
import io.treasure.common.constant.Constant;
import io.treasure.common.page.PageData;
import io.treasure.common.utils.Result;
import io.treasure.dto.ActivityDto;
import io.treasure.dto.NewsDto;
import io.treasure.dto.ReceiveGiftDto;
import io.treasure.entity.ActivityEntity;
import io.treasure.service.ActivityService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

import java.util.Arrays;
import java.util.Map;

@RestController
@RequestMapping("/activity")
@Api(tags="平台活动")
public class ActivityController {

    @Autowired
    private ActivityService activityService;

    @Login
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
    public Result<String> receiveGift(@RequestBody ReceiveGiftDto dto) {
        int code = activityService.receiveGift(dto);
        if(code == 200) {
            return new Result<String>().ok("领取成功");
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
        }
        return new Result<String>().error("系统繁忙");
    }

    @Login
    @GetMapping("selectById")
    @ApiOperation("根据id查询")
    public ActivityDto selectById(Long id) {
        return activityService.selectById(id);
    }

}

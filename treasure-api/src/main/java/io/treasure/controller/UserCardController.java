package io.treasure.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import io.treasure.annotation.Login;
import io.treasure.common.constant.Constant;
import io.treasure.common.page.PageData;
import io.treasure.common.utils.Result;
import io.treasure.dto.CardInfoDTO;
import io.treasure.dto.CardMakeDTO;
import io.treasure.entity.CardMakeEntity;
import io.treasure.service.CardMakeService;
import io.treasure.service.impl.UserCardServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

import java.util.List;
import java.util.Map;


@RestController
@RequestMapping("/userCard")
@Api(tags="用户赠送金表")
public class UserCardController {
    @Autowired
    private UserCardServiceImpl userCardService;
    @Autowired
    private CardMakeService cardMakeService;
    @Login
    @GetMapping("/goCard")
    @ApiOperation("用户充值赠送金表")
    public Result selectMartCoupon(@RequestParam long userId, @RequestParam long id , @RequestParam String password){

        Result result = userCardService.selectByIdAndPassword(id, password, userId);


        return new Result().ok(result);
    }

    @GetMapping("/makePageList")
    @ApiOperation("列表PC")
    @ApiImplicitParams({
            @ApiImplicitParam(name = Constant.PAGE, value = "当前页码，从1开始", paramType = "query", required = true, dataType="int") ,
            @ApiImplicitParam(name = Constant.LIMIT, value = "每页显示记录数", paramType = "query",required = true, dataType="int") ,
            @ApiImplicitParam(name = "startDate", value = "开始time", paramType = "query",dataType="String") ,
            @ApiImplicitParam(name = "endDate", value = "结束time", paramType = "query",dataType="String")
    })
    public Result makePageList(@ApiIgnore @RequestParam Map<String, Object> params) {
        return new Result<PageData<CardMakeDTO>>().ok(cardMakeService.pageList(params));
    }

    @GetMapping("/pageList")
    @ApiOperation("列表PC")
    @ApiImplicitParams({
            @ApiImplicitParam(name = Constant.PAGE, value = "当前页码，从1开始", paramType = "query", required = true, dataType="int") ,
            @ApiImplicitParam(name = Constant.LIMIT, value = "每页显示记录数", paramType = "query",required = true, dataType="int") ,
            @ApiImplicitParam(name = "startDate", value = "开始time", paramType = "query",dataType="String") ,
            @ApiImplicitParam(name = "endDate", value = "结束time", paramType = "query",dataType="String")
    })
    public Result pageList(@ApiIgnore @RequestParam Map<String, Object> params) {
        return new Result<PageData<CardInfoDTO>>().ok(userCardService.pageList(params));
    }

    @PostMapping("/makeCard")
    @ApiOperation("制卡")
    public Result makeCard(@RequestBody CardMakeEntity dto) {
        return cardMakeService.makeCard(dto);
    }

    @GetMapping("/openCard")
    @ApiOperation("开卡")
    public Result openCard(@RequestParam List<Long> ids) {
        return new Result().ok(userCardService.openCard(ids));
    }

}

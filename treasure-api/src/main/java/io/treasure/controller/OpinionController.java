package io.treasure.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import io.treasure.annotation.Login;
import io.treasure.common.constant.Constant;
import io.treasure.common.page.PageData;
import io.treasure.common.utils.Result;
import io.treasure.enm.Common;
import io.treasure.service.OpinionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import springfox.documentation.annotations.ApiIgnore;

import java.util.Map;

@RestController
@RequestMapping("/opinion")
@Api(tags="意见反馈表")
public class OpinionController {
    @Autowired
    private OpinionService opinionService;

    @Login
    @GetMapping("insertOpinion")
    @ApiOperation("添加意见反馈表")
    @ApiImplicitParams({
            @ApiImplicitParam(name="type",value="类型 0---用户 1---商家",paramType ="query",required = true,dataType = "int"),
            @ApiImplicitParam(name="creator",value="创建者 商户id 或者 用户id",paramType ="query",required = true,dataType = "long"),
            @ApiImplicitParam(name = "messageBoard", value = "留言板内容", paramType = "query", required = true, dataType="String"),
            @ApiImplicitParam(name = "messageImg", value = "反馈图片", paramType = "query", required = true, dataType="String")
    })
    public Result insertOpinion(@ApiIgnore @RequestParam Map<String, Object> params){
        opinionService.insertOpinion(params);
        return new Result().ok("添加成功");
    }

}

package io.treasure.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import io.treasure.annotation.Login;
import io.treasure.common.constant.Constant;
import io.treasure.common.page.PageData;
import io.treasure.common.utils.Result;
import io.treasure.entity.AppviewEntity;
import io.treasure.service.AppviewService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

import java.util.Map;

@RestController
@RequestMapping("/appview")
@Api(tags="app关联页")
public class AppviewController {

    @Autowired
    private AppviewService service;

    @Login
    @GetMapping("page")
    @ApiOperation("分页查询")
    @ApiImplicitParams({
            @ApiImplicitParam(name = Constant.PAGE, value = "当前页码，从1开始", paramType = "query", required = true, dataType="int") ,
            @ApiImplicitParam(name = Constant.LIMIT, value = "每页显示记录数", paramType = "query",required = true, dataType="int")
    })
    public Result<PageData<AppviewEntity>> page(@ApiIgnore @RequestParam Map<String, Object> params) {
        return new Result<PageData<AppviewEntity>>().ok(service.pageList(params));
    }

    @Login
    @GetMapping("del")
    @ApiOperation("删除")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "id", paramType = "query",dataType="long") ,
    })
    public Result<String> del(@ApiIgnore @RequestParam Long id) {
        service.deleteById(id);
        return new Result<String>().ok("ok");
    }

    @Login
    @PostMapping("add")
    @ApiOperation("保存")
    public Result<String> add(@RequestBody AppviewEntity obj) {
        service.insert(obj);
        return new Result<String>().ok("ok");
    }

}

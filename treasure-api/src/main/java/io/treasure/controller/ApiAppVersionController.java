package io.treasure.controller;

import io.treasure.annotation.Login;
import io.treasure.dto.AppVersionDTO;
import io.treasure.service.AppVersionService;


import io.treasure.common.constant.Constant;
import io.treasure.common.page.PageData;

import io.treasure.common.utils.Result;
import io.treasure.common.validator.AssertUtils;
import io.treasure.common.validator.ValidatorUtils;
import io.treasure.common.validator.group.AddGroup;
import io.treasure.common.validator.group.DefaultGroup;
import io.treasure.common.validator.group.UpdateGroup;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

import javax.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.Map;


/**
 * APP版本号表
 *
 * @author Super 63600679@qq.com
 * @since 1.0.0 2019-10-10
 */
@RestController
@RequestMapping("/api/version")
@Api(tags="APP版本号")
public class ApiAppVersionController {
    @Autowired
    private AppVersionService appVersionService;

    @Login
    @GetMapping("page")
    @ApiOperation("分页")
    @ApiImplicitParams({
        @ApiImplicitParam(name = Constant.PAGE, value = "当前页码，从1开始", paramType = "query", required = true, dataType="int") ,
        @ApiImplicitParam(name = Constant.LIMIT, value = "每页显示记录数", paramType = "query",required = true, dataType="int") ,
        @ApiImplicitParam(name = Constant.ORDER_FIELD, value = "排序字段", paramType = "query", dataType="String") ,
        @ApiImplicitParam(name = Constant.ORDER, value = "排序方式，可选值(asc、desc)", paramType = "query", dataType="String")
    })
    public Result<PageData<AppVersionDTO>> page(@ApiIgnore @RequestParam Map<String, Object> params){
        PageData<AppVersionDTO> page = appVersionService.page(params);

        return new Result<PageData<AppVersionDTO>>().ok(page);
    }

    @Login
    @GetMapping("{id}")
    @ApiOperation("信息")
    public Result<AppVersionDTO> get(@PathVariable("id") Long id){
        AppVersionDTO data = appVersionService.get(id);

        return new Result<AppVersionDTO>().ok(data);
    }

    @Login
    @GetMapping("/updateInfo")
    @ApiOperation("获取更新信息")
    @ApiImplicitParams({
            @ApiImplicitParam(name="appId",value="appId",required=true,paramType="query"),
            @ApiImplicitParam(name="version",value="版本号",required=true,paramType="query")
    })
    public Result<AppVersionDTO> getUpdateInfo(String appId,String version){
        AppVersionDTO data = appVersionService.getUpdateInfo(appId,version);

        return new Result<AppVersionDTO>().ok(data);
    }

    @Login
    @PostMapping
    @ApiOperation("保存")
    public Result save(@RequestBody AppVersionDTO dto){
        //效验数据
        ValidatorUtils.validateEntity(dto, AddGroup.class, DefaultGroup.class);

        appVersionService.save(dto);

        return new Result();
    }
    @Login
    @PutMapping
    @ApiOperation("修改")
    public Result update(@RequestBody AppVersionDTO dto){
        //效验数据
        ValidatorUtils.validateEntity(dto, UpdateGroup.class, DefaultGroup.class);

        appVersionService.update(dto);

        return new Result();
    }
    @Login
    @DeleteMapping
    @ApiOperation("删除")
    
    public Result delete(@RequestBody Long[] ids){
        //效验数据
        AssertUtils.isArrayEmpty(ids, "id");

        appVersionService.delete(ids);

        return new Result();
    }
    
    
}
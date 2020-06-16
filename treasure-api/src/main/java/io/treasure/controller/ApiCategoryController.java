package io.treasure.controller;

import io.treasure.annotation.Login;
import io.treasure.dto.ActivityDto;
import io.treasure.dto.CategoryDTO;
import io.treasure.dto.CategoryPageDto;
import io.treasure.enm.Common;
import io.treasure.entity.ActivityEntity;
import io.treasure.entity.CategoryEntity;
import io.treasure.service.CategoryService;


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
import java.util.Date;
import java.util.List;
import java.util.Map;


/**
 * 店铺类型分类表
 *
 * @author Super 63600679@qq.com
 * @since 1.0.0 2019-08-18
 */
@RestController
@RequestMapping("/api/category")
@Api(tags="店铺类型分类表")
public class ApiCategoryController {
    @Autowired
    private CategoryService categoryService;

    @GetMapping("page")
    @ApiOperation("分页")
    @ApiImplicitParams({
        @ApiImplicitParam(name = Constant.PAGE, value = "当前页码，从1开始", paramType = "query", required = true, dataType="int") ,
        @ApiImplicitParam(name = Constant.LIMIT, value = "每页显示记录数", paramType = "query",required = true, dataType="int") ,
        @ApiImplicitParam(name = Constant.ORDER_FIELD, value = "排序字段", paramType = "query", dataType="String") ,
        @ApiImplicitParam(name = Constant.ORDER, value = "排序方式，可选值(asc、desc)", paramType = "query", dataType="String")
    })
    public Result<PageData<CategoryDTO>> page(@ApiIgnore @RequestParam Map<String, Object> params){
        PageData<CategoryDTO> page = categoryService.page(params);

        return new Result<PageData<CategoryDTO>>().ok(page);
    }

    @GetMapping("commendPage")
    @ApiOperation("分类推荐")
    @ApiImplicitParams({
            @ApiImplicitParam(name = Constant.PAGE, value = "当前页码，从1开始", paramType = "query", required = true, dataType="int") ,
            @ApiImplicitParam(name = Constant.LIMIT, value = "每页显示记录数", paramType = "query",required = true, dataType="int") ,
            @ApiImplicitParam(name = Constant.ORDER_FIELD, value = "排序字段", paramType = "query", dataType="String") ,
            @ApiImplicitParam(name = Constant.ORDER, value = "排序方式，可选值(asc、desc)", paramType = "query", dataType="String")
    })
    public Result<PageData<CategoryDTO>> commendPage(@ApiIgnore @RequestParam Map<String, Object> params){
        params.put("showInCommend",1);
        params.put("pid","0");
        PageData<CategoryDTO> page = categoryService.page(params);

        return new Result<PageData<CategoryDTO>>().ok(page);
    }

    @GetMapping("navPage")
    @ApiOperation("导航栏分类信息")
    @ApiImplicitParams({
            @ApiImplicitParam(name = Constant.PAGE, value = "当前页码，从1开始", paramType = "query", required = true, dataType="int") ,
            @ApiImplicitParam(name = Constant.LIMIT, value = "每页显示记录数", paramType = "query",required = true, dataType="int") ,
            @ApiImplicitParam(name = Constant.ORDER_FIELD, value = "排序字段", paramType = "query", dataType="String") ,
            @ApiImplicitParam(name = Constant.ORDER, value = "排序方式，可选值(asc、desc)", paramType = "query", dataType="String")
    })
    public Result<PageData<CategoryDTO>> navPage(@ApiIgnore @RequestParam Map<String, Object> params){
        params.put("showInNav",1);
        params.put("pid","0");
        PageData<CategoryDTO> page = categoryService.page(params);

        return new Result<PageData<CategoryDTO>>().ok(page);
    }

    @Login
    @GetMapping("{id}")
    @ApiOperation("信息")
    public Result<CategoryDTO> get(@PathVariable("id") Long id){
        CategoryDTO data = categoryService.get(id);

        return new Result<CategoryDTO>().ok(data);
    }

    @Login
    @PostMapping
    @ApiOperation("保存")
    public Result save(@RequestBody CategoryDTO dto){
        //效验数据
        ValidatorUtils.validateEntity(dto, AddGroup.class, DefaultGroup.class);

        categoryService.save(dto);

        return new Result();
    }
    @Login
    @PutMapping
    @ApiOperation("修改")
    public Result update(@RequestBody CategoryDTO dto){
        //效验数据
        ValidatorUtils.validateEntity(dto, UpdateGroup.class, DefaultGroup.class);

        categoryService.update(dto);

        return new Result();
    }

    @Login
    @DeleteMapping
    @ApiOperation("删除")
    
    public Result delete(@RequestBody Long[] ids){
        //效验数据
        AssertUtils.isArrayEmpty(ids, "id");

        categoryService.delete(ids);

        return new Result();
    }
    @CrossOrigin
    @GetMapping("categoryOnePage")
    @ApiOperation("一级分类列表")
    @ApiImplicitParams({
            @ApiImplicitParam(name = Constant.PAGE, value = "当前页码，从1开始", paramType = "query", required = true, dataType="int") ,
            @ApiImplicitParam(name = Constant.LIMIT, value = "每页显示记录数", paramType = "query",required = true, dataType="int") ,
            @ApiImplicitParam(name = Constant.ORDER_FIELD, value = "排序字段", paramType = "query", dataType="String") ,
            @ApiImplicitParam(name = Constant.ORDER, value = "排序方式，可选值(asc、desc)", paramType = "query", dataType="String")
    })
    public Result<PageData<CategoryDTO>> categoryOnePage(@ApiIgnore @RequestParam Map<String, Object> params){
        params.put("pid","0");
        params.put("status", Common.values()+"");
        PageData<CategoryDTO> page = categoryService.page(params);
        return new Result<PageData<CategoryDTO>>().ok(page);
    }
    @CrossOrigin
    @GetMapping("categoryTwoPage")
    @ApiOperation("二级分类列表")
    @ApiImplicitParams({
            @ApiImplicitParam(name = Constant.PAGE, value = "当前页码，从1开始", paramType = "query", required = true, dataType="int") ,
            @ApiImplicitParam(name = Constant.LIMIT, value = "每页显示记录数", paramType = "query",required = true, dataType="int") ,
            @ApiImplicitParam(name = Constant.ORDER_FIELD, value = "排序字段", paramType = "query", dataType="String") ,
            @ApiImplicitParam(name = Constant.ORDER, value = "排序方式，可选值(asc、desc)", paramType = "query", dataType="String"),
            @ApiImplicitParam(name="pid",value="分类编号多个用,分割",paramType = "query",dataType = "String",required = true)
    })
    public Result<PageData<CategoryDTO>> categoryTwoPage(@ApiIgnore @RequestParam Map<String, Object> params){
        params.put("status", Common.values()+"");
        PageData<CategoryDTO> page = categoryService.page(params);
        return new Result<PageData<CategoryDTO>>().ok(page);
    }

    @GetMapping("pageList")
    @ApiOperation("分页查询PC")
    @ApiImplicitParams({
            @ApiImplicitParam(name = Constant.PAGE, value = "当前页码，从1开始", paramType = "query", required = true, dataType="int") ,
            @ApiImplicitParam(name = Constant.LIMIT, value = "每页显示记录数", paramType = "query",required = true, dataType="int") ,
            @ApiImplicitParam(name = "startDate", value = "开始time", paramType = "query",dataType="String") ,
            @ApiImplicitParam(name = "endDate", value = "结束time", paramType = "query",dataType="String")
    })
    public Result<PageData<CategoryPageDto>> pageList(@ApiIgnore @RequestParam Map<String, Object> params) {
        return new Result<PageData<CategoryPageDto>>().ok(categoryService.pageList(params));
    }

    @PostMapping("updatePC")
    @ApiOperation("更新PC")
    public Result<Integer> updatePC(@RequestBody CategoryDTO dto) {
        dto.setCreateDate(categoryService.selectById(dto.getId()).getCreateDate());
        dto.setUpdateDate(new Date());
        categoryService.update(dto);
        return new Result();
    }

    @PostMapping("insertPC")
    @ApiOperation("新增PC")
    public Result<Integer> insertPC(@RequestBody CategoryDTO dto) {
        dto.setCreateDate(new Date());
        categoryService.save(dto);
        return new Result();
    }

    @GetMapping("selectById")
    @ApiOperation("查询PC")
    public Result<CategoryEntity> selectById(@RequestParam Long id) {
        return new Result().ok(categoryService.selectById(id));
    }

    @GetMapping("delPC")
    @ApiOperation("删除PC")
    public Result delPC(@RequestParam Long id){
        categoryService.deleteById(id);
        return new Result();
    }

}
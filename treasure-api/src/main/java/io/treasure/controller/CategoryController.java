package io.treasure.controller;


import io.treasure.common.constant.Constant;
import io.treasure.common.page.PageData;
import io.treasure.common.utils.Result;
import io.treasure.common.validator.AssertUtils;
import io.treasure.common.validator.ValidatorUtils;
import io.treasure.common.validator.group.AddGroup;
import io.treasure.common.validator.group.DefaultGroup;
import io.treasure.common.validator.group.UpdateGroup;
import io.treasure.dto.CategoryDTO;
import io.treasure.enm.Common;
import io.treasure.entity.CategoryEntity;
import io.treasure.service.CategoryService;
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
 * @since 1.0.0 2019-07-24
 */
@RestController
@RequestMapping("/category")
@Api(tags="店铺类型分类表")
public class CategoryController {
    @Autowired
    private CategoryService categoryService;

    @GetMapping("pageOn")
    @ApiOperation("显示中数据")
    @ApiImplicitParams({
        @ApiImplicitParam(name = Constant.PAGE, value = "1", paramType = "query", required = true, dataType="int") ,
        @ApiImplicitParam(name = Constant.LIMIT, value = "10", paramType = "query",required = true, dataType="int") ,
        @ApiImplicitParam(name = Constant.ORDER_FIELD, value = "id", paramType = "query", dataType="String") ,
        @ApiImplicitParam(name = Constant.ORDER, value = "desc", paramType = "query", dataType="String")
    })
    public Result<PageData<CategoryDTO>> pageOn(@ApiIgnore @RequestParam Map<String, Object> params,String name){
        params.put("status", String.valueOf(Common.STATUS_ON.getStatus()));
        params.put("name",name);
        PageData<CategoryDTO> page = categoryService.page(params);
        return new Result<PageData<CategoryDTO>>().ok(page);
    }
    @GetMapping("pageOff")
    @ApiOperation("隐藏中数据")
    @ApiImplicitParams({
            @ApiImplicitParam(name = Constant.PAGE, value = "1", paramType = "query", required = true, dataType="int") ,
            @ApiImplicitParam(name = Constant.LIMIT, value = "2", paramType = "query",required = true, dataType="int") ,
            @ApiImplicitParam(name = Constant.ORDER_FIELD, value = "id", paramType = "query", dataType="String") ,
            @ApiImplicitParam(name = Constant.ORDER, value = "desc", paramType = "query", dataType="String")
    })
    public Result<PageData<CategoryDTO>> pageOff(@ApiIgnore @RequestParam Map<String, Object> params,String name){
        params.put("status", String.valueOf(Common.STATUS_OFF.getStatus()));
        params.put("name",name);
        PageData<CategoryDTO> page = categoryService.page(params);
        return new Result<PageData<CategoryDTO>>().ok(page);
    }
    @GetMapping("{id}")
    @ApiOperation("信息")
    public Result<CategoryDTO> get(@PathVariable("id") Long id){
        CategoryDTO data = categoryService.get(id);
        return new Result<CategoryDTO>().ok(data);
    }

    @PostMapping
    @ApiOperation("保存")
    public Result save(@RequestBody CategoryDTO dto){
        //效验数据
        ValidatorUtils.validateEntity(dto);
        CategoryEntity category=new CategoryEntity();
        category.setBrief(dto.getBrief());
        category.setIcon(dto.getIcon());
        category.setName(dto.getName());
        category.setStatus(dto.getStatus());
        category.setCreateDate(new Date());
        category.setCreator(dto.getCreator());
        category.setSort(dto.getSort());
        categoryService.insert(category);
        return new Result();
    }

    @PutMapping
    @ApiOperation("修改")
    public Result update(@RequestBody CategoryDTO dto){
        //效验数据
        ValidatorUtils.validateEntity(dto);
        CategoryEntity category=new CategoryEntity();
        category.setBrief(dto.getBrief());
        category.setIcon(dto.getIcon());
        category.setName(dto.getName());
        category.setStatus(dto.getStatus());
        category.setUpdateDate(new Date());
        category.setUpdater(dto.getCreator());
        category.setSort(dto.getSort());
        category.setId(dto.getId());
        categoryService.update(category,null);
        return new Result();
    }

    @DeleteMapping
    @ApiOperation("删除")
    public Result delete(@RequestBody Long[] ids){
        //效验数据
        AssertUtils.isArrayEmpty(ids, "id");
        categoryService.delete(ids);
        return new Result();
    }

    /**
     * 显示数据
     * @param id
     * @return
     */
    @PutMapping("on")
    @ApiOperation("显示数据")
    public Result on(@RequestBody Long id){
        if(id>0){
            categoryService.on(id,Common.STATUS_ON.getStatus());
            return new Result();
        }
        return new Result().error("显示数据失败！");
    }

    /**
     * 隐藏数据
     * @param id
     * @return
     */
    @PutMapping("off")
    @ApiOperation("隐藏数据")
    public Result off(@RequestBody Long id){
        if(id>0){
            categoryService.off(id,Common.STATUS_OFF.getStatus());
            return new Result();
        }
        return new Result().error("隐藏数据失败！");
    }
}
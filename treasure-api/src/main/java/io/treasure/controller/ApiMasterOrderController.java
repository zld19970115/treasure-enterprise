package io.treasure.controller;

import io.treasure.annotation.Login;
import io.treasure.dto.MasterOrderDTO;
import io.treasure.service.MasterOrderService;


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
 * 订单表
 *
 * @author Super 63600679@qq.com
 * @since 1.0.0 2019-08-23
 */
@RestController
@RequestMapping("/api/masterOrder")
@Api(tags="订单表")
public class ApiMasterOrderController {
    @Autowired
    private MasterOrderService masterOrderService;

    @Login
    @GetMapping("page")
    @ApiOperation("分页")
    @ApiImplicitParams({
        @ApiImplicitParam(name = Constant.PAGE, value = "当前页码，从1开始", paramType = "query", required = true, dataType="int") ,
        @ApiImplicitParam(name = Constant.LIMIT, value = "每页显示记录数", paramType = "query",required = true, dataType="int") ,
        @ApiImplicitParam(name = Constant.ORDER_FIELD, value = "排序字段", paramType = "query", dataType="String") ,
        @ApiImplicitParam(name = Constant.ORDER, value = "排序方式，可选值(asc、desc)", paramType = "query", dataType="String")
    })
    public Result<PageData<MasterOrderDTO>> page(@ApiIgnore @RequestParam Map<String, Object> params){
        PageData<MasterOrderDTO> page = masterOrderService.page(params);

        return new Result<PageData<MasterOrderDTO>>().ok(page);
    }

    @Login
    @GetMapping("{id}")
    @ApiOperation("信息")
    public Result<MasterOrderDTO> get(@PathVariable("id") Long id){
        MasterOrderDTO data = masterOrderService.get(id);

        return new Result<MasterOrderDTO>().ok(data);
    }

    @Login
    @PostMapping
    @ApiOperation("保存")
    public Result save(@RequestBody MasterOrderDTO dto){
        //效验数据
        ValidatorUtils.validateEntity(dto, AddGroup.class, DefaultGroup.class);

        masterOrderService.save(dto);

        return new Result();
    }
    @Login
    @PutMapping
    @ApiOperation("修改")
    public Result update(@RequestBody MasterOrderDTO dto){
        //效验数据
        ValidatorUtils.validateEntity(dto, UpdateGroup.class, DefaultGroup.class);

        masterOrderService.update(dto);

        return new Result();
    }
        @Login
    @DeleteMapping
    @ApiOperation("删除")
    
    public Result delete(@RequestBody Long[] ids){
        //效验数据
        AssertUtils.isArrayEmpty(ids, "id");

        masterOrderService.delete(ids);

        return new Result();
    }
    
    
}
package io.treasure.controller;


import cn.hutool.db.Page;
import io.treasure.common.constant.Constant;
import io.treasure.common.page.PageData;
import io.treasure.common.utils.Result;
import io.treasure.common.validator.AssertUtils;
import io.treasure.common.validator.ValidatorUtils;
import io.treasure.common.validator.group.AddGroup;
import io.treasure.common.validator.group.DefaultGroup;
import io.treasure.common.validator.group.UpdateGroup;
import io.treasure.dto.MerchantDTO;
import io.treasure.enm.Common;
import io.treasure.service.MerchantService;
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
 * 商户表
 *
 * @author Super 63600679@qq.com
 * @since 1.0.0 2019-07-23
 */
@RestController
@RequestMapping("/merchant")
@Api(tags="商户表")
public class MerchantController {
    @Autowired
    private MerchantService merchantService;

    @GetMapping("page")
    @ApiOperation("分页")
    @ApiImplicitParams({
        @ApiImplicitParam(name = Constant.PAGE, value = "当前页码，从1开始", paramType = "query", required = true, dataType="int") ,
        @ApiImplicitParam(name = Constant.LIMIT, value = "每页显示记录数", paramType = "query",required = true, dataType="int") ,
        @ApiImplicitParam(name = Constant.ORDER_FIELD, value = "排序字段", paramType = "query", dataType="String") ,
        @ApiImplicitParam(name = Constant.ORDER, value = "排序方式，可选值(asc、desc)", paramType = "query", dataType="String")
    })
    public Result<PageData<MerchantDTO>> page(@ApiIgnore @RequestParam Map<String, Object> params){

        PageData<MerchantDTO> page = merchantService.page(params);
        return new Result<PageData<MerchantDTO>>().ok(page);
    }

    @GetMapping("{id}")
    @ApiOperation("信息")
    public Result<MerchantDTO> get(@PathVariable("id") Long id){
        MerchantDTO data = merchantService.get(id);

        return new Result<MerchantDTO>().ok(data);
    }

    @PostMapping
    @ApiOperation("保存")
    public Result save(@RequestBody MerchantDTO dto){
        //效验数据
        ValidatorUtils.validateEntity(dto, AddGroup.class, DefaultGroup.class);
        dto.setStatus(Common.STATUS_ON.getStatus());
        dto.setCreateDate(new Date());
        merchantService.save(dto);

        return new Result();
    }

    @PutMapping
    @ApiOperation("修改")
    public Result update(@RequestBody MerchantDTO dto){
        //效验数据
        ValidatorUtils.validateEntity(dto, UpdateGroup.class, DefaultGroup.class);
        dto.setStatus(Common.STATUS_ON.getStatus());
        dto.setUpdateDate(new Date());
        merchantService.update(dto);

        return new Result();
    }

    @DeleteMapping
    @ApiOperation("删除")
    public Result delete(@RequestBody Long id){
       if(id>0){
           merchantService.remove(id);
       }else{
           return new Result().error("删除失败！");
       }
        return new Result();
    }
}
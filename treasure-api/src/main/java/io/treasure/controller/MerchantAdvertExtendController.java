package io.treasure.controller;


import io.treasure.annotation.Login;
import io.treasure.common.constant.Constant;
import io.treasure.common.page.PageData;
import io.treasure.common.utils.Result;
import io.treasure.common.validator.AssertUtils;
import io.treasure.common.validator.ValidatorUtils;
import io.treasure.common.validator.group.AddGroup;
import io.treasure.common.validator.group.DefaultGroup;
import io.treasure.common.validator.group.UpdateGroup;
import io.treasure.dto.MerchantAdvertExtendDTO;

import io.treasure.enm.Common;
import io.treasure.service.MerchantAdvertExtendService;
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
 * 商户广告位推广
 *
 * @author Super 63600679@qq.com
 * @since 1.0.0 2019-08-23
 */
@RestController
@RequestMapping("/merchantadvertextend")
@Api(tags="商户广告位推广")
public class MerchantAdvertExtendController {
    @Autowired
    private MerchantAdvertExtendService merchantAdvertExtendService;
    @Login
    @GetMapping("allPage")
    @ApiOperation("全部列表")
    @ApiImplicitParams({
        @ApiImplicitParam(name = Constant.PAGE, value = "当前页码，从1开始", paramType = "query", required = true, dataType="int") ,
        @ApiImplicitParam(name = Constant.LIMIT, value = "每页显示记录数", paramType = "query",required = true, dataType="int") ,
        @ApiImplicitParam(name = Constant.ORDER_FIELD, value = "排序字段", paramType = "query", dataType="String") ,
        @ApiImplicitParam(name = Constant.ORDER, value = "排序方式，可选值(asc、desc)", paramType = "query", dataType="String")
    })
    public Result<PageData<MerchantAdvertExtendDTO>> allPage(@ApiIgnore @RequestParam Map<String, Object> params){
        params.put("status", Common.STATUS_ON.getStatus()+"");
        PageData<MerchantAdvertExtendDTO> page = merchantAdvertExtendService.page(params);
        return new Result<PageData<MerchantAdvertExtendDTO>>().ok(page);
    }
    @Login
    @GetMapping("page")
    @ApiOperation("列表")
    @ApiImplicitParams({
            @ApiImplicitParam(name = Constant.PAGE, value = "当前页码，从1开始", paramType = "query", required = true, dataType="int") ,
            @ApiImplicitParam(name = Constant.LIMIT, value = "每页显示记录数", paramType = "query",required = true, dataType="int") ,
            @ApiImplicitParam(name = Constant.ORDER_FIELD, value = "排序字段", paramType = "query", dataType="String") ,
            @ApiImplicitParam(name = Constant.ORDER, value = "排序方式，可选值(asc、desc)", paramType = "query", dataType="String"),
            @ApiImplicitParam(name ="merchantId", value ="商户编号", paramType = "query",required = true,  dataType="long")
    })
    public Result<PageData<MerchantAdvertExtendDTO>> page(@ApiIgnore @RequestParam Map<String, Object> params){
        params.put("status", Common.STATUS_ON.getStatus()+"");
        PageData<MerchantAdvertExtendDTO> page = merchantAdvertExtendService.page(params);
        return new Result<PageData<MerchantAdvertExtendDTO>>().ok(page);
    }
    @Login
    @GetMapping("getByInfo")
    @ApiOperation("信息")
    @ApiImplicitParams({
            @ApiImplicitParam(name ="id",value = "编号", paramType = "query", required = true, dataType="long")
    })
    public Result<MerchantAdvertExtendDTO> get(Long id){
        MerchantAdvertExtendDTO data = merchantAdvertExtendService.get(id);
        return new Result<MerchantAdvertExtendDTO>().ok(data);
    }
    @Login
    @PostMapping("save")
    @ApiOperation("保存")
    public Result save(@RequestBody MerchantAdvertExtendDTO dto){
        //效验数据
        ValidatorUtils.validateEntity(dto, AddGroup.class);
        dto.setCreateDate(new Date());
        merchantAdvertExtendService.save(dto);
        return new Result();
    }
    @Login
    @PutMapping("update")
    @ApiOperation("修改")
    public Result update(@RequestBody MerchantAdvertExtendDTO dto){
        //效验数据
        ValidatorUtils.validateEntity(dto, UpdateGroup.class);
        merchantAdvertExtendService.update(dto);
        return new Result();
    }

}
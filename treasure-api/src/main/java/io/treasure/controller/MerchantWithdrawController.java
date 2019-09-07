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
import io.treasure.dto.MerchantWithdrawDTO;
import io.treasure.enm.Common;
import io.treasure.enm.WithdrawEnm;
import io.treasure.service.MerchantWithdrawService;
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
 * 提现表
 *
 * @author Super 63600679@qq.com
 * @since 1.0.0 2019-08-20
 */
@RestController
@RequestMapping("/merchantwithdraw")
@Api(tags="提现管理")
public class MerchantWithdrawController {
    @Autowired
    private MerchantWithdrawService merchantWithdrawService;
    @Login
    @GetMapping("allPage")
    @ApiOperation("全部列表")
    @ApiImplicitParams({
        @ApiImplicitParam(name = Constant.PAGE, value = "当前页码，从1开始", paramType = "query", required = true, dataType="int") ,
        @ApiImplicitParam(name = Constant.LIMIT, value = "每页显示记录数", paramType = "query",required = true, dataType="int") ,
        @ApiImplicitParam(name = Constant.ORDER_FIELD, value = "排序字段", paramType = "query", dataType="String") ,
        @ApiImplicitParam(name = Constant.ORDER, value = "排序方式，可选值(asc、desc)", paramType = "query", dataType="String"),
            @ApiImplicitParam(name="merchantId",value="商户编号",paramType = "query",required = true,dataType = "long")

    })
    public Result<PageData<MerchantWithdrawDTO>> allPage(@ApiIgnore @RequestParam Map<String, Object> params){
        params.put("status", Common.STATUS_ON.getStatus()+"");
        PageData<MerchantWithdrawDTO> page = merchantWithdrawService.page(params);
        return new Result<PageData<MerchantWithdrawDTO>>().ok(page);
    }
    @Login
    @GetMapping("agreePage")
    @ApiOperation("同意提现列表")
    @ApiImplicitParams({
            @ApiImplicitParam(name = Constant.PAGE, value = "当前页码，从1开始", paramType = "query", required = true, dataType="int") ,
            @ApiImplicitParam(name = Constant.LIMIT, value = "每页显示记录数", paramType = "query",required = true, dataType="int") ,
            @ApiImplicitParam(name = Constant.ORDER_FIELD, value = "排序字段", paramType = "query", dataType="String") ,
            @ApiImplicitParam(name = Constant.ORDER, value = "排序方式，可选值(asc、desc)", paramType = "query", dataType="String"),
            @ApiImplicitParam(name="merchantId",value="商户编号",paramType = "query",required = true,dataType = "long")
    })
    public Result<PageData<MerchantWithdrawDTO>> agreePage(@ApiIgnore @RequestParam Map<String, Object> params){
        params.put("status", Common.STATUS_ON.getStatus()+"");
        params.put("verifyState", WithdrawEnm.STATUS_AGREE_YES.getStatus()+"");
        PageData<MerchantWithdrawDTO> page = merchantWithdrawService.page(params);
        return new Result<PageData<MerchantWithdrawDTO>>().ok(page);
    }
    @Login
    @GetMapping("agreeNoPage")
    @ApiOperation("拒绝提现列表")
    @ApiImplicitParams({
            @ApiImplicitParam(name = Constant.PAGE, value = "当前页码，从1开始", paramType = "query", required = true, dataType="int") ,
            @ApiImplicitParam(name = Constant.LIMIT, value = "每页显示记录数", paramType = "query",required = true, dataType="int") ,
            @ApiImplicitParam(name = Constant.ORDER_FIELD, value = "排序字段", paramType = "query", dataType="String") ,
            @ApiImplicitParam(name = Constant.ORDER, value = "排序方式，可选值(asc、desc)", paramType = "query", dataType="String"),
            @ApiImplicitParam(name="merchantId",value="商户编号",paramType = "query",required = true,dataType = "long")
    })
    public Result<PageData<MerchantWithdrawDTO>> agreeNoPage(@ApiIgnore @RequestParam Map<String, Object> params){
        params.put("status", Common.STATUS_ON.getStatus()+"");
        params.put("verifyState", WithdrawEnm.STATUS_AGREE_NO.getStatus()+"");
        PageData<MerchantWithdrawDTO> page = merchantWithdrawService.page(params);
        return new Result<PageData<MerchantWithdrawDTO>>().ok(page);
    }
    @Login
    @GetMapping("getInfo")
    @ApiOperation("详细信息")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "编号", paramType = "query", required = true, dataType="Long")
    })
    public Result<MerchantWithdrawDTO> get(@RequestParam Long id){
        MerchantWithdrawDTO data = merchantWithdrawService.get(id);
        return new Result<MerchantWithdrawDTO>().ok(data);
    }
    @Login
    @PostMapping("save")
    @ApiOperation("申请提现")
    public Result save(@RequestBody MerchantWithdrawDTO dto){
        //效验数据
        ValidatorUtils.validateEntity(dto, AddGroup.class);
        dto.setCreateDate(new Date());
        dto.setStatus(Common.STATUS_ON.getStatus());
        dto.setVerifyState(WithdrawEnm.STATUS_NO.getStatus());
        dto.setType(WithdrawEnm.TYPE_WEIXIN.getStatus());
        dto.setWay(WithdrawEnm.WAY_HAND.getStatus());
        merchantWithdrawService.save(dto);
        return new Result();
    }

    @Login
    @DeleteMapping("remove")
    @ApiOperation("删除")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "编号", paramType = "query", required = true, dataType="long")
    })
    public Result delete(@RequestParam long id){
        merchantWithdrawService.updateStatusById(id,Common.STATUS_DELETE.getStatus());
        return new Result();
    }
}
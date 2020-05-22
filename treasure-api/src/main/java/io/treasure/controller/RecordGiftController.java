package io.treasure.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import io.treasure.common.constant.Constant;
import io.treasure.common.page.PageData;
import io.treasure.common.utils.Result;
import io.treasure.dto.ChargeCashDTO;
import io.treasure.dto.ClientUserDTO;
import io.treasure.dto.MerchantWithdrawDTO;
import io.treasure.dto.RecordGiftDTO;
import io.treasure.service.ClientUserService;
import io.treasure.service.RecordGiftService;
import io.treasure.service.impl.MerchantServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

import java.math.BigDecimal;
import java.util.Date;
import java.util.Map;

/**
 * 充值表
 */
@RestController
@RequestMapping("/recordgift")
@Api(tags="充值记录")
public class RecordGiftController {
    @Autowired
    private RecordGiftService recordGiftService;
    @Autowired
    private ClientUserService clientUserService;

    @GetMapping("/insertRecordGiftAdmin")
    @ApiOperation("后台赠送代付金")
    @ApiImplicitParams({
            @ApiImplicitParam(name = Constant.PAGE, value = "当前页码，从1开始", paramType = "query", required = true, dataType="int") ,
            @ApiImplicitParam(name = Constant.LIMIT, value = "每页显示记录数", paramType = "query",required = true, dataType="int") ,
            @ApiImplicitParam(name = Constant.ORDER_FIELD, value = "排序字段", paramType = "query", dataType="String") ,
            @ApiImplicitParam(name = Constant.ORDER, value = "排序方式，可选值(asc、desc)", paramType = "query", dataType="String"),
            @ApiImplicitParam(name = "userId", value = "用户id", paramType = "query", dataType="String"),
            @ApiImplicitParam(name = "transferredMobile", value = "用户手机号", paramType = "query", dataType="String"),
            @ApiImplicitParam(name = "useGift", value = "代付金数量", paramType = "query", dataType="String"),
            @ApiImplicitParam(name = "creator", value = "管理员id", paramType = "query", dataType="String"),
    })
    public Result insertRecordGiftAdmin(@ApiIgnore @RequestParam Map<String,Object> params){
        Date date = new Date();
        params.put("createDate",date);
        int i = recordGiftService.insertRecordGiftAdmin(params);
        if (i==1){
            //更新用户表里的代付金余额
            clientUserService.addRecordGiftByUserid((String) params.get("userId"), (String) params.get("useGift"));
            return new Result().ok("赠送成功");
        }else {
            return new Result().error("赠送失败");
        }

    }

    @PostMapping("/getAllRecordGoht")
    @ApiOperation("全部代付金充值记录")
    @ApiImplicitParams({
            @ApiImplicitParam(name = Constant.PAGE, value = "当前页码，从1开始", paramType = "query", required = true, dataType="int") ,
            @ApiImplicitParam(name = Constant.LIMIT, value = "每页显示记录数", paramType = "query",required = true, dataType="int") ,
            @ApiImplicitParam(name = Constant.ORDER_FIELD, value = "排序字段", paramType = "query", dataType="String") ,
            @ApiImplicitParam(name = Constant.ORDER, value = "排序方式，可选值(asc、desc)", paramType = "query", dataType="String"),
    })
    public Result getAllRecordGoht(@ApiIgnore @RequestParam Map<String, Object> params){
        PageData<RecordGiftDTO> page = recordGiftService.getAllRecordGoht(params);
        return new Result().ok(page);
    }

    @GetMapping("/getRecordUserAll")
    @ApiOperation("根据手机号模糊查询用户")
    @ApiImplicitParams({
            @ApiImplicitParam(name = Constant.PAGE, value = "当前页码，从1开始", paramType = "query", required = true, dataType="int") ,
            @ApiImplicitParam(name = Constant.LIMIT, value = "每页显示记录数", paramType = "query",required = true, dataType="int") ,
            @ApiImplicitParam(name = Constant.ORDER_FIELD, value = "排序字段", paramType = "query", dataType="String") ,
            @ApiImplicitParam(name = Constant.ORDER, value = "排序方式，可选值(asc、desc)", paramType = "query", dataType="String"),
            @ApiImplicitParam(name = "mobile", value = "用户手机号", paramType = "query", dataType="String"),
    })
    public Result getRecordUserAll(@ApiIgnore @RequestParam Map<String, Object> params){
        PageData<ClientUserDTO> page = clientUserService.getRecordUserAll(params);
        return new Result().ok(page);
    }


    @GetMapping("getRecordGiftByUserId")
    @ApiOperation("根据手机号/日期查询后台代付金充值记录")
    @ApiImplicitParams({
            @ApiImplicitParam(name = Constant.PAGE, value = "当前页码，从1开始", paramType = "query", required = true, dataType="int") ,
            @ApiImplicitParam(name = Constant.LIMIT, value = "每页显示记录数", paramType = "query",required = true, dataType="int") ,
            @ApiImplicitParam(name = Constant.ORDER_FIELD, value = "排序字段", paramType = "query", dataType="String") ,
            @ApiImplicitParam(name = Constant.ORDER, value = "排序方式，可选值(asc、desc)", paramType = "query", dataType="String"),
            @ApiImplicitParam(name = "transferredMobile", value = "手机号", paramType = "query", dataType="String"),
            @ApiImplicitParam(name = "createDateTop", value = "记录开始日期", paramType = "query", dataType="String"),
            @ApiImplicitParam(name = "createDateDown", value = "记录截止日期", paramType = "query", dataType="String"),
            @ApiImplicitParam(name = "status", value = "状态类型", paramType = "query", dataType="int"),
    })
    public Result<PageData<RecordGiftDTO>> getRecordGiftByUserId(@ApiIgnore @RequestParam Map<String, Object> params){
        PageData<RecordGiftDTO> page = recordGiftService.getRecordGiftByUserId(params);
        return new Result<PageData<RecordGiftDTO>>().ok(page);
    }



}

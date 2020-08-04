package io.treasure.controller;

import com.alipay.api.AlipayApiException;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import io.treasure.annotation.Login;
import io.treasure.common.constant.Constant;
import io.treasure.common.exception.RenException;
import io.treasure.common.page.PageData;
import io.treasure.common.sms.SMSConfig;
import io.treasure.common.utils.Result;
import io.treasure.common.validator.ValidatorUtils;
import io.treasure.common.validator.group.AddGroup;
import io.treasure.dto.MerchantWithdrawDTO;
import io.treasure.dto.UserWithdrawDTO;
import io.treasure.enm.Common;
import io.treasure.enm.WithdrawEnm;
import io.treasure.entity.ClientUserEntity;
import io.treasure.entity.MerchantEntity;
import io.treasure.service.ClientUserService;
import io.treasure.service.UserWithdrawService;
import io.treasure.service.impl.MerchantServiceImpl;
import io.treasure.utils.SendSMSUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

import javax.servlet.http.HttpServletRequest;
import java.math.BigDecimal;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/userwithdraw")
@Api(tags="用户提现管理")
public class UserWithdrawController {
    @Autowired
    private MerchantServiceImpl merchantService;
    @Autowired
    private ClientUserService clientUserService;
    @Autowired
    private UserWithdrawService userWithdrawService;
    @Autowired
    private SMSConfig smsConfig;
    @Login
    @PostMapping("save")
    @ApiOperation("申请提现")
    public Result save(@RequestBody UserWithdrawDTO dto){
        //效验数据
        ValidatorUtils.validateEntity(dto, AddGroup.class);
        ClientUserEntity clientUserEntity = clientUserService.selectById(dto.getUserId());
        BigDecimal coin = clientUserEntity.getCoin();
        List<UserWithdrawDTO> userWithdrawDTOS = userWithdrawService.selectByUserIdAndStasus(dto.getUserId());
        if (userWithdrawDTOS.size()!=0) {
            return new Result().error("提现处理中，请稍后再试");
        }
        double money = dto.getMoney();
        if (money>coin.doubleValue()){
            return new Result().error("提现金额不足");
        }
        if(money<1 || money >5000){
            return new Result().error("提现范围在1~5000元");
        }

//
//        if((int)money != money){
//            return new Result().error("请输入整数");
//        }

        dto.setCreateDate(new Date());
        dto.setStatus(Common.STATUS_ON.getStatus());
        dto.setVerifyState(WithdrawEnm.STATUS_NO.getStatus());
        dto.setWay(WithdrawEnm.WAY_HAND.getStatus());
        userWithdrawService.save(dto);
        java.math.BigDecimal bd1 = new java.math.BigDecimal(money);
        coin = coin.subtract(bd1);
        clientUserEntity.setCoin(coin);
        clientUserService.updateById(clientUserEntity);
        String mobile = merchantService.selectOfficialMobile();
        SendSMSUtil.MerchantsWithdrawal(mobile,dto.getMoney().toString(), clientUserEntity.getUsername(), smsConfig);
        return new Result();
    }

    @GetMapping("audit")
    @ApiOperation("审核")
    public Result audit(@RequestParam Long id,@RequestParam Integer state,@RequestParam String verifyReason, HttpServletRequest request) throws AlipayApiException {
        UserWithdrawDTO dto = userWithdrawService.get(id);
        dto.setVerifyState(state);
        dto.setVerifyReason(verifyReason);
        Result result=userWithdrawService.audit(dto,request);
        ClientUserEntity clientUserEntity = clientUserService.selectById(dto.getUserId());
        LinkedHashMap<String, String> map=new LinkedHashMap<String,String>();
        map.put("code",clientUserEntity.getUsername());
        map.put("money",dto.getMoney().toString());
        if(dto.getVerifyState()==2){
            map.put("state","已经通过");
            map.put("content","请及时查看");
        }else if(dto.getVerifyState()==3){
            map.put("state","没有通过");
            map.put("content",dto.getVerifyReason());
        }else {
            throw new RenException("审核异常！");
        }
        SendSMSUtil.sendmerchantWithdraw(clientUserEntity.getMobile(),map, smsConfig);
        return result;
    }
    @CrossOrigin
    @Login
    @GetMapping("allPage")
    @ApiOperation("全部列表")
    @ApiImplicitParams({
            @ApiImplicitParam(name = Constant.PAGE, value = "当前页码，从1开始", paramType = "query", required = true, dataType="int") ,
            @ApiImplicitParam(name = Constant.LIMIT, value = "每页显示记录数", paramType = "query",required = true, dataType="int") ,
            @ApiImplicitParam(name = Constant.ORDER_FIELD, value = "排序字段", paramType = "query", dataType="String") ,
            @ApiImplicitParam(name = Constant.ORDER, value = "排序方式，可选值(asc、desc)", paramType = "query", dataType="String"),
            @ApiImplicitParam(name="userId",value="用户编号",paramType = "query",required = true,dataType = "long")

    })
    public Result<PageData<UserWithdrawDTO>> allPage(@ApiIgnore @RequestParam Map<String, Object> params){
        params.put("status", Common.STATUS_ON.getStatus()+"");
        PageData<UserWithdrawDTO> page = userWithdrawService.page(params);
        return new Result<PageData<UserWithdrawDTO>>().ok(page);
    }
    @CrossOrigin
    @Login
    @GetMapping("agreePage")
    @ApiOperation("同意提现列表")
    @ApiImplicitParams({
            @ApiImplicitParam(name = Constant.PAGE, value = "当前页码，从1开始", paramType = "query", required = true, dataType="int") ,
            @ApiImplicitParam(name = Constant.LIMIT, value = "每页显示记录数", paramType = "query",required = true, dataType="int") ,
            @ApiImplicitParam(name = Constant.ORDER_FIELD, value = "排序字段", paramType = "query", dataType="String") ,
            @ApiImplicitParam(name = Constant.ORDER, value = "排序方式，可选值(asc、desc)", paramType = "query", dataType="String"),
            @ApiImplicitParam(name="userId",value="用户编号",paramType = "query",required = true,dataType = "String")
    })
    public Result<PageData<UserWithdrawDTO>> agreePage(@ApiIgnore @RequestParam Map<String, Object> params){
        params.put("status", Common.STATUS_ON.getStatus()+"");
        params.put("verifyState", WithdrawEnm.STATUS_AGREE_YES.getStatus()+"");
        PageData<UserWithdrawDTO> page = userWithdrawService.listPage(params);
        return new Result<PageData<UserWithdrawDTO>>().ok(page);
    }
    @CrossOrigin
    @Login
    @GetMapping("agreeNoPage")
    @ApiOperation("拒绝提现列表")
    @ApiImplicitParams({
            @ApiImplicitParam(name = Constant.PAGE, value = "当前页码，从1开始", paramType = "query", required = true, dataType="int") ,
            @ApiImplicitParam(name = Constant.LIMIT, value = "每页显示记录数", paramType = "query",required = true, dataType="int") ,
            @ApiImplicitParam(name = Constant.ORDER_FIELD, value = "排序字段", paramType = "query", dataType="String") ,
            @ApiImplicitParam(name = Constant.ORDER, value = "排序方式，可选值(asc、desc)", paramType = "query", dataType="String"),
            @ApiImplicitParam(name="userId",value="用户编号",paramType = "query",required = true,dataType = "String")
    })
    public Result<PageData<UserWithdrawDTO>> agreeNoPage(@ApiIgnore @RequestParam Map<String, Object> params){
        params.put("status", Common.STATUS_ON.getStatus()+"");
        params.put("verifyState", WithdrawEnm.STATUS_AGREE_NO.getStatus()+"");
        PageData<UserWithdrawDTO> page = userWithdrawService.listPage(params);
        return new Result<PageData<UserWithdrawDTO>>().ok(page);
    }
    @CrossOrigin
    @Login
    @GetMapping("applPage")
    @ApiOperation("未审核提现列表")
    @ApiImplicitParams({
            @ApiImplicitParam(name = Constant.PAGE, value = "当前页码，从1开始", paramType = "query", required = true, dataType="int") ,
            @ApiImplicitParam(name = Constant.LIMIT, value = "每页显示记录数", paramType = "query",required = true, dataType="int") ,
            @ApiImplicitParam(name = Constant.ORDER_FIELD, value = "排序字段", paramType = "query", dataType="String") ,
            @ApiImplicitParam(name = Constant.ORDER, value = "排序方式，可选值(asc、desc)", paramType = "query", dataType="String"),
            @ApiImplicitParam(name="userId",value="用户编号",paramType = "query",required = true,dataType = "String")
    })
    public Result<PageData<UserWithdrawDTO>> applPage(@ApiIgnore @RequestParam Map<String, Object> params){
        params.put("status", Common.STATUS_ON.getStatus()+"");
        params.put("verifyState", WithdrawEnm.STATUS_NO.getStatus()+"");
        PageData<UserWithdrawDTO> page = userWithdrawService.listPage(params);
        return new Result<PageData<UserWithdrawDTO>>().ok(page);
    }
    @CrossOrigin
    @Login
    @GetMapping("getInfo")
    @ApiOperation("详细信息")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "编号", paramType = "query", required = true, dataType="Long")
    })
    public Result<UserWithdrawDTO> get(@RequestParam Long id){
        UserWithdrawDTO data = userWithdrawService.get(id);
        return new Result<UserWithdrawDTO>().ok(data);
    }



}

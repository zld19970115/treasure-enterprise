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
import io.treasure.entity.MasterOrderEntity;
import io.treasure.entity.MerchantEntity;
import io.treasure.entity.MerchantWithdrawEntity;
import io.treasure.service.MerchantWithdrawService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import io.treasure.service.impl.MerchantServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

import javax.servlet.http.HttpServletResponse;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.util.Date;
import java.util.HashMap;
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
    @Autowired
    private MerchantServiceImpl merchantService;
    @CrossOrigin
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
    @CrossOrigin
    @Login
    @GetMapping("agreePage")
    @ApiOperation("同意提现列表")
    @ApiImplicitParams({
            @ApiImplicitParam(name = Constant.PAGE, value = "当前页码，从1开始", paramType = "query", required = true, dataType="int") ,
            @ApiImplicitParam(name = Constant.LIMIT, value = "每页显示记录数", paramType = "query",required = true, dataType="int") ,
            @ApiImplicitParam(name = Constant.ORDER_FIELD, value = "排序字段", paramType = "query", dataType="String") ,
            @ApiImplicitParam(name = Constant.ORDER, value = "排序方式，可选值(asc、desc)", paramType = "query", dataType="String"),
            @ApiImplicitParam(name="merchantId",value="商户编号",paramType = "query",required = true,dataType = "String")
    })
    public Result<PageData<MerchantWithdrawDTO>> agreePage(@ApiIgnore @RequestParam Map<String, Object> params){
        params.put("status", Common.STATUS_ON.getStatus()+"");
        params.put("verifyState", WithdrawEnm.STATUS_AGREE_YES.getStatus()+"");
        PageData<MerchantWithdrawDTO> page = merchantWithdrawService.listPage(params);
        return new Result<PageData<MerchantWithdrawDTO>>().ok(page);
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
            @ApiImplicitParam(name="merchantId",value="商户编号",paramType = "query",required = true,dataType = "String")
    })
    public Result<PageData<MerchantWithdrawDTO>> agreeNoPage(@ApiIgnore @RequestParam Map<String, Object> params){
        params.put("status", Common.STATUS_ON.getStatus()+"");
        params.put("verifyState", WithdrawEnm.STATUS_AGREE_NO.getStatus()+"");
        PageData<MerchantWithdrawDTO> page = merchantWithdrawService.listPage(params);
        return new Result<PageData<MerchantWithdrawDTO>>().ok(page);
    }
    @CrossOrigin
    @Login
    @GetMapping("applPage")
    @ApiOperation("拒绝提现列表")
    @ApiImplicitParams({
            @ApiImplicitParam(name = Constant.PAGE, value = "当前页码，从1开始", paramType = "query", required = true, dataType="int") ,
            @ApiImplicitParam(name = Constant.LIMIT, value = "每页显示记录数", paramType = "query",required = true, dataType="int") ,
            @ApiImplicitParam(name = Constant.ORDER_FIELD, value = "排序字段", paramType = "query", dataType="String") ,
            @ApiImplicitParam(name = Constant.ORDER, value = "排序方式，可选值(asc、desc)", paramType = "query", dataType="String"),
            @ApiImplicitParam(name="merchantId",value="商户编号",paramType = "query",required = true,dataType = "String")
    })
    public Result<PageData<MerchantWithdrawDTO>> applPage(@ApiIgnore @RequestParam Map<String, Object> params){
        params.put("status", Common.STATUS_ON.getStatus()+"");
        params.put("verifyState", WithdrawEnm.STATUS_NO.getStatus()+"");
        PageData<MerchantWithdrawDTO> page = merchantWithdrawService.listPage(params);
        return new Result<PageData<MerchantWithdrawDTO>>().ok(page);
    }
    @CrossOrigin
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
        MerchantEntity merchantEntity = merchantService.selectById(dto.getMerchantId());
        Double notCash = merchantEntity.getNotCash();

        Double money = dto.getMoney();
        if (money>notCash){
            return new Result().error("提现金额不足");
        }
        dto.setCreateDate(new Date());
        dto.setStatus(Common.STATUS_ON.getStatus());
        dto.setVerifyState(WithdrawEnm.STATUS_NO.getStatus());
        dto.setWay(WithdrawEnm.WAY_HAND.getStatus());
        merchantWithdrawService.save(dto);
        return new Result();
    }
    @CrossOrigin
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


    @CrossOrigin
    @Login
    @GetMapping("/selectCath")
    @ApiOperation("查询提现金额")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "martId", value = "编号", paramType = "query", required = true, dataType="Long")
    })
    public Result selectCath(@RequestParam Long martId) {

        List<MasterOrderEntity>  masterOrderEntity = merchantWithdrawService.selectOrderByMartID(martId);
        MerchantEntity merchantEntity = merchantService.selectById(martId);
        Double wartCash = merchantWithdrawService.selectWaitByMartId(martId);

        if (masterOrderEntity==null){
            if(null!=merchantEntity){
                BigDecimal wartcashZore = new BigDecimal("0.00");
                merchantEntity.setTotalCash(0.00);
                merchantEntity.setAlreadyCash(0.00);
                merchantEntity.setNotCash(0.00);
                merchantEntity.setPointMoney(0.00);
                merchantEntity.setWartCash(wartcashZore);
                merchantService.updateById(merchantEntity);
                Map map = new HashMap();
                map.put("alead_cash", 0.00);
                map.put("not_cash", 0.00);
                map.put("wart_cash",wartcashZore);
                return new Result().ok(map);
            }else{
                return new Result().error("无法获取店铺信息!");
            }
        }
        List<MerchantWithdrawEntity> merchantWithdrawEntities = merchantWithdrawService.selectPoByMartID(martId);
        if (merchantWithdrawEntities.size()==0){
            BigDecimal bigDecimal = merchantWithdrawService.selectTotalCath(martId);
            BigDecimal bigDecimal1 = merchantWithdrawService.selectPointMoney(martId);

            BigDecimal wartcashZore = new BigDecimal("0.00");
            if (null==bigDecimal ){

                if(null!=merchantEntity){
                    if (bigDecimal1==null){  bigDecimal1 = new BigDecimal("0.00");}
                    merchantEntity.setTotalCash(0.00);
                    merchantEntity.setAlreadyCash(0.00);
                    merchantEntity.setNotCash(0.00);
                    merchantEntity.setPointMoney(bigDecimal1.doubleValue());
                    merchantEntity.setWartCash(wartcashZore);
                    merchantService.updateById(merchantEntity);
                    Map map = new HashMap();

                    map.put("alead_cash", 0.00);
                    map.put("not_cash", 0.00);
                    map.put("wart_cash",wartcashZore);
                    return new Result().ok(map);
                }else{
                    return new Result().error("无法获取店铺信息!");
                }
            }
            merchantEntity.setTotalCash(bigDecimal.doubleValue());
            merchantEntity.setAlreadyCash(0.00);
            merchantEntity.setNotCash(bigDecimal.doubleValue());
            merchantEntity.setPointMoney(bigDecimal1.doubleValue());
            merchantEntity.setWartCash(wartcashZore);
            merchantService.updateById(merchantEntity);
            Map map = new HashMap();
            map.put("alead_cash", 0.00);
            map.put("not_cash", bigDecimal.doubleValue());
            map.put("wart_cash",wartcashZore);

            return new Result().ok(map);
        }
        if (merchantWithdrawEntities.size() != 0) {
            BigDecimal wartcash = new BigDecimal(String.valueOf(wartCash));
            BigDecimal bigDecimal = merchantWithdrawService.selectTotalCath(martId);//查询总额
            BigDecimal bigDecimal1 = merchantWithdrawService.selectPointMoney(martId);//查询扣点总额
            if (bigDecimal==null){
                bigDecimal = new BigDecimal("0.00");
            }
            Double aDouble = merchantWithdrawService.selectAlreadyCash(martId); //查询已提现总额
            if (aDouble==null){
                aDouble=0.00;
            }
            String allMoney = String.valueOf(merchantWithdrawService.selectByMartId(martId));
            BigDecimal v = new BigDecimal(allMoney);
            BigDecimal a = bigDecimal.subtract(v);
            double c = a.doubleValue();
            merchantEntity.setTotalCash(bigDecimal.doubleValue());
            merchantEntity.setAlreadyCash(aDouble);
            merchantEntity.setNotCash(c);
            merchantEntity.setPointMoney(bigDecimal1.doubleValue());
            merchantEntity.setWartCash(wartcash);
            merchantService.updateById(merchantEntity);
            Map map = new HashMap();

            map.put("alead_cash", aDouble);//已提现
            map.put("not_cash", c);//未体现
            map.put("wart_cash",wartcash);//审核中
            return new Result().ok(map);
    }
        return new Result();
}
    @CrossOrigin
    @Login
    @PutMapping("agreeYes")
    @ApiOperation("同意提现")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "编号", paramType = "query", required = true, dataType="long"),
            @ApiImplicitParam(name = "verify", value = "审核人", paramType = "query", required = true, dataType="long")
    })
    public Result agreeYes(@RequestParam long id,@RequestParam long verify){
        merchantWithdrawService.verify(id,verify,WithdrawEnm.STATUS_AGREE_YES.getStatus(),"同意提现",new Date());
        return new Result().ok("提现成功");
    }
    @CrossOrigin
    @Login
    @PutMapping("agreeNo")
    @ApiOperation("拒绝提现")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "编号", paramType = "query", required = true, dataType="long"),
            @ApiImplicitParam(name = "verifyReason", value = "拒绝原因", paramType = "query", required = true, dataType="String"),
            @ApiImplicitParam(name = "verify", value = "审核人", paramType = "query", required = true, dataType="long")
    })
    public Result agreeNo(@RequestParam long id,@RequestParam long verify,@RequestParam String verifyReason){
        merchantWithdrawService.verify(id,verify,WithdrawEnm.STATUS_AGREE_NO.getStatus(),verifyReason,new Date());
        return new Result().ok("提现成功");
    }

    @CrossOrigin
    @Login
    @PutMapping("selectWithStatus")
    @ApiOperation("查询可提现状态")
    public Result selectWithStatus(){
        String s = merchantWithdrawService.selectWithStatus();

        return new Result().ok(s);
    }




}
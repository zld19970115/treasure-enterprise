package io.treasure.controller;

import io.treasure.annotation.Login;
import io.treasure.common.constant.Constant;
import io.treasure.common.page.PageData;
import io.treasure.common.utils.DateUtils;
import io.treasure.common.utils.Result;
import io.treasure.common.validator.AssertUtils;
import io.treasure.common.validator.ValidatorUtils;
import io.treasure.common.validator.group.AddGroup;
import io.treasure.common.validator.group.DefaultGroup;
import io.treasure.common.validator.group.UpdateGroup;
import io.treasure.dto.MerchantCouponDTO;
import io.treasure.enm.Common;
import io.treasure.enm.CouponEnm;
import io.treasure.entity.MerchantCouponEntity;
import io.treasure.service.MerchantCouponService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.extern.java.Log;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

import javax.servlet.http.HttpServletResponse;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Map;


/**
 * 商户端优惠卷
 *
 * @author Super 63600679@qq.com
 * @since 1.0.0 2019-08-23
 */
@RestController
@RequestMapping("/merchantcoupon")
@Api(tags="商户端优惠卷")
public class MerchantCouponController {
    @Autowired
    private MerchantCouponService merchantCouponService;
    @CrossOrigin
    @Login
    @GetMapping("page")
    @ApiOperation("全部列表")
    @ApiImplicitParams({
            @ApiImplicitParam(name = Constant.PAGE, value = "当前页码，从1开始", paramType = "query", required = true, dataType="int") ,
            @ApiImplicitParam(name = Constant.LIMIT, value = "每页显示记录数", paramType = "query",required = true, dataType="int") ,
            @ApiImplicitParam(name = Constant.ORDER_FIELD, value = "排序字段", paramType = "query", dataType="String") ,
            @ApiImplicitParam(name = Constant.ORDER, value = "排序方式，可选值(asc、desc)", paramType = "query", dataType="String"),
            @ApiImplicitParam(name = "merchantId", value = "商户", paramType = "query",required = true,  dataType="long")
    })
    public Result<PageData<MerchantCouponDTO>> page(@ApiIgnore @RequestParam Map<String, Object> params){
        params.put("status",Common.STATUS_ON.getStatus()+"");
        PageData<MerchantCouponDTO> page = merchantCouponService.listPage(params);
        return new Result<PageData<MerchantCouponDTO>>().ok(page);
    }
    @CrossOrigin
    @Login
    @GetMapping("getInfo")
    @ApiOperation("详细信息")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "编号", paramType = "query", required = true, dataType="long")
    })
    public Result<MerchantCouponDTO> get(@RequestParam long id){
        MerchantCouponDTO data = merchantCouponService.get(id);
        return new Result<MerchantCouponDTO>().ok(data);
    }
    @CrossOrigin
    @Login
    @PostMapping("save")
    @ApiOperation("保存")
    public Result save(@RequestBody MerchantCouponDTO dto){
        //效验数据
        ValidatorUtils.validateEntity(dto, AddGroup.class);
        dto.setCreateDate(new Date());
//        dto.setGrants(CouponEnm.STATUS_GRANTS.getStatus());
        dto.setStatus(Common.STATUS_ON.getStatus());
        merchantCouponService.save(dto);
        return new Result();
    }
    @CrossOrigin
    @Login
    @PutMapping("update")
    @ApiOperation("修改")
    public Result update(@RequestBody MerchantCouponDTO dto){
        //效验数据
        ValidatorUtils.validateEntity(dto, UpdateGroup.class, DefaultGroup.class);
        dto.setUpdateDate(new Date());
        //dto.setGrants(CouponEnm.STATUS_GRANTS.getStatus());
        dto.setStatus(Common.STATUS_ON.getStatus());
        merchantCouponService.update(dto);
        return new Result();
    }
    @CrossOrigin
    @Login
    @DeleteMapping("remove")
    @ApiOperation("删除")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value ="编号", paramType = "query", required = true, dataType="long")
    })
    public Result delete(@RequestParam long id){
        merchantCouponService.updateStatusById(id,Common.STATUS_DELETE.getStatus());
        return new Result();
    }

    /**
     * 通过商户id查询此商户所有满减优惠
     * @param merchantId
     * @return
     */
    @CrossOrigin
    @Login
    @GetMapping("getMoneyOffByMerchantId")
    @ApiOperation("查询此用户所有满减优惠")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "merchantId", value ="商户id", paramType = "query", required = true, dataType="long"),
            @ApiImplicitParam(name = "totalMoney", value ="总价", paramType = "query", required = true, dataType="BigDecimal"),
            @ApiImplicitParam(name = "userId", value ="用户ID", paramType = "query", required = true, dataType="userId")
    })
    public Result< MerchantCouponDTO> getMoneyOffByMerchantId(long merchantId, BigDecimal totalMoney,long userId){
        Result difference = merchantCouponService.getDifference(merchantId, totalMoney,userId);

        return new Result().ok(difference);
    }


}
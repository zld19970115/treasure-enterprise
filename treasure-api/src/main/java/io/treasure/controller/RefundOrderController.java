package io.treasure.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import io.treasure.annotation.Login;
import io.treasure.common.constant.Constant;
import io.treasure.common.page.PageData;
import io.treasure.common.utils.Result;
import io.treasure.dto.RefundOrderDTO;
import io.treasure.entity.RefundOrderEntity;
import io.treasure.service.RefundOrderService;
import io.treasure.service.SlaveOrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

import java.util.Map;

@RestController
@RequestMapping("/BackGoodsAudit")
@Api(tags="商户审核退菜表")
public class RefundOrderController {
    @Autowired
    private RefundOrderService refundOrderService;

    @Autowired
    private SlaveOrderService slaveOrderService;

    @Login
    @GetMapping("getRefundOrderByMerchantId")
    @ApiOperation("查询商户所有退款信息")
    @ApiImplicitParams({
            @ApiImplicitParam(name = Constant.PAGE, value = "当前页码，从1开始", paramType = "query", required = true, dataType="int") ,
            @ApiImplicitParam(name = Constant.LIMIT, value = "每页显示记录数", paramType = "query",required = true, dataType="int") ,
            @ApiImplicitParam(name = Constant.ORDER_FIELD, value = "排序字段", paramType = "query", dataType="String") ,
            @ApiImplicitParam(name = Constant.ORDER, value = "排序方式，可选值(asc、desc)", paramType = "query", dataType="String"),
            @ApiImplicitParam(name = "merchantId", value = "商户ID", paramType = "query",required=true, dataType="Long")
    })
    public Result<PageData<RefundOrderDTO>> getRefundOrderByMerchantId(@ApiIgnore @RequestParam Map<String, Object> params){
        PageData<RefundOrderDTO> page = refundOrderService.getRefundOrderByMerchantId(params);
        return new Result<PageData<RefundOrderDTO>>().ok(page);
    }

    @Login
    @PutMapping("agreeToARefund")
    @ApiOperation("同意审核")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "orderId", value = "订单ID", paramType = "query", required = true, dataType="String") ,
            @ApiImplicitParam(name = "goodId", value = "商品ID", paramType = "query",required = true, dataType="long")
    })
    public Result agreeToARefund(String orderId,Long goodId ){
        refundOrderService.agreeToARefund(orderId,goodId);
        return  new Result();
    }

    @Login
    @PutMapping("DoNotAgreeToRefund")
    @ApiOperation("拒绝审核")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "orderId", value = "订单ID", paramType = "query", required = true, dataType="String") ,
            @ApiImplicitParam(name = "goodId", value = "商品ID", paramType = "query",required = true, dataType="long")
    })
    public Result DoNotAgreeToRefund(String orderId,Long goodId ){
        refundOrderService.DoNotAgreeToRefund(orderId,goodId);
        return  new Result();
    }
}

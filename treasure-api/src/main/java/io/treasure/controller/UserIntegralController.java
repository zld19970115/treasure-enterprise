package io.treasure.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.treasure.common.utils.Result;
import io.treasure.entity.ClientUserEntity;
import io.treasure.entity.MasterOrderEntity;
import io.treasure.service.ClientUserService;
import io.treasure.service.MasterOrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;

/**
 * 用户积分表
 * 2019.8.28
 */
@RestController
@RequestMapping("/UserIntegral")
@Api(tags="用户积分表")
public class UserIntegralController {

    @Autowired
    private ClientUserService clientUserService;
    @GetMapping("/IntegralGift")
    @ApiOperation("用户积分兑换赠送金")
    public Result integralGift(@RequestParam Long userId, @RequestParam BigDecimal integral){
        //用户使用积分兑换赠送金，比例暂时为2:1
        BigDecimal a = new BigDecimal("100");
        BigDecimal b = new BigDecimal("2");
        if (integral.compareTo(a)==-1){
            return  new Result().error("积分必须大于等于100");
        }
        if( integral.doubleValue()%100!=0){
            return  new Result().error("请输入100的倍数");
        }
        ClientUserEntity clientUserEntity = clientUserService.selectById(userId);
        BigDecimal integral1 = clientUserEntity.getIntegral();
        BigDecimal gift = clientUserEntity.getGift();

        if (integral1.compareTo(integral)==-1){
            return  new Result().error("积分不足");
        }
        integral1= integral1.subtract(integral);
        clientUserEntity.setIntegral(integral1);
        integral= integral.divide(b);
        gift = gift.add(integral);
        clientUserEntity.setGift(gift);
        clientUserService.updateById(clientUserEntity);
        return  new Result().ok("兑换成功");
    }
}
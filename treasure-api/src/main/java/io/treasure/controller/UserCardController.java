package io.treasure.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.treasure.common.utils.Result;
import io.treasure.entity.CardInfoEntity;
import io.treasure.entity.ClientUserEntity;
import io.treasure.entity.MerchantCouponEntity;
import io.treasure.service.UserCardService;
import io.treasure.service.impl.ClientUserServiceImpl;
import io.treasure.service.impl.UserCardServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;


@RestController
@RequestMapping("/userCard")
@Api(tags="用户赠送金表")
public class UserCardController {
    @Autowired
    UserCardServiceImpl userCardService;
    @Autowired
    ClientUserServiceImpl clientUserService;
    @GetMapping("/goCard")
    @ApiOperation("用户充值赠送金表")
    public Result selectMartCoupon(@RequestParam long userId, @RequestParam long id , @RequestParam String password){

        CardInfoEntity cardInfoEntity = userCardService.selectByIdAndPassword(id, password);
          if (cardInfoEntity==null){
              return new Result().error("账号密码错误");
          }
       if (cardInfoEntity.getStatus()==1){
           return new Result().error("该卡密未激活");
       }
        if (cardInfoEntity.getStatus()==3){
            return new Result().error("该卡密已使用");
        }
        if (cardInfoEntity.getStatus()==9){
            return new Result().error("该卡密已删除");
        }
        BigDecimal a = new BigDecimal("50");

        ClientUserEntity clientUserEntity = clientUserService.selectById(userId);
        if (clientUserEntity==null){
            return new Result().error("请登录");
        }
        if( clientUserEntity.getGift().compareTo(a)==1){
            return new Result().error("赠送金余额大于50不可充值");
        }
        BigDecimal money = cardInfoEntity.getMoney().add(clientUserEntity.getGift());
        clientUserEntity.setGift(money);
        clientUserService.updateById(clientUserEntity);
        Date date = new Date();
        cardInfoEntity.setStatus(3);
        cardInfoEntity.setBindCardDate(date);
        cardInfoEntity.setBindCardUser(userId);
        userCardService.updateById(cardInfoEntity);
        return new Result().ok("充值成功");
    }

}

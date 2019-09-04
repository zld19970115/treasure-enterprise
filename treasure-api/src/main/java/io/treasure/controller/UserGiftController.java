package io.treasure.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.treasure.common.utils.Result;
import io.treasure.dto.UserGiftDTO;
import io.treasure.entity.ClientUserEntity;
import io.treasure.entity.UserGiftEntity;
import io.treasure.service.impl.UserGiftServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;

/**
 * 用户赠送金表
 * 2019.9.3
 */
@RestController
@RequestMapping("/userGift")
@Api(tags="用户赠送金表")
public class UserGiftController {
    @Autowired
    UserGiftServiceImpl userGiftService;
    @PostMapping("/insertGift")
    @ApiOperation("用户充值赠送金")
    public Result insertGift(@RequestBody UserGiftDTO dto){
        ClientUserEntity clientUserEntity = userGiftService.selectBynumber(dto.getUserNumber());
       if (clientUserEntity==null){
           return new Result().error("不存在此用户");
       }
        UserGiftEntity userGiftEntity = userGiftService.selectStatus(dto.getNumber(),dto.getPassword());
       if (userGiftEntity==null){
           return new Result().error("账号密码错误");
       }
       if (userGiftEntity.getStatus()==1){
           return new Result().error("该卡密已使用");
       }
        long id = userGiftEntity.getId();
        String userNumber = dto.getUserNumber();
        userGiftService.updateUnumberAndStatus(userNumber,id);
        BigDecimal money = userGiftEntity.getMoney().add(clientUserEntity.getGift());
        userGiftService.updateGift(money,clientUserEntity.getId());
        return new Result();

    }


}

package io.treasure.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import io.treasure.common.utils.Result;
import io.treasure.dto.UserGiftDTO;
import io.treasure.entity.ClientUserEntity;
import io.treasure.entity.UserGiftEntity;
import io.treasure.service.impl.UserGiftServiceImpl;
import io.treasure.utils.DateUtil;
import org.apache.ibatis.annotations.Param;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.xml.crypto.Data;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;

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
    @PostMapping("/goGift")
    @ApiOperation("用户充值赠送金")
    public Result goGift(@RequestBody UserGiftDTO dto){
        ClientUserEntity clientUserEntity = userGiftService.selectBynumber(dto.getUserNumber());
       if (clientUserEntity==null){
           return new Result().error("不存在此用户");
       }
        UserGiftEntity userGiftEntity = userGiftService.selectStatus(dto.getNumber(),dto.getPassword());
       if (userGiftEntity==null){
           return new Result().error("账号密码错误");
       }
        Date endDate = userGiftEntity.getEndDate();
        long end = endDate.getTime();
         Date date = new Date();
        long now = date.getTime();
        if (now>end){
            return new Result().error("卡密已经过期");
        }
        if (userGiftEntity.getStatus()==1){
           return new Result().error("该卡密已使用");
       }
        BigDecimal a = new BigDecimal("50");

       if( clientUserEntity.getGift().compareTo(a)==1){
           return new Result().error("赠送金余额大于50不可充值");
       }

        long id = userGiftEntity.getId();
        String userNumber = dto.getUserNumber();
        userGiftService.updateUnumberAndStatus(userNumber,id);
        BigDecimal money = userGiftEntity.getMoney().add(clientUserEntity.getGift());
        userGiftService.updateGift(money,clientUserEntity.getId());
        return new Result().ok("充值成功");

    }

    @GetMapping("/insertGift")
    @ApiOperation("创建赠送金卡劵")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "article", value = "卡眷数量", paramType = "query", required = true, dataType = "long"),
            @ApiImplicitParam(name = "money", value = "钱数", paramType = "query", required = true, dataType = "long"),
            @ApiImplicitParam(name = "end_date", value = "卡眷截止时间", paramType = "query", required = true, dataType = "")
    })
    public Result insertGift( Integer article, BigDecimal money,Date end_date) {

        for (int i = 0; i < article; i++) {
            //注意replaceAll前面的是正则表达式
            UserGiftDTO uge = new UserGiftDTO();
            String uuid = UUID.randomUUID().toString().replaceAll("-", "");
            Date date = new Date();
            int password = (int) ((Math.random() * 9 + 1) * 100000);
            uge.setNumber(uuid);
            uge.setCreateDate(date);
            uge.setEndDate(end_date);
            uge.setMoney(money);
            uge.setPassword(password);
            userGiftService.insertGift(uge);
        }

            return new Result().ok("创建成功");
    }
    @GetMapping("/select")
    @ApiOperation("查看赠送金表")
    public Result insertGift( long id) {
        UserGiftEntity userGiftEntity = userGiftService.selectById(id);
        return new Result().ok(userGiftEntity);
    }


}

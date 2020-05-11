package io.treasure.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.treasure.annotation.Login;
import io.treasure.common.utils.Result;
import io.treasure.service.impl.UserCardServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("/userCard")
@Api(tags="用户赠送金表")
public class UserCardController {
    @Autowired
    private UserCardServiceImpl userCardService;
    @Login
    @GetMapping("/goCard")
    @ApiOperation("用户充值赠送金表")
    public Result selectMartCoupon(@RequestParam long userId, @RequestParam long id , @RequestParam String password){

        Result result = userCardService.selectByIdAndPassword(id, password, userId);


        return new Result().ok(result);
    }

}

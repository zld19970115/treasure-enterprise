package io.treasure.controller;

import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import io.treasure.common.utils.Result;
import io.treasure.service.ClientUserVersionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("/version")
public class ClientUserVersionController {

    @Autowired
    private ClientUserVersionService clientUserVersionService;

    @GetMapping("/test")
    @ApiOperation("简易锁控制")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "mobile", value = "用户手机号", paramType = "query", required = true, dataType = "string"),
            @ApiImplicitParam(name = "process_type", value = "1：下单2：退单", paramType = "query", required = true, dataType = "int")
    })
    public Result testVersion(String mobile, int process_type) {

        if (process_type == 1){
            clientUserVersionService.checkNormalOperation(mobile, 1);
        }else{
            clientUserVersionService.checkNormalOperation(mobile, 2);
        }

        return new Result().ok("测试成功");
    }
}
package io.treasure.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.treasure.common.utils.Result;
import io.treasure.entity.ClientUserEntity;
import io.treasure.entity.TokenEntity;
import io.treasure.entity.UserPushEntity;
import io.treasure.service.ClientUserService;
import io.treasure.service.TokenService;
import io.treasure.service.UserPushService;
import org.apache.logging.log4j.util.Strings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.Date;

@RestController
@RequestMapping("/userPush")
@Api(tags="用户推送")
public class UserPushController {

    @Autowired
    private UserPushService userPushService;

    @Autowired
    private TokenService tokenService;

    @Autowired
    private ClientUserService clientUserService;

    @GetMapping("/userClient")
    @ApiOperation("用户推送维护")
    public Result userClient(@RequestParam String clientId, HttpServletRequest request) {
        if (Strings.isNotBlank(clientId)) {
            String token = request.getHeader("token");
            Long userId = null;
            if (Strings.isNotBlank(token)) {
                TokenEntity obj = tokenService.getByToken(token);
                if (obj != null && obj.getUserId() != null) {
                    ClientUserEntity info = clientUserService.selectById(obj.getUserId());
                    if (info != null) {
                        userId = info.getId();
                    }
                }
            }
            UserPushEntity obj = userPushService.selectByCid(clientId);
            if(obj != null) {
                obj.setClientId(clientId);
                obj.setUserId(userId);
                obj.setCreateTime(new Date());
                userPushService.updateById(obj);
            } else {
                obj = new UserPushEntity();
                obj.setUserId(userId);
                obj.setClientId(clientId);
                obj.setCreateTime(new Date());
                userPushService.insert(obj);
            }
        }
        return new Result().ok("ok");
    }

}

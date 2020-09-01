package io.treasure.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import io.treasure.annotation.Login;
import io.treasure.common.constant.Constant;
import io.treasure.common.page.PageData;
import io.treasure.common.utils.Result;
import io.treasure.entity.ClientUserEntity;
import io.treasure.entity.TokenEntity;
import io.treasure.entity.UserPushEntity;
import io.treasure.service.ClientUserService;
import io.treasure.service.TokenService;
import io.treasure.service.UserPushService;
import io.treasure.vo.UserPushVo;
import org.apache.logging.log4j.util.Strings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import springfox.documentation.annotations.ApiIgnore;

import javax.servlet.http.HttpServletRequest;
import java.util.Date;
import java.util.Map;

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
            if(obj == null && userId != null) {
                obj = userPushService.selectByUserId(userId);
            }
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

    @Login
    @GetMapping("page")
    @ApiOperation("分页查询")
    @ApiImplicitParams({
            @ApiImplicitParam(name = Constant.PAGE, value = "当前页码，从1开始", paramType = "query", required = true, dataType="int") ,
            @ApiImplicitParam(name = Constant.LIMIT, value = "每页显示记录数", paramType = "query",required = true, dataType="int")
    })
    public Result<PageData<UserPushVo>> page(@ApiIgnore @RequestParam Map<String, Object> params) {
        return new Result<PageData<UserPushVo>>().ok(userPushService.pageList(params));
    }

}

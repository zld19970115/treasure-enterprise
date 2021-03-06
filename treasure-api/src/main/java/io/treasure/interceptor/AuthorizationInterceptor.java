/**
 * Copyright (c) 2019 聚宝科技 All rights reserved.
 *
 * https://www.treasure.io
 *
 * 版权所有，侵权必究！
 */

package io.treasure.interceptor;

import io.treasure.annotation.Login;
import io.treasure.common.exception.ErrorCode;
import io.treasure.common.exception.RenException;
import io.treasure.entity.TokenEntity;
import io.treasure.service.TokenService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * 权限(Token)验证
 *
 * @author Super 63600679@qq.com
 */
@Component
public class AuthorizationInterceptor extends HandlerInterceptorAdapter {
    @Autowired
    private TokenService tokenService;
    public static final String USER_KEY = "userId";

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        Login annotation;
        System.out.println("验证 token");
//        request.getContextPath();
//        request.getMethod()
        if(handler instanceof HandlerMethod) {
            System.out.println("验证 token");
            annotation = ((HandlerMethod) handler).getMethodAnnotation(Login.class);
        }else{
            System.out.println("验证2 token");
            return true;
        }

        if(annotation == null){
            return true;
        }

        //从header中获取token
        String token = request.getHeader("token");

        //如果header中不存在token，则从参数中获取token
        if(StringUtils.isBlank(token)){
            token = request.getParameter("token");
        }
        //token为空
        if(StringUtils.isBlank(token)){
            System.out.println("当前token值：为空");
            throw new RenException(ErrorCode.TOKEN_NOT_EMPTY);
        }

        //查询token信息
        TokenEntity tokenEntity = tokenService.getByToken(token);
        if(tokenEntity == null || tokenEntity.getExpireDate().getTime() < System.currentTimeMillis()){
            throw new RenException(ErrorCode.TOKEN_INVALID);
        }
//        Long longDate = new Date().getTime();
//        tokenEntity.setExpireDate(new Date(longDate+7*24*60*60*1000));
//        System.out.println("token+111111111111111111111111111111111111111111111111111111111");
//        tokenService.updateById(tokenEntity);

        //设置userId到request里，后续根据userId，获取用户信息
        request.setAttribute(USER_KEY, tokenEntity.getUserId());

        return true;
    }
}

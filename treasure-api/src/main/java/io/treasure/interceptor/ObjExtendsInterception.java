package io.treasure.interceptor;

import io.treasure.annotation.ObjExtends;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Component
public class ObjExtendsInterception extends HandlerInterceptorAdapter {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {

        ObjExtends defaultParams = null;
        if(handler instanceof HandlerMethod) {
            defaultParams = ((HandlerMethod) handler).getMethodAnnotation(ObjExtends.class);
        }

        if(defaultParams == null){
            System.out.println("1");
        }else{
            System.out.println("2");
        }


        return super.preHandle(request, response, handler);
    }
}

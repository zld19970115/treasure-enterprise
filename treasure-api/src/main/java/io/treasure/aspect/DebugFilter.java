package io.treasure.aspect;


import org.springframework.stereotype.Component;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;

import javax.servlet.*;
import java.io.IOException;
import java.util.Enumeration;

//@Component
public class DebugFilter implements Filter {

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        Enumeration<String> parameterNames = servletRequest.getParameterNames();

        System.out.println("protocol,characterCode:"+servletRequest.getProtocol()+","+servletRequest.getCharacterEncoding());
        while(parameterNames.hasMoreElements()){
            System.out.println("parameterName:"+parameterNames.nextElement());
        }
        filterChain.doFilter(servletRequest, servletResponse);//到下一个链
    }

//
//    @Override
//    public void addInterceptors(InterceptorRegistry registry) {
//        registry.addInterceptor(localInterceptor())
//                .addPathPatterns("/**")
//                .excludePathPatterns("/user/login")
//                .excludePathPatterns("/swagger-resources/**", "/webjars/**", "/v2/**", "/swagger-ui.html/**");
//    }
//
//    @Override
//    protected void addResourceHandlers(ResourceHandlerRegistry registry) {
//        registry.addResourceHandler("swagger-ui.html")
//                .addResourceLocations("classpath:/META-INF/resources/");
//        registry.addResourceHandler("/webjars/**")
//                .addResourceLocations("classpath:/META-INF/resources/webjars/");
//    }


}

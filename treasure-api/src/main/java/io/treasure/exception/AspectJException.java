package io.treasure.exception;

import io.treasure.common.xss.XssHttpServletRequestWrapper;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.DefaultParameterNameDiscoverer;
import org.springframework.core.ParameterNameDiscoverer;
import org.springframework.stereotype.Component;

import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.Method;
import java.util.Arrays;

//@Aspect
//@Component
public class AspectJException {

    //private Logger logger = LoggerFactory.getLogger(javax.servlet.http.HttpServlet.class);


    @Before("execution(* javax.servlet.http.HttpServlet.service(..))")
    public void MonitorException(JoinPoint joinPoint)throws Exception{
        Object[] args = joinPoint.getArgs();//[io.treasure.common.xss.XssHttpServletRequestWrapper@3734b5e, com.alibaba.druid.support.http.WebStatFilter$StatHttpServletResponseWrapper@db4a540]
        String classType = joinPoint.getTarget().getClass().getName();
        String methodName = joinPoint.getSignature().getName();
        //System.out.println("请求调用异常："+(io.treasure.common.xss.XssHttpServletRequestWrapper)args[0]);
        XssHttpServletRequestWrapper xsrw =(io.treasure.common.xss.XssHttpServletRequestWrapper)args[0];
        System.out.println("请求异常"+xsrw.getContextPath()+","+xsrw.getInputStream());
        ServletInputStream inputStream = xsrw.getInputStream();
        Byte[] tmp = null;
//        while(inputStream.readLine(,0,1024) != null){
//
//        }
    }

}

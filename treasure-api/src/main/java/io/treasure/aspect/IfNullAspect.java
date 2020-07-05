package io.treasure.aspect;


import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.DeclareAnnotation;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;

@Component
@Aspect
public class IfNullAspect {

    @Pointcut("@annotation(io.treasure.annotation.IfNull)")
    public void IfNull(){
        System.out.println("什么情况");
    }

    @Before("IfNull()")
    public void before(JoinPoint joinPoint){

        System.out.println("haha:"+joinPoint.getTarget());
    }
}

package com.cqw.service;

import com.spring.Around;
import com.spring.Aspect;
import com.spring.Component;
import com.spring.ProceedingJoinPoint;

@Aspect
@Component
public class LogAop {

//    @Around(execution = "com.cqw.service.UserServiceImpl.test")
    public Object around(ProceedingJoinPoint joinPoint) {
        Object result=null;
        try {
            System.out.println("前置通知.....");
            result=joinPoint.proceed();
            System.out.println("正常执行后置通知.....");
        } catch (Throwable throwable) {
            System.out.println("异常执行通知.....");
            throwable.printStackTrace();
        } finally {
            System.out.println("最终通知......");
        }
       return result;
    }

//    @Around(execution = "com.cqw.service.OrderServiceImpl.test")
//    public Object aroundOrder(ProceedingJoinPoint joinPoint) {
//        Object result=null;
//        try {
//            System.out.println("前置通知.....");
//            result=joinPoint.proceed();
//            System.out.println("正常执行后置通知.....");
//        } catch (Throwable throwable) {
//            System.out.println("异常执行通知.....");
//            throwable.printStackTrace();
//        } finally {
//            System.out.println("最终通知......");
//        }
//        return result;
//    }
}

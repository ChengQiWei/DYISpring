package com.spring;


import net.sf.cglib.proxy.Enhancer;
import net.sf.cglib.proxy.MethodInterceptor;
import net.sf.cglib.proxy.MethodProxy;

import java.lang.reflect.Method;
import java.sql.Connection;
import java.util.List;

public class CglibProxy {

    Class<?> targetClass;

    List<String> transactionalMethod;

    public CglibProxy(Class<?> targetClass, List<String> transactionalMethod) {
        this.targetClass = targetClass;
        this.transactionalMethod = transactionalMethod;
    }

    public   Object getCglibProxy(){

        Enhancer enhancer=new Enhancer();
        enhancer.setSuperclass(targetClass);
        enhancer.setCallback(new MethodInterceptor() {
            @Override
            public Object intercept(Object o, Method method, Object[] args,
                                    MethodProxy methodProxy) throws Throwable {
                Object result =null;
                if(transactionalMethod.contains(method.getName())){
                    //开启事务
                    //从threadLocal当中获取connection，关闭自动提交
                   Connection connection=
                           TransactionManager.getThreadConnection();
                   connection.setAutoCommit(false);
                    System.out.println("cglib事务前置。。。。");
                    try {
                        result=methodProxy.invokeSuper(o,args);
                        //手动提交
                        connection.commit();
                        System.out.println("cglib事务后置。。。。");
                    } catch (Throwable throwable) {
                        //回滚事务
                        connection.rollback();
                        throwable.printStackTrace();
                    }
                    //关闭事务
                }
                else{
                    result=methodProxy.invokeSuper(o,args);
                }
                return result;
            }
        });
        return enhancer.create();
    }
}

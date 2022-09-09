package com.spring;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;


public class JdkProxy<T> {

    //目标对象类
    private Class<?> targetClass;
    //执行目标对象的方法名，用于之后与method对比
    private String methodName;

    //目标对象 用于之后proceedingJoinPoint调用
    private Object targetObject;
     //aop方法，因为之后要调用这个方法，执行这个方法里的逻辑
    private Method aopMethod;

    private Class<?> aopClass;

    public JdkProxy(Class<?> targetClass, String methodName, Object targetObject, Method aopMethod, Class<?> aopClass) {
        this.targetClass = targetClass;
        this.methodName = methodName;
        this.targetObject = targetObject;
        this.aopMethod = aopMethod;
        this.aopClass = aopClass;
    }


    public Object getProxyInstance(){

        //目标类的代理类通过调用aop的method方法进而调用目标类的method方法；如果不进行aop，那么直接执行目标对象的方法
        //通过反编译可以发现，代理类是直接调用的InvocationHandler中的invoke（）方法来执行方法
        return Proxy.newProxyInstance(targetClass.getClassLoader(),
                targetClass.getInterfaces(), new InvocationHandler() {
                    @Override
                    //proxy 代理对象 method 要代理的方法 args代理参数
                    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
                       if(methodName.equals(method.getName())){
                           ProceedingJoinPoint proceedingJoinPoint =
                                   new ProceedingJoinPoint(method,
                                           targetObject,args);
                           //执行aop切面类的method对象,
                           // 就可以执行aop中的一系列内容，同时内部执行proceedingjoinpoint
                           // 的方法，来调用目标类的的方法
                          return aopMethod.invoke(aopClass.newInstance(),
                                   proceedingJoinPoint);
                       }
                       else {
                          return method.invoke(targetObject,args);
                       }
                    }
                });
    }
}

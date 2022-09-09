package com.spring;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.sql.Connection;
import java.util.List;

public class TransProxy {


    //目标对象类
    private Class<?> targetClass;
    //执行目标对象的方法名，用于之后与method对比
    private Object targetObject;
    private List<String> transactionalMethod;

    public TransProxy(Class<?> targetClass,  Object targetObject,
                      List<String> transactionalMethod) {
        this.targetClass = targetClass;
        this.transactionalMethod = transactionalMethod;
        this.targetObject =targetObject;
    }

    public Object getProxyInstance(){

        //目标类的代理类通过调用aop的method方法进而调用目标类的method方法；如果不进行aop，那么直接执行目标对象的方法
        //通过反编译可以发现，代理类是直接调用的InvocationHandler中的invoke（）方法来执行方法
        return Proxy.newProxyInstance(targetClass.getClassLoader(),
                targetClass.getInterfaces(), new InvocationHandler() {
                    @Override
                    //proxy 代理对象 method 要代理的方法 args代理参数
                    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
                        Object result=null;
                        if(transactionalMethod.contains(method.getName())){
                            Connection connection=
                                    TransactionManager.getThreadConnection();
                            connection.setAutoCommit(false);
                            System.out.println("事务前置。。。。");
                            try {
                                result=method.invoke(targetObject,args);
                                //手动提交
                                connection.commit();
                                System.out.println("事务后置。。。。");
                            } catch (Throwable throwable) {
                                //回滚事务
                                connection.rollback();
                                System.out.println("事务失败回滚。。。。");
                                throwable.printStackTrace();
                            }
                        }
                        else {
                            result=method.invoke(targetObject,args);
                        }
                        return result;
                    }
                });
    }




}

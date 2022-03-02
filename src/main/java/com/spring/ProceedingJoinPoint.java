package com.spring;


/*连接点*/

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class ProceedingJoinPoint {


    private Method method;

    private Object target;
    private  Object[] args;

    public ProceedingJoinPoint(Method method, Object target, Object[] args) {
        this.method = method;
        this.target = target;
        this.args = args;
    }

    public Object proceed() {
        Object result=null;
        try {
            result=method.invoke(target,args);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } finally {
        }
        return result;
    }
}

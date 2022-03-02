package com.spring;

public class BeanDefinition {


    private  Class clazz;
    //bean类型
    private String scope;

//    public BeanDefinition(Class clazz, String scope) {
//        this.clazz = clazz;
//        this.scope = scope;
//    }

    public Class getClazz() {
        return clazz;
    }

    public String getScope() {
        return scope;
    }

    public void setClazz(Class clazz) {
        this.clazz = clazz;
    }

    public void setScope(String scope) {
        this.scope = scope;
    }
}

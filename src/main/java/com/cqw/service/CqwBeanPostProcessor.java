package com.cqw.service;

import com.spring.BeanPostProcessor;
import com.spring.Component;

@Component("cqwBeanPostProcessor")
public class CqwBeanPostProcessor implements BeanPostProcessor {


    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName) {
        System.out.println(beanName+"初始化前......");
        return bean;
    }

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) {
        System.out.println(beanName+"初始化后......");
        return bean;
    }
}

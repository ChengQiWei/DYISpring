package com.cqw;

import com.cqw.service.OrderService;
import com.cqw.service.UserService;
import com.spring.CqwApplicationContext;

public class Test {

    public static void main(String[] args) throws IllegalAccessException {

        CqwApplicationContext cqwApplicationContext =
                new CqwApplicationContext(AppConfig.class);

        UserService userServiceImpl=(UserService)cqwApplicationContext.getBean(
                "userServiceImpl");

        userServiceImpl.test();
        userServiceImpl.getOrderServiceImpl().order();

        OrderService orderServiceImpl=(OrderService)cqwApplicationContext.getBean(
                "orderServiceImpl");

        orderServiceImpl.order();


    }
}

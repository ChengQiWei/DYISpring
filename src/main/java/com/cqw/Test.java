package com.cqw;

import com.cqw.service.OrderService;
import com.cqw.service.UserService;
import com.spring.CqwApplicationContext;

public class Test {

    public static void main(String[] args) {

        CqwApplicationContext cqwApplicationContext =
                new CqwApplicationContext(AppConfig.class);

        UserService userService=(UserService)cqwApplicationContext.getBean(
                "userService");

        userService.test();


    }
}

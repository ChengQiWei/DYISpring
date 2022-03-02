package com.cqw.service;


import com.spring.Autowired;
import com.spring.Component;

@Component("orderServiceImpl")
public class OrderServiceImpl implements OrderService {


    @Override
    public  void order(){
        System.out.println("orderService接口.....");
    }

}

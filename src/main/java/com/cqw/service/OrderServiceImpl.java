package com.cqw.service;


import com.spring.Autowired;
import com.spring.Component;
import com.spring.Transactional;

@Component("orderServiceImpl")
public class OrderServiceImpl implements OrderService {

    @Autowired
    OrderDao orderDaoImpl;

    @Override
    public  void order(){
        System.out.println("orderService接口.....");
    }

    @Override
    @Transactional
    public  void  tranfer(){

        orderDaoImpl.tranfr("Jetty",1000);
        int i=1/0;
           System.out.println("转账成功");

        orderDaoImpl.tranfr("Mark",-1000);

    }

    public  void test(){
        System.out.println(this.getClass());
    }

}

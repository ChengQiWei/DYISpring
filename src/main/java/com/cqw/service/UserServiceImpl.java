package com.cqw.service;


import com.spring.*;

@Component("userServiceImpl")
public class UserServiceImpl implements BeanNameAware, InitializingBean ,UserService{

     @Autowired
     private OrderService orderServiceImpl;

     private String beanName;

    @Override
    public void setBeanName(String name) {
        this.beanName=name;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        System.out.println("初始化.........");
    }

    @Override
    public  void test(){
         System.out.println(orderServiceImpl);
         System.out.println(beanName);
     }

    public OrderService getOrderServiceImpl() {
        return orderServiceImpl;
    }
}

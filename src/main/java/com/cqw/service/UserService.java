package com.cqw.service;


import com.spring.*;

@Component("userService")
public class UserService implements BeanNameAware, InitializingBean {

     @Autowired
     private OrderService orderService;

     private String beanName;

    @Override
    public void setBeanName(String name) {
        this.beanName=name;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        System.out.println("初始化.........");
    }

    public  void test(){
         System.out.println(orderService);
         System.out.println(beanName);
     }
}

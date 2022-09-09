package com.cqw.service;

import com.spring.Component;
import com.spring.TransactionManager;

import java.sql.*;

@Component("orderDaoImpl")
public class OrderDaoImpl implements OrderDao{

    @Override
    public  int tranfr (String name,int balance) {

        int result=0;
        try {

           //从TransactionManager中获取公共连接
            Connection conn = TransactionManager.getThreadConnection();

            PreparedStatement ps = conn.prepareStatement("update book_user set" +
                    " balance=balance+? where name =?");
            ps.setObject(1, balance);
            ps.setObject(2, name);

           result= ps.executeUpdate();


        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }  finally {
        }
        return result;
    }
}

package com.spring;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

/*事务管理器*/
public class TransactionManager {


    //本地线程局部变量
    static ThreadLocal<Connection> threadLocal=new ThreadLocal<>();

    static {

        Connection connection = getConnection();
        threadLocal.set(connection);

    }

    public static  Connection getThreadConnection(){

        return threadLocal.get();
    }

    public static  Connection getConnection(){

        try {
            Class.forName("com.mysql.cj.jdbc.Driver");

            Connection conn = DriverManager.getConnection("jdbc:mysql" +
                            "://localhost:3306/test01?characterEncoding=utf-8" +
                            "&&serverTimezone=GMT","root",
                    "123456");

            return conn;

        } catch (SQLException throwables) {
            throwables.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } finally {
        }

        return null;

    }


}

package cn.itcast.database;

import java.sql.SQLException;

public class Mysql_regist_found extends Mysql_JDBC{

    public void run(){

        //提交修改密码信息
        try {
            myStatement(Person_edit.sql);
        } catch (SQLException e) {
            System.out.println("查账户未注册");
            e.printStackTrace();
        }
    }
}

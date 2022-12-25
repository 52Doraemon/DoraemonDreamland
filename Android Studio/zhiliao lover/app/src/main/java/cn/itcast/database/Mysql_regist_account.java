package cn.itcast.database;

import android.widget.Toast;

import java.sql.Connection;
import java.sql.SQLException;

public class Mysql_regist_account extends Mysql_JDBC{

    public void run(){
        //提交登录数据
        try {
            myStatement(Regist_account.sql);
            Regist_account.verify = true;
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}

package cn.itcast.database;

import android.content.Intent;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class Mysql_verify extends Mysql_JDBC{

    public void run(){
        //验证用户账号密码
        if(log_in()) {
            Login.verify = true;
        }
    }
}

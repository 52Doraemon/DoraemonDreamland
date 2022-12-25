package cn.itcast.database;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.system.Os;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.mysql.jdbc.AbandonedConnectionCleanupThread;

import java.util.concurrent.BlockingDeque;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class Login extends AppCompatActivity {
    public static String user_name;
    public static boolean verify = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_xml);

        Intent intent = getIntent();
        user_name = intent.getStringExtra("user_name");

        //初始化控件
        EditText name = findViewById(R.id.user_Name);
        EditText password = findViewById(R.id.user_password);
        Button btn_log = findViewById(R.id.btn_login);
        TextView regist = findViewById(R.id.user_regist);
        TextView found_password = findViewById(R.id.user_found_password);

        name.setText(user_name);

        //登录
        btn_log.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //如果输入的用户名和密码都不为空则连接数据库验证登录
                //获取数据库里面的用户名和密码，并与输入框的用户名和密码比较
                //如果密码一致则将result设置为true，表示验证成功

                //获取输入的用户名和密码
                String user_name = name.getText().toString().trim();
                String user_password = password.getText().toString().trim();
                if (!user_name.isEmpty() && !user_password.isEmpty()) {

                    //ExecutorService 会自动提供一个线程池和相关 API，用于为其分配任务。
                    ExecutorService executor = Executors.newSingleThreadExecutor();

                    //添加任务的方法：
                    executor.execute(new Mysql_JDBC());
                    Mysql_JDBC.Mysql_sql(user_name, user_password);

                    executor.execute(new Mysql_verify());

                    //线程加锁
                    try {
                        Mysql_verify.sleep(500);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                    //终止的方法：
                    executor.shutdown();//等待线程池中任务执行完后（期间不再接受新任务），再关闭

                    if(verify){
                        //登录跳转。如果验证成功，跳转到主界面，并结束当前页面
                        Intent intent = new Intent(Login.this, MainActivity.class);
                        intent.putExtra("user_name", user_name);
                        Toast.makeText(Login.this, "登录成功！", Toast.LENGTH_SHORT).show();
                        startActivity(intent);
                        finish();
                    } else
                        Toast.makeText(Login.this, "用户名或密码错误，请重新输入！", Toast.LENGTH_SHORT).show();
                }
                }
        });

        //注册
        regist.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Login.this, Regist_account.class);
                startActivity(intent);
            }
        });
        //找回密码
        found_password.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //跳转到找回密码界面
                Intent intent = new Intent(Login.this, Regist_found.class);
                startActivity(intent);
            }
        });
    }
}

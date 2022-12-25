package cn.itcast.database;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Looper;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Logger;

public class Regist_found extends AppCompatActivity {
    protected static String sql;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.regist_found_password);

        EditText regist_username = findViewById(R.id.regist_number_edittext);
        EditText regist_password = findViewById(R.id.regist_password_edittext);
        EditText regist_confirm_password = findViewById(R.id.regist_password_confirm_edittext);
//        EditText regist_verification_code = findViewById(R.id.regist_verification_code_edittext);

//        Button btn_verification_code = findViewById(R.id.btn_verification_code);
        Button btn_found = findViewById(R.id.regist_found);

//        btn_verification_code.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                //根据手机号发送验证码
//            }
//        });

        btn_found.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String user_name = regist_username.getText().toString().trim();
                String password = regist_password.getText().toString().trim();
                String confirm_password = regist_confirm_password.getText().toString().trim();
                //提交修改密码信息
                //“equals()”比较字符串中所包含的内容是否相同
                if (password.equals(confirm_password)) {

                    //sql命令语句
                    sql = "update user set user_password=\""+confirm_password+"\" where user_name = \""+user_name+"\"";

                    //提交登录数据
                    //ExecutorService 会自动提供一个线程池和相关 API，用于为其分配任务。
                    ExecutorService executor = Executors.newSingleThreadExecutor();

                    //添加任务的方法：
                    executor.execute(new Mysql_regist_found());

                    //终止的方法：
                    executor.shutdown();//等待线程池中任务执行完后（期间不再接受新任务），再关闭

                    //除了Activity ui线程默认创建之外，其他线程不会自动创建调用 Looper.prepare()来给线程创建消息循环，然后再通过，Looper.loop()来使消息循环起作用。
                    new Thread(){
                        @Override
                        public void run() {
                            super.run();
                            Looper.prepare();
                            try {
                                Toast.makeText(Regist_found.this,"该用户名未注册",Toast.LENGTH_SHORT).show();
                            }catch (Exception e) {
                                Logger.getLogger("error",e.toString());
                            }
                            Looper.loop();
                        }
                    }.start();
                }
                if(true){
                    //修改密码成功跳转。如果验证成功，跳转到登录界面，并结束当前页面
                    Intent intent = new Intent(Regist_found.this, Login.class);
                    intent.putExtra("user_name", user_name);
                    Toast.makeText(Regist_found.this, "修改密码成功！", Toast.LENGTH_SHORT).show();
                    startActivity(intent);
                    finish();
                }
                else
                    Toast.makeText(Regist_found.this,"密码和确认密码不一致，请重新输入",Toast.LENGTH_SHORT).show();
            }
        });

        ImageView back = findViewById(R.id.arrow_back);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

    }
}
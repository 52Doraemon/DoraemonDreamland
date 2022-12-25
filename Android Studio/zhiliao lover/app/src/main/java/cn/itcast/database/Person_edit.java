package cn.itcast.database;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ActionBar;
import android.content.Intent;
import android.os.Bundle;
import android.os.Looper;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.Toast;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Logger;

public class Person_edit extends AppCompatActivity implements View.OnClickListener {

    protected static String sql;
    public static boolean verify = false;
//    public static String OLD_user_name;
    private ImageView[] img;
    private int[] img_id = {R.id.avatar1, R.id.avatar2, R.id.avatar3, R.id.avatar4, R.id.avatar5, R.id.avatar6};
    private int get_id = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.edit_personal_data);

//        Intent intent = getIntent();
//        OLD_user_name = intent.getStringExtra("OLD_user_name");

        //初始化控件
        EditText user, style;
        user = findViewById(R.id.user_name);
        style = findViewById(R.id.style_name);
        create_img();

        //提交保存数据
        Button save = findViewById(R.id.btn_save);
        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String user_name, style_name;
                user_name = user.getText().toString().trim();//修改的用户名
                style_name = style.getText().toString().trim();//个性签名
//                System.out.println(get_id);//get_id为当前图片的下标，直接放到数据库即可

                //这里连接数据库，提交修改的用户数据

                //sql命令语句
                sql = "UPDATE USER \n" +
                        "\tSET user_name = \"" + user_name + "\",\n" +
                        "\tuser_photo = " + get_id + ",\n" +
                        "\tuser_signature = \"" + style_name + "\"\n" +
                        "WHERE\n" +
                        "\tuser_name = \"" + MainActivity.user_name + "\"";

                new Thread() {
                    @Override
                    public void run() {
                        super.run();
                        Connection conn = null;
                        try {
                            conn = Mysql_JDBC.myConnection();
                            Statement statement1 = null;
                            statement1 = conn.createStatement();
                            Statement statement2 = null;
                            statement2 = conn.createStatement();
                            ResultSet result = null;
                            result = statement1.executeQuery("select * from user where user_name ='"+user_name+"'");
                            if (result.next()) {
                                verify=false;
                            } else {
                                verify=true;
                                statement2.execute(sql);

                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }.start();
                try {
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                if(verify){
                    //修改个人信息成功跳转。如果验证成功，跳转返回个人界面，并结束当前页面
                    MainActivity.user_name = user_name;
                    Intent intent = new Intent(Person_edit.this, MainActivity.class);
                    intent.putExtra("user_name", user_name);
                    Toast.makeText(Person_edit.this, "编辑资料成功！", Toast.LENGTH_SHORT).show();
                    startActivity(intent);
                    finish();
                }else Toast.makeText(Person_edit.this, "编辑个人信息失败，用户名重名", Toast.LENGTH_SHORT).show();
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

    @Override
    public void onClick(View view) {
        for (int i = 0; i < img_id.length; i++) {
            img[i].setSelected(false);
            if (view.getId() == img_id[i]) {
                get_id = i;
                img[i].setSelected(true);
            }
        }
    }

    private void create_img() {
        img = new ImageView[img_id.length];
        for (int i = 0; i < img.length; i++) {
            img[i] = findViewById(img_id[i]);
            img[i].setImageResource(MainActivity.img[i]);
            img[i].setSelected(false);
            img[i].setOnClickListener(this);
        }
    }

}
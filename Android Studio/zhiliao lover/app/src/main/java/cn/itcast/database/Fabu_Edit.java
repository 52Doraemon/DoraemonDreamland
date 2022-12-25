package cn.itcast.database;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Fabu_Edit extends AppCompatActivity implements View.OnClickListener {

    private EditText inputText, gl_title;
    private String string, userID, date;
    @SuppressLint("SimpleDateFormat")
    private SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");  //获取时间函数 dateFormat.format(date);
    private ImageView editDelete, confirm;
    private RadioGroup rg;
    private Connection conn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.publish_works);
        userID = MainActivity.user_name;
        inputText = (EditText) findViewById(R.id.work_value);
        editDelete = (ImageView) findViewById(R.id.add_delete);
        confirm = (ImageView) findViewById(R.id.confirm);
        rg = (RadioGroup) findViewById(R.id.radiogroup);
        gl_title = findViewById(R.id.gl_title);

        RadioButton yh, dt;
        dt = findViewById(R.id.dt);
        dt.setOnClickListener(this);
        yh = findViewById(R.id.yh);
        yh.setOnClickListener(this);

        editDelete.setOnClickListener(this);
        confirm.setOnClickListener(this);

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
        switch (view.getId()) {
            case R.id.dt:
                gl_title.setVisibility(View.INVISIBLE);
                break;
            case R.id.yh:
                gl_title.setVisibility(View.VISIBLE);
                break;
            case R.id.add_delete:
                inputText.setText("");
                inputText.clearFocus();
                break;
            case R.id.confirm:
                if (view.equals(confirm)) {
                    String value = inputText.getText().toString().trim();
                    String title = gl_title.getText().toString().trim();
                    if (value.isEmpty()) {
                        Toast.makeText(this, "输入内容不能为空！", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    boolean flag = false;
                    date = dateFormat.format(new Date());
                    for (int i = 0; i < rg.getChildCount(); i++) {
                        RadioButton rb = (RadioButton) rg.getChildAt(i);
                        if (rb.isChecked()) {
                            //用户名为类成员变量的userID
                            //时间为date
                            //上方value即为输入内容
                            //i的值决定发送到哪个分区
                            //0则发送到动态广场
                            //1则发送到约会攻略
                            //这里进行连接数据库操作
                            if (i == 0) {
                                //发布到动态广场
                                new Thread() {
                                    @Override
                                    public void run() {
                                        try {
                                            if (conn == null) conn = Mysql_JDBC.myConnection();
                                            Statement statement1 = conn.createStatement();
                                            Statement statement2 = conn.createStatement();
                                            Statement statement3 = conn.createStatement();
                                            ResultSet result1 = statement1.executeQuery("select * from dongtai");
                                            ResultSet result2 = statement2.executeQuery("select * from user where user_name='" + MainActivity.user_name + "'");
                                            int id = 1, img = 0;
                                            String user_name = "null";
                                            if (result1.next() && result2.next()) {
                                                result1.last();
                                                result2.first();
                                                id = result1.getRow() + 1;
                                                user_name = result2.getString(1);
                                                img = result2.getInt(4);
                                                result1.close();
                                                result2.close();
                                            }
                                            statement3.execute("insert into dongtai values(" + id + ",'" + MainActivity.user_name + "'," + img + ",'" + value + "','" + date + "',0,0,0)");
                                            Statement statement4 = conn.createStatement();
                                            statement4.execute("update user set user_works = user_works + 1 where user_name ='" + user_name + "'");
                                        } catch (Exception e) {
                                            e.printStackTrace();
                                        }
                                    }
                                }.start();
                                Toast.makeText(this, "发布成功！", Toast.LENGTH_SHORT).show();
                                finish();
                            } else {
                                //发布到约会攻略
                                if (title.isEmpty()) {
                                    Toast.makeText(this, "标题不能为空！", Toast.LENGTH_SHORT).show();
                                    return;
                                }
                                new Thread() {
                                    @Override
                                    public void run() {
                                        try {
                                            if (conn == null) conn = Mysql_JDBC.myConnection();
                                            Statement statement1 = conn.createStatement();
                                            Statement statement2 = conn.createStatement();
                                            Statement statement3 = conn.createStatement();
                                            ResultSet result1 = statement1.executeQuery("select * from strategy");
                                            ResultSet result2 = statement2.executeQuery("select * from user where user_name='" + MainActivity.user_name + "'");
                                            int id = 1, img = 0;
                                            String user_name = "null";
                                            if (result1.next() && result2.next()) {
                                                result1.last();
                                                result2.first();
                                                id = result1.getRow() + 1;
                                                user_name = result2.getString(1);
                                                img = result2.getInt(4);
                                                result1.close();
                                                result2.close();
                                            }
                                            statement3.execute("insert into strategy values(" + id + ",'" + MainActivity.user_name + "'," + img + ",'" + value + "','" + date + "',0,0,0,'" + title + "')");
                                            Statement statement4 = conn.createStatement();
                                            statement4.execute("update user set user_works = user_works + 1 where user_name ='" + MainActivity.user_name + "'");
                                            //System.out.println(id + " " + user_name + " " + img + " " + value + " " + date + " " + title);
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
                                Toast.makeText(this, "发布成功！", Toast.LENGTH_SHORT).show();
                                finish();
                            }
                            flag = true;
                            break;
                        }
                    }
                    if (!flag) Toast.makeText(this, "请选择发送分区", Toast.LENGTH_SHORT).show();
                }
        }
    }

}
package cn.itcast.database;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Gong_lue_detail extends AppCompatActivity {

    private int ID;
    private String[] user_name = new String[0];//评论用户名
    private String[] value = new String[0];//评论内容
    private String[] date = new String[0];//时间
    private int[] user_img = new int[0];//头像
    private boolean zang;
    private Connection conn;

    private class DdtAdapter extends RecyclerView.Adapter<DdtAdapter.ViewHolder> {
        @NonNull
        @Override
        public DdtAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.dynamic_detail_re, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull DdtAdapter.ViewHolder holder, int position) {
            holder.icon.setImageResource(MainActivity.img[user_img[position]]);
            holder.name.setText(user_name[position]);
            holder.date.setText(date[position]);
            holder.value.setText(value[position]);
        }

        @Override
        public int getItemCount() {
            return user_name.length;
        }

        public class ViewHolder extends RecyclerView.ViewHolder {

            ImageView icon;
            TextView name, date, value;

            public ViewHolder(@NonNull View itemView) {
                super(itemView);
                icon = itemView.findViewById(R.id.user_photo);
                name = itemView.findViewById(R.id.user_name);
                date = itemView.findViewById(R.id.user_time);
                value = itemView.findViewById(R.id.user_discuss);
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.strategy_detail);

        Intent intent = getIntent();
        ID = intent.getIntExtra("glID", -1);

        //初始化控件
        TextView title, name, values, date;
        ImageView img;
        img = findViewById(R.id.img);
        title = findViewById(R.id.title);
        name = findViewById(R.id.user_name);
        values = findViewById(R.id.dynamic_discuss);
        date = findViewById(R.id.publish_time);

        //将约会攻略页面传过来的值初始化到页面中
        String user_name = intent.getStringExtra("name");
        title.setText(intent.getStringExtra("title"));//设置标题
        name.setText(intent.getStringExtra("name"));//设置用户名
        values.setText(intent.getStringExtra("values"));//设置内容
        date.setText(intent.getStringExtra("date"));//设置时间
        img.setImageResource(MainActivity.img[intent.getIntExtra("img", -1)]);//设置头像
        zang = intent.getBooleanExtra("zang", false);
        //发送请求获取用户评论数据并初始化到类成员变量中
        try {
            update();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        EditText ouser_value = findViewById(R.id.ouser_discuss_values);
        Button ouser_send = findViewById(R.id.ouser_discuss_send);
        ouser_send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String my_pl_value = ouser_value.getText().toString().trim();
                if (!my_pl_value.isEmpty()) {
                    //如果评论不为空，则连接数据库发布评论
                    new Thread() {
                        @Override
                        public void run() {
                            try {
                                if (conn == null) conn = Mysql_JDBC.myConnection();
                                Statement statement = conn.createStatement();
                                ResultSet user_result = statement.executeQuery("select * from user where user_name= '" + MainActivity.user_name + "'");
                                String user = null;
                                int user_img = 0;
                                if (user_result.next()) {
                                    user_result.first();
                                    user = user_result.getString(1);
                                    user_img = user_result.getInt(4);
                                }
                                @SuppressLint("SimpleDateFormat")
                                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                                String date = dateFormat.format(new Date());
                                user_result.close();
                                statement.execute("insert into strategy_comment values(" + ID + ",'" + user + "'," + user_img + ",'" + my_pl_value + "','" + date + "')");
                                Statement statement1 = conn.createStatement();
                                Statement statement2 = conn.createStatement();
                                statement1.execute("update user set user_comments = user_comments + 1 where user_name = '" + user + "'");
                                statement2.execute("update strategy set strategy_comments = strategy_comments + 1 where strategy_id = " + ID);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    }.start();
                    Toast.makeText(Gong_lue_detail.this, "发布成功！", Toast.LENGTH_SHORT).show();
                    ouser_value.setText("");
                    ouser_value.clearFocus();
                    try {
                        Thread.sleep(500);
                        update();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                } else Toast.makeText(Gong_lue_detail.this, "输入内容不能为空！", Toast.LENGTH_SHORT).show();
            }
        });
        ImageView back = findViewById(R.id.dynamic_back);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        ImageView z = findViewById(R.id.ouser_discuss_z);
        z.setSelected(zang);
        z.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                zang = !zang;
                z.setSelected(zang);
                new Thread() {
                    @Override
                    public void run() {
                        try {
                            if (conn == null) conn = Mysql_JDBC.myConnection();
                            Statement statement = conn.createStatement();
                            Statement z = conn.createStatement();
                            Statement get_z = conn.createStatement();
                            if (zang) {
                                statement.execute("insert into strategy_likes_logs values(" + ID + ",'" + MainActivity.user_name + "')");
                                z.execute("update strategy set strategy_likes = strategy_likes + 1 where strategy_id =" + ID);
                                get_z.execute("update user set user_likes_all = user_likes_all + 1 where user_name ='" + MainActivity.user_name + "'");
                            } else {
                                statement.execute("delete from strategy_likes_logs where Likes_strategy_id = " + ID + " and user_id = '" + MainActivity.user_name + "'");
                                z.execute("update strategy set strategy_likes = strategy_likes - 1 where strategy_id =" + ID);
                                get_z.execute("update user set user_likes_all = user_likes_all - 1 where user_name ='" + MainActivity.user_name + "'");
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }.start();
            }
        });
    }

    private void update() throws InterruptedException {
        new Thread() {
            @Override
            public void run() {
                try {
                    if (conn == null) conn = Mysql_JDBC.myConnection();
                    Statement statement = conn.createStatement();
                    ResultSet resultSet = statement.executeQuery("select * from strategy_comment where strategy_id =" + ID);
                    if (resultSet.next()) {
                        resultSet.last();
                        int length = resultSet.getRow();
                        user_name = new String[length];
                        value = new String[length];
                        user_img = new int[length];
                        date = new String[length];
                        resultSet.first();
                        int i = 0;
                        @SuppressLint("SimpleDateFormat") SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
                        Date d;
                        while (!resultSet.isAfterLast()) {
                            //更新上方数组数据
                            user_name[i] = resultSet.getString(2);
                            user_img[i] = resultSet.getInt(3);
                            value[i] = resultSet.getString(4);
                            d = dateFormat.parse(resultSet.getString(5));
                            date[i] = dateFormat.format(d);
                            i++;
                            resultSet.next();
                        }
                    }
                    resultSet.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }.start();
        Thread.sleep(500);
        LinearLayoutManager layoutManager = new LinearLayoutManager(Gong_lue_detail.this);
        DdtAdapter newAdapter = new DdtAdapter();
        RecyclerView recyclerView = findViewById(R.id.detail);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(newAdapter);
    }
}
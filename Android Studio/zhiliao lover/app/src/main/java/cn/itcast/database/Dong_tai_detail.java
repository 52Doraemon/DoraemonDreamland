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

public class Dong_tai_detail extends AppCompatActivity {

    private int ID;
    private String[] user_name = new String[0];//评论用户名
    private String[] value = new String[0];//评论内容
    private String[] date = new String[0];//时间
    private int[] user_img = new int[0];//头像
    private boolean zang = false;//是否点赞
    private Connection conn = null;

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
        setContentView(R.layout.dynamic_detail);

        Intent intent = getIntent();
        ID = intent.getIntExtra("dyID", -1);
        String name = intent.getStringExtra("name");//主动态用户名
        String date = intent.getStringExtra("date");//主时间
        String value = intent.getStringExtra("value");//主内容
        int user_icon = intent.getIntExtra("user_icon", -1);//用户头像

        TextView Name, Date, Value;
        ImageView Icon;
        Name = findViewById(R.id.publish_user_name);
        Date = findViewById(R.id.publish_time);
        Value = findViewById(R.id.dynamic_discuss);
        Icon = findViewById(R.id.profile_photo);
        Name.setText(name);
        Date.setText(date);
        Value.setText(value);
        Icon.setImageResource(MainActivity.img[user_icon]);

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
                                statement.execute("insert into dongtai_comment values(" + ID + ",'" + user + "'," + user_img + ",'" + my_pl_value + "','" + date + "')");
                                Statement statement1 = conn.createStatement();
                                Statement statement2 = conn.createStatement();
                                statement1.execute("update user set user_comments = user_comments + 1 where user_name = '" + user + "'");
                                statement2.execute("update dongtai set dongtai_comments = dongtai_comments + 1 where dongtai_id = " + ID);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    }.start();
                    Toast.makeText(Dong_tai_detail.this, "发布成功！", Toast.LENGTH_SHORT).show();
                    ouser_value.setText("");
                    ouser_value.clearFocus();
                    try {
                        Thread.sleep(500);
                        update();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                } else Toast.makeText(Dong_tai_detail.this, "输入内容不能为空！", Toast.LENGTH_SHORT).show();
            }
        });
        ImageView back = findViewById(R.id.dynamic_back);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        //点赞按钮
        ImageView z = findViewById(R.id.ouser_discuss_z);
        zang = intent.getBooleanExtra("zang", false);
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
                                statement.execute("insert into dongtai_likes_logs values(" + ID + ",'" + MainActivity.user_name + "')");
                                z.execute("update dongtai set dongtai_likes = dongtai_likes + 1 where dongtai_id =" + ID);
                                get_z.execute("update user set user_likes_all = user_likes_all + 1 where user_name ='" + name + "'");
                            } else {
                                statement.execute("delete from dongtai_likes_logs where Likes_dongtai_id = " + ID + " and user_name = '" + MainActivity.user_name + "'");
                                z.execute("update dongtai set dongtai_likes = dongtai_likes - 1 where dongtai_id =" + ID);
                                get_z.execute("update user set user_likes_all = user_likes_all - 1 where user_name ='" + name + "'");
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
        //根据该动态ID更新该动态的评论数据
        //连接数据库，更新上面数据
        new Thread() {
            @Override
            public void run() {
                try {
                    if (conn == null) conn = Mysql_JDBC.myConnection();
                    Statement statement = conn.createStatement();
                    ResultSet resultSet = statement.executeQuery("select * from dongtai_comment where dongtai_id =" + ID);
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
        LinearLayoutManager layoutManager = new LinearLayoutManager(Dong_tai_detail.this) {
            @Override
            public boolean canScrollVertically() {
                return false;
            }
        };
        DdtAdapter ddtAdapter = new DdtAdapter();
        RecyclerView recyclerView = findViewById(R.id.dynamic_detail);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(ddtAdapter);
    }

}
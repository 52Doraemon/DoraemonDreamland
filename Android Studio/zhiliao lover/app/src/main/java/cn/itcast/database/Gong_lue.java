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
import android.widget.ImageView;
import android.widget.TextView;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Gong_lue extends AppCompatActivity {

    private int[] gl_ID = {0, 1, 2};//攻略ID
    private static String[] name = {"123", "456", "789"};//发布攻略的用户名
    private String[] title = {"123", "456", "789"};//攻略标题
    private String[] values = {"111", "222", "333"};//内容
    private int[] img = {0, 1, 2};//头像
    private String[] date = {"2022-6-18 12:00", "2022-6-20 15:22", "2022-6-22 16:40"};//时间
    private int[] pl = {120, 1256, 2300};//评论量
    private boolean[] zang = {false, true, false};//是否已被点赞
    private Connection conn;


    private class GlAdapter extends RecyclerView.Adapter<GlAdapter.ViewHolder> {

        @NonNull
        @Override
        public GlAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.main_fragment_dynamic, parent, false);
            return new GlAdapter.ViewHolder(view);
        }

        @SuppressLint("SetTextI18n")
        @Override
        public void onBindViewHolder(@NonNull GlAdapter.ViewHolder holder, int position) {
            holder.main_title.setText(title[position]);
            holder.main_value.setText(values[position]);
            holder.main_img.setImageResource(MainActivity.img[img[position]]);
            holder.main_date.setText(date[position] + " | " + pl[position] + "评论");
        }

        @Override
        public int getItemCount() {
            return title.length;
        }

        public class ViewHolder extends RecyclerView.ViewHolder {

            ImageView main_img;
            TextView main_title, main_value, main_date;

            public ViewHolder(@NonNull View itemView) {
                super(itemView);
                main_img = itemView.findViewById(R.id.main_dynamic_img);
                main_title = itemView.findViewById(R.id.main_dynamic_title);
                main_value = itemView.findViewById(R.id.main_dynamic_value);
                main_date = itemView.findViewById(R.id.main_dynamic_time);
                itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(Gong_lue.this, Gong_lue_detail.class);
                        int id = getAdapterPosition();
                        new Thread() {
                            @Override
                            public void run() {
                                try {
                                    if (conn == null) conn = Mysql_JDBC.myConnection();
                                    Statement statement1 = conn.createStatement();
                                    Statement statement2 = conn.createStatement();
                                    statement1.execute("update strategy set strategy_views = strategy_views+1 where user_name='" + Gong_lue.name[id] + "'");
                                    statement2.execute("update user set user_views = user_views + 1 where user_name='" + Gong_lue.name[id] + "'");
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                        }.start();
                        intent.putExtra("name", name[id]);
                        intent.putExtra("title", title[id]);
                        intent.putExtra("values", values[id]);
                        intent.putExtra("img", img[id]);
                        intent.putExtra("date", date[id]);
                        intent.putExtra("glID", gl_ID[id]);
                        intent.putExtra("zang", zang[id]);
                        startActivity(intent);
                    }
                });
            }
        }
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.strategy);
        try {
            update();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        ImageView back = findViewById(R.id.exit);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        try {
            update();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private void update() throws InterruptedException {
        //连接数据库，获取数据
        //更新上面数据即可
        new Thread() {
            @Override
            public void run() {
                try {
                    Statement statement = null;
                    ResultSet resultSet = null;
                    if (conn == null) conn = Mysql_JDBC.myConnection();
                    statement = conn.createStatement();
                    resultSet = statement.executeQuery("select * from strategy");
                    if (!resultSet.wasNull()) {
                        //游标不空则进行更新数据操作
                        resultSet.last();
                        int length = resultSet.getRow();
                        //初始化数据长度
                        gl_ID = new int[length];
                        name = new String[length];
                        title = new String[length];
                        values = new String[length];
                        img = new int[length];
                        date = new String[length];
                        pl = new int[length];
                        zang = new boolean[length];
                        resultSet.first();
                        int i = 0;
                        @SuppressLint("SimpleDateFormat") SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
                        Date d;
                        while (!resultSet.isAfterLast()) {
                            //更新上方数组数据
                            gl_ID[i] = resultSet.getInt(1);
                            name[i] = resultSet.getString(2);
                            img[i] = resultSet.getInt(3);
                            values[i] = resultSet.getString(4);
                            d = dateFormat.parse(resultSet.getString(5));
                            date[i] = dateFormat.format(d);
                            pl[i] = resultSet.getInt(7);
                            title[i] = resultSet.getString(9);
                            ResultSet is_zang;
                            Statement zs = conn.createStatement();
                            is_zang = zs.executeQuery("select * from strategy_likes_logs where Likes_strategy_id = " + gl_ID[i] + " and user_id = '" + MainActivity.user_name + "'");
                            zang[i] = is_zang.next();
                            is_zang.close();
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
        RecyclerView recyclerView = findViewById(R.id.strategy_re);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        GlAdapter glAdapter = new GlAdapter();
        recyclerView.setAdapter(glAdapter);
    }

}
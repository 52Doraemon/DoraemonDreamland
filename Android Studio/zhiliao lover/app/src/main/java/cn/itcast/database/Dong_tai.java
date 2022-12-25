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

public class Dong_tai extends AppCompatActivity {

    private static int[] dy_ID = {0, 1, 2};//动态ID
    private static String[] name = new String[0];//动态用户名
    private static String[] date = new String[0];//日期
    private static String[] value = new String[0];//动态内容
    private static int[] z = new int[0];//点赞数
    private static int[] p = new int[0];//评论数
    private static int[] user_icon = new int[0];//图标
    private static boolean[] zang = new boolean[0];//是否已被点赞
    private Connection conn = null;

    private class GetData extends Thread {

        String sql;

        public GetData(String sql) {
            this.sql = sql;
        }

        @Override
        public void run() {
            try {
                update_dt_data();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        private void update_dt_data() throws Exception {
            ResultSet resultSet;
            Statement statement;
            if (conn == null) conn = Mysql_JDBC.myConnection();
            statement = conn.createStatement();
            resultSet = statement.executeQuery(sql);
            if (resultSet.next()) {
                //游标不空则进行更新数据操作
                resultSet.last();
                int length = resultSet.getRow();
                //初始化数据长度
                dy_ID = new int[length];
                name = new String[length];
                value = new String[length];
                user_icon = new int[length];
                date = new String[length];
                p = new int[length];
                z = new int[length];
                zang = new boolean[length];
                resultSet.first();
                int i = 0;
                @SuppressLint("SimpleDateFormat") SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
                Date d;
                while (!resultSet.isAfterLast()) {
                    //更新上方数组数据
                    dy_ID[i] = resultSet.getInt(1);
                    name[i] = resultSet.getString(2);
                    user_icon[i] = resultSet.getInt(3);
                    value[i] = resultSet.getString(4);
                    d = dateFormat.parse(resultSet.getString(5));
                    date[i] = dateFormat.format(d);
                    z[i] = resultSet.getInt(6);
                    p[i] = resultSet.getInt(7);
                    //是否被点赞
                    ResultSet is_zang;
                    Statement zs = conn.createStatement();
                    is_zang = zs.executeQuery("select * from dongtai_likes_logs where Likes_dongtai_id = " + dy_ID[i] + " and user_name = '" + MainActivity.user_name + "'");
                    zang[i] = is_zang.next();
                    is_zang.close();
                    i++;
                    resultSet.next();
                }
            }
            resultSet.close();
        }
    }

    private class DtAdapter extends RecyclerView.Adapter<DtAdapter.ViewHolder> {

        @NonNull
        @Override
        public DtAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.dynamic_re, parent, false);
            return new ViewHolder(view);
        }

        @SuppressLint("SetTextI18n")
        @Override
        public void onBindViewHolder(@NonNull DtAdapter.ViewHolder holder, int position) {
            holder.name.setText(name[position]);
            holder.date.setText(date[position]);
            holder.value.setText(value[position]);
            holder.z.setText(Integer.toString(z[position]));
            holder.p.setText(Integer.toString(p[position]));
            holder.user_icon.setImageResource(MainActivity.img[user_icon[position]]);
            holder.tp.setSelected(zang[position]);
        }

        @Override
        public int getItemCount() {
            return name.length;
        }

        public class ViewHolder extends RecyclerView.ViewHolder {
            ImageView user_icon, tp;
            TextView name;
            TextView date;
            TextView value;
            TextView z;
            TextView p;

            public ViewHolder(@NonNull View itemView) {
                super(itemView);
                name = itemView.findViewById(R.id.publish_user_name);
                date = itemView.findViewById(R.id.publish_time);
                value = itemView.findViewById(R.id.dynamic_discuss);
                z = itemView.findViewById(R.id.number_thumb_up);
                p = itemView.findViewById(R.id.number_chat);
                user_icon = itemView.findViewById(R.id.profile_photo);
                tp = itemView.findViewById(R.id.dynamic_thumb_up);
                tp.setOnClickListener(new View.OnClickListener() {
                    @SuppressLint("SetTextI18n")
                    @Override
                    public void onClick(View view) {
                        int position = getAdapterPosition();
                        Dong_tai.zang[position] = !Dong_tai.zang[position];
                        tp.setSelected(Dong_tai.zang[position]);
                        if (Dong_tai.zang[position]) Dong_tai.z[position]++;
                        else Dong_tai.z[position]--;
                        z.setText(Integer.toString(Dong_tai.z[position]));
                        new Thread() {
                            @Override
                            public void run() {
                                //实现点赞操作
                                try {
                                    if (conn == null) conn = Mysql_JDBC.myConnection();
                                    Statement statement = conn.createStatement();
                                    Statement z = conn.createStatement();
                                    Statement get_z = conn.createStatement();
                                    if (Dong_tai.zang[position]) {
                                        statement.execute("insert into dongtai_likes_logs values(" + Dong_tai.dy_ID[position] +
                                                ",'" + MainActivity.user_name + "')");
                                        z.execute("update dongtai set dongtai_likes = dongtai_likes + 1 where dongtai_id =" + Dong_tai.dy_ID[position]);
                                        get_z.execute("update user set user_likes_all = user_likes_all + 1 where user_name ='" + Dong_tai.name[position] + "'");
                                    } else {
                                        statement.execute("delete from dongtai_likes_logs where Likes_dongtai_id = "
                                                + Dong_tai.dy_ID[position] + " and user_name = '" + MainActivity.user_name + "'");
                                        z.execute("update dongtai set dongtai_likes = dongtai_likes-1 where dongtai_id =" + Dong_tai.dy_ID[position]);
                                        get_z.execute("update user set user_likes_all = user_likes_all - 1 where user_name ='" + Dong_tai.name[position] + "'");
                                    }
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                        }.start();
                    }
                });
                itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        //每个动态绑定监听事件，点击即可跳转到详细动态页面
                        int position = getAdapterPosition();//获取数组下标

                        //点击之后浏览量加1
                        new Thread() {
                            @Override
                            public void run() {
                                try {
                                    if (conn == null) conn = Mysql_JDBC.myConnection();
                                    Statement statement1 = conn.createStatement();
                                    Statement statement2 = conn.createStatement();
                                    statement1.execute("update dongtai set dongtai_views = dongtai_views+1 where user_name='" + Dong_tai.name[position] + "'");
                                    statement2.execute("update user set user_views = user_views + 1 where user_name='" + Dong_tai.name[position] + "'");
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                        }.start();

                        Intent intent = new Intent(Dong_tai.this, Dong_tai_detail.class);
                        intent.putExtra("name", Dong_tai.name[position]);
                        intent.putExtra("user_icon", Dong_tai.user_icon[position]);
                        intent.putExtra("date", Dong_tai.date[position]);
                        intent.putExtra("value", Dong_tai.value[position]);
                        intent.putExtra("zang", Dong_tai.zang[position]);
                        intent.putExtra("dyID", Dong_tai.dy_ID[position]);
                        startActivity(intent);
                    }
                });
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dynamic);
        update();
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
        update();
    }

    //更新页面数据方法
    private void update() {
        //连接数据库，获取数据
        //数据内容包括：动态ID、动态用户名、用户头像、动态内容、发布时间
        //点赞数、评论数、是否以被当前用户点赞
        //更新上面数组数据即可
        GetData getData = new GetData("select * from dongtai");
        getData.start();
        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        RecyclerView recyclerView = findViewById(R.id.dynamic_re);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        DtAdapter dynamic = new DtAdapter();
        recyclerView.setAdapter(dynamic);
    }


}
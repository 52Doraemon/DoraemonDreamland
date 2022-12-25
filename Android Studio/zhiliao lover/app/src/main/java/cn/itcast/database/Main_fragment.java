package cn.itcast.database;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link Main_fragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class Main_fragment extends Fragment implements View.OnClickListener {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private View root;
    private Context context;
    private static int[] gl_ID = {0, 1, 2};//攻略ID
    private static String[] name = new String[0];//用户名
    private static String[] title = new String[0];//标题
    private static String[] values = new String[0];//内容
    private static int[] img = new int[0];//头像
    private static String[] date = new String[0];//时间
    private static int[] pl = new int[0];//评论量
    private static boolean[] zang = new boolean[0];//是否已被点赞
    private static Connection conn;

    public Main_fragment() {
        // Required empty public constructor
    }

    private static class GetData extends Thread {

        private String sql;

        public GetData(String sql) {
            this.sql = sql;
        }

        @Override
        public void run() {
            try {
                update_main_data();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        private void update_main_data() throws Exception {
            Statement statement = null;
            ResultSet resultSet = null;
            if (conn == null) conn = Mysql_JDBC.myConnection();
            statement = conn.createStatement();
            resultSet = statement.executeQuery(sql);
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
        }
    }

    @Override
    public void onClick(View view) {
        Intent intent;
        switch (view.getId()) {
            //跳转到约会攻略界面
            case R.id.gong_lue:
                intent = new Intent(context, Gong_lue.class);
                startActivity(intent);
                break;
            //跳转到动态广场界面
            case R.id.dong_tai:
                intent = new Intent(context, Dong_tai.class);
                startActivity(intent);
                break;
//            //跳转到情感树洞界面
//            case R.id.shu_dong:
//                break;
//            //跳转到心动商城界面
//            case R.id.shang_cheng:
//                break;
        }
    }

    private class GlAdapter extends RecyclerView.Adapter<GlAdapter.ViewHolder> {

        @NonNull
        @Override
        public GlAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.main_fragment_dynamic, parent, false);
            return new ViewHolder(view);
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
                        Intent intent = new Intent(context, Gong_lue_detail.class);
                        int id = getAdapterPosition();
                        new Thread() {
                            @Override
                            public void run() {
                                try {
                                    if (conn == null) conn = Mysql_JDBC.myConnection();
                                    Statement statement1 = conn.createStatement();
                                    Statement statement2 = conn.createStatement();
                                    statement1.execute("update strategy set strategy_views = strategy_views+1 where user_name ='" + name[id] + "'");
                                    statement2.execute("update user set user_views = user_views + 1 where user_name='" + name[id] + "'");
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                        }.start();
                        intent.putExtra("glID", gl_ID[id]);
                        intent.putExtra("name", name[id]);
                        intent.putExtra("title", title[id]);
                        intent.putExtra("values", values[id]);
                        intent.putExtra("img", img[id]);
                        intent.putExtra("date", date[id]);
                        intent.putExtra("zang", zang[id]);
                        startActivity(intent);
                    }
                });
            }
        }
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment Main_fragment.
     */
    // TODO: Rename and change types and number of parameters
    public static Main_fragment newInstance(String param1, String param2) {
        Main_fragment fragment = new Main_fragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        if (root == null) {
            root = inflater.inflate(R.layout.main, container, false);
            context = container.getContext();
            //在更新main界面数据
            GetData getData = new GetData("select * from strategy");
            getData.start();
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            TextView gong_lue, dong_tai, shu_dong;
            gong_lue = root.findViewById(R.id.gong_lue);
            dong_tai = root.findViewById(R.id.dong_tai);
//            shu_dong = root.findViewById(R.id.shu_dong);
            gong_lue.setOnClickListener(this);
            dong_tai.setOnClickListener(this);
//            shu_dong.setOnClickListener(this);

            //下方更新主界面的攻略数据
            RecyclerView recyclerView = root.findViewById(R.id.main_dynamic_recycler);
            LinearLayoutManager layoutManager = new LinearLayoutManager(context);
            recyclerView.setLayoutManager(layoutManager);
            GlAdapter adapter = new GlAdapter();
            recyclerView.setAdapter(adapter);
        }
        return root;
    }
}
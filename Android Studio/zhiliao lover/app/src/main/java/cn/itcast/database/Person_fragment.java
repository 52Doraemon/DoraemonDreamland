package cn.itcast.database;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link Person_fragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class Person_fragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private View root;

    private static int person_img;
    private static String person_name;
    private static String person_style_name;
    private static String get_z;
    private static String get_r;
    private static String my_pl;
    private static String my_zp;


    public Person_fragment() {
        // Required empty public constructor
    }

    //创建连接数据库的线程类
    private static class SQLThread extends Thread {

        private final String sql;

        public SQLThread(String sql) {
            this.sql = sql;
        }

        @Override
        public void run() {
            try {
                update_person_date();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        //更新用户数据方法
        private void update_person_date() throws Exception {
            Connection conn = null;
            Statement statement = null;
            ResultSet resultSet = null;

            conn = Mysql_JDBC.myConnection();
            statement = conn.createStatement();
            resultSet = statement.executeQuery(sql);
            if (!resultSet.wasNull()) {
                //游标不空则进行更新数据操作
                resultSet.first();
                while (!resultSet.isAfterLast()) {
                    //用户名
                    person_name = resultSet.getString(1);
                    //个性签名
                    person_style_name = resultSet.getString(5);
                    //用户头像
                    person_img = resultSet.getInt(4);
                    //总赞数
                    get_z = resultSet.getString(6);
                    //总浏览量
                    get_r = resultSet.getString(7);
                    //评论数
                    my_pl = resultSet.getString(8);
                    //作品数
                    my_zp = resultSet.getString(9);
                    resultSet.next();
                }
            }
            resultSet.close();
        }
    }


    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment Person_fragment.
     */
    // TODO: Rename and change types and number of parameters
    public static Person_fragment newInstance(String param1, String param2) {
        Person_fragment fragment = new Person_fragment();
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

    @SuppressLint("SetTextI18n")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        if (root == null) {
            root = inflater.inflate(R.layout.person, container, false);
            TextView edit = root.findViewById(R.id.user_edit);


            edit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    //跳转到编辑个人信息界面
                    Intent intent = new Intent(container.getContext(), Person_edit.class);
                    startActivity(intent);
                }
            });
            //控件初始化
            ImageView person_img = root.findViewById(R.id.person_img);//用户头像
            TextView person_name = root.findViewById(R.id.person_name);//用户名
            TextView person_style_name = root.findViewById(R.id.person_style_name);//个性签名
            TextView get_z = root.findViewById(R.id.person_zang);//总获赞数
            TextView get_r = root.findViewById(R.id.person_read);//总浏览量
            TextView my_pl = root.findViewById(R.id.my_pl);//评论数
            TextView my_zp = root.findViewById(R.id.my_zp);//作品数

            // 连接数据库,根据上方的用户名查询并获取数据
            //在此更新数据，连接数据库，获取到数据后将数据放到上面的控件中
            //获取个人数据：用户名、个性签名、总点赞数、总预览数、评论数、发布作品数
            SQLThread thread = new SQLThread("select * from user where user_name='" + MainActivity.user_name + "'");
            thread.start();
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            person_img.setImageResource(MainActivity.img[Person_fragment.person_img]);
            person_name.setText(Person_fragment.person_name);
            person_style_name.setText(Person_fragment.person_style_name);
            get_z.setText("获赞 " + Person_fragment.get_z);
            get_r.setText("浏览量 " + Person_fragment.get_r);
            my_pl.setText(Person_fragment.my_pl);
            my_zp.setText(Person_fragment.my_zp);
        }
        return root;
    }

}
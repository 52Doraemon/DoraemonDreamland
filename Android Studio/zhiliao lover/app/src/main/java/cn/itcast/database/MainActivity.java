package cn.itcast.database;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.annotation.SuppressLint;
import android.app.Person;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private Main_fragment main;
    private Person_fragment person;
    public static String user_name;
    public static int[] img = {R.drawable.img1, R.drawable.img2, R.drawable.img3, R.drawable.img4, R.drawable.img5, R.drawable.img6,};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Intent intent = getIntent();
        user_name = intent.getStringExtra("user_name");

        //初始化桌面控件
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        main = new Main_fragment();
        fragmentTransaction.replace(R.id.main_value, main);
        fragmentTransaction.commit();
        ImageView main_view = findViewById(R.id.main_view);
        main_view.setOnClickListener(this);
        ImageView person_view = findViewById(R.id.person_view);
        person_view.setOnClickListener(this);
        ImageView add = findViewById(R.id.add_view);
        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, Fabu_Edit.class);
                startActivity(intent);
                Intent intent1;
            }
        });

//修改个人资料传递旧用户名
//        person_view.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Intent intent = new Intent(MainActivity.this, Person_edit.class);
//                intent.putExtra("OLD_user_name", user_name);
//                startActivity(intent);
//            }
//    });

    }


    @Override
    protected void onRestart() {
        super.onRestart();
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        main = new Main_fragment();
        person = null;
        fragmentTransaction.replace(R.id.main_value, main);
        fragmentTransaction.commitAllowingStateLoss();
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public void onClick(View view) {
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        if (main != null) fragmentTransaction.hide(main);
        if (person != null) fragmentTransaction.hide(person);
        switch (view.getId()) {
            case R.id.main_view:
                if (main == null) {
                    main = new Main_fragment();
                    fragmentTransaction.add(R.id.main_value, main);
                } else {
                    fragmentTransaction.show(main);
                }
                break;
            case R.id.person_view:
                if (person == null) {
                    person = new Person_fragment();
                    fragmentTransaction.add(R.id.main_value, person);
                } else {
                    fragmentTransaction.show(person);
                }
                break;
        }
        fragmentTransaction.commit();
    }
}
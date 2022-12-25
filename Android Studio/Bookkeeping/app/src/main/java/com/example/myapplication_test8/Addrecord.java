package com.example.myapplication_test8;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class Addrecord extends AppCompatActivity implements DatePickerDialog.OnDateSetListener {

    private TextView date;//日历控件
    private float money;//消费金额
    private String now;//系统时间
    private int y, m, d;//插入记录的年月日
    private DatePickerDialog dialog;
    private String key;
    private EditText ed;


    //各按钮控件
    private final TextView[] cType_tx = new TextView[3];
    private final TextView[] mType_tx = new TextView[12];
    private final TextView[] aType_tx = new TextView[4];

    //判断按钮是否被选中
    private final boolean[] cType_select = new boolean[3];
    private final boolean[] mType_select = new boolean[12];
    private final boolean[] aType_select = new boolean[4];

    //各按钮ID
    private final int[] cType_id = {R.id.addrecord_expenditure, R.id.addrecord_income, R.id.addrecord_account};
    private final int[] mType_id = {R.id.food, R.id.transit, R.id.clothing, R.id.shoppingbag, R.id.medical_services,
            R.id.school, R.id.play, R.id.sports, R.id.home, R.id.trips, R.id.pets, R.id.service};
    private final int[] aType_id = {R.id.type_cash, R.id.type_wc, R.id.type_pay, R.id.type_more};

    //消费类型按钮选中样式
    private final int[] mType_drawable1 = {R.drawable.ic_food1, R.drawable.ic_transit1,
            R.drawable.ic_clothing1, R.drawable.ic_shoppingbag1,
            R.drawable.ic_medical_services1, R.drawable.ic_school1,
            R.drawable.ic_play1, R.drawable.ic_sports1, R.drawable.ic_home1,
            R.drawable.ic_trips1, R.drawable.ic_pets1, R.drawable.ic_service1};
    private final int[] mType_drawable2 = {R.drawable.ic_food2, R.drawable.ic_transit2,
            R.drawable.ic_clothing2, R.drawable.ic_shoppingbag2,
            R.drawable.ic_medical_services2, R.drawable.ic_school2,
            R.drawable.ic_play2, R.drawable.ic_sports2, R.drawable.ic_home2,
            R.drawable.ic_trips2, R.drawable.ic_pets2, R.drawable.ic_service2};


    //日历监听，获取选中的日期
    @SuppressLint("SetTextI18n")
    @Override
    public void onDateSet(DatePicker datePicker, int i, int i1, int i2) {
        y = i;
        m = i1 + 1;
        d = i2;
        String dm, dd;
        if (m < 10) dm = "0" + m;
        else dm = m + "";
        if (d < 10) dd = "0" + d;
        else dd = d + "";
        date.setText(y + "-" + dm + "-" + dd);
    }

    //监听类，各控件绑定监听事件，选中的按钮将会变色
    private class Listener implements View.OnClickListener {

        private int id;

        public Listener(int id) {
            this.id = id;
        }

        @SuppressLint("UseCompatLoadingForDrawables")
        @Override
        public void onClick(View view) {
            if (id == 1) {
                for (int i = 0; i < cType_id.length; i++) {
                    cType_select[i] = false;
                    cType_tx[i].setTextColor(getColor(R.color.addrecord_select_text1));
                    cType_tx[i].setBackground(getDrawable(R.color.addrecord_select_background1));
                    if (view.getId() == cType_id[i]) {
                        cType_select[i] = true;
                        cType_tx[i].setTextColor(getColor(R.color.addrecord_select_text2));
                        cType_tx[i].setBackground(getDrawable(R.color.addrecord_select_background2));
                    }
                }
            } else if (id == 2) {
                for (int i = 0; i < aType_id.length; i++) {
                    aType_select[i] = false;
                    aType_tx[i].setTextColor(getColor(R.color.addrecord_select_text1));
                    aType_tx[i].setBackground(getDrawable(R.color.addrecord_select_background1));
                    if (view.getId() == aType_id[i]) {
                        aType_select[i] = true;
                        aType_tx[i].setTextColor(getColor(R.color.addrecord_select_text2));
                        aType_tx[i].setBackground(getDrawable(R.color.addrecord_select_background2));
                    }
                }
            } else {
                for (int i = 0; i < mType_id.length; i++) {
                    mType_select[i] = false;
                    mType_tx[i].setTextColor(getColor(R.color.addrecord_select_text1));
                    mType_tx[i].setCompoundDrawablesRelativeWithIntrinsicBounds(null, getDrawable(mType_drawable1[i]), null, null);
                    if (view.getId() == mType_id[i]) {
                        mType_select[i] = true;
                        mType_tx[i].setTextColor(getColor(R.color.addrecord_select_text2));
                        mType_tx[i].setCompoundDrawablesRelativeWithIntrinsicBounds(null, getDrawable(mType_drawable2[i]), null, null);
                    }
                }
            }
        }
    }

    @SuppressLint({"UseCompatLoadingForDrawables", "SetTextI18n"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_addrecord);

        //初始化控件并绑定监听
        AddTextView(cType_tx, cType_id, 1);
        AddTextView(aType_tx, aType_id, 2);
        AddTextView(mType_tx, mType_id, 3);


        //添加金额输入文本框限制监听器
        //输入总长度8位，小数2位
        ed = findViewById(R.id.add_money);
        ed.addTextChangedListener(new DecimalInputTextWatcher(ed, 8, 2));

        //获取日期
        getDate();

        //初始化页面传递过来的数据
        getIntentData();

        //左上角退出按钮
        ImageView back = findViewById(R.id.addrecord_back);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        //右上角确定按钮
        ImageView check = findViewById(R.id.navi_check);
        check.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("DefaultLocale")
            @Override
            public void onClick(View view) {
                //输入准确性判断
                int mType = log_index(mType_select), aType = log_index(aType_select), cType = log_index(cType_select);
                String s = ed.getText().toString();
                if (mType == -1 || aType == -1 || cType == -1 || s.isEmpty()) {
                    Toast.makeText(Addrecord.this, "输入金额或选择类型不能为空！", Toast.LENGTH_SHORT).show();
                    return;
                }
                //输入金额，保留两位小数
                money = Float.parseFloat(s);
                s = String.format("%.2f", money);
                money = Float.parseFloat(s);//消费金额
                //消费记录的时间，格式为"YYYY-MM-dd"
                String dm, dd;
                if (m < 10) dm = "0" + m;
                else dm = m + "";
                if (d < 10) dd = "0" + d;
                else dd = d + "";
                String dt = y + "-" + dm + "-" + dd;
                //插入或修改数据
                SQLiteDatabase db = MainActivity.cb_db.getWritableDatabase();
                if (key.isEmpty()) {
                    String sql = "insert into Money (TTime ,Time , Money ,M_Type ,A_Type ,C_Type) values" +
                            "('" + now + "','" + dt + "'," + money + "," + mType + " ," + aType + " ," + cType + ")";
                    db.execSQL(sql);
                    Toast.makeText(Addrecord.this, "记录添加成功！", Toast.LENGTH_SHORT).show();
                } else {
                    String sql = "update Money set Time='" + dt + "',Money=" + money + ",M_Type=" + mType + ",A_Type=" + aType + ",C_Type=" + cType +
                            " where TTime='" + key + "'";
                    db.execSQL(sql);
                    Toast.makeText(Addrecord.this, "记录修改成功！", Toast.LENGTH_SHORT).show();
                }
                db.close();
                finish();
            }
        });
    }

    //初始化控件并绑定监听事件
    private void AddTextView(TextView[] tx, int[] tx_id, int type) {
        Listener listener = new Listener(type);
        for (int i = 0; i < tx_id.length; i++) {
            tx[i] = findViewById(tx_id[i]);
            tx[i].setOnClickListener(listener);
        }
    }

    //获取选择控件的下标
    private int log_index(boolean[] type) {
        for (int i = 0; i < type.length; i++)
            if (type[i]) return i;
        return -1;
    }

    //获取日历日期
    private void getDate() {
        date = findViewById(R.id.addrecord_date);
        @SuppressLint("SimpleDateFormat") SimpleDateFormat sdate = new SimpleDateFormat("yyyy-MM-dd");
        date.setText(sdate.format(new Date()));
        sdate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        now = sdate.format(new Date());
        Calendar calendar = Calendar.getInstance();
        y = calendar.get(Calendar.YEAR);
        m = calendar.get(Calendar.MONTH) + 1;
        d = calendar.get(Calendar.DAY_OF_MONTH);
        dialog = new DatePickerDialog(Addrecord.this, Addrecord.this, y, m - 1, d);
        date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.show();
            }
        });
    }

    //获取页面传递的数据
    @SuppressLint({"UseCompatLoadingForDrawables", "SetTextI18n"})
    private void getIntentData() {
        Intent intent = getIntent();
        key = intent.getStringExtra("primary");
        if (!key.isEmpty()) {
            TextView recordID = findViewById(R.id.record_name);
            recordID.setText("修改记录");
            int A_type_id = intent.getIntExtra("A_type", -1);
            int C_type_id = intent.getIntExtra("C_type", -1);
            int M_type_id = intent.getIntExtra("M_type", -1);
            float money = intent.getFloatExtra("cost", 0);
            String time = intent.getStringExtra("date");
            aType_select[A_type_id] = true;
            aType_tx[A_type_id].setTextColor(getColor(R.color.addrecord_select_text2));
            aType_tx[A_type_id].setBackground(getDrawable(R.color.addrecord_select_background2));
            cType_select[C_type_id] = true;
            cType_tx[C_type_id].setTextColor(getColor(R.color.addrecord_select_text2));
            cType_tx[C_type_id].setBackground(getDrawable(R.color.addrecord_select_background2));
            mType_select[M_type_id] = true;
            mType_tx[M_type_id].setTextColor(getColor(R.color.addrecord_select_text2));
            mType_tx[M_type_id].setCompoundDrawablesRelativeWithIntrinsicBounds(null, getDrawable(mType_drawable2[M_type_id]), null, null);
            ed.setText(money + "");
            @SuppressLint("SimpleDateFormat") SimpleDateFormat s = new SimpleDateFormat("yyyy-MM-dd");
            try {
                Date date_time = s.parse(time);
                Calendar calendar = Calendar.getInstance();
                calendar.setTime(date_time);
                y = calendar.get(Calendar.YEAR);
                m = calendar.get(Calendar.MONTH) + 1;
                d = calendar.get(Calendar.DAY_OF_MONTH);
                dialog = new DatePickerDialog(Addrecord.this, Addrecord.this, y, m - 1, d);
                date.setText(time);
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
    }

}
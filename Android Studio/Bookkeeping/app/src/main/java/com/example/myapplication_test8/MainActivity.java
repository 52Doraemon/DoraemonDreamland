package com.example.myapplication_test8;

import static com.example.myapplication_test8.R.drawable;
import static com.example.myapplication_test8.R.id;
import static com.example.myapplication_test8.R.layout;
import android.annotation.SuppressLint;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.icu.text.DecimalFormat;
import android.os.Bundle;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class MainActivity extends AppCompatActivity implements OnClickListener {

    //记账数据库
    public static SQLiteOpenHelper cb_db;

    //记账记录数组
    private Record[] records;

    //记账类型图标数组
    private final int[] img_icon = {drawable.ic_food2, drawable.ic_transit2,
                                    drawable.ic_clothing2, drawable.ic_shoppingbag2,
                                    drawable.ic_medical_services2, drawable.ic_school2,
                                    drawable.ic_play2, drawable.ic_sports2, drawable.ic_home2,
                                    drawable.ic_trips2, drawable.ic_pets2, drawable.ic_service2};

    //记账类型数组
    private final String[] text_type = {"餐饮", "交通", "服饰", "购物", "医疗", "教育", "娱乐", "运动", "生活", "旅行", "宠物", "其他"};

    //记账支付类型数组
    private final String[] a_type = {"现金", "微信支付", "支付宝", "其他"};

    private RecyclerView recyclerView;
    private TextView select_all, select_all_not, delete, record_0;
    private Vibrator vibrator;
    private boolean via = false;
    private double Zic_Net_Asset_Sum = 0.00;//Zic_Net_Asset_Sum为余额：所有的Money代数和
    private double Zic_Xianjin = 0.00;//Zic_Xianjin：A_Type为0的Money总额（资产页面我的现金余额）
    private double Zic_Weixin = 0.00;//Zic_Weixin：A_Type为1的Money总额（资产页面我的微信余额）
    private double Zic_Zhifubao = 0.00;//Zic_Zhifubao：A_Type为2的Money总额（资产页面我的支付宝余额）
    private double Zic_Qita = 0.00;//Zic_Qita：A_Type为3的Money总额（资产页面其他支付方式余额）
    private double Zic_Total_Expenditure = 0.00;//Zic_Total_Expenditure：C_Type为0的Money总额(资产页面总支出)
    private double Zic_Total_Income = 0.00;//Zic_Total_Income：C_Type为1的Money总额（资产页面总收入）
    private TextView zhuye_net_asset_sum, zhuye_zongzhichu_shuju, zhuye_zongshouru_shuju;

    //记账记录数组类定义
    public static class Record {
        final int length;
        String date;
        float in, out;
        final String[] primary_key;
        final int[] M_type;
        final int[] A_type;
        final int[] C_type;
        final float[] cost;
        final boolean[] select;

        //记账记录方式定义
        public Record(int length) {
            this.length = length;
            primary_key = new String[length];
            M_type = new int[length];//消费类型，0到11 分别对应餐饮、交通、服饰、购物、医疗、教育、娱乐、运动、生活、旅行、宠物、其他
            A_type = new int[length];//消费资产类型，0到3  分别对应:现金、微信、支付宝、其他
            C_type = new int[length];//收支类型，0到2  分别对应:支出、收入、不计入收支
            cost = new float[length];
            select = new boolean[length];
            in = 0.00F;
            out = 0.00F;
        }

    }

    //RecyclerView类，记录分组，按日期分组
    class ListAdapter extends RecyclerView.Adapter<ListAdapter.ViewHolder> {

        private final boolean vis;//是否显示勾选框
        private final boolean sel;//是否全选

        public ListAdapter(boolean vis, boolean sel) {
            this.vis = vis;
            this.sel = sel;
        }


        //ViewHolder优化类
        class ViewHolder extends RecyclerView.ViewHolder {

            final TextView re_date;
            final TextView re_in;
            final TextView re_out;
            final RecyclerView re_re;

            public ViewHolder(@NonNull View itemView) {
                super(itemView);
                re_date = itemView.findViewById(id.re_date);
                re_re = itemView.findViewById(id.re_re);
                re_in = itemView.findViewById(id.re_in);
                re_out = itemView.findViewById(id.re_out);
            }
        }

        @NonNull
        @Override
        //绑定记录样式
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(layout.main_rw, parent, false);
            return new ViewHolder(view);
        }

        @SuppressLint("SetTextI18n")
        @Override
        //绑定界面数据
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            holder.re_date.setText(records[position].date);
            DecimalFormat decimalFormat = new DecimalFormat("0.00");
            String in = decimalFormat.format(records[position].in);
            String out = decimalFormat.format(records[position].out);
            holder.re_in.setText("收 " + in);
            holder.re_out.setText("支 " + out);
            LinearLayoutManager linearLayoutManager = new LinearLayoutManager(MainActivity.this);
            ReListAdapter adapter = new ReListAdapter(position);
            holder.re_re.setLayoutManager(linearLayoutManager);
            holder.re_re.setAdapter(adapter);
        }


        @Override
        //获得记录长度
        public int getItemCount() {
            return records.length;
        }

        //listview，记录分组，按当天记录分组
        class ReListAdapter extends RecyclerView.Adapter<ReListAdapter.ViewHolder> {

            final int index;

            public ReListAdapter(int index) {
                this.index = index;
            }

            @NonNull
            @Override
            public ReListAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(layout.main_rw_rw, parent, false);
                return new ViewHolder(view);
            }

            @SuppressLint("SetTextI18n")
            @Override
            public void onBindViewHolder(@NonNull ReListAdapter.ViewHolder holder, @SuppressLint("RecyclerView") int position) {
                //更新消费类型
                holder.re_re_type.setText(text_type[records[index].M_type[position]]);
                holder.re_re_img.setImageResource(img_icon[records[index].M_type[position]]);

                //更新勾选框是否显示,如果显示，则绑定勾选监听
                if (vis) {
                    records[index].select[position] = false;
                    holder.re_re_check.setVisibility(View.VISIBLE);
                    holder.re_re_check.setOnClickListener(view -> {
                        records[index].select[position] = holder.re_re_check.isChecked();
                        System.out.println(holder.re_re_check.isChecked());
                    });
                    //如果全选，则显示选中的勾选框
                    if (sel) {
                        holder.re_re_check.setChecked(true);
                        records[index].select[position] = true;
                    }
                }

                //更新消费金额
                DecimalFormat decimalFormat = new DecimalFormat(".00");
                String money = decimalFormat.format(records[index].cost[position]);
                int c_type = records[index].C_type[position];
                if (c_type == 0)
                    holder.re_re_pay1.setText("-" + money);
                else if (c_type == 1)
                    holder.re_re_pay1.setText("+" + money);
                else holder.re_re_pay1.setText(money);

                //更新消费支付类型
                holder.re_re_pay2.setText(a_type[records[index].A_type[position]]);

            }

            @Override
            public int getItemCount() {
                return records[index].length;
            }

            @SuppressLint("SetTextI18n")
            //控件绑定优化并添加监听。设置监听，点击跳转到修改记录界面，并传递记录的数据,设置监听，长按进入记录删除功能
            public class ViewHolder extends RecyclerView.ViewHolder {

                final CheckBox re_re_check;
                final ImageView re_re_img;
                final TextView re_re_type;
                final TextView re_re_pay1;
                final TextView re_re_pay2;

                //控件绑定优化并添加监听。设置监听，点击跳转到修改记录界面，并传递记录的数据,设置监听，长按进入记录删除功能
                public ViewHolder(@NonNull View itemView) {
                    super(itemView);
                    re_re_type = itemView.findViewById(id.re_re_type);
                    re_re_check = itemView.findViewById(id.re_re_check);
                    re_re_img = itemView.findViewById(id.re_re_img);
                    re_re_pay1 = itemView.findViewById(id.re_re_pay_type_1);
                    re_re_pay2 = itemView.findViewById(id.re_re_pay_type_2);

                    //设置监听，点击跳转到修改记录界面，并传递记录的数据
                    itemView.setOnClickListener(view -> {
                        //跳转到修改记录界面，并把该记录的数据传到修改界面上
                        int id = getAdapterPosition();
                        Intent intent = new Intent(MainActivity.this, Addrecord.class);
                        intent.putExtra("primary", records[index].primary_key[id]);
                        intent.putExtra("A_type", records[index].A_type[id]);
                        intent.putExtra("C_type", records[index].C_type[id]);
                        intent.putExtra("M_type", records[index].M_type[id]);
                        intent.putExtra("cost", records[index].cost[id]);
                        intent.putExtra("date", records[index].date);
                        startActivity(intent);
                    });
                    //设置监听，长按进入记录删除功能
                    itemView.setOnLongClickListener(view -> {
                        //长按更新按钮的状态，让每个记录的按钮全部显示
                        if (!via) {
                            ListAdapter newAdapter = new ListAdapter(true, false);
                            recyclerView.setAdapter(newAdapter);
                            select_all.setVisibility(View.VISIBLE);
                            delete.setVisibility(View.VISIBLE);
                            vibrator.vibrate(VibrationEffect.createOneShot(50, VibrationEffect.DEFAULT_AMPLITUDE));
                            via = true;
                        }
                        return true;
                    });
                }
            }
        }
    }

    //记账数据库
    static class CashBookSQLite extends SQLiteOpenHelper {

        public CashBookSQLite(@Nullable Context context, @Nullable String name, @Nullable SQLiteDatabase.CursorFactory factory, int version) {
            super(context, name, factory, version);
        }

        @Override
        public void onCreate(SQLiteDatabase sqLiteDatabase) {
            //创建记账表
            String sql = "create table Money (" +
                    "TTime Datetime not null," +
                    "Time Date not null," +
                    "Money float not null," +
                    "M_Type int not null," +
                    "A_Type int not null," +
                    "C_Type int not null," +
                    "primary key (TTime));";
            sqLiteDatabase.execSQL(sql);
        }

        @Override
        public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
            //更新数据库版本时才写，不更新不用写
        }

    }

    protected void onCreate(Bundle savedInstanceState) {
        //参数暂存数据。之后再onCreate中取出，保证Activity销毁但是数据不销毁。
        super.onCreate(savedInstanceState);

        setContentView(layout.activity_main);

        //初始化页面控件
        create();

        //添加列表记录
        update_record();

        //全选按钮
        select_all.setOnClickListener(view -> {
            ListAdapter newListAdapter = new ListAdapter(true, true);
            recyclerView.setAdapter(newListAdapter);
            select_all.setVisibility(View.GONE);
            select_all_not.setVisibility(View.VISIBLE);
        });

        //取消全选按钮
        select_all_not.setOnClickListener(view -> {
            ListAdapter newListAdapter = new ListAdapter(true, false);
            recyclerView.setAdapter(newListAdapter);
            select_all.setVisibility(View.VISIBLE);
            select_all_not.setVisibility(View.GONE);
        });

        //删除选中的数据按钮
        delete.setOnClickListener(view -> {
            //将被选中的数据删除
            boolean flag = false;
            SQLiteDatabase db = cb_db.getWritableDatabase();
            for (Record record : records) {
                for (int j = 0; j < record.select.length; j++) {
                    if (record.select[j]) {
                        flag = true;
                        String sql = "delete from Money where TTime='" + record.primary_key[j] + "'";
                        db.execSQL(sql);
                    }
                }
            }
            db.close();
            select_all.setVisibility(View.GONE);
            select_all_not.setVisibility(View.GONE);
            delete.setVisibility(View.GONE);
            update_record();
            via = false;
            if (flag) Toast.makeText(MainActivity.this, "删除记录成功！", Toast.LENGTH_SHORT).show();
        });
    }

    //页面监听器，隐藏/显示余额等数据，主屏幕右下角按钮，点击将跳转到添加记录界面，主页跳转资产详细记录界面按钮
    public void onClick(View view) {
        switch (view.getId()) {
            case id.zhuye_zic_hide://隐藏/显示余额等数据
                TextView index = findViewById(R.id.zhuye_net_asset_sum);
                if(index.getText() == "*****"){
                    DecimalFormat decimalFormat = new DecimalFormat("0.00");
                    zhuye_net_asset_sum.setText(decimalFormat.format(Zic_Net_Asset_Sum));
                    zhuye_zongzhichu_shuju.setText("总支出  " + decimalFormat.format(Zic_Total_Expenditure));
                    zhuye_zongshouru_shuju.setText("总收入  " + decimalFormat.format(Zic_Total_Income));
                }
                else {
                    zhuye_net_asset_sum.setText("*****");
                    zhuye_zongzhichu_shuju.setText("总支出  " + "****");
                    zhuye_zongshouru_shuju.setText("总收入  " + "****");
                }
                break;
            case id.add_icon://主屏幕右下角按钮，点击将跳转到添加记录界面
                Intent intent1 = new Intent(MainActivity.this, Addrecord.class);
                intent1.putExtra("primary", "");
                startActivity(intent1);
                break;
            case id.zic://主页跳转资产详细记录界面按钮
                Intent intent2 = new Intent(MainActivity.this, Property_Activity.class);
                intent2.putExtra("Zic_Net_Asset_Sum", Zic_Net_Asset_Sum);//Zic_Net_Asset_Sum为余额：所有的Money代数和
                intent2.putExtra("Zic_Xianjin", Zic_Xianjin);//Zic_Xianjin：A_Type为1的Money总额（资产页面我的现金余额）
                intent2.putExtra("Zic_Weixin", Zic_Weixin);//Zic_Weixin：A_Type为0的Money总额（资产页面我的微信余额）
                intent2.putExtra("Zic_Zhifubao", Zic_Zhifubao);//Zic_Zhifubao：A_Type为2的Money总额（资产页面我的支付宝余额）
                intent2.putExtra("Zic_Qita", Zic_Qita);//Zic_Qita：A_Type为3的Money总额（资产页面其他支付方式余额)
                intent2.putExtra("Zic_Total_Expenditure", Zic_Total_Expenditure);//Zic_Total_Expenditure：C_Type为0的Money总额(资产页面总支出)
                intent2.putExtra("Zic_Total_Income", Zic_Total_Income);//Zic_Total_Income：C_Type为1的Money总额（资产页面总收入）
                startActivity(intent2);
                break;
        }
    }

    //页面重新出现时，更新数据
    @Override
    protected void onRestart() {
        super.onRestart();
        update_record();
    }

    //初始化控件
    private void create() {
        select_all = findViewById(id.select_all);
        select_all_not = findViewById(id.select_all_not);
        delete = findViewById(id.delete);
        record_0 = findViewById(id.record_0);
        recyclerView = findViewById(id.main_rw);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        vibrator = (Vibrator) getSystemService(Service.VIBRATOR_SERVICE);
        zhuye_net_asset_sum = findViewById(id.zhuye_net_asset_sum);
        zhuye_zongzhichu_shuju = findViewById(id.zhuye_zongzhichu_shuju);
        zhuye_zongshouru_shuju = findViewById(id.zhuye_zongshouru_shuju);
        findViewById(id.add_icon).setOnClickListener(this);
        findViewById(id.zic).setOnClickListener(this);
        findViewById(id.zhuye_zic_hide).setOnClickListener(this);
    }

    //更新主界面上的记录数据
    @SuppressLint("SetTextI18n")
    private void update_record() {
        cb_db = new CashBookSQLite(this, "CashBook", null, 1);
        SQLiteDatabase db = cb_db.getReadableDatabase();
        Zic_Net_Asset_Sum = 0.00;
        Zic_Total_Expenditure = 0.00;
        Zic_Total_Income = 0.00;
        Zic_Weixin = 0.00;
        Zic_Xianjin = 0.00;
        Zic_Zhifubao = 0.00;
        Zic_Qita = 0.00;
        //获取按日期排序后的数据
        Cursor result1 = db.query(true, "Money", new String[]{"Time"}, null, null, "Time", null, "Time desc", null);
        if (result1.getCount() != 0) {
            record_0.setVisibility(View.INVISIBLE);
            records = new Record[result1.getCount()];
            result1.moveToFirst();
            while (!result1.isAfterLast()) {
                //数据处理
                int index1 = result1.getPosition();
                String date = result1.getString(0);
                //获取每日的消费记录
                Cursor result2 = db.query("Money", new String[]{"TTime", "Money", "M_Type", "A_Type", "C_Type"}, "Time = '" + date + "'", null, null, null, null, null);
                records[index1] = new Record(result2.getCount());
                records[index1].date = date;
                result2.moveToFirst();
                while (!result2.isAfterLast()) {
                    //数据处理
                    int index2 = result2.getPosition();
                    float money = result2.getFloat(1);//钱
                    int M = result2.getInt(2);//消费类型
                    int A = result2.getInt(3);//消费资产
                    int C = result2.getInt(4);//收支类型
                    records[index1].primary_key[index2] = result2.getString(0);
                    records[index1].cost[index2] = money;
                    records[index1].M_type[index2] = M;
                    records[index1].A_type[index2] = A;
                    records[index1].C_type[index2] = C;
                    if (C == 0) {
                        records[index1].out += money;
                        Zic_Net_Asset_Sum -= money;
                        Zic_Total_Expenditure += money;
                    } else if (C == 1 || C == 2) {
                        records[index1].in += money;
                        Zic_Net_Asset_Sum += money;
                        Zic_Total_Income += money;
                    }
                    switch (A) {
                        case 0:
                            if (C == 0) Zic_Xianjin -= money;
                            else Zic_Xianjin += money;
                            break;
                        case 1:
                            if (C == 0) Zic_Weixin -= money;
                            else Zic_Weixin += money;
                            break;
                        case 2:
                            if (C == 0) Zic_Zhifubao -= money;
                            else Zic_Zhifubao += money;
                            break;
                        case 3:
                            if (C == 0) Zic_Qita -= money;
                            else Zic_Qita += money;
                            break;
                    }
                    result2.moveToNext();
                }
                result1.moveToNext();
                result2.close();
            }
            ListAdapter myAdapter = new ListAdapter(false, false);
            recyclerView.setAdapter(myAdapter);
        } else {
            recyclerView.setAdapter(null);
            record_0.setVisibility(View.VISIBLE);
        }
        result1.close();
        db.close();
        select_all.setVisibility(View.GONE);
        select_all_not.setVisibility(View.GONE);
        delete.setVisibility(View.GONE);
        via = false;
        //刷新主页资产数据
        DecimalFormat decimalFormat = new DecimalFormat("0.00");
        zhuye_net_asset_sum.setText(decimalFormat.format(Zic_Net_Asset_Sum));
        zhuye_zongzhichu_shuju.setText("总支出  " + decimalFormat.format(Zic_Total_Expenditure));
        zhuye_zongshouru_shuju.setText("总收入  " + decimalFormat.format(Zic_Total_Income));
    }
}
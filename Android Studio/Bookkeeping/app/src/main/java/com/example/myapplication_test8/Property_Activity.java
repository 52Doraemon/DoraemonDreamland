package com.example.myapplication_test8;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.icu.text.DecimalFormat;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class Property_Activity extends AppCompatActivity {

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //获取Intent中暂存的数据
        Intent intent = getIntent();
        double Zic_Net_Asset_Sum = intent.getDoubleExtra("Zic_Net_Asset_Sum", 0);
        double Zic_Xianjin = intent.getDoubleExtra("Zic_Xianjin", 0);
        double Zic_Weixin = intent.getDoubleExtra("Zic_Weixin", 0);
        double Zic_Zhifubao = intent.getDoubleExtra("Zic_Zhifubao", 0);
        double Zic_Qita = intent.getDoubleExtra("Zic_Qita", 0);
        double Zic_Total_Expenditure = intent.getDoubleExtra("Zic_Total_Expenditure", 0);
        double Zic_Total_Income = intent.getDoubleExtra("Zic_Total_Income", 0);
        System.out.println(Zic_Total_Income);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_property);

        //刷新资产页数据
        TextView zic_net_asset_sum = findViewById(R.id.zic_net_asset_sum);
        TextView zic_zongzhichu_shuju = findViewById(R.id.zic_zongzhichu_shuju);
        TextView zic_zongshouru_shuju = findViewById(R.id.zic_zongshouru_shuju);

        TextView zic_net_asset_sum_shuju = findViewById(R.id.zic_net_asset_sum_shuju);//资产账户余额

        TextView zic_xianjin_shuju = findViewById(R.id.zic_xianjin_shuju);
        TextView zic_weixin_shuju = findViewById(R.id.zic_weixin_shuju);
        TextView zic_zhifubao_shuju = findViewById(R.id.zic_zhifubao_shuju);
        TextView zic_qita_shuju = findViewById(R.id.zic_qita_shuju);

        DecimalFormat decimalFormat = new DecimalFormat("0.00");
        zic_net_asset_sum.setText(decimalFormat.format(Zic_Net_Asset_Sum));
        zic_zongzhichu_shuju.setText("总支出  " + decimalFormat.format(Zic_Total_Expenditure));
        zic_zongshouru_shuju.setText("总收入  " + decimalFormat.format(Zic_Total_Income));

        //保留两小数
        double sum = Zic_Xianjin + Zic_Weixin + Zic_Zhifubao + Zic_Qita;
        zic_net_asset_sum_shuju.setText(decimalFormat.format(sum));
        zic_xianjin_shuju.setText(decimalFormat.format(Zic_Xianjin));
        zic_weixin_shuju.setText(decimalFormat.format(Zic_Weixin));
        zic_zhifubao_shuju.setText(decimalFormat.format(Zic_Zhifubao));
        zic_qita_shuju.setText(decimalFormat.format(Zic_Qita));

        //左上角返回箭头
        ImageView arrow = findViewById(R.id.zic_ic_arrowback);
        arrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        findViewById(R.id.zic_hide).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //隐藏/显示余额等数据
                TextView index = findViewById(R.id.zic_net_asset_sum);
                if(index.getText() == "*****"){
                    DecimalFormat decimalFormat = new DecimalFormat("0.00");
                    zic_net_asset_sum.setText(decimalFormat.format(Zic_Net_Asset_Sum));
                    zic_zongzhichu_shuju.setText("总支出  " + decimalFormat.format(Zic_Total_Expenditure));
                    zic_zongshouru_shuju.setText("总收入  " + decimalFormat.format(Zic_Total_Income));
                }
                else {
                    DecimalFormat decimalFormat = new DecimalFormat("0.00");
                    zic_net_asset_sum.setText("*****");
                    zic_zongzhichu_shuju.setText("总支出  " + "****");
                    zic_zongshouru_shuju.setText("总收入  " + "****");
                }
            }


        });
    };
}

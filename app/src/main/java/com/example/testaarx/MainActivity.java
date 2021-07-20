package com.example.testaarx;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.scanzxing.ScanZingMainActivity;
import com.example.testaarx.download.OutLineDataActivity;
import com.facilityone.wireless.demand.DemandActivity;
import com.facilityone.wireless.demand.DemandCreateActivity;
import com.facilityone.wireless.maintenance.MaintenanceActivity;
import com.facilityone.wireless.patrol.PatrolActivity;
import com.facilityone.wireless.workorder.WorkOrderActivity;
import com.facilityone.wireless.workorder.WorkorderCreateActivity;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    private TextView tv1;
    private Button btn2;
    private Button btn3;
    private Button btn4;
    private Button btn6;
    private Button button5;
    private Button mDownBtn;
    private Button mButton2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
        initListeners();
    }

    private void initListeners() {
        mButton2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, WorkorderCreateActivity.class);
                MainActivity.this.startActivity(intent);
            }
        });
        tv1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, PatrolActivity.class);
                MainActivity.this.startActivity(intent);
            }
        });
        btn2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, MaintenanceActivity.class);
                MainActivity.this.startActivity(intent);
            }
        });
        btn3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, WorkOrderActivity.class);
                MainActivity.this.startActivity(intent);
            }
        });
        btn4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, DemandActivity.class);
                MainActivity.this.startActivity(intent);
            }
        });
        button5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, DemandCreateActivity.class);
                MainActivity.this.startActivity(intent);
            }
        });
        btn6.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, ScanZingMainActivity.class);
                MainActivity.this.startActivity(intent);
            }
        });
        mDownBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, OutLineDataActivity.class);
                MainActivity.this.startActivity(intent);
            }
        });
    }

    private void initView() {
        tv1 = (TextView) findViewById(R.id.tv1);
        btn2 = (Button) findViewById(R.id.btn2);
        btn3 = (Button) findViewById(R.id.btn3);
        btn4 = (Button) findViewById(R.id.btn4);
        btn6 = (Button) findViewById(R.id.btn6);
        button5 = (Button) findViewById(R.id.button5);
        mDownBtn = (Button) findViewById(R.id.downBtn);
        mButton2 = (Button) findViewById(R.id.button2);
    }
}

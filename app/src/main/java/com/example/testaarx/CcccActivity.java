package com.example.testaarx;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;

import com.facilityone.wireless.demand.DemandCreateActivity;

public class CcccActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cccc);
        Intent intent = new Intent(CcccActivity.this, DemandCreateActivity.class);
        CcccActivity.this.startActivity(intent);
    }
}

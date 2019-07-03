package com.example.demo;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        TextView text = (TextView) findViewById(R.id.text);
        text.setText("当前包名为:" + getAppInfo());
    }
    /**
     * 获取当前包名
     * @return
     */
    private String getAppInfo() {
        try {
            String pkName = this.getPackageName();
            return pkName;
        } catch (Exception e) {
        }
        return null;
    }
}

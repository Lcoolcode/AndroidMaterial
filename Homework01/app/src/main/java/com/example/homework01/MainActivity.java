package com.example.homework01;


import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "life";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG,"onCreate");


        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
//        // 设置文本内容
//        TextView tv = findViewById(R.id.text);
//        tv.setText("星星点灯~");
        // 跳转事件
        Button ch3ex1 = findViewById(R.id.btn1);
        Button ch3ex2 = findViewById(R.id.btn2);
        Button ch3ex3 = findViewById(R.id.btn3);
        ch3ex1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 意图对象
                Intent intent = new Intent();
                // 意图对象的上下文
                intent.setClass(MainActivity.this, Ch3Ex1Activity.class);
                startActivity(intent);
            }
        });
        ch3ex2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 意图对象
                Intent intent = new Intent();
                // 意图对象的上下文
                intent.setClass(MainActivity.this, Ch3Ex2Activity.class);
                startActivity(intent);
            }
        });
        ch3ex3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 意图对象
                Intent intent = new Intent();
                // 意图对象的上下文
                intent.setClass(MainActivity.this, Ch3Ex3Activity.class);
                startActivity(intent);
            }
        });
//        // 设置视图的宽高，首先获取布局参数(宽度和高度)
//        ViewGroup.LayoutParams params = ch3ex1.getLayoutParams();
//        // 修改布局参数的值，默认px为单位，要把dp转换成px
//        params.width = 0;
//        ch3ex1.setLayoutParams(params);
    }


    // 开始
    @Override
    protected void onStart() {
        super.onStart();
        Log.d(TAG,"onStart");

    }

    // 设置
    @Override
    protected void onResume() {
        super.onResume();
        Log.d(TAG,"onResume");

    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.d(TAG,"onPause");

    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.d(TAG,"onStop");

    }
}
package com.example.homework01;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.airbnb.lottie.LottieAnimationView;

public class Ch3Ex1Activity extends AppCompatActivity {
    private LottieAnimationView animationView;
    private CheckBox loopCheckBox;
    private SeekBar seekBar;
    private TextView textHint;
    private Button appMain;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ch3ex1);

        animationView = findViewById(R.id.animation_view);
        loopCheckBox = findViewById(R.id.loop_checkbox);
        seekBar = findViewById(R.id.seekbar);
        textHint = findViewById(R.id.text_hint);
        appMain = findViewById(R.id.appMain);
        appMain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 意图对象
                Intent intent = new Intent();
                // 意图对象的上下文
                intent.setClass(Ch3Ex1Activity.this, MainActivity.class);
                startActivity(intent);
            }
        });

        loopCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    // 当选中自动播放的时候，开始播放 lottie 动画，同时禁止手动修改进度
                    animationView.playAnimation();
                    seekBar.setEnabled(false);
                } else {
                    // 当去除自动播放时，停止播放 lottie 动画，同时允许手动修改进度
                    animationView.pauseAnimation();
                    seekBar.setEnabled(true);
                }
            }
        });

        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                // TODO ex1-2: 这里应该调用哪个函数呢
                // 当滑块进度改变
                textHint.setText("滑块进度改变");
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                // 当滑块开始滑动
                Toast.makeText(Ch3Ex1Activity.this,"SeekBar开始滑动",Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                // 当结束滑动滑块
                Toast.makeText(Ch3Ex1Activity.this,"SeekBar结束滑动了",Toast.LENGTH_SHORT).show();
            }
        });
    }

}

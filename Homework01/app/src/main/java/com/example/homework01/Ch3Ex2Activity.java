package com.example.homework01;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.afollestad.materialdialogs.MaterialDialog;
import com.jrummyapps.android.colorpicker.ColorPickerDialog;
import com.jrummyapps.android.colorpicker.ColorPickerDialogListener;


public class Ch3Ex2Activity extends AppCompatActivity implements  ColorPickerDialogListener{
    private View target;
    private View startColorPicker;
    private View endColorPicker;
    private Button durationSelector;
    private AnimatorSet animatorSet;
    private Button appMain;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ch3ex2);

        target = findViewById(R.id.target);
        startColorPicker = findViewById(R.id.start_color_picker);
        endColorPicker = findViewById(R.id.end_color_picker);
        durationSelector = findViewById(R.id.duration_selector);
        appMain = findViewById(R.id.appMain);
        appMain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 意图对象
                Intent intent = new Intent();
                // 意图对象的上下文
                intent.setClass(Ch3Ex2Activity.this, MainActivity.class);
                startActivity(intent);
            }
        });

        startColorPicker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ColorPickerDialog.newBuilder()
                        .setColor(getBackgroundColor(startColorPicker))
                        .setColor(getBackgroundColor(startColorPicker))
                        .setDialogType(ColorPickerDialog.TYPE_PRESETS) // 设置对话框类型（预定义颜色或自定义颜色）
                        .setShowAlphaSlider(true) // 是否显示透明度滑块
                        .setDialogId(0) // 对话框 ID
                        .setAllowCustom(true)
                        .show(Ch3Ex2Activity.this);// 显示对话框


            }
        });

        endColorPicker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ColorPickerDialog.newBuilder()
                        .setColor(getBackgroundColor(endColorPicker)) // 设置初始颜色
                        .setDialogType(ColorPickerDialog.TYPE_PRESETS) // 设置对话框类型（预定义颜色或自定义颜色）
                        .setShowAlphaSlider(true) // 是否显示透明度滑块
                        .setDialogId(1) // 对话框 ID
                        .setAllowCustom(true)
                        .show(Ch3Ex2Activity.this); // 是否允许自定义颜色

            }
        });

        durationSelector.setText(String.valueOf(1000));
        durationSelector.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new MaterialDialog.Builder(Ch3Ex2Activity.this)
                        .inputType(InputType.TYPE_CLASS_NUMBER)
                        .input(getString(R.string.duration_hint), durationSelector.getText(), new MaterialDialog.InputCallback() {
                            @Override
                            public void onInput(@NonNull MaterialDialog dialog, CharSequence input) {
                                onDurationChanged(input.toString());
                            }
                        })
                        .show();
            }
        });
        resetTargetAnimation();
    }

    @Override
    public void onColorSelected(int dialogId, int color) {
        // 处理所选的颜色
        if (dialogId == 0) {
            onStartColorChanged(color);
        } else if (dialogId == 1) {
            onEndColorChanged(color);
        }
    }

    @Override
    public void onDialogDismissed(int dialogId) {
        // 处理所选的颜色
        if (dialogId == 0) {
            // 对话框关闭的回调
        } else if (dialogId == 1) {

        }
    }

    private void onStartColorChanged(int color) {
        startColorPicker.setBackgroundColor(color);
        resetTargetAnimation();
    }

    private void onEndColorChanged(int color) {
        endColorPicker.setBackgroundColor(color);
        resetTargetAnimation();
    }

    private void onDurationChanged(String input) {
        boolean isValid = true;
        try {
            int duration = Integer.parseInt(input);
            if (duration < 100 || duration > 10000) {
                isValid = false;
            }
        } catch (Throwable e) {
            isValid = false;
        }
        if (isValid) {
            durationSelector.setText(input);
            resetTargetAnimation();
        } else {
            Toast.makeText(Ch3Ex2Activity.this, R.string.invalid_duration, Toast.LENGTH_LONG).show();
        }
    }

    private int getBackgroundColor(View view) {
        Drawable bg = view.getBackground();
        if (bg instanceof ColorDrawable) {
            return ((ColorDrawable) bg).getColor();
        }
        return Color.WHITE;
    }

    private void resetTargetAnimation() {
        if (animatorSet != null) {
            animatorSet.cancel();
        }

        // 在这里实现了一个 ObjectAnimator，对 target 控件的背景色进行修改
        // 可以思考下，这里为什么要使用 ofArgb，而不是 ofInt 呢？
        ObjectAnimator animator1 = ObjectAnimator.ofArgb(target,
                "backgroundColor",
                getBackgroundColor(startColorPicker),
                getBackgroundColor(endColorPicker));
        animator1.setDuration(Integer.parseInt(durationSelector.getText().toString()));
        animator1.setRepeatCount(ObjectAnimator.INFINITE);
        animator1.setRepeatMode(ObjectAnimator.REVERSE);

        // TODO ex2-1：在这里实现另一个 ObjectAnimator，对 target 控件的大小进行缩放，从 1 到 2 循环

        // TODO ex2-2：在这里实现另一个 ObjectAnimator，对 target 控件的透明度进行修改，从 1 到 0.5f 循环

        // TODO ex2-3: 将上面创建的其他 ObjectAnimator 都添加到 AnimatorSet 中
        animatorSet = new AnimatorSet();
        animatorSet.playTogether(animator1);
        animatorSet.start();
    }

}

package com.example.homework01;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.tabs.TabLayout;


/**
 * 使用 ViewPager 和 Fragment 做一个简单版的好友列表界面
 * 1. 使用 ViewPager 和 Fragment 做个可滑动界面
 * 2. 使用 TabLayout 添加 Tab 支持
 * 3. 对于好友列表 Fragment，使用 Lottie 实现 Loading 效果，在 5s 后展示实际的列表，要求这里的动效是淡入淡出
 */
public class Ch3Ex3Activity extends AppCompatActivity {
    private Button appMain;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ch3ex3);

        final ViewPager pager = findViewById(R.id.view_pager);
        appMain = findViewById(R.id.appMain);
        appMain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 意图对象
                Intent intent = new Intent();
                // 意图对象的上下文
                intent.setClass(Ch3Ex3Activity.this, MainActivity.class);
                startActivity(intent);
            }
        });
        pager.setAdapter(new FragmentPagerAdapter(getSupportFragmentManager()) {
            @Override
            public Fragment getItem(int i) {
                return new PlaceholderFragment();
            }

            @Override
            public int getCount() {
                return 2;
            }

            @Override
            public CharSequence getPageTitle(int position) {
                if (position == 0)
                    return "好友列表";
                else if (position == 1)
                    return "我的好友";
                return null;
            }

        });

        // ex3-2, 添加 TabLayout 支持 Tab
        TabLayout tab = findViewById(R.id.tab_layout);
        tab.setupWithViewPager(pager);
    }
}


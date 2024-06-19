package com.example.mediaplayer.activity;

import android.content.Intent;
import android.os.Bundle;
import android.service.autofill.OnClickAction;
import android.view.View;
import android.widget.ImageView;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;
import com.example.mediaplayer.fragment.CollectSongsFragment;
import com.example.mediaplayer.R;
import com.example.mediaplayer.adapter.RecentPlayAdapter;
import com.example.mediaplayer.fragment.SelfSongsFragment;
import com.google.android.material.tabs.TabLayout;

import java.util.ArrayList;
import java.util.List;
/**
 * Create author: 李欣洁
 * Last version: 2024/5/28 
 * Description: MainActivity 
 */
public class MainActivity extends AppCompatActivity {
    private ImageView icon_local,icon_collect,icon_recent_play;
    private ViewPager viewPager;
    private TabLayout tabLayout;
    private List<String> titles = new ArrayList<>();//放标题
    private List<Fragment> fragments = new ArrayList<>();//放fragment
    ActionBar actionBar;
    RecyclerView recyclerView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        // 设置头部状态栏
        actionBar = getSupportActionBar();
        assert actionBar != null;
        actionBar.setTitle("我的");
        recyclerView = findViewById(R.id.recent_list);
        recyclerView.setLayoutManager(new LinearLayoutManager(this, RecyclerView.HORIZONTAL, false));
        recyclerView.setAdapter(new RecentPlayAdapter());
        init();
        // 设置图标点击事件
        icon_local = findViewById(R.id.icon_local);
        icon_local.setOnClickListener(new ClickIconAction());
        icon_collect = findViewById(R.id.icon_collect);
        icon_collect.setOnClickListener(new ClickIconAction());
        icon_recent_play = findViewById(R.id.icon_recent_play);
        icon_recent_play.setOnClickListener(new ClickIconAction());

    }


    // 点击icon监听事件
    private class ClickIconAction implements View.OnClickListener{
//        private int flag = 0;
        @Override
        public void onClick(View v){
            if(v.getId() == R.id.icon_local){
                // 跳转至本地音乐界面
                Intent intent = new Intent(MainActivity.this, LocalMusicActivity.class);
                startActivity(intent);
            }
            else if(v.getId() == R.id.icon_collect){
                Intent intent = new Intent(MainActivity.this,CollectMusicActivity.class);
                startActivity(intent);
            }
            else if(v.getId() == R.id.icon_recent_play){
                Intent intent = new Intent(MainActivity.this,RecentMusicActivity.class);
                startActivity(intent);
            }
        }
    }
    private void init(){
        tabLayout=findViewById(R.id.tab_layout);
        viewPager=findViewById(R.id.view_page);
        fragments.add(new SelfSongsFragment());
        fragments.add(new CollectSongsFragment());
        titles.add("自建歌单");
        titles.add("收藏歌单");
        //添加tab标签
        for (int i=0;i<titles.size();i++){
            tabLayout.addTab(tabLayout.newTab().setText(titles.get(i)));
        }
        //添加设置适配器
        viewPager.setAdapter(new ViewPagerAdapter(getSupportFragmentManager()));
        //把TabLayout与ViewPager关联起来
        tabLayout.setupWithViewPager(viewPager);
    }
    //自定义适配器
    class ViewPagerAdapter extends FragmentStatePagerAdapter {
        public ViewPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @NonNull
        @Override
        public Fragment getItem(int position) {
            return fragments.get(position);
        }


        @Override
        public int getCount() {
            return fragments.size();
        }

        @Nullable
        @Override
        public CharSequence getPageTitle(int position) {
            return titles.get(position);
        }
    }

}
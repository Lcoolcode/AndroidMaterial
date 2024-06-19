package com.example.mediaplayer.activity;
import android.os.Bundle;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.example.mediaplayer.dao.MusicDao;
import com.example.mediaplayer.db.AppDatabase;
import com.example.mediaplayer.db.MyDBHelper;
import com.example.mediaplayer.entity.Music;
import com.example.mediaplayer.fragment.AlbumFragment;
import com.example.mediaplayer.fragment.FolderFragment;
import com.example.mediaplayer.R;
import com.example.mediaplayer.fragment.SingerFragment;
import com.example.mediaplayer.fragment.SingleSongFragment;
import com.example.mediaplayer.fragment.TestFragment;
import com.example.mediaplayer.fragment.frag1;
import com.google.android.material.tabs.TabLayout;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;

/**
 * Create author: 李欣洁
 * Last version: 2024/5/28
 * Description: LocalMusicActivity
 */
public class LocalMusicActivity extends AppCompatActivity {
    private MyDBHelper myDBHelper;
    // 单曲列表、歌手列表
    private List<Music> songList,singerSongList,albumList;
    private ViewPager viewPager;
    private TabLayout tabLayout;
    private List<String> titles = new ArrayList<>();//放标题
    private List<Fragment> fragments = new ArrayList<>();//放fragment
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_local_music);
        //自带标题栏
        ActionBar actionBar=getSupportActionBar();
        actionBar.setTitle("本地音乐");
        //初始化控件
        init();
    }

    @Override
    protected void onStart() {
        super.onStart();
        myDBHelper = MyDBHelper.getInstance(this,1);
        myDBHelper.openReadLink();
    }
    private void init(){
        tabLayout=findViewById(R.id.tab_layout);
        viewPager=findViewById(R.id.view_page);
        fragments.add(new SingerFragment());
        fragments.add(new SingleSongFragment());
        fragments.add(new AlbumFragment());
        fragments.add(new FolderFragment());
        titles.add("歌手");
        titles.add("单曲");
        titles.add("专辑");
        titles.add("文件夹");
        //添加tab标签
        for (int i=0;i<titles.size();i++){
            tabLayout.addTab(tabLayout.newTab().setText(titles.get(i)));
        }
        //添加设置适配器
        viewPager.setAdapter(new ViewPagerAdapter(getSupportFragmentManager()));
        //把TabLayout与ViewPager关联起来
        tabLayout.setupWithViewPager(viewPager);
    }





    // 读取数据库中的全部数据
    public List<Music> readSQLite( ){
        System.out.println("单曲读取开始");
        if(myDBHelper != null){
            System.out.println("数据库打开成功");
            // 获取到所有歌曲的List
            songList = myDBHelper.queryInfo("singer=singer");
            System.out.println("songList大小:");
            System.out.println( songList.size());
        }

        return songList;
    }

    // 读取数据库的歌手数据
    public List<Music> readSingerList(){
        System.out.println("歌手读取开始");
        if(myDBHelper != null){
            System.out.println("数据库打开成功");
            // 获取到所有歌手List
            singerSongList = myDBHelper.querySingerInfo();
            System.out.println("singerSongList大小:");
            System.out.println( singerSongList.size());
        }
        return singerSongList;
    }
    //自定义适配器
    class ViewPagerAdapter extends FragmentStatePagerAdapter {
        public ViewPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @NonNull
        @Override
        public Fragment getItem(int position) {
            if(position == 0){
                singerSongList = readSingerList();
                Bundle bundle = new Bundle();
                bundle.putSerializable("singer-key",(Serializable)singerSongList);
                fragments.get(position).setArguments(bundle);
            }
            else if(position == 1){
                songList = readSQLite();
                // 传递数据给Fragment
                Bundle bundle = new Bundle();
                bundle.putSerializable("song-key",(Serializable)songList);
                fragments.get(position).setArguments(bundle);
            }
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
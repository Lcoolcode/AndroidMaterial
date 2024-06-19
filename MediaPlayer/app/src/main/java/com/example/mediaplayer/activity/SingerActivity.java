package com.example.mediaplayer.activity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.res.AssetManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.example.mediaplayer.R;
import com.example.mediaplayer.entity.Music;
import com.example.mediaplayer.fragment.ADetailSongFragment;
import com.example.mediaplayer.fragment.AlbumFragment;
import com.example.mediaplayer.fragment.TestFragment;
import com.example.mediaplayer.util.DisplayUtil;
import com.example.mediaplayer.view.SimpleViewPagerIndicator;
import com.example.mediaplayer.view.StickNavLayout;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

public class SingerActivity extends AppCompatActivity implements SimpleViewPagerIndicator.IndicatorClickListener, StickNavLayout.MyStickyListener{

    public static final String UID = "UID";
    public static final String[] titles = new String[]{"单曲","专辑"};

    StickNavLayout mStickNavLayout;

    ImageView iv_avatar;
    SimpleViewPagerIndicator mIndicator;
    ViewPager mViewPager;
    private TabFragmentPagerAdapter mAdapter;

    private List<Fragment> mFragments = new ArrayList<>();
    private ActionBar actionBar;

    private Intent intent1,intent2;
    private List<Music> musicList = new ArrayList<>();
    private String name;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_singer);
        intent1 = getIntent();
        name = intent1.getStringExtra("name");
        mStickNavLayout = findViewById(R.id.id_stickynavlayout);
        iv_avatar = findViewById(R.id.singer_main);
        mIndicator = findViewById(R.id.id_stickynavlayout_indicator);
        mViewPager = findViewById(R.id.id_stickynavlayout_viewpager);

        String filename = name + ".jpg";
        Bitmap bitmap = getBitmapFromAssets(filename);
        iv_avatar.setImageBitmap(bitmap);
        if(Build.VERSION.SDK_INT >= 21){
            View decorView = getWindow().getDecorView();
            //int option = View.SYSTEM_UI_FLAG_VISIBLE|View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN;
            int option = View.SYSTEM_UI_FLAG_LAYOUT_STABLE|View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN;
            decorView.setSystemUiVisibility(option);
//            getWindow().setStatusBarColor(Color.parseColor("#9C27B0"));
            getWindow().setStatusBarColor(Color.TRANSPARENT);
        }
        initView();
        initData();
//        //设置隐藏头部状态
        actionBar = getSupportActionBar();
        actionBar.hide();
    }

    protected void initData() {
    }

    protected void initView() {
        mIndicator.setIndicatorClickListener(this);
        mIndicator.setTitles(titles);
        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                mIndicator.scroll(position,positionOffset);
            }
            @Override
            public void onPageSelected(int position) {
            }
            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });
        for(int i=0;i<titles.length;i++){
            mFragments.add(ADetailSongFragment.newInstance());
        }
        mAdapter = new TabFragmentPagerAdapter(getSupportFragmentManager(),mFragments);
        mViewPager.setAdapter(mAdapter);
        mViewPager.setCurrentItem(0);
        mStickNavLayout.setScrollListener(this);
        int height = DisplayUtil.getScreenHeight(SingerActivity.this)-DisplayUtil.dip2px(SingerActivity.this,65)-DisplayUtil.dip2px(SingerActivity.this,40);
        LinearLayout.LayoutParams layoutParams= (LinearLayout.LayoutParams) mViewPager.getLayoutParams();
        layoutParams.height = height;
        mViewPager.setLayoutParams(layoutParams);
    }

    public static void toArtistDetailActivity(Context context, String uid){
        Intent intent = new Intent(context,SingerActivity.class);
        intent.putExtra(UID,uid);
        context.startActivity(intent);
    }

    @Override
    public void onClickItem(int k) {
        mViewPager.setCurrentItem(k);
        System.out.println("点击singe里面获取到的："+k);
    }
    //获取手机屏幕宽度，像素为单位
    private float getMobileWidth() {
        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);
        int width = dm.widthPixels;
        return width;
    }

    //改变顶部图片的大小，参数为导航栏相对于其父布局的top
    @Override
    public void imageScale(float bottom) {
        float height = DisplayUtil.dip2px(SingerActivity.this,220);
        float mScale = bottom/height;
        float width = getMobileWidth()*mScale;
        float dx = (width-getMobileWidth())/2;
        iv_avatar.layout((int)(0-dx),0,(int)(getMobileWidth()+dx),(int)bottom);
    }



    class TabFragmentPagerAdapter extends FragmentPagerAdapter {
        private List<Fragment> mlist;

        public TabFragmentPagerAdapter(FragmentManager fm, List<Fragment> list){
            super(fm);
            this.mlist = list;
        }

        @Override
        public Fragment getItem(int position) {
            musicList = loadData();
            Bundle bundle = new Bundle();
            bundle.putSerializable("songs",(Serializable)musicList);
            mFragments.get(position).setArguments(bundle);
            return mlist.get(position);
        }


        @Override
        public int getCount() {
            return mlist.size();
        }
    }

    public Bitmap getBitmapFromAssets(String fileName) {
        AssetManager assetManager = this.getAssets();
        InputStream is = null;
        Bitmap bitmap = null;
        try {
            is = assetManager.open(fileName);
            bitmap = BitmapFactory.decodeStream(is);
        } catch (IOException e) {
            // 异常处理
            e.printStackTrace();
        } finally {
            if (is != null) {
                try {
                    is.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        if (bitmap == null) {
            return null;
        }
        return bitmap;
    }

    @SuppressLint("Range")
    public List<Music> loadData(){
        // 获取歌曲列表
        List<Music> musics = new ArrayList<>();
        List<Music> songs = new ArrayList<>();
        // 1. 检查sdcard是否可用
        if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())) {
            // 2. 获取sdcard下Music文件夹的File对象
            File musicDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MUSIC);
            // 3. 检查Music文件夹是否存在
            if (musicDir.exists()) {
                // 4. 通过File类的listFiles()方法，获取Music文件夹下所有子级File对象
                File[] files = musicDir.listFiles();
                // 5. 检查获取到的File列表是否有效（数组是否为null，或数组长度是否为0）
                if (files != null && files.length > 0) {
                    // 6. 遍历File列表
                    for (int i = 0; i < files.length; i++) {
                        // 获取音乐文件的Cursor
                        Cursor cursor = this.getContentResolver().query(
                                MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                                null,
                                MediaStore.Audio.Media.DATA + "=?",
                                new String[] { files[i].getAbsolutePath() },  // filePath是文件的绝对路径
                                null);

                        if (cursor != null && cursor.moveToFirst()) {
                            do {
                                // 获取艺术家内容
                                @SuppressLint("Range") String artist = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.ARTIST));
                                @SuppressLint("Range") String fileName = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.DISPLAY_NAME));

                                if (fileName.toUpperCase(Locale.CHINA).endsWith(".MP3")) {
                                    Music music = new Music();
                                    music.setName(fileName.substring(0, fileName.length() - 4));
                                    music.setArtist(artist);
                                    music.setPath(cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.DATA)));
                                    musics.add(music);
                                }
                            } while (cursor.moveToNext());
                            cursor.close();
                        }
                    }

                }
            }
        }
        for(int i=0;i < musics.size();i++){
            if(musics.get(i).getArtist().equals(name)){
                songs.add(musics.get(i));
            }
        }

        return songs;
    }

}
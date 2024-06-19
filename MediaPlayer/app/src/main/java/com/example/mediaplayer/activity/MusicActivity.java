package com.example.mediaplayer.activity;

import androidx.annotation.DrawableRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;
import androidx.room.Room;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.example.mediaplayer.R;
import com.example.mediaplayer.dao.MusicDao;
import com.example.mediaplayer.db.AppDatabase;
import com.example.mediaplayer.entity.Music;
import com.example.mediaplayer.service.*;
import com.google.android.material.tabs.TabLayout;

import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.res.AssetManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.LinearInterpolator;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import static java.lang.Integer.parseInt;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import me.zhengken.lyricview.LyricView;

/**
 * Create author: 李欣洁
 * Last version: 2024/5/28 
 * Description: MusicActivity 音乐播放
 */
public class MusicActivity extends AppCompatActivity {
    private BroadcastReceiver uiUpdateReceiver;
    private View view;
    private Context context;
    private MyPagerAdapter myPagerAdapter;
    private ViewPager viewPager;
    private ArrayList<View> viewArrayList = new ArrayList<>();
    private Button btn_play, btn_next, btn_before, btn_collect;
    //进度条
    private static SeekBar sb;
    private static TextView tv_progress,tv_total,name_song;
    //动画
    private ObjectAnimator animator;
    private MusicService.MusicControl musicControl;
    private String name;
    private String path;
    // intent1用于接受数据；intent2用于从当前页面跳转到service页面
    private Intent intent1,intent2;
    private MyServiceConn conn;
    //记录服务是否被解绑，默认没有
    private boolean isUnbind =false;
    //跟踪音乐播放状态
    private boolean isPlaying = false;
    private int flag = 0;
    ActionBar actionBar;
    private TabLayout tabLayout;
    private static LyricView mLyricView;
    private static LyricView mLyricView1;
    private List<String> titles = new ArrayList<>();//放标题
    private int modeFlag = 1;
    private String playMode = "random";
    private int currentMusicIndex = 0; //当前播放歌曲
    private Music playMusic;
    private ExecutorService executorService;

    AppDatabase db;
    MusicDao dao;
    private LiveData<Boolean> isFavoriteLiveData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_music);
        // 初始播放列表数据
        intent1=getIntent();
        currentMusicIndex = intent1.getIntExtra("position",1);
        playMusic = getMusic(this,currentMusicIndex);
        // 初始化数据库及ExecutorService
        db = Room.databaseBuilder(getApplicationContext(),AppDatabase.class, "MusicPlayer.db").addMigrations(AppDatabase.MIGRATION_1_2).build();
        dao = db.musicDao();
        executorService = Executors.newSingleThreadExecutor();

        tabLayout = findViewById(R.id.tab_layout);
        btn_collect = findViewById(R.id.addCollect);
        btn_collect.setOnClickListener(new OnBtnClick());
        titles.add("歌曲");
        titles.add("歌词");
        for (int i=0;i<titles.size();i++){
            tabLayout.addTab(tabLayout.newTab().setText(titles.get(i)));
        }
        viewPager = findViewById(R.id.viewPager);
        @SuppressLint("InflateParams") View music_play_record = LayoutInflater.from(this).inflate(R.layout.music_play_record,null);
        @SuppressLint("InflateParams") View music_play_lrc = LayoutInflater.from(this).inflate(R.layout.music_play_lrc,null);
        viewArrayList.add(music_play_record);
        viewArrayList.add(music_play_lrc);
        myPagerAdapter = new MyPagerAdapter(viewArrayList);
        viewPager.setOffscreenPageLimit(viewArrayList.size() -1);
        viewPager.setAdapter(myPagerAdapter);
        viewPager.addOnPageChangeListener(onPageChangeListener);
        tabLayout.setupWithViewPager(viewPager);
        //设置隐藏头部状态
        actionBar = getSupportActionBar();
        actionBar.hide();
        view = viewArrayList.get(0);
        // 获取特定歌曲的isFavorite状态
        String songName = playMusic.getName();
        isFavoriteLiveData = dao.isSongFavorite(songName);
        // 观察LiveData对象
        isFavoriteLiveData.observe(this, new Observer<Boolean>() {
            @Override
            public void onChanged(@Nullable Boolean isFavorite) {
                // 当歌曲的isFavorite状态发生变化时，更新UI
                if (isFavorite != null) {
                    executorService.execute(()->{
                        if(!isFavorite){
                            btn_collect.setBackgroundResource(R.drawable.btn_collect1);
                        } else{
                            btn_collect.setBackgroundResource(R.drawable.btn_collect2);
                        }
                    });

                }
            }
        });

    }
    private ViewPager.OnPageChangeListener onPageChangeListener = new ViewPager.OnPageChangeListener() {
        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

        }

        @Override
        public void onPageSelected(int position) {
            //当新页面选中时调用此方法，position 为新选中页面的位置索引
            //在所选页面的时候,点点图片也要发生变化
        }

        @Override
        public void onPageScrollStateChanged(int state) {

        }
    };





    //用于实现连接服务，比较模板化，不需要详细知道内容
    class MyServiceConn  implements ServiceConnection{
        @Override
        public void onServiceConnected(ComponentName name, IBinder service){
            musicControl=(MusicService.MusicControl) service;
        }
        @Override
        public void onServiceDisconnected(ComponentName name){

        }
    }

    //判断服务是否被解绑
    private void unbind(boolean isUnbind){
        //如果解绑了
        if(!isUnbind){
            musicControl.pausePlay();//音乐暂停播放
            isPlaying = false;
            flag = 0;
            unbindService(conn);//解绑服务
        }
    }
    @Override
    protected void onDestroy(){
        super.onDestroy();
        unbind(isUnbind);//解绑服务
        executorService.shutdown();
    }
    //handler机制，可以理解为线程间的通信，我获取到一个信息，然后把这个信息告诉你，就这么简单
    @SuppressLint("HandlerLeak")
    public static Handler handler=new Handler(){//创建消息处理器对象
        //在主线程中处理从子线程发送过来的消息
        @Override
        public void handleMessage(Message msg){
            Bundle bundle=msg.getData();//获取从子线程发送过来的音乐播放进度
            //获取当前进度currentPosition和总时长duration
            int duration=bundle.getInt("duration");
            int currentPosition=bundle.getInt("currentPosition");
            //对进度条进行设置
            sb.setMax(duration);
            sb.setProgress(currentPosition);
            //歌曲是多少分钟多少秒钟
            int minute=duration/1000/60;
            int second=duration/1000%60;
            String strMinute=null;
            String strSecond=null;
            if(minute<10){//如果歌曲的时间中的分钟小于10
                strMinute="0"+minute;//在分钟的前面加一个0
            }else{
                strMinute=minute+"";
            }
            if (second<10){//如果歌曲中的秒钟小于10
                strSecond="0"+second;//在秒钟前面加一个0
            }else{
                strSecond=second+"";
            }
            //这里就显示了歌曲总时长
            tv_total.setText(strMinute+":"+strSecond);
            //歌曲当前播放时长
            minute=currentPosition/1000/60;
            second=currentPosition/1000%60;
            if(minute<10){//如果歌曲的时间中的分钟小于10
                strMinute="0"+minute;//在分钟的前面加一个0
            }else{
                strMinute=minute+" ";
            }
            if (second<10){//如果歌曲中的秒钟小于10
                strSecond="0"+second;//在秒钟前面加一个0
            }else{
                strSecond=second+" ";
            }
            //显示当前歌曲已经播放的时间
            tv_progress.setText(strMinute+":"+strSecond);
            mLyricView.setCurrentTimeMillis(currentPosition);
            mLyricView1.setCurrentTimeMillis(currentPosition);
        }
    };

    class OnBtnClick implements View.OnClickListener{
        @Override
        public void onClick(View v) {
            if (v.getId() == R.id.btn_play){
                if(!isPlaying  && flag == 0){
                    path = playMusic.getPath();
                    ExecutorService databaseExecutor = Executors.newSingleThreadExecutor();
                    databaseExecutor.execute(new Runnable() {
                        @Override
                        public void run() {
                            Music music = playMusic;
                            music.setLastPlayed(System.currentTimeMillis());
                            dao.insertSong(music);
                        }
                    });
                    try {
                        System.out.println("play传入的index："+ currentMusicIndex + path);
                        musicControl.play(path,playMode,currentMusicIndex);
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                    animator.start();
                    isPlaying = true;
                    flag = 1;
                    btn_play.setBackgroundResource(R.drawable.icon_pause);
                }
                else if(!isPlaying && flag == 1){
                    musicControl.continuePlay();
                    animator.start();
                    isPlaying = true;
                    btn_play.setBackgroundResource(R.drawable.icon_pause);
                }
                else if(isPlaying){
                    musicControl.pausePlay();
                    animator.pause();
                    isPlaying = false;
                    flag = 1;
                    btn_play.setBackgroundResource(R.drawable.icon_play);
                }
            }
            else if (v.getId() == R.id.btn_exit){
                unbind(isUnbind);
                isUnbind=true;
                finish();
            }
            else if (v.getId() == R.id.addCollect){
                //收藏
                Music music = playMusic;
                ExecutorService databaseExecutor = Executors.newSingleThreadExecutor();
                databaseExecutor.execute(new Runnable() {
                    @Override
                    public void run() {
                        if(!music.isFavorite()){
                            music.setFavorite(true);
                            dao.updateFavorite(music.getName(),true);
                            btn_collect.setBackgroundResource(R.drawable.btn_collect2);
                        } else{
                            music.setFavorite(false);
                            dao.updateFavorite(music.getName(),false);
                            btn_collect.setBackgroundResource(R.drawable.btn_collect1);
                        }

                    }
                });


            }
            else if(v.getId() == R.id.btn_before){
                // 播放上一首
                System.out.println("播放上一首");
                unbind(isUnbind);
                isUnbind=true;
                currentMusicIndex--;
                flag = 0;
                playMusic = getMusic(context,currentMusicIndex);
                path = playMusic.getPath();
//                System.out.println(currentMusicIndex);
                //创建一个意图对象，是从当前的Activity跳转到Service
                Intent intent3 = new Intent(view.getContext(), MusicService.class);
                conn = new MyServiceConn();//创建服务连接对象
                bindService(intent3, conn, BIND_AUTO_CREATE);//绑定服务
                init(view, 0, currentMusicIndex);
            }
            else if(v.getId() == R.id.btn_next){
                // 播放下一首
                System.out.println("播放下一首");
                unbind(isUnbind);
                isUnbind=true;
                currentMusicIndex++;
                flag = 0;
                //创建一个意图对象，是从当前的Activity跳转到Service
                Intent intent3  = new Intent(view.getContext(), MusicService.class);
                conn = new MyServiceConn();//创建服务连接对象
                bindService(intent3, conn, BIND_AUTO_CREATE);//绑定服务
                playMusic = getMusic(context,currentMusicIndex);
                init(view, 0, currentMusicIndex);
            }
        }
    }

    public class MyPagerAdapter extends PagerAdapter {
        private ArrayList<View> viewArrayList = new ArrayList<>();

        // 页面适配器
        public MyPagerAdapter(ArrayList<View> viewArrayList) {
            this.viewArrayList = viewArrayList;
        }

        @Override
        public int getCount() {
            return viewArrayList.size();
        }

        @Override
        public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
            return view == object;
        }

        @NonNull
        @Override
        public Object instantiateItem(@NonNull ViewGroup container, int position) {
            View view = viewArrayList.get(position);
            container.addView(view, 0);
            init(view, position, currentMusicIndex);
            return viewArrayList.get(position);
        }
        @Nullable
        @Override
        public CharSequence getPageTitle(int position) {
            return titles.get(position);
        }

        @Override
        public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
            container.removeView(viewArrayList.get(position));
        }
    }

    public void init(View view,int position,int index) {
        context = view.getContext();
        if (position == 0) {
            //进度条上小绿点的位置，也就是当前已播放时间
            tv_progress = (TextView) findViewById(R.id.tv_progress);
            //进度条的总长度，就是总时间
            tv_total = (TextView) findViewById(R.id.tv_total);
            //进度条的控件
            sb = (SeekBar) findViewById(R.id.sb);
            //歌曲名显示的控件
            name_song = (TextView) findViewById(R.id.song_name);
            btn_play = findViewById(R.id.btn_play);
            btn_next = findViewById(R.id.btn_next);
            btn_before = findViewById(R.id.btn_before);
            //绑定控件的同时设置点击事件监听器
            findViewById(R.id.btn_play).setOnClickListener(new OnBtnClick());
            findViewById(R.id.btn_next).setOnClickListener(new OnBtnClick());
            findViewById(R.id.btn_before).setOnClickListener(new OnBtnClick());
            findViewById(R.id.btn_exit).setOnClickListener(new OnBtnClick());


            //创建一个意图对象，是从当前的Activity跳转到Service
            intent2 = new Intent(view.getContext(), MusicService.class);
            conn = new MyServiceConn();//创建服务连接对象
            bindService(intent2, conn, BIND_AUTO_CREATE);//绑定服务

            //为滑动条添加事件监听，每个控件不同果然点击事件方法名都不同
            sb.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                @Override
                public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                    //进当滑动条到末端时，结束动画
                    if (progress == seekBar.getMax()) {
                        animator.pause();//停止播放动画
                        btn_play.setBackgroundResource(R.drawable.icon_pause);
                    }
                }
                @Override
                //滑动条开始滑动时调用
                public void onStartTrackingTouch(SeekBar seekBar) {
                }

                @Override
                //滑动条停止滑动时调用
                public void onStopTrackingTouch(SeekBar seekBar) {
                    //根据拖动的进度改变音乐播放进度
                    int progress = seekBar.getProgress();//获取seekBar的进度
                    musicControl.seekTo(progress);//改变播放进度
                }
            });
            //声明并绑定音乐播放器的iv_music控件
            ImageView iv_music = (ImageView) findViewById(R.id.iv_music);
            String file = playMusic.getName();
            name_song.setText(playMusic.getName());

            System.out.println("当前播放index:" + index);
            String filename = file + ".jpg";
            Bitmap bitmap = getBitmapFromAssets(view.getContext(), filename);
            iv_music.setImageBitmap(bitmap);

            //rotation和0f,360.0f就设置了动画是从0°旋转到360°
            animator = ObjectAnimator.ofFloat(iv_music, "rotation", 0f, 360.0f);
            animator.setDuration(10000);//动画旋转一周的时间为10秒
            animator.setInterpolator(new LinearInterpolator());//匀速
            animator.setRepeatCount(-1);//-1表示设置动画无限循环
            mLyricView1 = (LyricView) view.findViewById(R.id.lyric_view);
            String filename1 = file + ".lrc";
            File file1 = getFileFromAssets(view.getContext(), filename1);
            mLyricView1.setLyricFile(file1);

        } else if (position == 1) {
            mLyricView = (LyricView) view.findViewById(R.id.custom_lyric_view);
            String file = playMusic.getName();
            String filename = file + ".lrc";
            File file1 = getFileFromAssets(view.getContext(), filename);
            mLyricView.setLyricFile(file1);
        }
    }
    public static File getFileFromAssets(Context context, String assetFileName) {
        File file = new File(context.getFilesDir(), assetFileName);
        if (!file.exists() || file.length() == 0) {
            try (InputStream is = context.getAssets().open(assetFileName);
                 OutputStream os = new FileOutputStream(file)) {
                byte[] buffer = new byte[1024];
                int read;
                while ((read = is.read(buffer)) != -1) {
                    os.write(buffer, 0, read);
                }
                os.flush();
            } catch (IOException e) {
                e.printStackTrace();
                return null;
            }
        }
        return file;
    }


    public Bitmap getBitmapFromAssets(Context context, String fileName) {
        AssetManager assetManager = context.getAssets();
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
        // 绘制圆形图片
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();
        int radius = Math.min(width, height) / 2;
        Bitmap circleBitmap = Bitmap.createBitmap(radius * 2, radius * 2, Bitmap.Config.ARGB_8888);
        Paint paint = new Paint();
        Canvas canvas = new Canvas(circleBitmap);
        final Rect rect = new Rect(0, 0, radius * 2, radius * 2);
        final RectF rectF = new RectF(rect);
        paint.setAntiAlias(true);
        paint.setFilterBitmap(true);
        paint.setDither(true);
        canvas.drawCircle(radius, radius, radius, paint);
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        canvas.drawBitmap(bitmap, rect, rectF, paint);
        return circleBitmap;
    }


    @SuppressLint("Range")
    public Music getMusic(Context context,int position){
        // 获取歌曲列表
        List<Music> musics = new ArrayList<>();
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
                        Cursor cursor = context.getContentResolver().query(
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

        return musics.get(position);
    }

}


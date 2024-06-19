package com.example.mediaplayer.service;

import android.app.Service;
import android.content.Intent;
import android.content.res.AssetFileDescriptor;
import android.content.res.AssetManager;
import android.media.MediaPlayer;
import android.os.Binder;
import android.os.Bundle;
import android.os.Environment;
import android.os.IBinder;
import android.os.Message;

import com.example.mediaplayer.activity.MusicActivity;

import java.io.FileDescriptor;
import java.io.IOException;
import java.util.Objects;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Create author: 李欣洁
 * Last version: 2024/5/30
 * Description: MusicService 音乐播放服务类
 */
public class MusicService extends Service {
    //声明一个MediaPlayer引用
    private MediaPlayer player;
    //声明一个计时器引用
    private Timer timer;
    //构造函数
    public MusicService() {}
    @Override
    public IBinder onBind(Intent intent){
        return new MusicControl();
    }
    @Override
    public void onCreate(){
        super.onCreate();
        //创建音乐播放器对象
        player=new MediaPlayer();
    }
    //添加计时器用于设置音乐播放器中的播放进度条
    public void addTimer(){
        //如果timer不存在，也就是没有引用实例
        if(timer==null){
            //创建计时器对象
            timer=new Timer();
            TimerTask task=new TimerTask() {
                @Override
                public void run() {
                    if (player==null) return;
                    int duration=player.getDuration();//获取歌曲总时长
                    int currentPosition=player.getCurrentPosition();//获取播放进度
                    Message msg= MusicActivity.handler.obtainMessage();//创建消息对象
                    //将音乐的总时长和播放进度封装至bundle中
                    Bundle bundle=new Bundle();
                    bundle.putInt("duration",duration);
                    bundle.putInt("currentPosition",currentPosition);
                    //再将bundle封装到msg消息对象中
                    msg.setData(bundle);
                    //最后将消息发送到主线程的消息队列
                    MusicActivity.handler.sendMessage(msg);
                }
            };
            //开始计时任务后的5毫秒，第一次执行task任务，以后每500毫秒（0.5s）执行一次
            timer.schedule(task,5,500);
        }
    }
    //Binder是一种跨进程的通信方式
    public class MusicControl extends Binder {
        public void play(String path,String playMode,int index) throws IOException {//String path
            if(player == null){
                player = new MediaPlayer();
            }
            try {
                player.setDataSource(path);
                player.prepare();
                player.start();//播放音乐
                player.setLooping(true);
                addTimer();
            } catch (IllegalStateException e) {
                e.printStackTrace();
                if(player.isPlaying()) player.stop();//停止播放音乐
                player.release();
                player=null;//将player置为空
                player = new MediaPlayer();
                player.setDataSource(path);
                player.prepare();
                player.start();//播放音乐
                player.setLooping(true);
                addTimer();
            } catch (IOException e) {
                e.printStackTrace();
                // 处理 IO 异常
            }

        }
        //下面的暂停继续和退出方法全部调用的是MediaPlayer自带的方法
        public void pausePlay(){
            player.pause();//暂停播放音乐
        }
        public void continuePlay(){
            player.start();//继续播放音乐
        }
        public void seekTo(int progress){
            player.seekTo(progress);//设置音乐的播放位置
        }

    }

    //销毁多媒体播放器
    @Override
    public void onDestroy(){
        super.onDestroy();
        if(player==null) return;
        if(player.isPlaying()) player.stop();//停止播放音乐
        player.release();//释放占用的资源
        player=null;//将player置为空
    }
}


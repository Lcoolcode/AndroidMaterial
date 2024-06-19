package com.example.mediaplayer.fragment;

import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import com.example.mediaplayer.R;

import java.io.IOException;

public class frag2 extends Fragment {
    //创建一个View
    private View zj;
    private TextView test;
    private Button btn;
    private MediaPlayer mediaPlayer;
    //显示布局
    public View onCreateView(final LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        zj = inflater.inflate(R.layout.frag2_layout, null);
        btn = zj.findViewById(R.id.btn);
        btn.setOnClickListener(new OnBtnClick());
        return zj;
    }
    class OnBtnClick implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            if(mediaPlayer == null){
                mediaPlayer = new MediaPlayer();
            }
            // 设置MP3文件路径
            String path = Environment.getExternalStorageDirectory().getPath() + "/Music/麋鹿王.mp3";
            System.out.println(path);
            try {
                mediaPlayer.setDataSource(path);
                mediaPlayer.prepare(); // 或者 mediaPlayer.prepareAsync();
            } catch (IOException e) {
                e.printStackTrace();
            }
            if (mediaPlayer.isPlaying()) {
                mediaPlayer.pause();
                btn.setText("Play");
            } else {
                mediaPlayer.start();
                btn.setText("Pause");
            }
        }
    }
    @Override
    public void onDestroy() {
        super.onDestroy();
        if(mediaPlayer!=null){
            mediaPlayer.release();
            mediaPlayer = null;
        }
    }
}

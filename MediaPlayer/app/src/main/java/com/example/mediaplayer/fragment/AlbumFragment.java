package com.example.mediaplayer.fragment;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.mediaplayer.activity.AlbumSongActivity;
import com.example.mediaplayer.adapter.AlbumFragmentAdapter;
import com.example.mediaplayer.R;
import com.example.mediaplayer.service.MusicService;

import java.util.ArrayList;
import static android.content.Context.BIND_AUTO_CREATE;

import android.annotation.SuppressLint;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.res.AssetManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Environment;
import android.os.IBinder;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.mediaplayer.R;
import com.example.mediaplayer.activity.MusicActivity;
import com.example.mediaplayer.entity.Music;
import com.example.mediaplayer.service.MusicService;


import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
/**
 * Create author: 李欣洁
 * Last version: 2024/5/28
 * Description: AlbumFragment
 */
public class AlbumFragment extends Fragment {
    // 专辑列表
    private View view;
    private Context context;
    private ArrayList<String> album_name = new ArrayList<>();
    private ArrayList<String> singer_name = new ArrayList<>();
    private ArrayList<String> music_file = new ArrayList<>();
    private ArrayList<Bitmap> icons = new ArrayList<Bitmap>();
    private Intent intent1;
    private MusicService.MusicControl musicControl;
    private SingleSongFragment.MyServiceConn conn;
    private ListView listView;
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.music_list,container,false);
        context = this.getContext();
        List<Music> musicList = loadData();
        for (int i = 0; i < musicList.size();i++){
            album_name.add(musicList.get(i).getAlbum());
            singer_name.add(musicList.get(i).getArtist());
            music_file.add(musicList.get(i).getPath());
            String filename = musicList.get(i).getName() + ".jpg";
            AssetManager am = getActivity().getAssets();
            InputStream is = null;
            Bitmap bitmap = null;
            try {
                is = am.open(filename);
                bitmap = BitmapFactory.decodeStream(is);
                is.close();
                icons.add(bitmap);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        //创建listView列表并且绑定控件
        ListView listView=view.findViewById(R.id.lv);
        //实例化一个适配器
        MyBaseAdapter adapter= new MyBaseAdapter();
        //列表设置适配器
        listView.setAdapter(adapter);
        //列表元素的点击监听器
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if(isAdded() && getActivity() != null){
                    System.out.println("点击专辑");
                    Intent intent=new Intent(AlbumFragment.this.getContext(), AlbumSongActivity.class);
                    //将歌曲名和歌曲的下标存入Intent对象
                    intent.putExtra("name",album_name.get(position));
                    intent.putExtra("file",music_file.get(position));
                    intent.putExtra("position",position);
                    //开始跳转
                    startActivity(intent);
                }
            }
        });
        System.out.println("接受数据");

        return  view;
    }


    //用于实现连接服务
    class MyServiceConn  implements ServiceConnection {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service){
            musicControl=(MusicService.MusicControl) service;
        }
        @Override
        public void onServiceDisconnected(ComponentName name){

        }
    }
    class MyBaseAdapter extends BaseAdapter {
        @Override
        public int getCount(){return  album_name.size();}
        @Override
        public Object getItem(int i){return album_name.get(i);}
        @Override
        public long getItemId(int i){return i;}

        @Override
        public View getView(int i ,View convertView, ViewGroup parent) {
            //绑定好VIew，然后绑定控件
            @SuppressLint("ViewHolder") View view=View.inflate(AlbumFragment.this.getContext(),R.layout.item_layout,null);
            TextView tv_name=view.findViewById(R.id.item_name);
            ImageView iv=view.findViewById(R.id.iv);
            TextView tv_singer=view.findViewById(R.id.item_singer);
            //设置控件显示的内容，就是获取的歌曲名和歌手图片
            tv_name.setText(album_name.get(i));
            iv.setImageBitmap(icons.get(i));
            tv_singer.setText(singer_name.get(i));
            return view;
        }
    }


    @SuppressLint("Range")
    public List<Music> loadData(){
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
                                @SuppressLint("Range") String album = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.ALBUM));
                                @SuppressLint("Range") String fileName = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.DISPLAY_NAME));

                                if (fileName.toUpperCase(Locale.CHINA).endsWith(".MP3")) {
                                    Music music = new Music();
                                    music.setName(fileName.substring(0, fileName.length() - 4));
                                    music.setArtist(artist);
                                    music.setPath(cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.DATA)));
                                    music.setAlbum(album);
                                    System.out.println(album);
                                    musics.add(music);
                                }
                            } while (cursor.moveToNext());
                            cursor.close();
                        }
                    }
                }
            }
        }
        System.out.println("SingleSongFragment界面的size："+musics.size());

        return musics;
    }
    
}
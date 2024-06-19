package com.example.mediaplayer.activity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.res.AssetManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.room.Room;

import com.example.mediaplayer.R;
import com.example.mediaplayer.dao.MusicDao;
import com.example.mediaplayer.db.AppDatabase;
import com.example.mediaplayer.entity.Music;
import com.example.mediaplayer.fragment.SingleSongFragment;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class AlbumSongActivity extends AppCompatActivity {
    private Intent intent;
    private ImageView imageView;
    private String album_name;
    private List<Music> album_lists;
    private ActionBar actionBar;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        Context context =this;
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_album_song);
        intent = getIntent();
        album_name = intent.getStringExtra("name");
        actionBar = getSupportActionBar();
        assert actionBar != null;
        String title = "专辑："+album_name;
        actionBar.setTitle(title);
        album_lists = loadData();
        System.out.println("album_list"+album_lists.size());
        String filename = album_name + ".jpg";
        AssetManager am = this.getAssets();
        InputStream is = null;
        Bitmap bitmap = null;
        try {
            is = am.open(filename);
            bitmap = BitmapFactory.decodeStream(is);
            is.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        imageView = findViewById(R.id.album_image);
        imageView.setImageBitmap(bitmap);
        RecyclerView recyclerView = findViewById(R.id.recyclerView);
        AlbumAdapter adapter = new AlbumAdapter(context,album_lists);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(AlbumSongActivity.this));
    }
    class AlbumAdapter extends RecyclerView.Adapter<AlbumSongActivity.AlbumAdapter.ViewHolder> {
        private List<Music> musicList;
        private LayoutInflater mInflater;

        // 数据传递到适配器
        AlbumAdapter(Context context, List<Music> data) {
            this.mInflater = LayoutInflater.from(context);
            this.musicList = data;
        }

        // 为每个item创建ViewHolder
        @Override
        public AlbumSongActivity.AlbumAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = mInflater.inflate(R.layout.recyclerview_item, parent, false);
            return new AlbumSongActivity.AlbumAdapter.ViewHolder(view);
        }

        // 绑定数据到ViewHolder
        @Override
        public void onBindViewHolder(AlbumSongActivity.AlbumAdapter.ViewHolder holder, int position) {
            Music music = musicList.get(position);
            holder.myTextView.setText(music.getName());
            holder.singerView.setText(music.getArtist());
            // 更多绑定数据的代码...
        }

        // 总item数
        @Override
        public int getItemCount() {
            return musicList.size();
        }

        // 存储和复用Views的类
        public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
            TextView myTextView;
            TextView singerView;

            ViewHolder(View itemView) {
                super(itemView);
                myTextView = itemView.findViewById(R.id.tvMusicName);
                singerView = itemView.findViewById(R.id.tvMusicDescription);
                itemView.setOnClickListener(this);
            }

            @Override
            public void onClick(View view) {
                //创建Intent对象，参数就是从frag1跳转到MusicActivity
                int position = getAdapterPosition();
                Intent intent=new Intent(AlbumSongActivity.this, MusicActivity.class);
//                System.out.println("点击获取到的position" + position);
                //将歌曲名和歌曲的下标存入Intent对象
                intent.putExtra("name",album_lists.get(position).getName());
                intent.putExtra("file",album_lists.get(position).getPath());
                intent.putExtra("position",position);
//                //开始跳转
                startActivity(intent);
            }
        }
        // 便于获取点击事件

    }
    @SuppressLint("Range")
    public List<Music> loadData(){
        // 获取歌曲列表
        List<Music> musics = new ArrayList<>();
        List<Music> tmp = new ArrayList<>();
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
        for(int i=0;i<musics.size();i++){
            System.out.println(album_name);
            if (musics.get(i).getAlbum().equals(album_name)){
                tmp.add(musics.get(i));
            }
            System.out.println("tmp"+tmp.size());
        }
        return tmp;
    }
}

package com.example.mediaplayer.fragment;

import android.annotation.SuppressLint;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.res.AssetManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.Image;
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
import com.example.mediaplayer.activity.AlbumSongActivity;
import com.example.mediaplayer.activity.MusicActivity;
import com.example.mediaplayer.entity.Music;
import com.example.mediaplayer.service.MusicService;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class CollectAlbumsFragment extends Fragment {
    // 专辑列表
    private View view;
    private Context context;
    private ArrayList<String> album_name = new ArrayList<>();
    private ArrayList<String> singer_name = new ArrayList<>();
    private ArrayList<String> song_name = new ArrayList<>();
    private ArrayList<String> music_file = new ArrayList<>();
    private ArrayList<Bitmap> icons = new ArrayList<Bitmap>();
    private Intent intent1;
    private MusicService.MusicControl musicControl;
    private MyServiceConn conn;
    private ListView listView;
    private List<Music> album_list;
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.music_list, container, false);
        Bundle arguments = getArguments();
        context = this.getContext();
        if (arguments != null) {
           album_list = (List<Music>) arguments.getSerializable("album-key");
            for (int i = 0; i < album_list.size(); i++) {
                album_name.add(album_list.get(i).getAlbum());
                song_name.add(album_list.get(i).getName());
                singer_name.add(album_list.get(i).getArtist());
                music_file.add(album_list.get(i).getPath());
                String filename = album_list.get(i).getName() + ".jpg";
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
            ListView listView = view.findViewById(R.id.lv);
            //实例化一个适配器
            MyBaseAdapter adapter= new MyBaseAdapter();
            //列表设置适配器
            listView.setAdapter(adapter);
            //列表元素的点击监听器
            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    if (isAdded() && getActivity() != null) {
                        System.out.println("点击专辑");
                        //创建Intent对象，参数就是从frag1跳转到MusicActivity
                        Intent intent = new Intent(CollectAlbumsFragment.this.getContext(), AlbumSongActivity.class);
                        //将歌曲名和歌曲的下标存入Intent对象
                        intent.putExtra("song_name", song_name.get(position));
                        intent.putExtra("album_name", album_name.get(position));
                        intent.putExtra("file", music_file.get(position));
                        intent.putExtra("position", position);
                        //开始跳转
                        startActivity(intent);
                    }
                }
            });
            System.out.println("接受数据");
        }
        return view;
    }



    //用于实现连接服务，比较模板化，不需要详细知道内容
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
        public int getCount() {
            return album_name.size();
        }

        @Override
        public Object getItem(int i) {
            return album_name.get(i);
        }

        @Override
        public long getItemId(int i) {
            return i;
        }

        @Override
        public View getView(int i, View convertView, ViewGroup parent) {
            //绑定好VIew，然后绑定控件
            @SuppressLint("ViewHolder") View view = View.inflate(CollectAlbumsFragment.this.getContext(), R.layout.item_layout, null);
            TextView tv_name = view.findViewById(R.id.item_name);
            ImageView iv = view.findViewById(R.id.iv);
            TextView tv_singer = view.findViewById(R.id.item_singer);
            //设置控件显示的内容，就是获取的歌曲名和歌手图片
            tv_name.setText(album_name.get(i));
            iv.setImageBitmap(icons.get(i));
            tv_singer.setText(singer_name.get(i));
            return view;
        }
    }
}

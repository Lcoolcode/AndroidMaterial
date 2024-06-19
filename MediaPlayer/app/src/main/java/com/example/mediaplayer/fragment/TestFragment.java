package com.example.mediaplayer.fragment;

import android.content.Context;
import android.content.Intent;
import android.content.res.AssetFileDescriptor;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
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
import com.example.mediaplayer.entity.MP3;

import java.io.FileDescriptor;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class TestFragment extends Fragment {
    private View view;
    private ArrayList<String> song_name = new ArrayList<>();
    private ArrayList<String> singer_name = new ArrayList<>();
    private ArrayList<String> music_file = new ArrayList<>();
    private ArrayList<Bitmap> icons = new ArrayList<Bitmap>();
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        //绑定布局，只不过这里是用inflate()方法
        view=inflater.inflate(R.layout.music_list,null);
        Bundle arguments = getArguments();
        if (arguments != null) {
            List<MP3> mp3List = (List<MP3>) arguments.getSerializable("song-key");
            if (mp3List != null) {
                System.out.println("接受到的" + mp3List.size());
                for(int i=0 ;i<mp3List.size();i++){
                    song_name.add(mp3List.get(i).getSong());
                    singer_name.add(mp3List.get(i).getSinger());
                    music_file.add(mp3List.get(i).getSong());
                    String filename = mp3List.get(i).getSong() + ".jpg";
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
            } else {
                System.out.println("mp3List 是 null");
            }
        } else {
            System.out.println("没有接收到 Arguments");
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
                //创建Intent对象，参数就是从frag1跳转到MusicActivity
                Intent intent=new Intent(TestFragment.this.getContext(), MusicActivity.class);
                //将歌曲名和歌曲的下标存入Intent对象
                intent.putExtra("name",song_name.get(position));
                System.out.println("点击item传入的");
                System.out.println(song_name.get(position));
//                Bundle bundle = new Bundle();
//                bundle.putParcelable("image", icons.get(position));
//                intent.putExtra("bundle", bundle);
                intent.putExtra("file",music_file.get(position));
                intent.putExtra("position",String.valueOf(position));
                //开始跳转
                startActivity(intent);
                }
            }
        });
        System.out.println("接受数据");
        return  view;
    }


    class MyBaseAdapter extends BaseAdapter {
        @Override
        public int getCount(){return  song_name.size();}
        @Override
        public Object getItem(int i){return song_name.get(i);}
        @Override
        public long getItemId(int i){return i;}

        @Override
        public View getView(int i ,View convertView, ViewGroup parent) {
            //绑定好VIew，然后绑定控件
            View view=View.inflate(TestFragment.this.getContext(),R.layout.item_layout,null);
            TextView tv_name=view.findViewById(R.id.item_name);
            ImageView iv=view.findViewById(R.id.iv);
            TextView tv_singer=view.findViewById(R.id.item_singer);
            //设置控件显示的内容，就是获取的歌曲名和歌手图片
            tv_name.setText(song_name.get(i));
            iv.setImageBitmap(icons.get(i));
            tv_singer.setText(singer_name.get(i));
            return view;
        }
    }
}

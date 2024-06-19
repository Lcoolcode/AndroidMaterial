package com.example.mediaplayer.fragment;

import android.content.Intent;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.mediaplayer.R;
import com.example.mediaplayer.activity.MusicActivity;
import com.example.mediaplayer.activity.SingerActivity;
import com.example.mediaplayer.entity.MP3;
import com.example.mediaplayer.entity.Music;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * Create author: 李欣洁
 * Last version: 2024/5/28
 * Description: SingerFragment 本地歌曲-歌手
 */
public class SingerFragment extends Fragment {
    // 歌手名字
    private ArrayList<String> singer_name = new ArrayList<>();
    private ArrayList<String> gender = new ArrayList<>();
    private ArrayList<Bitmap> images = new ArrayList<>();
    private  List<Music> mp3List;

    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // 绑定布局
        View view = inflater.inflate(R.layout.fragment_singer,null);
        Bundle arguments = getArguments();
        if (arguments != null) {
            // 获取歌手列表
            mp3List = (List<Music>) arguments.getSerializable("singer-key");
            System.out.println(mp3List.size());
            if (mp3List != null) {
                System.out.println("接受到的" + mp3List.size());
                for(int i=0 ;i<mp3List.size();i++){
                    singer_name.add(mp3List.get(i).getArtist());
                    String filename = mp3List.get(i).getArtist() + ".jpg";
                    AssetManager am = getActivity().getAssets();
                    InputStream is = null;
                    Bitmap bitmap = null;
                    try {
                        is = am.open(filename);
                        bitmap = BitmapFactory.decodeStream(is);
                        is.close();
                        images.add(bitmap);
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

        // 创建列表并绑定控件
        ListView listView = view.findViewById(R.id.singer_lv);
        MyBaseAdapter adapter = new MyBaseAdapter();
        listView.setAdapter(adapter);
        //列表元素的点击监听器
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if(isAdded() && getActivity() != null){
                    System.out.println("点击歌手按钮");
                    //创建Intent对象，跳转到SingerActivity
                    Intent intent=new Intent(SingerFragment.this.getContext(), SingerActivity.class);
                    System.out.println(mp3List.get(position).getArtist());
                    //将歌曲名和歌曲的下标存入Intent对象
                    intent.putExtra("name", mp3List.get(position).getArtist());
                    //开始跳转
                    startActivity(intent);
                }
            }
        });
        return view;
    }

    /**
     * 默认显示所有歌手
     * 点击btn_all     显示所有歌手
     * 点击btn_male    显示男歌手
     * 点击btn_female  显示女歌手
     */
    //监听
    class ClickOnButton implements View.OnClickListener{
        @Override
        public void onClick(View v) {
            if(v.getId() == R.id.btn_all){

            }
            else if(v.getId() == R.id.btn_male){

            }
            else if(v.getId() == R.id.btn_female){

            }

        }
    }
    //创建自定义适配器
    class MyBaseAdapter extends BaseAdapter{
        @Override
        public int getCount(){return  singer_name.size();}
        @Override
        public Object getItem(int i){return singer_name.get(i);}
        @Override
        public long getItemId(int i){return i;}

        @Override
        public View getView(int i ,View convertView, ViewGroup parent) {
            //绑定好VIew，然后绑定控件
            View view=View.inflate(SingerFragment.this.getContext(),R.layout.singer_item,null);
            TextView tv_name=view.findViewById(R.id.singer_name);
            ImageView iv=view.findViewById(R.id.singer_image);
            //设置控件显示的内容，就是获取的歌曲名和歌手图片
            tv_name.setText(singer_name.get(i));
            iv.setImageBitmap(images.get(i));
            return view;
        }
    }
}
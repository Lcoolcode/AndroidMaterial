package com.example.mediaplayer.fragment;


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
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.mediaplayer.R;
import com.example.mediaplayer.activity.AlbumSongActivity;
import com.example.mediaplayer.activity.MusicActivity;
import com.example.mediaplayer.entity.Music;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;


@SuppressLint("ValidFragment")
public class ADetailSongFragment extends Fragment {

    //    @Bind(R.id.ll_detail_song)
//    LinearLayout llSong;
    RecyclerView mRecyclerView;
    private int height=0;
    private List<Music> musicList = new ArrayList<>();
    private Context context;
    private ArrayList<String> song_name = new ArrayList<>();
    private ArrayList<String> singer_name = new ArrayList<>();
    private ArrayList<String> music_file = new ArrayList<>();
    private ArrayList<Bitmap> icons = new ArrayList<Bitmap>();



    @SuppressLint("ValidFragment")
    public ADetailSongFragment(){
    }

    public static ADetailSongFragment newInstance() {
        ADetailSongFragment fragment = new ADetailSongFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
        }

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(getContentView(),container,false);
        Bundle arguments = getArguments();
        if (arguments != null) {
            musicList = (List<Music>) arguments.getSerializable("songs");
        }
        mRecyclerView = view.findViewById(R.id.rv_detail_song);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this.context));
        mRecyclerView.setAdapter(new RecycleAdapter(musicList));

        System.out.println("接受数据");
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }



    protected int getContentView() {
        return R.layout.fragment_adetail_song;
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }


    class RecycleAdapter extends RecyclerView.Adapter<RecycleAdapter.MyViewHolder>{
        private final List<Music> datalist;
        public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
            public TextView textView,textView1;
            public ImageView imageView;
            public MyViewHolder(View v){
                super(v);
                textView = v.findViewById(R.id.item_name);
                textView1 = v.findViewById(R.id.item_singer);
                imageView = v.findViewById(R.id.iv);
            }

            @Override
            public void onClick(View v) {
                System.out.println("点击当前歌手专辑");
            }
        }
        // 适配器的构造函数
        public RecycleAdapter(List<Music> musicList) {
            datalist = musicList;
        }

        // 创建新的视图（由布局管理器调用）
        @Override
        public RecycleAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType){
            // 创建一个新视图
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_layout, parent, false);
            MyViewHolder vh = new MyViewHolder(v);
            return vh;
        }

        // 替换视图的内容（由布局管理器调用）
        @Override
        public void onBindViewHolder(MyViewHolder holder, int position) {
            // - 获取元素数据
            // - 替换视图内容
            holder.textView.setText(datalist.get(position).getName());
            holder.textView1.setText(datalist.get(position).getArtist());
            String filename = datalist.get(position).getName() + ".jpg";
            AssetManager am = getActivity().getAssets();
            InputStream is = null;
            Bitmap bitmap = null;
            try {
                is = am.open(filename);
                bitmap = BitmapFactory.decodeStream(is);
                is.close();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            holder.imageView.setImageBitmap(bitmap);

        }

        // 返回数据集的大小（由布局管理器调用）
        @Override
        public int getItemCount() {
            return datalist.size();
        }

    }


}

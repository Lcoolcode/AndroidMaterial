package com.example.mediaplayer.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mediaplayer.R;

public class RecentPlayAdapter extends RecyclerView.Adapter<RecentPlayAdapter.ViewHolder> {

        private String[] album_name = {"莱美杠铃音乐", "周深歌单", "方大同歌单", "黄子弘凡歌单", "石凯歌单",};
        private int[] pic = {R.drawable.music1, R.drawable.music2, R.drawable.music3, R.drawable.ic_launcher_background,
            R.drawable.ic_launcher_background, R.drawable.ic_launcher_background};


    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView album_name;
        ImageView album_pic;

        public ViewHolder(View view) {
            super(view);
            album_name = (TextView) view.findViewById(R.id.album_name);
            album_pic = (ImageView) view.findViewById(R.id.album_pic);
        }
    }

        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int i){
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recent_play_list,parent,false);
            ViewHolder holder=new ViewHolder(view);
            holder.album_name=view.findViewById(R.id.album_name);
            holder.album_pic=view.findViewById(R.id.album_pic);
            view.setTag(holder);
            return holder;
        }

        public void onBindViewHolder(@NonNull ViewHolder holder, int position){
            holder.album_name.setText(album_name[position]);
            holder.album_pic.setBackgroundResource(pic[position]);

        }
        public int getItemCount(){
            return album_name.length;
        }

}



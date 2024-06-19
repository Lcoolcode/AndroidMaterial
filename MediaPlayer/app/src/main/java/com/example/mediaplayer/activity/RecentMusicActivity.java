package com.example.mediaplayer.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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

import java.util.List;
/**
 * Create author: 李欣洁
 * Last version: 2024/5/29
 * Description: RecentMusicActivity
 */
public class RecentMusicActivity extends AppCompatActivity {
    private MusicViewModel viewModel;
    AppDatabase db;
    MusicDao dao;
    private ActionBar actionBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Context context = this;
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recent_music);
        actionBar = getSupportActionBar();
        assert actionBar != null;
        actionBar.setTitle("最近播放");
//        actionBar.setDisplayHomeAsUpEnabled(true);
//        actionBar.show();

        // 初始化ViewModel
        db = Room.databaseBuilder(getApplicationContext(),AppDatabase.class, "MusicPlayer.db").addMigrations(AppDatabase.MIGRATION_1_2).build();
        dao = db.musicDao();
        viewModel = new ViewModelProvider(this,new MusicViewModelFactory(dao)).get(MusicViewModel.class);

        // 观察LiveData对象
        viewModel.getRecentSongs().observe(this, new Observer<List<Music>>() {
            @Override
            public void onChanged(List<Music> musicList) {
                // 更新UI，例如更新一个RecyclerView的适配器
                RecyclerView recyclerView = findViewById(R.id.recyclerView);
                MusicAdapter adapter = new MusicAdapter(context,musicList);
                recyclerView.setAdapter(adapter);
                recyclerView.setLayoutManager(new LinearLayoutManager(RecentMusicActivity.this));
            }
        });
    }

    public static class MusicViewModel extends ViewModel {
        private final LiveData<List<Music>> recentSongs;

        public MusicViewModel(MusicDao dao) {
            recentSongs = dao.getRecentSongs();
        }

        public LiveData<List<Music>> getRecentSongs() {
            return recentSongs;
        }
    }

    public class MusicViewModelFactory implements ViewModelProvider.Factory {
        private final MusicDao musicDao;

        public MusicViewModelFactory(MusicDao musicDao) {
            this.musicDao = musicDao;
        }

        @NonNull
        @Override
        public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
            if (modelClass.isAssignableFrom(MusicViewModel.class)) {
                return (T) new MusicViewModel(musicDao);
            }
            throw new IllegalArgumentException("Unknown ViewModel class");
        }
    }

   class MusicAdapter extends RecyclerView.Adapter<MusicAdapter.ViewHolder> {
        private List<Music> musicList;
        private LayoutInflater mInflater;

        // 数据传递到适配器
        MusicAdapter(Context context, List<Music> data) {
            this.mInflater = LayoutInflater.from(context);
            this.musicList = data;
        }

        // 为每个item创建ViewHolder
        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = mInflater.inflate(R.layout.recyclerview_item, parent, false);
            return new ViewHolder(view);
        }

        // 绑定数据到ViewHolder
        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            Music music = musicList.get(position);
            holder.myTextView.setText(music.getName());
            holder.tvMusicDescription.setText(music.getArtist());

        }

        // 总item数
        @Override
        public int getItemCount() {
            return musicList.size();
        }

        // 存储和复用Views的类
        public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
            TextView myTextView;
            TextView tvMusicDescription;

            ViewHolder(View itemView) {
                super(itemView);
                myTextView = itemView.findViewById(R.id.tvMusicName);
                tvMusicDescription = itemView.findViewById(R.id.tvMusicDescription);
                itemView.setOnClickListener(this);
            }

            @Override
            public void onClick(View view) {
                int position = getAdapterPosition();
                Intent intent=new Intent(RecentMusicActivity.this, MusicActivity.class);
//                System.out.println("点击获取到的position" + position);
                //将歌曲名和歌曲的下标存入Intent对象
                intent.putExtra("name",musicList.get(position).getName());
                intent.putExtra("file",musicList.get(position).getPath());
                intent.putExtra("position",position);
//                //开始跳转
                startActivity(intent);
            }
        }
        // 便于获取点击事件

    }

}

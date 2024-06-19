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
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.room.Room;
import androidx.viewpager.widget.ViewPager;

import com.example.mediaplayer.R;
import com.example.mediaplayer.dao.MusicDao;
import com.example.mediaplayer.db.AppDatabase;
import com.example.mediaplayer.db.MyDBHelper;
import com.example.mediaplayer.entity.Music;
import com.example.mediaplayer.entity.Music;
import com.example.mediaplayer.fragment.CollectAlbumsFragment;
import com.example.mediaplayer.fragment.CollectSingersFragment;
import com.example.mediaplayer.fragment.CollectSongsFragment;
import com.google.android.material.tabs.TabLayout;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class CollectMusicActivity extends AppCompatActivity {
    private MyDBHelper myDBHelper;
    // 单曲列表、歌手列表
    private List<Music> songList,singerSongList,albumList;
    private ViewPager viewPager;
    private TabLayout tabLayout;
    private List<String> titles = new ArrayList<>();//放标题
    private List<Fragment> fragments = new ArrayList<>();//放fragment
    private MusicViewModel viewModel;
    private List<Music> collect_lists,collect_album_lists;
    private MusicViewModel musicViewModel;
    AppDatabase db;
    MusicDao dao;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Context context = this;
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recent_music);
        //自带标题栏
        ActionBar actionBar=getSupportActionBar();
        actionBar.setTitle("我的收藏");
        // 初始化ViewModel
        db = Room.databaseBuilder(getApplicationContext(),AppDatabase.class, "MusicPlayer.db").addMigrations(AppDatabase.MIGRATION_1_2).build();
        dao = db.musicDao();
        viewModel = new ViewModelProvider(this,new MusicViewModelFactory(dao)).get(MusicViewModel.class);

        // 观察LiveData对象
        viewModel.getFavoriteSongs().observe(this, new Observer<List<Music>>() {
            @Override
            public void onChanged(List<Music> musicList) {
                collect_lists = musicList;
                // 更新UI，例如更新一个RecyclerView的适配器
                RecyclerView recyclerView = findViewById(R.id.recyclerView);
                MusicAdapter adapter = new MusicAdapter(context,musicList);
                recyclerView.setAdapter(adapter);
                recyclerView.setLayoutManager(new LinearLayoutManager(CollectMusicActivity.this));
            }
        });
    }


    public static class MusicViewModel extends ViewModel {
        private final LiveData<List<Music>> recentSongs;
        private final  LiveData<List<Music>> favoriteSongs;
        private final  LiveData<List<Music>> favoriteAlbums;

        public MusicViewModel(MusicDao dao) {
            recentSongs = dao.getRecentSongs();
            favoriteSongs = dao.getFavoriteSongs();
            favoriteAlbums = dao.getFavoriteAlbums();
        }

        public LiveData<List<Music>> getRecentSongs() {
            return recentSongs;
        }
        public LiveData<List<Music>> getFavoriteSongs() {
            return favoriteSongs;

        }
        public LiveData<List<Music>> getFavoriteAlbums() {
            return favoriteAlbums;

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
            if (modelClass.isAssignableFrom(CollectMusicActivity.MusicViewModel.class)) {
                //noinspection unchecked
                return (T) new CollectMusicActivity.MusicViewModel(musicDao);
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
        public void onBindViewHolder(MusicAdapter.ViewHolder holder, int position) {
            Music music = musicList.get(position);
            holder.myTextView.setText(music.getName());
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

            ViewHolder(View itemView) {
                super(itemView);
                myTextView = itemView.findViewById(R.id.tvMusicName);
                itemView.setOnClickListener(this);
            }

            @Override
            public void onClick(View view) {
                int position = getAdapterPosition();
                Intent intent=new Intent(CollectMusicActivity.this, MusicActivity.class);
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
    //自定义适配器
//    class ViewPagerAdapter extends FragmentStatePagerAdapter {
//        public ViewPagerAdapter(FragmentManager fm) {
//            super(fm);
//        }
//
//        @NonNull
//        @Override
//        public Fragment getItem(int position) {
//            if(position == 0){
//                Bundle bundle = new Bundle();
//                bundle.putSerializable("song-key",(Serializable)collect_lists);
//                fragments.get(position).setArguments(bundle);
//            }else if(position == 1){
//                Bundle bundle = new Bundle();
//                bundle.putSerializable("album-key",(Serializable)collect_album_lists);
//                fragments.get(position).setArguments(bundle);
//            }
//            // 更新UI，例如更新一个RecyclerView的适配器
//            return fragments.get(position);
//        }
//
//        @Override
//        public int getCount() {
//            return fragments.size();
//        }
//
//        @Nullable
//        @Override
//        public CharSequence getPageTitle(int position) {
//            return titles.get(position);
//        }
//    }
}

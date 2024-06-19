package com.example.mediaplayer.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.example.mediaplayer.entity.Music;

import java.util.List;

@Dao
public interface  MusicDao {
    // 新增查询方法按照专辑查询歌曲
    @Query("SELECT * FROM songs WHERE album = :albumName")
    LiveData<List<Music>> getSongsByAlbum(String albumName);

    @Query("SELECT * FROM songs ORDER BY lastPlayed DESC LIMIT 10")
    LiveData<List<Music>> getRecentSongs();


    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertSong(Music song);

    @Query("UPDATE songs SET isFavorite = :isFavorite WHERE name = :name")
    void updateFavorite(String name, boolean isFavorite);

    // 新增查询方法获取收藏的歌曲列表
    @Query("SELECT * FROM songs WHERE isFavorite = 1 ORDER BY name")
    LiveData<List<Music>> getFavoriteSongs();

    // 新增查询方法检查单个歌曲是否为喜爱
    @Query("SELECT isFavorite FROM songs WHERE name = :name LIMIT 1")
    LiveData<Boolean> isSongFavorite(String name);

    // 新增方法更新专辑的喜爱状态
    @Query("UPDATE songs SET isAlbumFavorite = :isAlbumFavorite WHERE album = :albumName")
    void updateAlbumFavorite(String albumName, boolean isAlbumFavorite);

    // 新增查询方法查询某个专辑的喜爱状态
    @Query("SELECT EXISTS(SELECT 1 FROM songs WHERE album = :albumName AND isAlbumFavorite = 1 LIMIT 1)")
    LiveData<Boolean> isAlbumFavorite(String albumName);

    // 新增查询方法获取喜爱的专辑列表
    @Query("SELECT *  FROM songs WHERE isAlbumFavorite = 1 ORDER BY album")
    LiveData<List<Music>> getFavoriteAlbums();
}

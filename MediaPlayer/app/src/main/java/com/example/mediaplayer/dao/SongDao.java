package com.example.mediaplayer.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.example.mediaplayer.entity.Song;

import java.util.List;

@Dao
public interface SongDao {
    @Query("SELECT * FROM songs ORDER BY lastPlayed DESC LIMIT 10")
    LiveData<List<Song>> getRecentSongs();

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertSong(Song song);

    @Query("UPDATE songs SET isFavorite = :isFavorite WHERE id = :id")
    void updateFavorite(int id, boolean isFavorite);
}

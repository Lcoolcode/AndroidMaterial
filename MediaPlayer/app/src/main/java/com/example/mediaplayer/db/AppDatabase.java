package com.example.mediaplayer.db;

import androidx.room.Database;
import androidx.room.RoomDatabase;
import androidx.room.migration.Migration;
import androidx.sqlite.db.SupportSQLiteDatabase;

import com.example.mediaplayer.dao.MusicDao;
import com.example.mediaplayer.dao.SongDao;
import com.example.mediaplayer.entity.Music;


@Database(entities = {Music.class}, version = 2)
public abstract class AppDatabase extends RoomDatabase {
    public abstract MusicDao musicDao();
    // 创建迁移对象，从版本1迁移到版本2
    public static final Migration MIGRATION_1_2 = new Migration(1, 2) {
        @Override
        public void migrate(SupportSQLiteDatabase database) {
            // 迁移逻辑，根据您的具体更改进行调整
            // 例如，如果您添加了新列：
            database.execSQL("ALTER TABLE songs ADD COLUMN isAlbumFavorite INTEGER DEFAULT 0 NOT NULL");
        }
    };
}




package com.example.mediaplayer.db;

import android.annotation.SuppressLint;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.mediaplayer.entity.Music;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;


public class MyDBHelper extends SQLiteOpenHelper {
    private final int MODE = Context.MODE_PRIVATE; // 访问模式
    private static final String TAG = "MyDBHelper";
    //声明数据库帮助器的实例
    public static MyDBHelper userDBHelper = null;
    //声明数据库的名称
    public static final String DB_NAME = "MusicPlayer1.db";
    //声明表的名称
    public static final String TABLE_NAME = "music";
    //声明数据库的版本号
    public static int DB_VERSION = 1;
    //数据库实例
    private SQLiteDatabase myDatabase;
    private Context context;

    //定义类的方法
    public MyDBHelper(@Nullable Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }
    public MyDBHelper(@Nullable Context context,int version){
        super(context, DB_NAME, null, version);
    }

    //利用单例模式获取数据库帮助器的实例
    public static MyDBHelper getInstance(Context context, int version) {
        if (userDBHelper == null && version > 0) {
            userDBHelper = new MyDBHelper(context, version);
        } else if (userDBHelper == null) {
            userDBHelper = new MyDBHelper(context);
        }
        return userDBHelper;
    }

    //打开数据库的写连接
    public SQLiteDatabase openWriteLink() {
        if (myDatabase == null || !myDatabase.isOpen()) {
            myDatabase = userDBHelper.getWritableDatabase();
        }
        return myDatabase;
    }

    //getWritableDatabase()与getReadableDatabase() 这两个方法都可以获取到数据库的连接
    //正常情况下没有区别，当手机存储空间不够了
    //getReadableDatabase()就不能进行插入操作了，执行插入没有效果
    //getWritableDatabase()：也不能进行插入操作，如果执行插入数据的操作，则会抛异常。对于现在来说不会出现这种情况，用哪种方式都可以

    //打开数据库的读连接
    public SQLiteDatabase openReadLink() {
        if (myDatabase == null || !myDatabase.isOpen()) {
            myDatabase = userDBHelper.getReadableDatabase();
            System.out.println("打开数据库读连接");
        }
        return myDatabase;
    }

    //关闭数据库的读连接
    public void closeLink() {
        if (myDatabase != null && myDatabase.isOpen()) {
            myDatabase.close();
            myDatabase = null;
            System.out.println("打开数据库写连接");
        }
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    @SuppressLint("Range")
    public List<Music> queryInfo(String song){
        String sql = String.format("select * from %s where %s;", TABLE_NAME, song);
        List<Music> songList = new ArrayList<>();
        Cursor cursor = myDatabase.rawQuery(sql, null);
        while(cursor.moveToNext()){
            Music mp3 = new Music();
            mp3.setName(cursor.getString(cursor.getColumnIndex("song")));
            mp3.setArtist(cursor.getString(cursor.getColumnIndex("singer")));
            mp3.setAlbum(cursor.getString(cursor.getColumnIndex("album")));
            songList.add(mp3);
        }
        cursor.close();
        return songList;
    }

//    @SuppressLint("Range")
//    public List<Music> queryAlbumInfo(String song){
//        String sql = "select singer,gender,count(*) as number from music group by album;"
//        List<Music> songList = new ArrayList<>();
//        Cursor cursor = myDatabase.rawQuery(sql, null);
//        while(cursor.moveToNext()){
//            Music mp3 = new Music();
//            mp3.setSong(cursor.getString(cursor.getColumnIndex("song")));
//            mp3.setSinger(cursor.getString(cursor.getColumnIndex("singer")));
//            mp3.setAlbum(cursor.getString(cursor.getColumnIndex("album")));
//            mp3.setGender(cursor.getString(cursor.getColumnIndex("gender")));
//            songList.add(mp3);
//        }
//        cursor.close();
//        return songList;
//    }


    @SuppressLint("Range")
    public List<Music> querySingerInfo(){
        String sql = "select singer,count(*) as number from music group by singer;";
        List<Music> singerList = new ArrayList<>();
        Cursor cursor = myDatabase.rawQuery(sql, null);
        while(cursor.moveToNext()){
            Music mp3 = new Music();
            mp3.setArtist(cursor.getString(cursor.getColumnIndex("singer")));
            mp3.setId(cursor.getInt(cursor.getColumnIndex("number")));
            singerList.add(mp3);
        }
        cursor.close();
        return singerList;
    }

    @SuppressLint("Range")
    public List<Music> querySingerSongInfo(String singer){
        String sql = "select * from music where singer='" + singer + "';";
        List<Music> singerSongList = new ArrayList<>();
        Cursor cursor = myDatabase.rawQuery(sql, null);
        while(cursor.moveToNext()){
            Music mp3 = new Music();
            mp3.setArtist(cursor.getString(cursor.getColumnIndex("singer")));
            mp3.setName(cursor.getString(cursor.getColumnIndex("song")));
            mp3.setAlbum(cursor.getString(cursor.getColumnIndex("album")));
            singerSongList.add(mp3);
        }
        cursor.close();
        return singerSongList;
    }
}


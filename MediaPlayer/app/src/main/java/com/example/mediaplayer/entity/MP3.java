package com.example.mediaplayer.entity;

import android.content.Context;
import android.media.MediaPlayer;
import android.os.Environment;
import android.os.Parcel;
import android.os.Parcelable;
import android.widget.Toast;

import androidx.annotation.NonNull;

import java.io.IOException;
import java.io.Serializable;

public class MP3 implements Serializable {
    public MP3() {

    }

    public String getSinger() {
        return singer;
    }

    public void setSinger(String singer) {
        this.singer = singer;
    }

    public String getSong() {
        return song;
    }

    public void setSong(String song) {
        this.song = song;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getAlbum() {
        return album;
    }

    public void setAlbum(String album) {
        this.album = album;
    }

    public Integer getSongNumber() {
        return songNumber;
    }

    public void setSongNumber(Integer songNumber) {
        this.songNumber = songNumber;
    }

    private String singer;//歌手名
    private String song;//音乐名
    private String gender;//歌手性别
    private String album;//歌曲所属专辑
    private Integer songNumber;//单个歌手歌曲数量

    public MP3(String singer, String song, String gender, String album,Integer songNumber) {
        this.singer = singer;
        this.song = song;
        this.gender = gender;
        this.album = album;
        this.songNumber = songNumber;

    }
}
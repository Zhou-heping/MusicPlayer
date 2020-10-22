package com.example.musicplayer.data;

import org.litepal.crud.LitePalSupport;

public class SongList extends LitePalSupport {
    private String songListId; // 歌单id
    private String name;       // 歌单名
    private long imageId;      // 歌单图片
    private String info;       // 歌单信息

    public String getSongListId() {
        return songListId;
    }
    public void setSongListId(String songListId) {
        this.songListId = songListId;
    }
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public long getImageId() {
        return imageId;
    }
    public void setImageId(long imageId) {
        this.imageId = imageId;
    }
    public String getInfo() {
        return info;
    }
    public void setInfo(String info) {
        this.info = info;
    }
}
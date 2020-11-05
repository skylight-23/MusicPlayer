package com.example.musicplayer;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.provider.MediaStore;

import java.util.ArrayList;

/**
 * 从内部存储中读取下载好的后缀名为.mp3的音乐文件，返回值为Music集合
 */
public class MusicList {

    public static ArrayList<Music> getMusicData(Context context){
        ArrayList<Music> musicList = new ArrayList<Music>();
        ContentResolver contentResolver = context.getContentResolver();
        if(contentResolver != null){
            Cursor cursor = contentResolver.query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, null, null, null,
                    MediaStore.Audio.Media.DEFAULT_SORT_ORDER);
            if(cursor == null){
                return null;
            }
            if(cursor.moveToFirst()) {
                do {
                    Music music = new Music();
                    String artist = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.ARTIST));
                    String name = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.DISPLAY_NAME));
                    if ("<unknown>".equals(artist)) {
                        String[] split = name.split("-");
                        artist =  split[0];
                    }
                    int time = cursor.getInt(cursor.getColumnIndex(MediaStore.Audio.Media.DURATION));
                    String url = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.DATA));
                    String isMp3 = name.substring(name.length() - 3, name.length());
                    if (isMp3.equals("mp3")) {
                        music.setArtist(artist);
                        music.setTime(time);
                        music.setUrl(url);
                        music.setName(name.substring(0,name.length()-4));
                        musicList.add(music);
                    }
                } while (cursor.moveToNext());
            }
        }
        return musicList;
    }
}


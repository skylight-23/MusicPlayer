package com.example.musicplayer;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.provider.MediaStore;

import java.util.ArrayList;

//从内部存储中读取下载好的后缀名为.mp3的音乐文件，返回值为Music集合

public class MusicList {

    public static ArrayList<Music> getMusicData(Context context){
        ArrayList<Music> musicList = new ArrayList<Music>();
        ContentResolver contentResolver = context.getContentResolver();
        //判断获取到的ContentResolver是否为空，如果不为空，调用contentResolver.query方法查询本地的音乐文件，
        // 返回一个Cursor对象。如果Cursor为空，说明没有数据，直接返回null；
        if(contentResolver != null){
            Cursor cursor = contentResolver.query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, null, null, null,
                    MediaStore.Audio.Media.DEFAULT_SORT_ORDER);
            if(cursor == null){
                return null;
            }
            if(cursor.moveToFirst()) {//查询第一条数据，如果不为空，就循环调用moveToNext()方法查询下一条数据直到没有数据为止；
                do {
                    Music music = new Music(); //在查询中，通过cursor.getString获取到相应的数据，该数据就对应等于Music中的成员变量；
                    // 将查询到的数据赋值到Music对象，完成查询数据的存储，并把该Music对象添加到Music集合，这样就获取到了本地音乐数据集合。
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


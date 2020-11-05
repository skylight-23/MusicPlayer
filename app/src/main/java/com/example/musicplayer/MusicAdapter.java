package com.example.musicplayer;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;

public class MusicAdapter extends BaseAdapter {
    private Context mContext;
    private List<Music> mMusicList;

    public MusicAdapter(Context context,List<Music> musicList){
        mContext = context;
        mMusicList = musicList;
    }

    @Override
    public int getCount() {
        return mMusicList.size();
    }

    @Override
    public Object getItem(int position) {
        return mMusicList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        Music music = mMusicList.get(position);
        if (convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(R.layout.item, parent,false);
            viewHolder = new ViewHolder();
            viewHolder.music_item_name = convertView.findViewById(R.id.music_item_name);
            viewHolder.music_item_time = convertView.findViewById(R.id.music_item_time);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        viewHolder.music_item_name.setText(music.getName());
        viewHolder.music_item_time.setText(formatTime(music.getTime()));
        return convertView;
    }

    class ViewHolder {
        TextView music_item_name;
        TextView music_item_time;
    }
    //将时间转换为××：××格式
    private String formatTime(int time) {
        int ms2s = (time / 1000);
        int minute = ms2s / 60;
        int second = ms2s % 60;
        return String.format("%02d:%02d", minute, second);
    }
}


package com.example.musicplayer;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.content.res.AssetFileDescriptor;
import android.content.res.AssetManager;
import android.media.MediaParser;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private ArrayList<Music> arrayList;
    private MusicAdapter mMusicAdapter;
    private ListView mListView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        requestPermission();

    }
    private void initView() {
        arrayList = MusicList.getMusicData(this);
        mListView = findViewById(R.id.listView_main);
        mMusicAdapter = new MusicAdapter(this, arrayList);
        mListView.setAdapter(mMusicAdapter);
        //实现页面跳转，将当前点击的音乐条目传过去给音乐播放页面，这样它才知道播放哪条音乐
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
               Intent intent = new Intent(MainActivity.this,DetailActivity.class);
               Bundle bundle =  new Bundle();
               bundle.putInt("position",position);
               intent.putExtras(bundle);
               startActivity(intent);
            }
        });
    }

    //定义方法requestPermission()
    private void requestPermission() {
        //创建允许权限的集合
        List<String> permissionList = new ArrayList<String>();
        //如果该权限没同意，就加入到允许权限的集合
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            permissionList.add(Manifest.permission.READ_EXTERNAL_STORAGE);
        }
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            permissionList.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
        }
        //如果允许权限不为空，说明有权限还没申请，就一起申请
        if (!permissionList.isEmpty()) {
            ActivityCompat.requestPermissions(this, permissionList.toArray(new String[permissionList.size()]), 1);
        }
        //权限申请完成之后，才去初始化视图
        else {
            //适配器的适配工作
            initView();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case 1:
                if (grantResults.length > 0) {
                    for (int i = 0; i < grantResults.length; i++) {
                        int grantResult = grantResults[i];
                        if (grantResult == PackageManager.PERMISSION_DENIED) {
                            String s = permissions[i];
                            Toast.makeText(this, s + "权限被拒绝了", Toast.LENGTH_SHORT).show();
                        } else {
                            initView();
                        }
                    }
                }
        }
    }






}

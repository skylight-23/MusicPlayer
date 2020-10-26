package com.example.musicplayer;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.content.res.AssetFileDescriptor;
import android.content.res.AssetManager;
import android.media.MediaParser;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.text.SimpleDateFormat;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{
    private MediaPlayer mediaPlayer = new MediaPlayer();
    private Handler myHandler = new Handler();
    private SeekBar seekBar;
    private TextView timeTextView;
    private SimpleDateFormat time = new SimpleDateFormat("m:ss");
    private int i = 0;
    File file = Environment.getExternalStorageDirectory();//SD卡根目录
    //歌曲路径
    private String[] musicPath = new String[]{
            file + "TestMusic/Jony J - 顽家.mp3",
            file + "TestMusic/沙一汀EL - 所以你睡了没.mp3",
            file + "TestMusic/法老 - 百变酒精.mp3",
            file + "TestMusic/落日飞车 - 我是一只鱼.mp3"
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Button play = findViewById(R.id.play);//播放
        Button pause = findViewById(R.id.pause);//暂停
        Button nextMusic = findViewById(R.id.next);//下一首
        Button preMusic = findViewById(R.id.previous);//上一首

        play.setOnClickListener(this);
        pause.setOnClickListener(this);
        nextMusic.setOnClickListener(this);
        preMusic.setOnClickListener(this);

        seekBar = findViewById(R.id.seekbar);
        timeTextView = findViewById(R.id.text1);


        //运行时权限处理，动态申请WRITE_EXTERNAL_STORAGE权限
        //PackageManager.PERMISSION_GRANTED 表示有权限， PackageManager.PERMISSION_DENIED 表示无权限
        if(ContextCompat.checkSelfPermission(MainActivity.this,Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(MainActivity.this,new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},1);
            //这里会调用后面的onRequestPermissionResult
        }else{
            initMediaPlayer(0);
        }

        myHandler.post(updateUI);
    }

    private void initMediaPlayer(int musicIndex) {
        try{
            mediaPlayer.setDataSource(musicPath[musicIndex]);//指定音乐文件的路径
            mediaPlayer.prepare();
        }catch (Exception e){
            e.printStackTrace();
        }

                //这个要放在指定音频文件路径之后
                seekBar.setMax(mediaPlayer.getDuration());
                //拖动进度条时应该发生的事情
                seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                    @Override
                    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                        //如果不判断是否来自用户操作进度条，会不断执行下面语句块里面的逻辑
                        if(fromUser){
                            mediaPlayer.seekTo(seekBar.getProgress());
                        }
                    }

                    @Override
                    public void onStartTrackingTouch(SeekBar seekBar) {

                    }

                    @Override
                    public void onStopTrackingTouch(SeekBar seekBar) {

                    }
                });
            }

    //拒绝权限获取则直接关闭程序
    public void onRequestPermissionsResult(int requestCode,String[] permissions,int[] grantResults){
        switch (requestCode){
            case 1:
            {
                if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                    initMediaPlayer(0);
                }else{
                    Toast.makeText(this,"拒绝权限将无法使用程序",Toast.LENGTH_SHORT).show();
                    finish();
                }
                break;
            }
            default:
                break;
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.play:
                if(!mediaPlayer.isPlaying()){
                    mediaPlayer.start();//开始播放
                }
                break;
            case R.id.pause:
                if(mediaPlayer.isPlaying()){
                    mediaPlayer.pause();// 暂停播放
                }
                break;
            case R.id.stop:
                if(mediaPlayer.isPlaying()){
                    mediaPlayer.reset();// 停止播放
                }
                break;
            case R.id.next:
                playNextMusic();
                break;
            case R.id.previous:
                playPreMusic();
                break;
            default:
                break;
        }
    }

    private void playNextMusic(){
        if(mediaPlayer != null && i < 4 && i >=0){
            mediaPlayer.reset();//没有reset会报IllegalStateException
            switch (i){
                case 0: case 1: case 2:
                    initMediaPlayer(i+1);
                    i = i + 1;
                case 3:
                    initMediaPlayer(0);
            }
            if(!mediaPlayer.isPlaying()){
                mediaPlayer.start();
            }
        }
    }

    private void playPreMusic(){
        if(mediaPlayer != null && i < 4 && i >=0){
            mediaPlayer.reset();//没有reset会报IllegalStateException
            switch (i){
                case 1: case 2: case 3:
                    initMediaPlayer(i-1);
                    i = i - 1;
                case 0:
                    initMediaPlayer(3);
            }
            if(!mediaPlayer.isPlaying()){
                mediaPlayer.start();
            }
        }
    }
    //更新UI
    private Runnable updateUI = new Runnable() {
        @Override
        public void run() {
            //获取歌曲进度并在进度条上展现
            seekBar.setProgress(mediaPlayer.getCurrentPosition());
            //获取播放位置
            timeTextView.setText(time.format(mediaPlayer.getCurrentPosition()) + "s");
            myHandler.postDelayed(updateUI,1000);
        }

    };

    protected void onDestroy(){
        super.onDestroy();
        //handler发送是定时1000s发送的，如果不关闭，MediaPlayer release了还在getCurrentPosition就会报IllegalStateException错误
        myHandler.removeCallbacks(updateUI);
        if(mediaPlayer != null){
            mediaPlayer.stop();
            mediaPlayer.release();
        }
    }
}

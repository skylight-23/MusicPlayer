package com.example.musicplayer;

import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import java.io.IOException;
import java.util.ArrayList;

public class DetailActivity extends AppCompatActivity implements View.OnClickListener {
    private Button btn_pre, btn_play, btn_next,btn_return;
    private TextView tv_cur_time, tv_total_time;
    private SeekBar seekBar;

    //seekBar是否被拖动
    private boolean isSeekBarChanging;
    private MusicService musicService;
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if (msg.what == 0x01) {
                tv_cur_time.setText("00:00");
                tv_cur_time.setText(formatTime(musicService.getCurrent()));
                tv_total_time.setText(formatTime(musicService.getTime()));
                seekBar.setProgress(musicService.getCurrent());
                seekBar.setMax(musicService.getTime());
                seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                    @Override
                    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

                    }

                    @Override
                    public void onStartTrackingTouch(SeekBar seekBar) {
                        isSeekBarChanging = true;
                    }

                    @Override
                    public void onStopTrackingTouch(SeekBar seekBar) {
                        isSeekBarChanging = false;
                        musicService.seekTo(seekBar.getProgress());
                    }
                });
                handler.sendEmptyMessage(0x01);
            }
        }
    };

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unbindService(conn);
        handler.removeCallbacksAndMessages(null);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        initView();
        Intent intent = new Intent(this, MusicService.class);
        //获取MainActivity传过来的数据
        Bundle bundle = getIntent().getExtras();
        //再把这些包装好的数据重新装入Intent，发送给Service
        intent.putExtras(bundle);
        startService(intent);
        bindService(intent, conn, BIND_AUTO_CREATE);
    }

    ServiceConnection conn = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            musicService = ((MusicService.MusicBinder) service).getService();
            handler.sendEmptyMessage(0x01);
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {

        }
    };

    /**
     * 按钮点击事件
     */
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_pre:
                musicService.next(-1);
                break;
            case R.id.btn_play:
                musicService.play();
                break;
            case R.id.btn_next:
                musicService.next(1);
                break;
            case R.id.btn_return:
                onBackPressed();
                break;
        }
        updateBtnPlayOrPause();
    }

    private void updateBtnPlayOrPause() {
        if (MusicService.isPlaying) {
            btn_play.setText("暂停");
        } else {
            btn_play.setText("播放");
        }
    }

    private void initView() {
        btn_pre = findViewById(R.id.btn_pre);
        btn_play = findViewById(R.id.btn_play);
        btn_next = findViewById(R.id.btn_next);
        btn_return = findViewById(R.id.btn_return);
        tv_cur_time = findViewById(R.id.tv_cur_time);
        tv_total_time = findViewById(R.id.tv_total_time);
        seekBar = findViewById(R.id.seekBar);
        btn_pre.setOnClickListener(this);
        btn_play.setOnClickListener(this);
        btn_next.setOnClickListener(this);
        btn_return.setOnClickListener(this);
    }

    private String formatTime(int time) {
        int ms2s = (time / 1000);
        int minute = ms2s / 60;
        int second = ms2s % 60;
        return String.format("%02d:%02d", minute, second);
    }
}



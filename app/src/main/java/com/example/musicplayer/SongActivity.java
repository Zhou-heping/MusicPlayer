package com.example.musicplayer;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;


import android.content.Intent;
import android.media.MediaPlayer;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.VideoView;

import com.example.musicplayer.data.Music;
import java.text.SimpleDateFormat;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

import static com.example.musicplayer.MainActivity.currentMusicList;
import static com.example.musicplayer.util.GetMusicInfo.getAlbumArt;

public class SongActivity extends AppCompatActivity implements View.OnClickListener {
    private ImageButton nextSong;       // 下一首
    private ImageButton preSong;        // 上一首
    private ImageButton start;          // 播放或暂停按钮
    private TextView songName;          // 歌曲名
    private ImageButton playMode;       // 播放方式
    private VideoView videoView;       // 播放动画
    private ImageButton loveButton;     // 喜欢
    private TextView currentTime, totalTime;    // 当前播放时长和总时长
    private SeekBar seekBar;        //进度条
    private ImageButton backSongList;   // 返回
    private MediaPlayer mediaPlayer = new MediaPlayer();
    private int currentMusicPostion;     // 当前音乐位置
    private boolean isSeekBarChanging;
    private int mode = 0;
    private static final String TAG = "SongActivity";
    private Timer timer;
    private Random random;
    private Music music;
    private SimpleDateFormat format;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_song);
        Bundle bundle = getIntent().getExtras();
        currentMusicPostion = bundle.getInt("musicPosition", 0);
        Log.d(TAG, "onCreate: "+currentMusicPostion);
        initLayout();
        setListener();
        seekBar.setOnSeekBarChangeListener(new MySeekBar());
        initMediaPlayer(currentMusicPostion);
        if (music.getLove() == 1){
            loveButton.setImageResource(R.drawable.love);
        }else {
            loveButton.setImageResource(R.drawable.unlove);
        }
    }
    private void initLayout(){
        backSongList = findViewById(R.id.back_songlist);
        seekBar = findViewById(R.id.song_progress);
        currentTime = findViewById(R.id.current_time);
        totalTime = findViewById(R.id.total_time);
        start = findViewById(R.id.start);
        nextSong = findViewById(R.id.next_song);
        preSong = findViewById(R.id.previous_song);
        songName = findViewById(R.id.song_name);
        videoView = findViewById(R.id.vidioView);
        playMode = findViewById(R.id.play_order);
        loveButton = findViewById(R.id.love);
        format = new SimpleDateFormat("mm:ss");
    }
    private void setListener(){
        backSongList.setOnClickListener(this);
        start.setOnClickListener(this);
        nextSong.setOnClickListener(this);
        preSong.setOnClickListener(this);
        playMode.setOnClickListener(this);
        loveButton.setOnClickListener(this);
    }

    // 初始化MediaPlayer
    private void initMediaPlayer(int position){
        try {
            seekBar.setProgress(0);
            mediaPlayer.reset();
            music = currentMusicList.get(position);
            songName.setText(music.getTitle());
            //albumCover.setImageBitmap(getAlbumArt((int)music.getAlbumId(), SongActivity.this));
            mediaPlayer.setDataSource(music.getPath());
            mediaPlayer.prepare();  // 进入准备状态
            mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                @Override
                public void onPrepared(MediaPlayer mp) {
                    seekBar.setMax(mediaPlayer.getDuration());
                    totalTime.setText(format.format(mediaPlayer.getDuration()) + "");
                    currentTime.setText("00:00");
                }
            });
            Thread.sleep(1000);
            startMusic();
        }catch (Exception e){
            Log.d(TAG, "initMediaPlayer: " + "error");
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.back_songlist:
                mediaPlayer.stop();
                Intent intent = new Intent(SongActivity.this, SongListActivity.class);
                startActivity(intent);
                finish();
                break;
            case R.id.start:
                if (mediaPlayer.isPlaying()){
                    pauseMusic();
                }else {
                    startMusic();
                }
                break;
            case R.id.next_song:
                setPosition();
                initMediaPlayer(currentMusicPostion);
                break;
            case R.id.previous_song:
                if (currentMusicPostion != 0) {
                    currentMusicPostion--;
                }else {
                    currentMusicPostion = currentMusicList.size()-1;
                }
                initMediaPlayer(currentMusicPostion);
                break;
            case R.id.play_order:
                if (mode == 0){
                    mode = 1;
                    playMode.setBackgroundResource(R.drawable.randomplay);
                }else {
                    mode = 0;
                    playMode.setBackgroundResource(R.drawable.orderplay);
                }
                break;
            case R.id.love:
                Log.d(TAG, "onClick: "+"clicked");
                if (music.getLove() == 0){
                    loveButton.setImageResource(R.drawable.love);
                    music.setLove(1);
                }else {
                    loveButton.setImageResource(R.drawable.unlove);
                    music.setLove(0);
                }
                currentMusicList.get(currentMusicPostion).setLove(music.getLove());
                music.save();
                break;
            default:
        }
    }

    // 进度条处理
    public class MySeekBar implements SeekBar.OnSeekBarChangeListener{
        @Override
        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
            // 播放结束时，自动播放下一首
            if (progress >= mediaPlayer.getDuration()){
                setPosition();
                initMediaPlayer(currentMusicPostion);
            }
        }
        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {
            // 滚动时停止定时器
            isSeekBarChanging = true;
        }

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {
            // 滚动结束后重新设置进度条的值
            isSeekBarChanging = false;
            mediaPlayer.seekTo(seekBar.getProgress());
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        isSeekBarChanging = true;
        if (mediaPlayer != null){
            mediaPlayer.reset();
            mediaPlayer.release();
            mediaPlayer = null;
        }
        if (timer != null){
            timer.cancel();
            timer = null;
        }
    }
    // 播放音乐
    private void startMusic(){
        mediaPlayer.start();    // 开始播放
        start.setImageResource(R.drawable.start);
        mediaPlayer.seekTo(seekBar.getProgress());
        // 监听播放时回调函数
        timer = new Timer();
        timer.schedule(new TimerTask() {
            Runnable updateUI = new Runnable() {
                @Override
                public void run() {
                    currentTime.setText(format.format(mediaPlayer.getCurrentPosition()) + "");
                }
            };
            @Override
            public void run() {
                if(!isSeekBarChanging && mediaPlayer.isPlaying()){
                    seekBar.setProgress(mediaPlayer.getCurrentPosition());
                    runOnUiThread(updateUI);
                }
            }
        }, 0, 50);
    }

    // 暂停音乐
    private void pauseMusic(){
        mediaPlayer.pause();    // 暂停播放
        start.setImageResource(R.drawable.pause);
    }

    // 设置下一首音乐在歌单中的位置
    private void setPosition(){
        if (mode == 0){
            if (currentMusicPostion < currentMusicList.size()-1) {
                currentMusicPostion++;
            }else {
                currentMusicPostion = 0;
            }
        }else {
            random = new Random();
            currentMusicPostion = random.nextInt(currentMusicList.size());
        }
    }
}
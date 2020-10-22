package com.example.musicplayer;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import com.example.musicplayer.data.Music;
import com.example.musicplayer.util.GetMusicInfo;
import java.util.ArrayList;
import java.util.List;
import static com.example.musicplayer.MainActivity.currentMusicList;
import static com.example.musicplayer.MainActivity.localMusic;
import static com.example.musicplayer.MainActivity.loveMusic;

public class SongListActivity extends AppCompatActivity implements View.OnClickListener {
    private RecyclerView recyclerView;  // 音乐列表
    private ImageButton back;
    private MusicItemAdapter musicItemAdapter;      // 适配器
    private List<Music> musicList = new ArrayList<>();  // 数据
    private static final String TAG = "SongListActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_song_list);
        Intent intent = getIntent();
        getMusicList(intent.getStringExtra("songListName"));
        Log.d(TAG, "onCreate: "+"songlist");
        initLayout();
        setListener();
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        musicItemAdapter = new MusicItemAdapter(musicList);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(musicItemAdapter);
    }

    // 监听器
    private void setListener(){
        back.setOnClickListener(this);
    }
    // 寻找各个控件
    private void initLayout() {
        back = findViewById(R.id.back_home);
        recyclerView = findViewById(R.id.music_list_view);
    }

    // 从数据库中查询歌单中的歌曲
    private void getMusicList(String songListName){
        currentMusicList.clear();
        GetMusicInfo.getLocalMusic(SongListActivity.this);
        if (songListName.equals("本地音乐")) {
            musicList = localMusic;
        }else {
            musicList = loveMusic;
        }
        currentMusicList = musicList;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.back_home:
                Intent intent = new Intent(SongListActivity.this, MainActivity.class);
                startActivity(intent);
                finish();
                break;
            default:
        }
    }
}
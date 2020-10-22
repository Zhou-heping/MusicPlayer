package com.example.musicplayer;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.example.musicplayer.data.Music;
import com.example.musicplayer.data.SongList;
import org.litepal.LitePal;
import java.util.ArrayList;
import java.util.List;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import static com.example.musicplayer.MainActivity.localMusic;
import static com.example.musicplayer.MainActivity.loveMusic;

public class MyMusicFragment extends Fragment {

    private Context context = getActivity();
    private RecyclerView songListView;  // 歌单列表
    public static List<SongList> songLists = new ArrayList<>();
    public static SongListAdapter songListAdapter;
    private static final String TAG = "MyMusicFragment";

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.my_music_fragment, container, false);
        initLayout(view);
        initSongLists();
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        songListAdapter = new SongListAdapter(songLists, getActivity());
        songListView.setLayoutManager(layoutManager);
        songListView.setAdapter(songListAdapter);
        return view;
    }

    // 寻找各个控件
    private void initLayout(View view){
        songListView = view.findViewById(R.id.songlist_view);
    }
    // 从数据库中获取歌单列表信息
    public static void initSongLists(){
        localMusic.clear();
        loveMusic.clear();
        songLists.clear();
        localMusic = LitePal.findAll(Music.class);
        for (Music music : localMusic){
            if (music.getLove() == 1){
                loveMusic.add(music);
            }
        }
        songLists = LitePal.findAll(SongList.class);
        if (loveMusic.size() != 0) {
            songLists.get(1).setImageId(loveMusic.get(0).getAlbumId());
            songLists.get(1).setInfo("共" + loveMusic.size() + "首音乐");
            songLists.get(1).save();
        }
    }
}
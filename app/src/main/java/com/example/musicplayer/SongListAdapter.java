package com.example.musicplayer;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import com.example.musicplayer.data.SongList;
import java.util.List;
import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;
import static com.example.musicplayer.util.GetMusicInfo.getAlbumArt;


public class SongListAdapter extends RecyclerView.Adapter<SongListAdapter.ViewHolder>{
    private static final String TAG = "SongListAdapter";
    private Context mContext;
    public static FragmentActivity fragmentActivity;
    public static List<SongList> mSongList;

    static class ViewHolder extends RecyclerView.ViewHolder{
        ImageView songListImage;
        TextView songListName;
        TextView songListInfo;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            this.songListImage = itemView.findViewById(R.id.songlist_image);
            this.songListName = itemView.findViewById(R.id.songlist_name);
            this.songListInfo = itemView.findViewById(R.id.songlist_info);

            // item的点击监听器
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(fragmentActivity, SongListActivity.class);
                    intent.putExtra("songListName",mSongList.get(getAdapterPosition()).getName());
                    fragmentActivity.startActivity(intent);
                }
            });
        }
    }

    public SongListAdapter(List<SongList> mSongList, FragmentActivity fragmentActivity) {
        this.mSongList = mSongList;
        this.fragmentActivity = fragmentActivity;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        if (mContext == null){
            mContext = viewGroup.getContext();
        }
        View view = LayoutInflater.from(mContext).inflate(R.layout.songlist_item,
                viewGroup, false);
        ViewHolder holder = new ViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {
        SongList songList = mSongList.get(i);
        viewHolder.songListImage.setImageBitmap(getAlbumArt((int)songList.getImageId(), fragmentActivity));
        viewHolder.songListName.setText(songList.getName());
        viewHolder.songListInfo.setText(songList.getInfo());
    }
    @Override
    public int getItemCount() {
        return mSongList.size();
    }
}
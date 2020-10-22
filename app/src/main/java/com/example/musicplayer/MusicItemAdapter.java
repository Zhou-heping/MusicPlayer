package com.example.musicplayer;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import com.example.musicplayer.data.Music;
import com.example.musicplayer.util.GetMusicInfo;

import java.util.ArrayList;
import java.util.List;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class MusicItemAdapter extends RecyclerView.Adapter<MusicItemAdapter.ViewHolder> {
    public static List<Music> musicList = new ArrayList<>();
    public static Context mContext;
    static class ViewHolder extends RecyclerView.ViewHolder{
        ImageView musicImage;
        TextView musicName;
        TextView musicAuthor;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            this.musicImage = itemView.findViewById(R.id.music_image);
            this.musicName = itemView.findViewById(R.id.music_name);
            this.musicAuthor = itemView.findViewById(R.id.music_author);
            // item点击事件
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(mContext, SongActivity.class);
                    Bundle bundle = new Bundle();
                    bundle.putInt("musicPosition", getAdapterPosition());
                    intent.putExtras(bundle);
                    mContext.startActivity(intent);
                }
            });
        }
    }
    public MusicItemAdapter(List<Music> musicList) {
        this.musicList = musicList;
    }

    @NonNull
    @Override
    public MusicItemAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        if (mContext == null){
            mContext = viewGroup.getContext();
        }
        View view = LayoutInflater.from(mContext).inflate(R.layout.music_item,
                viewGroup, false);
        ViewHolder holder = new ViewHolder(view);
        // item的点击监听器
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Music music = musicList.get(position);
        holder.musicImage.setImageBitmap(GetMusicInfo.getAlbumArt((int)music.getAlbumId(), mContext));
        holder.musicName.setText(music.getTitle());
        holder.musicAuthor.setText(music.getAritist() + "-" + music.getAlbum());
    }

    @Override
    public int getItemCount() {
        return musicList.size();
    }
}
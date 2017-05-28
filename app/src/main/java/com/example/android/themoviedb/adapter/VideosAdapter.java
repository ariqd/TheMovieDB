package com.example.android.themoviedb.adapter;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.android.themoviedb.R;
import com.example.android.themoviedb.listener.MovieClickListener;
import com.example.android.themoviedb.model.VideoModel;
import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * Created by AriqD on 22/05/2017.
 */

public class VideosAdapter extends RecyclerView.Adapter<VideosAdapter.ViewHolder> {

    private Context context;
    private List<VideoModel> videoList;

    public VideosAdapter(Context context, List<VideoModel> videoList) {
        this.context = context;
        this.videoList = videoList;
    }
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.videos_row, parent, false);
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        final VideoModel video = videoList.get(position);

        holder.tvTitle.setText(video.getName());
        holder.tvType.setText(video.getType());

        Picasso.with(context).load("http://img.youtube.com/vi/" + video.getKey() + "/mqdefault.jpg").into(holder.ivThumbnail);

        holder.setMovieClickListener(new MovieClickListener() {
            @Override
            public void onClick(View view) {
                context.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("http://www.youtube.com/watch?v=" + video.getKey())));
            }
        });

    }

    @Override
    public int getItemCount() {
        return videoList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        ImageView ivThumbnail;
        TextView tvTitle;
        TextView tvType;
        MovieClickListener movieClickListener;

        public ViewHolder(View itemView) {
            super(itemView);

            ivThumbnail = (ImageView) itemView.findViewById(R.id.iv_thumbnail);
            tvTitle = (TextView) itemView.findViewById(R.id.tv_video_title);
            tvType = (TextView) itemView.findViewById(R.id.tv_video_type);

            itemView.setOnClickListener(this);
        }

        public void setMovieClickListener(MovieClickListener movieClickListener) {
            this.movieClickListener = movieClickListener;
        }

        @Override
        public void onClick(View view) {
            movieClickListener.onClick(view);
        }
    }
}

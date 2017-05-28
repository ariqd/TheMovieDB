package com.example.android.themoviedb.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.android.themoviedb.R;
import com.example.android.themoviedb.model.EpisodeModel;
import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * Created by AriqD on 24/05/2017.
 */

public class EpisodesAdapter extends RecyclerView.Adapter<EpisodesAdapter.ViewHolder> {

    private Context context;
    private List<EpisodeModel> episodesList;

    public EpisodesAdapter(Context context, List<EpisodeModel> episodesList) {
        this.context = context;
        this.episodesList = episodesList;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_episodes, parent, false);
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        final EpisodeModel episode = episodesList.get(position);

        holder.tvNo.setText(Integer.toString(episode.getEpisodeNumber()));
        holder.tvName.setText(episode.getName());
        holder.tvDate.setText(episode.getAir_date());
        holder.tvOverview.setText(episode.getOverview());
        holder.tvVote.setText(Double.toString(episode.getVote()));
        Picasso.with(context).load("https://image.tmdb.org/t/p/w342" + episode.getStillPath()).into(holder.ivStill);

    }

    @Override
    public int getItemCount() {
        return episodesList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvNo;
        TextView tvName;
        TextView tvDate;
        TextView tvOverview;
        TextView tvVote;
        ImageView ivStill;

        public ViewHolder(View itemView) {
            super(itemView);

            tvNo = (TextView) itemView.findViewById(R.id.tv_no);
            tvName = (TextView) itemView.findViewById(R.id.tv_episode_title);
            tvDate = (TextView) itemView.findViewById(R.id.tv_date);
            tvOverview = (TextView) itemView.findViewById(R.id.tv_overview);
            tvVote = (TextView) itemView.findViewById(R.id.tv_rating);
            ivStill = (ImageView) itemView.findViewById(R.id.iv_episode_image);
        }
    }
}

package com.example.android.themoviedb.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.android.themoviedb.MovieActivity;
import com.example.android.themoviedb.R;
import com.example.android.themoviedb.listener.MovieClickListener;
import com.example.android.themoviedb.model.CastModel;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by AriqD on 18/05/2017.
 */

public class CreditAdapter extends RecyclerView.Adapter<CreditAdapter.ViewHolder> {

    private Context context;
    List<CastModel> castModels = new ArrayList<>();

    public CreditAdapter(Context context, List<CastModel> castModels) {
        this.context = context;
        this.castModels = castModels;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_people_playing_as, parent, false);
        CreditAdapter.ViewHolder viewHolder = new CreditAdapter.ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        final CastModel cast = castModels.get(position);

        holder.tvTitle.setText(cast.getName());
        holder.tvAs.setText(cast.getCharacter());
        holder.tvYear.setText(cast.getDate());

        Picasso.with(context).load("https://image.tmdb.org/t/p/w342" + cast.getProfilePath()).into(holder.ivPoster);

        holder.setMovieClickListener(new MovieClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, MovieActivity.class);
                intent.putExtra("id", cast.getId());
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return castModels.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView tvTitle;
        TextView tvAs;
        TextView tvYear;
        ImageView ivPoster;
        MovieClickListener movieClickListener;

        public ViewHolder(View itemView) {
            super(itemView);

            tvTitle = (TextView) itemView.findViewById(R.id.tv_title);
            tvAs = (TextView) itemView.findViewById(R.id.tv_as);
            tvYear = (TextView) itemView.findViewById(R.id.tv_year);
            ivPoster = (ImageView) itemView.findViewById(R.id.iv_poster);

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

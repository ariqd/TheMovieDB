package com.example.android.themoviedb.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.android.themoviedb.R;
import com.example.android.themoviedb.TvActivity;
import com.example.android.themoviedb.listener.MovieClickListener;
import com.example.android.themoviedb.model.TvModel;
import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * Created by AriqD on 24/05/2017.
 */

public class TvPopularAdapter extends RecyclerView.Adapter<TvPopularAdapter.ViewHolder> {

    private Context context;
    private List<TvModel> popularList;

    public TvPopularAdapter(Context context, List<TvModel> popularList) {
        this.context = context;
        this.popularList = popularList;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_tv_popular, parent, false);
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(TvPopularAdapter.ViewHolder holder, int position) {
        final TvModel tv = popularList.get(position);

        int i = position + 1;
        holder.tvNumber.setText(Integer.toString(i));
        Picasso.with(context).load("https://image.tmdb.org/t/p/w342" + tv.getPosterPath()).into(holder.ivPoster);

        holder.setMovieClickListener(new MovieClickListener() {
            @Override
            public void onClick(View view) {
            Intent intent = new Intent(context, TvActivity.class);
            intent.putExtra("id", tv.getId());
            context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return popularList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView tvNumber;
        ImageView ivPoster;
        MovieClickListener movieClickListener;

        public ViewHolder(View itemView) {
            super(itemView);

            tvNumber = (TextView) itemView.findViewById(R.id.tv_number);
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

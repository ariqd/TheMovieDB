package com.example.android.themoviedb.adapter.search;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
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

import java.util.Iterator;
import java.util.List;

/**
 * Created by AriqD on 28/05/2017.
 */

public class SearchTvAdapter extends RecyclerView.Adapter<SearchTvAdapter.ViewHolder> {

    private Context context;
    private List<TvModel> movieList;

    public SearchTvAdapter(Context context, List<TvModel> movieList) {
        this.context = context;
        this.movieList = movieList;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_search_movie, parent, false);
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        final TvModel movie = movieList.get(position);

        holder.tvTitle.setText(movie.getName());
        holder.tvVote.setText(Double.toString(movie.getVote()));
        Picasso.with(context).load("https://image.tmdb.org/t/p/w342" + movie.getPosterPath()).into(holder.ivPoster);

        final StringBuilder genres = new StringBuilder();
        Iterator<String> it = movie.getGenre_ids().iterator();
        if (it.hasNext()) {
            genres.append(it.next());
        }
        while (it.hasNext()) {
            genres.append(", ");
            genres.append(it.next());
        }
        holder.tvGenre.setText(genres);

        holder.setMovieClickListener(new MovieClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, TvActivity.class);
                intent.putExtra("id", movie.getId());
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return movieList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView tvTitle;
        TextView tvGenre;
        TextView tvVote;
        ImageView ivPoster;
        MovieClickListener movieClickListener;

        public ViewHolder(View itemView) {
            super(itemView);

            tvTitle = (TextView) itemView.findViewById(R.id.tv_title);
            tvGenre = (TextView) itemView.findViewById(R.id.tv_genre);
            tvVote = (TextView) itemView.findViewById(R.id.tv_vote);
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

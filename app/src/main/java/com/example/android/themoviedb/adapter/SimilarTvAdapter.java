package com.example.android.themoviedb.adapter;

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

import java.io.Serializable;
import java.util.Iterator;
import java.util.List;

/**
 * Created by AriqD on 23/05/2017.
 */

public class SimilarTvAdapter extends RecyclerView.Adapter<SimilarTvAdapter.ViewHolder>{

    private Context context;
    private List<TvModel> similarList;

    public SimilarTvAdapter(Context context, List<TvModel> similarList) {
        this.context = context;
        this.similarList = similarList;
    }

    @Override
    public SimilarTvAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_movies_simple, parent, false);
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(SimilarTvAdapter.ViewHolder holder, int position) {
        final TvModel movie = similarList.get(position);

        holder.tvName.setText(movie.getName());
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

        holder.setMovieClickListener(new MovieClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, TvActivity.class);
                intent.putExtra("id", movie.getId());
                intent.putExtra("genre", (Serializable) genres);
                context.startActivity(intent);
            }
        });

    }

    @Override
    public int getItemCount() {
        return similarList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        ImageView ivPoster;
        TextView tvName;
        MovieClickListener movieClickListener;

        public ViewHolder(View itemView) {
            super(itemView);

            ivPoster = (ImageView) itemView.findViewById(R.id.iv_simple_poster);
            tvName = (TextView) itemView.findViewById(R.id.tv_simple_title);

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

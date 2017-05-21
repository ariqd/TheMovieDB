package com.example.android.themoviedb.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.android.themoviedb.MovieDetails;
import com.example.android.themoviedb.R;
import com.example.android.themoviedb.listener.MovieClickListener;
import com.example.android.themoviedb.model.MovieModel;
import com.squareup.picasso.Picasso;

import java.io.Serializable;
import java.util.Iterator;
import java.util.List;

/**
 * Created by AriqD on 18/05/2017.
 */

public class PopularAdapter extends RecyclerView.Adapter<PopularAdapter.ViewHolder>{

    private Context context;
    private List<MovieModel> popularList;

    public PopularAdapter(Context context, List<MovieModel> movieList) {
        this.context = context;
        this.popularList = movieList;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_popular_movies, parent, false);
        PopularAdapter.ViewHolder viewHolder = new PopularAdapter.ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        final MovieModel movie = popularList.get(position);

        int i = position + 1;
        holder.tvNumber.setText(Integer.toString(i));
        Picasso.with(context).load("https://image.tmdb.org/t/p/w500" + movie.getPosterPath()).into(holder.ivPoster);

        final StringBuilder genres = new StringBuilder();
        Iterator<String> it = movie.getGenreList().iterator();
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
                Intent intent = new Intent(context, MovieDetails.class);
                intent.putExtra("id", movie.getId());
                intent.putExtra("genre", (Serializable) genres);
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

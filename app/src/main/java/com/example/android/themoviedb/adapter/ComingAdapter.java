package com.example.android.themoviedb.adapter;

import android.content.Context;
import android.content.Intent;
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
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

/**
 * Created by AriqD on 19/05/2017.
 */

public class ComingAdapter extends RecyclerView.Adapter<ComingAdapter.ViewHolder> {

    private Context context;
    private List<MovieModel> movieList;

    public ComingAdapter(Context context, List<MovieModel> movieList) {
        this.context = context;
        this.movieList = movieList;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_coming_soon, parent, false);
        ComingAdapter.ViewHolder viewHolder = new ComingAdapter.ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        final MovieModel movie = movieList.get(position);

        DateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd");
        DateFormat outputFormat = new SimpleDateFormat("MMM dd, yyyy");
        String inputDateStr = movie.getReleaseDate();
        Date date = null;
        try {
            date = inputFormat.parse(inputDateStr);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        String mdy = outputFormat.format(date);
        holder.tvReleaseDate.setText(mdy);

        Picasso.with(context).load("https://image.tmdb.org/t/p/w500" + movie.getPosterPath()).into(holder.ivComingPoster);

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
        return movieList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView tvReleaseDate;
        ImageView ivComingPoster;
        MovieClickListener movieClickListener;

        public ViewHolder(View itemView) {
            super(itemView);

            tvReleaseDate = (TextView) itemView.findViewById(R.id.tv_coming_date);
            ivComingPoster = (ImageView) itemView.findViewById(R.id.iv_coming_poster);

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

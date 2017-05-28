package com.example.android.themoviedb.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.example.android.themoviedb.R;
import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * Created by AriqD on 22/05/2017.
 */

public class ImagesAdapter extends RecyclerView.Adapter<ImagesAdapter.ViewHolder> {

    private Context context;
    private List<String> imagesList;

    public ImagesAdapter (Context context, List<String> imagesList) {
        this.context = context;
        this.imagesList = imagesList;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.images_row, parent, false);
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        final String image = imagesList.get(position);
        Picasso.with(context).load("https://image.tmdb.org/t/p/w500" + image).into(holder.ivImage);
        Log.d("path", image);
    }

    @Override
    public int getItemCount() {
        return imagesList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView ivImage;

        public ViewHolder(View itemView) {
            super(itemView);
            ivImage = (ImageView) itemView.findViewById(R.id.iv_image);
        }
    }
}

package com.example.android.themoviedb.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.android.themoviedb.PeopleActivity;
import com.example.android.themoviedb.R;
import com.example.android.themoviedb.listener.CastClickListener;
import com.example.android.themoviedb.model.CastModel;
import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * Created by AriqD on 18/05/2017.
 */

public class CastAdapter extends RecyclerView.Adapter<CastAdapter.ViewHolder> {

    private Context context;
    private List<CastModel> castList;

    public CastAdapter(Context context, List<CastModel> castList) {
        this.context = context;
        this.castList = castList;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_cast, parent, false);
        CastAdapter.ViewHolder viewHolder = new CastAdapter.ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        final CastModel cast = castList.get(position);

        holder.tvName.setText(cast.getName());
        holder.tvCharacterName.setText(cast.getCharacter());

        Picasso.with(context).load("https://image.tmdb.org/t/p/w342" + cast.getProfilePath()).into(holder.ivFace);

        holder.setCastClickListener(new CastClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, PeopleActivity.class);
                intent.putExtra("id", cast.getId());
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return castList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView tvName;
        TextView tvCharacterName;
        ImageView ivFace;
        CastClickListener castClickListener;

        public ViewHolder(View itemView) {
            super(itemView);

            tvName = (TextView) itemView.findViewById(R.id.tv_name);
            tvCharacterName = (TextView) itemView.findViewById(R.id.tv_character);
            ivFace = (ImageView) itemView.findViewById(R.id.iv_face);

            itemView.setOnClickListener(this);
        }

        public void setCastClickListener(CastClickListener castClickListener) {
            this.castClickListener = castClickListener;
        }

        @Override
        public void onClick(View view) {
            castClickListener.onClick(view);
        }
    }
}

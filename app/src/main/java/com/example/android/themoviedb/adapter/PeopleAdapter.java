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
import com.example.android.themoviedb.model.PeopleModel;
import com.squareup.picasso.Picasso;

import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.List;

/**
 * Created by AriqD on 23/05/2017.
 */

public class PeopleAdapter extends RecyclerView.Adapter<PeopleAdapter.ViewHolder> {

    private Context context;
    private List<PeopleModel> peopleList;

    public PeopleAdapter(Context context, List<PeopleModel> peopleList) {
        this.context = context;
        this.peopleList = peopleList;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_people, parent, false);
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        final PeopleModel people = peopleList.get(position);

        int number = position + 1;
        holder.tvNo.setText(Integer.toString(number) + ". ");
        holder.tvName.setText(people.getName());
        Picasso.with(context).load("https://image.tmdb.org/t/p/w342" + people.getProfilePath()).into(holder.ivFace);
        double popularityDouble = people.getPopularity();
        DecimalFormat df = new DecimalFormat("#.##");
        df.setRoundingMode(RoundingMode.CEILING);
        String popularityString = String.valueOf(Double.parseDouble(df.format(popularityDouble)));
        holder.tvPopularity.setText(popularityString);

        holder.setCastClickListener(new CastClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, PeopleActivity.class);
                intent.putExtra("id", people.getId());
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return peopleList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView tvNo;
        TextView tvName;
        ImageView ivFace;
        TextView tvPopularity;
        CastClickListener castClickListener;

        public ViewHolder(View itemView) {
            super(itemView);

            tvNo = (TextView) itemView.findViewById(R.id.tv_no);
            tvName = (TextView) itemView.findViewById(R.id.tv_name);
            ivFace = (ImageView) itemView.findViewById(R.id.iv_face);
            tvPopularity = (TextView) itemView.findViewById(R.id.tv_popularity);

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

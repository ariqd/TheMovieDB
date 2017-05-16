package com.example.android.themoviedb;

import android.app.ActionBar;
import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import org.w3c.dom.Text;

import java.util.zip.Inflater;

public class MovieDetails extends AppCompatActivity {

    ImageView ivBackdrop;
    ImageView ivPoster;
    TextView tvTitle;
    TextView tvReleaseDate;
    TextView tvOverview;
    private Context context = this;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_details);

        android.support.v7.app.ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        ivBackdrop = (ImageView) findViewById(R.id.iv_backdrop);
        ivPoster = (ImageView) findViewById(R.id.iv_detail_poster);
        tvTitle = (TextView) findViewById(R.id.tv_detail_title);
        tvReleaseDate = (TextView) findViewById(R.id.tv_detail_release_date);
        tvOverview = (TextView) findViewById(R.id.tv_detail_overview);

        Intent intent = getIntent();

        tvTitle.setText(intent.getStringExtra("title"));
        tvReleaseDate.setText(intent.getStringExtra("release_date"));
        tvOverview.setText(intent.getStringExtra("overview"));

        Picasso.with(context).load("https://image.tmdb.org/t/p/w500" + intent.getStringExtra("poster")).into(ivPoster);
        Picasso.with(context).load("https://image.tmdb.org/t/p/w1280" + intent.getStringExtra("backdrop")).into(ivBackdrop);

//        Log.d("New Intent", "Halo");
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home ) {
            finish();
            return true;
        }
        // other menu select events may be present here

        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_main, menu);
        return true;
    }
}

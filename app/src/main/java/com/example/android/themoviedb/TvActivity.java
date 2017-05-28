package com.example.android.themoviedb;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.android.themoviedb.adapter.CastAdapter;
import com.example.android.themoviedb.adapter.SimilarTvAdapter;
import com.example.android.themoviedb.adapter.VideosAdapter;
import com.example.android.themoviedb.model.CastModel;
import com.example.android.themoviedb.model.TvModel;
import com.example.android.themoviedb.model.VideoModel;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.DateFormat;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

public class TvActivity extends AppCompatActivity {

    ImageView ivBackdrop;
    ImageView ivPoster;
    TextView tvFirstAir;
    TextView tvOverview;
    TextView tvGenre;
    TextView tvRating;
    TextView tvVotes;
    TextView tvLastAir;
    TextView tvCreator;
    TextView tvNetworks;
    TextView tvStatus;
    TextView tvType;
    TextView tvToolbarTitle;

    private List<CastModel> castList = new ArrayList<CastModel>();
    private List<TvModel> similarList = new ArrayList<>();
    private List<VideoModel> videoList = new ArrayList<>();

    private SimilarTvAdapter similarAdapter;
    private CastAdapter castAdapter;
    private VideosAdapter videosAdapter;

    private String tvId;
    private String title;
    private int count = 0;

    private Intent intent;
    private Context context = this;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tv);
//        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
//        setSupportActionBar(toolbar);
        intent = getIntent();

        ivBackdrop = (ImageView) findViewById(R.id.iv_backdrop);
        ivPoster = (ImageView) findViewById(R.id.iv_detail_poster);
        tvFirstAir = (TextView) findViewById(R.id.tv_date_1_text);
        tvOverview = (TextView) findViewById(R.id.tv_detail_overview);
        tvGenre = (TextView) findViewById(R.id.tv_genre);
        tvRating = (TextView) findViewById(R.id.tv_rating);
        tvVotes = (TextView) findViewById(R.id.tv_vote_count);
        tvLastAir = (TextView) findViewById(R.id.tv_date_2_text);
        tvCreator = (TextView) findViewById(R.id.tv_created_by_name);
        tvNetworks = (TextView) findViewById(R.id.tv_networks_name);
        tvStatus = (TextView) findViewById(R.id.tv_status_name);
        tvType = (TextView) findViewById(R.id.tv_type_text);
        tvToolbarTitle = (TextView) findViewById(R.id.tv_toolbar_title);

        RecyclerView rvCast = (RecyclerView) findViewById(R.id.rv_cast);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getApplicationContext(),
                LinearLayoutManager.HORIZONTAL, false);
        rvCast.setLayoutManager(layoutManager);
        rvCast.setNestedScrollingEnabled(false);
        castAdapter = new CastAdapter(this, castList);
        rvCast.setAdapter(castAdapter);
        rvCast.setFocusable(false);

        RecyclerView rvSimilar = (RecyclerView) findViewById(R.id.rv_similar);
        RecyclerView.LayoutManager layoutManager2 = new LinearLayoutManager(getApplicationContext(),
                LinearLayoutManager.HORIZONTAL, false);
        rvSimilar.setLayoutManager(layoutManager2);
        rvSimilar.setNestedScrollingEnabled(false);
        similarAdapter = new SimilarTvAdapter(this, similarList);
        rvSimilar.setAdapter(similarAdapter);
        rvSimilar.setFocusable(false);

        RecyclerView rvVideos = (RecyclerView) findViewById(R.id.rv_videos);
        RecyclerView.LayoutManager layoutManagerVideos = new LinearLayoutManager(getApplicationContext(),
                LinearLayoutManager.HORIZONTAL, false);
        rvVideos.setNestedScrollingEnabled(false);
        rvVideos.setLayoutManager(layoutManagerVideos);
        videosAdapter = new VideosAdapter(this, videoList);
        rvVideos.setAdapter(videosAdapter);
        rvVideos.setFocusable(false);

        new FetchMovieDetails().execute();
    }

    public class FetchMovieDetails extends AsyncTask<Void, Void, String> {

        @Override
        protected String doInBackground(Void... params) {
            HttpURLConnection httpURLConnection = null;
            BufferedReader bufferedReader = null;
            String strJSONMovie = "";

            try {
                String strUrl = " https://api.themoviedb.org/3/tv/" +
                        String.valueOf(intent.getIntExtra("id", 0)) +"?api_key=9417ea2506327529d239284cd696078c" +
                        "&append_to_response=credits,similar,videos";
                URL url = new URL(strUrl);

                httpURLConnection = (HttpURLConnection) url.openConnection();
                httpURLConnection.setRequestMethod("GET");
                httpURLConnection.connect();

                InputStream inputStream = httpURLConnection.getInputStream();
                StringBuffer stringBuffer = new StringBuffer();

                if (inputStream == null) {
                    return null;
                } else {
                    bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
                }

                String line = "";
                while ((line = bufferedReader.readLine()) != null) {
                    stringBuffer.append(line);
                }

                if (stringBuffer.length() == 0) {
                    return null;
                } else {
                    strJSONMovie = stringBuffer.toString();
                    return strJSONMovie;
                }

            } catch (IOException e) {
                Log.e("Parse Error", e.toString());
                return null;
            }
        }

        private void fetchJSONMovie (String response) {
            try{
                final JSONObject jsonObject = new JSONObject(response);

                tvId = jsonObject.getString("id");
                title = jsonObject.getString("name");

                JSONArray jsonArraySeasons = jsonObject.getJSONArray("seasons");
                for (int s = 0; s < jsonArraySeasons.length(); s++) {
                    JSONObject jsonObjectSeasons = jsonArraySeasons.getJSONObject(s);
                    if (jsonObjectSeasons.getInt("season_number") != 0) {
                        count = s;
                    }
                }
                System.out.println(count);

                tvToolbarTitle.setText(jsonObject.getString("name"));
                tvOverview.setText(jsonObject.getString("overview"));

                DateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd");
                DateFormat outputFormat = new SimpleDateFormat("MMMM dd, yyyy");
                String inputDateStr1 = jsonObject.getString("first_air_date");
                String inputDateStr2 = jsonObject.getString("last_air_date");
                Date date = null;
                Date date2 = null;
                try {
                    date = inputFormat.parse(inputDateStr1);
                    date2 = inputFormat.parse(inputDateStr2);
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                String mdy = outputFormat.format(date);
                String mdy2 = outputFormat.format(date2);
                tvFirstAir.setText(mdy);
                tvLastAir.setText(mdy2);

                ArrayList<String> movieGenreNames = new ArrayList<>();
                JSONArray jsonArrayGenre = jsonObject.getJSONArray("genres");
                for (int i = 0; i < jsonArrayGenre.length(); i++) {
                    JSONObject jsonObjectGenre = jsonArrayGenre.getJSONObject(i);
                    movieGenreNames.add(jsonObjectGenre.getString("name"));
                }
                final StringBuilder genres = new StringBuilder();
                Iterator<String> it = movieGenreNames.iterator();
                if (it.hasNext()) {
                    genres.append(it.next());
                }
                while (it.hasNext()) {
                    genres.append(", ");
                    genres.append(it.next());
                }
                tvGenre.setText(genres);

                tvRating.setText(Double.toString(jsonObject.getDouble("vote_average")));
                tvVotes.setText(Integer.toString(jsonObject.getInt("vote_count")));
                tvStatus.setText(jsonObject.getString("status"));
                tvType.setText(jsonObject.getString("type"));

                JSONArray jsonArrayCrew = jsonObject.getJSONArray("created_by");
                ArrayList<String> creatorsList = new ArrayList<>();
                for (int x = 0; x < jsonArrayCrew.length(); x++) {
                    JSONObject jsonObjectCreator = jsonArrayCrew.getJSONObject(x);
                    creatorsList.add(jsonObjectCreator.getString("name"));
                }
                StringBuilder creators = new StringBuilder();
                Iterator<String> its = creatorsList.iterator();
                if (its.hasNext()) {
                    creators.append(its.next());
                }
                while (its.hasNext()) {
                    creators.append(", ");
                    creators.append(its.next());
                }
                tvCreator.setText(creators);

                JSONArray jsonArrayNetworks = jsonObject.getJSONArray("networks");
                ArrayList<String> networksList = new ArrayList<>();
                for (int x = 0; x < jsonArrayNetworks.length(); x++) {
                    JSONObject jsonObjectCreator = jsonArrayNetworks.getJSONObject(x);
                    networksList.add(jsonObjectCreator.getString("name"));
                }
                StringBuilder networks = new StringBuilder();
                Iterator<String> itn = networksList.iterator();
                if (itn.hasNext()) {
                    networks.append(itn.next());
                }
                while (itn.hasNext()) {
                    networks.append(", ");
                    networks.append(itn.next());
                }
                tvNetworks.setText(networks);

                JSONObject jsonObjectCredits = jsonObject.getJSONObject("credits");
                JSONArray jsonArray = jsonObjectCredits.getJSONArray("cast");

                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject jsonObjectList = jsonArray.getJSONObject(i);
                    CastModel cast = new CastModel();

                    cast.setId(jsonObjectList.getInt("id"));
                    cast.setOrder(jsonObjectList.getInt("order"));
                    cast.setCredit_id(jsonObjectList.getString("credit_id"));
                    cast.setName(jsonObjectList.getString("name"));
                    cast.setCharacter(jsonObjectList.getString("character"));
                    cast.setProfilePath(jsonObjectList.getString("profile_path"));

                    castList.add(cast);
                }
                castAdapter.notifyDataSetChanged();

                Picasso.with(context).load("https://image.tmdb.org/t/p/w342" + jsonObject.getString("poster_path")).into(ivPoster);
                Picasso.with(context).load("https://image.tmdb.org/t/p/w780" + jsonObject.getString("backdrop_path")).into(ivBackdrop);

                /*
                 * Similar Movies
                 */
                JSONObject jsonObjectSimilar = jsonObject.getJSONObject("similar");
                JSONArray jsonArraySimilar = jsonObjectSimilar.getJSONArray("results");

                for (int a = 0; a < jsonArraySimilar.length(); a++) {
                    JSONObject jsonObjectSimilarResults = jsonArraySimilar.getJSONObject(a);
                    TvModel movie = new TvModel();
                    movie.setId(jsonObjectSimilarResults.getInt("id"));
                    movie.setName(jsonObjectSimilarResults.getString("name"));
                    movie.setPosterPath(jsonObjectSimilarResults.getString("poster_path"));
                    similarList.add(movie);
                }
                similarAdapter.notifyDataSetChanged();

                Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
                setTitle(" ");
                setSupportActionBar(toolbar);
                ActionBar actionBar = getSupportActionBar();
                actionBar.setDisplayHomeAsUpEnabled(true);

                final String name = jsonObject.getString("name");
                final CollapsingToolbarLayout collapsingToolbarLayout = (CollapsingToolbarLayout) findViewById(R.id.toolbar_layout);
                AppBarLayout appBarLayout = (AppBarLayout) findViewById(R.id.app_bar);
                appBarLayout.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {
                    boolean isShow = false;
                    int scrollRange = -1;

                    @Override
                    public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
                        if (scrollRange == -1) {
                            scrollRange = appBarLayout.getTotalScrollRange();
                        }
                        if (scrollRange + verticalOffset == 0) {
                            collapsingToolbarLayout.setTitle(name);
                            tvToolbarTitle.setVisibility(View.GONE);
                            isShow = true;
                        } else if(isShow) {
                            collapsingToolbarLayout.setTitle(" ");
                            tvToolbarTitle.setVisibility(View.VISIBLE);
                            isShow = false;
                        }
                    }
                });

                /*
                 * Videos
                 */
                JSONObject jsonObjectVideos = jsonObject.getJSONObject("videos");
                JSONArray jsonArrayVideos = jsonObjectVideos.getJSONArray("results");
                for (int v = 0; v < jsonArrayVideos.length(); v++) {
                    JSONObject jsonObjectResults = jsonArrayVideos.getJSONObject(v);
                    VideoModel video = new VideoModel();

                    video.setId(jsonObjectResults.getString("id"));
                    video.setName(jsonObjectResults.getString("name"));
                    video.setSite(jsonObjectResults.getString("site"));
                    video.setKey(jsonObjectResults.getString("key"));
                    video.setType(jsonObjectResults.getString("type"));
                    video.setSize(jsonObjectResults.getInt("size"));

//                    if (video.getType().equals("Trailer"))
                    videoList.add(video);
                }
                videosAdapter.notifyDataSetChanged();

            } catch (JSONException e) {
                Log.e("Error JSON", e.toString());
            }
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            fetchJSONMovie(s);
//            Log.d("Cek", s);
        }
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

    public void onClick(View view) {
        Intent seasons = new Intent(context, EpisodesActivity.class);
        seasons.putExtra("id", tvId);
        seasons.putExtra("count", count);
        seasons.putExtra("title", title);
        startActivity(seasons);
    }

}

package com.example.android.themoviedb;

import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.android.themoviedb.adapter.CastAdapter;
import com.example.android.themoviedb.adapter.ImagesAdapter;
import com.example.android.themoviedb.adapter.MovieAdapter;
import com.example.android.themoviedb.adapter.SimilarAdapter;
import com.example.android.themoviedb.adapter.VideosAdapter;
import com.example.android.themoviedb.model.CastModel;
import com.example.android.themoviedb.model.GenreModel;
import com.example.android.themoviedb.model.MovieModel;
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

public class MovieActivity extends AppCompatActivity {

    ImageView ivBackdrop;
    ImageView ivPoster;
    TextView tvReleaseDate;
    TextView tvOverview;
    TextView tvGenre;
    TextView tvRating;
    TextView tvVotes;
    TextView tvDuration;
    TextView tvDirectorName;
    TextView tvBudget;
    TextView tvRevenue;
    TextView tvTagline;
    TextView tvToolbarTitle;

    private List<CastModel> castList = new ArrayList<CastModel>();
    private List<MovieModel> similarList = new ArrayList<MovieModel>();
    private List<VideoModel> videoList = new ArrayList<>();
    private List<String> imagesList = new ArrayList<>();

    private CastAdapter castAdapter;
    private SimilarAdapter similarAdapter;
    private ImagesAdapter imagesAdapter;
    private VideosAdapter videosAdapter;

    private Context context = this;
    private String query;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie);

        ivBackdrop = (ImageView) findViewById(R.id.iv_backdrop);
        ivPoster = (ImageView) findViewById(R.id.iv_detail_poster);
        tvReleaseDate = (TextView) findViewById(R.id.tv_detail_release_date);
        tvOverview = (TextView) findViewById(R.id.tv_detail_overview);
        tvGenre = (TextView) findViewById(R.id.tv_genre);
        tvRating = (TextView) findViewById(R.id.tv_rating);
        tvVotes = (TextView) findViewById(R.id.tv_vote_count);
        tvDuration = (TextView) findViewById(R.id.tv_detail_duration);
        tvDirectorName = (TextView) findViewById(R.id.tv_director_name);
        tvBudget = (TextView) findViewById(R.id.tv_budget_name);
        tvRevenue = (TextView) findViewById(R.id.tv_revenue_name);
        tvTagline = (TextView) findViewById(R.id.tv_tagline_text);
        tvToolbarTitle = (TextView) findViewById(R.id.tv_toolbar_title);

        RecyclerView rvCast = (RecyclerView) findViewById(R.id.rv_cast);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getApplicationContext(),
                LinearLayoutManager.HORIZONTAL, false);
        rvCast.setLayoutManager(layoutManager);
        rvCast.setNestedScrollingEnabled(false);
        castAdapter = new CastAdapter(this, castList);
        rvCast.setAdapter(castAdapter);

        RecyclerView rvSimilar = (RecyclerView) findViewById(R.id.rv_similar);
        RecyclerView.LayoutManager layoutManager2 = new LinearLayoutManager(getApplicationContext(),
                LinearLayoutManager.HORIZONTAL, false);
        rvSimilar.setLayoutManager(layoutManager2);
        rvSimilar.setNestedScrollingEnabled(false);
        similarAdapter = new SimilarAdapter(this, similarList);
        rvSimilar.setAdapter(similarAdapter);

        RecyclerView rvImages = (RecyclerView) findViewById(R.id.rv_images);
        RecyclerView.LayoutManager layoutManagerImages = new LinearLayoutManager(getApplicationContext(),
                LinearLayoutManager.HORIZONTAL, false);
        rvImages.setLayoutManager(layoutManagerImages);
        rvImages.setNestedScrollingEnabled(false);
        imagesAdapter = new ImagesAdapter(this, imagesList);
        rvImages.setAdapter(imagesAdapter);

        RecyclerView rvVideos = (RecyclerView) findViewById(R.id.rv_videos);
        RecyclerView.LayoutManager layoutManagerVideos = new LinearLayoutManager(getApplicationContext(),
                LinearLayoutManager.HORIZONTAL, false);
        rvVideos.setNestedScrollingEnabled(false);
        rvVideos.setLayoutManager(layoutManagerVideos);
        videosAdapter = new VideosAdapter(this, videoList);
        rvVideos.setAdapter(videosAdapter);

        new FetchMovieDetails().execute();
        new FetchCasts().execute();
    }

    public class FetchMovieDetails extends AsyncTask<Void, Void, String> {

        @Override
        protected String doInBackground(Void... params) {
            HttpURLConnection httpURLConnection = null;
            BufferedReader bufferedReader = null;
            String strJSONMovie = "";
            Intent intent = getIntent();

            try {
                String strUrl = " https://api.themoviedb.org/3/movie/" +
                        String.valueOf(intent.getIntExtra("id", 0)) +"?api_key=9417ea2506327529d239284cd696078c" +
                        "&append_to_response=credits,similar,images,videos";
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

                DateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd");
                DateFormat outputFormat = new SimpleDateFormat("MMMM dd, yyyy");
                String inputDateStr = jsonObject.getString("release_date");
                Date date = null;
                try {
                    date = inputFormat.parse(inputDateStr);
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                String mdy = outputFormat.format(date);

                tvToolbarTitle.setText(jsonObject.getString("title"));
                tvReleaseDate.setText(mdy);
                tvOverview.setText(jsonObject.getString("overview"));

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

                int hours = jsonObject.getInt("runtime") / 60;
                int minutes = jsonObject.getInt("runtime") % 60;
                tvDuration.setText(String.format("%d hrs %2d mins", hours, minutes));

                Locale locale = new Locale("us", "US");
                NumberFormat formatter = NumberFormat.getCurrencyInstance(locale);
                formatter.setMaximumFractionDigits(0);
                tvBudget.setText(formatter.format(jsonObject.getInt("budget")));
                tvRevenue.setText(formatter.format(jsonObject.getInt("revenue")));
                tvTagline.setText(jsonObject.getString("tagline"));

                JSONObject jsonArrayCredits = jsonObject.getJSONObject("credits");
                JSONArray jsonArrayCrew = jsonArrayCredits.getJSONArray("crew");

                ArrayList directorsList = new ArrayList();
                for (int x = 0; x < jsonArrayCrew.length(); x++) {
                    JSONObject jsonObjectCrew = jsonArrayCrew.getJSONObject(x);
                    String job = jsonObjectCrew.getString("job");
                    if (Objects.equals(job, "Director")) {
                        directorsList.add(jsonObjectCrew.getString("name"));
                    }
                }
                StringBuilder directors = new StringBuilder();
                Iterator<String> its = directorsList.iterator();
                if (its.hasNext()) {
                    directors.append(its.next());
                }
                while (its.hasNext()) {
                    directors.append(", ");
                    directors.append(its.next());
                }
                tvDirectorName.setText(directors);

                Picasso.with(context).load("https://image.tmdb.org/t/p/w342" + jsonObject.getString("poster_path")).into(ivPoster);
                Picasso.with(context).load("https://image.tmdb.org/t/p/w780" + jsonObject.getString("backdrop_path")).into(ivBackdrop);

                /*
                 * Similar Movies
                 */
                JSONObject jsonObjectSimilar = jsonObject.getJSONObject("similar");
                JSONArray jsonArraySimilar = jsonObjectSimilar.getJSONArray("results");

                for (int a = 0; a < jsonArraySimilar.length(); a++) {
                    JSONObject jsonObjectSimilarResults = jsonArraySimilar.getJSONObject(a);
                    MovieModel movie = new MovieModel();
                    movie.setId(jsonObjectSimilarResults.getInt("id"));
                    movie.setTitle(jsonObjectSimilarResults.getString("title"));
                    movie.setPosterPath(jsonObjectSimilarResults.getString("poster_path"));
                    similarList.add(movie);
                }
                similarAdapter.notifyDataSetChanged();

                Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
                setTitle(" ");
                setSupportActionBar(toolbar);
                ActionBar actionBar = getSupportActionBar();
                actionBar.setDisplayHomeAsUpEnabled(true);

                final String title = jsonObject.getString("title");
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
                            collapsingToolbarLayout.setTitle(title);
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
                 * Images
                 */
                JSONObject jsonObjectImages = jsonObject.getJSONObject("images");
                JSONArray jsonArrayBackdrop = jsonObjectImages.getJSONArray("backdrops");
                for (int n = 0; n < 8; n++) {
                    JSONObject jsonObjectImage = jsonArrayBackdrop.getJSONObject(n);
                    imagesList.add(jsonObjectImage.getString("file_path"));
                }
                imagesAdapter.notifyDataSetChanged();


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

    public class FetchCasts extends AsyncTask<Void, Void, String> {

        @Override
        protected String doInBackground(Void... params) {
            HttpURLConnection httpURLConnection = null;
            BufferedReader bufferedReader = null;
            String strJSONMovie = "";
            Intent intent = getIntent();

            try {
                String strUrl = " https://api.themoviedb.org/3/movie/" +
                        String.valueOf(intent.getIntExtra("id", 0)) +"/credits?api_key=9417ea2506327529d239284cd696078c";
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

        private void fetchJSONCredit (String response) {
            try{
                JSONObject jsonObject = new JSONObject(response);
                JSONArray jsonArray = jsonObject.getJSONArray("cast");

                for (int i = 0; i < 11; i++) {
                    JSONObject jsonObjectList = jsonArray.getJSONObject(i);
                    CastModel cast = new CastModel();

                    cast.setId(jsonObjectList.getInt("id"));
                    cast.setCast_id(jsonObjectList.getInt("cast_id"));
                    cast.setOrder(jsonObjectList.getInt("order"));
                    cast.setCredit_id(jsonObjectList.getString("credit_id"));
                    cast.setName(jsonObjectList.getString("name"));
                    cast.setCharacter(jsonObjectList.getString("character"));
                    cast.setProfilePath(jsonObjectList.getString("profile_path"));

                    castList.add(cast);
                }
                castAdapter.notifyDataSetChanged();
            } catch (JSONException e) {
                Log.e("Error JSON", e.toString());
            }
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            fetchJSONCredit(s);
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

        MenuItem myActionMenuItem = menu.findItem(R.id.action_search);
        SearchView searchView = (SearchView) myActionMenuItem.getActionView();
        searchView.setIconifiedByDefault(false);
        searchView.setQuery(query, false);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {

            @Override
            public boolean onQueryTextSubmit(String querySubmit) {
                Intent intent = new Intent(context, SearchActivity.class);
                intent.putExtra("query", querySubmit);
                startActivity(intent);
                overridePendingTransition(0,0);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });

        return true;
    }
}

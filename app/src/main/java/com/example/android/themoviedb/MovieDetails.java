package com.example.android.themoviedb;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.android.themoviedb.adapter.CastAdapter;
import com.example.android.themoviedb.adapter.MovieAdapter;
import com.example.android.themoviedb.adapter.PopularAdapter;
import com.example.android.themoviedb.adapter.SimilarAdapter;
import com.example.android.themoviedb.model.CastModel;
import com.example.android.themoviedb.model.GenreModel;
import com.example.android.themoviedb.model.MovieModel;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Array;
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
import java.util.Objects;

public class MovieDetails extends AppCompatActivity {

    ImageView ivBackdrop;
    ImageView ivPoster;
    TextView tvTitle;
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

    private List<CastModel> castList = new ArrayList<CastModel>();
    private List<MovieModel> similarList = new ArrayList<MovieModel>();
    private List<GenreModel> genreList = new ArrayList<GenreModel>();
    private List<Integer> movieGenreList;
    private List<String> movieGenreNames;

    private CastAdapter castAdapter;
    private RecyclerView rvCast;
    private RecyclerView.LayoutManager layoutManager;

    private SimilarAdapter similarAdapter;
    private RecyclerView rvSimilar;
    private RecyclerView.LayoutManager layoutManager2;


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
        tvGenre = (TextView) findViewById(R.id.tv_genre);
        tvRating = (TextView) findViewById(R.id.tv_rating);
        tvVotes = (TextView) findViewById(R.id.tv_vote_count);
        tvDuration = (TextView) findViewById(R.id.tv_detail_duration);
        tvDirectorName = (TextView) findViewById(R.id.tv_director_name);
        tvBudget = (TextView) findViewById(R.id.tv_budget_name);
        tvRevenue = (TextView) findViewById(R.id.tv_revenue_name);
        tvTagline = (TextView) findViewById(R.id.tv_tagline_text);

        /*
         *  Cast Adapter
         */
        rvCast = (RecyclerView) findViewById(R.id.rv_cast);
        layoutManager = new LinearLayoutManager(getApplicationContext(), LinearLayoutManager.HORIZONTAL, false);
        rvCast.setLayoutManager(layoutManager);
        castAdapter = new CastAdapter(this, castList);
        rvCast.setAdapter(castAdapter);

        rvSimilar = (RecyclerView) findViewById(R.id.rv_similar);
        layoutManager2 = new LinearLayoutManager(getApplicationContext(), LinearLayoutManager.HORIZONTAL, false);
        rvSimilar.setLayoutManager(layoutManager2);
        similarAdapter = new SimilarAdapter(this, similarList);
        rvSimilar.setAdapter(similarAdapter);

        new FetchGenreData().execute();
        new FetchMovieDetails().execute();
        new FetchCasts().execute();
    }

    public class FetchMovieDetails extends AsyncTask<Void, Void, String> {

        @Override
        protected String doInBackground(Void... params) {
//            return null;
            HttpURLConnection httpURLConnection = null;
            BufferedReader bufferedReader = null;
            String strJSONMovie = "";
            Intent intent = getIntent();

            try {
                String strUrl = " https://api.themoviedb.org/3/movie/" +
                        String.valueOf(intent.getIntExtra("id", 0)) +"?api_key=9417ea2506327529d239284cd696078c" +
                        "&append_to_response=credits,similar";
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
                JSONObject jsonObject = new JSONObject(response);

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

                tvTitle.setText(jsonObject.getString("title"));
                tvReleaseDate.setText(mdy);
                tvOverview.setText(jsonObject.getString("overview"));
                tvGenre.setText(getIntent().getStringExtra("genre"));
                tvRating.setText(Double.toString(jsonObject.getDouble("vote_average")));
                tvVotes.setText(Integer.toString(jsonObject.getInt("vote_count")));

                int hours = jsonObject.getInt("runtime") / 60;
                int minutes = jsonObject.getInt("runtime") % 60;
                tvDuration.setText(String.format("%d hrs %2d mins", hours, minutes));

                NumberFormat formatter = NumberFormat.getCurrencyInstance();
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
                Iterator<String> it = directorsList.iterator();
                if (it.hasNext()) {
                    directors.append(it.next());
                }
                while (it.hasNext()) {
                    directors.append(", ");
                    directors.append(it.next());
                }
                tvDirectorName.setText(directors);

                Picasso.with(context).load("https://image.tmdb.org/t/p/w500" + jsonObject.getString("poster_path")).into(ivPoster);
                Picasso.with(context).load("https://image.tmdb.org/t/p/w1280" + jsonObject.getString("backdrop_path")).into(ivBackdrop);

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

                    /*
                     * Fetch Genre ID in Movie list with Genre list
                     */
                    movieGenreList = new ArrayList<>();
                    movieGenreNames = new ArrayList<>();
                    JSONArray jsonArrayGenre = jsonObjectSimilarResults.getJSONArray("genre_ids");
                    for (int j = 0; j < jsonArrayGenre.length(); j++) {
                        Integer genre_id = jsonArrayGenre.getInt(j);
                        movieGenreList.add(genre_id);
                    }

                    for (Integer genreid : movieGenreList) {
                        for (GenreModel list : genreList) {
                            if (list.getId() == genreid) {
                                movieGenreNames.add(list.getGenreName());
                            }
                        }
                    }
                    movie.setGenreList(movieGenreNames);
                    similarList.add(movie);
                }
                similarAdapter.notifyDataSetChanged();
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

    public class FetchGenreData extends AsyncTask<Void, Void, String> {

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            fetchJSONGenre(s);
        }

        @Override
        protected String doInBackground(Void... voids) {
            HttpURLConnection httpURLConnection = null;
            BufferedReader bufferedReader = null;
            String strJSONGenre = null;

            try {
                URL url = new URL(" https://api.themoviedb.org/3/genre/movie/list?api_key=9417ea2506327529d239284cd696078c&language=en-US");

                httpURLConnection = (HttpURLConnection) url.openConnection();
                httpURLConnection.setRequestMethod("GET");
                httpURLConnection.connect();

                InputStream inputStream = httpURLConnection.getInputStream();
                StringBuffer stringBuffer = new StringBuffer();

                if (inputStream == null){
                    return null;
                } else {
                    bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
                }

                String line;
                while ((line = bufferedReader.readLine()) != null){
                    stringBuffer.append(line);
                }

                if (stringBuffer.length() == 0){
                    return null;
                } else {
                    strJSONGenre = stringBuffer.toString();
                    return strJSONGenre;
                }


            } catch (IOException e){
                Log.e("Error parsing genre", e.toString());
                return null;
            }
        }

        private void fetchJSONGenre (String response) {
            try{
                JSONObject jsonObjectGenre = new JSONObject(response);
                JSONArray jsonArrayGenre = jsonObjectGenre.getJSONArray("genres");

                for (int i = 0; i < jsonArrayGenre.length(); i++) {
                    JSONObject jsonObjectList = jsonArrayGenre.getJSONObject(i);

                    GenreModel genre = new GenreModel();

                    genre.setId(jsonObjectList.getInt("id"));
                    genre.setGenreName(jsonObjectList.getString("name"));

                    genreList.add(genre);
                }
                // Adapter Notify Data Changed
//                similarAdapter.notifyDataSetChanged();
            } catch (JSONException e) {
                Log.e("Error JSON", e.toString());
            }
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
}

package com.example.android.themoviedb;

import android.os.AsyncTask;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import com.example.android.themoviedb.adapter.TvAiringAdapter;
import com.example.android.themoviedb.adapter.TvPopularAdapter;
import com.example.android.themoviedb.model.GenreModel;
import com.example.android.themoviedb.model.TvModel;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class TabTvShows extends Fragment {

    private TvAiringAdapter tvAiringAdapter;
    private TvPopularAdapter tvPopularAdapter;
    private List<TvModel> tvModelList = new ArrayList<>();
    private List<TvModel> tvPopularList = new ArrayList<>();
    private List<GenreModel> genreList = new ArrayList<>();
    private List<Integer> movieGenreList;
    private List<String> movieGenreNames;
    private ProgressBar progressBar;
    private ProgressBar progressBar2;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.activity_tab_tv_shows, container, false);

        RecyclerView rvAiringToday = (RecyclerView) rootView.findViewById(R.id.rv_airing);
        progressBar = (ProgressBar) rootView.findViewById(R.id.progress_bar_airing);
        RecyclerView.LayoutManager layoutManagerAiring = new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false);
        rvAiringToday.setLayoutManager(layoutManagerAiring);
        tvAiringAdapter = new TvAiringAdapter(getActivity(), tvModelList);
        rvAiringToday.setAdapter(tvAiringAdapter);
        rvAiringToday.setNestedScrollingEnabled(false);
        rvAiringToday.setFocusable(false);

        RecyclerView rvTvPopular = (RecyclerView) rootView.findViewById(R.id.rv_tv_popular);
        progressBar2 = (ProgressBar) rootView.findViewById(R.id.progress_bar_popular);
        RecyclerView.LayoutManager layoutManagerPopular = new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false);
        rvTvPopular.setLayoutManager(layoutManagerPopular);
        tvPopularAdapter = new TvPopularAdapter(getActivity(), tvPopularList);
        rvTvPopular.setAdapter(tvPopularAdapter);
        rvTvPopular.setNestedScrollingEnabled(false);
        rvTvPopular.setFocusable(false);

        new FetchGenreData().execute();
        new FetchAiringToday().execute();
        new FetchPopular().execute();

        return rootView;
    }

    public class FetchAiringToday extends AsyncTask<Void, Void, String> {

        @Override
        protected String doInBackground(Void... voids) {
            HttpURLConnection httpURLConnection;
            BufferedReader bufferedReader;
            String strJSONMovie;

            try {
                String strUrl = "https://api.themoviedb.org/3/tv/airing_today" +
                        "?api_key=9417ea2506327529d239284cd696078c";
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

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            progressBar.setVisibility(View.GONE);
            fetchJSONPopular(s);
        }

        private void fetchJSONPopular (String response) {
            try{
                JSONObject jsonObject = new JSONObject(response);
                JSONArray jsonArray = jsonObject.getJSONArray("results");

                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject jsonObjectList = jsonArray.getJSONObject(i);
                    TvModel tv = new TvModel();

                    tv.setId(jsonObjectList.getInt("id"));
                    tv.setName(jsonObjectList.getString("name"));
//                    tv.setOverview(jsonObjectList.getString("overview"));
                    tv.setPosterPath(jsonObjectList.getString("poster_path"));
//                    tv.setBackdropPath(jsonObjectList.getString("backdrop_path"));
//                    tv.setFirstAirDate(jsonObjectList.getString("first_air_date"));
                    tv.setVote(jsonObjectList.getDouble("vote_average"));
//                    tv.setVote_count(jsonObjectList.getDouble("vote_count"));
//                    tv.setPopularity(jsonObjectList.getDouble("popularity"));

                    /*
                     * Fetch Genre ID in Movie list with Genre list
                     */
                    movieGenreList = new ArrayList<>();
                    movieGenreNames = new ArrayList<>();
                    JSONArray jsonArrayGenre = jsonObjectList.getJSONArray("genre_ids");
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

                    tv.setGenre_ids(movieGenreNames);

                    tvModelList.add(tv);
                }
                tvAiringAdapter.notifyDataSetChanged();
            } catch (JSONException e) {
                Log.e("Error JSON", e.toString());
            }
        }
    }

    public class FetchPopular extends AsyncTask<Void, Void, String> {

        @Override
        protected String doInBackground(Void... voids) {
            HttpURLConnection httpURLConnection;
            BufferedReader bufferedReader;
            String strJSONMovie;

            try {
                String strUrl = "https://api.themoviedb.org/3/tv/popular" +
                        "?api_key=9417ea2506327529d239284cd696078c";
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

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            progressBar2.setVisibility(View.GONE);
            fetchJSONPopular(s);
        }

        private void fetchJSONPopular (String response) {
            try{
                JSONObject jsonObject = new JSONObject(response);
                JSONArray jsonArray = jsonObject.getJSONArray("results");

                for (int i = 0; i < 8; i++) {
                    JSONObject jsonObjectList = jsonArray.getJSONObject(i);
                    TvModel tv = new TvModel();

                    tv.setId(jsonObjectList.getInt("id"));
                    tv.setName(jsonObjectList.getString("name"));
//                    tv.setOverview(jsonObjectList.getString("overview"));
                    tv.setPosterPath(jsonObjectList.getString("poster_path"));
//                    tv.setBackdropPath(jsonObjectList.getString("backdrop_path"));
//                    tv.setFirstAirDate(jsonObjectList.getString("first_air_date"));
//                    tv.setVote(jsonObjectList.getDouble("vote_average"));
//                    tv.setVote_count(jsonObjectList.getDouble("vote_count"));
//                    tv.setPopularity(jsonObjectList.getDouble("popularity"));

                    /*
                     * Fetch Genre ID in Movie list with Genre list
                     */
//                    movieGenreList = new ArrayList<>();
//                    movieGenreNames = new ArrayList<>();
//                    JSONArray jsonArrayGenre = jsonObjectList.getJSONArray("genre_ids");
//                    for (int j = 0; j < jsonArrayGenre.length(); j++) {
//                        Integer genre_id = jsonArrayGenre.getInt(j);
//                        movieGenreList.add(genre_id);
//                    }
//
//                    for (Integer genreid : movieGenreList) {
//                        for (GenreModel list : genreList) {
//                            if (list.getId() == genreid) {
//                                movieGenreNames.add(list.getGenreName());
//                            }
//                        }
//                    }

//                    tv.setGenre_ids(movieGenreNames);
                    tvPopularList.add(tv);
                }
                tvPopularAdapter.notifyDataSetChanged();
            } catch (JSONException e) {
                Log.e("Error JSON", e.toString());
            }
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
                URL url = new URL(" https://api.themoviedb.org/3/genre/tv/list?api_key=9417ea2506327529d239284cd696078c&language=en-US");

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
            } catch (JSONException e) {
                Log.e("Error JSON", e.toString());
            }
        }
    }
}

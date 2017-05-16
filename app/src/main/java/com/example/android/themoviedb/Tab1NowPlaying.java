package com.example.android.themoviedb;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

import com.example.android.themoviedb.adapter.MovieAdapter;
import com.example.android.themoviedb.model.GenreModel;
import com.example.android.themoviedb.model.MovieModel;

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

import static android.content.ContentValues.TAG;

/**
 * Created by AriqD on 12/05/2017.
 */

public class Tab1NowPlaying extends Fragment{

    private RecyclerView rvMoviesData;
    private MovieAdapter movieAdapter;
    private RecyclerView.LayoutManager layoutManager;
    private List<GenreModel> genreList = new ArrayList<GenreModel>();
    private List<MovieModel> movieList = new ArrayList<MovieModel>();
    private List<Integer> movieGenreList;
    private List<String> movieGenreNames;
    private ProgressBar progressBar;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.tab1_nowplaying, container, false);
        rootView.setTag(TAG);

        rvMoviesData = (RecyclerView) rootView.findViewById(R.id.rv_movies_data);
        progressBar = (ProgressBar) rootView.findViewById(R.id.progress_bar);

        layoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false);
        rvMoviesData.setLayoutManager(layoutManager);

        movieAdapter = new MovieAdapter(getActivity(), movieList);
        rvMoviesData.setAdapter(movieAdapter);

        new FetchGenreData().execute();
        new FetchMovieData().execute();

        return rootView;
    }

    public class FetchMovieData extends AsyncTask<Void, Void, String> {

        @Override
        protected String doInBackground(Void... params) {
            HttpURLConnection httpURLConnection = null;
            BufferedReader bufferedReader = null;
            String strJSONMovie = "";

            try {
                // Call the API and set as URL
                String strUrl = "https://api.themoviedb.org/3/movie/now_playing" +
                        "?api_key=9417ea2506327529d239284cd696078c&language=en-US";
                URL url = new URL(strUrl);

                // Connect to the API
                httpURLConnection = (HttpURLConnection) url.openConnection();
                httpURLConnection.setRequestMethod("GET");
                httpURLConnection.connect();

                // Check if the Call is not null
                // if it's not null, put the results into StringBuffer
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
//            progressDialog.dismiss();
            progressBar.setVisibility(View.GONE);
            fetchJSONMovie(s);
//            tvMovieData.setText(s);
            Log.d("Cek Response", s);
        }

        private void fetchJSONMovie (String response) {
            try{
                JSONObject jsonObject = new JSONObject(response);
                JSONArray jsonArray = jsonObject.getJSONArray("results");

                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject jsonObjectList = jsonArray.getJSONObject(i);
                    movieGenreList = new ArrayList<>();
                    movieGenreNames = new ArrayList<>();

                    MovieModel movie = new MovieModel();

                    movie.setId(jsonObjectList.getInt("id"));
                    movie.setTitle(jsonObjectList.getString("title"));
                    movie.setOverview(jsonObjectList.getString("overview"));
                    movie.setVoteAverage(jsonObjectList.getDouble("vote_average"));
                    movie.setPosterPath(jsonObjectList.getString("poster_path"));
                    movie.setVoteCount(jsonObjectList.getInt("vote_count"));
                    movie.setOverview(jsonObjectList.getString("overview"));
                    movie.setReleaseDate(jsonObjectList.getString("release_date"));
                    movie.setBackdropPath(jsonObjectList.getString("backdrop_path"));

                    /*
                     * Fetch Genre ID in Movie list with Genre list
                     */
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

                    movie.setGenreList(movieGenreNames);

                    movieList.add(movie);
                }
                movieAdapter.notifyDataSetChanged();
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
            Log.d("Cek Genre", s);
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
                movieAdapter.notifyDataSetChanged();
            } catch (JSONException e) {
                Log.e("Error JSON", e.toString());
            }
        }
    }
}

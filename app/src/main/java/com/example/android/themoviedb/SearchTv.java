package com.example.android.themoviedb;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.android.themoviedb.adapter.search.SearchTvAdapter;
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

import static android.content.ContentValues.TAG;

/**
 * Created by AriqD on 24/05/2017.
 */

public class SearchTv extends Fragment {

    private String query;
    private SearchTvAdapter searchTvAdapter;
    private ProgressBar progressBarSearch;
    private List<TvModel> movieList = new ArrayList<>();
    private List<GenreModel> genreList = new ArrayList<>();
    private List<Integer> movieGenreList;
    private List<String> movieGenreNames;
    private TextView tvNoResults;

    public SearchTv (String query) {
        this.query = query;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_search, container, false);
        rootView.setTag(TAG);

        RecyclerView rvSearchMovie = (RecyclerView) rootView.findViewById(R.id.rv_search);
        progressBarSearch = (ProgressBar) rootView.findViewById(R.id.progress_bar);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
        rvSearchMovie.setLayoutManager(layoutManager);
        searchTvAdapter = new SearchTvAdapter(getActivity(), movieList);
        rvSearchMovie.setAdapter(searchTvAdapter);
        rvSearchMovie.setNestedScrollingEnabled(false);

        tvNoResults = (TextView) rootView.findViewById(R.id.tv_no_results);
//        tvNoResults.setVisibility(View.GONE);

        new FetchGenreData().execute();
        new FetchSearchTvData().execute();

        return rootView;
    }

    public class FetchSearchTvData extends AsyncTask<Void, Void, String> {

        @Override
        protected String doInBackground(Void... params) {
            HttpURLConnection httpURLConnection = null;
            BufferedReader bufferedReader = null;
            String strJSONMovie = "";

            try {
                String strUrl = "https://api.themoviedb.org/3/search/tv?" +
                        "api_key=9417ea2506327529d239284cd696078c&language=en-US&query=" + query +
                        "&page=1&include_adult=false";
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
        protected void onPreExecute() {
            super.onPreExecute();
            movieList.clear();
        }


        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            progressBarSearch.setVisibility(View.GONE);
            if (!s.isEmpty()) {
                fetchJSONMovie(s);
            }
        }

        private void fetchJSONMovie (String response) {
            try{
                JSONObject jsonObject = new JSONObject(response);
                JSONArray jsonArray = jsonObject.getJSONArray("results");

                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject jsonObjectList = jsonArray.getJSONObject(i);

                    TvModel movie = new TvModel();

                    movie.setId(jsonObjectList.getInt("id"));
                    movie.setName(jsonObjectList.getString("name"));
                    movie.setOverview(jsonObjectList.getString("overview"));
                    movie.setVote(jsonObjectList.getDouble("vote_average"));
                    movie.setOverview(jsonObjectList.getString("overview"));
                    movie.setFirstAirDate(jsonObjectList.getString("first_air_date"));
                    movie.setPosterPath(jsonObjectList.getString("poster_path"));
                    movie.setBackdropPath(jsonObjectList.getString("backdrop_path"));

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

                    movie.setGenre_ids(movieGenreNames);
                    movieList.add(movie);
                }
                searchTvAdapter.notifyDataSetChanged();
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
                searchTvAdapter.notifyDataSetChanged();
            } catch (JSONException e) {
                Log.e("Error JSON", e.toString());
            }
        }
    }
}

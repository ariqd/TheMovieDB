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

import com.example.android.themoviedb.adapter.PeopleAdapter;
import com.example.android.themoviedb.adapter.search.SearchPeopleAdapter;
import com.example.android.themoviedb.model.PeopleModel;

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

public class SearchPeople extends Fragment {

    private String query;
    private PeopleAdapter peopleAdapter;
    private ProgressBar progressBarPeople;
    private List<PeopleModel> peopleList = new ArrayList<>();
    private TextView tvNoResults;

    public SearchPeople(String query) {
        this.query = query;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_search, container, false);
        rootView.setTag(TAG);

//        Log.e("Query People", query);
        RecyclerView rvSearchPeople = (RecyclerView) rootView.findViewById(R.id.rv_search);
        progressBarPeople = (ProgressBar) rootView.findViewById(R.id.progress_bar);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
        rvSearchPeople.setLayoutManager(layoutManager);
        peopleAdapter = new PeopleAdapter(getActivity(), peopleList);
        rvSearchPeople.setAdapter(peopleAdapter);
        rvSearchPeople.setNestedScrollingEnabled(false);

        tvNoResults = (TextView) rootView.findViewById(R.id.tv_no_results);
//        tvNoResults.setVisibility(View.INVISIBLE);

        new FetchSearchPeopleData().execute();

        return rootView;
    }

    public class FetchSearchPeopleData extends AsyncTask<Void, Void, String> {

        @Override
        protected String doInBackground(Void... params) {
            HttpURLConnection httpURLConnection = null;
            BufferedReader bufferedReader = null;
            String strJSONMovie = "";

            try {
                String strUrl = "https://api.themoviedb.org/3/search/person?" +
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
            peopleList.clear();
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            progressBarPeople.setVisibility(View.GONE);
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

                    PeopleModel people = new PeopleModel();

                    people.setId(jsonObjectList.getInt("id"));
                    people.setName(jsonObjectList.getString("name"));
                    people.setPopularity(jsonObjectList.getDouble("popularity"));
                    people.setProfilePath(jsonObjectList.getString("profile_path"));
//                    if (!peopleList.isEmpty()){
//                        peopleList.clear();
//                    }
                    peopleList.clear();
                    peopleList.add(people);
                }
                peopleAdapter.notifyDataSetChanged();
            } catch (JSONException e) {
                Log.e("Error JSON", e.toString());
            }
        }
    }
}

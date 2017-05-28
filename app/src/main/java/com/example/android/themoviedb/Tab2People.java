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

import com.example.android.themoviedb.adapter.PeopleAdapter;
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

/**
 * Created by AriqD on 12/05/2017.
 */

public class Tab2People extends Fragment {

    private PeopleAdapter peopleAdapter;
    private ProgressBar progressBar;
    private List<PeopleModel> peopleList = new ArrayList<>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.tab2_people, container, false);

        RecyclerView rvPeople = (RecyclerView) rootView.findViewById(R.id.rv_people_popular);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getActivity());
        rvPeople.setLayoutManager(layoutManager);
        rvPeople.setNestedScrollingEnabled(false);
        peopleAdapter = new PeopleAdapter(getActivity(), peopleList);
        rvPeople.setAdapter(peopleAdapter);
        rvPeople.setFocusable(false);

        new FetchMostPopular().execute();

        return rootView;
    }

    public class FetchMostPopular extends AsyncTask<Void, Void, String> {

        @Override
        protected String doInBackground(Void... voids) {
            HttpURLConnection httpURLConnection = null;
            BufferedReader bufferedReader = null;
            String strJSONMovie = "";

            try {
                String strUrl = "https://api.themoviedb.org/3/person/popular" +
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
            fetchJSONPopular(s);
        }

        private void fetchJSONPopular (String response) {
            try{
                JSONObject jsonObject = new JSONObject(response);
                JSONArray jsonArray = jsonObject.getJSONArray("results");

                for (int i = 0; i < 20; i++) {
                    JSONObject jsonObjectList = jsonArray.getJSONObject(i);
                    PeopleModel people = new PeopleModel();
                    people.setId(jsonObjectList.getInt("id"));
                    people.setName(jsonObjectList.getString("name"));
                    people.setPopularity(jsonObjectList.getDouble("popularity"));
                    people.setProfilePath(jsonObjectList.getString("profile_path"));
                    peopleList.add(people);
                }
                peopleAdapter.notifyDataSetChanged();
            } catch (JSONException e) {
                Log.e("Error JSON", e.toString());
            }
        }
    }
}


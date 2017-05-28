package com.example.android.themoviedb;

import android.content.Intent;
import android.os.AsyncTask;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import android.widget.TextView;

import com.example.android.themoviedb.adapter.EpisodesAdapter;
import com.example.android.themoviedb.model.EpisodeModel;
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
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

public class EpisodesActivity extends AppCompatActivity {

    /**
     * The {@link android.support.v4.view.PagerAdapter} that will provide
     * fragments for each of the sections. We use a
     * {@link FragmentPagerAdapter} derivative, which will keep every
     * loaded fragment in memory. If this becomes too memory intensive, it
     * may be best to switch to a
     * {@link android.support.v4.app.FragmentStatePagerAdapter}.
     */
    private SectionsPagerAdapter mSectionsPagerAdapter;

    /**
     * The {@link ViewPager} that will host the section contents.
     */
    private ViewPager mViewPager;
    private Intent intent;
    private int count;
    private String tvId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_episodes);

        intent = getIntent();
        count = intent.getIntExtra("count", 0);
        tvId = intent.getStringExtra("id");

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle(intent.getStringExtra("title"));
        setSupportActionBar(toolbar);

        android.support.v7.app.ActionBar actionBar = getSupportActionBar();
        assert actionBar != null;
        actionBar.setDisplayHomeAsUpEnabled(true);

        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(mViewPager);
        tabLayout.setTabMode(TabLayout.MODE_SCROLLABLE);

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


//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        // Inflate the menu; this adds items to the action bar if it is present.
//        getMenuInflater().inflate(R.menu.menu_episodes, menu);
//        return true;
//    }
//
//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//        // Handle action bar item clicks here. The action bar will
//        // automatically handle clicks on the Home/Up button, so long
//        // as you specify a parent activity in AndroidManifest.xml.
//        int id = item.getItemId();
//
//        //noinspection SimplifiableIfStatement
//        if (id == R.id.action_settings) {
//            return true;
//        }
//
//        return super.onOptionsItemSelected(item);
//    }

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment {
        /**
         * The fragment argument representing the section number for this
         * fragment.
         */
        private static final String ARG_SECTION_NUMBER = "section_number";
        private String id;
        private List<EpisodeModel> episodesList = new ArrayList<>();
        private EpisodesAdapter episodesAdapter;

        public PlaceholderFragment(String id) {
            this.id = id;
        }

        /**
         * Returns a new instance of this fragment for the given section
         * number.
         */
        public PlaceholderFragment newInstance(int sectionNumber) {
            PlaceholderFragment fragment = new PlaceholderFragment(id);
            Bundle args = new Bundle();
            args.putInt(ARG_SECTION_NUMBER, sectionNumber);
            fragment.setArguments(args);

            return fragment;
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_episodes, container, false);

            RecyclerView rvEpisodes = (RecyclerView) rootView.findViewById(R.id.rv_episodes);
            RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getActivity());
            rvEpisodes.setLayoutManager(layoutManager);
            episodesAdapter = new EpisodesAdapter(getActivity(), episodesList);
            rvEpisodes.setAdapter(episodesAdapter);
            rvEpisodes.setNestedScrollingEnabled(false);
            rvEpisodes.setFocusable(false);

            new FetchSeasonDetails().execute();

            return rootView;
        }

        public class FetchSeasonDetails extends AsyncTask<Void, Void, String> {

            @Override
            protected String doInBackground(Void... voids) {
                HttpURLConnection httpURLConnection;
                BufferedReader bufferedReader;
                String strJSONMovie;

                try {
                    String strUrl = "https://api.themoviedb.org/3/tv/" +
                             id + "/season/" + getArguments().getInt(ARG_SECTION_NUMBER) + "?api_key=9417ea2506327529d239284cd696078c";
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
                fetchJSONEpisodes(s);
            }

            private void fetchJSONEpisodes (String response) {
                try{
                    JSONObject jsonObject = new JSONObject(response);
                    JSONArray jsonArrayEpisodes = jsonObject.getJSONArray("episodes");

                    for (int i = 0; i < jsonArrayEpisodes.length(); i++) {
                        JSONObject jsonObjectEpisodes = jsonArrayEpisodes.getJSONObject(i);
                        EpisodeModel episode = new EpisodeModel();

                        episode.setId(jsonObjectEpisodes.getInt("id"));
                        episode.setName(jsonObjectEpisodes.getString("name"));
                        episode.setOverview(jsonObjectEpisodes.getString("overview"));
                        episode.setAir_date(jsonObjectEpisodes.getString("air_date"));
                        episode.setStillPath(jsonObjectEpisodes.getString("still_path"));
                        episode.setVote(jsonObjectEpisodes.getDouble("vote_average"));
                        episode.setEpisodeNumber(jsonObjectEpisodes.getInt("episode_number"));

                        episodesList.add(episode);
                    }
                    episodesAdapter.notifyDataSetChanged();
                } catch (JSONException e) {
                    Log.e("Error JSON", e.toString());
                }
            }
        }
    }

    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            // getItem is called to instantiate the fragment for the given page.
            // Return a PlaceholderFragment (defined as a static inner class below).
            PlaceholderFragment placeholderFragment = new PlaceholderFragment(tvId);
            return placeholderFragment.newInstance(position + 1);
        }

        @Override
        public int getCount() {
            // Show n total pages.
            return count;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            int num = 1;
            for (int i = 0; i < count; i++) {
                if (position == i) {
                    return "Season "+ num;
                }
                num++;
            }
            return null;
        }
    }
}

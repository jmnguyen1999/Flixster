package com.example.flixster.activities;
/**
 * FlixsterActivity.java
 * Purpose:                This is where the main Flixster screen resides. Makes network requests to "themoviedb.org" API, via MovieDBClient class, to access Now Playing movies and Configuration image sizes.Then, displays this data through a RecyclerView and created classes: "Movie.java" and "MovieAdapter.java"
 *
 * Used:                            MovieDBClient.java, MovieAdapter.java, Movie.java
 * Corresponding Layout file:       "activity_flixster.xml"
 *
 * @author Josephine Mai Nguyen
 * @version 2.0
 */

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.util.Log;

import com.codepath.asynchttpclient.AsyncHttpClient;
import com.codepath.asynchttpclient.callback.JsonHttpResponseHandler;
import com.example.flixster.R;
import com.example.flixster.adapters.MovieAdapter;
import com.example.flixster.models.Movie;
import com.example.flixster.network.MovieDBClient;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import okhttp3.Headers;

public class FlixsterActivity extends AppCompatActivity {
    //Constants/Fields:
    private static final String TAG = "FlixsterActivity";           //For Log.d statements

    List<Movie> movies;
    MovieDBClient client;
    RecyclerView rvMovies;
    MovieAdapter movieAdapter;
    String movieBackdropSize;
    String moviePosterSize;

    @Override
    /**
     * Purpose: Called when the app is ran. Attaches to "activity_flixster.xml" and obtains/sets up RecyclerView. Initializes movies List
     */
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_flixster);

        movies = new ArrayList<>();

        //1.) Set up RecyclerView: Create and bind MovieAdapter and LayoutManager
        rvMovies = findViewById(R.id.rvMovies);
        movieAdapter = new MovieAdapter(this, movies);          //note: movies[] will be updated later
        rvMovies.setAdapter(movieAdapter);
        rvMovies.setLayoutManager(new LinearLayoutManager(this));

        //2.) Obtains now playing movies, updates movies List appropriately, and notifies movieAdapter:
        fetchMovies();
    }

    /**
     * Purpose:     Makes configuration network request via MovieDBClient class to initialize fields: : "posterPathSize" and "backdropPathSize". Gets available movie image sizes and chooses the median size for the intialization.
     */
    private void fetchConfiguration(){
        client = new MovieDBClient();
        client.makeConfigurationRequest(new JsonHttpResponseHandler() {
            @Override
            //Initializes "movieBackdropSize" and "moviePosterSize" on success response:
            public void onSuccess(int i, Headers headers, JSON json) {
                Log.d(TAG, "getMovies(): onSuccess()-get(CONFIGURATION_URL)");
                JSONObject jsonObject = json.jsonObject;
                try {
                    JSONObject images = jsonObject.getJSONObject("images");
                    JSONArray backdropSizes = images.getJSONArray("backdrop_sizes");
                    JSONArray posterSizes = images.getJSONArray("poster_sizes");

                    movieBackdropSize = backdropSizes.get(backdropSizes.length()/2).toString();     //all sizes are sorted --> choose middle index
                    moviePosterSize = posterSizes.get(posterSizes.length()/2).toString();
                } catch (JSONException e) {
                    Log.e(TAG, "getMovies(): Hit JSON exception - onSuccess(): get(CONFIGURATION_URL)", e);
                    e.printStackTrace();
                }
            }
            @Override
            public void onFailure(int i, Headers headers, String s, Throwable throwable) {
                Log.d(TAG, "getMovies(): onFailure() - get(CONFIGURATION_URL)");
            }
        });
    }


    /**
     * Purpose:         Makes network request via MovieDBClient to get Now Playing movies. Creates list of Movies objects with image sizes chosen from fetchConfiguration(), and updates RecyclerView "movieAdpater" field
     */
    private void fetchMovies(){
        //1.) Get available movie image sizes and choose the "posterPathSize" and "backdropPathSize" to use when initializing Movie objects later
        fetchConfiguration();

        //2.) Make network request to get now playing movies, initialize "movies" List, and update adapter:
        client = new MovieDBClient();
        client.makeNowPlayingRequest(new JsonHttpResponseHandler(){
            @Override
            public void onSuccess(int i, Headers headers, JSON json) {
                Log.d(TAG, "fetchMovies(): onSuccess()");
                JSONObject jsonObject = json.jsonObject;
                try {
                    JSONArray results = jsonObject.getJSONArray("results");
                    Log.i(TAG, "Results: " + results.toString());

                    movies.addAll(Movie.jsonArrayToList(results, moviePosterSize, movieBackdropSize));      //initializes every movie to same poster and backdrop size
                    Log.i(TAG, "Movies List: " + movies.toString());
                    Log.i(TAG, "Movies: " + movies.size());
                    movieAdapter.notifyDataSetChanged();

                } catch (JSONException e) {
                    Log.e(TAG, "fetchMovies(): Hit JSON exception - onSuccess()", e);
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int i, Headers headers, String s, Throwable throwable) {
                Log.d(TAG, "fetchMovies(): onFailure()");
            }
        });
    }
}
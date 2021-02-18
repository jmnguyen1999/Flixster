package com.example.flixster.activities;
/**
 * MainActivity.java
 * Purpose: This is where the main Flixster screen resides. Makes network requests to "themoviedb.org" API to access Now Playing movies and Configuration image sizes.
 *          Then, displays this data through a RecyclerView and created classes: "Movie.java" and "MovieAdapter.java"
 *
 * Corresponding Layout file: "activity_main.xml"
 *
 * @author Josephine Mai Nguyen
 * @version 1.0
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
    public static final String NOW_PLAYING_URL = "https://api.themoviedb.org/3/movie/now_playing?api_key=a07e22bc18f5cb106bfe4cc1f83ad8ed";
    public static final String CONFIGURATION_URL = "https://api.themoviedb.org/3/configuration?api_key=a07e22bc18f5cb106bfe4cc1f83ad8ed";
    private static final String TAG = "FlixsterActivity";        //For Log.d statements

    List<Movie> movies;
    MovieDBClient client;
    RecyclerView rvMovies;
    MovieAdapter movieAdapter;
    String movieBackdropSize;
    String moviePosterSize;

    @Override
    /**onCreate()
     * Purpose: Called when the app is ran. Attaches to "activity_main.xml" and obtains/sets up RecyclerView. Makes network requests using AsyncHttpClient, obtains
     *          JSONArray of now playing movies, and JSONArrays of available backdrop and poster image sizes, and uses data to initialize fields.
     */
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_flixster);

        movies = new ArrayList<>();

        //1.) Set up RecyclerView: Create and bind MovieAdapter and LayoutManager
        rvMovies = findViewById(R.id.rvMovies);
        movieAdapter = new MovieAdapter(this, movies);         //note: movies[] will be updated later
        rvMovies.setAdapter(movieAdapter);
        rvMovies.setLayoutManager(new LinearLayoutManager(this));

        //2.) Obtain now playing movies and update movieAdapter with movies List
        fetchMovies();
        //2.)Create MovieDBClient to: (1) Get and choose movie images sizes, (2) Get now-playing movies, (3) Create a list of Movie objects with found info, and (4)Notifies movieAdapter of updated model

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
     * Purpose:         Makes network requst via MovieDBClient to get Now Playing movies. Creates list of Movies objects with image sizes chosen from fetchConfiguration(), and updates RecyclerView "movieAdpater" field
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

                    movies.addAll(Movie.jsonArrayToList(results, moviePosterSize, movieBackdropSize));//initializes every movie to same poster and backdrop size
                    // Log.i(TAG, "Movies List: " + movies.toString());
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
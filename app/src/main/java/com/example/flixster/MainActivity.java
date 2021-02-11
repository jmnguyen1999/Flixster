package com.example.flixster;
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
import com.example.flixster.adapters.MovieAdapter;
import com.example.flixster.models.Movie;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import okhttp3.Headers;

public class MainActivity extends AppCompatActivity {
    //Constants/Fields:
    public static final String NOW_PLAYING_URL = "https://api.themoviedb.org/3/movie/now_playing?api_key=a07e22bc18f5cb106bfe4cc1f83ad8ed";
    public static final String CONFIGURATION_URL = "https://api.themoviedb.org/3/configuration?api_key=a07e22bc18f5cb106bfe4cc1f83ad8ed";
    public static final String TAG = "MainActivity";        //For Log.d statements

    List<Movie> movies;
    String movieBackdropSize;
    String moviePosterSize;

    @Override
    /**onCreate()
     * Purpose: Called when the app is ran. Attaches to "activity_main.xml" and obtains/sets up RecyclerView. Makes network requests using AsyncHttpClient, obtains
     *          JSONArray of now playing movies, and JSONArrays of available backdrop and poster image sizes, and uses data to initialize fields.
     */
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        movies = new ArrayList<>();

        //1.) Set up RecyclerView: Create and bind MovieAdapter and LayoutManager
        RecyclerView rvMovies = findViewById(R.id.rvMovies);
        MovieAdapter movieAdapter = new MovieAdapter(this, movies);         //note: movies[] will be updated later
        rvMovies.setAdapter(movieAdapter);
        rvMovies.setLayoutManager(new LinearLayoutManager(this));

        //2.)Create client and execute network requests to API
        AsyncHttpClient client = new AsyncHttpClient();

        //2a.) Get available images sizes and choose the median size as each movie's "posterPathSize" and "backdropPathSize":
        client.get(CONFIGURATION_URL, new JsonHttpResponseHandler() {
            @Override
            //Initializes "movieBackdropSize" and "moviePosterSize" on success response:
            public void onSuccess(int i, Headers headers, JSON json) {
                Log.d(TAG, "onSuccess(): get(CONFIGURATION_URL)");
                JSONObject jsonObject = json.jsonObject;
                try {
                    JSONObject images = jsonObject.getJSONObject("images");
                    JSONArray backdropSizes = images.getJSONArray("backdrop_sizes");
                    JSONArray posterSizes = images.getJSONArray("poster_sizes");

                    movieBackdropSize = backdropSizes.get(backdropSizes.length()/2).toString();     //all sizes are sorted --> choose middle index
                    moviePosterSize = posterSizes.get(posterSizes.length()/2).toString();
                } catch (JSONException e) {
                    Log.e(TAG, "Hit JSON exception - onSuccess(): get(CONFIGURATION_URL)", e);
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int i, Headers headers, String s, Throwable throwable) {
                Log.d(TAG, "onFailure(): get(CONFIGURATION_URL)");
            }
        });

        //2b.) Get and save now playing movies into "movies" List
        client.get(NOW_PLAYING_URL, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int i, Headers headers, JSON json) {
                Log.d(TAG, "onSuccess(): get(NOW_PLAYING_URL");
                JSONObject jsonObject = json.jsonObject;
                try {
                    JSONArray results = jsonObject.getJSONArray("results");
                    Log.i(TAG, "Results: " + results.toString());

                    movies.addAll(Movie.jsonArrayToList(results, moviePosterSize, movieBackdropSize));      //initializes every movie to same poster and backdrop size
                    movieAdapter.notifyDataSetChanged();

                    Log.i(TAG, "Movies: " + movies.size());
                } catch (JSONException e) {
                    Log.e(TAG, "Hit JSON exception - onSuccess(): get(NOW_PLAYING_URL)", e);
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int i, Headers headers, String s, Throwable throwable) {
                Log.d(TAG, "onFailure() : get(NOW_PLAYING_URL)");
            }
        });

    }
}
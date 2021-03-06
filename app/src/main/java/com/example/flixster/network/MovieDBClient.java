package com.example.flixster.network;
/**
 * MovieDBClient.java
 * Purpose:                 A seperate class to make all network requests. Makes requests to "themoviedb.org"
 *
 * Used:                            None
 * Corresponding Layout file:       None
 *
 * @author Josephine Mai Nguyen
 * @version 1.0
 */
import android.util.Log;

import com.codepath.asynchttpclient.AsyncHttpClient;
import com.codepath.asynchttpclient.callback.JsonHttpResponseHandler;
import com.example.flixster.adapters.MovieAdapter;
import com.example.flixster.models.Movie;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import okhttp3.Headers;

public class MovieDBClient {
    public static final String NOW_PLAYING_URL = "https://api.themoviedb.org/3/movie/now_playing?api_key=a07e22bc18f5cb106bfe4cc1f83ad8ed";
    public static final String CONFIGURATION_URL = "https://api.themoviedb.org/3/configuration?api_key=a07e22bc18f5cb106bfe4cc1f83ad8ed";
    public static final String VIDEO_URL = "https://api.themoviedb.org/3/movie/%d/videos?api_key=a07e22bc18f5cb106bfe4cc1f83ad8ed";
    public static final String TAG = "MovieDBClient";

    private AsyncHttpClient client;

    public MovieDBClient(){
        client = new AsyncHttpClient();
    }

    public void makeConfigurationRequest(JsonHttpResponseHandler handler){
        client.get(CONFIGURATION_URL, handler);
    }

    public void makeNowPlayingRequest(JsonHttpResponseHandler handler){
        client.get(NOW_PLAYING_URL, handler);
    }

    public void makeTrailerRequest(int id, JsonHttpResponseHandler handler){
        client.get(String.format(VIDEO_URL, id), handler);
    }

}

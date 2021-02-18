package com.example.flixster.activities;

import android.os.Bundle;
import android.util.Log;
import android.widget.RatingBar;
import android.widget.TextView;

import com.codepath.asynchttpclient.AsyncHttpClient;
import com.codepath.asynchttpclient.callback.JsonHttpResponseHandler;
import com.example.flixster.R;
import com.example.flixster.models.Movie;
import com.example.flixster.network.MovieDBClient;
import com.google.android.youtube.player.YouTubeBaseActivity;
import com.google.android.youtube.player.YouTubeInitializationResult;
import com.google.android.youtube.player.YouTubePlayer;
import com.google.android.youtube.player.YouTubePlayerView;

import org.json.JSONArray;
import org.json.JSONException;
import org.parceler.Parcels;

import okhttp3.Headers;

public class DetailsActivity extends YouTubeBaseActivity {

    private static final String YOUTUBE_API_KEY = "AIzaSyD0CkaPZjI5CTA5eAJik0ieuXxwzbr1MTA";
    public static final String VIDEO_URL = "https://api.themoviedb.org/3/movie/%d/videos?api_key=a07e22bc18f5cb106bfe4cc1f83ad8ed";
    private static final String TAG = "DetailsActivity";
    private static final int trailerIndex = 0;          //always play the first trailer available
    TextView tvTitle;
    TextView tvOverview;
    RatingBar ratingBar;
    YouTubePlayerView ypvVideoPlayer;
    MovieDBClient client;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);
        tvTitle = findViewById(R.id.tvTitle);
        tvOverview = findViewById(R.id.tvOverview);
        ratingBar= findViewById(R.id.ratingBar);
        ypvVideoPlayer = findViewById(R.id.ypvVideoPlayer);

        Movie movie = Parcels.unwrap(getIntent().getParcelableExtra("movie"));
        tvTitle.setText(movie.getTitle());
        tvOverview.setText(movie.getOverview());
        ratingBar.setRating((float) movie.getRating());

        fetchTrailer(movie);
    }

    /**
     * Purpose:         Executes network request through MovieDVClient to obtain the first trailer of the given movie. Then, ties the ypvVideoPlayer to play the trailer when clicked on. If no trailer can be played (no trailers available, or not playable by YouTube),
     * @param movie:    the movie wanted to inquire trailers for
     */
    private void fetchTrailer(Movie movie){
        client = new MovieDBClient();
        //1.) Make request to get available trailers using the movie id:
        client.makeTrailerRequest(movie.getMovieId(), new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int i, Headers headers, JSON json) {
                Log.d(TAG, "makeTrailerRequest(" + String.valueOf(movie.getMovieId()) + ") : onSuccess()");
                try {
                    JSONArray results = json.jsonObject.getJSONArray(("results"));

                    //1.) Check if there is a YouTube video can choose: not empty, and playable by YouTube
                    if(results.length() > 0){
                        String siteName = results.getJSONObject(trailerIndex).getString("site");
                        if(siteName.equals("YouTube")){
                            //2.) Get the key/endpoint of the YouTube video:
                            String youtubeKey = results.getJSONObject(trailerIndex).getString("key");
                            Log.d(TAG, "onSuccess(): youtubeKey = " + youtubeKey);
                            initializeYoutube(youtubeKey);
                        }
                    }
                    else{ //Display movie image?

                    }
                } catch (JSONException e) {
                    Log.e(TAG, "onSuccess() - Error parsing JSON");
                    e.printStackTrace();
                }
            }
            @Override
            public void onFailure(int i, Headers headers, String s, Throwable throwable) {
                Log.d(TAG, "makeTrailerRequest(" + String.valueOf(movie.getMovieId()) + ") : onFailure()");
            }
        });
    }


    /**
     * Purpose:     Initializes ypvVideoPlayer and sets an onInitializedListener() to play the YouTube video corresponding to the given Youtube URL endpoint.
     * @param youtubeKey:   the endpoint of a Youtube URL
     */
    private void initializeYoutube(String youtubeKey) {
        ypvVideoPlayer.initialize(YOUTUBE_API_KEY, new YouTubePlayer.OnInitializedListener() {
            @Override
            public void onInitializationSuccess(YouTubePlayer.Provider provider, YouTubePlayer youTubePlayer, boolean b) {
                Log.d(TAG, "initializeYoutube(): onInitializationSuccess()");
                youTubePlayer.cueVideo(youtubeKey);
            }

            @Override
            public void onInitializationFailure(YouTubePlayer.Provider provider, YouTubeInitializationResult youTubeInitializationResult) {
                Log.d(TAG, "initializeYoutube(): onInitializationFailure()");
            }
        });
    }
}
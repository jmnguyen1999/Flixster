package com.example.flixster.activities;
/**
 * MovieTrailerActivity.java
 * Purpose:             To display a given movie trailer endpoint through the YoutubePlayerView using a Youtube API key. By default: (1) opens up fullscreen and begins playing, (2) Exiting fullscreen --> exits activity
 *
 * Used:                            DetailsActivity.java
 * Corresponding Layout file:       "activity_movie_trailer.xml"
 *
 * @author Josephine Mai Nguyen
 * @version 1.0
 */
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.example.flixster.R;
import com.example.flixster.models.Movie;
import com.example.flixster.network.MovieDBClient;
import com.google.android.youtube.player.YouTubeBaseActivity;
import com.google.android.youtube.player.YouTubeInitializationResult;
import com.google.android.youtube.player.YouTubePlayer;
import com.google.android.youtube.player.YouTubePlayerView;

import org.parceler.Parcels;

public class MovieTrailerActivity extends YouTubeBaseActivity {
    private static final String YOUTUBE_API_KEY = "AIzaSyD0CkaPZjI5CTA5eAJik0ieuXxwzbr1MTA";
    private static final String YOUTUBE_ENDPOINT_KEY = "youtubeKey";
    private static final String TAG = "MovieTrailerActivity";           //for Log() statements

    YouTubePlayerView ypvVideoPlayer;
    String youtubeKey;

    @Override
    /**
     * Purpose: Called when the activity is run. Attaches to "activity_movie_trailer.xml", obtains movie trailer endpoint through Intents
     */
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_trailer);
        ypvVideoPlayer = findViewById(R.id.ypvVideoPlayer);

        //Get youtube trailer endpoint:
        youtubeKey = getIntent().getStringExtra(YOUTUBE_ENDPOINT_KEY);

        initializeYoutube();
    }

    /**
     * Purpose:         Initializes the YoutubePlayerView to open to fullscreen, automatically play video, and create an OnFullscreenListener() to exit the activity when user exits fullscreen.
     */
    private void initializeYoutube(){
        ypvVideoPlayer.initialize(YOUTUBE_API_KEY, new YouTubePlayer.OnInitializedListener() {
            @Override
            public void onInitializationSuccess(YouTubePlayer.Provider provider, YouTubePlayer youTubePlayer, boolean b) {
                Log.d(TAG, "initializeYoutube(): onInitializationSuccess()");
                youTubePlayer.setFullscreen(true);
                youTubePlayer.loadVideo(youtubeKey);

                //Exits from activity once user exits full screen:
                youTubePlayer.setOnFullscreenListener(new YouTubePlayer.OnFullscreenListener() {
                    @Override
                    public void onFullscreen(boolean b) {
                        if(!b){
                            finish();
                        }
                    }
                });
            }

            @Override
            public void onInitializationFailure(YouTubePlayer.Provider provider, YouTubeInitializationResult youTubeInitializationResult) {
                Log.d(TAG, "initializeYoutube(): onInitializationFailure()");
            }
        });
    }

}
package com.example.flixster.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;

import com.example.flixster.R;
import com.example.flixster.models.Movie;
import com.google.android.youtube.player.YouTubeBaseActivity;
import com.google.android.youtube.player.YouTubeInitializationResult;
import com.google.android.youtube.player.YouTubePlayer;
import com.google.android.youtube.player.YouTubePlayerView;

import org.parceler.Parcels;

public class MovieTrailerActivity extends YouTubeBaseActivity {
    private static final String YOUTUBE_API_KEY = "AIzaSyD0CkaPZjI5CTA5eAJik0ieuXxwzbr1MTA";
    private static final String YOUTUBE_ENDPOINT_KEY = "youtubeKey";
    private static final String MOVIE_KEY = "movie";
    private static final String TAG = "MovieTrailerActivity";

    YouTubePlayerView ypvVideoPlayer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_trailer);

        ypvVideoPlayer = findViewById(R.id.ypvVideoPlayer);

        String youtubeKey = getIntent().getStringExtra(YOUTUBE_ENDPOINT_KEY);

        ypvVideoPlayer.initialize(YOUTUBE_API_KEY, new YouTubePlayer.OnInitializedListener() {
            @Override
            public void onInitializationSuccess(YouTubePlayer.Provider provider, YouTubePlayer youTubePlayer, boolean b) {
                Log.d(TAG, "initializeYoutube(): onInitializationSuccess()");
                youTubePlayer.setFullscreen(true);
                youTubePlayer.cueVideo(youtubeKey);

                //Exits from activity once exit full screen:
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
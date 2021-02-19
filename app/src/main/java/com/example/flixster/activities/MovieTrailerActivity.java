package com.example.flixster.activities;

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
/**
 * Purpose:     Initializes ypvVideoPlayer and sets an onInitializedListener() to play the YouTube video corresponding to the given Youtube URL endpoint.
 */
public class MovieTrailerActivity extends YouTubeBaseActivity {
    private static final String YOUTUBE_API_KEY = "AIzaSyD0CkaPZjI5CTA5eAJik0ieuXxwzbr1MTA";
    private static final String YOUTUBE_ENDPOINT_KEY = "youtubeKey";
    private static final String MOVIE_KEY = "movie";
    private static final String TAG = "MovieTrailerActivity";

    YouTubePlayerView ypvVideoPlayer;
    String youtubeKey;
    boolean autoplay;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_trailer);
        ypvVideoPlayer = findViewById(R.id.ypvVideoPlayer);

        //Get values from DetailsActivity:
        Movie movie = Parcels.unwrap(getIntent().getParcelableExtra(MOVIE_KEY));
        youtubeKey = getIntent().getStringExtra(YOUTUBE_ENDPOINT_KEY);

        initializeYoutube();
    }

    private void initializeYoutube(){
        ypvVideoPlayer.initialize(YOUTUBE_API_KEY, new YouTubePlayer.OnInitializedListener() {
            @Override
            public void onInitializationSuccess(YouTubePlayer.Provider provider, YouTubePlayer youTubePlayer, boolean b) {
                Log.d(TAG, "initializeYoutube(): onInitializationSuccess()");
                youTubePlayer.setFullscreen(true);
                youTubePlayer.loadVideo(youtubeKey);

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
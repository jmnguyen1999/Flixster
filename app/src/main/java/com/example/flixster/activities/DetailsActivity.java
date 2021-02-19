package com.example.flixster.activities;
/**
 * DetailsActivity.java
 * Purpose:               The details screen called when a movie is selected from the "FlixsterActivity". Displays the title, overview, and rating of a given movie. Also starts MovieTrailerActivity when play button icon is clicked to display YouTube trailer.
 *
 * Used:                            MovieDBClient.java, Movie.java, MovieTrailerActivity.java
 * Corresponding Layout file:       "activity_details.xml"
 *
 * @author Josephine Mai Nguyen
 * @version 1.0
 */
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.codepath.asynchttpclient.callback.JsonHttpResponseHandler;
import com.example.flixster.R;
import com.example.flixster.models.Movie;
import com.example.flixster.network.MovieDBClient;
import com.google.android.youtube.player.YouTubePlayerView;

import org.json.JSONArray;
import org.json.JSONException;
import org.parceler.Parcels;

import okhttp3.Headers;

public class DetailsActivity extends AppCompatActivity {
    private static final String TAG = "DetailsActivity";        //for Log() statements
    private static final int TRAILER_INDEX = 0;             //always play the first trailer available
    private static final String MOVIE_KEY = "movie";

    TextView tvTitle;
    TextView tvOverview;
    RatingBar ratingBar;
    ImageView ivPoster;
    ImageView ivPlayBttn;

    MovieDBClient client;       //For making network calls for youtube trailer endpoints
    String youtubeKey;

    @Override
    /**
     * Purpose: Called when the activity is run. Attaches to "activity_details.xml", displays updated movie information, sets up a listener to MovieTrailerActivity{}
     */
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);

        //Initializations:
        tvTitle = findViewById(R.id.tvTitle);
        tvOverview = findViewById(R.id.tvOverview);
        ratingBar= findViewById(R.id.ratingBar);
        ivPoster = findViewById(R.id.ivPoster);
        ivPlayBttn = findViewById(R.id.ivPlayBttn);

        //Display Views:
        Movie movie = Parcels.unwrap(getIntent().getParcelableExtra(MOVIE_KEY));
        tvTitle.setText(movie.getTitle());
        tvOverview.setText(movie.getOverview());
        ratingBar.setRating((float) movie.getRating());
        Glide.with(this).load(movie.getBackdropPath()).into(ivPoster);

        //Get youtube trailer endpoint:  updates "youtubeKey"
        fetchTrailer(movie);
        Log.i(TAG, "onCreate(): youtubeKey=" + youtubeKey);

        //Display Play button and attach listener:      starts MovieTrailerActivity{}
        ivPlayBttn.setImageResource(R.drawable.play_bttn);
        ivPlayBttn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(DetailsActivity.this, MovieTrailerActivity.class);
                intent.putExtra("youtubeKey", youtubeKey);
                startActivity(intent);
            }
        });
    }

    /**
     * Purpose:         Executes network request through MovieDVClient to obtain the endpoint of the first trailer of the given movie. If no trailer can be played (no trailers available, or not playable by YouTube), display a pop up message using Toast.
     * @param movie:    the movie wanted to inquire trailers for
     */
    public void fetchTrailer(Movie movie){
        client = new MovieDBClient();

        //1.) Make request to get available trailers using the movie id:
        client.makeTrailerRequest(movie.getMovieId(), new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int i, Headers headers, JSON json) {
                Log.d(TAG, "makeTrailerRequest(" + String.valueOf(movie.getMovieId()) + ") : onSuccess()");
                try {
                    JSONArray results = json.jsonObject.getJSONArray(("results"));

                    //1.) Check if there is a YouTube video can choose:     not empty, and playable by YouTube
                    if(results.length() > 0){
                        String siteName = results.getJSONObject(TRAILER_INDEX).getString("site");
                        if(siteName.equals("YouTube")){

                            //2.) Get the key/endpoint of the YouTube video:
                            youtubeKey = results.getJSONObject(TRAILER_INDEX).getString("key");
                            Log.d(TAG, "onSuccess(): youtubeKey = " + youtubeKey);
                        }
                    }
                    else{ //Pop up message
                        Toast.makeText(getApplicationContext(), "Video cannot be played.", Toast.LENGTH_SHORT).show();
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

}
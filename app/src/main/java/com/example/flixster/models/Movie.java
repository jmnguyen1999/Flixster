package com.example.flixster.models;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class Movie {
    String posterPath;
    String title;
    String overview;
    String backdropPath;
    String posterPathSize;
    String backdropPathSize;

    public Movie(JSONObject jsonObject, String posterPathSize, String backdropPathSize) throws JSONException {
        backdropPath = jsonObject.getString("backdrop_path");
        posterPath = jsonObject.getString("poster_path");
        title = jsonObject.getString("title");
        overview =jsonObject.getString("overview");
        this.posterPathSize = posterPathSize;
        this.backdropPathSize = backdropPathSize;
    }

    //Obtains each object in the movie array and inputs into a List of Movies
    public static List<Movie> jsonArrayToList(JSONArray moviesArray, String posterPathSize, String backdropPathSize) throws JSONException {
        List<Movie> movies = new ArrayList<>();
        for(int i = 0; i < moviesArray.length(); i++){
            movies.add(new Movie(moviesArray.getJSONObject(i), posterPathSize, backdropPathSize));
        }
        return movies;
    }

    //This appends the beginning of the url to the posterPath, hardcodes a width of 342 --> need to fetch which widths are avail and append that instead
    public String getPosterPath() {
        return String.format("https://image.tmdb.org/t/p/%s/%s", posterPathSize, posterPath);
    }
    public String getTitle() {
        return title;
    }
    public String getBackdropPath(){
        return String.format("https://image.tmdb.org/t/p/%s/%s", backdropPathSize, backdropPath);
    }
    public String getOverview() {
        return overview;
    }
}

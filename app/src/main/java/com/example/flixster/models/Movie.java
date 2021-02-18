package com.example.flixster.models;
/**
 * Movie.java
 * Purpose:     To contain all the needed Movie data in an object. Contains the movie's: posterpath, title, overview, and backdrop. Also saves which backdrop
 *              and posterpath image sizes to use when displaying. Class also has a "jsonArrayToList()" which can be used objectively without an instantiation
 *              of Movie{} at all.
 *
 * Used by: MainActivity.java
 *
 * @author Josephine Mai Nguyen
 * @version 1.0
 */
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.parceler.Parcel;

import java.util.ArrayList;
import java.util.List;

@Parcel
public class Movie {
    //Fields:
    private int movieId;
    private String posterPath;
    private String title;
    private String overview;
    private String backdropPath;
    private String posterPathSize;
    private String backdropPathSize;
    private double rating;

    public Movie(){}
    /**
     * Movie()
     * Purpose:     Constructor. Needs a JSON object that contains all the data for fields (a movie) and image sizes.
     */
    public Movie(JSONObject jsonObject, String posterPathSize, String backdropPathSize) throws JSONException {
        backdropPath = jsonObject.getString("backdrop_path");
        posterPath = jsonObject.getString("poster_path");
        title = jsonObject.getString("title");
        overview =jsonObject.getString("overview");
        rating = jsonObject.getDouble("vote_average");
        movieId = jsonObject.getInt("id");

        this.posterPathSize = posterPathSize;
        this.backdropPathSize = backdropPathSize;
    }

    /**
     * jsonArrayToList()
     * Purpose:     Obtains each object in a given JSONArray of movies and inputs into a List of Movie objects. Then returns the List.
     */
    public static List<Movie> jsonArrayToList(JSONArray moviesArray, String posterPathSize, String backdropPathSize) throws JSONException {
        List<Movie> movies = new ArrayList<>();
        for(int i = 0; i < moviesArray.length(); i++){
            movies.add(new Movie(moviesArray.getJSONObject(i), posterPathSize, backdropPathSize));
        }
        return movies;
    }


    //Getter methods - self-explanatory:

    //NOTE: URL structure: base URL + imagesize + '/' + imagepath
    public String getPosterPath() {
        return String.format("https://image.tmdb.org/t/p/%s/%s", posterPathSize, posterPath);
    }
    public String getBackdropPath(){
        return String.format("https://image.tmdb.org/t/p/%s/%s", backdropPathSize, backdropPath);
    }
    public String getTitle() {
        return title;
    }
    public String getOverview() {
        return overview;
    }
    public String getBackdropPathSize() {
        return backdropPathSize;
    }
    public String getPosterPathSize() {
        return posterPathSize;
    }
    public double getRating() { return rating; }
    public int getMovieId(){return movieId;}
}

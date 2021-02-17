package com.example.flixster.adapters;
/**
 * MovieAdapter.java
 * Purpose: Adapter for RecyclerView use with displaying each movie after the other.
 *
 * Used by: MainActivity.java
 * Layout files: "item_movie.xml"
 *
 * @author Josephine Mai Nguyen
 * @version 1.0
 */
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.flixster.DetailActivity;
import com.example.flixster.R;
import com.example.flixster.models.Movie;

import org.parceler.Parcels;

import java.util.List;

public class MovieAdapter extends RecyclerView.Adapter<MovieAdapter.ViewHolder> {
    Context context;            // = where is the adapter being constructed from (MainActivity)
    List<Movie> movies;         //= data to bind to

    //Constructor:
    public MovieAdapter(Context context, List<Movie> movies) {
        this.context = context;
        this.movies = movies;
    }

    @NonNull
    @Override
    /**
     * onCreateViewHolder()
     * Purpose:     Inflates a layout from "item_movie.xml" and creates/returns a ViewHolder.
     */
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Log.d("MovieAdapter", "onCreateViewHolder");
        View movieView = LayoutInflater.from(context).inflate(R.layout.item_movie, parent, false);
        return new ViewHolder(movieView);
    }

    @Override
    /**
     * onBindViewHolder()
     * Purpose:     Puts movie data at the given position into the View through the ViewHolder method "bind(movie)"
     */
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Log.d("MovieAdapter", "onBindViewHolder: position = " + position);
        Movie movie = movies.get(position);
        holder.bind(movie);
    }

    @Override
    //self-explanatory
    public int getItemCount() {
        return movies.size();
    }

    /**
     * ViewHolder{}
     * Purpose:     Creates a Movie View of sorts --> separates and obtains references to each View component of a given element/row (also a View) in the
     *              RecyclerView. Can update/bind data to each component in this "Movie View".
     */
    public class ViewHolder extends RecyclerView.ViewHolder{
        RelativeLayout container;
        TextView tvTitle;
        TextView tvOverview;
        ImageView ivPoster;

        //Constructor:
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            container = itemView.findViewById(R.id.container);
            tvTitle = itemView.findViewById(R.id.tvTitle);
            tvOverview = itemView.findViewById(R.id.tvOverview);
            ivPoster = itemView.findViewById(R.id.ivPoster);
        }

        /**
         * bind()
         * Purpose:     Binds movie details (title, overview, and image) to corresponding View in the RecyclerView.
         *              For images: landscape orient. --> use backdrop
         *                          portrait orient. --> use posterpath
         */
        public void bind(Movie movie) {
            tvTitle.setText(movie.getTitle());
            tvOverview.setText(movie.getOverview());
            String imageURL;
            if(context.getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE){
                imageURL = movie.getBackdropPath();
            }
            else{
                imageURL = movie.getPosterPath();
            }
            Glide.with(context).load(imageURL).into(ivPoster);      //loads imageURL into the ImageView "ivPoster" onto the activity ("context")

            container.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View v) {
                    Intent i = new Intent(context, DetailActivity.class);
                    i.putExtra("movie", Parcels.wrap(movie));
                    context.startActivity(i);
                }
            });
        }
    }
}

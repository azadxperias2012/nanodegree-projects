package com.example.android.popularmovies;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by aabbasal on 11/29/2015.
 */
public class MoviesData implements Parcelable{

    public static final String MOVIE_POSTER_URL = "http://image.tmdb.org/t/p/w185/";

    private long movieId;
    private String movieTitle;
    private String movieReleaseDate;
    private String moviePosterPath;
    private String moviePlot;
    private Double movieRating;

    public MoviesData(long movieId, String movieTitle, String movieReleaseDate, String moviePosterPath, String moviePlot, Double movieRating) {
        this.movieId = movieId;
        this.movieTitle = movieTitle;
        this.movieReleaseDate = movieReleaseDate;
        this.moviePosterPath = moviePosterPath;
        this.moviePlot = moviePlot;
        this.movieRating = movieRating;
    }

    public String getMovieTitle() {
        return movieTitle;
    }

    public void setMovieTitle(String mMovieTitle) {
        this.movieTitle = mMovieTitle;
    }

    public String getMovieReleaseDate() {
        return movieReleaseDate;
    }

    public void setMovieReleaseDate(String mMovieReleaseDate) {
        this.movieReleaseDate = mMovieReleaseDate;
    }

    public Double getMovieRating() {
        return movieRating;
    }

    public void setMovieRating(Double mMovieRating) {
        this.movieRating = mMovieRating;
    }

    public String getMoviePosterPath() {
        return moviePosterPath;
    }

    public void setMoviePosterPath(String mMoviePosterPath) {
        this.moviePosterPath = mMoviePosterPath;
    }

    public String getMoviePlot() {
        return moviePlot;
    }

    public void setMoviePlot(String moviePlot) {
        this.moviePlot = moviePlot;
    }

    public long getMovieId() {
        return movieId;
    }

    public void setMovieId(long movieId) {
        this.movieId = movieId;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(movieId);
        dest.writeString(movieTitle);
        dest.writeString(movieReleaseDate);
        dest.writeString(moviePosterPath);
        dest.writeString(moviePlot);
        dest.writeDouble(movieRating);
    }

    public static final Parcelable.Creator<MoviesData> CREATOR = new Parcelable.Creator<MoviesData>() {

        @Override
        public MoviesData createFromParcel(Parcel source) {
            return new MoviesData(source);
        }

        @Override
        public MoviesData[] newArray(int size) {
            return new MoviesData[size];
        }
    };

    private MoviesData(Parcel source) {
        movieId = source.readLong();
        movieTitle = source.readString();
        movieReleaseDate = source.readString();
        moviePosterPath = source.readString();
        moviePlot = source.readString();
        movieRating = source.readDouble();
    }
}

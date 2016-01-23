package com.example.android.popularmovies;

/**
 * Created by aabbasal on 1/2/2016.
 */
public class TrailerData {
    private String trailerId;
    private String trailerKey;
    private String trailerName;

    public TrailerData(String trailerId, String trailerKey, String trailerName) {
        this.trailerId = trailerId;
        this.trailerKey = trailerKey;
        this.trailerName = trailerName;
    }

    public String getTrailerId() {
        return trailerId;
    }

    public void setTrailerId(String trailerId) {
        this.trailerId = trailerId;
    }

    public String getTrailerKey() {
        return trailerKey;
    }

    public void setTrailerKey(String trailerKey) {
        this.trailerKey = trailerKey;
    }

    public String getTrailerName() {
        return trailerName;
    }

    public void setTrailerName(String trailerName) {
        this.trailerName = trailerName;
    }
}

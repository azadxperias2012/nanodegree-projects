package com.example.android.popularmovies;

/**
 * Created by aabbasal on 1/2/2016.
 */
public class ReviewData {

    private String reviewId;
    private String author;
    private String reviewUrl;

    public ReviewData(String reviewId, String author, String reviewUrl) {
        this.setReviewId(reviewId);
        this.setAuthor(author);
        this.setReviewUrl(reviewUrl);
    }

    public String getReviewId() {
        return reviewId;
    }

    public void setReviewId(String reviewId) {
        this.reviewId = reviewId;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getReviewUrl() {
        return reviewUrl;
    }

    public void setReviewUrl(String reviewUrl) {
        this.reviewUrl = reviewUrl;
    }
}

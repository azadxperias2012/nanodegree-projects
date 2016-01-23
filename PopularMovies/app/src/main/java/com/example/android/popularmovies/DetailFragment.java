package com.example.android.popularmovies;

import android.content.ContentProviderOperation;
import android.content.Context;
import android.content.Intent;
import android.content.OperationApplicationException;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.RemoteException;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.example.android.popularmovies.data.MovieDataColumns;
import com.example.android.popularmovies.data.MovieDataProvider;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

public class DetailFragment extends Fragment {

    private static final int MOVIE_DATA_NOT_FOUND = -1;
    private static final String LOG_TAG = DetailFragment.class.getSimpleName();
    public static final String MOVIE_DETAIL = "Movie Detail";

    @Bind(R.id.detail_movies_image_view)
    ImageView imageView;
    @Bind(R.id.detail_movie_title)
    TextView movieTitleTextView;
    @Bind(R.id.detail_movie_release_date)
    TextView movieReleaseDateTextView;
    @Bind(R.id.detail_movie_rating)
    TextView movieRatingTextView;
    @Bind(R.id.detail_movie_Plot)
    TextView moviePlotTextView;
    @Bind(R.id.trailers_list_view)
    ListView trailersListView;
    @Bind(R.id.reviews_list_view)
    ListView reviewsListView;
    @Bind(R.id.detail_scroll_view)
    ScrollView detailScrollView;
    @Bind(R.id.favorite_toggle_button)
    ToggleButton favoriteToggleButton;

    private TrailerDataAdapter trailerDataAdapter;
    private ReviewDataAdapter reviewDataAdapter;

    public DetailFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        MoviesData moviesData = null;
        Bundle arguments = getArguments();
        if(arguments != null) {
            moviesData = arguments.getParcelable(DetailFragment.MOVIE_DETAIL);
        }

        View rootView = inflater.inflate(R.layout.fragment_detail, container, false);
        ButterKnife.bind(this, rootView);

        if(moviesData != null) {
            setDetailLayoutViews(moviesData);
            setToggleButtonState(moviesData);
            setToggleButtonChangeEventListener(moviesData);
            setTrailers(String.valueOf(moviesData.getMovieId()));
            setReviews(String.valueOf(moviesData.getMovieId()));
        } else {
            rootView = inflater.inflate(R.layout.fargment_no_detail, container, false);
        }
        return rootView;
    }

    private void setToggleButtonState(final MoviesData moviesData) {
        //List<MoviesData> favouriteMovieList = getFavoritesMovieData(MoviesFragment.newInstance().getActivity());
        List<MoviesData> favouriteMovieList = getFavoritesMovieData(getActivity());
        int position = getMoviePosition(moviesData, favouriteMovieList);
        if (position != MOVIE_DATA_NOT_FOUND) {
            favoriteToggleButton.setChecked(true);
        }
    }

    private List<MoviesData> getFavoritesMovieData(Context context) {
        List<MoviesData> favoriteMoviesList = null;
        Cursor c = context.getContentResolver().query(MovieDataProvider.Movies.CONTENT_URI,
                null, null, null, null);

        if (c != null && c.getCount() > 0) {
            if (c.moveToFirst()) {
                favoriteMoviesList = new ArrayList<MoviesData>();
                do {
                    favoriteMoviesList.add(MoviesData.getMoviesDataFromCursor(c));
                } while (c.moveToNext());
            }
            c.close();
        }
        return favoriteMoviesList;
    }

    private void deleteFavoritesMovieData(Context context, long movieId) {
        long curPos = -1;
        Cursor c = context.getContentResolver().query(MovieDataProvider.Movies.CONTENT_URI,
                null, null, null, null);
        if (c != null && c.getCount() > 0) {
            if (c.moveToFirst()) {
                do {
                    MoviesData moviesData = MoviesData.getMoviesDataFromCursor(c);
                    if(moviesData.getMovieId() == movieId) {
                        curPos = c.getLong(c.getColumnIndex(MovieDataColumns._ID));
                        break;
                    }
                } while (c.moveToNext());
            }
        }
        c.close();

        if(curPos != -1) {
            context.getContentResolver().delete(MovieDataProvider.Movies.withId(curPos),
                    null, null);
        }
    }

    public void insertData(MoviesData movieData){
        Log.d(LOG_TAG, "insert");
        ArrayList<ContentProviderOperation> batchOperations = new ArrayList<>(1);

        ContentProviderOperation.Builder builder = ContentProviderOperation.newInsert(
                MovieDataProvider.Movies.CONTENT_URI);
        builder.withValue(MovieDataColumns.MOVIE_ID, movieData.getMovieId());
        builder.withValue(MovieDataColumns.MOVIE_TITLE, movieData.getMovieTitle());
        builder.withValue(MovieDataColumns.MOVIE_PLOT, movieData.getMoviePlot());
        builder.withValue(MovieDataColumns.MOVIE_POSTER_PATH, movieData.getMoviePosterPath());
        builder.withValue(MovieDataColumns.MOVIE_RATING, movieData.getMovieRating());
        builder.withValue(MovieDataColumns.MOVIE_RELEASE_DATE, movieData.getMovieReleaseDate());

        batchOperations.add(builder.build());

        try{
            //MoviesFragment.newInstance().getActivity().getContentResolver().applyBatch(MovieDataProvider.AUTHORITY, batchOperations);
            getActivity().getContentResolver().applyBatch(MovieDataProvider.AUTHORITY, batchOperations);
        } catch(RemoteException | OperationApplicationException e){
            Log.e(LOG_TAG, "Error applying batch insert", e);
        }

    }

    private void setToggleButtonChangeEventListener(final MoviesData moviesData) {
        favoriteToggleButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    insertData(moviesData);
                } else {
                    //deleteFavoritesMovieData(MoviesFragment.newInstance().getActivity(), moviesData.getMovieId());
                    deleteFavoritesMovieData(getActivity(), moviesData.getMovieId());
                }
            }
        });
    }

    private int getMoviePosition(MoviesData moviesData, List<MoviesData> moviesDataList) {
        int position = MOVIE_DATA_NOT_FOUND;
        if ((moviesDataList != null) && !(moviesDataList.isEmpty())) {
            for (position = 0; position < moviesDataList.size(); position++) {
                if (moviesDataList.get(position).getMovieId() == moviesData.getMovieId()) {
                    break;
                }
            }
            if (position == moviesDataList.size()) {
                position = MOVIE_DATA_NOT_FOUND;
            }
        }
        return position;
    }

    private void setDetailLayoutViews(MoviesData moviesData) {
        String imagePath = moviesData.getMoviePosterPath();
        StringBuilder moviePosterUrlBuilder = new StringBuilder(MoviesData.MOVIE_POSTER_URL);
        moviePosterUrlBuilder.append(imagePath);

        Picasso.with(this.getContext())
                .load(moviePosterUrlBuilder.toString())
                .placeholder(R.drawable.no_poster)
                .error(R.drawable.no_poster)
                .into(imageView);

        movieTitleTextView.setText(moviesData.getMovieTitle());
        movieReleaseDateTextView.setText(moviesData.getMovieReleaseDate());
        movieRatingTextView.setText(moviesData.getMovieRating().toString());
        moviePlotTextView.setText(moviesData.getMoviePlot());
    }

    private void setTrailers(String movieId) {
        List<TrailerData> popularMoviesTrailers = new ArrayList<TrailerData>();
        trailerDataAdapter = new TrailerDataAdapter(
                getActivity(),
                R.layout.list_item_trailers,
                R.id.trailer_text_view, popularMoviesTrailers);
        trailersListView.setAdapter(trailerDataAdapter);

        trailersListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                TrailerData item = trailerDataAdapter.getItem(position);
                String TRAILER_URL = "https://www.youtube.com/watch?v=%s";
                Uri webpage = Uri.parse(String.format(TRAILER_URL, item.getTrailerKey()));
                Intent intent = new Intent(Intent.ACTION_VIEW, webpage);
                if (intent.resolveActivity(getActivity().getPackageManager()) != null) {
                    startActivity(intent);
                }
            }
        });

        FetchTrailersTask moviesTask = new FetchTrailersTask();
        moviesTask.execute(movieId);
    }

    private void setReviews(String movieId) {
        List<ReviewData> reviewers = new ArrayList<ReviewData>();
        reviewDataAdapter = new ReviewDataAdapter(
                getActivity(),
                R.layout.list_item_reviews,
                R.id.review_text_view, reviewers);
        reviewsListView.setAdapter(reviewDataAdapter);

        reviewsListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                ReviewData reviewData = reviewDataAdapter.getItem(position);
                String REVIEW_URL = reviewData.getReviewUrl();
                Uri webPage = Uri.parse(REVIEW_URL);
                Intent intent = new Intent(Intent.ACTION_VIEW, webPage);
                if (intent.resolveActivity(getActivity().getPackageManager()) != null) {
                    startActivity(intent);
                }
            }
        });

        new FetchReviewsTask().execute(movieId);

    }

    private class FetchTrailersTask extends AsyncTask<String, Void, TrailerData[]> {

        private String LOG_TAG = FetchTrailersTask.class.getSimpleName();

        @Override
        protected TrailerData[] doInBackground(String... params) {
            // Will contain the raw JSON response as a string.
            String trailersJsonStr = null;

            //http://api.themoviedb.org/3/movie/244786/videos?api_key=20e2694e221baaa8b68dff9f09b2ac58
            final String MOVIE_DB_TRAILERS_BASE_URL = "http://api.themoviedb.org/3/movie/%s/videos?";
            final String API_KEY_PARAM = "api_key";

            final String FORMATTED_MOVIE_DB_TRAILERS_URL = String.format(MOVIE_DB_TRAILERS_BASE_URL, params[0]);

            // Construct the URL for the moviedb query, with possible parameters
            Uri builtUri = null;
            builtUri = Uri.parse(FORMATTED_MOVIE_DB_TRAILERS_URL).buildUpon()
                    .appendQueryParameter(API_KEY_PARAM, BuildConfig.THE_MOVIE_DB_API_KEY)
                    .build();

            trailersJsonStr = MovieDBApiHelper.getJSONResponse(builtUri.toString());
            Log.v(LOG_TAG, "Built URI " + builtUri.toString());

            try {
                return getTrailersDataFromJson(trailersJsonStr);
            } catch (JSONException e) {
                Log.e(LOG_TAG, "Error ", e);
            }

            return null;
        }

        @Override
        protected void onPostExecute(TrailerData[] result) {
            if (result != null) {
                trailerDataAdapter.clear();
                List<TrailerData> popularMoviesTrailers = new ArrayList<TrailerData>();
                for (TrailerData trailers : result) {
                    popularMoviesTrailers.add(trailers);
                }
                trailerDataAdapter.addAll(popularMoviesTrailers);
            }
            Helper.getListViewSize(trailersListView);
            detailScrollView.scrollTo(0, 0);
        }

        private TrailerData[] getTrailersDataFromJson(String moviesJson) throws JSONException {
            if (moviesJson == null) {
                return null;
            }

            final String MOVIE_TRAILER_RESULTS = "results";

            // get the json object from the given json string
            JSONObject movieDBJsonObj = new JSONObject(moviesJson);
            // find "results" array from the json object
            JSONArray resultsJsonArr = movieDBJsonObj.getJSONArray(MOVIE_TRAILER_RESULTS);

            if ((resultsJsonArr != null) && (resultsJsonArr.length() > 0)) {
                TrailerData[] moviesTrailersArr = new TrailerData[resultsJsonArr.length()];

                for (int i = 0; i < resultsJsonArr.length(); i++) {
                    // get individual result object from the results array
                    JSONObject resultObj = resultsJsonArr.getJSONObject(i);
                    moviesTrailersArr[i] = getTrailerData(resultObj);
                }

                return moviesTrailersArr;
            }

            return null;
        }

        private TrailerData getTrailerData(JSONObject resultObj) throws JSONException {
            final String TRAILER_ID = "id";
            final String TRAILER_KEY = "key";
            final String TRAILER_NAME = "name";

            // Create MoviesData object with the retrieved values from the JSON
            TrailerData trailerData = new TrailerData(
                    resultObj.getString(TRAILER_ID),
                    resultObj.getString(TRAILER_KEY),
                    resultObj.getString(TRAILER_NAME));
            return trailerData;
        }
    }

    private class FetchReviewsTask extends AsyncTask<String, Void, ReviewData[]> {

        private String LOG_TAG = FetchReviewsTask.class.getSimpleName();

        @Override
        protected ReviewData[] doInBackground(String... params) {
            // Will contain the raw JSON response as a string.
            String reviewsJsonStr = null;

            //http://api.themoviedb.org/3/movie/157336/reviews?api_key=20e2694e221baaa8b68dff9f09b2ac58
            final String MOVIE_DB_REVIEWS_BASE_URL = "http://api.themoviedb.org/3/movie/%s/reviews?";
            final String API_KEY_PARAM = "api_key";

            final String FORMATTED_MOVIE_DB_TRAILERS_URL = String.format(MOVIE_DB_REVIEWS_BASE_URL, params[0]);

            // Construct the URL for the moviedb query, with possible parameters
            Uri builtUri = null;
            builtUri = Uri.parse(FORMATTED_MOVIE_DB_TRAILERS_URL).buildUpon()
                    .appendQueryParameter(API_KEY_PARAM, BuildConfig.THE_MOVIE_DB_API_KEY)
                    .build();

            reviewsJsonStr = MovieDBApiHelper.getJSONResponse(builtUri.toString());
            Log.v(LOG_TAG, "Built URI " + builtUri.toString());

            try {
                return getReviewsDataFromJson(reviewsJsonStr);
            } catch (JSONException e) {
                Log.e(LOG_TAG, "Error ", e);
            }

            return null;
        }

        @Override
        protected void onPostExecute(ReviewData[] result) {
            if (result != null) {
                reviewDataAdapter.clear();
                List<ReviewData> popularMovieReviews = new ArrayList<ReviewData>();
                for (ReviewData reviewData : result) {
                    popularMovieReviews.add(reviewData);
                }
                reviewDataAdapter.addAll(popularMovieReviews);
            }
            Helper.getListViewSize(reviewsListView);
            detailScrollView.scrollTo(0, 0);
        }

        private ReviewData[] getReviewsDataFromJson(String moviesJson) throws JSONException {
            if (moviesJson == null) {
                return null;
            }

            final String MOVIE_REVIEWS_RESULTS = "results";

            // get the json object from the given json string
            JSONObject movieDBJsonObj = new JSONObject(moviesJson);
            // find "results" array from the json object
            JSONArray resultsJsonArr = movieDBJsonObj.getJSONArray(MOVIE_REVIEWS_RESULTS);

            if ((resultsJsonArr != null) && (resultsJsonArr.length() > 0)) {
                ReviewData[] moviesReviewsArr = new ReviewData[resultsJsonArr.length()];

                for (int i = 0; i < resultsJsonArr.length(); i++) {
                    // get individual result object from the results array
                    JSONObject resultObj = resultsJsonArr.getJSONObject(i);
                    moviesReviewsArr[i] = getReviewData(resultObj);
                }

                return moviesReviewsArr;
            }

            return null;
        }

        private ReviewData getReviewData(JSONObject resultObj) throws JSONException {
            final String REVIEW_ID = "id";
            final String REVIEW_AUTHOR = "author";
            final String REVIEW_URL = "url";

            // Create MoviesData object with the retrieved values from the JSON
            ReviewData reviewData = new ReviewData(
                    resultObj.getString(REVIEW_ID),
                    resultObj.getString(REVIEW_AUTHOR),
                    resultObj.getString(REVIEW_URL));
            return reviewData;
        }
    }
}

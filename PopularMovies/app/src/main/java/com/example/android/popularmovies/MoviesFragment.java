package com.example.android.popularmovies;

import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Parcelable;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;

import com.example.android.popularmovies.data.MovieDataProvider;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class MoviesFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {
    private static final int CURSOR_LOADER_ID = 0;
    private static final String LOG_TAG = MoviesFragment.class.getSimpleName();

    private MovieDataAdapter mMoviesAdapter;
    private List<MoviesData> popularMoviesList = null;
    private FavouriteMoviesCursorAdapter mFavouriteMoviesCursorAdapter = null;
    private Bundle mSavedMovieList;

    /**
     * A callback interface that all activities containing this fragment must
     * implement. This mechanism allows activities to be notified of item
     * selections.
     */
    public interface Callback {
        /**
         * DetailFragmentCallback for when an item has been selected.
         */
        public void onItemSelected(MoviesData moviesData);
    }

    public MoviesFragment() {
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        String sortOrder = getSortOrder();
        if(getString(R.string.pref_sort_favourite).equals(sortOrder)) {
            popularMoviesList = getFavoritesMovieData(getActivity());
            updateMovies(sortOrder);
        }

        getLoaderManager().initLoader(CURSOR_LOADER_ID, null, this);
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.d(LOG_TAG, "resume called");
        getLoaderManager().restartLoader(CURSOR_LOADER_ID, null, this);
    }

    @Override
    public View onCreateView(final LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        mSavedMovieList = new Bundle();

        // Create an empty list for the GridView.
        List<MoviesData> popularMovies = new ArrayList<MoviesData>();
        // Create an MovieDataAdapter to populate an empty movies poster grid view.
        mMoviesAdapter = new MovieDataAdapter(
                getActivity(),
                R.layout.grid_item_movies,
                R.id.grid_item_movies_textview, popularMovies);

        mFavouriteMoviesCursorAdapter = new FavouriteMoviesCursorAdapter(getActivity(), null, 0);

        View rootView = inflater.inflate(R.layout.fagment_movies, container, false);

        // Create a GridView and assign the ImageAdapter
        GridView moviesGridView = (GridView)rootView.findViewById(R.id.movies_grid_view);
        moviesGridView.setAdapter(mMoviesAdapter);

        moviesGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                ((Callback) getActivity())
                        .onItemSelected(mMoviesAdapter.getItem(position));
            }
        });

        String sortOrder = getSortOrder();
        updateMovies(sortOrder);

        return rootView;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        if(popularMoviesList != null) {
            String sortOrder = getSortOrder();
            mSavedMovieList.putParcelableArrayList(sortOrder,
                    (ArrayList<? extends Parcelable>) popularMoviesList);
        }
        super.onSaveInstanceState(outState);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return new CursorLoader(getActivity(), MovieDataProvider.Movies.CONTENT_URI,
                null,
                null,
                null,
                null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        mFavouriteMoviesCursorAdapter.swapCursor(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mFavouriteMoviesCursorAdapter.swapCursor(null);
    }

    public void onSortOrderChanged() {
        popularMoviesList = null;
        String sortOrder = getSortOrder();
        if(getString(R.string.pref_sort_favourite).equals(sortOrder)) {
            popularMoviesList = getFavoritesMovieData(getActivity());
            if(popularMoviesList == null) {
                mMoviesAdapter.clear();
            }
        } else {
            popularMoviesList = mSavedMovieList.getParcelableArrayList(sortOrder);
        }
        updateMovies(sortOrder);
    }

    private void updateMovies(String sortOrder) {
        if((popularMoviesList != null) && (!popularMoviesList.isEmpty())) {
            mMoviesAdapter.clear();
            mMoviesAdapter.addAll(popularMoviesList);
        } else if(!sortOrder.equals(getString(R.string.pref_sort_favourite))) {
            FetchMoviesTask moviesTask = new FetchMoviesTask();
            // Execute the task with the retrieved sort preference value.
            moviesTask.execute(sortOrder);
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

    private String getSortOrder() {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
        return prefs.getString(getString(R.string.pref_sort_key), getString(R.string.pref_sort_popular));
    }

    private class FetchMoviesTask extends AsyncTask<String, Void, MoviesData[]>{

        private String LOG_TAG = FetchMoviesTask.class.getSimpleName();

        @Override
        protected MoviesData[] doInBackground(String... params) {
            // These two need to be declared outside the try/catch
            // so that they can be closed in the finally block.
            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;

            // Will contain the raw JSON response as a string.
            String moviesJsonStr = null;

            try {
                final String MOVIE_DB_BASE_URL = "http://api.themoviedb.org/3/discover/movie?";
                final String MOVIE_DB_TOP_RATED_URL = "http://api.themoviedb.org/3/movie/top_rated?";
                final String API_KEY_PARAM = "api_key";
                final String SORT_PARAM = "sort_by";

                Uri builtUri = null;
                if(params[0].equalsIgnoreCase(getString(R.string.pref_sort_top_rated))) {
                    builtUri = Uri.parse(MOVIE_DB_TOP_RATED_URL).buildUpon()
                            .appendQueryParameter(API_KEY_PARAM, BuildConfig.THE_MOVIE_DB_API_KEY)
                            .build();
                } else {
                    builtUri = Uri.parse(MOVIE_DB_BASE_URL).buildUpon()
                            .appendQueryParameter(API_KEY_PARAM, BuildConfig.THE_MOVIE_DB_API_KEY)
                            .appendQueryParameter(SORT_PARAM, params[0])
                            .build();
                }

                // Construct the URL for the moviedb query, with possible parameters
                URL url = new URL(builtUri.toString());
                Log.v(LOG_TAG, "Built URI " + builtUri.toString());

                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.connect();

                InputStream inputStream = urlConnection.getInputStream();
                StringBuffer buffer = new StringBuffer();
                if(inputStream == null) {
                    moviesJsonStr = null;
                }
                reader = new BufferedReader(new InputStreamReader(inputStream));

                String line;
                while ((line = reader.readLine()) != null) {
                    // Since it's JSON, adding a newline isn't necessary (it won't affect parsing)
                    // But it does make debugging a *lot* easier if you print out the completed
                    // buffer for debugging.
                    buffer.append(line + "\n");
                }

                if (buffer.length() == 0) {
                    // Stream was empty.  No point in parsing.
                    moviesJsonStr = null;
                }
                moviesJsonStr = buffer.toString();

            } catch (IOException e) {
                Log.e(LOG_TAG, "Error " + e);
                // If the code didn't successfully get the movies data, there's no point in attempting
                // to parse it.
                moviesJsonStr = null;
            } finally {
                if(urlConnection != null) {
                    urlConnection.disconnect();
                }
                if (reader != null) {
                    try {
                        reader.close();
                    } catch (final IOException e) {
                        Log.e(LOG_TAG, "Error closing stream", e);
                    }
                }
            }

            try {
                return getMoviesDataFromJson(moviesJsonStr);
            } catch (JSONException e) {
                Log.e(LOG_TAG, "Error ", e);
            }

            return null;
        }

        @Override
        protected void onPostExecute(MoviesData[] result) {
            if(result != null) {
                mMoviesAdapter.clear();
                popularMoviesList = new ArrayList<MoviesData>();
                for(MoviesData movies : result) {
                    popularMoviesList.add(movies);
                }
                mMoviesAdapter.addAll(popularMoviesList);
            }
        }

        private MoviesData[] getMoviesDataFromJson(String moviesJson) throws JSONException {
            if(moviesJson == null) {
                return null;
            }

            final String MOVIE_DB_RESULTS   = "results";

            // get the json object from the given json string
            JSONObject movieDBJsonObj = new JSONObject(moviesJson);
            // find "results" array from the json object
            JSONArray resultsJsonArr = movieDBJsonObj.getJSONArray(MOVIE_DB_RESULTS);

            if((resultsJsonArr != null) && (resultsJsonArr.length() > 0)) {
                MoviesData[] moviesDataArr = new MoviesData[resultsJsonArr.length()];

                for(int i = 0; i < resultsJsonArr.length(); i++) {
                    // get individual result object from the results array
                    JSONObject resultObj = resultsJsonArr.getJSONObject(i);
                    moviesDataArr[i] = getMoviesData(resultObj);
                }

                return moviesDataArr;
            }

            return null;
        }

        private MoviesData getMoviesData(JSONObject resultObj) throws JSONException {
            final String MOVIE_TITLE        = "original_title";
            final String MOVIE_RELEASE_DATE = "release_date";
            final String MOVIE_POSTER_PATH  = "poster_path";
            final String MOVIE_PLOT         = "overview";
            final String MOVIE_RATING       = "vote_average";

            // Create MoviesData object with the retrieved values from the JSON
            MoviesData moviesData = new MoviesData(
                    resultObj.getLong("id"),
                    resultObj.getString(MOVIE_TITLE),
                    resultObj.getString(MOVIE_RELEASE_DATE),
                    resultObj.getString(MOVIE_POSTER_PATH), resultObj.getString(MOVIE_PLOT), resultObj.getDouble(MOVIE_RATING));
            return  moviesData;
        }
    }
}

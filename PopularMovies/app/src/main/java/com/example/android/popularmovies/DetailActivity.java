package com.example.android.popularmovies;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

public class DetailActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.frame_container, new DetailFragment())
                    .commit();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_movies, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            startActivity(new Intent(this, SettingsActivity.class));
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public static class DetailFragment extends Fragment {

        public DetailFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_detail, container, false);
            Intent intent = getActivity().getIntent();
            MoviesData moviesData = (MoviesData) intent.getSerializableExtra(MoviesActivity.EXTRA_MESSAGE);
            setDetailLayoutViews(moviesData, rootView);
            return rootView;
        }

        private void setDetailLayoutViews(MoviesData moviesData, View rootView) {
            String imagePath = moviesData.getMoviePosterPath();
            StringBuilder moviePosterUrlBuilder = new StringBuilder(MoviesData.MOVIE_POSTER_URL);
            moviePosterUrlBuilder.append(imagePath);

            ImageView imageView = (ImageView) rootView.findViewById(R.id.detail_movies_image_view);
            Picasso.with(this.getContext())
                    .load(moviePosterUrlBuilder.toString())
                    .placeholder(R.drawable.no_poster)
                    .error(R.drawable.no_poster)
                    .into(imageView);

            TextView textView = (TextView) rootView. findViewById(R.id.detail_movie_title);
            textView.setText(moviesData.getMovieTitle());

            textView = (TextView) rootView. findViewById(R.id.detail_movie_release_date);
            textView.setText(moviesData.getMovieReleaseDate());

            textView = (TextView) rootView. findViewById(R.id.detail_movie_rating);
            textView.setText(moviesData.getMovieRating().toString());

            textView = (TextView) rootView. findViewById(R.id.detail_movie_Plot);
            textView.setText(moviesData.getMoviePlot());
        }
    }
}

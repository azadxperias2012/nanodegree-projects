package com.udacity.gradle.builditbigger;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Pair;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;

import com.example.Joke;


public class MainActivity extends ActionBarActivity {

    private ProgressBar loadingIndicator = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_paid);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
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
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void tellJoke(View view) {
        showLoadingIndicator();
        String joke = Joke.tellJokes();
        new EndpointsAsyncTask().execute(new Pair<Context, String>(this, joke));
    }

    @Override
    protected void onPause() {
        super.onPause();
        hideLoadingIndicator();
    }

    @Override
    protected void onStop() {
        super.onStop();
        hideLoadingIndicator();
    }

    private void showLoadingIndicator() {
        if(loadingIndicator == null) {
            loadingIndicator = (ProgressBar) findViewById(R.id.loadingIndicator);
        }
        loadingIndicator.setVisibility(View.VISIBLE);
    }

    private void hideLoadingIndicator() {
        if(loadingIndicator != null) {
            loadingIndicator.setVisibility(View.GONE);
        }
    }
}

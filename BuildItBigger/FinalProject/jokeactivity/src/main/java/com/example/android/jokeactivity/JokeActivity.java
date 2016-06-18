package com.example.android.jokeactivity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.widget.TextView;

public class JokeActivity extends ActionBarActivity {

    public final static String EXTRA_MESSAGE = "com.example.android.jokeactivity.MESSAGE";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_joke);

        Intent intent = getIntent();
        String joke = intent.getStringExtra(EXTRA_MESSAGE);

        TextView textView = (TextView) findViewById(R.id.libraryWelcomeTextView);
        textView.setText(joke);
    }

    public void launchLibraryActivity(View view) {
        Intent myIntent = new Intent(this, JokeActivity.class);
        startActivity(myIntent);
    }
}

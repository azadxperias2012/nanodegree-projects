package com.example.android.myappportfolio;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    private Context context = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
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

    public void displayMessage(View view) {
        CharSequence text = null;

        // get the application context
        if(context == null) {
            context = getApplicationContext();
        }

        if(view.getId() == R.id.spotify_app) {
            text = getString(R.string.spotify_message);
        } else if(view.getId() == R.id.scores_app) {
            text = getString(R.string.scores_message);
        } else if(view.getId() == R.id.library_app) {
            text = getString(R.string.library_message);
        } else if(view.getId() == R.id.build_app) {
            text = getString(R.string.build_big_message);
        } else if(view.getId() == R.id.reader_app) {
            text = getString(R.string.reader_message);
        } else if(view.getId() == R.id.capstone_app) {
            text = getString(R.string.capstone_message);
        }

        // make and show toast message
        Toast toast = Toast.makeText(context, text, Toast.LENGTH_SHORT);
        toast.show();
    }
}

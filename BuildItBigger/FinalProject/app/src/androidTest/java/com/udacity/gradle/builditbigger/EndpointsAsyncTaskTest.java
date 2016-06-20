package com.udacity.gradle.builditbigger;

import android.content.Context;
import android.test.AndroidTestCase;
import android.util.Pair;

import com.example.Joke;

/**
 * Created by aabbasal on 6/20/2016.
 */
public class EndpointsAsyncTaskTest extends AndroidTestCase {

    public void testApp() {
        final String joke = Joke.tellJokes();
        assertNotNull(joke);
        new EndpointsAsyncTask() {
            @Override
            protected void onPostExecute(String result) {
                super.onPostExecute(result);
                assertNotNull(result);
                assertEquals("Should be same as the joke", joke, result);
            }
        }.execute(new Pair<Context, String>(this.getContext(), joke));

    }

}

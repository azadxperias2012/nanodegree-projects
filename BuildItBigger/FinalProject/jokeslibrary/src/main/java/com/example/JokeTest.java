package com.example;

import org.junit.Assert;
import org.junit.Test;

/**
 * Created by aabbasal on 6/5/2016.
 */
public class JokeTest {
    @Test
    public void testTellJokes() {
        String s = Joke.tellJokes();
        Assert.assertNotNull(s);
    }
}

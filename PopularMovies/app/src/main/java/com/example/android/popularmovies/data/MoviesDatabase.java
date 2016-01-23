package com.example.android.popularmovies.data;

import net.simonvt.schematic.annotation.Database;
import net.simonvt.schematic.annotation.Table;

/**
 * Created by aabbasal on 1/3/2016.
 */

@Database(version = MoviesDatabase.VERSION)
public final class MoviesDatabase {
    private MoviesDatabase() {}

    public static final int VERSION = 1;

    @Table(MovieDataColumns.class) public static final String MOVIES = "movies";
}

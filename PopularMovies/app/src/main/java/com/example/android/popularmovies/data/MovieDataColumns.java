package com.example.android.popularmovies.data;

import net.simonvt.schematic.annotation.AutoIncrement;
import net.simonvt.schematic.annotation.DataType;
import net.simonvt.schematic.annotation.NotNull;
import net.simonvt.schematic.annotation.PrimaryKey;

/**
 * Created by aabbasal on 1/3/2016.
 */
public interface MovieDataColumns {

    @DataType(DataType.Type.INTEGER) @PrimaryKey
    @AutoIncrement
    public static final String _ID = "_id";

    @DataType(DataType.Type.TEXT) @NotNull
    public static final String MOVIE_ID = "movie_id";

    @DataType(DataType.Type.TEXT) @NotNull
    public static final String MOVIE_TITLE = "movie_title";

    @DataType(DataType.Type.TEXT) @NotNull
    public static final String MOVIE_RELEASE_DATE = "movie_release_date";

    @DataType(DataType.Type.TEXT) @NotNull
    public static final String MOVIE_POSTER_PATH = "movie_poster_path";

    @DataType(DataType.Type.TEXT) @NotNull
    public static final String MOVIE_PLOT = "movie_plot";

    @DataType(DataType.Type.TEXT) @NotNull
    public static final String MOVIE_RATING = "movie_rating";

}

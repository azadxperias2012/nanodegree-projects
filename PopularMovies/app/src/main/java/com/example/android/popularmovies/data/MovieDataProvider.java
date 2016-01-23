package com.example.android.popularmovies.data;

import android.net.Uri;

import net.simonvt.schematic.annotation.ContentProvider;
import net.simonvt.schematic.annotation.ContentUri;
import net.simonvt.schematic.annotation.InexactContentUri;
import net.simonvt.schematic.annotation.TableEndpoint;

/**
 * Created by aabbasal on 1/3/2016.
 */

@ContentProvider(authority = MovieDataProvider.AUTHORITY, database = MoviesDatabase.class)
public final class MovieDataProvider {
    public static final String AUTHORITY = "com.example.android.popularmovies.data.MovieDataProvider";
    static final Uri BASE_CONTENT_URI = Uri.parse("content://" + AUTHORITY);

    interface Path {
        String FAVORITES = "favourite";
    }

    private static Uri buildUri(String... paths) {
        Uri.Builder builder = BASE_CONTENT_URI.buildUpon();
        for (String path : paths) {
            builder.appendPath(path);
        }
        return builder.build();
    }

    @TableEndpoint(table = MoviesDatabase.MOVIES)
    public static class Movies {
        @ContentUri(
                path = Path.FAVORITES,
                type = "vnd.android.cursor.dir/favourite")
        public static final Uri CONTENT_URI = buildUri(Path.FAVORITES);

        @InexactContentUri(
                name = "FAVORITE_ID",
                path = Path.FAVORITES + "/#",
                type = "vnd.android.cursor.item/favourite",
                whereColumn = MovieDataColumns._ID,
                pathSegment = 1)
        public static Uri withId(long id){
            return buildUri(Path.FAVORITES, String.valueOf(id));
        }
    }
}

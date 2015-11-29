package com.example.android.popularmovies;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * Created by aabbasal on 11/29/2015.
 */
public class MovieDataAdapter extends ArrayAdapter<MoviesData> {

    private int mFieldId = 0;
    private final LayoutInflater mInflater;
    private int mResource;

    public MovieDataAdapter(Context context, int resource, int imageViewResId, List<MoviesData> objects) {
        super(context, resource, objects);
        mFieldId = imageViewResId;
        mInflater = LayoutInflater.from(context);
        mResource = resource;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        return createViewFromResource(mInflater, position, convertView, parent, mResource);
    }

    private View createViewFromResource(LayoutInflater inflater, int position, View convertView,
                                        ViewGroup parent, int resource) {
        View view;
        ImageView imageView;

        if (convertView == null) {
            view = inflater.inflate(resource, parent, false);
        } else {
            view = convertView;
        }

        try {
            if (mFieldId == 0) {
                //  If no custom field is assigned, assume the whole resource is a ImageView
                imageView = (ImageView) view;
            } else {
                //  Otherwise, find the ImageView field within the layout
                imageView = (ImageView) view.findViewById(mFieldId);
            }
        } catch (ClassCastException e) {
            Log.e("ArrayAdapter", "You must supply a resource ID for a ImageView");
            throw new IllegalStateException(
                    "ArrayAdapter requires the resource ID to be a ImageView", e);
        }

        MoviesData item = getItem(position);
        String imagePath = null;
        if (item.getMoviePosterPath() instanceof CharSequence) {
            imagePath = ((CharSequence) item.getMoviePosterPath()).toString();
        } else {
            imagePath = item.getMoviePosterPath();
        }
        StringBuilder moviePosterUrlBuilder = new StringBuilder(MoviesData.MOVIE_POSTER_URL);
        moviePosterUrlBuilder.append(imagePath);
        Picasso.with(this.getContext()).load(moviePosterUrlBuilder.toString()).into(imageView);

        return view;
    }
}

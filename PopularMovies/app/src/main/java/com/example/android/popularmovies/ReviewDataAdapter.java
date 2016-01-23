package com.example.android.popularmovies;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

/**
 * Created by aabbasal on 1/2/2016.
 */
public class ReviewDataAdapter extends ArrayAdapter<ReviewData> {

    private int mFieldId = 0;
    private final LayoutInflater mInflater;
    private int mResource;

    public ReviewDataAdapter(Context context, int resource, int textViewResId, List<ReviewData> objects) {
        super(context, resource, objects);
        mFieldId = textViewResId;
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
        TextView textView;

        if (convertView == null) {
            view = inflater.inflate(resource, parent, false);
        } else {
            view = convertView;
        }

        try {
            if (mFieldId == 0) {
                //  If no custom field is assigned, assume the whole resource is a ImageView
                textView = (TextView) view;
            } else {
                //  Otherwise, find the ImageView field within the layout
                textView = (TextView) view.findViewById(mFieldId);
            }
        } catch (ClassCastException e) {
            Log.e("ArrayAdapter", "You must supply a resource ID for a ImageView");
            throw new IllegalStateException(
                    "ArrayAdapter requires the resource ID to be a ImageView", e);
        }

        ReviewData item = getItem(position);
        textView.setText(item.getAuthor());

        return view;
    }

}

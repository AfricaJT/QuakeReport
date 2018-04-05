package com.example.android.quakereporter;

import android.app.Activity;
import android.graphics.drawable.GradientDrawable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * Created by Jesse on 13/03/2018.
 */

public class EarthquakeAdapter extends ArrayAdapter<Earthquake> {

    private final String LOG_TAG = EarthquakeAdapter.class.getSimpleName();

    public EarthquakeAdapter(Activity context, ArrayList<Earthquake> earthquakes) {

        super(context, 0, earthquakes);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        Log.d(LOG_TAG, " - getView - updateUI");
        View listItemView = convertView;
        if (listItemView == null) {
            listItemView = LayoutInflater.from(getContext()).inflate(
                    R.layout.list_item_earthquake, parent, false);
        }

        Earthquake earthquake = getItem(position);


        TextView magIcon = listItemView.findViewById(R.id.magnitude);
        magIcon.setText(earthquake.getMagStr());

        /**
         * Set the proper background color on the magnitude circle.
         * Fetch the background from the TextView, which is a GradientDrawable.
         */
        GradientDrawable magnitudeCircle = (GradientDrawable) magIcon.getBackground();
        /**
         * Get the appropriate background color based on the current earthquake magnitude
         */
        int magnitudeColor = getMagnitudeColor(earthquake.getMag());
        /**
         * Set the color on the magnitude circle
         */
        magnitudeCircle.setColor(magnitudeColor);

        TextView offsetPlace = listItemView.findViewById(R.id.location_offset);
        offsetPlace.setText(earthquake.getOffsetPlace());

        TextView primaryPlace = listItemView.findViewById(R.id.primary_location);
        primaryPlace.setText(earthquake.getPrimaryPlace());

        TextView date = listItemView.findViewById(R.id.date);
        date.setText(earthquake.getDate());

        TextView time = listItemView.findViewById(R.id.time);
        time.setText(earthquake.getTime());

        return listItemView;
    }

    private int getMagnitudeColor(double mag) {

        int magInt = (int) Math.floor(mag);
        int magnitudeColorResourceId = -1;
        switch (magInt) {

            case 1:
                magnitudeColorResourceId = R.color.magnitude1;
                break;
            case 2:
                magnitudeColorResourceId = R.color.magnitude2;
                break;
            case 3:
                magnitudeColorResourceId = R.color.magnitude3;
                break;
            case 4:
                magnitudeColorResourceId = R.color.magnitude4;
                break;
            case 5:
                magnitudeColorResourceId = R.color.magnitude5;
                break;
            case 6:
                magnitudeColorResourceId = R.color.magnitude6;
                break;
            case 7:
                magnitudeColorResourceId = R.color.magnitude7;
                break;
            case 8:
                magnitudeColorResourceId = R.color.magnitude8;
                break;
            case 9:
                magnitudeColorResourceId = R.color.magnitude9;
                break;
            case 10:
                magnitudeColorResourceId = R.color.magnitude10plus;
                break;
            default:
                magnitudeColorResourceId = R.color.magnitude1;
                break;
        }
        return ContextCompat.getColor(getContext(), magnitudeColorResourceId);
    }
}
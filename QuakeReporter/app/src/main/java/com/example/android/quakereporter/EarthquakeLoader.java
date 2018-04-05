package com.example.android.quakereporter;

import android.content.AsyncTaskLoader;
import android.content.Context;
import android.util.Log;

import java.util.List;

/**
 * Created by Jesse on 22/03/2018.
 */

/**
 * Loads a list of earthquakes by using an AsyncTask to perform
 * the network request to the given URL
 */
public class EarthquakeLoader extends AsyncTaskLoader<List<Earthquake>> {

    /** Tag for log messages */
    private static final String LOG_TAG = EarthquakeLoader.class.getSimpleName();

    /**Query URL**/
    private String mURL;

    public EarthquakeLoader(Context context, String url) {
        super(context);
        mURL = url;
    }

    @Override
    protected void onStartLoading() {
        Log.d(LOG_TAG, " - onStartLoading");
        forceLoad();
    }

    /**This is on background thread**/
    @Override
    public List<Earthquake> loadInBackground() {
        Log.d(LOG_TAG, " - loadInBackground");
        if (mURL == null){
            return null;
        }

        //Perform the network request, parse the response, and extract a list of earthquakes.
        Log.d(LOG_TAG, " - QueryUtils.fetchEarthquakeData");
        return QueryUtils.fetchEarthquakeData(mURL);
    }
}

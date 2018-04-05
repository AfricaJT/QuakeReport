package com.example.android.quakereporter;

import android.app.LoaderManager;
import android.app.LoaderManager.LoaderCallbacks;
import android.content.Context;
import android.content.Intent;
import android.content.Loader;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements LoaderCallbacks<List<Earthquake>> {

    public static final String LOG_TAG = MainActivity.class.getName();
    /**
     * Constant value for the earthquake loader ID. We can choose any integer.
     * This really only comes into play if you're using multiple loaders.
     */
    private static final int EARTHQUAKE_LOADER_ID = 1;

    private static final String USGS_REQUEST_URL = "https://earthquake.usgs.gov/fdsnws/event/1/query";

    /** Adapter for the list of earthquakes */
    private EarthquakeAdapter adapter;

    /**TextView that is displayed when the list view is empty**/
    private TextView mEmptyStateTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final ListView earthquakeListView = findViewById(R.id.list);
        mEmptyStateTextView = findViewById(R.id.empty_view);
        earthquakeListView.setEmptyView(mEmptyStateTextView);

        adapter = new EarthquakeAdapter(this, new ArrayList<Earthquake>());
        earthquakeListView.setAdapter(adapter);


        if (haveNetworkConnection()){
            // Get a reference to the LoaderManager, in order to interact with loaders.
            LoaderManager loaderManager = getLoaderManager();
            // Initialize the loader. Pass in the int ID constant defined above and pass in null for
            // the bundle. Pass in this activity for the LoaderCallbacks parameter (which is valid
            // because this activity implements the LoaderCallbacks interface).
            Log.d(LOG_TAG, " - initLoader");
            loaderManager.initLoader(EARTHQUAKE_LOADER_ID, null, this);

            earthquakeListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    Earthquake currentEarthquake = (Earthquake) parent.getAdapter().getItem(position);

                    Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(currentEarthquake.getUrl()));
                    startActivity(browserIntent);
                }
            });
        }
    }


    @Override
    public Loader<List<Earthquake>> onCreateLoader(int i, Bundle bundle) {
        Log.d(LOG_TAG, " - onCreateLoader");
        SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(this);

        String minMagnitude = sharedPrefs.getString(getString(R.string.settings_min_magnitude_key), getString(R.string.settings_min_magnitude_default));
        String orderBy = sharedPrefs.getString(getString(R.string.settings_order_by_key), getString(R.string.settings_order_by_default));

        Uri baseUri = Uri.parse(USGS_REQUEST_URL);
        Uri.Builder uriBuilder = baseUri.buildUpon();

        uriBuilder.appendQueryParameter("format", "geojson");
        uriBuilder.appendQueryParameter("limit", "50");
        uriBuilder.appendQueryParameter("minmag", minMagnitude);
        uriBuilder.appendQueryParameter("orderby", orderBy);

        return new EarthquakeLoader(this, uriBuilder.toString());
    }

    @Override
    public void onLoadFinished(Loader<List<Earthquake>> loader, List<Earthquake> earthquakelist) {
        Log.d(LOG_TAG, " - onLoaderFinished");
        //Update the UI with the result

        // Clear the adapter of previous earthquake data
        adapter.clear();

        // If there is a valid list of {@link Earthquake}s, then add them to the adapter's
        // data set. This will trigger the ListView to update.
        if (earthquakelist != null && !earthquakelist.isEmpty()) {
            adapter.addAll(earthquakelist);
        }
        if (QueryUtils.earthquakesCount > 0){
            // Set empty state text to display "No earthquakes found."
            mEmptyStateTextView.setText(R.string.no_earthquakes);
        }

        ProgressBar progressBar = findViewById(R.id.loading_spinner);
        progressBar.setVisibility(View.INVISIBLE);

    }

    @Override
    public void onLoaderReset(Loader<List<Earthquake>> loader) {
        Log.d(LOG_TAG, " - onLoaderReset");
        //Loader reset, so we can clear out our existing data.
        adapter.clear();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            Intent settingsIntent = new Intent(this, SettingsActivity.class);
            startActivity(settingsIntent);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private boolean haveNetworkConnection() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        assert cm != null;
        NetworkInfo activeNetworkInfo = cm.getActiveNetworkInfo();
        ProgressBar progressBar;
        if (activeNetworkInfo == null){
            Log.e(LOG_TAG, "NO INTERNET NULL");
            progressBar = findViewById(R.id.loading_spinner);
            progressBar.setVisibility(View.GONE);
            // Set empty state text to display "No Internet found."
            mEmptyStateTextView.setText(R.string.no_internet);
            return false;
        }
        else{
            Log.v(LOG_TAG, "PROGRESS INTERNET CONNECTED");
            return true;
        }
    }

}



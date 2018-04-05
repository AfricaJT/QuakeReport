package com.example.android.quakereporter;

import android.renderscript.ScriptGroup;
import android.util.Log;

import com.example.android.quakereporter.Earthquake;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.ArrayList;

/**
 * Helper methods related to requesting and receiving earthquake data from USGS.
 */
public final class QueryUtils {

    private static final int TIMEOUT_IN_MILLIS = 10000;
    public static int earthquakesCount = 0;
    public static final String LOG_TAG = QueryUtils.class.getSimpleName();


    /**
     * Query the USGS data set and return an Earthquake ArrayList to represent the 10 earthquakes
     * @param requestUrl the URL api that queries the USGS Web server for the info
     * @return
     */
    public static ArrayList<Earthquake> fetchEarthquakeData(String requestUrl){

        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        //Create URL object
        URL url = createUrl(requestUrl);

        //Perform HTTP request to the URL and receive a JSON response back
        String jsonResponse = null;
        try {
            jsonResponse = makeHttpRequest(url);
        } catch (IOException e) {
            Log.e(LOG_TAG, "Error closing input stream");
        }

        //Extract relevant fields from the JSON response and create an Earthquake List
        return extractFeaturesfromJson(jsonResponse);

    }

    /**
     * Returns new URL object from the given string URL
     * @param stringUrl the URL as a String data type
     * @return the URL for the http GET request
     */
    private static URL createUrl (String stringUrl){
        Log.d(LOG_TAG, " - createUrl");
        URL url = null;

        try {
            url = new URL (stringUrl);
        } catch (MalformedURLException e) {
            Log.e(LOG_TAG, "Error with creating URL ", e);
        }
        return url;
    }

    /**
     * Make an HTTP request to the given URL and return a String as the response.
     * @param url The URL for the http GET request
     * @return The String response from the web server
     * @throws IOException if there is an error connecting to the web server
     */
    private static String makeHttpRequest (URL url) throws IOException {
        Log.d(LOG_TAG, " - makeHttpRequest");
        String jsonResponse = "";

        //If the url is null, then return early
        if (url == null){
            return jsonResponse;
        }

        HttpURLConnection urlConnection = null;
        InputStream inputStream = null;
        try{
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setReadTimeout(TIMEOUT_IN_MILLIS);
            urlConnection.setConnectTimeout(TIMEOUT_IN_MILLIS);
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();

            // IF the request was successful (response code 200),
            // then read the input stream and parse the response.
            if (urlConnection.getResponseCode() == 200){
                inputStream = urlConnection.getInputStream();
                jsonResponse = readFromStream(inputStream);
            } else{
                Log.e(LOG_TAG, "Error response code: " + urlConnection.getResponseCode());
            }
        } catch (SocketTimeoutException e){
            Log.e(LOG_TAG, "Socket Timed Out: " + e);
        } catch (IOException e){
            Log.e(LOG_TAG, "Problem retrieving the earthquake JSON results.", e);
        } finally {
            if (urlConnection != null){
                urlConnection.disconnect();
            }
            if (inputStream != null){
                inputStream.close();
            }
        }
        return jsonResponse;
    }

    /**
     * Convert the InputStream into a String which contains the whole JSON response from the web server
     * @param inputStream the data bits received from the web server
     * @throws IOException if the data received cannot be converted to UTF-8 characters
     */
    private static String readFromStream (InputStream inputStream) throws IOException {
        Log.d(LOG_TAG, " - readFromStream");
        StringBuilder output = new StringBuilder();
        if (inputStream != null){
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream, Charset.forName("UTF-8"));
            BufferedReader reader = new BufferedReader(inputStreamReader);
            String line = reader.readLine();
            while(line != null){
                output.append(line);
                line = reader.readLine();
            }
        }
        return output.toString();
    }

    /**
     * Return a Earthquake arraylist by parsing out information about every earthquake
     * from the input earthquakeJSON string
     * @return arraylist Earthquake
     */
    public static ArrayList<Earthquake> extractFeaturesfromJson(String jsonResponse) {
        Log.d(LOG_TAG, " - extractFeaturesfromJson");

        // Create an empty ArrayList that we can start adding earthquakes to
        ArrayList<Earthquake> earthquakes = new ArrayList<>();

        try {
            JSONObject root = new JSONObject(jsonResponse);

            JSONArray features = root.getJSONArray("features");
            for (int pos = 0 ; pos < features.length(); pos++){
                JSONObject earthquakeRoot = features.getJSONObject(pos);
                JSONObject properties = earthquakeRoot.getJSONObject("properties");
                double mag = properties.getDouble("mag");
                String place = properties.getString("place");
                long time = properties.getLong("time");
                String url = properties.getString("url");

                earthquakes.add(new Earthquake(mag, place, time, url));
                earthquakesCount++;
            }

        } catch (JSONException e) {
            // If an error is thrown when executing any of the above statements in the "try" block,
            // catch the exception here, so the app doesn't crash. Print a log message
            // with the message from the exception.
            Log.e("QueryUtils", "Problem parsing the earthquake JSON results", e);
        }

        // Return the list of earthquakes
        return earthquakes;
    }

}
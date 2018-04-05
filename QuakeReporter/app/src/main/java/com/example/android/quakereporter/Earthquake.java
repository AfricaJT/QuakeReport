package com.example.android.quakereporter;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Created by Jesse on 12/03/2018.
 */

public class Earthquake {

    private double mMag;

    private String mPlace;

    private Date mDate;

    private String mUrl;

    public Earthquake (double mag, String place, long epoch, String url){
        mMag = mag;
        mPlace = place;
        mDate =  new Date(epoch);
        mUrl = url;
    }

    public String getUrl(){return mUrl;}

    public double getMag() {
        return mMag;
    }

    public String getPrimaryPlace() {
        if (mPlace.contains(" of ")){
            int position = mPlace.indexOf(" of ") + 3;
            return mPlace.substring(position, mPlace.length());

        }
        else{
            return mPlace;
        }
    }

    public String getOffsetPlace(){
        if (mPlace.contains(" of ")){
            int position = mPlace.indexOf(" of ") + 3;
            return mPlace.substring(0, position);
        }
        else{
            return "Near the ";
        }
    }
    public String getMagStr (){
        DecimalFormat formatter = new DecimalFormat("0.0");
        return formatter.format(mMag);
    }

    public String getDate() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd, MMM yyyy", Locale.UK);
        return dateFormat.format(mDate);
    }

    public String getTime(){
        SimpleDateFormat dateFormat = new SimpleDateFormat("h:mm a", Locale.US);
        return dateFormat.format(mDate);
    }

    @Override
    public String toString() {
        return "Mag: " + mMag + " Place: " + mPlace + " Time: " + mDate;
    }
}

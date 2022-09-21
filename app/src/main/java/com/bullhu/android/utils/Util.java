package com.bullhu.android.utils;

import android.annotation.SuppressLint;
import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.os.Build;
import android.util.Log;

import androidx.annotation.RequiresApi;

import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

public class Util {

    private static String TAG = Util.class.getName();
    ;

    public static String getAddress(Context context, String latlang){

        Geocoder geocoder;


        try {
            /**
             * Geocoder.getFromLocation - Returns an array of Addresses
             * that are known to describe the area immediately surrounding the given latitude and longitude.
             */

            List<Address> addresses;
            geocoder = new Geocoder(context, Locale.getDefault());


            double lat = Double.parseDouble(latlang.split(",")[0]);
            double lang = Double.parseDouble(latlang.split(",")[1]);

            addresses = geocoder.getFromLocation(lat, lang, 1);// Here 1 represent max location result to returned, by documents it recommended 1 to 5

            String address = addresses.get(0).getAddressLine(0); // If any additional address line present than only, check with max available address lines by getMaxAddressLineIndex()
            String city = addresses.get(0).getLocality();
            String state = addresses.get(0).getAdminArea();
            String country = addresses.get(0).getCountryName();
            String postalCode = addresses.get(0).getPostalCode();
            String knownName = addresses.get(0).getFeatureName(); // Only if available else return NULL

            Log.d(TAG, String.valueOf(addresses));
            Log.d(TAG, knownName);

            return address;

        } catch (IOException e) {
            //e.printStackTrace();
            Log.e(TAG, "Impossible to connect to Geocoder", e);
        }

        return "";
    }

    public static String getDateTime(String timestamp){

        SimpleDateFormat formatter = new SimpleDateFormat("dd MMM/yyyy hh:mm");
        return formatter.format(new Date(Long.parseLong(timestamp)));
    }

    public static int getIntFromDouble(double ss){
        return (int) Math.round(ss);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public static String convertUTCtoLocal(String utcTime){

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("hh:mm:aa dd MMM yyyy");
        DateFormat iso8601 = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
        try {
            Date date = iso8601.parse(utcTime);
            String timestamp = simpleDateFormat.format(date);
            return timestamp;

        } catch (ParseException e) {
            e.printStackTrace();
        }

        return "";
    }
}

package com.example.nasaapplication;

import android.net.Uri;
import android.text.TextUtils;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.Random;

public class QueryUtils {

    public static final String LOG_TAG = QueryUtils.class.getSimpleName();

    private QueryUtils() {
    }


    //https://api.nasa.gov/planetary/apod?start_date=2020-01-01&end_date=2020-01-1&api_key=DEMO_KEY";

    private static URL createUrl(String stringDate){
        URL url = null;
        String myUrl = "";

        Uri.Builder builder = new Uri.Builder();
        builder.scheme("https")
                .authority("api.nasa.gov")
                .appendPath("planetary")
                .appendPath("apod")
                .appendQueryParameter("start_date", stringDate)
                .appendQueryParameter("end_date", stringDate)
                .appendQueryParameter("api_key", "DEMO_KEY");
        myUrl =  builder.build().toString();

        Log.i(LOG_TAG, "MY URL IS " + myUrl);

        try {
            url = new URL(myUrl);
        } catch (MalformedURLException e){
            Log.e(LOG_TAG, "Problem building the URL :" , e);
        }
        return url;
    }

    private static String makeHttpRequest (URL url) throws IOException {
        String JsonResponse = "";
        if (url == null){
            return JsonResponse;
        }

        HttpURLConnection urlConnection = null;
        InputStream inputStream = null;

        try {
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setReadTimeout(10000);
            urlConnection.setConnectTimeout(15000);
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();

            if (urlConnection.getResponseCode() == 200){
                inputStream = urlConnection.getInputStream();
                JsonResponse = readFromStream(inputStream);
            } else {
                Log.e(LOG_TAG, "Error Response Code: " + urlConnection.getResponseCode());
            }
        } catch (IOException e) {
            Log.e(LOG_TAG, "Problem retrieving NASA APOD.", e);
        } finally {
            if (urlConnection != null){
                urlConnection.disconnect();
            }
            if (inputStream != null) {
                inputStream.close();
            }
        }
        return JsonResponse;
    }

    private static String readFromStream(InputStream inputStream) throws IOException {
        StringBuilder output = new StringBuilder();
        if (inputStream != null){
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream, Charset.forName("UTF-8"));
            BufferedReader reader = new BufferedReader(inputStreamReader);
            String line = reader.readLine();

            while (line != null) {
                output.append(line);
                line = reader.readLine();
            }
        }

        return output.toString();
    }

    public static MyApod extractFeatureFromJson(String newApod){

        if (TextUtils.isEmpty(newApod)) {
            return null;
        }

        MyApod theNewApod = null;

        try {
            JSONArray baseJsonResponse = new JSONArray(newApod);
            JSONObject todaysMyApod = baseJsonResponse.getJSONObject(0);
            String updateDate = todaysMyApod.getString("date");
            String updateTitle= todaysMyApod.getString("title");
            String updateUrl = todaysMyApod.getString("url");


            Log.i(LOG_TAG, updateDate);
            Log.i(LOG_TAG, updateUrl);
            Log.i(LOG_TAG, updateTitle);

            theNewApod = new MyApod(updateDate, updateTitle, updateUrl);
        } catch (JSONException e) {
            Log.e(LOG_TAG, "Problem pursing the earthquake data", e);
        }

        return theNewApod;
    }



    public static MyApod fetchNASAData (String requestDate){
        URL url = createUrl(requestDate);
        String JsonResp = null;
        try {
            JsonResp = makeHttpRequest(url);
        } catch (IOException e){
            Log.e(LOG_TAG, "problem making the http request.", e);
        }

        MyApod returnApod = extractFeatureFromJson(JsonResp);
        return returnApod;
    }



}

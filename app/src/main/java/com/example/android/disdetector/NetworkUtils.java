package com.example.android.disdetector;

import android.net.Uri;
import android.util.Log;
import android.widget.ProgressBar;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Created by globe_000 on 9/26/2017.
 */

public class NetworkUtils {

    private static String API_URL = "http://api.meaningcloud.com/sentiment-2.1";

    private static String API_KEY = "55d1dbcf9402bdc4c257f8e9d6b6260c";

    public static URL buildURL(String dis){
        Uri builtUri = Uri.parse(API_URL).buildUpon()
                .appendQueryParameter("key", API_KEY)
                .appendQueryParameter("txt", dis)
                .appendQueryParameter("lang", "en")
                .build();
        URL url = null;
        try {
            url = new URL(builtUri.toString());
        } catch (MalformedURLException mfe){
            mfe.printStackTrace();
        }
        return url;
    }

    public static String getResponseFromHttpUrl(URL url) throws IOException {
        HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
        try {
            InputStream in = new BufferedInputStream(urlConnection.getInputStream());
            String response = convertStreamToString(in);
            if( response != null)
                return response;
            else
                return null;
        } finally {
            urlConnection.disconnect();
        }
    }

    public static String convertStreamToString(InputStream is) {
        BufferedReader br = new BufferedReader(new InputStreamReader(is));
        StringBuilder sb = new StringBuilder();
        String line;
        try {
            while((line = br.readLine()) != null) {
                sb.append(line).append('\n');
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                is.close();
            } catch (IOException ioe) {
                ioe.printStackTrace();
            }
        }
        return sb.toString();
    }

    public static String [] getJsonResponse (String response){
        String [] parsedDisData = new String[3];
        try{
           // JSONObject jsonObj = new JSONObject(response);
            //JSONArray arr = new JSONArray(response);
            JSONObject jsonObj = new JSONObject(response);
           // Log.e("JAMES", arr.toString());
//            JSONObject scoreTag = jsonObj.getJSONObject("score_tag");
//            JSONObject confidence = jsonObj.getJSONObject("confidence");
//            JSONObject irony = jsonObj.getJSONObject("irony");
            parsedDisData[0] = jsonObj.getString("score_tag");
            parsedDisData[1] = jsonObj.getString("confidence");
            parsedDisData[2] = jsonObj.getString("irony");

        } catch( final JSONException e){
            Log.e("APP","JSON PARSING ERROR: " + e.getMessage());
        }
        return parsedDisData;
    }



}

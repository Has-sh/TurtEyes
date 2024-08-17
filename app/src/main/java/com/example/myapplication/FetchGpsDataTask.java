package com.example.myapplication;

import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;


public class FetchGpsDataTask extends AsyncTask<Void, Void, String> {
    private static final String TAG = "FetchGpsDataTask";
    public String googleMapsUrl;
    private HttpURLConnection urlConnection;
    private String serverUrl; // Add this field
    public FetchGpsDataTask(String serverUrl) { // Modify constructor to accept serverUrl
        this.serverUrl = serverUrl;
    }
    public String getGoogleMapsUrl() {
        return googleMapsUrl;
    }

    public void setGoogleMapsUrl(String googleMapsUrl) {
        this.googleMapsUrl = googleMapsUrl;
    }
    public void setUrlConnection(HttpURLConnection urlConnection) {
        this.urlConnection = urlConnection;
    }

    @Override
    protected String doInBackground(Void... voids) {
        // URL of the API endpoint to fetch GPS data

        //String apiUrl = "http://192.168.137.125/gps";
        String apiUrl = serverUrl + "/gps";


        try {
            // Configure SSLContext if needed
            SSLContext sslContext = SSLUtils.getUnsafeSSLContext();
            HttpsURLConnection.setDefaultSSLSocketFactory(sslContext.getSocketFactory());
            HttpsURLConnection.setDefaultHostnameVerifier((hostname, session) -> true);

            // Create a URL object for the API endpoint
            URL url = new URL(apiUrl);

            // Open a connection to the URL using HttpURLConnection
            urlConnection = (HttpURLConnection) url.openConnection();

            try {
                // Check if the HTTP response code is OK (200)
                if (urlConnection.getResponseCode() == HttpURLConnection.HTTP_OK) {
                    // If OK, read the response data from the InputStream
                    InputStream in = urlConnection.getInputStream();
                    BufferedReader reader = new BufferedReader(new InputStreamReader(in));
                    StringBuilder result = new StringBuilder();
                    String line;

                    // Read each line of the response and append it to the result StringBuilder
                    while ((line = reader.readLine()) != null) {
                        result.append(line);
                    }

                    // Return the response data as a string
                    return result.toString();
                } else {
                    // Log an error if the HTTP response code is not OK
                    Log.e(TAG, "HTTP error code: " + urlConnection.getResponseCode());
                }
            } finally {
                // Disconnect the HttpURLConnection to release resources
                urlConnection.disconnect();
            }
        } catch (IOException e) {
            // Log an error if there's an exception while fetching data from the server
            Log.e(TAG, "Error fetching GPS data from server", e);
            return null;
        }

        // Return the API URL (for demonstration purposes, this is not the actual result)
        return apiUrl;
    }

    @Override
    protected void onPostExecute(String result) {
        super.onPostExecute(result);

        if (result != null) {
            // Process the result, update UI, etc.
            Log.d(TAG, "Received GPS data from server: " + result);

            // Parse the JSON data to extract latitude and longitude
            try {
                // Replace this logic with your actual parsing logic
                // Assuming the response is in JSON format with keys "latitude" and "longitude"
                double latitude = parseLatitudeFromJson(result);
                double longitude = parseLongitudeFromJson(result);

                // Call a method to open Google Maps with the retrieved coordinates
                openGoogleMaps(latitude, longitude);
            } catch (Exception e) {
                Log.e(TAG, "Error parsing GPS data", e);
            }
        } else {
            // Log an error if the server response is null
            Log.e(TAG, "Received null GPS data from server");
            // Handle the case where the server response is null (not implemented in this code)
        }
    }

    public double parseLatitudeFromJson(String json) {
        // Replace this method with your actual JSON parsing logic
        // Extract and return the latitude value from the JSON string
        try {
            // Assuming the response is a JSON object with a "latitude" key
            JSONObject jsonObject = new JSONObject(json);
            return jsonObject.getDouble("latitude");
        } catch (JSONException e) {
            Log.e(TAG, "Error parsing latitude from JSON", e);
            return 0.0; // Default value or handle the error accordingly
        }
    }

    public double parseLongitudeFromJson(String json) {
        // Replace this method with your actual JSON parsing logic
        // Extract and return the longitude value from the JSON string
        try {
            // Assuming the response is a JSON object with a "longitude" key
            JSONObject jsonObject = new JSONObject(json);
            return jsonObject.getDouble("longitude");
        } catch (JSONException e) {
            Log.e(TAG, "Error parsing longitude from JSON", e);
            return 0.0; // Default value or handle the error accordingly
        }
    }

    public void openGoogleMaps(double latitude, double longitude) {
        // Create a Google Maps URL
        this.googleMapsUrl = "https://www.google.com/maps?q=" + latitude + "," + longitude;

        // Create an Intent with ACTION_VIEW and the Google Maps URL
        Log.d(TAG, "Google Maps URL: " + this.googleMapsUrl);
    }
}


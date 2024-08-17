package com.example.myapplication;
import android.os.AsyncTask;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;


public class FetchSensorDataTask extends AsyncTask<Void, Void, String> {
    private static final String TAG = "FetchSensorDataTask";
    public  String result;
    private HttpURLConnection urlConnection;
    private String serverUrl;
    public FetchSensorDataTask(String serverUrl) {
        this.serverUrl = serverUrl;
    }
    public HttpURLConnection getUrlConnection() {
        return urlConnection;
    }

    public void setUrlConnection(HttpURLConnection urlConnection) {
        this.urlConnection = urlConnection;
    }

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }


    @Override
    protected String doInBackground(Void... voids) {
        // URL of the API endpoint to fetch sensor data
        //String apiUrl = "http://192.168.137.125/sensor";
        String apiUrl = serverUrl + "/sensor";


        try {
            // Configure SSLContext to trust all certificates (for testing purposes only)
            SSLContext sslContext = SSLUtils.getUnsafeSSLContext();
            HttpsURLConnection.setDefaultSSLSocketFactory(sslContext.getSocketFactory());

            // Disable hostname verification (for testing purposes only)
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
            Log.e(TAG, "Error fetching data from server", e);
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
            Log.d(TAG, "Received data from server: " + result);
            this.result = result;
            // Parse the JSON data and update your UI accordingly (not implemented in this code)
        } else {
            // Log an error if the server response is null
            Log.e(TAG, "Received null data from server");
            // Handle the case where the server response is null (not implemented in this code)
        }
    }
}
package com.example.myapplication;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;

public class FCMTokenManager extends FirebaseMessagingService {
    private static final String TAG = "FCMTokenManager";
    private static final String SERVER_URL = MainActivity.getServerUrl() + "/register_token";
    //private static final String SERVER_URL = "http://192.168.254.23:8080/register_token";

    public static void registerToken(Context context){
        FirebaseMessaging.getInstance().getToken()
                .addOnCompleteListener(new OnCompleteListener<String>() {
                    @Override
                    public void onComplete(@NonNull Task<String> task) {
                        if (!task.isSuccessful()) {
                            Log.w(TAG, "Fetching FCM registration token failed", task.getException());
                            return;
                        }

                        // Get new FCM registration token
                        String token = task.getResult();

                        // Log and toast
                        Log.d(TAG, token);
                        sendTokenToServer(token);
                        sendTokenToDatabase(token);
                    }
                });
    }

    public static void sendTokenToDatabase(String token) {
        FirebaseDatabase database = FirebaseDatabase.getInstance("https://esc491-eb2b5-default-rtdb.europe-west1.firebasedatabase.app");
        DatabaseReference tokensReference = database.getReference("devices/tokens");

        tokensReference.orderByValue().equalTo(token).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange( DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    Log.d(TAG, "Token already exists in database");
                } else {
                    // Use the current timestamp as a long-type key
                    long timestamp = System.currentTimeMillis();

                    tokensReference.child(String.valueOf(timestamp)).setValue(token)
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        Log.d(TAG, "Token saved to database");
                                    } else {
                                        Log.w(TAG, "Error saving token to database", task.getException());
                                    }
                                }
                            });
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.w(TAG, "Database error", databaseError.toException());
            }
        });
    }

    public static void sendTokenToServer(String token) {
        new SendTokenTask().execute(token);
    }
    private static class SendTokenTask extends AsyncTask<String, Void, Void> {
        @Override
        protected Void doInBackground(String... tokens) {
            try {
                String token = tokens[0];
                SSLContext sslContext = SSLUtils.getUnsafeSSLContext();
                HttpsURLConnection.setDefaultSSLSocketFactory(sslContext.getSocketFactory());

                // Disable hostname verification (for testing purposes only)
                HttpsURLConnection.setDefaultHostnameVerifier((hostname, session) -> true);
                // Create a URL object for your Flask server's /register_token endpoint
                URL url = new URL(SERVER_URL);

                // Open a connection to the URL using HttpURLConnection
                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();

                try {
                    // Set up the connection properties
                    urlConnection.setRequestMethod("POST");
                    urlConnection.setRequestProperty("Content-Type", "application/json");
                    urlConnection.setDoOutput(true);

                    // Create the JSON payload with the FCM token
                    String jsonInputString = "{\"token\": \"" + token + "\"}";

                    // Write the JSON payload to the output stream
                    try (OutputStream os = urlConnection.getOutputStream()) {
                        byte[] input = jsonInputString.getBytes("utf-8");
                        os.write(input, 0, input.length);
                    }

                    // Check if the HTTP response code is OK (200)
                    if (urlConnection.getResponseCode() == HttpURLConnection.HTTP_OK) {
                        // If OK, read the response data from the InputStream
                        try (BufferedReader br = new BufferedReader(new InputStreamReader(urlConnection.getInputStream(), "utf-8"))) {
                            StringBuilder response = new StringBuilder();
                            String responseLine;
                            while ((responseLine = br.readLine()) != null) {
                                response.append(responseLine.trim());
                            }

                            // Log the server response
                            Log.d(TAG, "Server Response: " + response.toString());
                        }
                    } else {
                        // Log an error if the HTTP response code is not OK
                        Log.e(TAG, "HTTP error code: " + urlConnection.getResponseCode());
                    }
                } finally {
                    // Disconnect the HttpURLConnection to release resources
                    urlConnection.disconnect();
                }
            } catch (Exception e) {
                // Log an error if there's an exception while sending the token to the server
                Log.e(TAG, "Error sending token to server", e);
            }
            return null;
        }
    }
    @Override
    public void onNewToken(String token) {
        // Handle new FCM token
        Log.d(TAG, "Refreshed token: " + token);

        // Send the token to your server to associate it with the device
        sendTokenToDatabase(token);
        sendTokenToServer(token);
    }
    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        // Handle the incoming FCM message
        if (remoteMessage.getData().size() > 0) {
            // Extract message body or data from remoteMessage
            String messageBody = remoteMessage.getData().get("key2");
            String titleBody = remoteMessage.getData().get("key1");

            // Extract the send time from the notification data payload
            String sendTimeStr = remoteMessage.getData().get("send_time");
            if (sendTimeStr != null) {
                long receiveTime = System.currentTimeMillis();
                long sendTime = Long.parseLong(sendTimeStr);
                long timeDifference = sendTime - receiveTime;
                Log.d(TAG, "Notification delay: " + timeDifference + " milliseconds");
            }

            // Call sendNotification to display the notification
            sendNotification(messageBody, titleBody);
        }
    }
    private void sendNotification(String messageBody,String titleBody) {
        Intent intent = new Intent(this, statsActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0 /* Request code */, intent,
//                PendingIntent.FLAG_UPDATE_CURRENT);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_IMMUTABLE);

        String channelId = "fcm_default_channel";
        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder notificationBuilder =
                new NotificationCompat.Builder(this, channelId)
                        .setSmallIcon(R.mipmap.ic_launcher)
                        .setContentTitle(titleBody)
                        .setContentText(messageBody)
                        .setAutoCancel(true)
                        .setSound(defaultSoundUri)
                        .setContentIntent(pendingIntent);

        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        // Since android Oreo notification channel is needed.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(channelId,
                    "Channel human readable title",
                    NotificationManager.IMPORTANCE_DEFAULT);
            notificationManager.createNotificationChannel(channel);
        }

        notificationManager.notify(0 /* ID of notification */, notificationBuilder.build());
    }

}

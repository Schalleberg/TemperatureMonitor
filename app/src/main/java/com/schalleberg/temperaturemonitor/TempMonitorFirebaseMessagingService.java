package com.schalleberg.temperaturemonitor;


import android.content.Intent;
import android.os.Build;
import android.provider.Settings;
import android.util.Log;
import android.widget.Toast;

import com.dropbox.core.DbxException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;


public class TempMonitorFirebaseMessagingService extends FirebaseMessagingService {
    public static String TAG = "FirebaseServices";

    /**
     * There are two scenarios when onNewToken is called:
     * 1) When a new token is generated on initial app startup
     * 2) Whenever an existing token is changed
     * Under #2, there are three scenarios when the existing token is changed:
     * A) App is restored to a new device
     * B) User uninstalls/reinstalls the app
     * C) User clears app data
     */
    @Override
    public void onNewToken(String token) {
        Log.d(TAG, "Refreshed token: " + token);

        // If you want to send messages to this application instance or
        // manage this apps subscriptions on the server side, send the
        // FCM registration token to your app server.
        sendRegistrationToServer(token);
    }
    public static void getDeviceToken() {
        FirebaseMessaging.getInstance().getToken()
                .addOnCompleteListener(new OnCompleteListener<String>() {
                    @Override
                    public void onComplete(Task<String> task) {
                        if (!task.isSuccessful()) {
                            Log.w(TAG, "Fetching FCM registration token failed", task.getException());
                            return;
                        }

                        // Get new FCM registration token
                        String token = task.getResult();
                        sendRegistrationToServer(token);
                     }
                });
    }

    private static void sendRegistrationToServer(String token)
    {
        // Log and toast
        String msg = token;
        Log.d(TAG, msg);
        //Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_SHORT).show();

        InputStream is = new ByteArrayInputStream(token.getBytes(StandardCharsets.UTF_8));
        try {
            String fileName = "/TemperatureMonitorAndroid/Devices/" + getDeviceIdentifier() + ".dat";
            DropboxClientFactory.getClient().files().uploadBuilder(fileName).uploadAndFinish(is);
        } catch (DbxException e) {
            Log.d(TAG, "Exception:" + e.getLocalizedMessage());
        } catch (IOException e) {
            Log.d(TAG, "Exception:" + e.getLocalizedMessage());
        }

    }

    private static String getDeviceIdentifier()
    {
        String androidID = Settings.Secure.getString(MainActivity.getAppContext().getContentResolver(), Settings.Secure.ANDROID_ID);
        String manufacturer = Build.MANUFACTURER;
        String model = Build.MODEL;
        return manufacturer + "_" + model + "_" + androidID;
    }


    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        // ...

        // TODO(developer): Handle FCM messages here.
        // Not getting messages here? See why this may be: https://goo.gl/39bRNJ
        Log.d(TAG, "From: " + remoteMessage.getFrom());

        // Check if message contains a data payload.
        if (remoteMessage.getData().size() > 0) {
            Log.d(TAG, "Message data payload: " + remoteMessage.getData());

//            if (/* Check if data needs to be processed by long running job */ true) {
//                // For long-running tasks (10 seconds or more) use Firebase Job Dispatcher.
//                scheduleJob();
//            } else {
//                // Handle message within 10 seconds
//                handleNow();
//            }

        }

        // Check if message contains a notification payload.
        if (remoteMessage.getNotification() != null) {
            Log.d(TAG, "Message Notification Body: " + remoteMessage.getNotification().getBody());
        }

        // Also if you intend on generating your own notifications as a result of a received FCM
        // message, here is where that should be initiated. See sendNotification method below.
    }

}

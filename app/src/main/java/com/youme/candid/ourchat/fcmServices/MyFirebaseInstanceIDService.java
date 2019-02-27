package com.youme.candid.ourchat.fcmServices;

import android.util.Log;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;

/**
 * Created by mindiii on 15/5/18.
 */

public class MyFirebaseInstanceIDService extends FirebaseInstanceIdService {
    private static final String TAG = MyFirebaseInstanceIDService.class.getSimpleName();

    @Override
    public void onTokenRefresh() {
        System.out.println("tokenn generated ++++  12432++++------------");

        String refreshedToken = FirebaseInstanceId.getInstance().getToken();
        Log.d(TAG, "Refreshed: " + refreshedToken);
        System.out.println("tokenn generated ++++  12432++++------------");

        sendRegistrationToServer(refreshedToken);
    }

    public void sendRegistrationToServer(String token) {
        Log.e(TAG, "sendRegistrationToServer: " + token);
    }
}

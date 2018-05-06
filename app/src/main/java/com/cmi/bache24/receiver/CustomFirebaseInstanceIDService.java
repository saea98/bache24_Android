package com.cmi.bache24.receiver;

import android.util.Log;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;

/**
 * Created by omar on 1/22/17.
 */

public class CustomFirebaseInstanceIDService extends FirebaseInstanceIdService {

    @Override
    public void onTokenRefresh() {
//        super.onTokenRefresh();
//        String refreshedToken = FirebaseInstanceId.getInstance().getToken();

//        Log.d("CustomFirebase", "Refreshed token = " + refreshedToken);
    }
}

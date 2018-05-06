package com.cmi.bache24;

import android.app.Application;
import android.support.multidex.MultiDex;
import android.util.Log;

//import com.apptentive.android.sdk.Apptentive;
import com.cmi.bache24.data.model.B24DebugResponse;
import com.cmi.bache24.data.model.B24DebugThrowable;
import com.cmi.bache24.util.Constants;
import com.flurry.android.FlurryAgent;
//import com.parse.*;

import com.cmi.bache24.data.model.B24Debug;
import uk.co.chrisjenx.calligraphy.CalligraphyConfig;

/**
 * Created by omar on 1/4/16.
 */
public class Bache24Application extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        MultiDex.install(this);

        FlurryAgent.setLogEnabled(false);
        FlurryAgent.init(this, Constants.FLURRY_API_KEY);

        /*ParseObject.registerSubclass(B24Debug.class);
        ParseObject.registerSubclass(B24DebugThrowable.class);
        ParseObject.registerSubclass(B24DebugResponse.class);

        Parse.initialize(this, Constants.PARSE_APP_ID, Constants.PARSE_CLIENT_KEY);*/

     //   Apptentive.register(this, Constants.APPTENTIVE_API_KEY);

        /*ParsePush.subscribeInBackground("apptentive", new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if (e == null) {
                    String deviceToken = (String) ParseInstallation.getCurrentInstallation().get("deviceToken");
                    Apptentive.setPushNotificationIntegration(Apptentive.PUSH_PROVIDER_PARSE, deviceToken);
                }
            }
        });*/


        CalligraphyConfig.initDefault(new CalligraphyConfig.Builder()
                .setDefaultFontPath("fonts/HelveticaNeueMedium.ttf")
                .setFontAttrId(R.attr.fontPath)
                .build()
        );

        mDefaultUEH = Thread.getDefaultUncaughtExceptionHandler();
        Thread.setDefaultUncaughtExceptionHandler(mCaughtExceptionHandler);
    }

    private static Thread.UncaughtExceptionHandler mDefaultUEH;

    private static Thread.UncaughtExceptionHandler mCaughtExceptionHandler = new Thread.UncaughtExceptionHandler() {

        @Override
        public void uncaughtException(Thread thread, Throwable throwable) {
            Log.i("SelectReportActivityi", "uncaughtException (means Crash :S)");

            mDefaultUEH.uncaughtException(thread, throwable);
        }
    };
}

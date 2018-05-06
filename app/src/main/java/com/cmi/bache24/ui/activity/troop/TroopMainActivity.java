package com.cmi.bache24.ui.activity.troop;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

//import com.apptentive.android.sdk.Apptentive;
import com.cmi.bache24.R;
import com.cmi.bache24.data.model.Report;
import com.cmi.bache24.data.model.Status;
import com.cmi.bache24.data.model.User;
import com.cmi.bache24.data.model.realm.Stretch;
import com.cmi.bache24.data.remote.ServicesManager;
import com.cmi.bache24.data.remote.interfaces.IGetVersionCallback;
import com.cmi.bache24.ui.activity.LoginActivity;
import com.cmi.bache24.ui.activity.ReportActivity;
import com.cmi.bache24.ui.dialog.NewVersionDialog;
import com.cmi.bache24.ui.dialog.SimpleIconCenteredAlertDialog;
import com.cmi.bache24.ui.dialog.interfaces.IStatusResponse;
import com.cmi.bache24.ui.dialog.interfaces.MessageDialogListener;
import com.cmi.bache24.ui.fragment.troop.TroopMainFragment;
import com.cmi.bache24.util.Constants;
import com.cmi.bache24.util.PreferencesManager;
import com.cmi.bache24.util.Utils;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.gson.Gson;
//import com.parse.ParseInstallation;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.Normalizer;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.realm.Realm;
import io.realm.RealmConfiguration;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class TroopMainActivity extends AppCompatActivity implements TroopMainFragment.TroopMainFragmentListener {

    private Toolbar mToolbar;
    private TroopMainFragment mTroopMainFragment;
    private ImageView mNewReportButton;
    private ImageView mLogoutButton;
    //    private ImageView mApptentiveButton;
    private TextView mTitle;
    private User mCurrentUser;
    private boolean versionAlertVisible = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.AppThemePink);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_troop_main);

        mTroopMainFragment = (TroopMainFragment) getSupportFragmentManager()
                .findFragmentById(R.id.fragment);

        mCurrentUser = PreferencesManager.getInstance().getUserInfo(this);

        setupToolbar();

        if (mCurrentUser != null) {

            String token = FirebaseInstanceId.getInstance().getToken();
            if (token != null) {
                ServicesManager.sendFirebaseToken(TroopMainActivity.this, mCurrentUser, token);
            }


            /*ParsePush.subscribeInBackground("B24-CDMX", new SaveCallback() {
                @Override
                public void done(ParseException e) {

                }
            });*/

            /*ParseInstallation installation = ParseInstallation.getCurrentInstallation();
            installation.put("userEmail", mCurrentUser.getEmail());
            installation.saveInBackground();*/
        }

        Utils.getInstance().enableLocalizationIfNecessary(this);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                importFromJson();
            }
        }, 1000);

        getStatesList();

        if (PreferencesManager.getInstance().showAppTentiveMessageCenter(this)) {
            PreferencesManager.getInstance().clearAppTentiveMessageCenterValues(this);
            showContactView();
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return false;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (mTroopMainFragment != null) {
            mTroopMainFragment.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    private void setupToolbar() {
        mToolbar = (Toolbar) findViewById(R.id.toolbar);

        mNewReportButton = (ImageView) findViewById(R.id.new_report_button);
        mLogoutButton = (ImageView) findViewById(R.id.logout_button);
//        mApptentiveButton = (ImageView) findViewById(R.id.apptentive_button);
        mTitle = (TextView) findViewById(R.id.toolbar_title_1);

        mNewReportButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mTroopMainFragment != null)
                    mTroopMainFragment.newReport();
            }
        });

//        mApptentiveButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                showContactView();
//            }
//        });

        mLogoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                logout();
            }
        });

        mTitle.setText(Constants.KEY_DELEGATIONS.get(mCurrentUser.getIdDelegacion()));

        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        getSupportActionBar().setDisplayShowHomeEnabled(false);
    }

    @Override
    public void onReportSelected(Report report) {
        Intent intentReportStatusDetail = new Intent(this, SelectReportActivity.class);

        Gson gson = new Gson();
        String jsonReport = gson.toJson(report);
        intentReportStatusDetail.putExtra(Constants.EXTRA_REPORT_STATUS_DETAIL, jsonReport);

        startActivityForResult(intentReportStatusDetail, Constants.TROOP_SELECT_REPORT_REQUEST_CODE);
        overridePendingTransition(R.anim.enter_from_right, R.anim.enter_from_left);
    }

    @Override
    public void onUserUpdated(User userInfo) {
        this.mCurrentUser = userInfo;
        mTitle.setText(Constants.KEY_DELEGATIONS.get(mCurrentUser.getIdDelegacion()));
        PreferencesManager.getInstance().setUserInfo(this, userInfo);
    }

    @Override
    public void onLocationPermissionDenied() {
        showDeniedLocationsPermissionsAlert();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    protected void onResume() {
        super.onResume();

        /*if (getIntent() != null && getIntent().getExtras() != null) {
            Log.i("getExtrasMain", "getExtras = " + getIntent().getExtras().toString());
        }*/

        /*Log.i("VALID_REPORT_ACT_LOG", "TroopMainActivity -> onResume");

        if (PreferencesManager.getInstance().isReportAttentionInProgress(this)) {

            Log.i("VALID_REPORT_ACT_LOG", "PreferencesManager.getInstance().isReportAttentionInProgress(this)");

            String reportTicket = PreferencesManager.getInstance().getReportAttentionTicketValue(this);
            if (reportTicket != "" || !reportTicket.isEmpty()) {

                Log.i("VALID_REPORT_ACT_LOG", "reportTicket != \"\" || !reportTicket.isEmpty()");

                if (mTroopMainFragment != null)
                    mTroopMainFragment.cancelReportAttention();

            } else {
                reloadReports();
            }

        } else {*/
//            reloadReports();
        /*}*/

        validateVersion();
    }

    private void logout() {
        AlertDialog alertDialog = new AlertDialog.Builder(this).create();
        alertDialog.setTitle(getResources().getString(R.string.profile_logout_title));
        alertDialog.setMessage(getResources().getString(R.string.profile_logout_message));
        alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, getResources().getString(R.string.profile_logout_cancel),
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });

        alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, getResources().getString(R.string.profile_logout_accept),
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();

                        removeFromParse();

                        PreferencesManager.getInstance().logoutSession(TroopMainActivity.this);
                        PreferencesManager.getInstance().removeReports(TroopMainActivity.this);

                        Intent reportActivityIntent = new Intent(TroopMainActivity.this, LoginActivity.class);
                        reportActivityIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(reportActivityIntent);
                        overridePendingTransition(R.anim.enter_from_right, R.anim.enter_from_left);
                    }
                });

        alertDialog.show();

    }

    private void removeFromParse() {
        /*ParseInstallation installation = ParseInstallation.getCurrentInstallation();
        installation.put("userEmail", "");
        installation.saveInBackground();*/
    }

    private void importFromJson() {
        if (!PreferencesManager.getInstance().isDatabaseImported(this)) {
            String jsonString = Utils.loadJSONFromAssets(this, "json/tramos.json");
            try {
                JSONArray jsonArray = new JSONArray(jsonString);
                if (jsonArray != null) {
                    saveJsonToBd(jsonArray);
                }
            } catch (JSONException ex) {
                ex.printStackTrace();
            }
        }
    }

    private void saveJsonToBd(JSONArray jsonArray) {
        RealmConfiguration configuration = new RealmConfiguration.Builder(this).build();
        Realm realm = Realm.getInstance(configuration);
        realm.beginTransaction();

        try {
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonEditorial = jsonArray.getJSONObject(i);

                Stretch stretch = new Stretch();
                stretch.setId(i + 1);
                stretch.setName(jsonEditorial.getString("tramo"));
                stretch.setCleanName(clearText(jsonEditorial.getString("tramo")));

                realm.copyToRealm(stretch);
            }
        } catch (JSONException ex) {

        }

        realm.commitTransaction();

        PreferencesManager.getInstance().setDatabaseImported(this, true);
    }

    private String clearText(String originalValue) {
        String cleanText = "";

        cleanText = originalValue.replaceAll("[-+.^:,()]", "").toLowerCase();
        cleanText = cleanText.replace("  ", " ");
        cleanText = Normalizer.normalize(cleanText, Normalizer.Form.NFD);
        cleanText = cleanText.replaceAll("[\\p{InCombiningDiacriticalMarks}]", "");

        return cleanText;
    }

    /*private void reloadReports() {
        if (mTroopMainFragment != null)
            mTroopMainFragment.loadReports();
    }*/

    public void validateVersion() {

        Log.i("VersionAlert", "validateVersion");

        ServicesManager.getLastVersion(new IGetVersionCallback() {
            @Override
            public void versionResponse(String version) {
                if (version != "") {
                    String appVersionName = Utils.getVersionName(TroopMainActivity.this);
                    if (!appVersionName.equals(version)) {
                        if (!versionAlertVisible) {
                            showNewVersionAlert();
                        }
                    }
                }
            }
        });
    }

    private void showNewVersionAlert() {
        versionAlertVisible = true;

        final NewVersionDialog newVersionDialog = new NewVersionDialog();

        newVersionDialog.setupDialog(new MessageDialogListener() {
            @Override
            public void onAccept() {
                launchGooglePlay();
            }
        });

        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.add(newVersionDialog, null);
        newVersionDialog.setCancelable(false);

        fragmentTransaction.commitAllowingStateLoss();
    }

    private void launchGooglePlay() {
        String packageName = getPackageName();
        try {
            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + packageName)));
        } catch (ActivityNotFoundException ex) {
            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + packageName)));
        }
    }

    private void getStatesList() {
        ServicesManager.getStatesList(new IStatusResponse() {
            @Override
            public void onResponse(List<Status> statusList) {
                if (statusList != null) {
                    HashMap<Integer, String> statusHashMap = new HashMap<Integer, String>();

                    for (int i = 0; i < statusList.size(); i++) {
                        statusHashMap.put(statusList.get(i).getId(), statusList.get(i).getName());
                    }

                    PreferencesManager.getInstance().setStatusMap(TroopMainActivity.this, statusHashMap);
                }
            }
        });
    }

    private void showContactView() {

        Map<String, Object> customData = new HashMap<String, Object>();
        customData.put("Sección", "Mapa");
        customData.put("Email", mCurrentUser != null ? mCurrentUser.getEmail() : "");

      //  Apptentive.showMessageCenter(this, customData);
    }

    private void showDeniedLocationsPermissionsAlert() {
        final SimpleIconCenteredAlertDialog deniedLocationPermissionDialog = new SimpleIconCenteredAlertDialog();

        deniedLocationPermissionDialog.setupDialog(getResources().getString(R.string.driver_title),
                getResources().getString(R.string.location_permission_denied),
                R.drawable.carita_fuchi,
                getResources().getString(R.string.driver_option_continue),
                new MessageDialogListener() {
                    @Override
                    public void onAccept() {
                        deniedLocationPermissionDialog.dismiss();
                    }
                });
        deniedLocationPermissionDialog.setCancelable(false);

        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.add(deniedLocationPermissionDialog, null);

        fragmentTransaction.commitAllowingStateLoss();
    }
}

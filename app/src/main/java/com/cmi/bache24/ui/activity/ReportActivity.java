package com.cmi.bache24.ui.activity;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

//import com.apptentive.android.sdk.Apptentive;
import com.cmi.bache24.R;
import com.cmi.bache24.data.model.Report;
import com.cmi.bache24.data.model.Status;
import com.cmi.bache24.data.model.User;
import com.cmi.bache24.data.remote.ServicesManager;
import com.cmi.bache24.ui.dialog.SimpleIconCenteredAlertDialog;
import com.cmi.bache24.ui.dialog.TwoButtonsCenteredIconAlertDialog;
import com.cmi.bache24.ui.dialog.interfaces.IStatusResponse;
import com.cmi.bache24.ui.dialog.interfaces.MessageDialogListener;
import com.cmi.bache24.ui.dialog.interfaces.TwoButtonsDialogListener;
import com.cmi.bache24.ui.fragment.BacheInfoFragment;
import com.cmi.bache24.ui.fragment.ContactFragment;
import com.cmi.bache24.ui.fragment.FaqFragment;
import com.cmi.bache24.ui.fragment.InformationFragment;
import com.cmi.bache24.ui.fragment.NewsFragment;
import com.cmi.bache24.ui.fragment.PaymentFragment;
import com.cmi.bache24.ui.fragment.PrimaryRoutesFragment;
import com.cmi.bache24.ui.fragment.PrivacyFragment;
import com.cmi.bache24.ui.fragment.ReportDetailFragment;
import com.cmi.bache24.ui.fragment.ReportFragment;
import com.cmi.bache24.ui.fragment.ReportStatusDetailFragment;
import com.cmi.bache24.ui.fragment.TermsFragment;
import com.cmi.bache24.ui.fragment.UserHistoryFragment;
import com.cmi.bache24.util.Constants;
import com.cmi.bache24.util.PreferencesManager;
import com.cmi.bache24.util.Utils;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;
import com.special.ResideMenu.ResideMenu;
import com.special.ResideMenu.ResideMenuItem;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class ReportActivity extends AppCompatActivity implements View.OnClickListener,
                                                                 ReportFragment.ReportFragmentListener,
                                                                 ReportDetailFragment.ReportDetailFragmentListener,
                                                                UserHistoryFragment.UserHistoryAndProfileListener,
                                                                ContactFragment.ContactFragmentListener {

    private Toolbar mToolbar;
    private ResideMenu resideMenu;
    private FrameLayout mFragmentDetailLayout;
    private Animation mShowReportDetailAnimation;
    private Animation mHideReportDetailAnimation;
    private boolean mReportDetailVisible = false;
    private boolean mProfileVisible = false;

    private FrameLayout mLayoutFragmentContainer;

    private ReportFragment mReportFragment;
    private ReportDetailFragment mReportDetailFragment;
    private FaqFragment mFaqFragment;
    private InformationFragment mInformationFragment;
    private PrivacyFragment mPrivacyFragment;
    private BacheInfoFragment mBacheInfoFragment;
    private ReportStatusDetailFragment mReportStatusDetailFragment;
    private UserHistoryFragment mUserHistoryFragment;
    private NewsFragment mNewsFragment;
    private ContactFragment mContactFragment;
    private TermsFragment mTermsFragment;
    private PrimaryRoutesFragment mPrimaryRoutesFragment;
    private PaymentFragment paymentFragment;

    private int currentFragmentPosition = -1;
    private TextView mTextTitle1;
    private TextView mTextTitle2;
    private Button mEditUser;
    private ImageView mImageProfile;

    private boolean mIsFromNotification = false;
    private String mBacheId = "";
    boolean driverAlertShowed = false;
    boolean showPermissionDeniedAlert = false;
    User currentUser;

    private FirebaseAuth firebaseAuth;
    private FirebaseAuth.AuthStateListener authStateListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.AppThemePink);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report);
        setupToolbar();

        mLayoutFragmentContainer = (FrameLayout) findViewById(R.id.fragment_container);

        resideMenu = new ResideMenu(this, Utils.getScreenHeight(this));
        resideMenu.setBackground(R.drawable.back_menu);
        resideMenu.attachToActivity(this);
        resideMenu.setSwipeDirectionDisable(ResideMenu.DIRECTION_RIGHT);
        resideMenu.setSwipeDirectionDisable(ResideMenu.DIRECTION_LEFT);

        mIsFromNotification = getIntent().getBooleanExtra(Constants.IS_FROM_NOTIFICATION, false);
        mBacheId = getIntent().getStringExtra(Constants.NOTIFICATION_BACHE_ID);

        String titles[] = { getResources().getString(R.string.menu_reports),
                            getResources().getString(R.string.menu_program_description),
                            getResources().getString(R.string.menu_primary_routes),
                            getResources().getString(R.string.menu_bache),
                            getResources().getString(R.string.menu_faqs),
                            getResources().getString(R.string.menu_payment),
//                            getResources().getString(R.string.menu_news),
                            getResources().getString(R.string.menu_contact),
                            getResources().getString(R.string.menu_privacy),
                            getResources().getString(R.string.menu_terms_2)
                        };

        int icon[] = {
                        R.drawable.icon_home,
                        R.drawable.icon_profile,
                        R.drawable.icon_calendar,
                        R.drawable.icon_calendar,
                        R.drawable.icon_settings,
                        R.drawable.icon_settings,
//                        R.drawable.icon_home,
                        R.drawable.icon_profile,
                        R.drawable.icon_calendar,
                        R.drawable.icon_settings
                    };

        for (int i = 0; i < titles.length; i++){
            ResideMenuItem item = new ResideMenuItem(this, icon[i], titles[i], i == (titles.length - 1));

            item.setTag(i);

            item.setOnClickListener(this);
            resideMenu.addMenuItem(item,  ResideMenu.DIRECTION_LEFT);
        }

        driverAlertShowed = true;
        showFragmentWithTag(Constants.TAG_REPORT);

        currentUser = PreferencesManager.getInstance().getUserInfo(this);

        if (currentUser != null) {
            /*ParsePush.subscribeInBackground("B24-CDMX", new SaveCallback() {
                @Override
                public void done(ParseException e) {
                    ParseInstallation installation = ParseInstallation.getCurrentInstallation();
                    installation.put("userEmail", currentUser.getEmail());
                    installation.saveInBackground();
                }
            });*/

            firebaseAuth = FirebaseAuth.getInstance();
            authStateListener = new FirebaseAuth.AuthStateListener() {

                @Override
                public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                    FirebaseUser user = firebaseAuth.getCurrentUser();
                    if (user != null) {

                        FirebaseDatabase database = FirebaseDatabase.getInstance();
//                        DatabaseReference databaseReference = database.getReference();

                        String token = FirebaseInstanceId.getInstance().getToken();
                        if (token != null) {

                            ServicesManager.sendFirebaseToken(ReportActivity.this, currentUser, token);

                            /*databaseReference
                                    .child("push")
                                    .child(currentUser.getEmail().replace(".", "-"))
                                    .child("token")
                                    .setValue(token);*/
                        }

                    }
                }
            };

            signInAnonymously();
        }

        Utils.getInstance().enableLocalizationIfNecessary(this);

        showReportDetailIfNecessary();

        setVersionName();

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                showRestrictionsAlert();
            }
        }, 1000);

        getStatesList();

        if (PreferencesManager.getInstance().showAppTentiveMessageCenter(this)) {
            PreferencesManager.getInstance().clearAppTentiveMessageCenterValues(this);
            showContactView();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        /*if (getIntent() != null && getIntent().getExtras() != null) {
            Log.i("getExtrasReport", "getExtras = " + getIntent().getExtras().toString());
        }*/
    }

    @Override
    protected void onStart() {
        super.onStart();
        firebaseAuth.addAuthStateListener(authStateListener);
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (authStateListener != null) {
            firebaseAuth.removeAuthStateListener(authStateListener);
        }
    }

    private void signInAnonymously() {
        firebaseAuth.signInAnonymously()
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
//                        Log.d("AnonymousAuth", "signInAnonymously:onComplete:" + task.isSuccessful());

                        // If sign in fails, display a message to the user. If sign in succeeds
                        // the auth state listener will be notified and logic to handle the
                        // signed in user can be handled in the listener.
                        /*if (!task.isSuccessful()) {
                            Log.w("AnonymousAuth", "signInAnonymously", task.getException());
                        }*/
                    }
                });
    }

    private void showReportDetailIfNecessary() {
        if (mIsFromNotification) {
            Intent reportDetailIntent = new Intent(this, ReportStatusDetailActivity.class);

            reportDetailIntent.putExtra(Constants.IS_FROM_NOTIFICATION, mIsFromNotification);
            reportDetailIntent.putExtra(Constants.NOTIFICATION_BACHE_ID, mBacheId);

            startActivity(reportDetailIntent);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_report, menu);

        return false;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == Constants.REPORT_DETAIL_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                if (data != null) {
                    if (mReportFragment != null)
                        mReportFragment.updateReportMarkers(data.getBooleanExtra(Constants.BUNDLE_REGISTER_REPORT_RESULT, false));
                }
            }
        } else {
            if (mUserHistoryFragment != null)
                mUserHistoryFragment.onActivityResult(requestCode, resultCode, data);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (mReportFragment != null) {
            mReportFragment.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }

        if (paymentFragment != null) {
            paymentFragment.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

    private void showProfile(boolean show) {
        Intent intentReportDetail = new Intent(this, UserHistoryActivity.class);

        startActivityForResult(intentReportDetail, Constants.REPORT_DETAIL_REQUEST_CODE);
        overridePendingTransition(R.anim.enter_from_right, R.anim.enter_from_left);
    }

    private void setupToolbar() {
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        mTextTitle1 = (TextView) findViewById(R.id.toolbar_title_1);
        mTextTitle2 = (TextView) findViewById(R.id.toolbar_title_2);
        mEditUser = (Button) findViewById(R.id.button_edit);
        mImageProfile = (ImageView) findViewById(R.id.image_profile_button);
        mEditUser.setVisibility(View.GONE);
        mEditUser.setEnabled(false);
        mEditUser.setOnClickListener(this);
        mImageProfile.setOnClickListener(this);
        mImageProfile.setVisibility(View.VISIBLE);

        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        getSupportActionBar().setDisplayShowHomeEnabled(false);
        mToolbar.setNavigationIcon(R.drawable.ic_menu);

        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                resideMenu.openMenu(ResideMenu.DIRECTION_LEFT);
            }
        });
    }

    private Intent intentReportDetail;

    @Override
    public void onClick(View view) {

        if (view.getTag() != null) {
            int menuId = (int) view.getTag();

            intentReportDetail = null;
            resideMenu.closeMenu();

            switch (menuId) {
                case 0:
                    showFragmentWithTag(Constants.TAG_REPORT);
                    break;
                case 1://Descripcion del programa
                    showFragmentWithTag(Constants.TAG_PROGRAM_DESCRIPTION);
                    break;
                case 2://Vialidades primarias
                    showFragmentWithTag(Constants.TAG_PRIMARY_ROUTES);
                    break;
                case 3://Que es un bache?
                    showFragmentWithTag(Constants.TAG_BACHE);
                    break;
                case 4://FAQ
                    showFragmentWithTag(Constants.TAG_FAQ);
                    break;
                case 5://Pago de daño
                    showFragmentWithTag(Constants.TAG_PAYMENT);
                    break;
                //case 4://Noticias  /// OCULTANDO LA SECCIÓN DE NOTICIAS; PARA DESOCULTAR INCREMENTAR A 1 LOS IDs
                //    showFragmentWithTag(Constants.TAG_NEWS);
                //    break;
                case 6://Contacto
//                    showFragmentWithTag(Constants.TAG_CONTACT);
//                    Apptentive.showMessageCenter(this);
                    showContactView();
                    break;
                case 7://Aviso de privacidad
                    showFragmentWithTag(Constants.TAG_PRIVACY);
                    break;
                case 8://Terminos de uso
                    showFragmentWithTag(Constants.TAG_TERMS);
                    break;
                case 9://Informacion sobre la app B24
                    break;
                default:
                    break;
            }
        }

        if (view.getId() == mEditUser.getId()) {
            if (mEditUser.getText().equals(getResources().getString(R.string.profile_edit))) {
                if (mUserHistoryFragment != null) {
                    mUserHistoryFragment.editUser();
                    mEditUser.setText(getResources().getString(R.string.profile_save));
                }
            } else if (mEditUser.getText().equals(getResources().getString(R.string.profile_save))) {
                if (mUserHistoryFragment != null) {
                    if (mUserHistoryFragment.validUserInfo()) {
                        mEditUser.setEnabled(false);
                        mUserHistoryFragment.endEditUser();
                    }
                }
            }
        }

        if (view.getId() == mImageProfile.getId()) {
            showProfile(!mProfileVisible);
        }
    }

    private void hideReportDetail() {

        mFragmentDetailLayout.setVisibility(View.INVISIBLE);

        if (mHideReportDetailAnimation == null) {
            mHideReportDetailAnimation = AnimationUtils.loadAnimation(this, R.anim.center_to_bottom);
            mHideReportDetailAnimation.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {

                }

                @Override
                public void onAnimationEnd(Animation animation) {
                    if (mReportFragment != null)
                        mReportFragment.newReportFinished();
                }

                @Override
                public void onAnimationRepeat(Animation animation) {

                }
            });
        }

        mReportDetailVisible = false;

        mFragmentDetailLayout.startAnimation(mHideReportDetailAnimation);
    }

    @Override
    public void onBackPressed() {

        if (mReportFragment != null) {
            if (mReportFragment.shouldInterceptOnBackPressed()) {
                return;
            }
        }

        if (currentFragmentPosition == 9 && mEditUser.getText().equals(getResources().getString(R.string.profile_save))) {
            if (mUserHistoryFragment != null) {
                if (mUserHistoryFragment.validUserInfo()) {
                    mEditUser.setEnabled(false);
                    mUserHistoryFragment.endEditUser();
                }
            }
        } else if (currentFragmentPosition == 0 ||
                currentFragmentPosition == 1 ||
                currentFragmentPosition == 2 ||
                currentFragmentPosition == 3 ||
                currentFragmentPosition == 4 ||
                currentFragmentPosition == 5 ||
                currentFragmentPosition == 6 ||
                currentFragmentPosition == 7) {
            super.onBackPressed();
        } else if (currentFragmentPosition == 8 || currentFragmentPosition == 9) {
            showFragmentWithTag(Constants.TAG_REPORT);
        } else if (currentFragmentPosition == 10) {
            showFragmentWithTag(Constants.TAG_USER_PROFILE);
        }
    }

    @Override
    public void onNewReportFinished() {
        showFragmentWithTag(Constants.TAG_REPORT);
    }

    /*@Override
    public void onTutorialClicked() {
        Intent result = new Intent();
        result.putExtra(Constants.BUNDLE_REGISTER_REPORT_RESULT, true);

        setResult(RESULT_OK, result);

        Intent reportActivityIntent = new Intent(this, TutorialActivity.class);
        reportActivityIntent.putExtra("_IS_FROM_HELP", true);

        startActivity(reportActivityIntent);
        overridePendingTransition(R.anim.enter_from_right, R.anim.enter_from_left);
        showFragmentWithTag(Constants.TAG_REPORT);
    }

    @Override
    public void onCaptureLocation() {

    }*/

    @Override
    public void onNewReportCanceled() {

    }

    private void showFragmentWithTag(String tag) {
        mEditUser.setVisibility(View.INVISIBLE);
        mImageProfile.setVisibility(View.INVISIBLE);

        switch (tag) {
            case Constants.TAG_REPORT:
                if (currentFragmentPosition != 0) {
                    currentFragmentPosition = 0;
                    if (mReportFragment == null)
                        mReportFragment = new ReportFragment();

                    showFragment(false, mReportFragment);

                    mReportFragment.updateReportMarkers(true);
                    changeTitle(true, getString(R.string.new_report_title));
                }

                if (!driverAlertShowed)
                    showDriverAlert();

                mImageProfile.setVisibility(View.VISIBLE);
                break;
            case Constants.TAG_PROGRAM_DESCRIPTION:
                if (currentFragmentPosition != 1) {
                    currentFragmentPosition = 1;
                    if (mInformationFragment == null) {
                        mInformationFragment = InformationFragment.newInstance("PROGRAM_DESCRIPTION"); //new InformationFragment();
                    } else {
                        mInformationFragment.updateText("PROGRAM_DESCRIPTION");
                    }

                    showFragment(false, mInformationFragment);
                    changeTitle(false, getString(R.string.information_title_description));
                }
                break;
            case Constants.TAG_PRIMARY_ROUTES:
                if (currentFragmentPosition != 2) {
                    currentFragmentPosition = 2;
                    if (mPrimaryRoutesFragment == null) {
                        mPrimaryRoutesFragment = PrimaryRoutesFragment.newInstance();
                    }

                    showFragment(false, mPrimaryRoutesFragment);
                    changeTitle(false, getString(R.string.menu_primary_routes));
                }
                break;
            case Constants.TAG_BACHE:
                if (currentFragmentPosition != 3) {
                    currentFragmentPosition = 3;
                    if (mBacheInfoFragment == null)
                        mBacheInfoFragment = new BacheInfoFragment();

                    showFragment(false, mBacheInfoFragment);
                    changeTitle(false, getString(R.string.information_title_bache));
                }
                break;
            case Constants.TAG_FAQ:
                if (currentFragmentPosition != 4) {
                    currentFragmentPosition = 4;
                    if (mFaqFragment == null)
                        mFaqFragment = new FaqFragment();

                    showFragment(false, mFaqFragment);
                    changeTitle(false, getString(R.string.information_title_faqs));
                }
                break;
            case Constants.TAG_PAYMENT:
                if (currentFragmentPosition != 5) {
                    currentFragmentPosition = 5;
                    if (paymentFragment == null) {
                        paymentFragment = new PaymentFragment();
                    }

                    showFragment(false, paymentFragment);
                    changeTitle(false, getString(R.string.menu_payment));
                }

                break;
            /*case Constants.TAG_NEWS:
                if (currentFragmentPosition != 5) {
                    currentFragmentPosition = 5;
                    if (mNewsFragment == null)
                        mNewsFragment = new NewsFragment();

                    showFragment(false, mNewsFragment);
                    changeTitle(false, getString(R.string.information_title_news));
                }
                break;*/
            case Constants.TAG_CONTACT:
                if (currentFragmentPosition != 6) {
                    currentFragmentPosition = 6;
                    if (mContactFragment == null)
                        mContactFragment = new ContactFragment();

                    showFragment(false, mContactFragment);
                    changeTitle(false, getString(R.string.menu_contact));
                }
                break;
            case Constants.TAG_PRIVACY:
                if (currentFragmentPosition != 7) {
                    currentFragmentPosition = 7;
                    if (mPrivacyFragment == null) {
                        mPrivacyFragment = PrivacyFragment.newInstance("PRIVACY");
                    } else {
                        mPrivacyFragment.updateText("PRIVACY");
                    }

                    showFragment(false, mPrivacyFragment);
                    changeTitle(false, getString(R.string.menu_privacy));
                }
                break;
            case Constants.TAG_TERMS:
                if (currentFragmentPosition != 8) {
                    currentFragmentPosition = 8;
                    if (mTermsFragment == null) {
                        mTermsFragment = new TermsFragment();
                    }

                    showFragment(false, mTermsFragment);
                    changeTitle(false, getString(R.string.menu_terms_2));
                }
                break;
            case Constants.TAG_REPORT_DETAIL:
                if (currentFragmentPosition != 9) {
                    currentFragmentPosition = 9;
                    if (mReportDetailFragment == null)
                        mReportDetailFragment = new ReportDetailFragment();

                    showFragment(true, mReportDetailFragment);

                    changeTitle(false, getString(R.string.report_detail_title));
                }
                break;
            case Constants.TAG_USER_PROFILE:
                if (currentFragmentPosition != 10) {
                    currentFragmentPosition = 10;
                    if (mUserHistoryFragment == null)
                        mUserHistoryFragment = new UserHistoryFragment();

                    showFragment(true, mUserHistoryFragment);

                    changeTitle(false, getString(R.string.userprofile_history_button));
                }
                break;
            case Constants.TAG_HISTORY_DETAIL:
                if (currentFragmentPosition != 11) {
                    currentFragmentPosition = 11;
                    if (mReportStatusDetailFragment == null)
                        mReportStatusDetailFragment = new ReportStatusDetailFragment();

                    showFragment(true, mReportStatusDetailFragment);
                }
                break;
            default:
                break;
        }
    }

    private void showFragment(boolean addToBackStack, Fragment fragment) {
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragment_container, fragment)
                .commit();
    }

    private void changeTitle(boolean isMainTitle, String title) {
        if (mTextTitle1 == null || mTextTitle2 == null)
            return;

        if (isMainTitle) {
            mTextTitle1.setVisibility(View.VISIBLE);
            mTextTitle2.setVisibility(View.GONE);
        } else {
            mTextTitle1.setVisibility(View.GONE);
            mTextTitle2.setVisibility(View.VISIBLE);
            mTextTitle2.setText(title);
        }
    }

    private void setVersionName() {
        String versionName = "Versión " + Utils.getVersionName(this);

        if (resideMenu != null) {
            resideMenu.setVersion(versionName);
        }
    }

    @Override
    public void onHistorySelect() {
        changeTitle(false, getResources().getString(R.string.userprofile_history_button));
        mEditUser.setVisibility(View.INVISIBLE);
        mEditUser.setEnabled(false);
    }

    @Override
    public void onProfileSelect() {
        changeTitle(false, getResources().getString(R.string.userprofile_profile_button));
        mEditUser.setVisibility(View.VISIBLE);
        mEditUser.setEnabled(true);
    }

    @Override
    public void onReportSelected(final Report report) {
        showFragmentWithTag(Constants.TAG_HISTORY_DETAIL);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                mReportStatusDetailFragment.showReportDetail(report, false);
                changeTitle(false, report.getTicket());
            }
        }, 200);
    }

    @Override
    public void onUpdateFinished() {
        mEditUser.setEnabled(true);
        mEditUser.setText(getResources().getString(R.string.profile_edit));
    }

    @Override
    public void onUpdateFailed() {
        mEditUser.setEnabled(true);
    }

    private void showRestrictionsAlert() {
        try {
            driverAlertShowed = false;
            final TwoButtonsCenteredIconAlertDialog restrictionsAlert = new TwoButtonsCenteredIconAlertDialog();

            restrictionsAlert.setupDialog(getResources().getString(R.string.alert_restrictions_title),
                    getResources().getString(R.string.alert_restrictions_message),
                    R.drawable.carita_feliz,
                    getResources().getString(R.string.alert_restrictions_go_to_details),
                    getResources().getString(R.string.alert_restrictions_ok),
                    new TwoButtonsDialogListener() {
                        @Override
                        public void onLeftButtonClick() {
                            restrictionsAlert.dismiss();
                            showFragmentWithTag(Constants.TAG_PRIMARY_ROUTES);
                        }

                        @Override
                        public void onRightButtonClick() {
                            restrictionsAlert.dismiss();
                            showDriverAlert();
                        }
                    });
            restrictionsAlert.setCancelable(false);

            FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
            fragmentTransaction.add(restrictionsAlert, null);

            fragmentTransaction.commitAllowingStateLoss();

//            restrictionsAlert.show(getFragmentManager(), "");

        } catch (Exception ex) {

        }
    }

    private void showDriverAlert() {
        try {
            driverAlertShowed = true;
            final SimpleIconCenteredAlertDialog driverAlertDialog = new SimpleIconCenteredAlertDialog();

            driverAlertDialog.setupDialog(getResources().getString(R.string.driver_title),
                    getResources().getString(R.string.driver_message),
                    R.drawable.carita_feliz,
                    getResources().getString(R.string.driver_option_continue),
                    new MessageDialogListener() {
                        @Override
                        public void onAccept() {
                            driverAlertDialog.dismiss();
                            showPaymentAlert();
//                            if (showPermissionDeniedAlert) {
//                                showDeniedLocationsPermissionsAlert();
//                            }
                        }
                    });
            driverAlertDialog.setCancelable(false);

            FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
            fragmentTransaction.add(driverAlertDialog, null);

            fragmentTransaction.commitAllowingStateLoss();

//            driverAlertDialog.show(getFragmentManager(), "");

        } catch (Exception ex) {

        }
    }

    private void showPaymentAlert() {
        final TwoButtonsCenteredIconAlertDialog paymentAlert = new TwoButtonsCenteredIconAlertDialog();

        paymentAlert.setupDialog(getResources().getString(R.string.alert_restrictions_title),
                getText(R.string.alert_payment_message).toString(),
                R.drawable.carita_feliz,
                getResources().getString(R.string.alert_payment_more_info),
                getResources().getString(R.string.alert_restrictions_ok),
                new TwoButtonsDialogListener() {
                    @Override
                    public void onLeftButtonClick() {
                        paymentAlert.dismiss();
                        showFragmentWithTag(Constants.TAG_PAYMENT);
                    }

                    @Override
                    public void onRightButtonClick() {
                        paymentAlert.dismiss();
                        if (showPermissionDeniedAlert) {
                            showDeniedLocationsPermissionsAlert();
                        }
                    }
                });
        paymentAlert.setCancelable(false);

        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.add(paymentAlert, null);

        fragmentTransaction.commitAllowingStateLoss();
    }

    private void showDeniedLocationsPermissionsAlert () {
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

    @Override
    public void onSuccessMessage() {
        showFragmentWithTag(Constants.TAG_REPORT);
    }

    @Override
    public void onNewReportStart() {
        mImageProfile.setVisibility(View.GONE);
        mToolbar.setNavigationIcon(android.R.color.transparent);
    }

    @Override
    public void onNewReportReset() {
        mImageProfile.setVisibility(View.VISIBLE);
        mToolbar.setNavigationIcon(R.drawable.ic_menu);
    }

    @Override
    public void onLocationPermissionDenied() {
        showPermissionDeniedAlert = true;
    }

    private void getStatesList() {
        ServicesManager.getStatesList(new IStatusResponse() {
            @Override
            public void onResponse(List<Status> statusList) {
                if (statusList != null) {
                    if (statusList.size() > 0) {
                        HashMap<Integer, String> statusHashMap = new HashMap<>();

                        for (int i = 0; i < statusList.size(); i++) {
                            statusHashMap.put(statusList.get(i).getId(), statusList.get(i).getName());
                        }

                        PreferencesManager.getInstance().setStatusMap(ReportActivity.this, statusHashMap);
                    }
                }
            }
        });
    }

    private void showContactView() {

        Map<String, Object> customData = new HashMap<String, Object>();
        customData.put("Sección", "Mapa");
        customData.put("Email", currentUser != null ? currentUser.getEmail() : "");

       // Apptentive.showMessageCenter(this, customData);
    }
}

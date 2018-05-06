package com.cmi.bache24.ui.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

//import com.apptentive.android.sdk.Apptentive;
import com.cmi.bache24.R;
import com.cmi.bache24.data.model.User;
import com.cmi.bache24.data.remote.ServicesManager;
import com.cmi.bache24.data.remote.interfaces.LoginCallback;
import com.cmi.bache24.ui.activity.AccessActivity;
import com.cmi.bache24.ui.activity.RegisterActivity;
import com.cmi.bache24.ui.activity.ReportActivity;
import com.cmi.bache24.ui.activity.troop.TroopMainActivity;
import com.cmi.bache24.util.Constants;
import com.cmi.bache24.util.PreferencesManager;
import com.cmi.bache24.util.Utils;
import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.Profile;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.twitter.sdk.android.Twitter;
import com.twitter.sdk.android.core.Callback;
import com.twitter.sdk.android.core.Result;
import com.twitter.sdk.android.core.TwitterException;
import com.twitter.sdk.android.core.TwitterSession;
import com.twitter.sdk.android.core.identity.TwitterLoginButton;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * A placeholder fragment containing a simple view.
 */
public class LoginFragment extends Fragment implements View.OnClickListener {

    private LinearLayout mLoginFbButton;
    private LinearLayout mLoginTwButton;
    private LinearLayout mLoginEmailButton;

    private LoginButton mFbNativeLoginButton;
    private CallbackManager mCallbackManager;

    private TwitterLoginButton mTwNativeLoginButton;
    private String mSocialNetworkSelected = "";
    private User mUserLogin;
    private boolean mLoginStarted = false;
    private LinearLayout mProgressLayout;
    private LinearLayout mRootLayout;
    private TextView mHasAccountTextView;
    //private TextView mContactTextView;

    public LoginFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_login, container, false);

        mLoginFbButton = (LinearLayout) rootView.findViewById(R.id.button_login_fb);
        mLoginTwButton = (LinearLayout) rootView.findViewById(R.id.button_login_tw);
        mLoginEmailButton = (LinearLayout) rootView.findViewById(R.id.button_login_email);

        mCallbackManager = CallbackManager.Factory.create();

        mFbNativeLoginButton = (LoginButton) rootView.findViewById(R.id.fb_login_button);
        mTwNativeLoginButton = (TwitterLoginButton) rootView.findViewById(R.id.tw_login_button);

        mProgressLayout = (LinearLayout) rootView.findViewById(R.id.progress_layout);
        mRootLayout = (LinearLayout) rootView.findViewById(R.id.root_view);
        mHasAccountTextView = (TextView) rootView.findViewById(R.id.textview_has_account);
        //mContactTextView = (TextView) rootView.findViewById(R.id.textview_contact);

        mProgressLayout.setVisibility(View.GONE);

        mLoginFbButton.setOnClickListener(this);
        mLoginTwButton.setOnClickListener(this);
        mLoginEmailButton.setOnClickListener(this);
        mHasAccountTextView.setOnClickListener(this);
        //mContactTextView.setOnClickListener(this);

        setupFacebookLoginButton();
        setupTwitterLogin();

        mUserLogin = new User();

        mRootLayout.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                Utils.hideKeyboard(getActivity(), view);
                return false;
            }
        });

        if (PreferencesManager.getInstance().showAppTentiveMessageCenter(getActivity())) {
            PreferencesManager.getInstance().clearAppTentiveMessageCenterValues(getActivity());
            showContactView();
        }

        return rootView;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (mSocialNetworkSelected.equals(Constants.FACEBOOK_SOCIAL_NETWORK)) {
            mSocialNetworkSelected = "";
            mCallbackManager.onActivityResult(requestCode, resultCode, data);
        } else if (mSocialNetworkSelected.equals(Constants.TWITTER_SOCIAL_NETWORK)) {
            mSocialNetworkSelected = "";
            mTwNativeLoginButton.onActivityResult(requestCode, resultCode, data);
        }
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == mLoginFbButton.getId()) {

            if (!Utils.getInstance().isInternetAvailable(getActivity()))
                return;

            mSocialNetworkSelected = Constants.FACEBOOK_SOCIAL_NETWORK;

            AccessToken accessToken = AccessToken.getCurrentAccessToken();
            if (accessToken == null)
                mFbNativeLoginButton.performClick();
            else
                getFacebookUserAccount(AccessToken.getCurrentAccessToken());
        } else if (view.getId() == mLoginTwButton.getId()) {
            if (!Utils.getInstance().isInternetAvailable(getActivity()))
                return;

            mSocialNetworkSelected = Constants.TWITTER_SOCIAL_NETWORK;
            mTwNativeLoginButton.performClick();
        } else if (view.getId() == mLoginEmailButton.getId()) {
            mUserLogin.setRegisterType(Constants.REGISTER_EMAIL);
            startRegisterActivityWithUserInfo(false, mUserLogin);
        } else if (view.getId() == mHasAccountTextView.getId()) {
            startLoginActivity();
//        } else if (view.getId() == mContactTextView.getId()) {
//            showContactView();
        }
    }

    private void setupFacebookLoginButton() {

        mFbNativeLoginButton.setReadPermissions("public_profile", "email");
        mFbNativeLoginButton.setFragment(this);
        mFbNativeLoginButton.registerCallback(mCallbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                getFacebookUserAccount(loginResult.getAccessToken());
            }

            @Override
            public void onCancel() {

            }

            @Override
            public void onError(FacebookException e) {

            }
        });
    }

    private void loginWithFbToServer(final User user) {
        User userLogin = user;

        userLogin.setHashToken(Utils.getInstance().getHashForToken(user.getFbToken()));

        ServicesManager.loginWithUser(userLogin, new LoginCallback() {
            @Override
            public void loginSuccess(User userInfo) {
                mProgressLayout.setVisibility(View.GONE);
                PreferencesManager.getInstance().setUserInfo(getActivity(), userInfo);

                showMainActivity(userInfo);
            }

            @Override
            public void loginFail(String message) {
                if (getActivity() != null) {
                    if (message != "") {
                        startRegisterActivityWithUserInfo(true, user);
                    }
                    mProgressLayout.setVisibility(View.GONE);
                }
            }

            @Override
            public void userBanned() {
                mProgressLayout.setVisibility(View.GONE);
                Utils.showUserBannedDialog(getActivity(), false);
            }

            @Override
            public void onTokenDisabled() {

            }
        });
    }

    private void loginWithTwToServer(final User user) {
        User userLogin = user;

        userLogin.setHashToken(Utils.getInstance().getHashForToken(user.getTwToken()));

        ServicesManager.loginWithUser(userLogin, new LoginCallback() {
            @Override
            public void loginSuccess(User userInfo) {
                mProgressLayout.setVisibility(View.GONE);
                PreferencesManager.getInstance().setUserInfo(getActivity(), userInfo);

                showMainActivity(userInfo);
            }

            @Override
            public void loginFail(String message) {

                if (getActivity() != null) {
                    if (message != "") {
                        startRegisterActivityWithUserInfo(true, user);
                    }
                    mProgressLayout.setVisibility(View.GONE);
                }
            }

            @Override
            public void userBanned() {
                mProgressLayout.setVisibility(View.GONE);
                Utils.showUserBannedDialog(getActivity(), false);
            }

            @Override
            public void onTokenDisabled() {

            }
        });
    }

    private void showMainActivity(User userInfo) {
        if (!mLoginStarted) {
            mLoginStarted = true;

            Intent userMainActivityIntent = null;

            if (userInfo.getUserType() == Constants.USER_TYPE_CITIZEN)
                userMainActivityIntent = new Intent(getActivity(), ReportActivity.class);
            else if (userInfo.getUserType() == Constants.USER_TYPE_TROOP)
                userMainActivityIntent = new Intent(getActivity(), TroopMainActivity.class);
            else
                userMainActivityIntent = new Intent(getActivity(), ReportActivity.class);

            userMainActivityIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(userMainActivityIntent);
            getActivity().overridePendingTransition(R.anim.enter_from_right, R.anim.enter_from_left);
        }
    }

    private void getFacebookUserAccount(final AccessToken token) {

        mProgressLayout.setVisibility(View.VISIBLE);

        GraphRequest request = GraphRequest.newMeRequest(token, new GraphRequest.GraphJSONObjectCallback() {

            @Override
            public void onCompleted(JSONObject jsonObject, GraphResponse graphResponse) {

                if (Profile.getCurrentProfile() != null) {
                    try {

                        mUserLogin.setId(Profile.getCurrentProfile().getId());
                        mUserLogin.setName(Profile.getCurrentProfile().getFirstName());

                        if (graphResponse != null) {
                            if (graphResponse.getJSONObject() != null) {
                                if (graphResponse.getJSONObject().has("email")) {
                                    mUserLogin.setEmail(graphResponse.getJSONObject().getString("email"));
                                }
                            }
                        }

                        mUserLogin.setFirtsLastName(Profile.getCurrentProfile().getLastName());
                        mUserLogin.setSecondLastName("");
                        mUserLogin.setFbUsername(Profile.getCurrentProfile().getName());
                        mUserLogin.setFbToken(Profile.getCurrentProfile().getId());
                        mUserLogin.setTwToken("");
                        mUserLogin.setHashToken("");

                    } catch (JSONException ex) {
                        Toast.makeText(getActivity(), "No pudimos acceder a tu cuenta", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    mUserLogin.setRegisterType(Constants.REGISTER_FACEBOOK);

                    loginWithFbToServer(mUserLogin);
                } else {
                    Toast.makeText(getActivity(), "No pudimos acceder a tu cuenta", Toast.LENGTH_SHORT).show();
                }
            }
        });

        Bundle parameters = new Bundle();
        parameters.putString("fields", "id,name,email,picture");
        request.setParameters(parameters);
        request.executeAsync();
    }

    private void setupTwitterLogin() {
        mTwNativeLoginButton.setCallback(new Callback<TwitterSession>() {
            @Override
            public void success(Result<TwitterSession> result) {
                getTwitterUserAccount(result);
            }

            @Override
            public void failure(TwitterException e) {

            }
        });
    }

    private void getTwitterUserAccount(final Result<TwitterSession> result1) {

        mProgressLayout.setVisibility(View.VISIBLE);

        Twitter.getApiClient().getAccountService().verifyCredentials(true, false, new Callback<com.twitter.sdk.android.core.models.User>() {
            @Override
            public void success(Result<com.twitter.sdk.android.core.models.User> result) {

                mUserLogin.setId(String.valueOf(result.data.id));
                mUserLogin.setName(result.data.name);
                mUserLogin.setFirtsLastName("");
                mUserLogin.setSecondLastName("");
                mUserLogin.setEmail(result.data.email);
                mUserLogin.setPicture(result.data.profileImageUrl.replace("_normal", "_bigger"));
                mUserLogin.setRegisterType(Constants.REGISTER_TWITTER);
                mUserLogin.setTwToken(String.valueOf(result.data.id));
                mUserLogin.setTwUsername(result1.data.getUserName());
                mUserLogin.setFbToken("");
                mUserLogin.setHashToken("");

                loginWithTwToServer(mUserLogin);
            }

            @Override
            public void failure(TwitterException e) {

            }
        });
    }

    private void startRegisterActivityWithUserInfo(boolean isFromSocialNetwork, User userData) {
        Intent intentRegister = new Intent(getActivity(), RegisterActivity.class);

        intentRegister.putExtra(Constants.EXTRA_LOGIN_FROM_SOCIAL_NETWORK, isFromSocialNetwork);
        intentRegister.putExtra(Constants.EXTRA_REGISTER_USER_DATA, userData);

        startActivity(intentRegister);
        getActivity().overridePendingTransition(R.anim.enter_from_right, R.anim.enter_from_left);
    }

    private void startLoginActivity() {
        Intent intentRegister = new Intent(getActivity(), AccessActivity.class);

        intentRegister.putExtra(Constants.EXTRA_SHOULD_SHOW_ACTIVATION_MESSAGE, false);

        startActivity(intentRegister);
        getActivity().overridePendingTransition(R.anim.enter_from_right, R.anim.enter_from_left);
    }

    private void showContactView() {
       // Apptentive.showMessageCenter(getActivity());
    }
}

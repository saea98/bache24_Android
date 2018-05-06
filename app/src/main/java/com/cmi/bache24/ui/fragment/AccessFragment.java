package com.cmi.bache24.ui.fragment;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

//import com.apptentive.android.sdk.Apptentive;
import com.cmi.bache24.R;
import com.cmi.bache24.data.model.User;
import com.cmi.bache24.data.remote.ServicesManager;
import com.cmi.bache24.data.remote.interfaces.LoginCallback;
import com.cmi.bache24.ui.activity.LoginActivity;
import com.cmi.bache24.ui.activity.RecoverPasswordActivity;
import com.cmi.bache24.ui.activity.ReportActivity;
import com.cmi.bache24.ui.activity.troop.TroopMainActivity;
import com.cmi.bache24.util.Constants;
import com.cmi.bache24.util.PreferencesManager;
import com.cmi.bache24.util.Utils;

import java.util.HashMap;
import java.util.Map;

/**
 * A placeholder fragment containing a simple view.
 */
public class AccessFragment extends Fragment implements View.OnClickListener {

    private TextView mActivationAccountTitleTextView;
    private TextView mActivationAccountMessageTextView;
    private TextView mForgotPasswordText;
    private EditText mUserNameEdit;
    private EditText mPasswordEdit;
    private Button mLoginButton;
    private TextView mRegistrationTextView;

    private User mUserLogin;
    private boolean mLoginStarted = false;
    private LinearLayout mProgressLayout;
    private LinearLayout mRootLayout;
    //private TextView mContactTextView;

    private boolean mShouldShowActivationMessage = false;

    public AccessFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_access, container, false);

        mActivationAccountTitleTextView = (TextView) rootView.findViewById(R.id.textview_activation_account_title);
        mActivationAccountMessageTextView = (TextView) rootView.findViewById(R.id.textview_activation_account_message);

        mForgotPasswordText = (TextView) rootView.findViewById(R.id.textview_forgot_password);
        mUserNameEdit = (EditText) rootView.findViewById(R.id.edittext_user);
        mPasswordEdit = (EditText) rootView.findViewById(R.id.edittext_password);
        mLoginButton = (Button) rootView.findViewById(R.id.button_login);

        mProgressLayout = (LinearLayout) rootView.findViewById(R.id.progress_layout);
        mRootLayout = (LinearLayout) rootView.findViewById(R.id.root_view);

        mRegistrationTextView = (TextView) rootView.findViewById(R.id.textview_register);
        //mContactTextView = (TextView) rootView.findViewById(R.id.textview_contact);

        mProgressLayout.setVisibility(View.GONE);

        mForgotPasswordText.setOnClickListener(this);
        mLoginButton.setOnClickListener(this);
        mRegistrationTextView.setOnClickListener(this);
        //mContactTextView.setOnClickListener(this);

        mUserLogin = new User();

        mRootLayout.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                Utils.hideKeyboard(getActivity(), view);
                return false;
            }
        });

        showActivationMessageIfNecessary();

        return rootView;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == mLoginButton.getId()) {
            login();
        } else if (view.getId() == mForgotPasswordText.getId()) {
            Intent recoverPasswordActivityIntent = new Intent(getActivity(), RecoverPasswordActivity.class);
            startActivity(recoverPasswordActivityIntent);
            getActivity().overridePendingTransition(R.anim.enter_from_right, R.anim.enter_from_left);
        } else if (view.getId() == mRegistrationTextView.getId()) {
            Intent loginActivityIntent = new Intent(getActivity(), LoginActivity.class);
            loginActivityIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);

            startActivity(loginActivityIntent);
            getActivity().overridePendingTransition(R.anim.enter_from_right, R.anim.enter_from_left);
//        } else if (view.getId() == mContactTextView.getId()) {
//            showContactView();
        }
    }

    private void login() {
        if (!Utils.getInstance().isInternetAvailable(getActivity()))
            return;

        if (mUserNameEdit.getText().toString().trim().isEmpty()) {
            Toast.makeText(getActivity(), "Ingresa tu usuario", Toast.LENGTH_SHORT).show();
            return;
        }

        if (mPasswordEdit.getText().toString().trim().isEmpty()) {
            Toast.makeText(getActivity(), "Ingresa tu contraseña", Toast.LENGTH_SHORT).show();
            return;
        }

        mProgressLayout.setVisibility(View.VISIBLE);

        View view = getActivity().getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }

        User userLogin = new User();
        userLogin.setEmail(mUserNameEdit.getText().toString().trim());
        userLogin.setPassword(mPasswordEdit.getText().toString().trim());

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
                        Toast.makeText(getActivity(), message, Toast.LENGTH_SHORT).show();
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

            if (userInfo == null)
                return;

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

    public void setShouldShowActivationMessage(boolean shouldShowActivationMessage) {
        this.mShouldShowActivationMessage = shouldShowActivationMessage;
        showActivationMessageIfNecessary();
    }

    private void showActivationMessageIfNecessary() {

        if (mActivationAccountTitleTextView != null && mActivationAccountMessageTextView != null
            && mForgotPasswordText != null && mRegistrationTextView != null) {
            mActivationAccountTitleTextView.setVisibility(mShouldShowActivationMessage ? View.VISIBLE : View.GONE);
            mActivationAccountMessageTextView.setVisibility(mShouldShowActivationMessage ? View.VISIBLE : View.GONE);
            mForgotPasswordText.setVisibility(mShouldShowActivationMessage ? View.GONE : View.VISIBLE);
            mRegistrationTextView.setVisibility(mShouldShowActivationMessage ? View.GONE : View.VISIBLE);
        }
    }

    private void showContactView() {

        Map<String, Object> customData = new HashMap<String, Object>();
        customData.put("Sección", "Inicio de sesión - Email");

       // Apptentive.showMessageCenter(getActivity(), customData);
    }
}

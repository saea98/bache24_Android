package com.cmi.bache24.ui.fragment;

import android.content.Context;
import android.content.Intent;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.cmi.bache24.R;
import com.cmi.bache24.data.model.User;
import com.cmi.bache24.data.remote.ServicesManager;
import com.cmi.bache24.data.remote.interfaces.LoginCallback;
import com.cmi.bache24.data.remote.interfaces.RecoverPasswordCallback;
import com.cmi.bache24.ui.activity.ReportActivity;
import com.cmi.bache24.ui.activity.troop.TroopMainActivity;
import com.cmi.bache24.util.Constants;
import com.cmi.bache24.util.PreferencesManager;
import com.cmi.bache24.util.Utils;

/**
 * A placeholder fragment containing a simple view.
 */
public class RecoverPasswordFragment extends Fragment implements View.OnClickListener {

    private LinearLayout layoutContainer1;
    private LinearLayout layoutContainer2;
    private EditText editMail;
    private Button buttonCancel;
    private Button buttonRecover;

    private EditText editPassword;
    private Button buttonReturn;
    private Button buttonEnter;
    private View progressView;
    private User userLogin;
    private LinearLayout mRootLayout;

    public RecoverPasswordFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_recover_password, container, false);

        layoutContainer1 = (LinearLayout) rootView.findViewById(R.id.layout_enter_mail);
        layoutContainer2 = (LinearLayout) rootView.findViewById(R.id.layout_enter_new_password);
        editMail = (EditText) rootView.findViewById(R.id.edittext_email);
        buttonCancel = (Button) rootView.findViewById(R.id.button_cancel);
        buttonRecover = (Button) rootView.findViewById(R.id.button_recover);

        editPassword = (EditText) rootView.findViewById(R.id.edit_new_password);
        buttonReturn = (Button) rootView.findViewById(R.id.button_return);
        buttonEnter = (Button) rootView.findViewById(R.id.button_login);
        progressView = rootView.findViewById(R.id.progress_layout);
        mRootLayout = (LinearLayout) rootView.findViewById(R.id.root_view);

        progressView.setVisibility(View.GONE);

        buttonCancel.setOnClickListener(this);
        buttonRecover.setOnClickListener(this);
        buttonReturn.setOnClickListener(this);
        buttonEnter.setOnClickListener(this);

        layoutContainer1.setVisibility(View.VISIBLE);
        layoutContainer2.setVisibility(View.GONE);

        mRootLayout.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                Utils.hideKeyboard(getActivity(), view);
                return false;
            }
        });

        return rootView;
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == buttonCancel.getId() || view.getId() == buttonReturn.getId()) {
            getActivity().finish();
        }

        if (view.getId() == buttonRecover.getId()) {
            recoverPasword();
        }

        if (view.getId() == buttonEnter.getId()) {
            login();
        }
    }

    private void recoverPasword() {
        if (!Utils.getInstance().isInternetAvailable(getActivity()))
            return;

        if (editMail.getText().toString().trim().isEmpty()) {
            Toast.makeText(getActivity(), "Ingresa tu e-mail", Toast.LENGTH_SHORT).show();
            return;
        }

        userLogin = new User();
        userLogin.setEmail(editMail.getText().toString().trim());

        progressView.setVisibility(View.VISIBLE);

        ServicesManager.recoverPassword(userLogin, new RecoverPasswordCallback() {
            @Override
            public void onRecoverSuccess() {
                progressView.setVisibility(View.GONE);
                layoutContainer1.setVisibility(View.GONE);
                layoutContainer2.setVisibility(View.VISIBLE);
            }

            @Override
            public void onRecoverFail(String message) {
                progressView.setVisibility(View.GONE);
                Toast.makeText(getActivity(), message, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void userBanned() {
                Utils.showLogin(getActivity());
            }

            @Override
            public void onTokenDisabled() {

            }
        });
    }

    private void login() {
        if (!Utils.getInstance().isInternetAvailable(getActivity()))
            return;

        if (editPassword.getText().toString().trim().isEmpty()) {
            Toast.makeText(getActivity(), "Ingresa tu contraseña", Toast.LENGTH_SHORT).show();
            return;
        }

        progressView.setVisibility(View.VISIBLE);

        View view = getActivity().getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }

        User userLogin = new User();
        userLogin.setEmail(editMail.getText().toString().trim());
        userLogin.setPassword(editPassword.getText().toString().trim());

        ServicesManager.loginWithUser(userLogin, new LoginCallback() {
            @Override
            public void loginSuccess(User userInfo) {
                progressView.setVisibility(View.GONE);
                PreferencesManager.getInstance().setUserInfo(getActivity(), userInfo);
                showMainActivity(userInfo);
            }

            @Override
            public void loginFail(String message) {
                if (getActivity() != null) {
                    Toast.makeText(getActivity(), message, Toast.LENGTH_SHORT).show();
                    progressView.setVisibility(View.GONE);
                }
            }

            @Override
            public void userBanned() {
                Utils.showLogin(getActivity());
            }

            @Override
            public void onTokenDisabled() {

            }
        });
    }

    private void showMainActivity(User userInfo) {
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

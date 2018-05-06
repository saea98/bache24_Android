package com.cmi.bache24.ui.fragment;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.cmi.bache24.R;
import com.cmi.bache24.data.model.Report;
import com.cmi.bache24.data.model.User;
import com.cmi.bache24.data.remote.ServicesManager;
import com.cmi.bache24.data.remote.interfaces.RegisterUserCallback;
import com.cmi.bache24.data.remote.interfaces.ReportsCallback;
import com.cmi.bache24.ui.activity.LoginActivity;
import com.cmi.bache24.ui.adapter.ReportsAdapter;
import com.cmi.bache24.util.Constants;
import com.cmi.bache24.util.ImagePicker;
import com.cmi.bache24.util.PreferencesManager;
import com.cmi.bache24.util.Utils;
import com.squareup.picasso.Picasso;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

//import android.app.AlertDialog;

/**
 * A placeholder fragment containing a simple view.
 */
public class UserHistoryFragment extends BaseFragment implements View.OnClickListener {

    private CircleImageView mProfilePicture;
    private TextView mTextViewName;
    private TextView mNoReports;
    private Button mButtonHistory;
    private Button mButtonProfile;
    private LinearLayout mLayoutHistory;
    private RelativeLayout mLayoutProfile;
    private RecyclerView mReportsList;
    private TextView mTextViewPhoneNumber;
    private TextView mTextViewEmail;
    private Button mButtonLogout;
    private CheckBox mCheckNotifications;
    private LinearLayout mLayoutNormal;
    private ReportsAdapter mReportsAdapter;
    private User mCurrentUser;
    private List<Report> mReports;

    //PARA EDICION
    private CircleImageView mImageProfilePicture;
    private Button mButtonAddPicture;
    private EditText mEditName;
    private EditText mEditLastName;
    private EditText mEditEmail;
    private EditText mEditPhone;
    private EditText mEditOldPassword;
    private EditText mEditNewPassword;
    private ImageButton mImageFB;
    private ImageButton mImageTW;
    private RelativeLayout mLayoutProfileEdit;
    private View progressView;

    private UserHistoryAndProfileListener mUserHistoryAndProfileListener;
    private static final int PICK_IMAGE_ID = 234;
    private Context mCurrentContext;

    public UserHistoryFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_user_history, container, false);

        mProfilePicture = (CircleImageView) root.findViewById(R.id.image_profile_picture);
        mTextViewName = (TextView) root.findViewById(R.id.textview_full_username);
        mNoReports = (TextView) root.findViewById(R.id.textview_no_reports);
        mButtonHistory = (Button) root.findViewById(R.id.button_history);
        mButtonProfile = (Button) root.findViewById(R.id.button_profile);
        mLayoutHistory = (LinearLayout) root.findViewById(R.id.layout_history);
        mLayoutProfile = (RelativeLayout) root.findViewById(R.id.layout_profile);
        mReportsList = (RecyclerView) root.findViewById(R.id.report_list);
        mTextViewPhoneNumber = (TextView) root.findViewById(R.id.textview_phone_number);
        mTextViewEmail = (TextView) root.findViewById(R.id.textview_email);
        mButtonLogout = (Button) root.findViewById(R.id.button_logout);
        mCheckNotifications = (CheckBox) root.findViewById(R.id.check_notifications);
        mLayoutNormal = (LinearLayout) root.findViewById(R.id.layout_profile_normal);

        //EDICION
        mImageProfilePicture = (CircleImageView) root.findViewById(R.id.image_profile_picture_edit);
        mButtonAddPicture = (Button) root.findViewById(R.id.button_add_picture);
        mEditName = (EditText) root.findViewById(R.id.edittext_name);
        mEditLastName = (EditText) root.findViewById(R.id.edittext_lastname);
        mEditEmail = (EditText) root.findViewById(R.id.edittext_email);
        mEditPhone = (EditText) root.findViewById(R.id.edittext_phone_number);
        mEditOldPassword = (EditText) root.findViewById(R.id.edittext_current_password);
        mEditNewPassword = (EditText) root.findViewById(R.id.edittext_new_password);
        mImageFB = (ImageButton) root.findViewById(R.id.image_button_fb);
        mImageTW = (ImageButton) root.findViewById(R.id.image_button_tw);
        mLayoutProfileEdit = (RelativeLayout) root.findViewById(R.id.layout_profile_edit);
        progressView = root.findViewById(R.id.progress_layout);
        progressView.setVisibility(View.GONE);

        mButtonAddPicture.setOnClickListener(this);
        //EDICION

        mReportsAdapter = new ReportsAdapter(getActivity());

        mButtonHistory.setOnClickListener(this);
        mButtonProfile.setOnClickListener(this);
        mButtonLogout.setOnClickListener(this);

        mCurrentUser = PreferencesManager.getInstance().getUserInfo(getActivity());

        final float scale = this.getResources().getDisplayMetrics().density;
        mCheckNotifications.setPadding(mCheckNotifications.getPaddingLeft() + (int) (10.0f * scale + 0.5f),
                mCheckNotifications.getPaddingTop(),
                mCheckNotifications.getPaddingRight(),
                mCheckNotifications.getPaddingBottom());

//        Log.i("NotificaitonsEn", "Are notifications enabled? " + PreferencesManager.getInstance().notificationsEnabled(mCurrentContext));

        mCheckNotifications.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                PreferencesManager.getInstance().setNotificationsEnabled(mCurrentContext, isChecked);
            }
        });

        mCheckNotifications.setChecked(PreferencesManager.getInstance().notificationsEnabled(mCurrentContext));

        mLayoutNormal.setVisibility(View.VISIBLE);
        mLayoutProfileEdit.setVisibility(View.GONE);
        mNoReports.setText("");

        showUserInfo();
        loadReports();
        showViewAtPosition(0);

        return root;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        mCurrentContext = context;

        try{
            if (context instanceof Activity) {
                mUserHistoryAndProfileListener = (UserHistoryAndProfileListener) context;
            }
        } catch (ClassCastException ex) {

        }
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == mButtonHistory.getId()) {
            showViewAtPosition(0);
            if (mUserHistoryAndProfileListener != null)
                mUserHistoryAndProfileListener.onHistorySelect();
        } else if (view.getId() == mButtonProfile.getId()) {
            showViewAtPosition(1);
            if (mUserHistoryAndProfileListener != null)
                mUserHistoryAndProfileListener.onProfileSelect();
        } else if (view.getId() == mButtonLogout.getId()) {
            logout();
        } else if (view.getId() == mButtonAddPicture.getId()) {
            checkCameraPermissions();
        }
    }

    private void logout() {
        AlertDialog alertDialog = new AlertDialog.Builder(getActivity()).create();
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

                        PreferencesManager.getInstance().logoutSession(getActivity());
                        PreferencesManager.getInstance().removeReports(getActivity());

                        Intent reportActivityIntent = new Intent(getActivity(), LoginActivity.class);
                        reportActivityIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(reportActivityIntent);
                        getActivity().overridePendingTransition(R.anim.enter_from_right, R.anim.enter_from_left);
                    }
                });

        alertDialog.show();

    }

    private void removeFromParse() {
        /*ParseInstallation installation = ParseInstallation.getCurrentInstallation();
        installation.put("userEmail", "");
        installation.saveInBackground();*/
    }

    private void showUserInfo() {
        if (mCurrentUser == null)
            return;

        mTextViewName.setText((mCurrentUser.getName() != null ? mCurrentUser.getName() : "") + " "
                                + (mCurrentUser.getFirtsLastName() != null ? mCurrentUser.getFirtsLastName() : "") + " "
                                + (mCurrentUser.getSecondLastName() != null ? mCurrentUser.getSecondLastName() : ""));

        if (mCurrentUser.getPictureUrl() != null) {
            if (!mCurrentUser.getPictureUrl().isEmpty()) {
                Picasso.with(getActivity())
                        .load(mCurrentUser.getPictureUrl())
                        .placeholder(R.drawable.user_registro)
                        .error(R.drawable.user_registro).into(mProfilePicture);

                Picasso.with(getActivity())
                        .load(mCurrentUser.getPictureUrl())
                        .placeholder(R.drawable.user_registro)
                        .error(R.drawable.user_registro).into(mImageProfilePicture);
            }
        }

        mTextViewPhoneNumber.setText(mCurrentUser.getPhone());
        mTextViewEmail.setText(mCurrentUser.getEmail());
    }

    private void loadReports() {

        if (!Utils.getInstance().isInternetAvailable(getActivity()))
            return;

        progressView.setVisibility(View.VISIBLE);
        ServicesManager.getReports(mCurrentUser, new ReportsCallback() {
            @Override
            public void onReportsCallback(List<Report> reports) {

                progressView.setVisibility(View.GONE);
                mReports = reports;
                mNoReports.setText(mReports.size() + (mReports.size() == 1 ? " reporte" : " reportes"));

                if (reports.size() == 0) {
                    showEmptyReportsDialog();
                    return;
                }

                mReportsAdapter.addAllSections(mReports);
                mReportsList.setLayoutManager(new LinearLayoutManager(getActivity()));
                mReportsList.setAdapter(mReportsAdapter);
                mReportsAdapter.setReportsListener(new ReportsAdapter.OnReportsListener() {
                    @Override
                    public void onReportClick(Report report) {
                        mUserHistoryAndProfileListener.onReportSelected(report);
                    }
                });
            }

            @Override
            public void onReportsFail(String message) {
                progressView.setVisibility(View.GONE);

                /*Log.i("BACHE_TIMEOUT", "Message 1");*/

                if (mCurrentContext != null) {

                    /*Log.i("BACHE_TIMEOUT", "Message 2");*/

                    if (message != "") {
                        /*Log.i("BACHE_TIMEOUT", "Message 3");*/
                        Toast.makeText(mCurrentContext, message, Toast.LENGTH_SHORT).show();
                    }
                }
            }

            @Override
            public void userBanned() {
                Utils.showLogin(getActivity());
            }

            @Override
            public void onTokenDisabled() {
                Utils.showLoginForBadToken(getActivity());
            }
        });
    }

    private void showEmptyReportsDialog() {
        if (getActivity() == null)
            return;

        AlertDialog.Builder dialog = new AlertDialog.Builder(getActivity());

        dialog.setMessage("No has enviado reportes");
        dialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

            }
        });
        dialog.show();
    }

    private void showViewAtPosition(int position) {
        mLayoutHistory.setVisibility(View.GONE);
        mLayoutProfile.setVisibility(View.GONE);

        if (position == 0) {
            mButtonHistory.setBackgroundResource(R.drawable.btn_activo);
            mButtonProfile.setBackgroundResource(R.drawable.btn_inactivo);
            mButtonHistory.setTextColor(getResources().getColor(R.color.text_color_white));
            mButtonProfile.setTextColor(getResources().getColor(R.color.primary));

            mLayoutHistory.setVisibility(View.VISIBLE);
        } else {
            mButtonHistory.setBackgroundResource(R.drawable.btn_inactivo);
            mButtonProfile.setBackgroundResource(R.drawable.btn_activo);
            mButtonHistory.setTextColor(getResources().getColor(R.color.primary));
            mButtonProfile.setTextColor(getResources().getColor(R.color.text_color_white));

            mLayoutProfile.setVisibility(View.VISIBLE);
        }
    }

    public void editUser() {
        mLayoutNormal.setVisibility(View.GONE);
        mLayoutProfileEdit.setVisibility(View.VISIBLE);

        mEditName.setText(mCurrentUser.getName() != null ? mCurrentUser.getName() : "");
        mEditLastName.setText(mCurrentUser.getFirtsLastName() != null ? mCurrentUser.getFirtsLastName() : "");

        mEditEmail.setText(mCurrentUser.getEmail());
        mEditPhone.setText(mCurrentUser.getPhone());
    }

    public boolean validUserInfo() {
        if (mEditEmail.getText().toString().trim().isEmpty()) {
            Toast.makeText(getActivity(), "Ingresa tu e-mail", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (!Utils.isValidEmail(mEditEmail.getText().toString())) {
            Toast.makeText(getActivity(), "Ingresa tu e-mail", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (mEditPhone.getText().toString().trim().isEmpty()) {
            Toast.makeText(getActivity(), "Ingresa tu teléfono", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (mEditPhone.getText().toString().trim().length() < 8) {
            Toast.makeText(getActivity(), "El teléfono debe tener entre 8 y 10 digitos", Toast.LENGTH_SHORT).show();
            return false;
        }

        return true;
    }

    public void endEditUser() {

        if (!Utils.getInstance().isInternetAvailable(getActivity()))
            return;

        User updatedUser = mCurrentUser;
        updatedUser.setPhone(mEditPhone.getText().toString().trim());
        updatedUser.setEmail(mEditEmail.getText().toString().trim());

        if (!mEditOldPassword.getText().toString().isEmpty()) {
            if (!mEditNewPassword.getText().toString().isEmpty()) {
                updatedUser.setOldPassword(mEditOldPassword.getText().toString());
                updatedUser.setPassword(mEditNewPassword.getText().toString());
            } else
                updatedUser.setPassword("");
        } else
            updatedUser.setPassword("");

        updatedUser.setPicture("");

        progressView.setVisibility(View.VISIBLE);

        Bitmap bitmapProfilePicture = null;

        bitmapProfilePicture = ((BitmapDrawable)mImageProfilePicture.getDrawable()).getBitmap();

        updatedUser.setPicture(bitmapProfilePicture != null ? Utils.bitmapToBase64(bitmapProfilePicture, Constants.PICTURE_CITIZEN_QUALITY) : "");

        ServicesManager.updateUser(updatedUser, new RegisterUserCallback() {
            @Override
            public void onRegisterSuccess(User userComplete) {
                progressView.setVisibility(View.GONE);

                PreferencesManager.getInstance().setUserInfo(getActivity(), userComplete);
                showUserInfo();
                mLayoutNormal.setVisibility(View.VISIBLE);
                mLayoutProfileEdit.setVisibility(View.GONE);
                if (mUserHistoryAndProfileListener != null)
                    mUserHistoryAndProfileListener.onUpdateFinished();
            }

            @Override
            public void onRegisterFail(String message) {
                progressView.setVisibility(View.GONE);
                if (mUserHistoryAndProfileListener != null)
                    mUserHistoryAndProfileListener.onUpdateFailed();
            }

            @Override
            public void userBanned() {
                Utils.showLogin(getActivity());
            }

            @Override
            public void onTokenDisabled() {
                Utils.showLoginForBadToken(getActivity());
            }
        });
    }

    public void cancelEditUser() {
        showUserInfo();
        mLayoutNormal.setVisibility(View.VISIBLE);
        mLayoutProfileEdit.setVisibility(View.GONE);
        if (mUserHistoryAndProfileListener != null)
            mUserHistoryAndProfileListener.onUpdateFinished();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_ID && resultCode == getActivity().RESULT_OK) {
            Bitmap bitmap = ImagePicker.getImageFromResult(getActivity(), resultCode, data, "", "profile_picture.jpg");
            mImageProfilePicture.setImageBitmap(bitmap);
            mImageProfilePicture.setScaleType(ImageView.ScaleType.CENTER_CROP);
        }
    }

    private void checkCameraPermissions() {
        checkPermissionsForAction(new String[] { Manifest.permission.CAMERA,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE },
                ACTION_TAKE_OR_CHOOSE_PICTURE,
                "Los permisos para tomar fotos están desactivados");
    }

    @Override
    public void doAllowedStuffWithPermissionGrantedForAction(String action) {
        Intent chooseImageIntent = ImagePicker.getPickImageIntent(getActivity(), "", "profile_picture.jpg");
        startActivityForResult(chooseImageIntent, PICK_IMAGE_ID);
    }

    @Override
    public void onPermissionDenied(String message) {
        Toast.makeText(mCurrentContext, message, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onPermissionDeniedPermanently(String message) {
        Toast.makeText(mCurrentContext, message, Toast.LENGTH_SHORT).show();
    }

    public interface UserHistoryAndProfileListener {
        void onHistorySelect();
        void onProfileSelect();
        void onReportSelected(Report report);
        void onUpdateFinished();
        void onUpdateFailed();
    }
}

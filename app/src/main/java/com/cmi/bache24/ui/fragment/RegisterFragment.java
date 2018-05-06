package com.cmi.bache24.ui.fragment;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.cmi.bache24.R;
import com.cmi.bache24.data.model.User;
import com.cmi.bache24.ui.activity.LegalsActivity;
import com.cmi.bache24.util.Constants;
import com.cmi.bache24.util.ImagePicker;
import com.cmi.bache24.util.PreferencesManager;
import com.cmi.bache24.util.Utils;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * A placeholder fragment containing a simple view.
 */
public class RegisterFragment extends BaseFragment implements View.OnClickListener {

    private boolean mIsFromSocialNetwork = false;

    private CircleImageView mImageProfilePicture;
    private Button mButtonAddPicture;
    private EditText mEditName;
    private EditText mEditLastName;
    private EditText mEditLastName2;
    private EditText mEditEmail;
    private EditText mEditPhone;
    private TextView mTextPasswordLabel;
    private EditText mEditPassword;
    private CheckBox mCheckAccept;
    private Button mButtonStartSession;
    private ImageView mImageViewSocialNetworkIcon;
    private User mUserInfo;
    private LinearLayout mProgressLayout;

    private static final int PICK_IMAGE_ID = 234;
    private RelativeLayout mRootLayout;

    public RegisterFragment() {
        this.setArguments(new Bundle());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_register, container, false);

        mImageProfilePicture = (CircleImageView) rootView.findViewById(R.id.image_profile_picture);
        mButtonAddPicture = (Button) rootView.findViewById(R.id.button_add_picture);
        mEditName = (EditText) rootView.findViewById(R.id.edittext_name);
        mEditLastName = (EditText) rootView.findViewById(R.id.edittext_lastname);
        mEditLastName2 = (EditText) rootView.findViewById(R.id.edittext_lastname_2);
        mEditEmail = (EditText) rootView.findViewById(R.id.edittext_email);
        mEditPhone = (EditText) rootView.findViewById(R.id.edittext_phone_number);
        mEditPassword = (EditText) rootView.findViewById(R.id.edittext_password);
        mCheckAccept = (CheckBox) rootView.findViewById(R.id.check_accept);
        mButtonStartSession = (Button) rootView.findViewById(R.id.button_start_session);
        mTextPasswordLabel = (TextView) rootView.findViewById(R.id.textview_password);
        mImageViewSocialNetworkIcon = (ImageView) rootView.findViewById(R.id.imageview_social_network);

        mProgressLayout = (LinearLayout) rootView.findViewById(R.id.progress_layout);
        mRootLayout = (RelativeLayout) rootView.findViewById(R.id.root_view);

        mProgressLayout.setVisibility(View.GONE);

        mIsFromSocialNetwork = getArguments().getBoolean(Constants.EXTRA_LOGIN_FROM_SOCIAL_NETWORK, false);

        mButtonStartSession.setOnClickListener(this);
        mButtonAddPicture.setOnClickListener(this);

        final float scale = this.getResources().getDisplayMetrics().density;
        mCheckAccept.setPadding(mCheckAccept.getPaddingLeft() + (int) (10.0f * scale + 0.5f),
                mCheckAccept.getPaddingTop(),
                mCheckAccept.getPaddingRight(),
                mCheckAccept.getPaddingBottom());

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
    public void onAttach(Context context) {
        super.onAttach(context);
        mCurrentContext = context;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == PICK_IMAGE_ID && resultCode == getActivity().RESULT_OK) {
            Bitmap bitmap = ImagePicker.getImageFromResult(getActivity(), resultCode, data, "", "profile_picture.jpg");
            mImageProfilePicture.setImageBitmap(bitmap);
            mImageProfilePicture.setScaleType(ImageView.ScaleType.CENTER_CROP);
        }
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == mButtonStartSession.getId()) {
            register();
        }

        if (view.getId() == mButtonAddPicture.getId()) {
            checkPermissionsForAction(new String[] { Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE },
                    ACTION_TAKE_OR_CHOOSE_PICTURE,
                    "Los permisos para tomar fotos están desactivados");
            /*Intent chooseImageIntent = ImagePicker.getPickImageIntent(getActivity(), "", "profile_picture.jpg");
            startActivityForResult(chooseImageIntent, PICK_IMAGE_ID);*/
        }
    }

    private void register() {
        if (!Utils.getInstance().isInternetAvailable(getActivity()))
            return;

        if (mEditName.getText().toString().trim().isEmpty()) {
            Toast.makeText(getActivity(), "Ingresa tu nombre", Toast.LENGTH_SHORT).show();
            return;
        }

        if (mEditLastName.getText().toString().trim().isEmpty()) {
            Toast.makeText(getActivity(), "Ingresa tu apellido paterno", Toast.LENGTH_SHORT).show();
            return;
        }

        if (mEditEmail.getText().toString().trim().isEmpty()) {
            Toast.makeText(getActivity(), "Ingresa tu e-mail", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!Utils.isValidEmail(mEditEmail.getText().toString())) {
            Toast.makeText(getActivity(), "Ingresa tu e-mail", Toast.LENGTH_SHORT).show();
            return;
        }

        if (mEditPhone.getText().toString().trim().isEmpty()) {
            Toast.makeText(getActivity(), "Ingresa tu teléfono", Toast.LENGTH_SHORT).show();
            return;
        }

        if (mEditPhone.getText().toString().trim().length() < 8) {
            Toast.makeText(getActivity(), "El teléfono debe tener entre 8 y 10 digitos", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!mIsFromSocialNetwork) {
            if (mEditPassword.getText().toString().trim().isEmpty()) {
                Toast.makeText(getActivity(), "Ingresa tu contraseña", Toast.LENGTH_SHORT).show();
                return;
            }
        }

        mUserInfo.setName(mEditName.getText().toString().trim());
        mUserInfo.setFirtsLastName(mEditLastName.getText().toString().trim());
        mUserInfo.setSecondLastName(mEditLastName2.getText().toString().trim());
        mUserInfo.setEmail(mEditEmail.getText().toString().trim());
        mUserInfo.setPhone(mEditPhone.getText().toString().trim());
        if (!mIsFromSocialNetwork) {
            mUserInfo.setPassword(mEditPassword.getText().toString());
        }

        Bitmap bitmapProfilePicture = null;

        if (mIsFromSocialNetwork) {
            if (mUserInfo.getRegisterType().equals(Constants.REGISTER_FACEBOOK)) {
                mUserInfo.setTwToken("");
                mUserInfo.setTwUsername("");
                mUserInfo.setFbUsername("");
                mUserInfo.setHashToken(Utils.getInstance().getHashForToken(mUserInfo.getFbToken()));
            } else if (mUserInfo.getRegisterType().equals(Constants.REGISTER_TWITTER)) {
                mUserInfo.setFbToken("");
                mUserInfo.setFbUsername("");
                mUserInfo.setTwUsername("");
                mUserInfo.setHashToken(Utils.getInstance().getHashForToken(mUserInfo.getTwToken()));
            }

            mUserInfo.setPassword("");
            bitmapProfilePicture = ((BitmapDrawable)mImageProfilePicture.getDrawable()).getBitmap();

        } else {
            bitmapProfilePicture = ((BitmapDrawable)mImageProfilePicture.getDrawable()).getBitmap();
            PreferencesManager.getInstance().setUserRegisteredWithEmail(getActivity(), true);

            mUserInfo.setFbToken("");
            mUserInfo.setFbUsername("");
            mUserInfo.setTwToken("");
            mUserInfo.setTwUsername("");
            mUserInfo.setHashToken("");
        }

        mUserInfo.setPicture(bitmapProfilePicture != null ? Utils.bitmapToBase64(bitmapProfilePicture, Constants.PICTURE_CITIZEN_QUALITY) : "");

        Intent legaslActivity = new Intent(getActivity(), LegalsActivity.class);
        legaslActivity.putExtra(Constants.EXTRA_REGISTER_USER_DATA, mUserInfo);
        legaslActivity.putExtra(Constants.EXTRA_LOGIN_FROM_SOCIAL_NETWORK, mIsFromSocialNetwork);
        startActivity(legaslActivity);
        getActivity().overridePendingTransition(R.anim.enter_from_right, R.anim.enter_from_left);
    }

    public void updateViews(boolean isFromSocialNetwork, User userInfo) {
        mIsFromSocialNetwork = isFromSocialNetwork;
        mUserInfo = userInfo;

        if (isFromSocialNetwork) {
            mButtonAddPicture.setVisibility(View.GONE);
            mImageViewSocialNetworkIcon.setVisibility(View.VISIBLE);
            mButtonAddPicture.setEnabled(false);
            mTextPasswordLabel.setVisibility(View.GONE);
            mEditPassword.setVisibility(View.GONE);

            mEditName.setText(userInfo.getName());
            mEditLastName.setText(userInfo.getFirtsLastName() != null ? userInfo.getFirtsLastName() : "");
            mEditEmail.setText(userInfo.getEmail());

            if (userInfo.getName() == null) {
                mEditName.requestFocus();
            } else if (userInfo.getEmail() == null) {
                mEditLastName.requestFocus();
            } else
                mEditEmail.requestFocus();


            String pictureUrl = "";
            if (userInfo.getRegisterType().equals(Constants.REGISTER_FACEBOOK)) {
                mImageViewSocialNetworkIcon.setImageResource(R.drawable.social_fb);
                pictureUrl = String.format("https://graph.facebook.com/%s/picture?type=large", userInfo.getId());
            } else if (userInfo.getRegisterType().equals(Constants.REGISTER_TWITTER)) {
                mImageViewSocialNetworkIcon.setImageResource(R.drawable.social_tw);
                pictureUrl = userInfo.getPicture();
            }

            mUserInfo.setPicture(pictureUrl);

            if (!pictureUrl.isEmpty()) {
                Picasso.with(getActivity())
                        .load(pictureUrl)
                        .placeholder(R.drawable.user_registro)
                        .error(R.drawable.user_registro).into(mImageProfilePicture);
            }
        }
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
}

package com.cmi.bache24.ui.fragment;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.widget.Toast;

import com.cmi.bache24.util.Constants;

/**
 * Created by omar on 7/10/16.
 */
public abstract class BaseFragment extends Fragment {

    public Context mCurrentContext;
    private String mAction;
    private String mMessageForDeniedPermissions;

    public static final String ACTION_TAKE_OR_CHOOSE_PICTURE = "take_or_choose_picture";
    public static final String ACTION_PHONE_CALL = "phone_call";
    public static final String ACTION_REQUEST_LOCATION = "user_location";

    public abstract void doAllowedStuffWithPermissionGrantedForAction(String action);
    public abstract void onPermissionDenied(String message);
    public abstract void onPermissionDeniedPermanently(String message);
    private String[] mRequestedPermissions;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        mCurrentContext = context;
    }

    public void checkPermissionsForAction(String[] permissions, String action, String messageForDeniedPermissions) {

        mRequestedPermissions = permissions;
        mAction = action;
        mMessageForDeniedPermissions = messageForDeniedPermissions;

        if (hasAllPermissions(permissions)) {
            doAllowedStuffWithPermissionGrantedForAction(mAction);
        } else {
            ActivityCompat.requestPermissions((Activity) mCurrentContext,
                    permissions,
                    Constants.REQUEST_CAMERA_PERMISSION_CODE);
        }
    }

    private boolean hasAllPermissions(String[] permissions) {
        for (int i = 0; i < permissions.length; i++) {
            if (!hasPermissionGrated(permissions[i])) {
                return false;
            }
        }

        return true;
    }

    private boolean hasPermissionGrated(String permissioName) {
        int permissionCheck = ContextCompat.checkSelfPermission(mCurrentContext, permissioName);

        return permissionCheck == PackageManager.PERMISSION_GRANTED;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (grantResults.length > 0) {
            if (requestCode == Constants.REQUEST_CAMERA_PERMISSION_CODE) {

                int permissionDeniedPosition = allPermissionRequestedGranted(grantResults);

                if (permissionDeniedPosition == -1)
                {
                    doAllowedStuffWithPermissionGrantedForAction(mAction);
                }
                else {
                    if (ActivityCompat.shouldShowRequestPermissionRationale((Activity) mCurrentContext,
                            mRequestedPermissions[permissionDeniedPosition])) {
                        onPermissionDenied(mMessageForDeniedPermissions);
                    } else {
                        onPermissionDeniedPermanently(mMessageForDeniedPermissions);
                    }
                }
            }
        }
    }

    private int allPermissionRequestedGranted(int[] grantResults) {
        for (int i = 0; i < grantResults.length; i++) {
            if (grantResults[i] != PackageManager.PERMISSION_GRANTED) {
                return i;
            }
        }

        return -1;
    }
}

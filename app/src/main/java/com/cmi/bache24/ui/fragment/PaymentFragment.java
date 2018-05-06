package com.cmi.bache24.ui.fragment;

import android.Manifest;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.cmi.bache24.R;

public class PaymentFragment extends BaseFragment implements View.OnClickListener {

    private Button callButton1;
    //private Button callButton2;
    private String callNumberSelected;

    public PaymentFragment() { }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_payment, container, false);

        callButton1 = (Button) rootView.findViewById(R.id.button_phone_number);
        //callButton2 = (Button) rootView.findViewById(R.id.button_phone_number_2);

        callButton1.setOnClickListener(this);
        //callButton2.setOnClickListener(this);

        return rootView;
    }

    private void checkCallPermissions() {
        checkPermissionsForAction(new String[] { Manifest.permission.CALL_PHONE },
                ACTION_PHONE_CALL,
                "Los permisos para hacer llamadas están desactivados");
    }

    @Override
    public void doAllowedStuffWithPermissionGrantedForAction(String action) {
        if (action == ACTION_PHONE_CALL && isAdded()) {
            Intent callIntent = new Intent(Intent.ACTION_CALL);
            callIntent.setData(Uri.parse("tel:" + callNumberSelected));
            startActivity(callIntent);
        }
    }

    @Override
    public void onPermissionDenied(String message) {
        Toast.makeText(mCurrentContext, message, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onPermissionDeniedPermanently(String message) {
        Toast.makeText(mCurrentContext, message, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == callButton1.getId()) {
            callNumberSelected = ((Button) v).getText().toString();
            checkCallPermissions();
        }
    }
}

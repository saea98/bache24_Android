package com.cmi.bache24.ui.dialog;

import android.app.DialogFragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;

import com.cmi.bache24.R;
import com.cmi.bache24.ui.dialog.interfaces.MessageDialogListener;

/**
 * Created by omar on 2/16/16.
 */
public class UserActivationDialog extends DialogFragment implements View.OnClickListener {

    private MessageDialogListener mMessageDialogListener;
    private Button mButtonAccept;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.layout_user_activation, container, false);
        getDialog().requestWindowFeature(Window.FEATURE_NO_TITLE);

        mButtonAccept = (Button) rootView.findViewById(R.id.button_ok);
        mButtonAccept.setOnClickListener(this);

        return rootView;
    }

    public void setDialogListener(MessageDialogListener messageDialogListener) {
        this.mMessageDialogListener = messageDialogListener;
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == mButtonAccept.getId()) {
            if (mMessageDialogListener != null)
                mMessageDialogListener.onAccept();
        }
    }
}

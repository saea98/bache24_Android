package com.cmi.bache24.ui.dialog;

import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;

import com.cmi.bache24.R;
import com.cmi.bache24.ui.dialog.interfaces.MessageDialogListener;

/**
 * Created by omar on 6/28/16.
 */
public class NewVersionDialog extends DialogFragment implements View.OnClickListener {

    private MessageDialogListener mMessageDialogListener;
    private Button mButtonAccept;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.new_version_dialog, container, false);
        getDialog().requestWindowFeature(Window.FEATURE_NO_TITLE);

        mButtonAccept = (Button) rootView.findViewById(R.id.button_accept);

        mButtonAccept.setOnClickListener(this);

        return rootView;
    }

    public void setupDialog(MessageDialogListener listener) {
        mMessageDialogListener = listener;
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == mButtonAccept.getId()) {
            if (mMessageDialogListener != null)
                mMessageDialogListener.onAccept();
        }
    }
}

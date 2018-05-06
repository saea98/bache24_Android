package com.cmi.bache24.ui.dialog;

import android.app.DialogFragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

import com.cmi.bache24.R;
import com.cmi.bache24.ui.dialog.interfaces.MessageDialogListener;

/**
 * Created by omar on 3/10/16.
 */
public class MultipleDevicesDialog extends DialogFragment implements View.OnClickListener {

    private MessageDialogListener mMessageDialogListener;
    private TextView mTextTitle;
    private TextView mTextMessage;
    private Button mButtonAccept;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.dialog_multiple_devices, container, false);
        getDialog().requestWindowFeature(Window.FEATURE_NO_TITLE);

        mButtonAccept = (Button) rootView.findViewById(R.id.button_ok);
        mTextTitle = (TextView) rootView.findViewById(R.id.text_title);
        mTextMessage = (TextView) rootView.findViewById(R.id.text_message);

        mButtonAccept.setOnClickListener(this);

        return rootView;
    }

    public void setupDialog(MessageDialogListener messageDialogListener) {
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

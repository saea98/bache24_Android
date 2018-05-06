package com.cmi.bache24.ui.dialog;

import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.cmi.bache24.R;
import com.cmi.bache24.ui.dialog.interfaces.MessageDialogListener;

/**
 * Created by omar on 2/8/16.
 */
public class SimpleIconCenteredAlertDialog extends DialogFragment implements View.OnClickListener {

    private TextView mTitleTextView;
    private ImageView mIconImageView;
    private TextView mMessageTextView;
    private Button mButtonAccept;

    private String mTitle;
    private String mMessage;
    private int mIcon;
    private String mButtonText;
    private MessageDialogListener mMessageDialogListener;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.driver_alert_layout, container, false);
        getDialog().requestWindowFeature(Window.FEATURE_NO_TITLE);

        mTitleTextView = (TextView) rootView.findViewById(R.id.textview_title);
        mIconImageView = (ImageView) rootView.findViewById(R.id.imageview_icon);
        mMessageTextView = (TextView) rootView.findViewById(R.id.textview_message);
        mButtonAccept = (Button) rootView.findViewById(R.id.button_accept);

        mTitleTextView.setText(mTitle);
        mIconImageView.setImageResource(mIcon);
        mMessageTextView.setText(mMessage);
        mButtonAccept.setText(mButtonText);
        mButtonAccept.setOnClickListener(this);

        return rootView;
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == mButtonAccept.getId()) {
            if (mMessageDialogListener != null)
                mMessageDialogListener.onAccept();
        }
    }

    public void setupDialog(String title, String message, int icon, String buttonText, MessageDialogListener listener) {
        this.mTitle = title;
        this.mMessage = message;
        this.mIcon = icon;
        this.mButtonText = buttonText;
        this.mMessageDialogListener = listener;
    }
}

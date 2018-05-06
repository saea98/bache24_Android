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
import com.cmi.bache24.ui.dialog.interfaces.TwoButtonsDialogListener;

/**
 * Created by omar on 5/18/16.
 */
public class TwoButtonsCenteredIconAlertDialog extends DialogFragment implements View.OnClickListener {

    private TextView mTitleTextView;
    private ImageView mIconImageView;
    private TextView mMessageTextView;
    private Button mButtonLeft;
    private Button mButtonRight;

    private String mTitle;
    private String mMessage;
    private int mIcon;
    private String mLeftButtonText;
    private String mRightButtonText;
    private TwoButtonsDialogListener mMessageDialogListener;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.dialog_two_buttons_centered_icon_alert, container, false);
        getDialog().requestWindowFeature(Window.FEATURE_NO_TITLE);

        mTitleTextView = (TextView) rootView.findViewById(R.id.textview_title);
        mIconImageView = (ImageView) rootView.findViewById(R.id.imageview_icon);
        mMessageTextView = (TextView) rootView.findViewById(R.id.textview_message);
        mButtonLeft = (Button) rootView.findViewById(R.id.button_left);
        mButtonRight= (Button) rootView.findViewById(R.id.button_right);

        mTitleTextView.setText(mTitle);
        mIconImageView.setImageResource(mIcon);
        mMessageTextView.setText(mMessage);
        mButtonLeft.setText(mLeftButtonText);
        mButtonRight.setText(mRightButtonText);
        mButtonLeft.setOnClickListener(this);
        mButtonRight.setOnClickListener(this);

        return rootView;
    }

    @Override
    public void onClick(View view) {
        if (mMessageDialogListener == null)
            return;

        if (view.getId() == mButtonLeft.getId()) {
            mMessageDialogListener.onLeftButtonClick();
        } else if (view.getId() == mButtonRight.getId()) {
            mMessageDialogListener.onRightButtonClick();
        }
    }

    public void setupDialog(String title,
                            String message,
                            int icon,
                            String leftButtonText,
                            String rightButtonText,
                            TwoButtonsDialogListener listener) {
        this.mTitle = title;
        this.mMessage = message;
        this.mIcon = icon;
        this.mLeftButtonText = leftButtonText;
        this.mRightButtonText= rightButtonText;
        this.mMessageDialogListener = listener;
    }
}

package com.cmi.bache24.ui.dialog;

import android.os.Bundle;
import android.support.annotation.Nullable;
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
 * Created by omar on 3/8/16.
 */
public class SimpleIconMessageDialog extends DialogFragment implements View.OnClickListener {

    private MessageDialogListener mMessageDialogListener;
    private ImageView mImageIcon;
    private TextView mTextTitle;
    private TextView mTextMessage;
    private Button mButtonAccept;

    private int mIconValue;
    private String mTitleValue;
    private String mMessageValue;
    private boolean mShowIcon;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.dialog_simple_icon_message_dialog, container, false);
        getDialog().requestWindowFeature(Window.FEATURE_NO_TITLE);

        mImageIcon = (ImageView) rootView.findViewById(R.id.image_icon);
        mTextTitle = (TextView) rootView.findViewById(R.id.text_title);
        mTextMessage = (TextView) rootView.findViewById(R.id.text_message);
        mButtonAccept = (Button) rootView.findViewById(R.id.button_accept);

        mImageIcon.setImageResource(mIconValue);
        mTextTitle.setText(mTitleValue);
        mTextMessage.setText(mMessageValue);

        if (!mShowIcon) {
            mImageIcon.setVisibility(View.GONE);
        } else {
            mImageIcon.setVisibility(View.VISIBLE);
        }

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

    public void setupDialog(int iconId, String title, String message, boolean showIcon, MessageDialogListener listener) {
        mIconValue = iconId;
        mTitleValue = title;
        mMessageValue = message;
        mShowIcon = showIcon;
        mMessageDialogListener = listener;
    }
}

package com.cmi.bache24.ui.dialog;

import android.app.DialogFragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;

import com.cmi.bache24.R;
import com.cmi.bache24.ui.dialog.interfaces.CommentsDialogListener;

/**
 * Created by omar on 12/13/15.
 */
public class CommentsDialog extends DialogFragment implements View.OnClickListener {

    private CommentsDialogListener mCommentsDialogListener;
    private Button mButtonAccept;
    private Button mButtonCancel;
    private EditText mEditComments;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.layout_comments_dialog, container, false);
        getDialog().requestWindowFeature(Window.FEATURE_NO_TITLE);

        mButtonAccept = (Button) rootView.findViewById(R.id.button_send);
        mButtonCancel = (Button) rootView.findViewById(R.id.button_cancel);

        mEditComments = (EditText) rootView.findViewById(R.id.edittext_comments);

        mButtonAccept.setOnClickListener(this);
        mButtonCancel.setOnClickListener(this);

        return rootView;
    }

    public void setCommentsDialogListener(CommentsDialogListener commentsDialogListener) {
        this.mCommentsDialogListener = commentsDialogListener;
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == mButtonAccept.getId()) {
            if (mCommentsDialogListener != null)
                mCommentsDialogListener.onSend(mEditComments.getText().toString());
        } else if (view.getId() == mButtonCancel.getId()) {
            if (mCommentsDialogListener != null)
                mCommentsDialogListener.onCancel();
        }
    }
}

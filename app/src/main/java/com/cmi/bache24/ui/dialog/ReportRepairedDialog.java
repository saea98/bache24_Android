package com.cmi.bache24.ui.dialog;

//import android.app.DialogFragment;
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
import com.cmi.bache24.ui.dialog.interfaces.CommentsDialogListener;

/**
 * Created by omar on 1/11/16.
 */
public class ReportRepairedDialog extends DialogFragment implements View.OnClickListener {

    private CommentsDialogListener mCommentsDialogListener;
    private Button mButtonAccept;
    private Button mButtonCancel;
    private TextView mTextTitle;
    private TextView mTextMessage;
    private ImageView mImageIcon;

    public enum ReportType {
        SOLVED, MARK_AS_WRONG, FAKE, SECONDARY_ROAD, PENDING, REALLOCATE, RELEASE
    }

    private ReportType mReportType;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.report_repaired_dialog, container, false);
        getDialog().requestWindowFeature(Window.FEATURE_NO_TITLE);

        mButtonAccept = (Button) rootView.findViewById(R.id.button_send);
        mButtonCancel = (Button) rootView.findViewById(R.id.button_cancel);

        mTextTitle = (TextView) rootView.findViewById(R.id.textview_title);
        mTextMessage = (TextView) rootView.findViewById(R.id.textview_message);
        mImageIcon = (ImageView) rootView.findViewById(R.id.imageview_icon);

        switch (mReportType) {
            case SOLVED:
                mTextTitle.setText(getResources().getString(R.string.troop_report_alert_title));
                mTextMessage.setText(getResources().getString(R.string.troop_report_message1));
                mImageIcon.setImageResource(R.drawable.happy);
                break;
            case MARK_AS_WRONG:
                mTextTitle.setText(getResources().getString(R.string.troop_report_alert_title));
                mTextMessage.setText(getResources().getString(R.string.troop_report_message2));
                mImageIcon.setImageResource(R.drawable.carita_feliz);
                break;
            case FAKE:
                mTextTitle.setText(getResources().getString(R.string.troop_report_alert_title));
                mTextMessage.setText(getResources().getString(R.string.troop_report_message4));
                mImageIcon.setImageResource(R.drawable.carita_ups);
                break;
            case SECONDARY_ROAD:
                mTextTitle.setText(getResources().getString(R.string.troop_report_alert_title));
                mTextMessage.setText(getResources().getString(R.string.troop_report_message5));
                mImageIcon.setImageResource(R.drawable.carita_ups);
                break;
            case PENDING:
                mTextTitle.setText(getResources().getString(R.string.troop_report_alert_title));
                mTextMessage.setText(getResources().getString(R.string.troop_report_message_5));
                mImageIcon.setImageResource(R.drawable.happy);
                break;
            case REALLOCATE:
                mTextTitle.setText(getResources().getString(R.string.troop_report_alert_title));
                mTextMessage.setText(getResources().getString(R.string.troop_report_message_6));
                mImageIcon.setImageResource(R.drawable.happy);
                break;
            case RELEASE:
                mTextTitle.setText(getResources().getString(R.string.troop_report_alert_title));
                mTextMessage.setText(getResources().getString(R.string.troop_report_release));
                mImageIcon.setImageResource(R.drawable.carita_ups);
                break;
            default:
                mTextTitle.setText(getResources().getString(R.string.troop_report_alert_title));
                mTextMessage.setText(getResources().getString(R.string.troop_report_message1));
                mImageIcon.setImageResource(R.drawable.happy);
                break;
        }

        mButtonAccept.setOnClickListener(this);
        mButtonCancel.setOnClickListener(this);

        return rootView;
    }

    public void setType(ReportType reportType) {
        mReportType = reportType;
    }

    public void setCommentsDialogListener(CommentsDialogListener commentsDialogListener) {
        this.mCommentsDialogListener = commentsDialogListener;
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == mButtonAccept.getId()) {
            if (mCommentsDialogListener != null)
                mCommentsDialogListener.onSend("");
        } else if (view.getId() == mButtonCancel.getId()) {
            if (mCommentsDialogListener != null)
                mCommentsDialogListener.onCancel();
        }
    }
}

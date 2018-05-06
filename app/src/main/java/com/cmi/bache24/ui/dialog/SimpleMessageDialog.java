package com.cmi.bache24.ui.dialog;

import android.app.DialogFragment;
import android.os.Bundle;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

import com.cmi.bache24.R;
import com.cmi.bache24.data.model.Report;
import com.cmi.bache24.ui.dialog.interfaces.MessageDialogListener;

/**
 * Created by omar on 11/28/15.
 */
public class SimpleMessageDialog extends DialogFragment implements View.OnClickListener {

    private MessageDialogListener mMessageDialogListener;
    private TextView mTextTitle;
    private TextView mTextMessage;
    private Button mButtonAccept;
    private Report mReport;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.dialog_simple_message, container, false);
        getDialog().requestWindowFeature(Window.FEATURE_NO_TITLE);

        mButtonAccept = (Button) rootView.findViewById(R.id.button_ok);
        mTextTitle = (TextView) rootView.findViewById(R.id.text_title);
        mTextMessage = (TextView) rootView.findViewById(R.id.text_message);

        StringBuilder stringBuilder = new StringBuilder();

        stringBuilder.append("<font color='#484848'>" + getResources().getString(R.string.report_sent) + "</font>");
        stringBuilder.append(" ");
        stringBuilder.append("<font color='#ec2697'><b>" + mReport.getTicket() + "</b></font>");
        stringBuilder.append("<font color='#484848'>.</font>");

        mTextMessage.setText(Html.fromHtml(stringBuilder.toString()));

        mButtonAccept.setOnClickListener(this);

        return rootView;
    }

    public void setupDialog(Report report, MessageDialogListener messageDialogListener) {
        this.mReport = report;
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

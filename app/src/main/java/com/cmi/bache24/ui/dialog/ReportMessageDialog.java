package com.cmi.bache24.ui.dialog;

import android.os.Bundle;
import android.support.v4.app.DialogFragment;
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
import com.cmi.bache24.util.Constants;

/**
 * Created by omar on 11/28/15.
 */
public class ReportMessageDialog extends DialogFragment implements View.OnClickListener {

    private MessageDialogListener mMessageDialogListener;
    private Report mCurrentReport;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.report_dialog_layout, container, false);
        getDialog().requestWindowFeature(Window.FEATURE_NO_TITLE);

        Button buttonAccept = (Button) rootView.findViewById(R.id.button_accept_dialog);
        TextView textViewReportId = (TextView) rootView.findViewById(R.id.textView13);

        if (mCurrentReport.getRoadType() == Constants.ROAD_TYPE_PRIMARY) {
            StringBuilder stringBuilder = new StringBuilder();

            stringBuilder.append("<font color='#484848'>" + getResources().getString(R.string.report_dialog_message4) + "</font>");
            stringBuilder.append(" <font color='#ec2697'><b>" + mCurrentReport.getTicket() + "</b></font>. ");
            stringBuilder.append("<font color='#484848'>" + getResources().getString(R.string.report_dialog_message1) + "</font>");

            textViewReportId.setText(Html.fromHtml(stringBuilder.toString()));
        } else {
            if (mCurrentReport.getReportVersion() == Constants.OLD_VERSION) {
                StringBuilder stringBuilder = new StringBuilder();

                stringBuilder.append("<font color='#484848'>" + getResources().getString(R.string.report_dialog_message4) + "</font>");
                stringBuilder.append(" <font color='#ec2697'><b>" + mCurrentReport.getTicket() + "</b></font>. ");
                stringBuilder.append("<font color='#484848'>" + getResources().getString(R.string.report_dialog_message1) + "</font>");

                textViewReportId.setText(Html.fromHtml(stringBuilder.toString()));
            } else {
                textViewReportId.setText(getResources().getString(R.string.report_dialog_text_temp));
            }
        }

        buttonAccept.setOnClickListener(this);

        return rootView;
    }

    public void setMessageDialogListener(Report report, MessageDialogListener mMessageDialogListener) {
        this.mCurrentReport = report;
        this.mMessageDialogListener = mMessageDialogListener;
    }

    @Override
    public void onClick(View view) {
        if (mMessageDialogListener != null)
            mMessageDialogListener.onAccept();
    }
}
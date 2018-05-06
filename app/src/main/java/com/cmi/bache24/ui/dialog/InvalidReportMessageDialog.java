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
import com.cmi.bache24.ui.dialog.interfaces.InvalidReportDialogListener;
import com.cmi.bache24.util.Constants;

/**
 * Created by omar on 11/28/15.
 */
public class InvalidReportMessageDialog extends DialogFragment implements View.OnClickListener {

    private InvalidReportDialogListener mMessageDialogListener;
    private Button buttonAccept;
    private Button mCallButton;
    private TextView mMessageText;
    private Report mCurrentReport;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.no_valid_report_dialog, container, false);
        getDialog().requestWindowFeature(Window.FEATURE_NO_TITLE);

        buttonAccept = (Button) rootView.findViewById(R.id.button_accept_dialog);
        mMessageText = (TextView) rootView.findViewById(R.id.textview_report_link);
        mCallButton = (Button) rootView.findViewById(R.id.button_call);

        if (mCurrentReport.getRoadType() == Constants.ROAD_TYPE_SECONDARY) {
            StringBuilder stringBuilder = new StringBuilder();

            stringBuilder.append("<font color='#484848'>" + getResources().getString(R.string.report_dialog_invalid_moreinfo_part_1) + "</font>");

            stringBuilder.append(" ");
            stringBuilder.append("<font color='#ec2697'><b>" + Constants.KEY_DELEGATIONS.get(mCurrentReport.getDelegationId()) + "</b></font>");

            stringBuilder.append(" ");
            stringBuilder.append("<font color='#484848'>" + getResources().getString(R.string.report_dialog_invalid_moreinfo_part_2) + "</font>");

            stringBuilder.append(" ");
            stringBuilder.append("<font color='#ec2697'><b>" + mCurrentReport.getTicket() + "</b></font>");

            stringBuilder.append(". ");
            stringBuilder.append("<font color='#484848'>" + getResources().getString(R.string.report_dialog_message1) + "</font>");

            mMessageText.setText(Html.fromHtml(stringBuilder.toString()));
        } else {
            if (mCurrentReport.getReportVersion() == Constants.OLD_VERSION) {
                StringBuilder stringBuilder = new StringBuilder();

                stringBuilder.append("<font color='#484848'>" + getResources().getString(R.string.report_dialog_invalid_moreinfo_part_1) + "</font>");

                stringBuilder.append(" ");
                stringBuilder.append("<font color='#ec2697'><b>" + Constants.KEY_DELEGATIONS.get(mCurrentReport.getDelegationId()) + "</b></font>");

                stringBuilder.append(" ");
                stringBuilder.append("<font color='#484848'>" + getResources().getString(R.string.report_dialog_invalid_moreinfo_part_2) + "</font>");

                stringBuilder.append(" ");
                stringBuilder.append("<font color='#ec2697'><b>" + mCurrentReport.getTicket() + "</b></font>");

                stringBuilder.append(". ");
                stringBuilder.append("<font color='#484848'>" + getResources().getString(R.string.report_dialog_message1) + "</font>");

                mMessageText.setText(Html.fromHtml(stringBuilder.toString()));
            } else {
                mMessageText.setText(getResources().getString(R.string.report_dialog_text_temp));
                mCallButton.setVisibility(View.GONE);
            }
        }

        buttonAccept.setOnClickListener(this);
        mCallButton.setOnClickListener(this);

        return rootView;
    }

    public void setMessageDialogListener(Report report, InvalidReportDialogListener mMessageDialogListener) {
        this.mCurrentReport = report;
        this.mMessageDialogListener = mMessageDialogListener;
    }

    @Override
    public void onClick(View view) {
        if (buttonAccept.getId() == view.getId()) {
            if (mMessageDialogListener != null)
                mMessageDialogListener.onAccept();
        } else if (mCallButton.getId() == view.getId()) {
            if (mMessageDialogListener != null)
                mMessageDialogListener.onClickLink();
        }
    }
}

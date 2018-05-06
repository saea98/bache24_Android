package com.cmi.bache24.ui.fragment.troop;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.cmi.bache24.R;
import com.cmi.bache24.data.model.AttendedReport;
import com.cmi.bache24.data.model.Report;
import com.cmi.bache24.data.model.User;
import com.cmi.bache24.data.remote.ServicesManager;
import com.cmi.bache24.data.remote.interfaces.NewReportCallback;
import com.cmi.bache24.ui.dialog.ReportRepairedDialog;
import com.cmi.bache24.ui.dialog.SimpleIconMessageDialog;
import com.cmi.bache24.ui.dialog.interfaces.CommentsDialogListener;
import com.cmi.bache24.ui.dialog.interfaces.MessageDialogListener;
import com.cmi.bache24.util.Constants;
import com.cmi.bache24.util.ImagePicker;
import com.cmi.bache24.util.PreferencesManager;
import com.cmi.bache24.util.Utils;

import java.text.SimpleDateFormat;
import java.util.Calendar;

/**
 * A placeholder fragment containing a simple view.
 */
public class PendingReportFragment extends Fragment implements View.OnClickListener, DatePickerDialog.OnDateSetListener {

    private User mCurrentUser;
    private Report mCurrentReport;

    private EditText mEditComments;
    private EditText mEditDate;
    private Button mButtonSend;
    private LinearLayout mRootLayout;
    private LinearLayout mProgressLayout;
    private DatePickerDialog mDatePicker;
    private SimpleDateFormat mDateFormatter;
    private Spinner mSpinnerComment;
    private String mCommentSelected;
    private TextView mCommentsInputLabel;
    private ImageView mPictureImageView;
    private TextView requirePictureTextView;

    private Context mCurrentContext;
    private PendingReportFragmentListener mListener;
    private static final int PICK_IMAGE_ID = 234;
    private boolean picture1Added = false;

    public PendingReportFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_pending_report, container, false);

        mEditComments = (EditText) rootView.findViewById(R.id.edit_comments);
        mEditDate = (EditText) rootView.findViewById(R.id.edit_date);
        mButtonSend = (Button) rootView.findViewById(R.id.button_send);
        mRootLayout = (LinearLayout) rootView.findViewById(R.id.root_view);
        mSpinnerComment = (Spinner) rootView.findViewById(R.id.spinner_to);
        mCommentsInputLabel = (TextView) rootView.findViewById(R.id.textView72);
        requirePictureTextView = (TextView) rootView.findViewById(R.id.textView71);
        mPictureImageView = (ImageView) rootView.findViewById(R.id.imageView17);

        mProgressLayout = (LinearLayout) rootView.findViewById(R.id.progress_layout);
        mProgressLayout.setVisibility(View.GONE);

        mPictureImageView.setOnClickListener(this);
        mEditDate.setOnClickListener(this);
        mButtonSend.setOnClickListener(this);

        requirePictureTextView.setVisibility(View.GONE);
        mPictureImageView.setVisibility(View.GONE);

        mCurrentUser = PreferencesManager.getInstance().getUserInfo(getActivity());

        mRootLayout.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                Utils.hideKeyboard(getActivity(), view);
                return false;
            }
        });

        setupDatePicker();
        setupSpinnerList();
        hideInputComment();

        return rootView;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        mCurrentContext = context;

        try{
            if (context instanceof Activity) {
                mListener = (PendingReportFragmentListener) context;
            }
        } catch (ClassCastException ex) {

        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == mEditDate.getId()) {
            mDatePicker.show();
        } else if (view.getId() == mButtonSend.getId()) {
            lockButtons();
            processReport();
        } else if (view.getId() == mPictureImageView.getId()) {
            showImagePicker();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        Bitmap bitmap;

        if (requestCode == PICK_IMAGE_ID && resultCode == getActivity().RESULT_OK) {
            bitmap = ImagePicker.getImageFromResult(getActivity(), resultCode, data, mCurrentReport.getTicket(), "pending_picture_1.jpg");
            mPictureImageView.setImageBitmap(bitmap);
            mPictureImageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
            picture1Added = true;
        }
    }

    public void setReport(Report report) {
        if (report == null)
            return;

        this.mCurrentReport = report;

        if (mCurrentReport.getOrigin() == Report.ReportOrigin.O72) {
            requirePictureTextView.setVisibility(View.VISIBLE);
            mPictureImageView.setVisibility(View.VISIBLE);
        } else {
            requirePictureTextView.setVisibility(View.GONE);
            mPictureImageView.setVisibility(View.GONE);
        }
    }

    private void setupDatePicker() {

        mDateFormatter = new SimpleDateFormat("dd-MM-yyyy");

        Calendar calendar = Calendar.getInstance();
        mDatePicker = new DatePickerDialog(mCurrentContext,
                                        this,
                                        calendar.get(Calendar.YEAR),
                                        calendar.get(Calendar.MONTH),
                                        calendar.get(Calendar.DAY_OF_MONTH));
    }

    private void processReport() {

        String textJustification = mCommentSelected;

        if (mCommentSelected.equals("Otro")) {
            if (mEditComments.getText().toString().trim().isEmpty()) {
                Toast.makeText(getActivity(), "Los comentarios son requeridos", Toast.LENGTH_SHORT).show();
                unlockButtons();
                return;
            }

            textJustification = mEditComments.getText().toString().trim();

        } else if (mCommentSelected.equals("Seleccionar")) {
            Toast.makeText(getActivity(), "Seleccione una justificación", Toast.LENGTH_SHORT).show();
            unlockButtons();
            return;
        }

        if (mEditDate.getText().toString().trim().isEmpty()) {
            Toast.makeText(getActivity(), "La fecha de compromiso es requerida", Toast.LENGTH_SHORT).show();
            unlockButtons();
            return;
        }

        String imageBase64String = "";

        if (mCurrentReport.getOrigin() == Report.ReportOrigin.O72) {
            Bitmap bitmapPicture1 = ((BitmapDrawable) mPictureImageView.getDrawable()).getBitmap();

            if ((bitmapPicture1 == null || picture1Added != true)) {
                Toast.makeText(getActivity(), "La foto es requerida", Toast.LENGTH_SHORT).show();
                unlockButtons();
                return;
            }

            imageBase64String = Utils.bitmapToBase64(bitmapPicture1, Constants.PICTURE_SQUAD_QUALITY);
        }

        final AttendedReport attendedReport = new AttendedReport();
        attendedReport.setToken(mCurrentUser.getToken());
        attendedReport.setTicket(mCurrentReport.getTicket());
        attendedReport.setEtapa(Constants.REPORT_STATUS_7);
        attendedReport.setBacheJustificacion(textJustification);
        attendedReport.setBacheCompromiso(mEditDate.getText().toString());

        attendedReport.setImage1(imageBase64String);

        PreferencesManager.getInstance().clearSendReportRetry(mCurrentContext);

        final ReportRepairedDialog reportRepairedDialog = new ReportRepairedDialog();
        reportRepairedDialog.setType(ReportRepairedDialog.ReportType.PENDING);
        reportRepairedDialog.setCommentsDialogListener(new CommentsDialogListener() {

            @Override
            public void onCancel() {
                unlockButtons();
                reportRepairedDialog.dismiss();
            }

            @Override
            public void onSend(String message) {
                reportRepairedDialog.dismiss();

                sendReport(attendedReport);
            }
        });

        reportRepairedDialog.setCancelable(false);

        FragmentTransaction fragmentTransaction = ((AppCompatActivity)mCurrentContext).getSupportFragmentManager().beginTransaction();
        fragmentTransaction.add(reportRepairedDialog, null);

        fragmentTransaction.commitAllowingStateLoss();

//        reportRepairedDialog.show(getActivity().getFragmentManager(), "");
    }

    private void sendReport(final AttendedReport report) {

        mProgressLayout.setVisibility(View.VISIBLE);

        if (mListener != null)
            mListener.onSendButtonClick();

        ServicesManager.updateReportStatusAttention(mCurrentContext, mCurrentUser, report, new NewReportCallback() {

            @Override
            public void onSuccessRegister(String resultCode, Report reportCompleteInfo) {

            }

            @Override
            public void onSuccessRegisterReport(Report reportCompleteInfo) {

                mProgressLayout.setVisibility(View.GONE);

                unlockButtons();

                PreferencesManager.getInstance().setReportAttentionInProgress(getActivity(), false);
                PreferencesManager.getInstance().setReportCanceled("PendingReportFragment - sendAttendedReport-onSuccessRegisterReport", getActivity(), true);

                showAlert(true,
                        getResources().getString(R.string.report_attention_alert_title_0),
                        getAlertMessage(report.getTicket(), false));
            }

            @Override
            public void onFailRegisterReport(String message) {
                mProgressLayout.setVisibility(View.GONE);

                unlockButtons();

                if (message.equals(Constants.AGU_DESCRIPTION)) {

                    PreferencesManager.getInstance().setReportAttentionInProgress(getActivity(), false);
                    PreferencesManager.getInstance().setReportCanceled("WrongReportFragment - onFailRegisterReport", getActivity(), true);

                    showAlert(true,
                            getResources().getString(R.string.report_attention_alert_title_0),
                            getAlertMessage(report.getTicket(), true));

                } else if (message.equals(Constants.REPORTE_ATTENDED)) {

                    PreferencesManager.getInstance().setReportAttentionInProgress(getActivity(), false);
                    PreferencesManager.getInstance().setReportCanceled("WrongReportFragment - onFailRegisterReport", getActivity(), true);

                    showAlertReportAttended();
                } else {

                    if (mListener != null) {
                        mListener.onSendReportError();
                    }

                    showAlert(false,
                            getResources().getString(R.string.error_dialog_title),
                            message);
                }
            }

            @Override
            public void userBanned() {

            }

            @Override
            public void onTokenDisabled() {
                Utils.showLoginForBadToken(getActivity());
            }
        });
    }

    private String getAlertMessage(String reportTicket, boolean hasError) {
        StringBuilder messageSB = new StringBuilder();
        messageSB.append(getResources().getString(R.string.report_attention_alert_message_agu_1));
        messageSB.append(" ");
        messageSB.append(reportTicket);
        messageSB.append(" ");
        messageSB.append(getResources().getString(R.string.report_attention_alert_message_agu_2));
        messageSB.append(" ");
        messageSB.append(getResources().getString(R.string.report_attention_alert_status_5));
        messageSB.append(".");

        return messageSB.toString();
    }

    private void showAlert(final boolean success, String title, String message) {
        final SimpleIconMessageDialog simpleIconMessageDialog = new SimpleIconMessageDialog();
        simpleIconMessageDialog.setupDialog(R.drawable.carita_ups,
                title,
                message, false,
                new MessageDialogListener() {
                    @Override
                    public void onAccept() {
                        simpleIconMessageDialog.dismiss();

                        if (success) {
                            Intent result = new Intent();
                            result.putExtra(Constants.BUNDLE_REGISTER_REPORT_RESULT, true);
                            getActivity().setResult(getActivity().RESULT_OK, result);
                            getActivity().finish();
                        }
                    }
                });
        simpleIconMessageDialog.setCancelable(false);

        FragmentTransaction fragmentTransaction = getActivity().getSupportFragmentManager().beginTransaction();
        fragmentTransaction.add(simpleIconMessageDialog, null);

        fragmentTransaction.commitAllowingStateLoss();
    }

    private void showAlertReportAttended() {
        final SimpleIconMessageDialog simpleIconMessageDialog = new SimpleIconMessageDialog();
        simpleIconMessageDialog.setupDialog(R.drawable.carita_ups,
                getResources().getString(R.string.report_attention_alert_title_0),
                getResources().getString(R.string.troop_report_attended),
                false,
                new MessageDialogListener() {
                    @Override
                    public void onAccept() {
                        simpleIconMessageDialog.dismiss();

                        Intent result = new Intent();
                        result.putExtra(Constants.BUNDLE_REGISTER_REPORT_RESULT, true);
                        getActivity().setResult(getActivity().RESULT_OK, result);
                        getActivity().finish();
                    }
                });
        simpleIconMessageDialog.setCancelable(false);

        FragmentTransaction fragmentTransaction = getActivity().getSupportFragmentManager().beginTransaction();
        fragmentTransaction.add(simpleIconMessageDialog, null);

        fragmentTransaction.commitAllowingStateLoss();
    }

    @Override
    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
        Calendar selectedDate = Calendar.getInstance();
        selectedDate.set(year, monthOfYear, dayOfMonth);
        mEditDate.setText(mDateFormatter.format(selectedDate.getTime()));
    }

    private void setupSpinnerList() {
        String[] orientarionList = getActivity().getResources().getStringArray(R.array.pending_list);
        ArrayAdapter<String> regionsAdapter = new ArrayAdapter<>(getActivity(), R.layout.rejection_item, orientarionList);
        mSpinnerComment.setAdapter(regionsAdapter);

        mSpinnerComment.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                mCommentSelected = (String) adapterView.getItemAtPosition(i);

                try {
                    if (mCommentSelected.equals("Seleccionar")) {

                        ((TextView) adapterView.getChildAt(0)).setTextColor(ContextCompat.getColor(getActivity(), R.color.text_hint_color));
                        hideInputComment();

                    } else if (mCommentSelected.equals("Otro")) {

                        showInputComment();

                    } else {
                        ((TextView) adapterView.getChildAt(0)).setTextColor(ContextCompat.getColor(getActivity(), R.color.text_color_gray));
                        hideInputComment();
                    }
                } catch (Exception ex) {

                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
    }

    private void showInputComment() {
        mEditComments.setVisibility(View.VISIBLE);
        mCommentsInputLabel.setVisibility(View.VISIBLE);
    }

    private void hideInputComment() {
        mEditComments.setVisibility(View.GONE);
        mCommentsInputLabel.setVisibility(View.GONE);
    }

    private void showImagePicker() {

//        if (mListener != null)
//            mListener.onPictureButtonClick();

        Intent chooseImageIntent = ImagePicker.getPickImageIntent(getActivity(), mCurrentReport.getTicket(), "pending_picture_1.jpg");
        startActivityForResult(chooseImageIntent, PICK_IMAGE_ID);
    }

    private void lockButtons() {
        mButtonSend.setEnabled(false);
    }

    private void unlockButtons() {
        mButtonSend.setEnabled(true);
    }

    public interface PendingReportFragmentListener {
        void onSendButtonClick();
        void onSendReportError();
    }
}

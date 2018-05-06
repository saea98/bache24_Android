package com.cmi.bache24.ui.fragment.troop;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
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

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * to handle interaction events.
 */
public class FakeReportFragment extends Fragment implements View.OnClickListener  {

    private FakeReportFragmentListener mListener;

    private User mCurrentUser;
    private Report mCurrentReport;

    private ImageView mImageView1;
    private ImageView mImageView2;
    private ImageView mImageView3;
    private EditText mEditComments;
    private Button mButtonSend;

    private boolean picture1Added = false;
    private boolean picture2Added = false;
    private boolean picture3Added = false;
    private LinearLayout mProgressLayout;

    private static final int PICK_IMAGE_ID = 234;
    private int mPictureToAdd = 0;
    private LinearLayout mRootLayout;

    private Context mCurrentContext;

    public FakeReportFragment() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_fake_report, container, false);

        mImageView1 = (ImageView) rootView.findViewById(R.id.imageView17);
        mImageView2 = (ImageView) rootView.findViewById(R.id.imageView18);
        mImageView3 = (ImageView) rootView.findViewById(R.id.imageView19);
        mEditComments = (EditText) rootView.findViewById(R.id.edit_comments);
        mButtonSend = (Button) rootView.findViewById(R.id.button_send);
        mRootLayout = (LinearLayout) rootView.findViewById(R.id.root_view);

        mProgressLayout = (LinearLayout) rootView.findViewById(R.id.progress_layout);
        mProgressLayout.setVisibility(View.GONE);

        mImageView1.setOnClickListener(this);
        mImageView2.setOnClickListener(this);
        mImageView3.setOnClickListener(this);
        mButtonSend.setOnClickListener(this);

        mCurrentUser = PreferencesManager.getInstance().getUserInfo(getActivity());

        mRootLayout.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                Utils.hideKeyboard(getActivity(), view);
                return false;
            }
        });

        return rootView;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        mCurrentContext = context;

        try{
            if (context instanceof Activity) {
                mListener = (FakeReportFragmentListener) context;
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
        if (view.getId() == mImageView1.getId()) {

            mPictureToAdd = 0;
            showImagePicker(mPictureToAdd);

        } else if (view.getId() == mImageView2.getId()) {

            mPictureToAdd = 1;
            showImagePicker(mPictureToAdd);

        } else if (view.getId() == mImageView3.getId()) {

            mPictureToAdd = 2;
            showImagePicker(mPictureToAdd);

        } else if (view.getId() == mButtonSend.getId()) {
            lockButtons();
            processReport();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        Bitmap bitmap;

        if (requestCode == PICK_IMAGE_ID && resultCode == getActivity().RESULT_OK) {
            switch (mPictureToAdd) {
                case 0:
                    bitmap = ImagePicker.getImageFromResult(getActivity(), resultCode, data, mCurrentReport.getTicket(), "f_b_picture_1.jpg");
                    mImageView1.setImageBitmap(bitmap);
                    mImageView1.setScaleType(ImageView.ScaleType.CENTER_CROP);
                    picture1Added = true;
                    break;
                case 1:
                    bitmap = ImagePicker.getImageFromResult(getActivity(), resultCode, data, mCurrentReport.getTicket(), "f_b_picture_2.jpg");
                    mImageView2.setImageBitmap(bitmap);
                    mImageView2.setScaleType(ImageView.ScaleType.CENTER_CROP);
                    picture2Added = true;
                    break;
                case 2:
                    bitmap = ImagePicker.getImageFromResult(getActivity(), resultCode, data, mCurrentReport.getTicket(), "f_b_picture_3.jpg");
                    mImageView3.setImageBitmap(bitmap);
                    mImageView3.setScaleType(ImageView.ScaleType.CENTER_CROP);
                    picture3Added = true;
                    break;
                default:
                    bitmap = ImagePicker.getImageFromResult(getActivity(), resultCode, data, mCurrentReport.getTicket(), "f_b_picture_1.jpg");
                    mImageView1.setImageBitmap(bitmap);
                    mImageView1.setScaleType(ImageView.ScaleType.CENTER_CROP);
                    picture1Added = true;
                    break;
            }
        }
    }

    public void setReport(Report report) {
        if (report == null)
            return;

        this.mCurrentReport = report;
        mEditComments.setText(getResources().getString(R.string.false_report_default_comment));
    }

    private void processReport() {
        Bitmap bitmapPicture1 = ((BitmapDrawable) mImageView1.getDrawable()).getBitmap();
        Bitmap bitmapPicture2 = ((BitmapDrawable) mImageView2.getDrawable()).getBitmap();
        Bitmap bitmapPicture3 = ((BitmapDrawable) mImageView3.getDrawable()).getBitmap();

        if ((bitmapPicture1 == null || picture1Added != true) ||
                (bitmapPicture2 == null || picture2Added != true) ||
                (bitmapPicture3 == null || picture3Added != true)) {
            Toast.makeText(getActivity(), "Las fotos son requeridas", Toast.LENGTH_SHORT).show();
            unlockButtons();
            return;
        }

        if (mEditComments.getText().toString().trim().isEmpty()) {
            Toast.makeText(getActivity(), "Los comentarios son requeridos", Toast.LENGTH_SHORT).show();
            unlockButtons();
            return;
        }

        final AttendedReport attendedReport = new AttendedReport();
        attendedReport.setToken(mCurrentUser.getToken());
        attendedReport.setTicket(mCurrentReport.getTicket());
        attendedReport.setEtapa(Constants.TROOP_REPORT_STATUS_FALSE);

        attendedReport.setImage1(Utils.bitmapToBase64(bitmapPicture1, Constants.PICTURE_SQUAD_QUALITY));
        attendedReport.setImage2(Utils.bitmapToBase64(bitmapPicture2, Constants.PICTURE_SQUAD_QUALITY));
        attendedReport.setImage3(Utils.bitmapToBase64(bitmapPicture3, Constants.PICTURE_SQUAD_QUALITY));
        attendedReport.setDescripcion(mEditComments.getText().toString());
        attendedReport.setCausa("-");

        PreferencesManager.getInstance().clearSendReportRetry(mCurrentContext);

        final ReportRepairedDialog reportRepairedDialog = new ReportRepairedDialog();
        reportRepairedDialog.setType(ReportRepairedDialog.ReportType.FAKE);
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
            mListener.onPictureButtonClick();

        ServicesManager.sendWrongReport(mCurrentContext, mCurrentUser, report, new NewReportCallback() {

            @Override
            public void onSuccessRegister(String resultCode, Report reportCompleteInfo) {

            }

            @Override
            public void onSuccessRegisterReport(Report reportCompleteInfo) {

                mProgressLayout.setVisibility(View.GONE);

                unlockButtons();

                PreferencesManager.getInstance().setReportAttentionInProgress(getActivity(), false);
                PreferencesManager.getInstance().setReportCanceled("FakeReportFragment - sendAttendedReport-onSuccessRegisterReport", getActivity(), true);

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
                    PreferencesManager.getInstance().setReportCanceled("FakeReportFragment - onFailRegisterReport", getActivity(), true);

                    showAlert(true,
                            getResources().getString(R.string.report_attention_alert_title_0),
                            getAlertMessage(report.getTicket(), true));

                } else if (message.equals(Constants.REPORTE_ATTENDED)) {

                    PreferencesManager.getInstance().setReportAttentionInProgress(getActivity(), false);
                    PreferencesManager.getInstance().setReportCanceled("FakeReportFragment - onFailRegisterReport", getActivity(), true);

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
        messageSB.append(getResources().getString(R.string.report_attention_alert_status_4));
        if (hasError) {
            messageSB.append(", ");
            messageSB.append(getResources().getString(R.string.report_attention_alert_message_agu_3));
        } else {
            messageSB.append(".");
        }

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

    private void showImagePicker(int position) {

        if (mListener != null)
            mListener.onPictureButtonClick();

        Intent chooseImageIntent = ImagePicker.getPickImageIntent(getActivity(), mCurrentReport.getTicket(), "f_b_picture_" + (position + 1) +".jpg");
        startActivityForResult(chooseImageIntent, PICK_IMAGE_ID);
    }

    private void lockButtons() {
        mButtonSend.setEnabled(false);
    }

    private void unlockButtons() {
        mButtonSend.setEnabled(true);
    }

    public interface FakeReportFragmentListener {
        void onPictureButtonClick();
        void onSendReportError();
    }
}

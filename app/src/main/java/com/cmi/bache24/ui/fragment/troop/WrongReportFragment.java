package com.cmi.bache24.ui.fragment.troop;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
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

public class WrongReportFragment extends Fragment implements View.OnClickListener {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private WrongReportFragmentListener mListener;
    private User mCurrentUser;
    private Report mCurrentReport;

    private ImageView mImageView1;
    private ImageView mImageView2;
    private ImageView mImageView3;
    private EditText mEditComments;
    private Spinner mSpinnerList;
    private Button mButtonSend;
    private String mSpinnerItemSelected;

    private boolean picture1Added = false;
    private boolean picture2Added = false;
    private boolean picture3Added = false;

    private static final int PICK_IMAGE_ID = 234;
    private int mPictureToAdd = 0;
    private LinearLayout mProgressLayout;
    private LinearLayout mRootLayout;

    private Context mCurrentContext;

    public WrongReportFragment() {

    }

    // TODO: Rename and change types and number of parameters
    public static WrongReportFragment newInstance(String param1, String param2) {
        WrongReportFragment fragment = new WrongReportFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_wrong_report, container, false);

        mImageView1 = (ImageView) rootView.findViewById(R.id.imageView17);
        mImageView2 = (ImageView) rootView.findViewById(R.id.imageView18);
        mImageView3 = (ImageView) rootView.findViewById(R.id.imageView19);
        mEditComments = (EditText) rootView.findViewById(R.id.edit_comments);
        mSpinnerList = (Spinner) rootView.findViewById(R.id.spinner_list);
        mButtonSend = (Button) rootView.findViewById(R.id.button_send);

        mProgressLayout = (LinearLayout) rootView.findViewById(R.id.progress_layout);
        mRootLayout = (LinearLayout) rootView.findViewById(R.id.root_view);

        mProgressLayout.setVisibility(View.GONE);

        mImageView1.setOnClickListener(this);
        mImageView2.setOnClickListener(this);
        mImageView3.setOnClickListener(this);
        mButtonSend.setOnClickListener(this);

        mCurrentUser = PreferencesManager.getInstance().getUserInfo(mCurrentContext);

        getRejectionList();

        mRootLayout.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                Utils.hideKeyboard(mCurrentContext, view);
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
                mListener = (WrongReportFragmentListener) context;
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

            if (mListener != null)
                mListener.onCompletReportClick();

            mPictureToAdd = 0;

            showImagePicker(mPictureToAdd);

        } else if (view.getId() == mImageView2.getId()) {

            if (mListener != null)
                mListener.onCompletReportClick();

            mPictureToAdd = 1;

            showImagePicker(mPictureToAdd);

        } else if (view.getId() == mImageView3.getId()) {

            if (mListener != null)
                mListener.onCompletReportClick();

            mPictureToAdd = 2;

            showImagePicker(mPictureToAdd);

        } else if (view.getId() == mButtonSend.getId()) {
            lockButtons();
            processReport();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.i("WRONG", "onResume");
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        Bitmap bitmap;

        if (requestCode == PICK_IMAGE_ID && resultCode == getActivity().RESULT_OK) {
            switch (mPictureToAdd) {
                case 0:
                    bitmap = ImagePicker.getImageFromResult(mCurrentContext, resultCode, data, mCurrentReport.getTicket(), "w_b_picture_1.jpg");
                    mImageView1.setImageBitmap(bitmap);
                    mImageView1.setScaleType(ImageView.ScaleType.CENTER_CROP);
                    picture1Added = true;
                    break;
                case 1:
                    bitmap = ImagePicker.getImageFromResult(mCurrentContext, resultCode, data, mCurrentReport.getTicket(), "w_b_picture_2.jpg");
                    mImageView2.setImageBitmap(bitmap);
                    mImageView2.setScaleType(ImageView.ScaleType.CENTER_CROP);
                    picture2Added = true;
                    break;
                case 2:
                    bitmap = ImagePicker.getImageFromResult(mCurrentContext, resultCode, data, mCurrentReport.getTicket(), "w_b_picture_3.jpg");
                    mImageView3.setImageBitmap(bitmap);
                    mImageView3.setScaleType(ImageView.ScaleType.CENTER_CROP);
                    picture3Added = true;
                    break;
                default:
                    bitmap = ImagePicker.getImageFromResult(mCurrentContext, resultCode, data, mCurrentReport.getTicket(), "w_b_picture_1.jpg");
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

        updateDefaultComment("[motivo]");
    }

    public void updateDefaultComment(String value) {
        mEditComments.setText(getResources().getString(R.string.wrong_report_default_comment) + " " + value + ".");
    }

    private void getRejectionList() {

        String[] regions = mCurrentContext.getResources().getStringArray(R.array.motivos_array);
        ArrayAdapter<String> regionsAdapter = new ArrayAdapter<>(mCurrentContext, R.layout.rejection_item, regions);
        mSpinnerList.setAdapter(regionsAdapter);


        mSpinnerList.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                mSpinnerItemSelected = (String) adapterView.getItemAtPosition(i);

                try {
                    if (mSpinnerItemSelected.equals("Seleccionar"))
                        ((TextView) adapterView.getChildAt(0)).setTextColor(ContextCompat.getColor(mCurrentContext, R.color.text_hint_color));
                    else {
                        ((TextView) adapterView.getChildAt(0)).setTextColor(ContextCompat.getColor(mCurrentContext, R.color.text_color_gray));
                        updateDefaultComment(mSpinnerItemSelected);
                    }

                } catch (Exception ex) {

                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
    }

    private void processReport() {
        Bitmap bitmapPicture1 = ((BitmapDrawable) mImageView1.getDrawable()).getBitmap();
        Bitmap bitmapPicture2 = ((BitmapDrawable) mImageView2.getDrawable()).getBitmap();
        Bitmap bitmapPicture3 = ((BitmapDrawable) mImageView3.getDrawable()).getBitmap();

        if ((bitmapPicture1 == null || picture1Added != true) ||
            (bitmapPicture2 == null || picture2Added != true) ||
            (bitmapPicture3 == null || picture3Added != true)) {
            Toast.makeText(mCurrentContext, "Las fotos son requeridas", Toast.LENGTH_SHORT).show();
            unlockButtons();
            return;
        }

        if (mEditComments.getText().toString().trim().isEmpty()) {
            Toast.makeText(mCurrentContext, "Los comentarios son requeridos", Toast.LENGTH_SHORT).show();
            unlockButtons();
            return;
        }

        if (mSpinnerItemSelected.equals("Seleccionar")) {
            Toast.makeText(mCurrentContext, "Elije una causa", Toast.LENGTH_SHORT).show();
            unlockButtons();
            return;
        }

        final AttendedReport attendedReport = new AttendedReport();
        attendedReport.setToken(mCurrentUser.getToken());
        attendedReport.setTicket(mCurrentReport.getTicket());
        attendedReport.setEtapa(Constants.TROOP_REPORT_STATUS_WRONG);

        attendedReport.setImage1(Utils.bitmapToBase64(bitmapPicture1, Constants.PICTURE_SQUAD_QUALITY));
        attendedReport.setImage2(Utils.bitmapToBase64(bitmapPicture2, Constants.PICTURE_SQUAD_QUALITY));
        attendedReport.setImage3(Utils.bitmapToBase64(bitmapPicture3, Constants.PICTURE_SQUAD_QUALITY));
        attendedReport.setDescripcion(mEditComments.getText().toString());
        attendedReport.setCausa(mSpinnerItemSelected);

//        PreferencesManager.getInstance().setShouldSendReportToStatus2(mCurrentContext, false);
        PreferencesManager.getInstance().clearSendReportRetry(mCurrentContext);

        final ReportRepairedDialog reportRepairedDialog = new ReportRepairedDialog();
        reportRepairedDialog.setType(ReportRepairedDialog.ReportType.MARK_AS_WRONG);
        reportRepairedDialog.setCommentsDialogListener(new CommentsDialogListener() {

            @Override
            public void onCancel() {
                reportRepairedDialog.dismiss();
                unlockButtons();
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

//        reportRepairedDialog.show(((AppCompatActivity)mCurrentContext).getFragmentManager(), "");
    }

    private void sendReport(final AttendedReport report) {

        mProgressLayout.setVisibility(View.VISIBLE);

        if (mListener != null)
            mListener.onCompletReportClick();

        ServicesManager.sendWrongReport(mCurrentContext, mCurrentUser, report, new NewReportCallback() {

            @Override
            public void onSuccessRegister(String resultCode, Report reportCompleteInfo) {

            }

            @Override
            public void onSuccessRegisterReport(Report reportCompleteInfo) {

                mProgressLayout.setVisibility(View.GONE);

                unlockButtons();

                PreferencesManager.getInstance().setReportAttentionInProgress(mCurrentContext, false);
                PreferencesManager.getInstance().setReportCanceled("WrongReportFragment - sendAttendedReport-onSuccessRegisterReport", mCurrentContext, true);

                showAlert(true,
                        getResources().getString(R.string.report_attention_alert_title_0),
                        getAlertMessage(report.getTicket(), false));
            }

            @Override
            public void onFailRegisterReport(String message) {
                mProgressLayout.setVisibility(View.GONE);

                unlockButtons();

                if (message.equals(Constants.AGU_DESCRIPTION)) {

                    PreferencesManager.getInstance().setReportAttentionInProgress(mCurrentContext, false);
                    PreferencesManager.getInstance().setReportCanceled("WrongReportFragment - onFailRegisterReport", mCurrentContext, true);

                    showAlert(true,
                            getResources().getString(R.string.report_attention_alert_title_0),
                            getAlertMessage(report.getTicket(), true));

                } else if (message.equals(Constants.REPORTE_ATTENDED)) {

                    PreferencesManager.getInstance().setReportAttentionInProgress(mCurrentContext, false);
                    PreferencesManager.getInstance().setReportCanceled("WrongReportFragment - onFailRegisterReport", mCurrentContext, true);

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
                Utils.showLoginForBadToken(mCurrentContext);
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
        messageSB.append(getResources().getString(R.string.report_attention_alert_status_3));
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

        Intent chooseImageIntent = ImagePicker.getPickImageIntent(mCurrentContext, mCurrentReport.getTicket(), "w_b_picture_" + (position + 1) + ".jpg");
        startActivityForResult(chooseImageIntent, PICK_IMAGE_ID);
    }

    private void lockButtons() {
        mButtonSend.setEnabled(false);
    }

    private void unlockButtons() {
        mButtonSend.setEnabled(true);
    }

    public interface WrongReportFragmentListener {
        void onCompletReportClick();
        void onSendReportError();
    }
}

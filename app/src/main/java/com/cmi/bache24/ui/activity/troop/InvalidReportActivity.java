package com.cmi.bache24.ui.activity.troop;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.cmi.bache24.R;
import com.cmi.bache24.data.model.AttendedReport;
import com.cmi.bache24.data.model.Report;
import com.cmi.bache24.data.model.User;
import com.cmi.bache24.data.remote.ServicesManager;
import com.cmi.bache24.data.remote.interfaces.NewReportCallback;
import com.cmi.bache24.ui.dialog.SimpleIconMessageDialog;
import com.cmi.bache24.ui.dialog.interfaces.MessageDialogListener;
import com.cmi.bache24.util.Constants;
import com.cmi.bache24.util.ImagePicker;
import com.cmi.bache24.util.PreferencesManager;
import com.cmi.bache24.util.Utils;
import com.google.gson.Gson;

public class InvalidReportActivity extends AppCompatActivity implements View.OnClickListener {

    private Toolbar toolbar;
    private TextView toolbarTitle;
    private RelativeLayout rootLayout;
    private LinearLayout progressLayout;
    private ImageView pictureImageView;
    private Button cancelButton;
    private Button acceptButton;

    private int pictureToAdd = 0;
    private static final int PICK_IMAGE_ID = 234;
    private Report currentReport;
    private boolean picture1Added = false;
    private User currentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        setTheme(R.style.AppThemePink);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_invalid_report);

        rootLayout = (RelativeLayout) findViewById(R.id.root_view);
        progressLayout = (LinearLayout) findViewById(R.id.progress_layout);
        pictureImageView = (ImageView) findViewById(R.id.imageView17);
        cancelButton = (Button) findViewById(R.id.button_cancel);
        acceptButton = (Button) findViewById(R.id.button_send);

        progressLayout.setVisibility(View.GONE);
        pictureImageView.setOnClickListener(this);
        cancelButton.setOnClickListener(this);
        acceptButton.setOnClickListener(this);

        rootLayout.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                Utils.hideKeyboard(InvalidReportActivity.this, view);
                return false;
            }
        });

        String reportString = getIntent().getStringExtra(Constants.EXTRA_REPORT_STATUS_DETAIL);
        if (reportString != "") {
            currentReport = new Gson().fromJson(reportString, Report.class);
        }

        currentUser = PreferencesManager.getInstance().getUserInfo(this);

        setupToolbar();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        Bitmap bitmap;

        if (requestCode == PICK_IMAGE_ID && resultCode == RESULT_OK) {
            bitmap = ImagePicker.getImageFromResult(this, resultCode, data, currentReport.getTicket(), "invalid_picture_1.jpg");
            pictureImageView.setImageBitmap(bitmap);
            pictureImageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
            picture1Added = true;
        }
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == pictureImageView.getId()) {

            pictureToAdd = 0;
            showImagePicker();

        } else if (v.getId() == cancelButton.getId()) {

            finish();

        } else if (v.getId() == acceptButton.getId()) {
            lockButtons();
            processReport();
        }
    }

    private void setupToolbar() {
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbarTitle = (TextView) findViewById(R.id.toolbar_title);

        if (currentReport != null)
            toolbarTitle.setText(currentReport.getTicket());

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        getSupportActionBar().setDisplayShowHomeEnabled(false);
    }

    private void showImagePicker() {
        Intent chooseImageIntent = ImagePicker.getPickImageIntent(this, currentReport.getTicket(), "invalid_picture_1.jpg");
        startActivityForResult(chooseImageIntent, PICK_IMAGE_ID);
    }

    private void lockButtons() {
        cancelButton.setEnabled(false);
        acceptButton.setEnabled(false);
    }

    private void unlockButtons() {
        cancelButton.setEnabled(true);
        acceptButton.setEnabled(true);
    }

    private void processReport() {

        Bitmap bitmapPicture1 = ((BitmapDrawable) pictureImageView.getDrawable()).getBitmap();

        if ((bitmapPicture1 == null || picture1Added != true)) {
            Toast.makeText(this, "La foto es requerida", Toast.LENGTH_SHORT).show();
            unlockButtons();
            return;
        }

        String imageBase64String = Utils.bitmapToBase64(bitmapPicture1, Constants.PICTURE_SQUAD_QUALITY);

        final AttendedReport attendedReport = new AttendedReport();
        attendedReport.setToken(currentUser.getToken());
        attendedReport.setTicket(currentReport.getTicket());
        attendedReport.setImage1(imageBase64String);

        sendReport(attendedReport);
    }

    private void sendReport(final AttendedReport report) {

        progressLayout.setVisibility(View.VISIBLE);
        ServicesManager.sendSecondaryRoadReport(this, currentUser, report, new NewReportCallback() {

            @Override
            public void onSuccessRegister(String resultCode, Report reportCompleteInfo) {

            }

            @Override
            public void onSuccessRegisterReport(Report reportCompleteInfo) {

                progressLayout.setVisibility(View.GONE);

                unlockButtons();

                showAlert(true,
                        getResources().getString(R.string.report_attention_alert_title_0),
                        getAlertMessage(report.getTicket(), false));
            }

            @Override
            public void onFailRegisterReport(String message) {
                progressLayout.setVisibility(View.GONE);

                unlockButtons();

                if (message.equals(Constants.AGU_DESCRIPTION)) {
                    showAlert(true,
                            getResources().getString(R.string.report_attention_alert_title_0),
                            getAlertMessage(report.getTicket(), true));

                } else if (message.equals(Constants.REPORTE_ATTENDED)) {
                    showAlertReportAttended();
                } else {
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
                Utils.showLoginForBadToken(InvalidReportActivity.this);
            }
        });
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
                            setResult(RESULT_OK, result);
                            finish();
                        }
                    }
                });
        simpleIconMessageDialog.setCancelable(false);

        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.add(simpleIconMessageDialog, null);

        fragmentTransaction.commitAllowingStateLoss();
    }

    private String getAlertMessage(String reportTicket, boolean hasError) {
        StringBuilder messageSB = new StringBuilder();
        messageSB.append(getResources().getString(R.string.report_attention_alert_message_agu_1));
        messageSB.append(" ");
        messageSB.append(reportTicket);
        messageSB.append(" ");
        messageSB.append(getResources().getString(R.string.report_attention_alert_message_agu_2));
        messageSB.append(" ");
        messageSB.append(getResources().getString(R.string.report_attention_alert_status_2));
        messageSB.append(".");

        return messageSB.toString();
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
                        setResult(RESULT_OK, result);
                        finish();
                    }
                });
        simpleIconMessageDialog.setCancelable(false);

        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.add(simpleIconMessageDialog, null);

        fragmentTransaction.commitAllowingStateLoss();
    }
}

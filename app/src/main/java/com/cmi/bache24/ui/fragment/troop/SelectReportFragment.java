package com.cmi.bache24.ui.fragment.troop;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AlertDialog;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.cmi.bache24.R;
import com.cmi.bache24.data.model.Report;
import com.cmi.bache24.data.model.User;
import com.cmi.bache24.data.remote.ServicesManager;
import com.cmi.bache24.data.remote.interfaces.NewReportCallback;
import com.cmi.bache24.data.remote.interfaces.ReportsCallback;
import com.cmi.bache24.data.remote.interfaces.SingleReportCallback;
import com.cmi.bache24.ui.activity.ImageViewerActivity;
import com.cmi.bache24.ui.activity.troop.FakeReportActivity;
import com.cmi.bache24.ui.activity.troop.InvalidReportActivity;
import com.cmi.bache24.ui.activity.troop.PendingReportActivity;
import com.cmi.bache24.ui.activity.troop.ReallocateReportActivity;
import com.cmi.bache24.ui.activity.troop.ValidReportActivity;
import com.cmi.bache24.ui.activity.troop.WrongReportActivity;
import com.cmi.bache24.ui.dialog.BachesToRepairDialog;
import com.cmi.bache24.ui.dialog.ReportRepairedDialog;
import com.cmi.bache24.ui.dialog.SimpleIconMessageDialog;
import com.cmi.bache24.ui.dialog.interfaces.CommentsDialogListener;
import com.cmi.bache24.ui.dialog.interfaces.MessageDialogListener;
import com.cmi.bache24.ui.dialog.interfaces.ReportsToFixDialogListener;
import com.cmi.bache24.ui.fragment.BaseFragment;
import com.cmi.bache24.util.Constants;
import com.cmi.bache24.util.PreferencesManager;
import com.cmi.bache24.util.Utils;
import com.google.android.gms.maps.model.LatLng;
import com.google.gson.Gson;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * A placeholder fragment containing a simple view.
 */
public class SelectReportFragment extends BaseFragment implements View.OnClickListener {

    private Report mCurrentReport;
//    private TextView mTextAddress;
    private TextView mTextComments;
    private ImageView mImagePicture1;
    private ImageView mImagePicture2;
    private ImageView mImagePicture3;
    private ImageView mImagePicture4;
    private TextView mGoogleMapsAddressTextView;
    private boolean mPictureAdded = false;
    private User mCurrentUser;

    private static final int PICK_IMAGE_ID = 234;
    private LinearLayout mProgressLayout;
    private List<ImageView> mUserPictures;

    private Button mButtonFix;
    private Button mButtonWrongReport;
    private Button mButtonInvalidReport;
    private Button mButtonFakeReport;
    private Button mAppPendingButton;
    private Button mAguPendingButton;
    private Button mRealloacateButton;
    private Button mReleaseReportButton;

    private TextView mPrimaryRoadValue;
    private TextView reportOriginTextView;

    private LinearLayout mAppOptionsLayout;
    private LinearLayout mAguOptionsLayout;

    private SelectReportFragmentListener selectReportFragmentListener;

    private String mReportAttentionOption = "";
    private static final String ValidReportOption = "valid_report_option";
    private static final String WrongReportOption = "wrong_report_option";
    private static final String FalseReportOption = "false_report_option";
    private static final String InvalidReportOption = "invallid_report_option";

    public SelectReportFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_select_report, container, false);

//        mTextAddress = (TextView) rootView.findViewById(R.id.textview_address);
        mTextComments = (TextView) rootView.findViewById(R.id.textview_comments);
        mImagePicture1 = (ImageView) rootView.findViewById(R.id.imageview_picture1);
        mImagePicture2 = (ImageView) rootView.findViewById(R.id.imageview_picture2);
        mImagePicture3 = (ImageView) rootView.findViewById(R.id.imageview_picture3);
        mImagePicture4 = (ImageView) rootView.findViewById(R.id.imageview_picture4);
        mButtonFix = (Button) rootView.findViewById(R.id.button_fix);
        mButtonWrongReport = (Button) rootView.findViewById(R.id.button_wrong_report_1);
        mButtonFakeReport = (Button) rootView.findViewById(R.id.button_fake_report);
        mButtonInvalidReport = (Button) rootView.findViewById(R.id.button_invalid);
        mGoogleMapsAddressTextView = (TextView) rootView.findViewById(R.id.textview_google_maps_address);

        mPrimaryRoadValue = (TextView) rootView.findViewById(R.id.textview_primary);
        reportOriginTextView = (TextView) rootView.findViewById(R.id.textview_origin);

        mAppOptionsLayout = (LinearLayout) rootView.findViewById(R.id.layout_app_options);
        mAguOptionsLayout = (LinearLayout) rootView.findViewById(R.id.layout_agu_options);


        mAppPendingButton = (Button) rootView.findViewById(R.id.button_pending_report);
        mAguPendingButton = (Button) rootView.findViewById(R.id.button_pending_report_agu);
        mRealloacateButton = (Button) rootView.findViewById(R.id.button_reallocate_report);

        mReleaseReportButton = (Button) rootView.findViewById(R.id.button_release_report);

        mAppOptionsLayout.setVisibility(View.GONE);
        mAguOptionsLayout.setVisibility(View.GONE);

        mReleaseReportButton.setVisibility(View.GONE);

        mButtonFix.setOnClickListener(this);
        mButtonWrongReport.setOnClickListener(this);
        mButtonFakeReport.setOnClickListener(this);
        mImagePicture1.setOnClickListener(this);
        mImagePicture2.setOnClickListener(this);
        mImagePicture3.setOnClickListener(this);
        mImagePicture4.setOnClickListener(this);
        mGoogleMapsAddressTextView.setOnClickListener(this);
        mButtonInvalidReport.setOnClickListener(this);

        mAppPendingButton.setOnClickListener(this);
        mAguPendingButton.setOnClickListener(this);
        mRealloacateButton.setOnClickListener(this);
        mReleaseReportButton.setOnClickListener(this);

        mButtonFix.setVisibility(View.VISIBLE);

        setImagesSize();

        mUserPictures = new ArrayList<>();
        mUserPictures.add(mImagePicture1);
        mUserPictures.add(mImagePicture2);
        mUserPictures.add(mImagePicture3);
        mUserPictures.add(mImagePicture4);

        mCurrentUser = PreferencesManager.getInstance().getUserInfo(getActivity());

        mProgressLayout = (LinearLayout) rootView.findViewById(R.id.progress_layout);
        mProgressLayout.setVisibility(View.GONE);

        return rootView;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        mCurrentContext = context;

        try{
            if (context instanceof Activity) {
                selectReportFragmentListener = (SelectReportFragmentListener) context;
            }
        } catch (ClassCastException ex) {

        }
    }

    @Override
    public void onResume() {
        super.onResume();
        unlockButtons();
        getAllReports();
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == mButtonFix.getId()) {
            mReportAttentionOption = ValidReportOption;
            lockButtons();
            checkCameraPermissions();
        }

        if (view.getId() == mButtonWrongReport.getId()) {
            mReportAttentionOption = WrongReportOption;
            lockButtons();
            checkCameraPermissions();
        }

        if (view.getId() == mButtonFakeReport.getId()) {
            mReportAttentionOption = FalseReportOption;
            lockButtons();
            checkCameraPermissions();
        }

        if (view.getId() == mAppPendingButton.getId()) {
            lockButtons();
            showPendingReportActivity();
        }

        if (view.getId() == mAguPendingButton.getId()) {
            lockButtons();
            showPendingReportActivity();
        }

        if (view.getId() == mRealloacateButton.getId()) {
            lockButtons();
            showReallocateReportActivity();
        }


        if (view.getId() == mImagePicture1.getId() ||
                view.getId() == mImagePicture2.getId() ||
                view.getId() == mImagePicture3.getId() ||
                view.getId() == mImagePicture4.getId()) {
            if (mCurrentReport.getPictures() != null) {
                try {
                    showImageViewer(mCurrentReport.getPictures().get(Integer.parseInt(view.getTag().toString())).getFoto());
                } catch (Exception ex) {
                    showImageViewer("");
                }
            }
        }

        if (view.getId() == mGoogleMapsAddressTextView.getId()) {
            if (mGoogleMapsAddressTextView.getText().equals(getResources().getString(R.string.address_not_found_short))) {
                return;
            }

            Uri gmmIntentUri = Uri.parse("google.navigation:q=" + mCurrentReport.getLatitude() + ",+" + mCurrentReport.getLongitude() + "");
            Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
            mapIntent.setPackage("com.google.android.apps.maps");
            startActivity(mapIntent);

        }

        if (view.getId() == mButtonInvalidReport.getId()) {

            mReportAttentionOption = InvalidReportOption;
            lockButtons();
            checkCameraPermissions();

//            lockButtons();
//            showSecondaryRoadDialog();
        }

        if (view.getId() == mReleaseReportButton.getId()) {
            lockButtons();
            showReleaseReportDialog();
        }
    }

    private void setImagesSize() {
        int screenWidth = Utils.getScreenWidth(getActivity());

        screenWidth = screenWidth - Utils.convertDpToPx(getActivity(), 16 * 2) - (5 * (4 - 1));

        int squareSide = (screenWidth / 4) - 5;

        LinearLayout.LayoutParams pictureParams =
                new LinearLayout.LayoutParams(squareSide, squareSide);

        pictureParams.setMargins(0, 0, 5, 0);

        mImagePicture1.setLayoutParams(pictureParams);
        mImagePicture2.setLayoutParams(pictureParams);
        mImagePicture3.setLayoutParams(pictureParams);
        mImagePicture4.setLayoutParams(pictureParams);

        mImagePicture1.setScaleType(ImageView.ScaleType.CENTER_CROP);
        mImagePicture2.setScaleType(ImageView.ScaleType.CENTER_CROP);
        mImagePicture3.setScaleType(ImageView.ScaleType.CENTER_CROP);
        mImagePicture4.setScaleType(ImageView.ScaleType.CENTER_CROP);

        mImagePicture1.setVisibility(View.GONE);
        mImagePicture2.setVisibility(View.GONE);
        mImagePicture3.setVisibility(View.GONE);
        mImagePicture4.setVisibility(View.GONE);

        RelativeLayout.LayoutParams pictureParams2 =
                new RelativeLayout.LayoutParams(squareSide, squareSide);
        pictureParams2.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
        pictureParams2.setMargins(0, 10, 5, 0);
    }

    public void showReportDetail(Report report) {
        if (report == null)
            return;

        this.mCurrentReport = report;

        setGoogleMapsTextAddress(getResources().getString(R.string.report_current_address_title_searching));
//        this.mTextAddress.setText(report.getAddress().trim().isEmpty() ? "Sin dirección" : report.getAddress().trim());
        this.mTextComments.setText(report.getDescription().trim().isEmpty() ? "Sin comentarios" : report.getDescription().trim());

        if (report.getLatitude().trim().length() > 0 && report.getLongitude().trim().length() > 0) {
            getAddressForPosition(new LatLng(Double.parseDouble(report.getLatitude()), Double.parseDouble(report.getLongitude())));
        } else {
            setGoogleMapsTextAddress("");
        }

        String isPrimaryRoadText = getResources().getString(R.string.troop_report_primary_title);
        mPrimaryRoadValue.setText(isPrimaryRoadText + " " + getResources().getString(mCurrentReport.isPrimary() ? R.string.button_yes : R.string.button_no));

        String originRoadText = "";

        switch (mCurrentReport.getOrigin()) {
            case APP:
                originRoadText = "APP";
                break;
            case O72:
                originRoadText = "AGU";
                break;
            case CMS:
                originRoadText = "CMS";
                break;
            case SMS:
                originRoadText = "SMS";
                break;
            default:
                originRoadText = "APP";
                break;
        }

        reportOriginTextView.setText(getResources().getString(R.string.troop_report_origin_title) + " " + originRoadText);

        int citizePictureIndex = 0;

        try {
            if (report.getPictures() != null) {
                if (report.getPictures().size() > 0) {
                    for (int i = 0; i < report.getPictures().size(); i++) {

                        if (i == mUserPictures.size())
                            break;

                        if (report.getPictures().get(i).getEtapaId() == Constants.REPORT_STATUS_NEW ||
                                report.getPictures().get(i).getEtapaId() == Constants.REPORT_STATUS_ASIGNED) {

                            Picasso.with(getActivity())
                                    .load(report.getPictures().get(citizePictureIndex).getFoto())
                                    .error(R.drawable.no_foto)
                                    .placeholder(R.drawable.no_foto)
                                    .into(mUserPictures.get(citizePictureIndex));

                            mUserPictures.get(citizePictureIndex).setTag(citizePictureIndex);

                            mUserPictures.get(citizePictureIndex).setVisibility(View.VISIBLE);

                            citizePictureIndex++;
                        }
                    }
                }
            }
        } catch (Exception ex) {

        }

        if (mCurrentReport.getOrigin() == Report.ReportOrigin.O72) { // SI VIENE DE SALESFORCE
            mAguOptionsLayout.setVisibility(View.VISIBLE);
        } else {
            mAppOptionsLayout.setVisibility(View.VISIBLE);
        }

//        if (mCurrentReport.getEtapaId() == Constants.REPORT_STATUS_ASIGNED) {
//            mReleaseReportButton.setVisibility(View.VISIBLE);
//        }
    }

    /*
    private void sendReportUpdate(User currentUser, int status) {

        if (!Utils.getInstance().isInternetAvailable(getActivity()))
            return;

        ServicesManager.updateReportStatus(mCurrentContext, currentUser, mCurrentReport.getTicket(), status, new NewReportCallback() {

            @Override
            public void onSuccessRegister(String resultCode, Report reportCompleteInfo) {

            }

            @Override
            public void onSuccessRegisterReport(Report reportCompleteInfo) {

            }

            @Override
            public void onFailRegisterReport(String message) {

            }

            @Override
            public void userBanned() {

            }

            @Override
            public void onTokenDisabled() {

            }
        });
    }
    */

    private void showReportActivity(int noToFix) {
        Intent reportDetailIntent = new Intent(getActivity(), ValidReportActivity.class);

        Gson gson = new Gson();
        String jsonReport = gson.toJson(mCurrentReport);
        reportDetailIntent.putExtra(Constants.EXTRA_REPORT_STATUS_DETAIL, jsonReport);
        reportDetailIntent.putExtra("REPORT_NO_BACHES", noToFix);

        if (selectReportFragmentListener != null) {
            selectReportFragmentListener.onOptionClick();
        }

        PreferencesManager.getInstance().setReportAttentionInProgress(getActivity(), false);

        getActivity().startActivityForResult(reportDetailIntent, Constants.VALID_REPORT_REQUEST_CODE);
        getActivity().overridePendingTransition(R.anim.enter_from_right, R.anim.enter_from_left);
    }

    private void showImageViewer(String url) {
        Intent reportActivityIntent = new Intent(getActivity(), ImageViewerActivity.class);
        reportActivityIntent.putExtra(Constants.EXTRA_IMAGE_URL, url);

        startActivity(reportActivityIntent);
    }

    private void getAddressForPosition(final LatLng position) {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                try {
                    Geocoder geocoder = new Geocoder(getActivity(), Locale.getDefault());
                    List<Address> addresses = geocoder.getFromLocation(position.latitude, position.longitude, 1);

                    if (addresses.size() > 0) {
                        setGoogleMapsTextAddress(addresses.get(0).getAddressLine(0) + ", "
                                                        + addresses.get(0).getAddressLine(1) + ", "
                                                        + addresses.get(0).getAddressLine(2));

                    } else {
                        setGoogleMapsTextAddress("");
                    }
                } catch (Exception ex) {
                    setGoogleMapsTextAddress("");
                }
            }
        }, 500);
    }

    private void setGoogleMapsTextAddress(String text) {
        StringBuilder stringBuilder = new StringBuilder();

        stringBuilder.append("<u>");
        stringBuilder.append(text);
        stringBuilder.append("</u>");

        mGoogleMapsAddressTextView.setText(Html.fromHtml(stringBuilder.toString()));
    }

//    private void showSecondaryRoadDialog() {
//        final ReportRepairedDialog secondaryRoadDialog = new ReportRepairedDialog();
//        secondaryRoadDialog.setType(ReportRepairedDialog.ReportType.SECONDARY_ROAD);
//        secondaryRoadDialog.setCommentsDialogListener(new CommentsDialogListener() {
//
//            @Override
//            public void onCancel() {
//                secondaryRoadDialog.dismiss();
//                unlockButtons();
//            }
//
//            @Override
//            public void onSend(String message) {
//                secondaryRoadDialog.dismiss();
//                sendReport(mCurrentReport);
//            }
//        });
//        secondaryRoadDialog.setCancelable(false);
//
//        FragmentTransaction fragmentTransaction = getActivity().getSupportFragmentManager().beginTransaction();
//        fragmentTransaction.add(secondaryRoadDialog, null);
//
//        fragmentTransaction.commitAllowingStateLoss();
//
////        secondaryRoadDialog.show(getActivity().getFragmentManager(), "");
//    }

    private void showReleaseReportDialog() {
        final ReportRepairedDialog releaseReportDialog = new ReportRepairedDialog();
        releaseReportDialog.setType(ReportRepairedDialog.ReportType.RELEASE);
        releaseReportDialog.setCommentsDialogListener(new CommentsDialogListener() {

            @Override
            public void onCancel() {
                releaseReportDialog.dismiss();
                unlockButtons();
            }

            @Override
            public void onSend(String message) {
                releaseReportDialog.dismiss();
                cancelReportAttention();
//                sendReport(mCurrentReport);
            }
        });

        releaseReportDialog.setCancelable(false);

//        simpleIconMessageDialog.setCancelable(false);

        FragmentTransaction fragmentTransaction = getActivity().getSupportFragmentManager().beginTransaction();
        fragmentTransaction.add(releaseReportDialog, null);

        fragmentTransaction.commitAllowingStateLoss();

//        secondaryRoadDialog.show(getActivity().getFragmentManager(), "");
    }

//    private void sendReport(final Report report) {
//
//        mProgressLayout.setVisibility(View.VISIBLE);
//        ServicesManager.sendSecondaryRoadReport(mCurrentContext, mCurrentUser, report, new NewReportCallback() {
//
//            @Override
//            public void onSuccessRegister(String resultCode, Report reportCompleteInfo) {
//
//            }
//
//            @Override
//            public void onSuccessRegisterReport(Report reportCompleteInfo) {
//
//                mProgressLayout.setVisibility(View.GONE);
//
//                unlockButtons();
//
//                showAlert(true,
//                        getResources().getString(R.string.report_attention_alert_title_0),
//                        getAlertMessage(report.getTicket(), false));
//            }
//
//            @Override
//            public void onFailRegisterReport(String message) {
//                mProgressLayout.setVisibility(View.GONE);
//
//                unlockButtons();
//
//                if (message.equals(Constants.AGU_DESCRIPTION)) {
//                    showAlert(true,
//                            getResources().getString(R.string.report_attention_alert_title_0),
//                            getAlertMessage(report.getTicket(), true));
//
//                } else if (message.equals(Constants.REPORTE_ATTENDED)) {
//                    showAlertReportAttended();
//                } else {
//                    showAlert(false,
//                            getResources().getString(R.string.error_dialog_title),
//                            message);
//                }
//            }
//
//            @Override
//            public void userBanned() {
//
//            }
//
//            @Override
//            public void onTokenDisabled() {
//                Utils.showLoginForBadToken(getActivity());
//            }
//        });
//    }

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

    private void checkCameraPermissions() {
        checkPermissionsForAction(new String[] {
                        Manifest.permission.CAMERA,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE },
                ACTION_TAKE_OR_CHOOSE_PICTURE,
                "Los permisos para tomar fotos están desactivados");
    }

    @Override
    public void doAllowedStuffWithPermissionGrantedForAction(String action) {
        switch (mReportAttentionOption) {
            case ValidReportOption:
                showValidReportActivity();
                break;
            case WrongReportOption:
                showWrongReportActivity();
                break;
            case FalseReportOption:
                showFalseReportActivity();
                break;
            case InvalidReportOption:
                showInvalidReportActivity();
                break;
            default:
                break;
        }
    }

    @Override
    public void onPermissionDenied(String message) {
        unlockButtons();
        Toast.makeText(mCurrentContext, message, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onPermissionDeniedPermanently(String message) {
        unlockButtons();
        Toast.makeText(mCurrentContext, message, Toast.LENGTH_SHORT).show();
    }

    private void showValidReportActivity() {
        if (mCurrentReport.getOrigin() == Report.ReportOrigin.O72) {
            showReportActivity(1);
        } else {
            final BachesToRepairDialog bachesToRepairDialog = new BachesToRepairDialog();
            bachesToRepairDialog.setCommentsDialogListener(new ReportsToFixDialogListener() {

                @Override
                public void onCancel() {
                    unlockButtons();
                    bachesToRepairDialog.dismiss();
                }

                @Override
                public void onAccept(int noToFix) {
                    bachesToRepairDialog.dismiss();
                    showReportActivity(noToFix);
                }
            });
            bachesToRepairDialog.setCancelable(false);
            bachesToRepairDialog.show(getActivity().getFragmentManager(), "");
        }
    }

    private void showWrongReportActivity() {
        Intent reportDetailIntent = new Intent(getActivity(), WrongReportActivity.class);

        Gson gson = new Gson();
        String jsonReport = gson.toJson(mCurrentReport);
        reportDetailIntent.putExtra(Constants.EXTRA_REPORT_STATUS_DETAIL, jsonReport);

        if (selectReportFragmentListener != null) {
            selectReportFragmentListener.onOptionClick();
        }

        PreferencesManager.getInstance().setReportAttentionInProgress(getActivity(), false);

        getActivity().startActivityForResult(reportDetailIntent, Constants.WRONG_REPORT_REQUEST_CODE);
        getActivity().overridePendingTransition(R.anim.enter_from_right, R.anim.enter_from_left);
    }

    private void showFalseReportActivity() {
        Intent reportDetailIntent = new Intent(getActivity(), FakeReportActivity.class);

        Gson gson = new Gson();
        String jsonReport = gson.toJson(mCurrentReport);
        reportDetailIntent.putExtra(Constants.EXTRA_REPORT_STATUS_DETAIL, jsonReport);

        if (selectReportFragmentListener != null) {
            selectReportFragmentListener.onOptionClick();
        }

        PreferencesManager.getInstance().setReportAttentionInProgress(getActivity(), false);

        getActivity().startActivityForResult(reportDetailIntent, Constants.FAKE_REPORT_REQUEST_CODE);
        getActivity().overridePendingTransition(R.anim.enter_from_right, R.anim.enter_from_left);
    }

    private void showInvalidReportActivity() {
        Intent reportDetailIntent = new Intent(getActivity(), InvalidReportActivity.class);

        Gson gson = new Gson();
        String jsonReport = gson.toJson(mCurrentReport);
        reportDetailIntent.putExtra(Constants.EXTRA_REPORT_STATUS_DETAIL, jsonReport);

        if (selectReportFragmentListener != null) {
            selectReportFragmentListener.onOptionClick();
        }

        PreferencesManager.getInstance().setReportAttentionInProgress(getActivity(), false);

        getActivity().startActivityForResult(reportDetailIntent, Constants.INVALID_REPORT_REQUEST_CODE);
        getActivity().overridePendingTransition(R.anim.enter_from_right, R.anim.enter_from_left);
    }

    // NUEVOS
    private void showPendingReportActivity() {
        Intent reportDetailIntent = new Intent(getActivity(), PendingReportActivity.class);

        Gson gson = new Gson();
        String jsonReport = gson.toJson(mCurrentReport);
        reportDetailIntent.putExtra(Constants.EXTRA_REPORT_STATUS_DETAIL, jsonReport);

        if (selectReportFragmentListener != null) {
            selectReportFragmentListener.onOptionClick();
        }

        PreferencesManager.getInstance().setReportAttentionInProgress(getActivity(), false);

        getActivity().startActivityForResult(reportDetailIntent, Constants.PENDING_REPORT_REQUEST_CODE);
        getActivity().overridePendingTransition(R.anim.enter_from_right, R.anim.enter_from_left);
    }

    private void showReallocateReportActivity() {
        Intent reportDetailIntent = new Intent(getActivity(), ReallocateReportActivity.class);

        Gson gson = new Gson();
        String jsonReport = gson.toJson(mCurrentReport);
        reportDetailIntent.putExtra(Constants.EXTRA_REPORT_STATUS_DETAIL, jsonReport);

        if (selectReportFragmentListener != null) {
            selectReportFragmentListener.onOptionClick();
        }

        PreferencesManager.getInstance().setReportAttentionInProgress(getActivity(), false);

        getActivity().startActivityForResult(reportDetailIntent, Constants.REALLOCATE_REPORT_REQUEST_CODE);
        getActivity().overridePendingTransition(R.anim.enter_from_right, R.anim.enter_from_left);
    }

    private void lockButtons() {
        mButtonFix.setEnabled(false);
        mButtonWrongReport.setEnabled(false);
        mButtonInvalidReport.setEnabled(false);
        mButtonFakeReport.setEnabled(false);
        mAppPendingButton.setEnabled(false);
        mAguPendingButton.setEnabled(false);
        mRealloacateButton.setEnabled(false);
        mReleaseReportButton.setEnabled(false);
    }

    private void unlockButtons() {
        mButtonFix.setEnabled(true);
        mButtonWrongReport.setEnabled(true);
        mButtonInvalidReport.setEnabled(true);
        mButtonFakeReport.setEnabled(true);
        mAppPendingButton.setEnabled(true);
        mAguPendingButton.setEnabled(true);
        mRealloacateButton.setEnabled(true);
        mReleaseReportButton.setEnabled(true);
    }

    public void cancelReportAttention() {
        if (!Utils.getInstance().isInternetAvailable(mCurrentContext))
            return;

        PreferencesManager.getInstance().setReportCanceled("TroopMainFragment - cancelReportAttention", mCurrentContext, true);

        mProgressLayout.setVisibility(View.VISIBLE);

//        Report currentReport = new Report();
//        currentReport.setTicket(PreferencesManager.getInstance().getReportAttentionTicketValue(mCurrentContext));

        /*new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                mProgressLayout.setVisibility(View.GONE);
                showAlert();
            }
        }, 3000);*/

        ServicesManager.cancelReportAttention(mCurrentContext, mCurrentUser, mCurrentReport, new NewReportCallback() {

            @Override
            public void onSuccessRegister(String resultCode, Report reportCompleteInfo) {

            }

            @Override
            public void onSuccessRegisterReport(Report reportCompleteInfo) {
                Log.i("ReportStatus", "Reporte restaurado correctamente");
                mProgressLayout.setVisibility(View.GONE);
//                loadReports();
                PreferencesManager.getInstance().clearReportAttentionData(mCurrentContext);
                unlockButtons();

                showAlert("Alerta", "Se ha liberado el reporte correctamente");
            }

            @Override
            public void onFailRegisterReport(String message) {
                Log.i("ReportStatus", "Error al resturar el Reporte");
                mProgressLayout.setVisibility(View.GONE);
                showAlert("Error", "No se liberó el reporte correctamente, por favor intenta de nuevo.");
                unlockButtons();
//                loadReports();
            }

            @Override
            public void userBanned() {

            }

            @Override
            public void onTokenDisabled() {

            }
        });
    }

    public void getAllReports() {

        if (!Utils.getInstance().isInternetAvailable(getActivity())) {
            return;
        }

        mProgressLayout.setVisibility(View.VISIBLE);

        ServicesManager.getReportsForTroops(mCurrentUser, new ReportsCallback() {
            @Override
            public void onReportsCallback(List<Report> reports) {
                mProgressLayout.setVisibility(View.GONE);
                getReportDetail(reports);
            }

            @Override
            public void onReportsFail(String message) {
                mProgressLayout.setVisibility(View.GONE);
                if (getActivity() != null) {
                    if (message != "") {
                        Toast.makeText(getActivity(), message, Toast.LENGTH_SHORT).show();
                    }
                }
            }

            @Override
            public void userBanned() {
                Utils.showLogin(mCurrentContext);
            }

            @Override
            public void onTokenDisabled() {
                Utils.showLoginForBadToken(mCurrentContext);
            }
        });
    }

    public void getReportDetail(List<Report> allReports) {

        Report reportFromList = null;

        for (int i = 0; i < allReports.size(); i++) {

            reportFromList = allReports.get(i);

            if (mCurrentReport.getTicket().trim().equals(reportFromList.getTicket())) {
                break;
            }
        }

        if (reportFromList != null) {
            showButtonForStatus(reportFromList);
        }


        /*if (report == null) {
            return;
        }

        mProgressLayout.setVisibility(View.VISIBLE);

        ServicesManager.getSingleReport(mCurrentUser, report, new SingleReportCallback() {
            @Override
            public void reportSuccess(Report report) {
                mProgressLayout.setVisibility(View.GONE);
                showButtonForStatus(report);
            }

            @Override
            public void reportFail(String message) {
                mProgressLayout.setVisibility(View.GONE);
                if (getActivity() != null) {
                    if (message != "") {
                        Toast.makeText(getActivity(), message, Toast.LENGTH_SHORT).show();
                    }
                }
            }

            @Override
            public void userBanned() {
                Utils.showLogin(getActivity());
            }

            @Override
            public void onTokenDisabled() {
                Utils.showLoginForBadToken(getActivity());
            }
        });*/
    }

    private void showButtonForStatus(Report report) {
        if (report.getEtapaId() == Constants.REPORT_STATUS_ASIGNED) {
            mReleaseReportButton.setVisibility(View.VISIBLE);
        }
    }

    private void showAlert(String title, String message) {
        new AlertDialog.Builder(mCurrentContext)
                .setTitle(title)
                .setMessage(message)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        getActivity().finish();
                    }
                })
                .setCancelable(false)
                .show();
    }

    public interface SelectReportFragmentListener {
        void onOptionClick();
    }
}
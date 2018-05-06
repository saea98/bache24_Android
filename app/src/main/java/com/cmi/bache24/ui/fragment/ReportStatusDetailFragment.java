package com.cmi.bache24.ui.fragment;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

//import com.apptentive.android.sdk.Apptentive;
import com.cmi.bache24.R;
import com.cmi.bache24.data.model.PushRecord;
import com.cmi.bache24.data.model.Report;
import com.cmi.bache24.data.model.User;
import com.cmi.bache24.data.remote.ServicesManager;
import com.cmi.bache24.data.remote.interfaces.SingleReportCallback;
import com.cmi.bache24.ui.activity.ImageViewerActivity;
import com.cmi.bache24.ui.adapter.PushHistoryAdapter;
import com.cmi.bache24.util.Constants;
import com.cmi.bache24.util.PreferencesManager;
import com.cmi.bache24.util.Utils;
import com.squareup.picasso.Picasso;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * A placeholder fragment containing a simple view.
 */
public class ReportStatusDetailFragment extends BaseFragment implements View.OnClickListener {

    private Report mCurrentReport;
    private TextView mAddressLabel;
    private TextView mTextAddress;
    private TextView mCommentsLabel;
    private TextView mTextComments;
    private ImageView mImagePicture1;
    private ImageView mImagePicture2;
    private ImageView mImagePicture3;
    private ImageView mImagePicture4;
    private ImageView mImagePictureStatus;
    private TextView mTextStatus;
    private TextView mTextStatusDate;
    //private Button mButtonLike;
    private Button mButtonShare;
    //private Button mApptentiveButton;
    private LinearLayout mLayoutSolvedInfo;
    private LinearLayout mLayoutShare;
    private View mSeparator2;
    private TextView mTextViewMessage;
    private boolean callableMessage = false;
    private LinearLayout mProgressLayout;
    private ScrollView mScrollview;
    private SimpleDateFormat mDateFormat;
    private SimpleDateFormat mDateFormatToShow;
    private User mCurrentUser;
    private List<ImageView> mUserPictures;
    private Button mCallButton;
    private ReportStatusDetailFragmentListener mReportStatusDetailFragmentListener;

    private LinearLayout mLayoutPushHistory;
    private RecyclerView mPushHistoryRecyclerView;
    private PushHistoryAdapter mPushHistoryAdapter;
    private LinearLayout mLayoutPushes;
    private String mReportAttendPictureUrl;
    private LinearLayout mLayoutContact;

    public ReportStatusDetailFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_report_status_detail, container, false);

        mAddressLabel = (TextView) rootView.findViewById(R.id.textView20);
        mTextAddress = (TextView) rootView.findViewById(R.id.textview_address);
        mCommentsLabel = (TextView) rootView.findViewById(R.id.textView22);
        mTextComments = (TextView) rootView.findViewById(R.id.textview_comments);
        mImagePicture1 = (ImageView) rootView.findViewById(R.id.imageview_picture1);
        mImagePicture2 = (ImageView) rootView.findViewById(R.id.imageview_picture2);
        mImagePicture3 = (ImageView) rootView.findViewById(R.id.imageview_picture3);
        mImagePicture4 = (ImageView) rootView.findViewById(R.id.imageview_picture4);
        mImagePictureStatus = (ImageView) rootView.findViewById(R.id.imageview_status_picture);
        mTextStatus = (TextView) rootView.findViewById(R.id.textview_status_label);
        mTextStatusDate = (TextView) rootView.findViewById(R.id.textview_datetime);
        //mButtonLike = (Button) rootView.findViewById(R.id.button_like);
        mButtonShare = (Button) rootView.findViewById(R.id.button_share);
        mLayoutSolvedInfo = (LinearLayout) rootView.findViewById(R.id.layout_solved_info);
        mSeparator2 = rootView.findViewById(R.id.separator2);
        mLayoutShare = (LinearLayout) rootView.findViewById(R.id.layout_solved_share);
        mTextViewMessage = (TextView) rootView.findViewById(R.id.textView21);

        mProgressLayout = (LinearLayout) rootView.findViewById(R.id.progress_layout);
        mProgressLayout.setVisibility(View.GONE);

        mScrollview = (ScrollView) rootView.findViewById(R.id.scrollview_info_container);
        mScrollview.setVisibility(View.GONE);

        mCallButton = (Button) rootView.findViewById(R.id.button_call);
        mCallButton.setVisibility(View.GONE);

        //mApptentiveButton = (Button) rootView.findViewById(R.id.button_apptentive);

        mLayoutPushHistory = (LinearLayout) rootView.findViewById(R.id.layout_push_history);
        mLayoutPushHistory.setVisibility(View.GONE);

        mPushHistoryRecyclerView = (RecyclerView) rootView.findViewById(R.id.push_history_list);

        mLayoutPushes = (LinearLayout) rootView.findViewById(R.id.layout_pushes);
        mLayoutContact = (LinearLayout) rootView.findViewById(R.id.layout_contact);
        mLayoutContact.setVisibility(View.GONE);

        //mButtonLike.setOnClickListener(this);
        mButtonShare.setOnClickListener(this);
        mTextViewMessage.setOnClickListener(this);
        mCallButton.setOnClickListener(this);
        mImagePictureStatus.setOnClickListener(this);
        mImagePicture1.setOnClickListener(this);
        mImagePicture2.setOnClickListener(this);
        mImagePicture3.setOnClickListener(this);
        mImagePicture4.setOnClickListener(this);
        //mApptentiveButton.setOnClickListener(this);

        mCurrentUser = PreferencesManager.getInstance().getUserInfo(mCurrentContext);

        setImagesSize();

        mUserPictures = new ArrayList<>();
        mUserPictures.add(mImagePicture1);
        mUserPictures.add(mImagePicture2);
        mUserPictures.add(mImagePicture3);
        mUserPictures.add(mImagePicture4);

        return rootView;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        mCurrentContext = context;

        try{
            if (context instanceof Activity) {
                mReportStatusDetailFragmentListener = (ReportStatusDetailFragmentListener) context;
            }
        } catch (ClassCastException ex) {

        }
    }

    @Override
    public void onClick(View view) {
//        if (view.getId() == mButtonLike.getId()) {
//
//            showContactView();
//        }
        if (view.getId() == mButtonShare.getId()) {
            Utils.getInstance().share(mCurrentContext, "Bache 24", mCurrentContext.getResources().getString(R.string.share_text));
        }

        if (view.getId() == mTextViewMessage.getId()) {

        }
        if (view.getId() == mCallButton.getId()) {
            checkCallPermissions();
        }

//        if (view.getId() == mApptentiveButton.getId()) {
//            showContactView();
//        }

        if (view.getId() == mImagePictureStatus.getId()) {
            showImageViewer(mReportAttendPictureUrl);
        } else if (view.getId() == mImagePicture1.getId() ||
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
    }

    private void setImagesSize() {
        int screenWidth = Utils.getScreenWidth(mCurrentContext);

        screenWidth = screenWidth - Utils.convertDpToPx(mCurrentContext, 16 * 2) - (5 * (4 - 1));

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

        mImagePictureStatus.setLayoutParams(pictureParams2);
        mImagePictureStatus.setScaleType(ImageView.ScaleType.CENTER_CROP);
    }

    public void showReportDetail(Report report, boolean isFromNotification) {
        if (!isFromNotification) {

            mScrollview.setVisibility(View.VISIBLE);

            if (report == null)
                return;

            showReportInformation(report);
        } else {
            getReportDetail(report);
        }
    }

    public void getReportDetail(Report reportId) {

        if (!Utils.getInstance().isInternetAvailable(mCurrentContext))
            return;

        mProgressLayout.setVisibility(View.VISIBLE);

        User currentUser = PreferencesManager.getInstance().getUserInfo(mCurrentContext);
        ServicesManager.getSingleReport(currentUser, reportId, new SingleReportCallback() {
            @Override
            public void reportSuccess(Report report) {
                mProgressLayout.setVisibility(View.GONE);
                mScrollview.setVisibility(View.VISIBLE);
                showReportInformation(report);
            }

            @Override
            public void reportFail(String message) {
                mProgressLayout.setVisibility(View.GONE);
                if (mCurrentContext != null) {
                    if (message != "") {
                        Toast.makeText(mCurrentContext, message, Toast.LENGTH_SHORT).show();
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

    private void showReportInformation(Report report) {

        mCurrentReport = report;

        mAddressLabel.setVisibility(report.getAddress().trim().length() > 0 ? View.VISIBLE : View.GONE);
        this.mTextAddress.setText(report.getAddress());

        mCommentsLabel.setVisibility(report.getDescription().trim().length() > 0 ? View.VISIBLE : View.GONE);
        this.mTextComments.setText(report.getDescription());

        mDateFormat = new SimpleDateFormat(Constants.DATE_FORMAT);
        mDateFormatToShow = new SimpleDateFormat(Constants.DATE_FORMAT_TO_SHOW_STATUS);

        Date date = null;
        try {
            date = mDateFormat.parse(report.getDate());
        } catch (ParseException ex) {
            date = new Date();
        }

        String dateString = mDateFormatToShow.format(date);

        this.mTextStatusDate.setText(dateString + " hrs.");

        int citizePictureIndex = 0;
        int lastPictureIndex = -1;

        try {
            if (report.getPictures() != null) {
                if (report.getPictures().size() > 0) {
                    for (int i = 0; i < report.getPictures().size(); i++) {

                        if (i == mUserPictures.size())
                            break;

                        if (report.getPictures().get(i).getEtapaId() == Constants.REPORT_STATUS_NEW ||
                                report.getPictures().get(i).getEtapaId() == Constants.REPORT_STATUS_ASIGNED) {

                            Picasso.with(mCurrentContext)
                                    .load(report.getPictures().get(citizePictureIndex).getFoto())
                                    .error(R.drawable.no_foto)
                                    .placeholder(R.drawable.no_foto)
                                    .into(mUserPictures.get(citizePictureIndex));

                            mUserPictures.get(citizePictureIndex).setTag(citizePictureIndex);

                            mUserPictures.get(citizePictureIndex).setVisibility(View.VISIBLE);

                            citizePictureIndex++;
                        } else {
                            lastPictureIndex = i;
                        }
                    }
                }
            }

            if (lastPictureIndex > -1) {
                mReportAttendPictureUrl = report.getPictures().get(lastPictureIndex).getFoto();

                Picasso.with(mCurrentContext)
                        .load(mReportAttendPictureUrl)
                        .error(R.drawable.no_foto)
                        .placeholder(R.drawable.no_foto)
                        .into(mImagePictureStatus);
            }
        } catch (Exception ex) {

        }

        switch (mCurrentReport.getEtapaId()) {
            case Constants.REPORT_STATUS_NEW: // 1 - En espera de solución
                showViewsForStatus1();
                break;
            case Constants.REPORT_STATUS_ASIGNED: // 2 - En revisión
                showViewsForStatus2();
                break;
            case Constants.REPORT_STATUS_SOLVED: // 3 - Atendido
                showViewsForStatus3();
                break;
            case Constants.TROOP_REPORT_STATUS_DOES_NOT_APPLY: // 4 - No aplica
                showViewsForStatus4();
                break;
            case Constants.TROOP_REPORT_STATUS_FALSE: // 5 - Bache no encontrado
                showViewsForStatus5();
                break;
            case Constants.TROOP_REPORT_STATUS_WRONG: // 6 - No es un bache
                showViewsForStatus6();
                break;
            case Constants.REPORT_STATUS_7: // 7 - Pendiente
                showViewsForStatus7();
                break;
            case Constants.REPORT_STATUS_8: // 8 - Reasignado
//                showViewsForStatus8();
                showViewsForStatus4();
                break;
            default:
                showViewsForStatus4(); // 4 - No aplica
                break;
        }

        showPushHistory(mCurrentReport.getPushHistory());
    }

    private void showViewsForStatus1() { //    1 - En espera de solución

        mLayoutSolvedInfo.setVisibility(View.GONE);
        mSeparator2.setVisibility(View.GONE);

        mLayoutShare.setVisibility(View.GONE);

        mTextViewMessage.setText(mCurrentContext.getResources().getString(R.string.report_detail_message_status_1));

        mLayoutContact.setVisibility(View.GONE);

        callableMessage = false;

        if (mReportStatusDetailFragmentListener != null) {
            mReportStatusDetailFragmentListener.onReportTitleChanged(PreferencesManager.getInstance().getStatusNameForId(mCurrentContext, mCurrentReport.getEtapaId()));
        }
    }

    private void showViewsForStatus2() { //    2 - En revisión
        mLayoutSolvedInfo.setVisibility(View.GONE);
        mSeparator2.setVisibility(View.GONE);

        mLayoutShare.setVisibility(View.GONE);

        mTextViewMessage.setText(mCurrentContext.getResources().getString(R.string.report_detail_message_status_2));

        mLayoutContact.setVisibility(View.GONE);

        callableMessage = false;

        if (mReportStatusDetailFragmentListener != null) {
            mReportStatusDetailFragmentListener.onReportTitleChanged(PreferencesManager.getInstance().getStatusNameForId(mCurrentContext, mCurrentReport.getEtapaId()));
        }
    }

    private void showViewsForStatus3() { //    3 - Atendido

        if (mReportAttendPictureUrl == "" || mReportAttendPictureUrl == null || mReportAttendPictureUrl.equals("")) {
            mLayoutSolvedInfo.setVisibility(View.GONE);
            mSeparator2.setVisibility(View.GONE);
        } else {
            mLayoutSolvedInfo.setVisibility(View.VISIBLE);
            mSeparator2.setVisibility(View.VISIBLE);
        }

        mLayoutShare.setVisibility(View.VISIBLE);
        mCallButton.setVisibility(View.GONE);
        //mApptentiveButton.setVisibility(View.GONE);

        mTextViewMessage.setText(mCurrentContext.getResources().getString(R.string.report_detail_message_status_3));

        callableMessage = false;

        if (mReportStatusDetailFragmentListener != null) {
            mReportStatusDetailFragmentListener.onReportTitleChanged(PreferencesManager.getInstance().getStatusNameForId(mCurrentContext, mCurrentReport.getEtapaId()));
        }
    }

    private void showViewsForStatus4() { //    4 - No aplica

        mLayoutSolvedInfo.setVisibility(View.GONE);
        mSeparator2.setVisibility(View.GONE);

        mLayoutShare.setVisibility(View.GONE);

        StringBuilder stringBuilder = new StringBuilder();

        stringBuilder.append("<font color='#484848'>" + getResources().getString(R.string.report_dialog_invalid_moreinfo_part_1) + "</font>");
        stringBuilder.append(" ");

        stringBuilder.append("<font color='#ec2697'><b>" + Constants.KEY_DELEGATIONS.get(mCurrentReport.getDelegationId()) + "</b></font>");

        stringBuilder.append(" ");
        stringBuilder.append("<font color='#484848'>" + getResources().getString(R.string.report_dialog_invalid_moreinfo_part_2) + "</font>");
        stringBuilder.append(" ");
        stringBuilder.append("<font color='#ec2697'><b>" + mCurrentReport.getSalesForceTicket() + "</b></font>");

        stringBuilder.append(". ");
        stringBuilder.append("<font color='#484848'>" + getResources().getString(R.string.report_dialog_message1) + "</font>");

        mTextViewMessage.setText(Html.fromHtml(stringBuilder.toString()));

        mCallButton.setVisibility(View.VISIBLE);
        mLayoutContact.setVisibility(View.VISIBLE);

        callableMessage = false;

        if (mReportStatusDetailFragmentListener != null) {
            mReportStatusDetailFragmentListener.onReportTitleChanged(PreferencesManager.getInstance().getStatusNameForId(mCurrentContext, mCurrentReport.getEtapaId()));
        }
    }

    private void showViewsForStatus5() { //    5 - Bache no encontrado

        mLayoutSolvedInfo.setVisibility(View.GONE);
        mSeparator2.setVisibility(View.GONE);

        mLayoutShare.setVisibility(View.GONE);

        mTextViewMessage.setText(mCurrentContext.getResources().getString(R.string.report_detail_message_status_5));

        mTextViewMessage.setGravity(Gravity.LEFT);

        mLayoutContact.setVisibility(View.GONE);

        callableMessage = false;

        if (mReportStatusDetailFragmentListener != null) {
            mReportStatusDetailFragmentListener.onReportTitleChanged(PreferencesManager.getInstance().getStatusNameForId(mCurrentContext, mCurrentReport.getEtapaId()));
        }
    }

    private void showViewsForStatus6() { //    6 - No es un bache
        if (mReportAttendPictureUrl == "" || mReportAttendPictureUrl == null || mReportAttendPictureUrl.equals("")) {
            mLayoutSolvedInfo.setVisibility(View.GONE);
            mSeparator2.setVisibility(View.GONE);
        } else {
            mLayoutSolvedInfo.setVisibility(View.VISIBLE);
            mSeparator2.setVisibility(View.VISIBLE);
        }

        mLayoutShare.setVisibility(View.GONE);

        StringBuilder stringBuilder = new StringBuilder();

        stringBuilder.append("<font color='#484848'>" + getResources().getString(R.string.report_detail_status_wrong_part_1) + "</font>");
        stringBuilder.append(" ");
        stringBuilder.append("<font color='#ec2697'><b>" + mCurrentReport.getCause() + "</b></font>");
        stringBuilder.append(" ");
        stringBuilder.append("<font color='#484848'>" + getResources().getString(R.string.report_dialog_invalid_moreinfo_part_2) + "</font>");
        stringBuilder.append(" ");
        stringBuilder.append("<font color='#ec2697'><b>" + mCurrentReport.getSalesForceTicket() + "</b></font>");
        stringBuilder.append(". ");
        stringBuilder.append("<font color='#484848'>" + getResources().getString(R.string.report_dialog_message1) + "</font>");

        mTextViewMessage.setText(Html.fromHtml(stringBuilder.toString()));

        mCallButton.setVisibility(View.VISIBLE);
        mLayoutContact.setVisibility(View.VISIBLE);

        callableMessage = false;

        if (mReportStatusDetailFragmentListener != null) {
            mReportStatusDetailFragmentListener.onReportTitleChanged(PreferencesManager.getInstance().getStatusNameForId(mCurrentContext, mCurrentReport.getEtapaId()));
        }
    }

    private void showViewsForStatus7() { //    7 - Pendiente
        mLayoutSolvedInfo.setVisibility(View.GONE);
        mSeparator2.setVisibility(View.GONE);

        mLayoutShare.setVisibility(View.GONE);

        StringBuilder stringBuilder = new StringBuilder();

        stringBuilder.append("<font color='#484848'>" + getResources().getString(R.string.report_detail_message_status_7_1) + "</font>");
        stringBuilder.append(" ");
        stringBuilder.append("<font color='#ec2697'><b>" + mCurrentReport.getCause() + "</b></font>");
        stringBuilder.append(" ");
        stringBuilder.append("<font color='#484848'>" + getResources().getString(R.string.report_detail_message_status_7_2) + "</font>");

        mTextViewMessage.setText(Html.fromHtml(stringBuilder.toString()));

        mLayoutContact.setVisibility(View.GONE);

        callableMessage = false;

        if (mReportStatusDetailFragmentListener != null) {
            mReportStatusDetailFragmentListener.onReportTitleChanged(PreferencesManager.getInstance().getStatusNameForId(mCurrentContext, mCurrentReport.getEtapaId()));
        }
    }

    private void showViewsForStatus8() { //    8 - Reasignado

    }

    private void showPushHistory(List<PushRecord> history) {

        if (history == null) {
            return;
        }

        if (history.size() == 0)
            return;

        mLayoutPushHistory.setVisibility(View.VISIBLE);
        mPushHistoryRecyclerView.setVisibility(View.GONE);

        for (PushRecord record : history) {
            View pushView = LayoutInflater.from(mCurrentContext).inflate(R.layout.push_record_row, null);
            TextView mTextPushMessage = (TextView) pushView.findViewById(R.id.textview_message);
            mTextPushMessage.setText(record.getMessage());
            mLayoutPushes.addView(pushView);
        }
    }

    private void showImageViewer(String url) {
        Intent reportActivityIntent = new Intent(mCurrentContext, ImageViewerActivity.class);
        reportActivityIntent.putExtra(Constants.EXTRA_IMAGE_URL, url);

        startActivity(reportActivityIntent);
    }

    private void checkCallPermissions() {
        checkPermissionsForAction(new String[] { Manifest.permission.CALL_PHONE },
                ACTION_PHONE_CALL,
                "Los permisos para hacer llamadas están desactivados");
    }

    private void makeCall() {
        Intent callIntent = new Intent(Intent.ACTION_CALL);
        callIntent.setData(Uri.parse("tel:" + Constants.REPORT_CALL));
        startActivity(callIntent);
    }

    @Override
    public void doAllowedStuffWithPermissionGrantedForAction(String action) {
        makeCall();
    }

    @Override
    public void onPermissionDenied(String message) {
        Toast.makeText(mCurrentContext, message, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onPermissionDeniedPermanently(String message) {
        Toast.makeText(mCurrentContext, message, Toast.LENGTH_SHORT).show();
    }

    private void showContactView() {

        Map<String, Object> customData = new HashMap<String, Object>();
        customData.put("Sección", "Detalle de reporte");
        customData.put("Email", mCurrentUser != null ? mCurrentUser.getEmail() : "");
        customData.put("Estado", PreferencesManager.getInstance().getStatusNameForId(mCurrentContext, mCurrentReport.getEtapaId()));
        customData.put("Reporte", mCurrentReport.getTicket());

      //  Apptentive.showMessageCenter(mCurrentContext, customData);
    }

    public interface ReportStatusDetailFragmentListener {
        void onReportTitleChanged(String title);
    }
}

package com.cmi.bache24.ui.fragment;

import android.Manifest;
import android.animation.ObjectAnimator;
import android.animation.TypeEvaluator;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.util.Property;
import android.view.InflateException;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.cmi.bache24.R;
import com.cmi.bache24.data.model.Report;
import com.cmi.bache24.data.model.User;
import com.cmi.bache24.data.remote.ServicesManager;
import com.cmi.bache24.data.remote.interfaces.NewReportCallback;
import com.cmi.bache24.ui.dialog.InvalidReportMessageDialog;
import com.cmi.bache24.ui.dialog.ReportMessageDialog;
import com.cmi.bache24.ui.dialog.SimpleIconMessageDialog;
import com.cmi.bache24.ui.dialog.SimpleMessageDialog;
import com.cmi.bache24.ui.dialog.interfaces.InvalidReportDialogListener;
import com.cmi.bache24.ui.dialog.interfaces.MessageDialogListener;
import com.cmi.bache24.util.Constants;
import com.cmi.bache24.util.ImagePicker;
import com.cmi.bache24.util.LatLngInterpolator;
import com.cmi.bache24.util.PreferencesManager;
import com.cmi.bache24.util.Utils;
//import com.google.android.gms.location.places.Place;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.File;
import java.util.List;
import java.util.Locale;

/**
 * A placeholder fragment containing a simple view.
 */
public class ReportDetailFragment extends BaseFragment implements OnMapReadyCallback, View.OnClickListener {

//    private EditText mEditAddress;
    private TextView mAddressTextView;
    private EditText mEditComments;
    private ImageView mImagePicture1;
    private ImageView mImagePicture2;
    private ImageView mImagePicture3;
    private ImageView mImagePicture4;
    private Button mButtonSendReport;
    private Button mButtonCancelReport;
    private ReportDetailFragmentListener mReportDetailFragmentListener;

    private boolean mPicture1Assigned = false;
    private boolean mPicture2Assigned = false;
    private boolean mPicture3Assigned = false;
    private boolean mPicture4Assigned = false;
    private int mPictureToAdd = 0;

    private GoogleMap mMap;
    private LatLng newBachePosition;
    private Marker mNewReportMarker;
    private String mAddressString;

    private Address mGeoCoderAddress;
    private LinearLayout mProgressLayout;
    private LinearLayout mRootLayout;
//    private Place mPlace;
    private static final int PICK_IMAGE_ID = 234;
    private String mOriginalAddressString;

    private static View rootView;
    private User mCurrentUser;
    private InvalidReportMessageDialog invalidReportMessageDialog;
    private int temporaryReportPosition = -1;
    private boolean isTemporaryReport = false;

    public ReportDetailFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        if (rootView != null) {
            ViewGroup parent = (ViewGroup) rootView.getParent();
            if (parent != null)
                parent.removeView(rootView);
        }
        try {
            rootView = inflater.inflate(R.layout.fragment_report_detail, container, false);
        } catch (InflateException e) {

        }

        mAddressTextView = (TextView) rootView.findViewById(R.id.textview_address);
        mEditComments = (EditText) rootView.findViewById(R.id.edittext_comments);
        mImagePicture1 = (ImageView) rootView.findViewById(R.id.imageview_picture1);
        mImagePicture2 = (ImageView) rootView.findViewById(R.id.imageview_picture2);
        mImagePicture3 = (ImageView) rootView.findViewById(R.id.imageview_picture3);
        mImagePicture4 = (ImageView) rootView.findViewById(R.id.imageview_picture4);
        mButtonSendReport = (Button) rootView.findViewById(R.id.button_send_report);
        mButtonCancelReport = (Button) rootView.findViewById(R.id.button_cancel_report);
        mProgressLayout = (LinearLayout) rootView.findViewById(R.id.progress_layout);
        mRootLayout = (LinearLayout) rootView.findViewById(R.id.root_view);

        mProgressLayout.setVisibility(View.GONE);

        mCurrentUser = PreferencesManager.getInstance().getUserInfo(getActivity());

        MapFragment mapFragment = (MapFragment) getActivity().getFragmentManager()
                .findFragmentById(R.id.map_report_detail);

        if (mapFragment != null)
            mapFragment.getMapAsync(this);

        setImagesSize();

        mImagePicture1.setOnClickListener(this);
        mImagePicture2.setOnClickListener(this);
        mImagePicture3.setOnClickListener(this);
        mImagePicture4.setOnClickListener(this);
        mButtonSendReport.setOnClickListener(this);
        mButtonCancelReport.setOnClickListener(this);

        if (savedInstanceState != null) {
            newBachePosition = new LatLng(savedInstanceState.getDouble("_REP_DET_LATITUDE"),
                    savedInstanceState.getDouble("_REP_DET_LONGITUDE"));

            mEditComments.setText(savedInstanceState.getString("_REP_DET_COMMENTS"));

            mPicture1Assigned = savedInstanceState.getBoolean("_REP_DET_PICTURE_1_SAVED");
            mPicture2Assigned = savedInstanceState.getBoolean("_REP_DET_PICTURE_2_SAVED");
            mPicture3Assigned = savedInstanceState.getBoolean("_REP_DET_PICTURE_3_SAVED");
            mPicture4Assigned = savedInstanceState.getBoolean("_REP_DET_PICTURE_4_SAVED");
        }

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

        try {
            if (context instanceof Activity) {
                mReportDetailFragmentListener = (ReportDetailFragmentListener) context;
            }
        } catch (ClassCastException ex) {

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
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == mButtonSendReport.getId()) {
            Utils.hideKeyboard(mCurrentContext, mEditComments);
            registerReport();
        }

        if (view.getId() == mButtonCancelReport.getId()) {
            clearViews();

            if (isTemporaryReport) {
                PreferencesManager.getInstance().setEditAddress(mCurrentContext, true);
            }

            if (mReportDetailFragmentListener != null)
                mReportDetailFragmentListener.onNewReportCanceled();
        }

        if (view.getId() == mImagePicture1.getId()) {
            mPictureToAdd = 0;

            checkCameraPermissions();
        }

        if (view.getId() == mImagePicture2.getId()) {
            mPictureToAdd = 1;

            checkCameraPermissions();
        }

        if (view.getId() == mImagePicture3.getId()) {
            mPictureToAdd = 2;

            checkCameraPermissions();
        }

        if (view.getId() == mImagePicture4.getId()) {
            mPictureToAdd = 3;

            checkCameraPermissions();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        String filename = "";
        Bitmap bitmap;

        if (requestCode == PICK_IMAGE_ID && resultCode == getActivity().RESULT_OK) {
            switch (mPictureToAdd) {
                case 0:
                    filename = "bache_picture_1.jpg";

                    bitmap = ImagePicker.getImageFromResult(getActivity(), resultCode, data, "", filename);
                    mImagePicture1.setImageBitmap(bitmap);
                    mImagePicture1.setScaleType(ImageView.ScaleType.CENTER_CROP);
                    mPicture1Assigned = true;
                    break;
                case 1:
                    filename = "bache_picture_2.jpg";

                    bitmap = ImagePicker.getImageFromResult(getActivity(), resultCode, data, "", filename);
                    mImagePicture2.setImageBitmap(bitmap);
                    mImagePicture2.setScaleType(ImageView.ScaleType.CENTER_CROP);
                    mPicture2Assigned = true;
                    break;
                case 2:
                    filename = "bache_picture_3.jpg";

                    bitmap = ImagePicker.getImageFromResult(getActivity(), resultCode, data, "", filename);
                    mImagePicture3.setImageBitmap(bitmap);
                    mImagePicture3.setScaleType(ImageView.ScaleType.CENTER_CROP);

                    mPicture3Assigned = true;
                    break;
                case 3:
                    filename = "bache_picture_4.jpg";

                    bitmap = ImagePicker.getImageFromResult(getActivity(), resultCode, data, "", filename);
                    mImagePicture4.setImageBitmap(bitmap);
                    mImagePicture4.setScaleType(ImageView.ScaleType.CENTER_CROP);
                    mPicture4Assigned = true;
                    break;
                default:
                    filename = "bache_picture_1.jpg";

                    bitmap = ImagePicker.getImageFromResult(getActivity(), resultCode, data, "", filename);
                    mImagePicture1.setImageBitmap(bitmap);
                    mImagePicture1.setScaleType(ImageView.ScaleType.CENTER_CROP);
                    mPicture1Assigned = true;
                    break;
            }
        }
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putDouble("_REP_DET_LATITUDE", newBachePosition.latitude);
        outState.putDouble("_REP_DET_LONGITUDE", newBachePosition.longitude);
        outState.putString("_REP_DET_ADDRESS_STRING", mAddressTextView.getText().toString());
        outState.putString("_REP_DET_COMMENTS", mEditComments.getText().toString());
        outState.putBoolean("_REP_DET_PICTURE_1_SAVED", mPicture1Assigned);
        outState.putBoolean("_REP_DET_PICTURE_2_SAVED", mPicture2Assigned);
        outState.putBoolean("_REP_DET_PICTURE_3_SAVED", mPicture3Assigned);
        outState.putBoolean("_REP_DET_PICTURE_4_SAVED", mPicture4Assigned);
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    public void setReportLocation(double latitude,
                                  double logitude,
                                  String addressAsString,
                                  boolean isTemporary,
                                  int positionInList) {
        newBachePosition = new LatLng(latitude, logitude);
        temporaryReportPosition = positionInList;
        isTemporaryReport = isTemporary;

        final User currentUser = PreferencesManager.getInstance().getUserInfo(getActivity());

        if (addressAsString != null) {
            this.mAddressTextView.setText(addressAsString);
            this.mEditComments.requestFocus();
        }

        if (currentUser.getUserType() == Constants.USER_TYPE_TROOP /*|| isTemporary*/) {
            getAddressForPosition(new LatLng(latitude, logitude));
        }

        mOriginalAddressString = addressAsString;

        if (mMap != null) {
            CameraPosition newCameraPosition = new CameraPosition(newBachePosition,
                    17,
                    mMap.getCameraPosition().tilt,
                    mMap.getCameraPosition().bearing);

            mMap.moveCamera(CameraUpdateFactory.newCameraPosition(newCameraPosition));

            if (mNewReportMarker != null)
                mNewReportMarker.remove();

            mNewReportMarker =
                    mMap.addMarker(new MarkerOptions()
                            .position(new LatLng(newBachePosition.latitude, newBachePosition.longitude))
                            .icon(BitmapDescriptorFactory.fromResource(R.drawable.pin_reporte_amarillo)));
        }
    }

    private void clearViews() {
        mAddressTextView.setText("");
        mEditComments.setText("");
        mImagePicture1.setImageResource(R.drawable.camara_);
        mImagePicture2.setImageResource(R.drawable.camara_);
        mImagePicture3.setImageResource(R.drawable.camara_);
        mImagePicture4.setImageResource(R.drawable.camara_);

        mImagePicture1.setScaleType(ImageView.ScaleType.CENTER);
        mImagePicture2.setScaleType(ImageView.ScaleType.CENTER);
        mImagePicture3.setScaleType(ImageView.ScaleType.CENTER);
        mImagePicture4.setScaleType(ImageView.ScaleType.CENTER);

        mPicture1Assigned = false;
        mPicture2Assigned = false;
        mPicture3Assigned = false;
        mPicture4Assigned = false;
    }

    private void CreateDirectory (File file) {
        if (!file.exists()) {
            file.mkdirs();
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        Criteria locationCriteria = new Criteria();
        locationCriteria.setAccuracy(Criteria.ACCURACY_COARSE);
        locationCriteria.setPowerRequirement(Criteria.POWER_MEDIUM);

        if (newBachePosition != null) {
            CameraPosition newCameraPosition = new CameraPosition(newBachePosition,
                    18,
                    mMap.getCameraPosition().tilt,
                    mMap.getCameraPosition().bearing);

            mMap.moveCamera(CameraUpdateFactory.newCameraPosition(newCameraPosition));

            if (mNewReportMarker != null)
                mNewReportMarker.remove();

            mNewReportMarker =
                    mMap.addMarker(new MarkerOptions()
                            .position(new LatLng(newBachePosition.latitude, newBachePosition.longitude))
                            .icon(BitmapDescriptorFactory.fromResource(R.drawable.pin_reporte_amarillo)));
        }

        if (mCurrentUser.getUserType() == Constants.USER_TYPE_TROOP) {
            mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
                @Override
                public void onMapClick(LatLng latLng) {
                    if (mNewReportMarker == null) {
                        mNewReportMarker =
                                mMap.addMarker(new MarkerOptions()
                                        .position(latLng)
                                        .icon(BitmapDescriptorFactory.fromResource(R.drawable.pin_reporte_amarillo)));

                        getAddressForPosition(latLng);
                    } else {
                        animateMarkerToPosition(mNewReportMarker, latLng, new LatLngInterpolator.Linear());
                        getAddressForPosition(latLng);
                    }
                }
            });
        }
        else
        {

        }
    }

    private void getAddressForPosition(final LatLng position) {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                try {
                    newBachePosition = position;
                    Geocoder geocoder = new Geocoder(getActivity(), Locale.getDefault());
                    List<Address> addresses = geocoder.getFromLocation(position.latitude, position.longitude, 1);

                    if (addresses.size() > 0) {
                        mGeoCoderAddress = addresses.get(0);
                        mAddressString = addresses.get(0).getAddressLine(0) + ", " + addresses.get(0).getAddressLine(1) + ", " + addresses.get(0).getAddressLine(2);
                        updateAddress(true, mAddressString);
                        mEditComments.requestFocus();
                        mOriginalAddressString = mAddressString;
                    } else {
                        updateAddress(false, getResources().getString(R.string.address_not_found_short));
                        mGeoCoderAddress = null;
                    }
                } catch (Exception ex) {
                    mGeoCoderAddress = null;
                    updateAddress(false, getResources().getString(R.string.address_not_found_short));
                }
            }
        }, 500);
    }

    private void updateAddress(boolean addressFound, String textAddress) {
        if (addressFound) {
//            mAddressTextView.setHint("");
            mAddressTextView.setText(textAddress);
        } else {
//            mAddressTextView.setHint(textAddress);
            mAddressTextView.setText("");
        }
    }

    private void animateMarkerToPosition(Marker marker, final LatLng finalPosition, final LatLngInterpolator latLngInterpolator) {
        TypeEvaluator<LatLng> typeEvaluator = new TypeEvaluator<LatLng>() {
            @Override
            public LatLng evaluate(float fraction, LatLng startValue, LatLng endValue) {
                return latLngInterpolator.interpolate(fraction, startValue, endValue);
            }
        };

        Property<Marker, LatLng> property = Property.of(Marker.class, LatLng.class, "position");
        ObjectAnimator animator = ObjectAnimator.ofObject(marker, property, typeEvaluator, finalPosition);
        animator.setDuration(500);
        animator.start();
    }

    private void registerReport() {

        if (!Utils.getInstance().isInternetAvailable(getActivity()))
            return;

        if (mAddressTextView.getText().toString().trim().length() == 0) {
            Toast.makeText(getActivity(), getResources().getString(R.string.report_detail_invalid_or_incomplete),
                    Toast.LENGTH_SHORT).show();

            return;
        }

        Report newReport = new Report();
        final User currentUser = PreferencesManager.getInstance().getUserInfo(getActivity());

        mProgressLayout.setVisibility(View.VISIBLE);

        newReport.setLongitude(String.valueOf(newBachePosition.longitude));
        newReport.setLatitude(String.valueOf(newBachePosition.latitude));

        newReport.setAddress(mAddressTextView.getText().toString());
        newReport.setVialidad(mOriginalAddressString);

        newReport.setDescription(mEditComments.getText().toString().trim());
        newReport.setDelegationId(1);
        newReport.setCalle(newReport.getAddress());
        newReport.setEtapaId(1);
        newReport.setDate(Utils.getInstance().getCurrentTimestamp());

        Bitmap bitmapPicture1 = null;
        Bitmap bitmapPicture2 = null;
        Bitmap bitmapPicture3 = null;
        Bitmap bitmapPicture4 = null;

        if (mPicture1Assigned) {
            bitmapPicture1 = ((BitmapDrawable)mImagePicture1.getDrawable()).getBitmap();
        }

        if (mPicture2Assigned) {
            bitmapPicture2 = ((BitmapDrawable)mImagePicture2.getDrawable()).getBitmap();
        }

        if (mPicture3Assigned) {
            bitmapPicture3 = ((BitmapDrawable)mImagePicture3.getDrawable()).getBitmap();
        }

        if (mPicture4Assigned) {
            bitmapPicture4 = ((BitmapDrawable)mImagePicture4.getDrawable()).getBitmap();
        }

        newReport.setPicture1(bitmapPicture1 != null ? Utils.bitmapToBase64(bitmapPicture1, Constants.PICTURE_CITIZEN_QUALITY) : "");
        newReport.setPicture2(bitmapPicture2 != null ? Utils.bitmapToBase64(bitmapPicture2, Constants.PICTURE_CITIZEN_QUALITY) : "");
        newReport.setPicture3(bitmapPicture3 != null ? Utils.bitmapToBase64(bitmapPicture3, Constants.PICTURE_CITIZEN_QUALITY) : "");
        newReport.setPicture4(bitmapPicture4 != null ? Utils.bitmapToBase64(bitmapPicture4, Constants.PICTURE_CITIZEN_QUALITY) : "");

        if (currentUser == null) {
            mProgressLayout.setVisibility(View.GONE);
            Toast.makeText(getActivity(), "Se encontró un problema durante el registro, inténtelo más tarde", Toast.LENGTH_SHORT).show();
            return;
        }

        ServicesManager.registerNewReport(currentUser, newReport, new NewReportCallback() {

            @Override
            public void onSuccessRegister(String resultCode, final Report reportCompleteInfo) {
                mProgressLayout.setVisibility(View.GONE);

                if (currentUser.getUserType() == Constants.USER_TYPE_TROOP) {

                    if (resultCode.equals(Constants.NEW_REPORT_RESULT_OUT_OF_RANGE) ||
                            resultCode == Constants.NEW_REPORT_RESULT_OUT_OF_RANGE) {
                        showMessageForOutOfRangeReport();
                    } else if (resultCode.equals(Constants.NEW_REPORT_RESULT_ALREADY_REPORTED) ||
                            resultCode == Constants.NEW_REPORT_RESULT_ALREADY_REPORTED) {
                        showMessageForAlreadyReported();
                    } else {
                        showMessageForTroop(reportCompleteInfo);
                    }
                } else {

                    PreferencesManager.getInstance().deleteTemporaryReport(mCurrentContext, temporaryReportPosition);

                    if (reportCompleteInfo.getRoadType() > Constants.ROAD_TYPE_DEFAULT) {
                        if (reportCompleteInfo.getRoadType() == Constants.ROAD_TYPE_PRIMARY) {
                            showMessageForPrimary(reportCompleteInfo);
                        } else if (reportCompleteInfo.getRoadType() == Constants.ROAD_TYPE_SECONDARY) {
                            showMessageForNoPrimary(reportCompleteInfo);
                        } else if (resultCode.equals(Constants.NEW_REPORT_RESULT_OUT_OF_RANGE)
                                || resultCode == Constants.NEW_REPORT_RESULT_OUT_OF_RANGE) {
                            showMessageForOutOfRangeReport();
                        } else if (resultCode.equals(Constants.NEW_REPORT_RESULT_ALREADY_REPORTED)
                                || resultCode == Constants.NEW_REPORT_RESULT_ALREADY_REPORTED) {
                            showMessageForAlreadyReported();
                        }
                    } else {
                        switch (resultCode) {
                            case Constants.NEW_REPORT_RESULT_OUT_OF_RANGE:
                                showMessageForOutOfRangeReport();
                                break;
                            case Constants.NEW_REPORT_RESULT_ALREADY_REPORTED:
                                showMessageForAlreadyReported();
                                break;
                            case Constants.NEW_REPORT_RESULT_OK_SERVICE:
                                showMessageForPrimary(reportCompleteInfo);
                                break;
                            case Constants.NEW_REPORT_RESULT_OK_SALESFORCE:
                                showMessageForNoPrimary(reportCompleteInfo);
                                break;
                            default:
                                showMessageForNoPrimary(reportCompleteInfo);
                                break;
                        }
                    }
                }
            }

            @Override
            public void onSuccessRegisterReport(final Report reportCompleteInfo) {

            }

            @Override
            public void onFailRegisterReport(String message) {
                mProgressLayout.setVisibility(View.GONE);
                if (message != "") {
                    Toast.makeText(getActivity(), message, Toast.LENGTH_SHORT).show();
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
        });
    }

    private void showMessageForPrimary(final Report report) {
        final ReportMessageDialog reportMessageDialog = new ReportMessageDialog();
        reportMessageDialog.setMessageDialogListener(report, new MessageDialogListener() {
            @Override
            public void onAccept() {
                clearViews();

                reportMessageDialog.dismiss();
                if (mReportDetailFragmentListener != null)
                    mReportDetailFragmentListener.onNewReportFinished();
            }
        });
        reportMessageDialog.setCancelable(false);

        FragmentTransaction fragmentTransaction = ((AppCompatActivity)mCurrentContext).getSupportFragmentManager().beginTransaction();
        fragmentTransaction.add(reportMessageDialog, null);

        fragmentTransaction.commitAllowingStateLoss();

//        reportMessageDialog.show(getActivity().getFragmentManager(), "");
    }

    private void showMessageForNoPrimary(final Report report) {
        invalidReportMessageDialog = new InvalidReportMessageDialog();
        invalidReportMessageDialog.setMessageDialogListener(report, new InvalidReportDialogListener() {
            @Override
            public void onAccept() {
                clearViews();

                invalidReportMessageDialog.dismiss();
                if (mReportDetailFragmentListener != null)
                    mReportDetailFragmentListener.onNewReportFinished();
            }

            @Override
            public void onClickLink() {
                clearViews();
                checkCallPermissions();
            }
        });
        invalidReportMessageDialog.setCancelable(false);

        FragmentTransaction fragmentTransaction = ((AppCompatActivity)mCurrentContext).getSupportFragmentManager().beginTransaction();
        fragmentTransaction.add(invalidReportMessageDialog, null);

        fragmentTransaction.commitAllowingStateLoss();

//        invalidReportMessageDialog.show(getActivity().getFragmentManager(), "");
    }

    private void checkCameraPermissions() {
        checkPermissionsForAction(new String[] { Manifest.permission.CAMERA,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE },
                ACTION_TAKE_OR_CHOOSE_PICTURE,
                "Los permisos para tomar fotos están desactivados");
    }

    private void checkCallPermissions() {
        checkPermissionsForAction(new String[] { Manifest.permission.CALL_PHONE },
                ACTION_PHONE_CALL,
                "Los permisos para hacer llamadas están desactivados");
    }

    private void showMessageForTroop(final Report report) {
        final SimpleMessageDialog simpleMessageDialog = new SimpleMessageDialog();
        simpleMessageDialog.setupDialog(report, new MessageDialogListener() {
            @Override
            public void onAccept() {
                clearViews();
                simpleMessageDialog.dismiss();
                if (mReportDetailFragmentListener != null)
                    mReportDetailFragmentListener.onNewReportFinished();
            }
        });
        simpleMessageDialog.setCancelable(false);
        simpleMessageDialog.show(getActivity().getFragmentManager(), "");
    }

    private void showMessageForOutOfRangeReport() {
        final SimpleIconMessageDialog simpleIconMessageDialog = new SimpleIconMessageDialog();
        simpleIconMessageDialog.setupDialog(R.drawable.carita_fuchi,
                getResources().getString(R.string.report_out_of_range_dialog_title),
                getResources().getString(R.string.out_of_range), true,
                new MessageDialogListener() {
                    @Override
                    public void onAccept() {
                        clearViews();
                        simpleIconMessageDialog.dismiss();
                        if (mReportDetailFragmentListener != null)
                            mReportDetailFragmentListener.onNewReportFinished();
                    }
                });
        simpleIconMessageDialog.setCancelable(false);

//        FragmentTransaction fragmentTransaction = getActivity().getSupportFragmentManager().beginTransaction();
        FragmentTransaction fragmentTransaction = ((AppCompatActivity)mCurrentContext).getSupportFragmentManager().beginTransaction();
        fragmentTransaction.add(simpleIconMessageDialog, null);

        fragmentTransaction.commitAllowingStateLoss();
    }

    private void showMessageForAlreadyReported() {
        final SimpleIconMessageDialog simpleIconMessageDialog = new SimpleIconMessageDialog();
        simpleIconMessageDialog.setupDialog(R.drawable.carita_ups,
                getResources().getString(R.string.report_dialog_title),
                getResources().getString(R.string.already_reported), true,
                new MessageDialogListener() {
                    @Override
                    public void onAccept() {
                        clearViews();
                        simpleIconMessageDialog.dismiss();
                        if (mReportDetailFragmentListener != null)
                            mReportDetailFragmentListener.onNewReportFinished();
                    }
                });
        simpleIconMessageDialog.setCancelable(false);

//        FragmentTransaction fragmentTransaction = getActivity().getSupportFragmentManager().beginTransaction();
        FragmentTransaction fragmentTransaction = ((AppCompatActivity)mCurrentContext).getSupportFragmentManager().beginTransaction();
        fragmentTransaction.add(simpleIconMessageDialog, null);

        fragmentTransaction.commitAllowingStateLoss();
    }

    /*public void setPlace(Place place) {
        mPlace = place;

        if (place != null) {
            mAddressTextView.setText(place.getAddress());
            updateMarker(place.getLatLng());
            newBachePosition = place.getLatLng();
        } else {
            Toast.makeText(getActivity(), getResources().getString(R.string.address_not_found), Toast.LENGTH_SHORT).show();
        }
    }*/

    private void updateMarker(LatLng latLng) {
        if (mNewReportMarker == null) {
            mNewReportMarker =
                    mMap.addMarker(new MarkerOptions()
                            .position(latLng)
                            .icon(BitmapDescriptorFactory.fromResource(R.drawable.pin_reporte_amarillo)));

            moveToMyCurrentLocation(latLng);
        } else {
            animateMarkerToPosition(mNewReportMarker, latLng, new LatLngInterpolator.Linear());
            moveToMyCurrentLocation(latLng);
        }
    }

    private void moveToMyCurrentLocation(LatLng position) {
        if (position != null && mMap != null) {
            CameraUpdate location = CameraUpdateFactory.newLatLngZoom(
                    position, mMap.getCameraPosition().zoom);
            mMap.animateCamera(location);
        }
    }

    private void showImagePicker(int position) {
        Intent chooseImageIntent = ImagePicker.getPickImageIntent(getActivity(), "", "bache_picture_" + (position + 1) + ".jpg");
        startActivityForResult(chooseImageIntent, PICK_IMAGE_ID);
    }

    private void makeCall() {
        Intent callIntent = new Intent(Intent.ACTION_CALL);
        callIntent.setData(Uri.parse("tel:" + Constants.REPORT_CALL));
        startActivity(callIntent);

        if (invalidReportMessageDialog != null) {
            invalidReportMessageDialog.dismiss();
        }

        if (mReportDetailFragmentListener != null) {
            mReportDetailFragmentListener.onNewReportFinished();
        }
    }

    @Override
    public void doAllowedStuffWithPermissionGrantedForAction(String action) {
        switch (action) {
            case ACTION_TAKE_OR_CHOOSE_PICTURE:
                showImagePicker(mPictureToAdd);
                break;
            case ACTION_PHONE_CALL:
                makeCall();
                break;
            default:
                break;
        }
    }

    @Override
    public void onPermissionDenied(String message) {
        Toast.makeText(mCurrentContext, message, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onPermissionDeniedPermanently(String message) {
        Toast.makeText(mCurrentContext, message, Toast.LENGTH_SHORT).show();
    }

    public interface ReportDetailFragmentListener {
        void onNewReportFinished();
//        void onTutorialClicked();
//        void onCaptureLocation();
        void onNewReportCanceled();
    }
}
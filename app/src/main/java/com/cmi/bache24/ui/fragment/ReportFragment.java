package com.cmi.bache24.ui.fragment;

import android.animation.ObjectAnimator;
import android.animation.TypeEvaluator;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.text.Html;
import android.util.Log;
import android.util.Property;
import android.view.InflateException;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

//import com.apptentive.android.sdk.util.Util;
import com.cmi.bache24.R;
import com.cmi.bache24.data.model.Report;
import com.cmi.bache24.data.model.User;
import com.cmi.bache24.data.remote.ServicesManager;
import com.cmi.bache24.data.remote.interfaces.ReportsCallback;
import com.cmi.bache24.ui.activity.ReportDetailActivity;
import com.cmi.bache24.ui.dialog.SimpleIconCenteredAlertDialog;
import com.cmi.bache24.ui.dialog.interfaces.MessageDialogListener;
import com.cmi.bache24.ui.views.CustomMapFragment;
import com.cmi.bache24.ui.views.TouchableWrapper;
import com.cmi.bache24.util.Constants;
import com.cmi.bache24.util.LatLngInterpolator;
import com.cmi.bache24.util.PreferencesManager;
import com.cmi.bache24.util.Utils;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

/**
 * A placeholder fragment containing a simple view.
 */
public class ReportFragment extends BaseFragment implements OnMapReadyCallback, LocationListener {

    private GoogleMap mMap;
    private LocationManager mLocationManager;
    private Location mMyCurrentLocation;
    private Button mButtonNewReport;
//    private TextView mTextViewNewReport;
    private TextView mCurrentAddressTextView;
    private ImageView mLocationButton;
    private LinearLayout mCurrentAddressLayoutContainer;
    private ReportFragmentListener mReportFragmentListener;
    private Marker mNewReportMarker;
    private Marker mMyCurrentLocationMarker;
    private String mAddressString;
    private Animation mShowAnimation;
    private User mCurrentUser;
    private Address mGeoCoderAddress;
    private static View rootView;
    private List<Marker> mReportsMarkersList;
    private RelativeLayout mCenteredMarkerLayout;
    private ImageView mCenteredMarkerImageView;
    private CustomMapFragment mMapFragment;
    private ImageView mLocationTempImageView;
    int REQUEST_LOCATION_PERMISSION_CODE = 342;
    private Button saveTempReportButton;
    private Report temporaryReportSelected;
    private int temporaryReportIndex = -1;
    private LatLng temporaryReportPosition;
    boolean isTemporaryReportInEdition = false;

//    private Marker temporaryReportMarker;

    private enum HomeStatus {
        SHOW_REPORTS,
        CREATE_REPORT
    }

    private HomeStatus mHomeStatus;

    public ReportFragment() {
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
            rootView = inflater.inflate(R.layout.fragment_report, container, false);
        } catch (InflateException e) {

        }

        mMapFragment = (CustomMapFragment) getActivity().getFragmentManager()
                .findFragmentById(R.id.map_report);
        mButtonNewReport = (Button) rootView.findViewById(R.id.button_new_report);
        mCurrentAddressTextView = (TextView) rootView.findViewById(R.id.textview_current_address);
        mCurrentAddressLayoutContainer = (LinearLayout) rootView.findViewById(R.id.layout_current_address_container);
//        mTextViewNewReport = (TextView) rootView.findViewById(R.id.button_new_report_textview);
        mLocationButton = (ImageView) rootView.findViewById(R.id.imageview_location);
        mCenteredMarkerLayout = (RelativeLayout) rootView.findViewById(R.id.layout_centered_marker);
        mCenteredMarkerImageView = (ImageView) rootView.findViewById(R.id.imageview_centered_marker);
        mLocationTempImageView  = (ImageView) rootView.findViewById(R.id.imageview_location_temp);
        saveTempReportButton = (Button) rootView.findViewById(R.id.button_save_temp_report);

        mLocationTempImageView.setVisibility(View.INVISIBLE);

        mLocationManager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);

        mCurrentUser = PreferencesManager.getInstance().getUserInfo(getActivity());

        if (mMapFragment != null)
            mMapFragment.getMapAsync(this);

        mButtonNewReport.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PreferencesManager.getInstance().setEditAddress(mCurrentContext, false);
                PreferencesManager.getInstance().setCancelTemporaryReport(mCurrentContext, false);

                if (mButtonNewReport.getText().equals(getResources().getString(R.string.tutorial_button_text))) {
                    showDetailReport();
                } else {

                    if (mMyCurrentLocation == null) {
                        Toast.makeText(getActivity(), "No se pudo detectar tu ubicación, por favor revisa si la localización se encuentra habilitada", Toast.LENGTH_LONG).show();
                        return;
                    }

                    isTemporaryReportInEdition = false;
                    temporaryReportIndex = -1;

                    if (PreferencesManager.getInstance().isReportTutorialFinished(getActivity())) {
                        moveToMyCurrentLocation(true);
                        showCreateReportViews(new LatLng(mMyCurrentLocation.getLatitude(), mMyCurrentLocation.getLongitude()), true);
                    } else {
                        showFirstReportTutorial(new LatLng(mMyCurrentLocation.getLatitude(), mMyCurrentLocation.getLongitude()));
                    }
                }
            }
        });

        mLocationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mMyCurrentLocation != null) {
                    getAddressForLatLng(new LatLng(mMyCurrentLocation.getLatitude(), mMyCurrentLocation.getLongitude()));
                    moveToMyCurrentLocation(true);
                } else {
                    Toast.makeText(getActivity(), "No se pudo detectar tu ubicación, por favor revisa si la localización se encuentra habilitada", Toast.LENGTH_LONG).show();
                }
            }
        });

        ViewTreeObserver viewTreeObserver = mCenteredMarkerImageView.getViewTreeObserver();
        viewTreeObserver.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {

                RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                params.setMargins(0, 0, 0, mCenteredMarkerImageView.getHeight() + (mLocationTempImageView.getHeight() / 2));
                mCenteredMarkerImageView.setLayoutParams(params);

                ViewTreeObserver vto = mCenteredMarkerImageView.getViewTreeObserver();

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                    vto.removeOnGlobalLayoutListener(this);
                } else {
                    vto.removeGlobalOnLayoutListener(this);
                }
            }
        });

        saveTempReportButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (isTemporaryReportInEdition) {

                    Log.i("EDIT_ADDRESS_", "Delete report at position => " +  temporaryReportIndex);

                    isTemporaryReportInEdition = false;
                    PreferencesManager.getInstance().deleteTemporaryReport(mCurrentContext, temporaryReportIndex);
//                    temporaryReportIndex = -1;
                }

                saveTemporaryReport();
                showTemporaryReports();

                resetNewReportViews();
                moveToMyCurrentLocation(true);
            }
        });

        resetNewReportViews();

        return rootView;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        mCurrentContext = context;

        try{
            if (context instanceof Activity) {
                mReportFragmentListener = (ReportFragmentListener) context;
            }
        } catch (ClassCastException ex) {

        }
    }

    @Override
    public void onResume() {
        super.onResume();

        isTemporaryReportInEdition = false;

        if (PreferencesManager.getInstance().shouldEditAddress(mCurrentContext)) {

            isTemporaryReportInEdition = true;

            Log.i("EDIT_ADDRESS_", "isTemporaryReportInEdition = " + isTemporaryReportInEdition + ", temporaryReportIndex = " + temporaryReportIndex);

            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(
                    temporaryReportPosition, mMap.getCameraPosition().zoom));

            showCreateReportViews(temporaryReportPosition, true);
            PreferencesManager.getInstance().setEditAddress(mCurrentContext, false);
        } else if (PreferencesManager.getInstance().shouldCancelTemporaryReport(mCurrentContext)) {

            Log.i("EDIT_ADDRESS_", "Cancel pressed");

            PreferencesManager.getInstance().setCancelTemporaryReport(mCurrentContext, false);
            resetNewReportViews();
            moveToMyCurrentLocation(true);
        } else if (mHomeStatus != HomeStatus.CREATE_REPORT) {
            Log.i("EDIT_ADDRESS_", "mHomeStatus != HomeStatus.CREATE_REPORT");

            showTemporaryReports();
            moveToMyCurrentLocation(true);
        }
    }

    public boolean shouldInterceptOnBackPressed() {
        if (mCurrentAddressLayoutContainer.getVisibility() == View.VISIBLE) {
            resetNewReportViews();
            moveToMyCurrentLocation(true);
            return true;
        }

        return false;
    }

    @Override
    public void onMapReady(GoogleMap map) {
        mMap = map;

        checkLocationPermissions();

        if (mMapFragment != null) {
            if (mMapFragment.mTouchView != null) {
                mMapFragment.mTouchView.setActionListener(new TouchableWrapper.ActionListener() {
                    @Override
                    public void onActionUp() {
                        if (mHomeStatus == HomeStatus.CREATE_REPORT) {
                            showCreateReportViews(mLastCameraPosition, false);
                        }
                    }

                    @Override
                    public void onActionDown() {
                        disableNewReportButton(true);
                    }
                });
            }
        }

        /*mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {
                temporaryReportPosition = latLng;

                if (temporaryReportMarker != null) {
                    temporaryReportMarker.remove();
                }

                temporaryReportMarker = mMap.addMarker(new MarkerOptions()
                        .position(latLng)
                        .icon(BitmapDescriptorFactory.fromResource(R.drawable.pin_reporte_gris)));
            }
        });*/

        mMap.setOnCameraChangeListener(new GoogleMap.OnCameraChangeListener() {
            @Override
            public void onCameraChange(CameraPosition cameraPosition) {
                mLastCameraPosition = cameraPosition.target;
            }
        });

        showTemporaryReports();

        loadReports();
    }

    private void disableNewReportButton(boolean disable) {
        if (mHomeStatus == HomeStatus.CREATE_REPORT) {
            mButtonNewReport.setAlpha(disable ? 0.5f : 1.0f);
            mButtonNewReport.setEnabled(!disable);

            saveTempReportButton.setAlpha(disable ? 0.5f : 1.0f);
            saveTempReportButton.setEnabled(!disable);
        }
    }

    private LatLng mLastCameraPosition;

    private void goToMyLocation() {
        if (mMyCurrentLocation != null) {
            LatLng latLng = new LatLng(mMyCurrentLocation.getLatitude(), mMyCurrentLocation.getLongitude());

            updateMyMarkerPosition(latLng);

            CameraPosition newCameraPosition = new CameraPosition(latLng,
                    18,
                    mMap.getCameraPosition().tilt,
                    mMap.getCameraPosition().bearing);
            mMap.moveCamera(CameraUpdateFactory.newCameraPosition(newCameraPosition));
        }
    }

    LocationManager locationManager;
    boolean isGPSEnabled;
    boolean isNetworkEnabled;
    boolean canGetLocation;

    public Location getLocation() {

        try {

            locationManager = (LocationManager) getActivity()
                    .getSystemService(getActivity().LOCATION_SERVICE);

            // getting GPS status
            isGPSEnabled = locationManager
                    .isProviderEnabled(LocationManager.GPS_PROVIDER);

            // getting network status
            isNetworkEnabled = locationManager
                    .isProviderEnabled(LocationManager.NETWORK_PROVIDER);

            if (!isGPSEnabled && !isNetworkEnabled) {
                // no network provider is enabled
            } else {
                this.canGetLocation = true;
                if (isNetworkEnabled) {

                    locationManager.requestLocationUpdates(
                            LocationManager.NETWORK_PROVIDER,
                            1000,
                            5, this);

                    if (locationManager != null) {
                        mMyCurrentLocation = locationManager
                                .getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                    }
                }
                // if GPS Enabled get lat/long using GPS Services
                if (isGPSEnabled) {

                    locationManager.requestLocationUpdates(
                            LocationManager.GPS_PROVIDER,
                            1000,
                            5, this);

                    if (locationManager != null) {
                        mMyCurrentLocation = locationManager
                                .getLastKnownLocation(LocationManager.GPS_PROVIDER);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (mMyCurrentLocation == null) {
            mMyCurrentLocation = getDefaultLocation();
        }

        return mMyCurrentLocation;
    }

    private void showDefaultLocation() {
        mMyCurrentLocation = getDefaultLocation();
        goToMyLocation();
    }

    private Location getDefaultLocation() {
        Location defaultLocation = new Location("GPS");
        defaultLocation.setLatitude(19.4339137);
        defaultLocation.setLongitude(-99.1338311);

        return defaultLocation;
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

    @Override
    public void onLocationChanged(Location location) {
        mMyCurrentLocation = location;

        if (mHomeStatus == HomeStatus.SHOW_REPORTS) {
            moveToMyCurrentLocation(true);
        }
    }

    @Override
    public void onStatusChanged(String s, int i, Bundle bundle) {

    }

    @Override
    public void onProviderEnabled(String s) {

    }

    @Override
    public void onProviderDisabled(String s) {

    }

    private void showDetailReport() {

        if (mNewReportMarker == null) {

            if (mMyCurrentLocation == null) {
                Toast.makeText(getActivity(), "No se pudo detectar tu ubicación, por favor revisa si la localización se encuentra habilitada", Toast.LENGTH_LONG).show();
                return;
            }

            if (mAddressString == null || mAddressString == "") {
                Toast.makeText(getActivity(), "No se pudo detectar tu ubicación", Toast.LENGTH_LONG).show();
                return;
            }
        }

//        showReportDetailActivityForPosition(mLastCameraPosition, mAddressString, false, -1);

        Log.i("NEW_REPORT__", "isTemporaryReportInEdition = " + isTemporaryReportInEdition + ", temporaryReportIndex = " + temporaryReportIndex);

        showReportDetailActivityForPosition(mLastCameraPosition, mAddressString, isTemporaryReportInEdition, temporaryReportIndex > -1 ? temporaryReportIndex : -1);

        // ORIGINAL
//        Intent intentReportDetail = new Intent(getActivity(), ReportDetailActivity.class);
//
////        if (mNewReportMarker == null) {
////            intentReportDetail.putExtra(Constants.EXTRA_NEW_REPORT_LATITUDE, mLastCameraPosition.latitude);
////            intentReportDetail.putExtra(Constants.EXTRA_NEW_REPORT_LONGITUDE, mLastCameraPosition.longitude);
////            intentReportDetail.putExtra(Constants.EXTRA_NEW_REPORT_ADDRESS_AS_STRING, mAddressString);
////        } else {
//            intentReportDetail.putExtra(Constants.EXTRA_NEW_REPORT_LATITUDE, mLastCameraPosition.latitude);
//            intentReportDetail.putExtra(Constants.EXTRA_NEW_REPORT_LONGITUDE, mLastCameraPosition.longitude);
//            intentReportDetail.putExtra(Constants.EXTRA_NEW_REPORT_ADDRESS_AS_STRING, mAddressString);
////        }
//
//        getActivity().startActivityForResult(intentReportDetail, Constants.REPORT_DETAIL_REQUEST_CODE);
//        getActivity().overridePendingTransition(R.anim.enter_from_right, R.anim.enter_from_left);
    }

    public void newReportFinished() {
        if (mNewReportMarker != null)
            mNewReportMarker.remove();

        showNewReportButton();
    }

    private void showNewReportButton() {
        if (mShowAnimation == null) {
            mShowAnimation = AnimationUtils.loadAnimation(getActivity(), R.anim.bottom_to_center);
            mShowAnimation.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {

                }

                @Override
                public void onAnimationEnd(Animation animation) {
                    mButtonNewReport.setVisibility(View.VISIBLE);
                }

                @Override
                public void onAnimationRepeat(Animation animation) {

                }
            });
        }

        mButtonNewReport.startAnimation(mShowAnimation);
    }

//    private void showPreviousReports(List<Report> reports) {
//
//        if (getActivity() == null)
//            return;
//
//        if (reports.size() > 0) {
//
//            mReportsMarkersList = null;
//            mReportsMarkersList = new ArrayList<>();
//
//            for (int i = 0; i < reports.size(); i++) {
//                LatLng markerPosition = new LatLng(Double.valueOf(reports.get(i).getLatitude()),
//                                                  Double.valueOf(reports.get(i).getLongitude()));
//
//                Marker reportMarker = null;
//
//                if (reports.get(i).getEtapaId() == Constants.REPORT_STATUS_SOLVED && reports.get(i).getAvenidaId() == Constants.AVENIDA_OK) {
//
//                    reportMarker = mMap.addMarker(new MarkerOptions()
//                            .position(markerPosition)
//                            .icon(BitmapDescriptorFactory.fromResource(R.drawable.pin_reporte_)));
//                } else if ((reports.get(i).getEtapaId() == Constants.REPORT_STATUS_NEW || reports.get(i).getEtapaId() == Constants.REPORT_STATUS_ASIGNED)
//                        && reports.get(i).getAvenidaId() == Constants.AVENIDA_OK) {
//                    reportMarker = mMap.addMarker(new MarkerOptions()
//                            .position(markerPosition)
//                            .icon(BitmapDescriptorFactory.fromResource(R.drawable.pin_reporte_amarillo)));
//                } else if (reports.get(i).getAvenidaId() == Constants.AVENIDA_NOT_VALID ||
//                           reports.get(i).getEtapaId() == Constants.TROOP_REPORT_STATUS_DOES_NOT_APPLY ||
//                           reports.get(i).getEtapaId() == Constants.TROOP_REPORT_STATUS_FALSE ||
//                           reports.get(i).getEtapaId() == Constants.TROOP_REPORT_STATUS_WRONG) {
//                    reportMarker = mMap.addMarker(new MarkerOptions()
//                            .position(markerPosition)
//                            .icon(BitmapDescriptorFactory.fromResource(R.drawable.pin_reporte_gris)));
//                } else {
//                    reportMarker = mMap.addMarker(new MarkerOptions()
//                            .position(markerPosition)
//                            .icon(BitmapDescriptorFactory.fromResource(R.drawable.pin_reporte_gris)));
//                }
//
//                if (reportMarker != null) {
//                    mReportsMarkersList.add(reportMarker);
//                }
//            }
//        }
//    }

    public void updateReportMarkers(boolean addNew) {
        if (addNew) {
            try {
                resetNewReportViews();
                mMap.clear();
                loadReports();
                mMyCurrentLocation = getLocation();
                goToMyLocation();
                mNewReportMarker = null;
            } catch (Exception ex) {

            }
        }
    }

    private void loadReports() {

        if (!Utils.getInstance().isInternetAvailable(getActivity()))
            return;

        ServicesManager.getReports(mCurrentUser, new ReportsCallback() {
            @Override
            public void onReportsCallback(List<Report> reports) {
                //showPreviousReports(reports);
            }

            @Override
            public void onReportsFail(String message) {
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
        });
    }


    private void resetNewReportViews() {
        mCurrentAddressLayoutContainer.setVisibility(View.GONE);
        mButtonNewReport.setText(getResources().getString(R.string.new_report_button_title));
        mCenteredMarkerImageView.setVisibility(View.INVISIBLE);
        disableNewReportButton(false);
        mHomeStatus = HomeStatus.SHOW_REPORTS;

        if (mNewReportMarker != null) {
            mNewReportMarker.remove();
            mNewReportMarker = null;
        }

        hideSaveButton();

        if (mReportsMarkersList != null) {
            for (int i = 0; i < mReportsMarkersList.size(); i++) {
                if (mReportsMarkersList.get(i) != null) {
                    mReportsMarkersList.get(i).setVisible(true);
                }
            }
        }

        if (mReportFragmentListener != null) {
            mReportFragmentListener.onNewReportReset();
        }
    }

    private void showCreateReportViews(final LatLng position, boolean showSaveButton) {
        mHomeStatus = HomeStatus.CREATE_REPORT;

        mButtonNewReport.setText(getResources().getString(R.string.tutorial_button_text));
        mCenteredMarkerImageView.setVisibility(View.VISIBLE);

        deleteMyMarkerPosition();

        if (showSaveButton)
            showSaveButton();

        try {
            if (mReportsMarkersList != null) {
                for (int i = 0; i < mReportsMarkersList.size(); i++) {
                    if (mReportsMarkersList.get(i) != null) {
                        mReportsMarkersList.get(i).setVisible(false);
                    }
                }
            }
        }
        catch (Exception ex) {

        }

        if (mReportFragmentListener != null) {
            mReportFragmentListener.onNewReportStart();
        }

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                mCurrentAddressLayoutContainer.setVisibility(View.VISIBLE);
                getAddressForLatLng(position);
            }
        }, 200);
    }

    public void setCurrentAddressString(String currentAddres, boolean disableButton) {
        StringBuilder currentAddressStringBuilder = new StringBuilder();

        currentAddressStringBuilder.append("<font color='#ec2697'>" +
                        getResources().getString(R.string.report_current_address_title) + "</font>");
        currentAddressStringBuilder.append(" ");
        currentAddressStringBuilder.append("<font color='#484848'>" +
                (currentAddres.length() == 0 ? getResources().getString(R.string.report_current_address_title_searching)
                        : currentAddres) + "</font>");

        mCurrentAddressTextView.setText(Html.fromHtml(currentAddressStringBuilder.toString()));

        mAddressString = currentAddres;

        disableNewReportButton(disableButton);
    }

    private void getAddressForLatLng(final LatLng position) {
        setCurrentAddressString("", true);

        new GetAddressInBackground(this).execute(position);

        /*getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                try {
                    Geocoder geocoder = new Geocoder(getActivity(), Locale.getDefault());
                    List<Address> addresses = geocoder.getFromLocation(position.latitude, position.longitude, 1);

                    if (addresses.size() > 0) {
                        mGeoCoderAddress = addresses.get(0);
                        mAddressString = addresses.get(0).getAddressLine(0) + ", " + addresses.get(0).getAddressLine(1) + ", " + addresses.get(0).getAddressLine(2);

                        setCurrentAddressString(mAddressString, false);
                    } else {
                        setCurrentAddressString(getResources().getString(R.string.address_not_found_short), true);
                        mGeoCoderAddress = null;
                    }
                } catch (Exception ex) {
                    mGeoCoderAddress = null;
                    setCurrentAddressString(getResources().getString(R.string.address_not_found_short), true);
                }
            }
        });*/
    }

    private void showFirstReportTutorial(final LatLng position) {
        final SimpleIconCenteredAlertDialog reportTutorialMessage = new SimpleIconCenteredAlertDialog();

        reportTutorialMessage.setupDialog(getResources().getString(R.string.first_report_tutorial_title),
                                    getResources().getString(R.string.first_report_tutorial_message),
                                    R.drawable.happy,
                                    getResources().getString(R.string.button_accept_label),
                                    new MessageDialogListener() {
                                        @Override
                                        public void onAccept() {
                                            reportTutorialMessage.dismiss();
                                            PreferencesManager.getInstance().setReportTutorialFinished(getActivity(), true);
                                            showCreateReportViews(position, true);
                                        }
                                    });
        reportTutorialMessage.setCancelable(false);
        reportTutorialMessage.show(getActivity().getSupportFragmentManager(), "");
    }

    private void showNewReportMarkerInPosition(LatLng latLng) {
        if (mNewReportMarker == null) {
            mNewReportMarker = mMap.addMarker(new MarkerOptions()
                    .position(latLng)
                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.pin_reporte_amarillo)));

            mNewReportMarker.setDraggable(true);
        } else {
            animateMarkerToPosition(mNewReportMarker, latLng, new LatLngInterpolator.Linear());
        }
    }

    private void moveToMyCurrentLocation(boolean animateToMarker) {
        if (mMyCurrentLocation != null && mMap != null) {
            LatLng latLng = new LatLng(mMyCurrentLocation.getLatitude(), mMyCurrentLocation.getLongitude());

            updateMyMarkerPosition(latLng);

            if (animateToMarker) {
                CameraUpdate location = CameraUpdateFactory.newLatLngZoom(
                        latLng, mMap.getCameraPosition().zoom);
                mMap.animateCamera(location);
            }
        }
    }

    private void updateMyMarkerPosition(LatLng latLng) {
        if (mHomeStatus == HomeStatus.SHOW_REPORTS) {
            deleteMyMarkerPosition();

            mMyCurrentLocationMarker = mMap.addMarker(new MarkerOptions()
                    .position(latLng)
                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.ubicacion)));
        }
    }

    private void deleteMyMarkerPosition() {
        if (mMyCurrentLocationMarker != null) {
            mMyCurrentLocationMarker.remove();
            mMyCurrentLocationMarker = null;
        }
    }

    private void checkLocationPermissions() {
        checkPermissionsForAction(new String[] {
                android.Manifest.permission.ACCESS_FINE_LOCATION,
                android.Manifest.permission.ACCESS_COARSE_LOCATION
            },
            ACTION_REQUEST_LOCATION,
            "Los permisos para ubicación están desactivados");
    }

    private void saveTemporaryReport() {
        Report tempReport = new Report();

        /*if (temporaryReportPosition == null && mMyCurrentLocationMarker != null) {
            temporaryReportPosition = mMyCurrentLocationMarker.getPosition();
        }

        if (temporaryReportPosition == null)
            return;*/

//        tempReport.setReportId(UUID.randomUUID().toString());
        tempReport.setLatitude(String.valueOf(mLastCameraPosition.latitude));
        tempReport.setLongitude(String.valueOf(mLastCameraPosition.longitude));
        tempReport.setAddress(mAddressString);
        tempReport.setTemp(true);

        PreferencesManager.getInstance().addLocalReport(mCurrentContext, tempReport);

        /*temporaryReportPosition = null;*/
    }

    private void showTemporaryReports() {
        final List<Report> temporaryReports = PreferencesManager.getInstance().getLocalReports(mCurrentContext);

        if (temporaryReports.size() >= 0 && mMap != null) {

            mReportsMarkersList = null;
            mReportsMarkersList = new ArrayList<>();

            if (mMap != null)
                mMap.clear();

            for (int i = 0; i < temporaryReports.size(); i++) {
                LatLng markerPosition = new LatLng(Double.valueOf(temporaryReports.get(i).getLatitude()),
                                                   Double.valueOf(temporaryReports.get(i).getLongitude()));

                Marker reportMarker = mMap.addMarker(new MarkerOptions()
                        .position(markerPosition)
                        .icon(BitmapDescriptorFactory.fromResource(R.drawable.pin_temporary_report)));

                if (reportMarker != null) {
                    mReportsMarkersList.add(reportMarker);
                }
            }

            mMap.setOnMarkerClickListener(null);

            mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
                @Override
                public boolean onMarkerClick(Marker marker) {
                    if (mReportsMarkersList == null)
                        return false;

                    if (mReportsMarkersList.size() == 0)
                        return false;

                    int position = -1;

                    for (int i = 0; i < mReportsMarkersList.size(); i++) {
                        if (mReportsMarkersList.get(i).equals(marker)) {
                            position = i;
                            break;
                        }
                    }

                    if (position >= 0) {

                        temporaryReportSelected = temporaryReports.get(position);
                        temporaryReportIndex = position;
                        temporaryReportPosition = mReportsMarkersList.get(position).getPosition();

                        showReportDetailActivityForPosition(
                                mReportsMarkersList.get(position).getPosition(),
                                temporaryReportSelected.getAddress(), true, position);
                    }

                    return true;
                }
            });
        }
    }

    private void showReportDetailActivityForPosition(LatLng position, String addressString, boolean isTemporary, int positionInList) {
        Intent intentReportDetail = new Intent(getActivity(), ReportDetailActivity.class);

//        Log.i("EDIT_ADDRESS_", "Send to detail => latitude = " + position.latitude + ", longitude = " + position.longitude);

        intentReportDetail.putExtra(Constants.EXTRA_NEW_REPORT_LATITUDE, position.latitude);
        intentReportDetail.putExtra(Constants.EXTRA_NEW_REPORT_LONGITUDE, position.longitude);
        intentReportDetail.putExtra(Constants.EXTRA_NEW_REPORT_ADDRESS_AS_STRING, addressString);
        intentReportDetail.putExtra(Constants.EXTRA_NEW_REPORT_IS_TEMPORARY, isTemporary);
        intentReportDetail.putExtra(Constants.EXTRA_NEW_REPORT_POSITION_IN_LIST, temporaryReportIndex);

        getActivity().startActivityForResult(intentReportDetail, Constants.REPORT_DETAIL_REQUEST_CODE);
        getActivity().overridePendingTransition(R.anim.enter_from_right, R.anim.enter_from_left);
    }

    private void hideSaveButton() {
        Animation saveButtonAnimation = AnimationUtils.loadAnimation(mCurrentContext, R.anim.hide_save_report_button);
        saveButtonAnimation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                saveTempReportButton.setVisibility(View.GONE);

                LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) mButtonNewReport.getLayoutParams();
                params.setMargins(Utils.convertDpToPx(mCurrentContext, 16), 0, Utils.convertDpToPx(mCurrentContext, 16), 0);
                mButtonNewReport.setLayoutParams(params);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        saveTempReportButton.startAnimation(saveButtonAnimation);


    }

    private void showSaveButton() {
        Animation saveButtonAnimation = AnimationUtils.loadAnimation(mCurrentContext, R.anim.show_save_report_button);
        saveTempReportButton.setVisibility(View.VISIBLE);
        saveTempReportButton.startAnimation(saveButtonAnimation);

        LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) mButtonNewReport.getLayoutParams();
        params.setMargins(Utils.convertDpToPx(mCurrentContext, 4), 0, Utils.convertDpToPx(mCurrentContext, 16), 0);
        mButtonNewReport.setLayoutParams(params);
    }

    @Override
    public void doAllowedStuffWithPermissionGrantedForAction(String action) {
        if (action == ACTION_REQUEST_LOCATION && isAdded()) {
            mMyCurrentLocation = getLocation();
            goToMyLocation();
        }
    }

    @Override
    public void onPermissionDenied(String message) {
        Toast.makeText(mCurrentContext, message, Toast.LENGTH_SHORT).show();
        showDefaultLocation();
    }

    @Override
    public void onPermissionDeniedPermanently(String message) {
        if (mReportFragmentListener != null) {
            mReportFragmentListener.onLocationPermissionDenied();
        }

        showDefaultLocation();
    }

    public interface ReportFragmentListener {
        void onNewReportStart();
        void onNewReportReset();
        void onLocationPermissionDenied();
    }

    public class GetAddressInBackground extends AsyncTask<LatLng, Void, String> {

        ReportFragment Owner;
//        private Address geoCoderAddress;

        public GetAddressInBackground(ReportFragment owner) {
            this.Owner = owner;
        }

        @Override
        protected String doInBackground(LatLng... params) {

            LatLng position = params[0];

            String addressString = "";

            try {
                Geocoder geocoder = new Geocoder(getActivity(), Locale.getDefault());
                List<Address> addresses = geocoder.getFromLocation(position.latitude, position.longitude, 1);

                if (addresses.size() > 0) {
//                    geoCoderAddress = addresses.get(0);
                    addressString = addresses.get(0).getAddressLine(0) + ", "
                            + addresses.get(0).getAddressLine(1) + ", "
                            + addresses.get(0).getAddressLine(2);
                } else {
                    addressString = getResources().getString(R.string.address_not_found_short);
//                    geoCoderAddress = null;
                }
            } catch (Exception ex) {
//                geoCoderAddress = null;
                addressString = getResources().getString(R.string.address_not_found_short);
            }

            return addressString;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            Owner.setCurrentAddressString(s, s == getResources().getString(R.string.address_not_found_short));
        }
    }
}

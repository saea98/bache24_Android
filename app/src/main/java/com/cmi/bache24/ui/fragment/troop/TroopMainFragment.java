package com.cmi.bache24.ui.fragment.troop;

import android.animation.ObjectAnimator;
import android.animation.TypeEvaluator;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.util.Property;
import android.view.InflateException;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.cmi.bache24.R;
import com.cmi.bache24.data.model.Report;
import com.cmi.bache24.data.model.User;
import com.cmi.bache24.data.remote.ServicesManager;
import com.cmi.bache24.data.remote.interfaces.LoginCallback;
import com.cmi.bache24.data.remote.interfaces.NewReportCallback;
import com.cmi.bache24.data.remote.interfaces.ReportsCallback;
import com.cmi.bache24.ui.activity.ReportDetailActivity;
import com.cmi.bache24.ui.adapter.TroopReportsAdapter;
import com.cmi.bache24.ui.fragment.BaseFragment;
import com.cmi.bache24.util.Constants;
import com.cmi.bache24.util.LatLngInterpolator;
import com.cmi.bache24.util.PreferencesManager;
import com.cmi.bache24.util.Utils;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;
import java.util.List;

/**
 * A placeholder fragment containing a simple view.
 */
public class TroopMainFragment extends BaseFragment implements OnMapReadyCallback,
                                                                LocationListener,
                                                                View.OnClickListener,
                                                                TextWatcher {

    private GoogleMap mMap;
    private LocationManager mLocationManager;
    private Location mMyCurrentLocation;
    private ImageView mButtonNewReport;
    private Marker mNewReportMarker;
    private String mAddressString;
    private LatLng mAddressPosition;
    private Animation mShowAnimation;
    private Animation mHideAnimation;
    private User mCurrentUser;
    private Button mButtonMap;
    private Button mButtonList;
    private RelativeLayout mLayoutMap;
    private RelativeLayout mLayoutList;
    private RecyclerView mReportsList;
    private TroopReportsAdapter mReportsAdapter;
    private TroopMainFragmentListener mTroopMainFragmentListener;
    private LinearLayout mProgressLayout;
    private ImageButton searchReportImageButton;
    private EditText searchReportEditText;

    private static View rootView;
    private List<Marker> mMarkerList;
    private List<Report> mAllReports;
    private ImageView locationButton;
    private Marker mMyCurrentLocationMarker;
    Context mCurrentActivity;
    int REQUEST_LOCATION_PERMISSION_CODE = 342;
    List<Report> filteredReports;

    public TroopMainFragment() {
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
            rootView = inflater.inflate(R.layout.fragment_troop_main, container, false);
        } catch (InflateException e) {

        }

        MapFragment mapFragment = (MapFragment) ((FragmentActivity)mCurrentActivity).getFragmentManager()
                .findFragmentById(R.id.map_report);

        mButtonMap = (Button) rootView.findViewById(R.id.button_map);
        mButtonList = (Button) rootView.findViewById(R.id.button_list);

        mLayoutMap = (RelativeLayout) rootView.findViewById(R.id.layout_map);
        mLayoutList = (RelativeLayout) rootView.findViewById(R.id.layout_list);

        mProgressLayout = (LinearLayout) rootView.findViewById(R.id.progress_layout);
        mProgressLayout.setVisibility(View.GONE);

        mReportsList = (RecyclerView) rootView.findViewById(R.id.report_list);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(mCurrentActivity);
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        mReportsList.setLayoutManager(linearLayoutManager);

        locationButton = (ImageView) rootView.findViewById(R.id.image_location);

        searchReportImageButton = (ImageButton) rootView.findViewById(R.id.image_button_search_report);
        searchReportEditText = (EditText) rootView.findViewById(R.id.edit_text_report_ticket);

        searchReportImageButton.setVisibility(View.GONE);
        searchReportEditText.setVisibility(View.GONE);

        searchReportEditText.addTextChangedListener(this);

        mButtonMap.setOnClickListener(this);
        mButtonList.setOnClickListener(this);
        locationButton.setOnClickListener(this);
        searchReportImageButton.setOnClickListener(this);

        mReportsAdapter = new TroopReportsAdapter(mCurrentActivity);
        mLocationManager = (LocationManager) mCurrentActivity.getSystemService(Context.LOCATION_SERVICE);
        mCurrentUser = PreferencesManager.getInstance().getUserInfo(mCurrentActivity);

        mMarkerList = new ArrayList<>();

        initalizeMap();

        if (mapFragment != null)
            mapFragment.getMapAsync(this);

        mLayoutMap.setVisibility(View.VISIBLE);
        mLayoutList.setVisibility(View.GONE);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                getUserInfo();
            }
        }, 1000);

        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();

        searchReportEditText.setText("");
        searchReportEditText.setVisibility(View.GONE);
        loadReports();
    }

    @Override
    public void onLocationChanged(Location location) {
        mMyCurrentLocation = location;
        moveToMyCurrentLocation(true);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
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

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        checkLocationPermissions();

        loadReports();
    }

    public void loadReports() {

        mReportsAdapter.addAllSections(new ArrayList<Report>());

        if (mMap != null)
            mMap.clear();

//        mReportsAdapter.notifyDataSetChanged();

        if (!Utils.getInstance().isInternetAvailable(mCurrentActivity))
            return;

        mProgressLayout.setVisibility(View.VISIBLE);

        mNewReportMarker = null;

        ServicesManager.getReportsForTroops(mCurrentUser, new ReportsCallback() {
            @Override
            public void onReportsCallback(List<Report> reports) {

                mProgressLayout.setVisibility(View.GONE);

                mAllReports = reports;
                showReportsAsMap(reports);
                showReportsAsList(reports);
                goToMyInitialLocation();
                PreferencesManager.getInstance().addReports(mCurrentActivity, reports);

                if (reports.size() == 0) {
                    Toast.makeText(mCurrentActivity, "No hay reportes en espera de solución", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onReportsFail(String message) {
                mProgressLayout.setVisibility(View.GONE);
                if (mCurrentActivity != null) {
                    if (message != "") {
                        Toast.makeText(mCurrentActivity, message, Toast.LENGTH_SHORT).show();
                    }
                }
            }

            @Override
            public void userBanned() {

            }

            @Override
            public void onTokenDisabled() {
                Utils.showLoginForBadToken(mCurrentActivity);
            }
        });
    }

    private void showReportsAsMap(List<Report> reports) {

        if (!isGooglePlayServicesAvailable())
        {
            return;
        }

        if (mMap != null)
            mMap.clear();

        if (mCurrentActivity == null)
            return;

        if (reports.size() > 0) {

            mMarkerList = null;
            mMarkerList = new ArrayList<>();

            for (int i = 0; i < reports.size(); i++) {
                LatLng markerPosition = new LatLng(Double.valueOf(reports.get(i).getLatitude()),
                        Double.valueOf(reports.get(i).getLongitude()));

                Marker marker = mMap.addMarker(new MarkerOptions()
                        .position(markerPosition)
                        .icon(BitmapDescriptorFactory.fromResource(R.drawable.pin_reporte_amarillo)));

                mMarkerList.add(marker);
            }

            mMap.setOnMarkerClickListener(null);

            mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
                @Override
                public boolean onMarkerClick(Marker marker) {
                    if (mMarkerList == null)
                        return false;

                    if (mMarkerList.size() == 0)
                        return false;

                    int position = -1;

                    for (int i = 0; i < mMarkerList.size(); i++) {
                        if (mMarkerList.get(i).equals(marker)) {
                            position = i;
                            break;
                        }
                    }

                    if (position >= 0)
                        mTroopMainFragmentListener.onReportSelected(mAllReports.get(position));

                    return true;
                }
            });
        }
    }

    private void showReportsAsList(List<Report> reports) {
        mReportsAdapter.addAllSections(reports);
        mReportsList.setAdapter(mReportsAdapter);

        mReportsAdapter.setReportsListener(new TroopReportsAdapter.OnReportsListener() {
            @Override
            public void onReportClick(Report report) {
                mTroopMainFragmentListener.onReportSelected(report);
            }
        });
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        mCurrentActivity = context;
        try{
            if (context instanceof Activity) {
                mTroopMainFragmentListener = (TroopMainFragmentListener) context;
            }
        } catch (ClassCastException ex) {

        }
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == mButtonMap.getId() || view.getId() == mButtonList.getId()) {

            boolean isMapSectionVisible = view.getId() == mButtonMap.getId();

            mLayoutMap.setVisibility(isMapSectionVisible ? View.VISIBLE : View.GONE);
            mLayoutList.setVisibility(isMapSectionVisible ? View.GONE : View.VISIBLE);

            mButtonMap.setBackgroundResource(isMapSectionVisible ? R.drawable.btn_activo : R.drawable.btn_inactivo);
            mButtonList.setBackgroundResource(isMapSectionVisible ? R.drawable.btn_inactivo : R.drawable.btn_activo);

            mButtonMap.setTextColor(ContextCompat.getColor(mCurrentContext, isMapSectionVisible ? R.color.text_color_white : R.color.primary));
            mButtonList.setTextColor(ContextCompat.getColor(mCurrentContext, isMapSectionVisible ? R.color.primary : R.color.text_color_white));

            searchReportImageButton.setVisibility(isMapSectionVisible ? View.GONE : View.VISIBLE);

            searchReportEditText.removeTextChangedListener(this);
            searchReportEditText.setText("");
            searchReportEditText.addTextChangedListener(this);
            searchReportEditText.setVisibility(View.GONE);

            loadReports();

        }
        /*else if (view.getId() == mButtonList.getId()) {
            mLayoutMap.setVisibility(View.GONE);
            mLayoutList.setVisibility(View.VISIBLE);

            mButtonMap.setBackgroundResource(R.drawable.btn_inactivo);
            mButtonList.setBackgroundResource(R.drawable.btn_activo);

            mButtonMap.setTextColor(getResources().getColor(R.color.primary));
            mButtonList.setTextColor(getResources().getColor(R.color.text_color_white));

            searchReportImageButton.setVisibility(View.VISIBLE);

            searchReportEditText.removeTextChangedListener(this);
            searchReportEditText.setText("");
            searchReportEditText.addTextChangedListener(this);
            searchReportEditText.setVisibility(View.GONE);

            loadReports();
        } */
        else if (view.getId() == locationButton.getId()) {
            moveToMyCurrentLocation(true);
        } else if (view.getId() == searchReportImageButton.getId()) {
            searchReportEditText.setVisibility(View.VISIBLE);
        }
    }

    public void reloadAfterUpdateReport() {
        mLayoutMap.setVisibility(View.VISIBLE);
        mLayoutList.setVisibility(View.GONE);

        mButtonMap.setBackgroundResource(R.drawable.btn_activo);
        mButtonList.setBackgroundResource(R.drawable.btn_inactivo);

        mButtonMap.setTextColor(getResources().getColor(R.color.text_color_white));
        mButtonList.setTextColor(getResources().getColor(R.color.primary));

        if (mMap != null)
            mMap.clear();

        loadReports();
    }

    public void newReport() {

        if (mMyCurrentLocation == null ) {
            Toast.makeText(mCurrentActivity, "No se pudo detectar tu ubicación, por favor revisa si la localización se encuentra habilitada", Toast.LENGTH_LONG).show();
            return;
        }

        Intent intentReportDetail = new Intent(mCurrentActivity, ReportDetailActivity.class);

        intentReportDetail.putExtra(Constants.EXTRA_NEW_REPORT_LATITUDE, mMyCurrentLocation.getLatitude());
        intentReportDetail.putExtra(Constants.EXTRA_NEW_REPORT_LONGITUDE, mMyCurrentLocation.getLongitude());
        intentReportDetail.putExtra(Constants.EXTRA_NEW_REPORT_ADDRESS_AS_STRING, mAddressString);

        ((FragmentActivity)mCurrentActivity).startActivityForResult(intentReportDetail, Constants.REPORT_DETAIL_REQUEST_CODE);
        ((FragmentActivity)mCurrentActivity).overridePendingTransition(R.anim.enter_from_right, R.anim.enter_from_left);
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

    private void goToMyInitialLocation() {
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


            locationManager = (LocationManager) mCurrentActivity
                    .getSystemService(mCurrentActivity.LOCATION_SERVICE);

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
            /*mMyCurrentLocation = new Location("GPS");
            mMyCurrentLocation.setLatitude(19.4339137);
            mMyCurrentLocation.setLongitude(-99.1338311);*/
        }

        return mMyCurrentLocation;
    }

    private void showDefaultLocation() {
        mMyCurrentLocation = getDefaultLocation();
        goToMyInitialLocation();
    }

    private Location getDefaultLocation() {
        Location defaultLocation = new Location("GPS");
        defaultLocation.setLatitude(19.4339137);
        defaultLocation.setLongitude(-99.1338311);

        return defaultLocation;
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

        deleteMyMarkerPosition();

        mMyCurrentLocationMarker = mMap.addMarker(new MarkerOptions()
                .position(latLng)
                .icon(BitmapDescriptorFactory.fromResource(R.drawable.ubicacion)));
    }

    private void deleteMyMarkerPosition() {
        if (mMyCurrentLocationMarker != null) {
            mMyCurrentLocationMarker.remove();
            mMyCurrentLocationMarker = null;
        }
    }

    private void getUserInfo() {
        ServicesManager.getUserInfo(mCurrentUser, new LoginCallback() {
            @Override
            public void loginSuccess(User userInfo) {
                mCurrentUser = userInfo;
                if (mTroopMainFragmentListener != null) {
                    mTroopMainFragmentListener.onUserUpdated(userInfo);
                }
            }

            @Override
            public void loginFail(String message) {

            }

            @Override
            public void userBanned() {

            }

            @Override
            public void onTokenDisabled() {

            }
        });
    }

    public void cancelReportAttention() {
        if (!Utils.getInstance().isInternetAvailable(mCurrentActivity))
            return;

        PreferencesManager.getInstance().setReportCanceled("TroopMainFragment - cancelReportAttention", mCurrentActivity, true);

        mProgressLayout.setVisibility(View.VISIBLE);

        Report currentReport = new Report();
        currentReport.setTicket(PreferencesManager.getInstance().getReportAttentionTicketValue(mCurrentActivity));

        ServicesManager.cancelReportAttention(mCurrentContext, mCurrentUser, currentReport, new NewReportCallback() {

            @Override
            public void onSuccessRegister(String resultCode, Report reportCompleteInfo) {

            }

            @Override
            public void onSuccessRegisterReport(Report reportCompleteInfo) {
                Log.i("ReportStatus", "Reporte restaurado correctamente");
                mProgressLayout.setVisibility(View.GONE);
                loadReports();
                PreferencesManager.getInstance().clearReportAttentionData(mCurrentActivity);
            }

            @Override
            public void onFailRegisterReport(String message) {
                Log.i("ReportStatus", "Error al resturar el Reporte");
                mProgressLayout.setVisibility(View.GONE);
                loadReports();
            }

            @Override
            public void userBanned() {

            }

            @Override
            public void onTokenDisabled() {

            }
        });
    }

    private void initalizeMap() {

        try {
            MapsInitializer.initialize(mCurrentActivity.getApplicationContext());
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private boolean isGooglePlayServicesAvailable() {
        GoogleApiAvailability googleApiAvailability = GoogleApiAvailability.getInstance();
        int status = googleApiAvailability.isGooglePlayServicesAvailable(mCurrentActivity);

        if (status != ConnectionResult.SUCCESS) {
//            if (googleApiAvailability.isUserResolvableError(status)) {
//                googleApiAvailability.getErrorDialog((Activity) mCurrentActivity, status, 2424).show();
//            }

            return false;
        }

        return true;
    }

    private void checkLocationPermissions() {
        checkPermissionsForAction(new String[] {
                        android.Manifest.permission.ACCESS_FINE_LOCATION,
                        android.Manifest.permission.ACCESS_COARSE_LOCATION
                },
                ACTION_REQUEST_LOCATION,
                "Los permisos para ubicación están desactivados");
    }

    @Override
    public void doAllowedStuffWithPermissionGrantedForAction(String action) {
        mMyCurrentLocation = getLocation();
        goToMyInitialLocation();
    }

    @Override
    public void onPermissionDenied(String message) {
        Toast.makeText(mCurrentContext, message, Toast.LENGTH_SHORT).show();
        showDefaultLocation();
    }

    @Override
    public void onPermissionDeniedPermanently(String message) {
        if (mTroopMainFragmentListener != null) {
            mTroopMainFragmentListener.onLocationPermissionDenied();
        }

        showDefaultLocation();
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {

    }

    @Override
    public void afterTextChanged(Editable s) {
        if (mAllReports == null) {
            return;
        }

        if (s.toString().length() == 0) {
            showReportsAsList(mAllReports);
            return;
        }

        List<Report> filteredReports = getFilteredReports(s.toString());
        showReportsAsList(filteredReports);
    }

    private List<Report> getFilteredReports(String s) {
        if (filteredReports == null) {
            filteredReports = new ArrayList<>();
        } else {
            filteredReports.clear();
        }

        for (int i = 0; i < mAllReports.size(); i++) {
            if (mAllReports.get(i).getTicket().toLowerCase().contains(s.toLowerCase())) {
                filteredReports.add(mAllReports.get(i));
            }
        }

        return filteredReports;
    }

    public interface TroopMainFragmentListener {
        void onReportSelected(Report report);
        void onUserUpdated(User userInfo);
        void onLocationPermissionDenied();
    }
}

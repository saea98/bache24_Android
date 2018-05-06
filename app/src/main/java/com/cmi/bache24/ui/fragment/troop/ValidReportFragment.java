package com.cmi.bache24.ui.fragment.troop;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
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
import com.cmi.bache24.data.RealmManager;
import com.cmi.bache24.data.model.AttendedReport;
import com.cmi.bache24.data.model.Mts;
import com.cmi.bache24.data.model.Report;
import com.cmi.bache24.data.model.SolvedBache;
import com.cmi.bache24.data.model.User;
import com.cmi.bache24.data.model.realm.Stretch;
import com.cmi.bache24.data.remote.ServicesManager;
import com.cmi.bache24.data.remote.interfaces.NewReportCallback;
import com.cmi.bache24.ui.adapter.troop.BachesAdapter;
import com.cmi.bache24.ui.adapter.troop.StretchesAdapter;
import com.cmi.bache24.ui.dialog.ReportRepairedDialog;
import com.cmi.bache24.ui.dialog.SimpleIconMessageDialog;
import com.cmi.bache24.ui.dialog.interfaces.CommentsDialogListener;
import com.cmi.bache24.ui.dialog.interfaces.MessageDialogListener;
import com.cmi.bache24.util.Constants;
import com.cmi.bache24.util.ImagePicker;
import com.cmi.bache24.util.PreferencesManager;
import com.cmi.bache24.util.Utils;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link ValidReportFragment.ValidReportFragmentListener} interface
 * to handle interaction events.
 */
public class ValidReportFragment extends Fragment implements View.OnClickListener {

    private ImageView mImageView1;
    private ImageView mImageView2;
    private ImageView mImageView3;
    private ImageView mImageView4;
    private ImageView mImageView5;
    private ImageView mImageView6;
    private EditText mEditCommentsSupervision;
    private EditText mEditCommentsAtencion;
    private Button mButtonSend;

    private ValidReportFragmentListener mListener;
    private int mPictureToAdd = 0;
    private static final int PICK_IMAGE_ID = 234;
    private boolean picture1Saved = false;
    private boolean picture2Saved = false;
    private boolean picture3Saved = false;
    private boolean picture4Saved = false;
    private boolean picture5Saved = false;
    private boolean picture6Saved = false;
    private RecyclerView mRecyclerViewBaches;
    private BachesAdapter mBachesAdapter;
    private Report mCurrentReport;
    private Spinner mMaterialSpinner;
    private String mMaterialSelected;
    private LinearLayout mSupervisionLayout;
    private LinearLayout mMtsRep072;
    private LinearLayout mProgressLayout;
    private User mCurrentUser;
    private int mNoBaches;
    private EditText mEditMts;
    private LinearLayout mRootLayout;

    private Spinner mOrientationSpinner;
    private Spinner mPhysicalPlacesSpinner;
    private LinearLayout mReferencesLayout;
    private EditText mStretchEditText;
    private LinearLayout mFilteredStretchesLayout;
    private RecyclerView mFilteredStretchesRecyclerView;
    private StretchesAdapter mStretchesAdapter;
    private List<Stretch> mStretches;
    private String mOrientarionSelected;
    private String mPhysicalPlaceSelected;

    private Context mCurrentContext;

    public ValidReportFragment() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_valid_report, container, false);

        mImageView1 = (ImageView) rootView.findViewById(R.id.imageView20);
        mImageView2 = (ImageView) rootView.findViewById(R.id.imageView21);
        mImageView3 = (ImageView) rootView.findViewById(R.id.imageView22);
        mImageView4 = (ImageView) rootView.findViewById(R.id.imageView23);
        mImageView5 = (ImageView) rootView.findViewById(R.id.imageView24);
        mImageView6 = (ImageView) rootView.findViewById(R.id.imageView25);

        mEditCommentsSupervision = (EditText) rootView.findViewById(R.id.edit_comments_1);
        mEditCommentsAtencion = (EditText) rootView.findViewById(R.id.edit_comments_2);
        mEditMts = (EditText) rootView.findViewById(R.id.edit_mts_rep);
        mButtonSend = (Button) rootView.findViewById(R.id.button_send);
        mRecyclerViewBaches = (RecyclerView) rootView.findViewById(R.id.baches_list);
        mMaterialSpinner = (Spinner) rootView.findViewById(R.id.spinner_list);
        mSupervisionLayout = (LinearLayout) rootView.findViewById(R.id.supervision_layout);
        mMtsRep072 = (LinearLayout) rootView.findViewById(R.id.layout_mts_rep_072);

        mRootLayout = (LinearLayout) rootView.findViewById(R.id.root_view);

        mOrientationSpinner = (Spinner) rootView.findViewById(R.id.spinner_orientation);
        mPhysicalPlacesSpinner = (Spinner) rootView.findViewById(R.id.spinner_physical_places);
        mReferencesLayout = (LinearLayout) rootView.findViewById(R.id.layout_references);
        mStretchEditText = (EditText) rootView.findViewById(R.id.edittext_stretch);

        mFilteredStretchesLayout = (LinearLayout) rootView.findViewById(R.id.layout_filtered_stretches);
        mFilteredStretchesRecyclerView = (RecyclerView) rootView.findViewById(R.id.recycler_stretches);
        mFilteredStretchesLayout.setVisibility(View.GONE);

        mMtsRep072.setVisibility(View.GONE);

        mProgressLayout = (LinearLayout) rootView.findViewById(R.id.progress_layout);
        mProgressLayout.setVisibility(View.GONE);

        mImageView1.setOnClickListener(this);
        mImageView2.setOnClickListener(this);
        mImageView3.setOnClickListener(this);
        mImageView4.setOnClickListener(this);
        mImageView5.setOnClickListener(this);
        mImageView6.setOnClickListener(this);

        mButtonSend.setOnClickListener(this);

        mCurrentUser = PreferencesManager.getInstance().getUserInfo(getActivity());

        setupMaterialList();
        setupOrientationList();
        setupPhysicalPlacesList();
        setupStretchEditText();

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
                mListener = (ValidReportFragmentListener) context;
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
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
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

        } else if (view.getId() == mImageView4.getId()) {

            if (mListener != null)
                mListener.onCompletReportClick();

            mPictureToAdd = 3;

            showImagePicker(mPictureToAdd);

        } else if (view.getId() == mImageView5.getId()) {

            if (mListener != null)
                mListener.onCompletReportClick();

            mPictureToAdd = 4;

            showImagePicker(mPictureToAdd);

        } else if (view.getId() == mImageView6.getId()) {

            if (mListener != null)
                mListener.onCompletReportClick();

            mPictureToAdd = 5;

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
                    picture1Saved = true;
                    break;
                case 1:
                    bitmap = ImagePicker.getImageFromResult(getActivity(), resultCode, data, mCurrentReport.getTicket(), "f_b_picture_2.jpg");
                    mImageView2.setImageBitmap(bitmap);
                    mImageView2.setScaleType(ImageView.ScaleType.CENTER_CROP);
                    picture2Saved = true;
                    break;
                case 2:
                    bitmap = ImagePicker.getImageFromResult(getActivity(), resultCode, data, mCurrentReport.getTicket(), "f_b_picture_3.jpg");
                    mImageView3.setImageBitmap(bitmap);
                    mImageView3.setScaleType(ImageView.ScaleType.CENTER_CROP);
                    picture3Saved = true;
                    break;
                case 3:
                    bitmap = ImagePicker.getImageFromResult(getActivity(), resultCode, data, mCurrentReport.getTicket(), "f_b_picture_4.jpg");
                    mImageView4.setImageBitmap(bitmap);
                    mImageView4.setScaleType(ImageView.ScaleType.CENTER_CROP);
                    picture4Saved = true;
                    break;
                case 4:
                    bitmap = ImagePicker.getImageFromResult(getActivity(), resultCode, data, mCurrentReport.getTicket(), "f_b_picture_5.jpg");
                    mImageView5.setImageBitmap(bitmap);
                    mImageView5.setScaleType(ImageView.ScaleType.CENTER_CROP);
                    picture5Saved = true;
                    break;
                case 5:
                    bitmap = ImagePicker.getImageFromResult(getActivity(), resultCode, data, mCurrentReport.getTicket(), "f_b_picture_6.jpg");
                    mImageView6.setImageBitmap(bitmap);
                    mImageView6.setScaleType(ImageView.ScaleType.CENTER_CROP);
                    picture6Saved = true;
                    break;
                default:
                    bitmap = ImagePicker.getImageFromResult(getActivity(), resultCode, data, mCurrentReport.getTicket(), "f_b_picture_1.jpg");
                    mImageView1.setImageBitmap(bitmap);
                    mImageView1.setScaleType(ImageView.ScaleType.CENTER_CROP);
                    picture1Saved = true;
                    break;
            }
        }
    }

    public void setReport(Report report, int noBaches) {
        if (report == null)
            return;

        this.mCurrentReport = report;
        this.mNoBaches = noBaches;

        mEditCommentsAtencion.setText(getResources().getString(R.string.valid_report_default_attention_comment));

        if (mCurrentReport.getOrigin() == Report.ReportOrigin.O72) {
            mSupervisionLayout.setVisibility(View.GONE);
            mRecyclerViewBaches.setVisibility(View.GONE);
            mMtsRep072.setVisibility(View.VISIBLE);

            mReferencesLayout.setVisibility(View.GONE);
        } else {

            mEditCommentsSupervision.setText(getResources().getString(R.string.valid_report_default_supervision_comment));


            List<Integer> list = new ArrayList<>();

            for (int i = 0; i < noBaches; i++) {
                list.add(i + 1);
            }

            mBachesAdapter = new BachesAdapter(getActivity());
            mBachesAdapter.addAllRecords(list);
            mRecyclerViewBaches.setLayoutManager(new LinearLayoutManager(getActivity()));
            mRecyclerViewBaches.setAdapter(mBachesAdapter);

            setRecyclerViewHeight(noBaches);
        }
    }

    private void processReport() {

        Bitmap bitmapPicture1 = ((BitmapDrawable) mImageView1.getDrawable()).getBitmap();
        Bitmap bitmapPicture2 = ((BitmapDrawable) mImageView2.getDrawable()).getBitmap();
        Bitmap bitmapPicture3 = ((BitmapDrawable) mImageView3.getDrawable()).getBitmap();

        Bitmap bitmapPicture4 = ((BitmapDrawable) mImageView4.getDrawable()).getBitmap();
        Bitmap bitmapPicture5 = ((BitmapDrawable) mImageView5.getDrawable()).getBitmap();
        Bitmap bitmapPicture6 = ((BitmapDrawable) mImageView6.getDrawable()).getBitmap();

        if (mCurrentReport.getOrigin() != Report.ReportOrigin.O72) {
            if (mStretchEditText.getText().toString().trim().isEmpty()) {
                Toast.makeText(getActivity(), "El tramo es requerido", Toast.LENGTH_SHORT).show();
                unlockButtons();
                return;
            }

            if (mOrientarionSelected.equals("Seleccionar")) {
                Toast.makeText(getActivity(), "El sentido es requerido", Toast.LENGTH_SHORT).show();
                unlockButtons();
                return;
            }

            if (mPhysicalPlaceSelected.equals("Seleccionar")) {
                Toast.makeText(getActivity(), "El lugar físico es requerido", Toast.LENGTH_SHORT).show();
                unlockButtons();
                return;
            }

            if ((bitmapPicture1 == null || picture1Saved != true) ||
                (bitmapPicture2 == null || picture2Saved != true) ||
                (bitmapPicture3 == null || picture3Saved != true)) {
                Toast.makeText(getActivity(), "Las fotos son requeridas", Toast.LENGTH_SHORT).show();
                unlockButtons();
                return;
            }
        }

        if ((bitmapPicture4 == null || picture4Saved != true) ||
            (bitmapPicture5 == null || picture5Saved != true) ||
            (bitmapPicture6 == null || picture6Saved != true)) {

            Toast.makeText(getActivity(), "Las fotos son requeridas", Toast.LENGTH_SHORT).show();
            unlockButtons();
            return;
        }


        if (mCurrentReport.getOrigin() != Report.ReportOrigin.O72) {
            if (mEditCommentsSupervision.getText().toString().trim().isEmpty()) {
                Toast.makeText(getActivity(), "Los comentarios de supervisión son requeridos", Toast.LENGTH_SHORT).show();
                unlockButtons();
                return;
            }
        }

        if (mEditCommentsAtencion.getText().toString().trim().isEmpty()) {
            Toast.makeText(getActivity(), "Los comentarios de atención son requeridos", Toast.LENGTH_SHORT).show();
            unlockButtons();
            return;
        }


        if (mCurrentReport.getOrigin() == Report.ReportOrigin.O72) {
            if (mEditMts.getText().toString().trim().isEmpty()) {
                Toast.makeText(getActivity(), "Los metros reparados son requeridos", Toast.LENGTH_SHORT).show();
                unlockButtons();
                return;
            }
        } else {
            boolean allMtsCaptured = true;
            for (int i = 0; i < mBachesAdapter.getMtsValues().size(); i++) {
                if (mBachesAdapter.getMtsValues().get(i).isEmpty()) {
                    allMtsCaptured = false;
                    break;
                }
            }

            if (!allMtsCaptured) {
                Toast.makeText(getActivity(), "Los metros reparados son requeridos", Toast.LENGTH_SHORT).show();
                unlockButtons();
                return;
            }
        }

        PreferencesManager.getInstance().clearSendReportRetry(mCurrentContext);

        final ReportRepairedDialog reportRepairedDialog = new ReportRepairedDialog();
        reportRepairedDialog.setType(ReportRepairedDialog.ReportType.SOLVED);
        reportRepairedDialog.setCommentsDialogListener(new CommentsDialogListener() {

            @Override
            public void onCancel() {
                reportRepairedDialog.dismiss();
                unlockButtons();
            }

            @Override
            public void onSend(String message) {
                reportRepairedDialog.dismiss();
                sendReport();
            }
        });

        reportRepairedDialog.setCancelable(false);

        FragmentTransaction fragmentTransaction = ((AppCompatActivity)mCurrentContext).getSupportFragmentManager().beginTransaction();
        fragmentTransaction.add(reportRepairedDialog, null);

        fragmentTransaction.commitAllowingStateLoss();

//        reportRepairedDialog.show(getActivity().getFragmentManager(), "");
    }

    private void sendReport() {
        if (!Utils.getInstance().isInternetAvailable(getActivity()))
            return;

        final AttendedReport attendedReport = new AttendedReport();

        mProgressLayout.setVisibility(View.VISIBLE);

        attendedReport.setToken(mCurrentUser.getToken());
        attendedReport.setTicket(mCurrentReport.getTicket());
        attendedReport.setEtapa(Constants.TROOP_REPORT_STATUS_MARK_AS_REPAIRED);
        attendedReport.setNoBaches(mNoBaches);
        attendedReport.setBachesList(new ArrayList<SolvedBache>());

        Bitmap bitmapPictureRepaired1 = null;
        Bitmap bitmapPictureRepaired2 = null;
        Bitmap bitmapPictureRepaired3 = null;

        Bitmap bitmapPictureRepaired4 = ((BitmapDrawable) mImageView4.getDrawable()).getBitmap();
        Bitmap bitmapPictureRepaired5 = ((BitmapDrawable) mImageView5.getDrawable()).getBitmap();
        Bitmap bitmapPictureRepaired6 = ((BitmapDrawable) mImageView6.getDrawable()).getBitmap();

        if (mCurrentReport.getOrigin() != Report.ReportOrigin.O72) {
            bitmapPictureRepaired1 = ((BitmapDrawable) mImageView1.getDrawable()).getBitmap();
            bitmapPictureRepaired2 = ((BitmapDrawable) mImageView2.getDrawable()).getBitmap();
            bitmapPictureRepaired3 = ((BitmapDrawable) mImageView3.getDrawable()).getBitmap();
        }

        attendedReport.setStretch(mStretchEditText.getText().toString());
        attendedReport.setOrientation(mOrientarionSelected);
        attendedReport.setPhysicalPlace(mPhysicalPlaceSelected);

        List<String> mMtsValues = null;

        if (mCurrentReport.getOrigin() != Report.ReportOrigin.O72) {
            mMtsValues = mBachesAdapter.getMtsValues();
        }

        for (int i = 0; i < mNoBaches; i++) {
            SolvedBache bache = new SolvedBache();

            if (i == 0) {
                bache.setImagen4(Utils.bitmapToBase64(bitmapPictureRepaired4, Constants.PICTURE_SQUAD_QUALITY));
                bache.setImagen5(Utils.bitmapToBase64(bitmapPictureRepaired5, Constants.PICTURE_SQUAD_QUALITY));
                bache.setImagen6(Utils.bitmapToBase64(bitmapPictureRepaired6, Constants.PICTURE_SQUAD_QUALITY));

                if (mCurrentReport.getOrigin() != Report.ReportOrigin.O72) {
                    bache.setImagen1(Utils.bitmapToBase64(bitmapPictureRepaired1, Constants.PICTURE_SQUAD_QUALITY));
                    bache.setImagen2(Utils.bitmapToBase64(bitmapPictureRepaired2, Constants.PICTURE_SQUAD_QUALITY));
                    bache.setImagen3(Utils.bitmapToBase64(bitmapPictureRepaired3, Constants.PICTURE_SQUAD_QUALITY));

                    bache.setComentario_supervision(mEditCommentsSupervision.getText().toString());
                }

                bache.setComentario_atencion(mEditCommentsAtencion.getText().toString());

                bache.setMaterial(mMaterialSelected);
                bache.setMetros(new ArrayList<Mts>());

                for (int j = 0; j < mNoBaches; j++) {
                    Mts metro = new Mts();

                    metro.setName("baches_metros" + (j + 1));

                    if (mCurrentReport.getOrigin() != Report.ReportOrigin.O72) {
                        metro.setValue(mMtsValues.get(j));
                    } else {
                        metro.setValue(mEditMts.getText().toString());
                    }

                    bache.getMetros().add(metro);
                }
            }

            attendedReport.getBachesList().add(bache);
        }

        if (mListener != null)
            mListener.onCompletReportClick();

        ServicesManager.sendAttendedReport(mCurrentContext, mCurrentUser, attendedReport, new NewReportCallback() {

            @Override
            public void onSuccessRegister(String resultCode, Report reportCompleteInfo) {

            }

            @Override
            public void onSuccessRegisterReport(Report reportCompleteInfo) {
                mProgressLayout.setVisibility(View.GONE);

                unlockButtons();

                PreferencesManager.getInstance().setReportAttentionInProgress(getActivity(), false);
                PreferencesManager.getInstance().setReportCanceled("ValidReportFragment - sendAttendedReport-onSuccessRegisterReport", getActivity(), true);

                showAlert(true,
                          getResources().getString(R.string.report_attention_alert_title_0),
                          getAlertMessage(attendedReport.getTicket(), false));
            }

            @Override
            public void onFailRegisterReport(String message) {
                mProgressLayout.setVisibility(View.GONE);

                unlockButtons();

                if (message.equals(Constants.AGU_DESCRIPTION)) {

                    PreferencesManager.getInstance().setReportAttentionInProgress(getActivity(), false);
                    PreferencesManager.getInstance().setReportCanceled("ValidReportFragment - onFailRegisterReport", getActivity(), true);

                    showAlert(true,
                            getResources().getString(R.string.report_attention_alert_title_0),
                            getAlertMessage(attendedReport.getTicket(), true));

                } else if (message.equals(Constants.REPORTE_ATTENDED)) {

                    PreferencesManager.getInstance().setReportAttentionInProgress(getActivity(), false);
                    PreferencesManager.getInstance().setReportCanceled("ValidReportFragment - onFailRegisterReport", getActivity(), true);

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
        messageSB.append(getResources().getString(R.string.report_attention_alert_status_1));
        if (hasError) {
            messageSB.append(", ");
            messageSB.append(getResources().getString(R.string.report_attention_alert_message_agu_3));
        } else {
            messageSB.append(".");
        }

        return messageSB.toString();
    }

    private void setRecyclerViewHeight(int numberOfElements) {
        ViewGroup.LayoutParams params = mRecyclerViewBaches.getLayoutParams();

        int pixels = Utils.convertDpToPx(getActivity(), 80);

        params.height = (numberOfElements * pixels);

        mRecyclerViewBaches.setLayoutParams(params);
    }

    private void setupMaterialList() {
        String[] regions = getActivity().getResources().getStringArray(R.array.report_material_types);
        ArrayAdapter<String> regionsAdapter = new ArrayAdapter<>(getActivity(), R.layout.rejection_item, regions);
        mMaterialSpinner.setAdapter(regionsAdapter);

        mMaterialSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                mMaterialSelected = (String) adapterView.getItemAtPosition(i);

                try {
                    ((TextView) adapterView.getChildAt(0)).setTextColor(getResources().getColor(R.color.text_color_gray));
                } catch (Exception ex) {

                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
    }

    private void setupOrientationList() {
        String[] orientarionList = getActivity().getResources().getStringArray(R.array.report_orientation_list);
        ArrayAdapter<String> regionsAdapter = new ArrayAdapter<>(getActivity(), R.layout.rejection_item, orientarionList);
        mOrientationSpinner.setAdapter(regionsAdapter);

        mOrientationSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                mOrientarionSelected = (String) adapterView.getItemAtPosition(i);

                try {
                    if (mOrientarionSelected.equals("Seleccionar"))
                        ((TextView) adapterView.getChildAt(0)).setTextColor(ContextCompat.getColor(getActivity(), R.color.text_hint_color));
                    else {
                        ((TextView) adapterView.getChildAt(0)).setTextColor(ContextCompat.getColor(getActivity(), R.color.text_color_gray));
                    }
                } catch (Exception ex) {

                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
    }

    private void setupPhysicalPlacesList() {
        String[] physicalPlacesList = getActivity().getResources().getStringArray(R.array.report_physical_places_list);
        ArrayAdapter<String> regionsAdapter = new ArrayAdapter<>(getActivity(), R.layout.rejection_item, physicalPlacesList);
        mPhysicalPlacesSpinner.setAdapter(regionsAdapter);

        mPhysicalPlacesSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                mPhysicalPlaceSelected = (String) adapterView.getItemAtPosition(i);

                try {
                    if (mOrientarionSelected.equals("Seleccionar"))
                        ((TextView) adapterView.getChildAt(0)).setTextColor(ContextCompat.getColor(getActivity(), R.color.text_hint_color));
                    else {
                        ((TextView) adapterView.getChildAt(0)).setTextColor(ContextCompat.getColor(getActivity(), R.color.text_color_gray));
                    }
                } catch (Exception ex) {

                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
    }

    private void setupStretchEditText() {
        mStretchEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (!charSequence.toString().isEmpty()) {
                    mStretches = RealmManager.getInstance(getActivity()).getStretches(charSequence.toString());

                    if (mStretchesAdapter == null) {
                        mStretchesAdapter = new StretchesAdapter(getActivity());
                        mFilteredStretchesRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
                        mFilteredStretchesRecyclerView.setAdapter(mStretchesAdapter);

                        mStretchesAdapter.setStretchListener(new StretchesAdapter.OnStretchListener() {
                            @Override
                            public void onStretchClick(Stretch report) {
                                mStretchEditText.setText(report.getName());
                                mFilteredStretchesLayout.setVisibility(View.GONE);
                                Utils.hideKeyboard(getActivity(), mStretchEditText);
                            }
                        });
                    }

                    mFilteredStretchesLayout.setVisibility(View.VISIBLE);

                    mStretchesAdapter.addAllRecords(mStretches);
                } else {
                    mFilteredStretchesLayout.setVisibility(View.GONE);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
    }

    public boolean shouldInterceptOnBackPressed() {
        if (mFilteredStretchesLayout.getVisibility() == View.VISIBLE) {
            mFilteredStretchesLayout.setVisibility(View.GONE);
            return true;
        }

        return false;
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

        Intent chooseImageIntent = ImagePicker.getPickImageIntent(getActivity(), mCurrentReport.getTicket(), "f_b_picture_" + (position + 1) + ".jpg");
        startActivityForResult(chooseImageIntent, PICK_IMAGE_ID);

    }

    private void lockButtons() {
        mButtonSend.setEnabled(false);
    }

    private void unlockButtons() {
        mButtonSend.setEnabled(true);
    }

    public interface ValidReportFragmentListener {
        void onCompletReportClick();
        void onSendReportError();
    }
}

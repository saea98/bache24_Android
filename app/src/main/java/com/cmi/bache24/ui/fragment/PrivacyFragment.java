package com.cmi.bache24.ui.fragment;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.cmi.bache24.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class PrivacyFragment extends Fragment {

    private TextView mTextInformation;
    private String mInfoType = "";

    public PrivacyFragment() {

    }

    public static PrivacyFragment newInstance(String textInfo) {
        PrivacyFragment fragment = new PrivacyFragment();

        Bundle args = new Bundle();
        args.putString("INFO_TYPE", textInfo);

        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mInfoType = getArguments().getString("INFO_TYPE", "");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_privacy, container, false);

        mTextInformation = (TextView) rootView.findViewById(R.id.textView17);

        updateText(mInfoType);

        return rootView;
    }

    public void updateText(String infoType) {
        this.mInfoType = infoType;
        if (getActivity() != null) {
            String text1 = getActivity().getResources().getString(R.string.privacy_text);
            mTextInformation.setText(text1);
        }
    }
}
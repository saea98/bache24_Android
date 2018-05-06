package com.cmi.bache24.ui.fragment;

import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.cmi.bache24.R;

/**
 * A placeholder fragment containing a simple view.
 */
public class InformationFragment extends Fragment {

    private TextView mTextInformation;
    private String mInfoType = "";

    public InformationFragment() {
    }

    public static InformationFragment newInstance(String textInfo) {
        InformationFragment fragment = new InformationFragment();

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
        View rootView = inflater.inflate(R.layout.fragment_information, container, false);

        mTextInformation = (TextView) rootView.findViewById(R.id.textView17);

        updateText(mInfoType);

        return rootView;
    }

    public void updateText(String infoType) {
        this.mInfoType = infoType;
        if (getActivity() != null) {
            if (mInfoType.equals("PRIVACY")) {
                String text1 = getActivity().getResources().getString(R.string.privacy_text);

                mTextInformation.setText(text1);
            } else if (mInfoType.equals("PROGRAM_DESCRIPTION")) {
                String text1 = getActivity().getResources().getString(R.string.program_description_1);
                String text2 = getActivity().getResources().getString(R.string.program_description_2);
                String text3 = getActivity().getResources().getString(R.string.program_description_3);
                String text4 = getActivity().getResources().getString(R.string.program_description_4);
                String text5 = getActivity().getResources().getString(R.string.program_description_5);

                mTextInformation.setText(text1 + text2 + text3 + text4 + text5);
            } else {
                mTextInformation.setText(getActivity().getResources().getString(R.string.lorem_ipsum));
            }
        }
    }
}

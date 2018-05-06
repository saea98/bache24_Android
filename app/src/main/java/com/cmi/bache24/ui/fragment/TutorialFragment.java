package com.cmi.bache24.ui.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.cmi.bache24.R;

/**
 * A placeholder fragment containing a simple view.
 */
public class TutorialFragment extends Fragment {

    private int mTutorialPosition = 0;
    private ImageView mImageTutorialPosition;
    private LinearLayout mLayoutTutorial1;
    private ScrollView mLayoutTutorial2;
    private ScrollView mLayoutTutorial3;
    private TextView mInstruction1TextView;

    public TutorialFragment() {
    }

    public static TutorialFragment newInstance(int position) {
        TutorialFragment fragment = new TutorialFragment();

        Bundle args = new Bundle();
        args.putInt("TUTORIAL_PISITION", position);

        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mTutorialPosition = getArguments().getInt("TUTORIAL_PISITION", 0);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_tutorial, container, false);

        mImageTutorialPosition = (ImageView) rootView.findViewById(R.id.imageview_tutorial);
        mLayoutTutorial1 = (LinearLayout) rootView.findViewById(R.id.layout_tutorial_1);
        mLayoutTutorial2 = (ScrollView) rootView.findViewById(R.id.scrollview_tutorial_2);
        mLayoutTutorial3 = (ScrollView) rootView.findViewById(R.id.scrollview_tutorial_3);

        mInstruction1TextView = (TextView) rootView.findViewById(R.id.textView52);

        if (mInstruction1TextView != null) {
            String text = "<font color='#5a5a5a'>" + getResources().getString(R.string.tutorial_text_12A) + "</font>" +
                    "<font color='#ec2697'> " + getResources().getString(R.string.report_button_text) + " </font>" +
                    "<font color='#5a5a5a'> " + getResources().getString(R.string.tutorial_text_12B) + "</font>";
            mInstruction1TextView.setText(Html.fromHtml(text));
        }

        mImageTutorialPosition.setVisibility(View.GONE);
        mLayoutTutorial1.setVisibility(View.GONE);
        mLayoutTutorial2.setVisibility(View.GONE);
        mLayoutTutorial3.setVisibility(View.GONE);

        switch (mTutorialPosition) {
            case 0:
                mLayoutTutorial1.setVisibility(View.VISIBLE);
                break;
            case 1:
                mLayoutTutorial2.setVisibility(View.VISIBLE);
                break;
            case 2:
                mLayoutTutorial3.setVisibility(View.VISIBLE);
                break;
            default:
                mLayoutTutorial1.setVisibility(View.VISIBLE);
                break;
        }

        return rootView;
    }
}

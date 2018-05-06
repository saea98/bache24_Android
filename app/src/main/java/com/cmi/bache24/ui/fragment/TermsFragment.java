package com.cmi.bache24.ui.fragment;


import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.cmi.bache24.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class TermsFragment extends Fragment {


    public TermsFragment() { }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_terms, container, false);

        final TextView textViewText = (TextView) rootView.findViewById(R.id.textView17);
        final TextView textViewText2 = (TextView) rootView.findViewById(R.id.textView18);
        textViewText2.setVisibility(View.INVISIBLE);

        textViewText.setVisibility(View.GONE);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                textViewText.setVisibility(View.VISIBLE);
                textViewText2.setVisibility(View.VISIBLE);
            }
        }, 250);

        return rootView;
    }

}

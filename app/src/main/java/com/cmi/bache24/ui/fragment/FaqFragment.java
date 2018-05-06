package com.cmi.bache24.ui.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListView;

import com.cmi.bache24.R;
import com.cmi.bache24.data.model.Faq;
import com.cmi.bache24.ui.adapter.FaqExpandableListAdapter;

import java.util.ArrayList;
import java.util.List;

/**
 * A placeholder fragment containing a simple view.
 */
public class FaqFragment extends Fragment {

    private ExpandableListView mExpandableListView;
    private FaqExpandableListAdapter mAdapter;
    private int mLastSelectedItem = -1;

    public FaqFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_faq, container, false);

        mExpandableListView = (ExpandableListView) rootView.findViewById(R.id.expandableListView);

        List<Faq> faqs = new ArrayList<>();

        faqs.add(new Faq(1, getActivity().getResources().getString(R.string.faq_title_1), getActivity().getResources().getString(R.string.faq_question_1)));
        faqs.add(new Faq(2, getActivity().getResources().getString(R.string.faq_title_2), getActivity().getResources().getString(R.string.faq_question_2)));
        faqs.add(new Faq(3, getActivity().getResources().getString(R.string.faq_title_3), getActivity().getResources().getString(R.string.faq_question_3)));
        faqs.add(new Faq(4, getActivity().getResources().getString(R.string.faq_title_4), getActivity().getResources().getString(R.string.faq_question_4)));
        faqs.add(new Faq(5, getActivity().getResources().getString(R.string.faq_title_5), getActivity().getResources().getString(R.string.faq_question_5)));
        faqs.add(new Faq(6, getActivity().getResources().getString(R.string.faq_title_6), getActivity().getResources().getString(R.string.faq_question_6)));

        mAdapter = new FaqExpandableListAdapter(getActivity(), faqs);
        mExpandableListView.setAdapter(mAdapter);

        mExpandableListView.setOnGroupExpandListener(new ExpandableListView.OnGroupExpandListener() {
            @Override
            public void onGroupExpand(int i) {
                if (mLastSelectedItem != -1
                        && i != mLastSelectedItem) {
                    mExpandableListView.collapseGroup(mLastSelectedItem);
                }
                mLastSelectedItem = i;
            }
        });

        return rootView;
    }
}

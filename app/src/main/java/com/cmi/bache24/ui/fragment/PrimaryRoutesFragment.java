package com.cmi.bache24.ui.fragment;


import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import com.cmi.bache24.R;
import com.cmi.bache24.data.model.Road;
import com.cmi.bache24.ui.adapter.PrimaryRoadsAdapter;
import com.cmi.bache24.util.RoadComparator;
import com.cmi.bache24.util.Utils;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class PrimaryRoutesFragment extends Fragment {

    private Context currentContext;
    private RecyclerView mPrimaryRoadsRecyclerView;
    private PrimaryRoadsAdapter mPrimaryRoadsAdapter;
    private ProgressBar mProgressBar;

    public PrimaryRoutesFragment() {

    }

    public static PrimaryRoutesFragment newInstance() {
        PrimaryRoutesFragment fragment = new PrimaryRoutesFragment();

        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_primary_routes, container, false);
        mPrimaryRoadsRecyclerView = (RecyclerView) rootView.findViewById(R.id.recylerview_primary_roads);
        mProgressBar = (ProgressBar) rootView.findViewById(R.id.progressbar);

        mProgressBar.setVisibility(View.VISIBLE);
        mPrimaryRoadsRecyclerView.setVisibility(View.GONE);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                loadPrimaryRoads();
            }
        }, 400);

        return rootView;
    }

    private void loadPrimaryRoads() {

        String jsonString = Utils.loadJSONFromAssets(currentContext, "json/vialidades.json");
        try {
            List<Road> primaryRoads = new ArrayList<>();

            Gson gson = new Gson();

            primaryRoads = gson.fromJson(jsonString, new TypeToken<List<Road>>() {
            }.getType());

            Collections.sort(primaryRoads, new RoadComparator());

            showPrimaryRoads(primaryRoads);
        } catch (Exception ex) {
            ex.printStackTrace();
        }

    }

    private void showPrimaryRoads(List<Road> primaryRoads) {
        if (mPrimaryRoadsAdapter == null) {
            mPrimaryRoadsAdapter = new PrimaryRoadsAdapter(currentContext);
        }

        mProgressBar.setVisibility(View.GONE);
        mPrimaryRoadsRecyclerView.setVisibility(View.VISIBLE);

        mPrimaryRoadsAdapter.addAllRecords(primaryRoads);
        mPrimaryRoadsRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        mPrimaryRoadsRecyclerView.setAdapter(mPrimaryRoadsAdapter);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        this.currentContext = context;
    }
}

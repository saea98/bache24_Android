package com.cmi.bache24.ui.adapter;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.cmi.bache24.R;
import com.cmi.bache24.data.model.Road;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by omar on 9/9/16.
 */
public class PrimaryRoadsAdapter extends RecyclerView.Adapter<PrimaryRoadsAdapter.PrimaryRoadViewHolder> {

    private Context mContext;
    private List<Road> mPrimaryRoads;

    public PrimaryRoadsAdapter(Context context) {
        this.mContext = context;
        this.mPrimaryRoads = new ArrayList<>();
    }

    @Override
    public PrimaryRoadViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View reportView = LayoutInflater.from(mContext).inflate(R.layout.primary_road_row, parent, false);

        return new PrimaryRoadViewHolder(reportView);
    }

    @Override
    public void onBindViewHolder(final PrimaryRoadViewHolder holder, final int position) {
        Road road = mPrimaryRoads.get(position);

        holder.setPrimaryRoad(road);
    }

    @Override
    public int getItemCount() {
        return mPrimaryRoads.size();
    }

    public void addAllRecords(List<Road> roads) {
        this.mPrimaryRoads.clear();
        this.mPrimaryRoads.addAll(roads);
        notifyDataSetChanged();
    }

    public class PrimaryRoadViewHolder extends RecyclerView.ViewHolder {

        private TextView mThoroughfareTextView;
        private LinearLayout mSectionsContainer;

        public PrimaryRoadViewHolder(View itemView) {
            super(itemView);

            mThoroughfareTextView = (TextView) itemView.findViewById(R.id.textview_thoroughfare);
            mSectionsContainer = (LinearLayout) itemView.findViewById(R.id.layout_sections);
        }

        public void setPrimaryRoad(Road primaryRoad) {
            mThoroughfareTextView.setText(primaryRoad.getThoroughfare());

            mSectionsContainer.removeAllViews();

            for (int i = 0; i < primaryRoad.getSections().length; i++) {
                TextView sectionTextView = new TextView(mContext);

                sectionTextView.setText(primaryRoad.getSections()[i]);
                sectionTextView.setTextColor(Color.parseColor("#696969"));
                sectionTextView.setLineSpacing(2, 1);
                sectionTextView.setTextSize(17);

                mSectionsContainer.addView(sectionTextView);
            }
        }
    }
}

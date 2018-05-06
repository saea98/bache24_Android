package com.cmi.bache24.ui.adapter.troop;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.cmi.bache24.R;
import com.cmi.bache24.data.model.realm.Stretch;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by omar on 5/26/16.
 */
public class StretchesAdapter extends RecyclerView.Adapter<StretchesAdapter.StretchViewHolder> {

    private Context mContext;
    private List<Stretch> mStretches;
    private OnStretchListener mStretchListener;

    public StretchesAdapter(Context context) {
        this.mContext = context;
        this.mStretches = new ArrayList<>();
    }

    @Override
    public StretchViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View reportView = LayoutInflater.from(mContext).inflate(R.layout.stretch_item, parent, false);

        return new StretchViewHolder(reportView);
    }

    @Override
    public void onBindViewHolder(StretchViewHolder holder, int position) {
        Stretch stretch = mStretches.get(position);

        holder.setStretchValue(stretch.getName());
    }

    @Override
    public int getItemCount() {
        return mStretches.size();
    }

    public void addAllRecords(List<Stretch> records) {
        this.mStretches.clear();
        this.mStretches.addAll(records);
        notifyDataSetChanged();
    }

    public void setStretchListener(OnStretchListener listener) {
        this.mStretchListener = listener;
    }

    public class StretchViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private TextView mStretchValue;

        public StretchViewHolder(View itemView) {
            super(itemView);
            itemView.setOnClickListener(this);

            mStretchValue = (TextView) itemView.findViewById(R.id.stretch_name);
        }

        public void setStretchValue(String stretchValue) {
            this.mStretchValue.setText(stretchValue);
        }

        @Override
        public void onClick(View view) {
            mStretchListener.onStretchClick(mStretches.get(getLayoutPosition()));
        }
    }

    public interface OnStretchListener {
        void onStretchClick(Stretch report);
    }
}
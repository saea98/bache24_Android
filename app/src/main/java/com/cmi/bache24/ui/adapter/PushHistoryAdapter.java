package com.cmi.bache24.ui.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.cmi.bache24.R;
import com.cmi.bache24.data.model.PushRecord;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by omar on 2/9/16.
 */
public class PushHistoryAdapter extends RecyclerView.Adapter<PushHistoryAdapter.PushRecordViewHolder> {

    private Context mContext;
    private List<PushRecord> mPushRecords;
    private DynamicHeightListener mDynamicHeightListener;

    public PushHistoryAdapter(Context context) {
        this.mContext = context;
        this.mPushRecords = new ArrayList<>();
    }

    @Override
    public PushRecordViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View reportView = LayoutInflater.from(mContext).inflate(R.layout.push_record_row, parent, false);

        return new PushRecordViewHolder(reportView);
    }

    public void setDynamicHeightListener(DynamicHeightListener listener) {
        this.mDynamicHeightListener = listener;
    }

    @Override
    public void onBindViewHolder(final PushRecordViewHolder holder, final int position) {
        PushRecord pushRecord = mPushRecords.get(position);

        holder.setPushMessage(pushRecord.getMessage());
    }

    @Override
    public int getItemCount() {
        return mPushRecords.size();
    }

    public void addAllRecords(List<PushRecord> records) {
        this.mPushRecords.clear();
        this.mPushRecords.addAll(records);
        notifyDataSetChanged();
    }

    public class PushRecordViewHolder extends RecyclerView.ViewHolder {

        private TextView mTextPushMessage;

        public PushRecordViewHolder(View itemView) {
            super(itemView);

            mTextPushMessage = (TextView) itemView.findViewById(R.id.textview_message);
        }

        public void setPushMessage(String pushMessage) {
            this.mTextPushMessage.setText(pushMessage);
        }
    }

    public interface DynamicHeightListener {
        void heightChange (int position, int height);
    }
}

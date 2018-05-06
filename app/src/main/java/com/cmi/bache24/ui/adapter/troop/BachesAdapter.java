package com.cmi.bache24.ui.adapter.troop;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import com.cmi.bache24.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by omar on 2/11/16.
 */
public class BachesAdapter extends RecyclerView.Adapter<BachesAdapter.BacheViewHolder> {

    private Context mContext;
    private List<Integer> mBachesList;
    private List<String> mMtsValues;

    public BachesAdapter(Context context) {
        this.mContext = context;
        this.mBachesList = new ArrayList<>();
        this.mMtsValues = new ArrayList<>();
    }

    @Override
    public BacheViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View reportView = LayoutInflater.from(mContext).inflate(R.layout.bache_mts_layout, parent, false);

        return new BacheViewHolder(reportView);
    }

    @Override
    public void onBindViewHolder(BacheViewHolder holder, final int position) {
        int bacheNo = mBachesList.get(position);

        holder.setNoBache(bacheNo);
        holder.mNoMtsEdit.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                mMtsValues.set(position, charSequence.toString());
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
    }

    @Override
    public int getItemCount() {
        return mBachesList.size();
    }

    public void addAllRecords(List<Integer> records) {
        this.mBachesList.clear();
        this.mBachesList.addAll(records);
        notifyDataSetChanged();

        for (int i = 0; i < this.mBachesList.size(); i++) {
            this.mMtsValues.add("");
        }
    }

    public List<String> getMtsValues() {
        return mMtsValues;
    }

    public class BacheViewHolder extends RecyclerView.ViewHolder {

        private TextView mNoBacheText;
        public EditText mNoMtsEdit;

        public BacheViewHolder(View itemView) {
            super(itemView);

            mNoBacheText = (TextView) itemView.findViewById(R.id.textview_no);
            mNoMtsEdit = (EditText) itemView.findViewById(R.id.edit_mts_repaired);
        }

        public void setNoBache(int pushMessageNumber) {
            this.mNoBacheText.setText("Bache " + pushMessageNumber);
        }
    }
}

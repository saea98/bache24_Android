package com.cmi.bache24.ui.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.cmi.bache24.R;
import com.cmi.bache24.data.model.Report;
import com.cmi.bache24.util.Constants;
import com.cmi.bache24.util.Utils;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * Created by omar on 1/29/16.
 */
public class TroopReportsAdapter extends RecyclerView.Adapter<TroopReportsAdapter.ReportViewHolder> {

    private Context mContext;
    private List<Report> mReportList;
    private OnReportsListener mReportsListener;
    private SimpleDateFormat mDateFormat;
    private SimpleDateFormat mDateFormatToShow;

    public TroopReportsAdapter(Context context) {
        this.mContext = context;
        this.mReportList = new ArrayList<>();
        mDateFormat = new SimpleDateFormat(Constants.DATE_FORMAT);
        mDateFormatToShow = new SimpleDateFormat(Constants.DATE_FORMAT_TO_SHOW);
    }

    @Override
    public ReportViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View reportView = LayoutInflater.from(mContext).inflate(R.layout.troop_report_row, parent, false);

        return new ReportViewHolder(reportView);
    }

    @Override
    public void onBindViewHolder(ReportViewHolder holder, int position) {
        Report report = mReportList.get(position);

        holder.setDefaultPicture();

        if (report.getPictures().size() > 0) {
            for (int i = 0; i < report.getPictures().size(); i++) {
                if (report.getPictures().get(i).getEtapaId() == Constants.REPORT_STATUS_NEW ||
                    report.getPictures().get(i).getEtapaId() == Constants.REPORT_STATUS_ASIGNED ||
                        report.getPictures().get(i).getEtapaId() == Constants.REPORT_STATUS_7) {
                    holder.setPicture(report.getPictures().get(i).getFoto());
                    break;
                }
            }
        }

        holder.setReportId(report.getTicket());

        holder.setDate(report.getCreationDate(), report.getEtapaId());
    }

    @Override
    public int getItemCount() {
        return mReportList.size();
    }

    public void setReportsListener(OnReportsListener listener) {
        this.mReportsListener = listener;
    }

    public void addAllSections(List<Report> reports) {
        this.mReportList.clear();
        this.mReportList.addAll(reports);
        notifyDataSetChanged();
    }

    public class ReportViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private ImageView mReportPicture;
        private TextView mTextviewReportId;
        private TextView mTextViewDate;
        private TextView mTextviewStatus;

        public ReportViewHolder(View itemView) {
            super(itemView);

            mReportPicture = (ImageView) itemView.findViewById(R.id.imageview_picture);
            mTextviewReportId = (TextView) itemView.findViewById(R.id.textview_report_id);
            mTextviewStatus = (TextView) itemView.findViewById(R.id.textview_status);
            mTextViewDate = (TextView) itemView.findViewById(R.id.textview_date);

            itemView.setOnClickListener(this);

            mTextviewReportId.setAllCaps(true);

            int squareSide = Utils.getInstance().getImagesSize(mContext);
            RelativeLayout.LayoutParams pictureParams =
                    new RelativeLayout.LayoutParams(squareSide, squareSide);

            pictureParams.setMargins(0, 0, 5, 0);

            mReportPicture.setLayoutParams(pictureParams);
        }

        public void setDefaultPicture() {
            Picasso.with(mContext).load(R.drawable.no_foto).into(mReportPicture);
        }

        public void setPicture (String location) {

//            Picasso.with(mContext).load(R.drawable.no_foto).into(mReportPicture);

            int squareSide = Utils.getInstance().getImagesSize(mContext);

            if (squareSide > 0) {
                Picasso.with(mContext)
                        .load(location)
                        .placeholder(R.drawable.no_foto)
                        .error(R.drawable.no_foto)
                        .resize(squareSide, squareSide)
                        .into(mReportPicture);
            } else {
                Picasso.with(mContext)
                        .load(location)
                        .placeholder(R.drawable.no_foto)
                        .error(R.drawable.no_foto)
                        .resize(100, 100)
                        .into(mReportPicture);
            }
        }

        public void setReportId(String reportId) {
            mTextviewReportId.setText(reportId);
        }

        public void setDate(Date date, int status) {
            if (date == null)
                date = new Date();

            Date now = new Date();

            long diffInMs = now.getTime() - date.getTime();

            long diffInSec = TimeUnit.MILLISECONDS.toSeconds(diffInMs);

            long remainingTime = 86400 - diffInSec; // 86400 Segundos == 24 Horas

            if (remainingTime <= 0) {

                if (status == Constants.REPORT_STATUS_7) {
                    mTextviewStatus.setText(mContext.getResources().getString(R.string.select_report_agu_2));
                    mTextviewStatus.setTextColor(mContext.getResources().getColor(R.color.pin_yellow));

                    mTextViewDate.setVisibility(View.GONE);
                    mTextViewDate.setText("");
                } else {
                    mTextviewStatus.setText(mContext.getResources().getString(R.string.troop_report_status_1));
                    mTextviewStatus.setTextColor(mContext.getResources().getColor(R.color.color_urgent));

                    mTextViewDate.setTextColor(mContext.getResources().getColor(R.color.color_urgent));
                    mTextViewDate.setText(mContext.getResources().getString(R.string.troop_report_timeout));
                    mTextViewDate.setVisibility(View.VISIBLE);
                }


            } else {

                if (remainingTime <= 18000) { // 5 Horas

                    if (status == Constants.REPORT_STATUS_7) {
                        mTextviewStatus.setText(mContext.getResources().getString(R.string.select_report_agu_2));
                        mTextviewStatus.setTextColor(mContext.getResources().getColor(R.color.pin_yellow));
                    } else {
                        mTextviewStatus.setText(mContext.getResources().getString(R.string.troop_report_status_1));
                        mTextviewStatus.setTextColor(mContext.getResources().getColor(R.color.color_urgent));
                    }

                    mTextViewDate.setTextColor(mContext.getResources().getColor(R.color.text_color));
                } else {
                    if (status == Constants.REPORT_STATUS_7) {
                        mTextviewStatus.setText(mContext.getResources().getString(R.string.select_report_agu_2));
                        mTextviewStatus.setTextColor(mContext.getResources().getColor(R.color.pin_yellow));
                    } else {
                        mTextviewStatus.setText(mContext.getResources().getString(R.string.troop_report_status_2));
                        mTextviewStatus.setTextColor(mContext.getResources().getColor(R.color.color_normal));
                    }
                    mTextViewDate.setTextColor(mContext.getResources().getColor(R.color.text_color));
                }

                if (status == Constants.REPORT_STATUS_7) {
                    mTextViewDate.setText("");
                    mTextViewDate.setVisibility(View.GONE);
                } else {
                    int minutes = (int) remainingTime / 3600;
                    int seconds = (int) (remainingTime / 60) % 60;

                    mTextViewDate.setText(mContext.getResources().getString(R.string.troop_report_remaining_time_1) + " "
                            + (minutes < 10 ? ("0" + minutes) : minutes) + ":" + (seconds < 10 ? ("0" + seconds) : seconds) + " "
                            + mContext.getResources().getString(R.string.troop_report_remaining_time_2));

                    mTextViewDate.setVisibility(View.VISIBLE);
                }
            }
        }

        @Override
        public void onClick(View view) {
            mReportsListener.onReportClick(mReportList.get(getLayoutPosition()));
        }
    }

    public interface OnReportsListener {
        void onReportClick(Report report);
    }
}

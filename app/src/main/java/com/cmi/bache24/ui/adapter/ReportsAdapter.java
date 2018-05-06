package com.cmi.bache24.ui.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.cmi.bache24.R;
import com.cmi.bache24.data.model.Report;
import com.cmi.bache24.util.Constants;
import com.cmi.bache24.util.PreferencesManager;
import com.cmi.bache24.util.Utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by omar on 12/1/15.
 */
public class ReportsAdapter extends RecyclerView.Adapter<ReportsAdapter.ReportViewHolder> {

    private Context mContext;
    private List<Report> mReportList;
    private OnReportsListener mReportsListener;
    private SimpleDateFormat mDateFormat;
    private SimpleDateFormat mDateFormatToShow;

    public ReportsAdapter(Context context) {
        this.mContext = context;
        this.mReportList = new ArrayList<>();
        mDateFormat = new SimpleDateFormat(Constants.DATE_FORMAT);
        mDateFormatToShow = new SimpleDateFormat(Constants.DATE_FORMAT_TO_SHOW);
    }

    @Override
    public ReportViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View reportView = LayoutInflater.from(mContext).inflate(R.layout.report_row, parent, false);

        return new ReportViewHolder(reportView);
    }

    @Override
    public void onBindViewHolder(ReportViewHolder holder, int position) {
        Report report = mReportList.get(position);

        try {

            if (report.getPictures().size() > 0) {

                if (report.getEtapaId() == Constants.REPORT_STATUS_NEW ||
                    report.getEtapaId() == Constants.REPORT_STATUS_ASIGNED) {

                    for (int i = 0; i < report.getPictures().size(); i++) {
                        if (report.getPictures().get(i).getEtapaId() == Constants.REPORT_STATUS_NEW ||
                            report.getPictures().get(i).getEtapaId() == Constants.REPORT_STATUS_ASIGNED) {

                            holder.setPicture(report.getPictures().get(i).getFoto());
                            break;
                        }
                    }

                } else {
                    int pictureIndex = -1;

                    for (int i = 0; i < report.getPictures().size(); i++) {
                        if (report.getPictures().get(i).getEtapaId() != Constants.REPORT_STATUS_NEW ||
                            report.getPictures().get(i).getEtapaId() != Constants.REPORT_STATUS_ASIGNED) {

                            pictureIndex = i;
                        }
                    }

                    if (pictureIndex > -1) {
                        holder.setPicture(report.getPictures().get(pictureIndex).getFoto());
                    }
                }
            }

        } catch (Exception ex) {

        }

        holder.setReportId(report.getTicket());
        holder.setStatus(report.isTemp(), report.getEtapaId(), report.getAvenidaId(), report.getReportVersion());

        Date date1 = null;
        try {
            date1 = mDateFormat.parse(report.getDate());
        } catch (ParseException ex) {
            date1 = new Date();
        }

        holder.setDate(date1);
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
        private TextView mTextviewStatus;
        private TextView mTextViewDate;

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

        public void setPicture (String location) {
            Glide.clear(mReportPicture);
            Glide.with(mContext)
                    .load(location)
                    .centerCrop()
                    .placeholder(R.drawable.no_foto)
                    .crossFade()
                    .into(mReportPicture);
        }

        public void setReportId(String reportId) {
            mTextviewReportId.setText(reportId);
        }

        public void setStatus(boolean isTemp, int etapaId, int avenidaId, int version) {
            if (isTemp) {
                mTextviewStatus.setText("Borrador guardado");

                mTextviewStatus.setTextColor(mContext.getResources().getColor(R.color.primary));

                return;
            }

            switch (etapaId) {
                case Constants.REPORT_STATUS_NEW: //    1  En espera de solución

                    mTextviewStatus.setText(PreferencesManager.getInstance().getStatusNameForId(mContext, etapaId));

                    mTextviewStatus.setTextColor(mContext.getResources().getColor(R.color.pin_yellow));
                    break;
                case Constants.REPORT_STATUS_ASIGNED://    2  En revisión

                    mTextviewStatus.setText(PreferencesManager.getInstance().getStatusNameForId(mContext, etapaId));

                    mTextviewStatus.setTextColor(mContext.getResources().getColor(R.color.pin_yellow));
                    break;
                case Constants.REPORT_STATUS_SOLVED://    3  Atendido
                    mTextviewStatus.setText(PreferencesManager.getInstance().getStatusNameForId(mContext, etapaId));

                    mTextviewStatus.setTextColor(mContext.getResources().getColor(R.color.primary));
                    break;
                case Constants.TROOP_REPORT_STATUS_DOES_NOT_APPLY://    4  No aplica

                    mTextviewStatus.setText(PreferencesManager.getInstance().getStatusNameForId(mContext, etapaId));

                    mTextviewStatus.setTextColor(mContext.getResources().getColor(R.color.pin_gray));
                    break;
                case Constants.TROOP_REPORT_STATUS_FALSE://    5  Bache no encontrado
                    mTextviewStatus.setText(PreferencesManager.getInstance().getStatusNameForId(mContext, etapaId));

                    mTextviewStatus.setTextColor(mContext.getResources().getColor(R.color.color_urgent));
                    break;
                case Constants.TROOP_REPORT_STATUS_WRONG://    6  No es un bache

                    mTextviewStatus.setText(PreferencesManager.getInstance().getStatusNameForId(mContext, etapaId));

                    mTextviewStatus.setTextColor(mContext.getResources().getColor(R.color.pin_gray));
                    break;
                case Constants.REPORT_STATUS_7://    7  Pendiente de solución
                    mTextviewStatus.setText(PreferencesManager.getInstance().getStatusNameForId(mContext, etapaId));

                    mTextviewStatus.setTextColor(mContext.getResources().getColor(R.color.pin_yellow));
                    break;
                default:

                    mTextviewStatus.setText(PreferencesManager.getInstance().getStatusNameForId(mContext, etapaId));

                    mTextviewStatus.setTextColor(mContext.getResources().getColor(R.color.pin_gray));
                    break;
            }
        }

        public void setDate(Date date) {
            if (date == null)
                date = new Date();

            String dateString = mDateFormatToShow.format(date);

            mTextViewDate.setText(mContext.getResources().getString(R.string.history_report_status_date) + "   " + dateString + " hrs");
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

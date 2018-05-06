package com.cmi.bache24.data.remote.interfaces;

import com.cmi.bache24.data.model.Report;

import java.util.List;

/**
 * Created by omar on 12/12/15.
 */
public interface ReportsCallback extends CallbackBase {
    void onReportsCallback(List<Report> reports);
    void onReportsFail(String message);
}

package com.cmi.bache24.data.remote.interfaces;

import com.cmi.bache24.data.model.Report;

/**
 * Created by omar on 12/6/15.
 */
public interface NewReportCallback extends CallbackBase {
    void onSuccessRegister(String resultCode, Report reportCompleteInfo);
    void onSuccessRegisterReport(Report reportCompleteInfo);
    void onFailRegisterReport(String message);
}

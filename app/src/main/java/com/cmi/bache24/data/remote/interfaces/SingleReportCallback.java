package com.cmi.bache24.data.remote.interfaces;

import com.cmi.bache24.data.model.Report;

/**
 * Created by omar on 1/26/16.
 */
public interface SingleReportCallback extends CallbackBase {

    void reportSuccess(Report report);
    void reportFail(String message);
}

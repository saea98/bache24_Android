package com.cmi.bache24.data.remote.interfaces;

import com.cmi.bache24.data.model.Rejection;

import java.util.List;

/**
 * Created by omar on 2/11/16.
 */
public interface RejectionsCallback {

    void onRejectionCallback(List<Rejection> list);
    void onFail(String message);
}

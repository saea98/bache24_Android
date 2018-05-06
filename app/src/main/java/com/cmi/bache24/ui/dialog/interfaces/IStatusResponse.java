package com.cmi.bache24.ui.dialog.interfaces;

import com.cmi.bache24.data.model.Status;

import java.util.List;

/**
 * Created by omar on 6/29/16.
 */
public interface IStatusResponse {
    void onResponse(List<Status> statusList);
}

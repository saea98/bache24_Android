package com.cmi.bache24.data.remote.interfaces;

/**
 * Created by omar on 12/5/15.
 */
public interface RecoverPasswordCallback extends CallbackBase {
    void onRecoverSuccess();
    void onRecoverFail(String message);
}

package com.cmi.bache24.data.remote.interfaces;

import com.cmi.bache24.data.model.User;

/**
 * Created by omar on 12/2/15.
 */
public interface RegisterUserCallback extends CallbackBase {
    void onRegisterSuccess(User userComplete);
    void onRegisterFail(String message);
}

package com.cmi.bache24.data.remote.interfaces;

import com.cmi.bache24.data.model.User;

/**
 * Created by omar on 12/5/15.
 */
public interface LoginCallback extends CallbackBase {
    void loginSuccess(User userInfo);
    void loginFail(String message);
}

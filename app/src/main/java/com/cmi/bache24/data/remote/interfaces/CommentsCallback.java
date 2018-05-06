package com.cmi.bache24.data.remote.interfaces;

/**
 * Created by omar on 1/31/16.
 */
public interface CommentsCallback extends CallbackBase {
    void onSendCommentsSuccess();
    void onSendCommentsFail(String message);
}

package com.cmi.bache24.ui.views;

import android.content.Context;
import android.os.Handler;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.FrameLayout;

/**
 * Created by omar on 4/11/16.
 */
public class TouchableWrapper extends FrameLayout {

    public boolean mMapIsTouched = false;
    private ActionListener mActionListener;

    public TouchableWrapper(Context context) {
        super(context);
    }

    public TouchableWrapper(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public TouchableWrapper(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {

        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN:
                mMapIsTouched = true;
                if (mActionUpHandler != null) {
                    mActionUpHandler.removeCallbacks(mActionUpRunnable);
                }

                if (mActionListener != null) {
                    mActionListener.onActionDown();
                }

                break;
            case MotionEvent.ACTION_UP:
                mMapIsTouched = false;
                if (mActionUpHandler != null) {
                    mActionUpHandler.postDelayed(mActionUpRunnable, 800);
                } else {
                    if (mActionListener != null) {
                        mActionListener.onActionUp();
                    }
                }
                break;
        }

        return super.dispatchTouchEvent(ev);
    }

    private Handler mActionUpHandler;
    private Runnable mActionUpRunnable = new Runnable() {
        @Override
        public void run() {
            if (mActionListener != null) {
                mActionListener.onActionUp();
            }
        }
    };

    public void setActionListener(ActionListener listener) {
        mActionUpHandler = new Handler();
        this.mActionListener = listener;
    }

    public interface ActionListener {
        void onActionUp();
        void onActionDown();
    }
}

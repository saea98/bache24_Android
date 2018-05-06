package com.cmi.bache24.ui.views;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.method.Touch;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.maps.MapFragment;

/**
 * Created by omar on 4/11/16.
 */
public class CustomMapFragment extends MapFragment {

    private View mOriginalContentView;
    public TouchableWrapper mTouchView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mOriginalContentView = super.onCreateView(inflater, container, savedInstanceState);

        mTouchView = new TouchableWrapper(getActivity());
        mTouchView.addView(mOriginalContentView);

        return mTouchView;
    }

    public void setActionListener(TouchableWrapper.ActionListener listener) {
        if (mTouchView != null) {
            mTouchView.setActionListener(listener);
        }
    }

    @Nullable
    @Override
    public View getView() {
        return mOriginalContentView;
    }
}

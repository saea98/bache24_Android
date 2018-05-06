package com.cmi.bache24.ui.fragment;

import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.cmi.bache24.R;
import com.squareup.picasso.Picasso;

/**
 * A placeholder fragment containing a simple view.
 */
public class ImageViewerFragment extends Fragment {

    private ImageView mBachePictureImageView;
    private String mImageUrl;

    public ImageViewerFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_image_viewer, container, false);

        mBachePictureImageView = (ImageView) rootView.findViewById(R.id.imageview_bache);

        return rootView;
    }

    public void setImageUrl(String url) {
        Picasso.with(getActivity()).load(url).into(mBachePictureImageView);
    }
}

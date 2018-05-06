package com.cmi.bache24.ui.fragment;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.cmi.bache24.R;
import com.cmi.bache24.data.model.Comment;
import com.cmi.bache24.data.model.Report;
import com.cmi.bache24.data.model.User;
import com.cmi.bache24.data.remote.ServicesManager;
import com.cmi.bache24.data.remote.interfaces.CommentsCallback;
import com.cmi.bache24.util.PreferencesManager;
import com.cmi.bache24.util.Utils;

/**
 * A placeholder fragment containing a simple view.
 */
public class ContactFragment extends Fragment implements View.OnClickListener {

    private EditText mCommentsEdit;
    private Button mSendButton;
    private View progressView;
    private User mCurrentUser;
    private ContactFragmentListener mContactFragmentListener;
    private RelativeLayout mRootLayout;

    public ContactFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_contact, container, false);

        mCommentsEdit = (EditText) rootView.findViewById(R.id.edit_comments);
        mSendButton = (Button) rootView.findViewById(R.id.button_send);
        progressView = rootView.findViewById(R.id.progress_layout);
        mRootLayout = (RelativeLayout) rootView.findViewById(R.id.root_view);

        progressView.setVisibility(View.GONE);

        mCurrentUser = PreferencesManager.getInstance().getUserInfo(getActivity());

        mSendButton.setOnClickListener(this);

        mRootLayout.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                Utils.hideKeyboard(getActivity(), view);
                return false;
            }
        });

        return rootView;
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == mSendButton.getId()) {
            sendComments();
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        try {
            if (context instanceof Activity) {
                mContactFragmentListener = (ContactFragmentListener) context;
            }
        } catch (ClassCastException ex) {

        }
    }

    private void sendComments() {
        if (mCommentsEdit.getText().toString().trim().isEmpty()) {
            Toast.makeText(getActivity(), "Agrega unos comentarios", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!Utils.getInstance().isInternetAvailable(getActivity()))
            return;

        Comment newComments = new Comment();
        newComments.setMessage(mCommentsEdit.getText().toString());
        newComments.setUser(mCurrentUser);
        newComments.setReport(new Report());

        progressView.setVisibility(View.VISIBLE);

        ServicesManager.sendComments(newComments, new CommentsCallback() {
            @Override
            public void onSendCommentsSuccess() {
                progressView.setVisibility(View.GONE);
                mCommentsEdit.setText("");
                showSuccessAlertDialog();
            }

            @Override
            public void onSendCommentsFail(String message) {
                progressView.setVisibility(View.GONE);
                if (message != "") {
                    Toast.makeText(getActivity(), message, Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void userBanned() {
                Utils.showLogin(getActivity());
            }

            @Override
            public void onTokenDisabled() {
                Utils.showLoginForBadToken(getActivity());
            }
        });
    }

    private void showSuccessAlertDialog() {
        AlertDialog.Builder dialog = new AlertDialog.Builder(getActivity());

        dialog.setTitle(getActivity().getResources().getString(R.string.comment_title));
        dialog.setMessage(getActivity().getResources().getString(R.string.comment_message));
        dialog.setPositiveButton(getActivity().getResources().getString(R.string.internet_unavailable_button), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

                if (mContactFragmentListener != null)
                    mContactFragmentListener.onSuccessMessage();
            }
        });
        dialog.show();
    }

    public interface ContactFragmentListener {
        void onSuccessMessage();
    }
}

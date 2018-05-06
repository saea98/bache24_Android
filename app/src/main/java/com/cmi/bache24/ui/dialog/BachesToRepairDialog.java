package com.cmi.bache24.ui.dialog;

import android.app.DialogFragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.cmi.bache24.R;
import com.cmi.bache24.ui.dialog.interfaces.ReportsToFixDialogListener;

/**
 * Created by omar on 1/18/16.
 */
public class BachesToRepairDialog extends DialogFragment implements View.OnClickListener {

    private ReportsToFixDialogListener mBachesToRepairDialog;
    private Button mButtonAccept;
    private Button mButtonCancel;
    private EditText mEditBachesToRepair;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.baches_to_repair_dialog_layout, container, false);
        getDialog().requestWindowFeature(Window.FEATURE_NO_TITLE);

        mButtonAccept = (Button) rootView.findViewById(R.id.button_send);
        mButtonCancel = (Button) rootView.findViewById(R.id.button_cancel);

        mEditBachesToRepair = (EditText) rootView.findViewById(R.id.edit_no_of_baches);

        mButtonAccept.setOnClickListener(this);
        mButtonCancel.setOnClickListener(this);

        return rootView;
    }

    public void setCommentsDialogListener(ReportsToFixDialogListener reportsToFixDialogListener) {
        this.mBachesToRepairDialog = reportsToFixDialogListener;
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == mButtonAccept.getId()) {
            if (mEditBachesToRepair.getText().toString().trim().isEmpty() || Integer.parseInt(mEditBachesToRepair.getText().toString()) == 0) {
                Toast.makeText(getActivity(), "Ingresa el número de baches que vas a atender", Toast.LENGTH_SHORT).show();
                return;
            }
            if (mBachesToRepairDialog != null)
                mBachesToRepairDialog.onAccept(Integer.parseInt(mEditBachesToRepair.getText().toString()));
        } else if (view.getId() == mButtonCancel.getId()) {
            if (mBachesToRepairDialog != null)
                mBachesToRepairDialog.onCancel();
        }
    }
}

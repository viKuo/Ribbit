package com.organizationiworkfor.ribbit;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.os.Bundle;

/**
 * Created by Vivien on 10/1/2015.
 */
public class AlertDialogFragment extends DialogFragment {
    private String mAlertMessage = "There is an error!";

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Context context = getActivity();
        AlertDialog.Builder builder = new AlertDialog.Builder(context)
                .setMessage(mAlertMessage)
                .setPositiveButton(android.R.string.ok, null);
        AlertDialog dialog = builder.create();
        return dialog;
    }

    public void setAlertMessage(String alertMessage) {
        mAlertMessage = alertMessage;
    }
}

package com.explodeman.castles.utils;


import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;

import com.explodeman.castles.R;

public interface IBasicDialog {

    default void showBasicDialogTwoBtn(Context context, String title, String message, DialogInterface.OnClickListener btnOkListener
            , DialogInterface.OnClickListener btnCancelListener) {

        @SuppressLint("DefaultLocale")
        AlertDialog alertDialog = new AlertDialog.Builder(context)
                .create();
        alertDialog.setMessage(message);
        alertDialog.setTitle(title);

        alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "OK",
                btnOkListener);

        alertDialog.setButton(AlertDialog.BUTTON_NEGATIVE, "CANCEL",
                btnCancelListener);

        alertDialog.setContentView(R.layout.support_simple_spinner_dropdown_item);
        alertDialog.show();
        alertDialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(Color.BLACK);
        alertDialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(Color.BLACK);

    }

    default void showBasicDialogTwoBtn(Context context, String title, String message, DialogInterface.OnClickListener btnOkListener) {
        DialogInterface.OnClickListener btnCancelListener = (dialog, which) -> dialog.dismiss();
        showBasicDialogTwoBtn(context, title, message, btnOkListener, btnCancelListener);
    }
}
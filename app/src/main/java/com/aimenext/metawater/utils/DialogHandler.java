package com.aimenext.metawater.utils;

import android.content.Context;

import androidx.appcompat.app.AlertDialog;

import com.aimenext.metawater.R;

public class DialogHandler {

    public static AlertDialog createLoadingDialog(Context context) {
        return new AlertDialog.Builder(context, R.style.CustomDialog)
                .setView(R.layout.layout_loading_dialog)
                .setCancelable(false)
                .create();
    }
}

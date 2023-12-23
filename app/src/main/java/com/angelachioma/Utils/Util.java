package com.angelachioma.Utils;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;

import com.angelachioma.R;


public class Util {
    Dialog dialog;
    public void showLoading() {
        dialog.show();
    }

    public void hideLoading() {
        dialog.dismiss();
    }

    public void initLoader(Context context) {
        dialog = new Dialog(context);
        dialog.setContentView(R.layout.dialogloading);
        dialog.setCanceledOnTouchOutside(false);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
    }
}

package com.example.sooglejay.scannews.util;

import android.app.ProgressDialog;
import android.content.Context;
import android.widget.TextView;


public class ProgressDialogUtil {
    private ProgressDialog dialog;
    private TextView tv;

    public ProgressDialogUtil(Context context) {
        initDialog(context);
    }

    public ProgressDialogUtil(Context context, boolean isCanceledOnTouchOutside) {
        initDialog(context, isCanceledOnTouchOutside);
    }

    private void initDialog(Context context) {
//        AlertDialog.Builder builder = new AlertDialog.Builder(context);
//        View view = LayoutInflater.from(context).inflate(R.layout.view_progress_bar_dialog, null, false);
//        tv = (TextView) view.findViewById(R.id.tv);
//        builder.setView(view);
//        dialog = builder.create();
//        dialog.setCancelable(false);
//        dialog.setCanceledOnTouchOutside(false);

        dialog = new ProgressDialog(context);
        dialog.setCanceledOnTouchOutside(false);
        dialog.setCancelable(false);
    }

    private void initDialog(Context context, boolean isCanceledOnTouchOutside) {
//        AlertDialog.Builder builder = new AlertDialog.Builder(context);
//        View view = LayoutInflater.from(context).inflate(R.layout.view_progress_bar_dialog, null, false);
//        tv = (TextView) view.findViewById(R.id.tv);
//        builder.setView(view);
//        dialog = builder.create();
//        dialog.setCancelable(false);
//        dialog.setCanceledOnTouchOutside(false);

        dialog = new ProgressDialog(context);
        dialog.setCanceledOnTouchOutside(isCanceledOnTouchOutside);
        dialog.setCancelable(isCanceledOnTouchOutside);
    }


    /**
     * 显示
     */
    public void show(String text) {
        if (dialog != null) {
//            tv.setText(text);
            dialog.setMessage(text);
            dialog.show();
        }
    }

    /**
     * 隐藏
     */
    public void hide() {
        if (dialog != null && dialog.isShowing()) {
            dialog.dismiss();
        }
    }
}

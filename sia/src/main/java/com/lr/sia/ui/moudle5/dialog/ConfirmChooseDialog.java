package com.lr.sia.ui.moudle5.dialog;

import android.app.Activity;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;

import com.lr.sia.R;
import com.lr.sia.utils.tool.UtilTools;

public class ConfirmChooseDialog implements View.OnClickListener {
    private OrderClickListener orderClickListener;
    private Activity activity;
    private AlertDialog dialog;
    private View view;

    public ConfirmChooseDialog(Activity activity) {
        this.activity = activity;
    }

    public void getDialog() {
        if (dialog == null) {
            dialog = new AlertDialog.Builder(activity).create();
        }
        view = View.inflate(activity,R.layout.confirm_choose,null);
        TextView tvCamera = view.findViewById(R.id.tvCamera);
        TextView tvChooseImage = view.findViewById(R.id.tvChooseImage);
        TextView tvCancel = view.findViewById(R.id.tvCancel);
        tvCamera.setOnClickListener(this);
        tvChooseImage.setOnClickListener(this);
        tvCancel.setOnClickListener(this);

        dialog.setCancelable(true);
        dialog.show();
        ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        dialog.setContentView(view, params);
        dialog.getWindow().setGravity(Gravity.CENTER);
        dialog.getWindow().setWindowAnimations(R.style.anim_menu_bottombar);
        dialog.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM);
        WindowManager.LayoutParams attributes = dialog.getWindow().getAttributes();
        // dialog显示时背景的昏暗程度
        attributes.dimAmount = 0.5f;
        attributes.width = UtilTools.getDeviceWidth(activity);
        dialog.getWindow().setAttributes(attributes);
    }

    public void setOrderClickListener(OrderClickListener orderClickListener) {
        this.orderClickListener = orderClickListener;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.tvCamera:
                if (orderClickListener != null) {
                    if (dialog.isShowing()) {
                        dialog.dismiss();
                    }
                    orderClickListener.onCamera();
                }
                break;
            case R.id.tvChooseImage:
                if (orderClickListener != null) {
                    if (dialog.isShowing()) {
                        dialog.dismiss();
                    }
                    orderClickListener.onChooseImage();
                }
                break;
            case R.id.tvCancel:
                if (dialog != null && dialog.isShowing()) {
                    dialog.dismiss();
                }
                break;
            default:
        }
    }

    public interface OrderClickListener {
        void onCamera();

        void onChooseImage();
    }
}

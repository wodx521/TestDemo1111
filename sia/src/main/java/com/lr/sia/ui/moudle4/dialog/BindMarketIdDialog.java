package com.lr.sia.ui.moudle4.dialog;

import android.app.Activity;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;

import com.lr.sia.R;
import com.lr.sia.utils.tool.UtilTools;

public class BindMarketIdDialog implements View.OnClickListener {
    private OrderClickListener orderClickListener;
    private Activity activity;
    private AlertDialog dialog;
    private View view;
    private EditText etUserId;

    public BindMarketIdDialog(Activity activity) {
        this.activity = activity;
    }

    public void getDialog() {
        if (dialog == null) {
            dialog = new AlertDialog.Builder(activity).create();
        }
        view = View.inflate(activity, R.layout.bind_market_id, null);

        etUserId = view.findViewById(R.id.etUserId);
        TextView tvConfirm = view.findViewById(R.id.tvConfirm);
        TextView tvGetId = view.findViewById(R.id.tvGetId);

        tvConfirm.setOnClickListener(this);
        tvGetId.setOnClickListener(this);

        dialog.setCancelable(false);
        dialog.show();
        ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        dialog.setContentView(view, params);
        dialog.getWindow().setGravity(Gravity.CENTER);
        dialog.getWindow().setWindowAnimations(R.style.anim_menu_bottombar);
        dialog.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM);
        WindowManager.LayoutParams attributes = dialog.getWindow().getAttributes();
        // dialog显示时背景的昏暗程度
        attributes.dimAmount = 0.5f;
        attributes.width = UtilTools.getDeviceWidth(activity) * 4 / 5;
        dialog.getWindow().setAttributes(attributes);
    }

    public void setOrderClickListener(OrderClickListener orderClickListener) {
        this.orderClickListener = orderClickListener;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.tvConfirm:
                if (orderClickListener != null) {
                    String id = etUserId.getText().toString();
                    if (!TextUtils.isEmpty(id)) {
                        if (dialog.isShowing()) {
                            dialog.dismiss();
                        }
                        orderClickListener.onBind(id);
                    } else {
                        Toast.makeText(activity,activity.getString(R.string.inputMarketId),Toast.LENGTH_SHORT).show();
                    }
                }
                break;
            case R.id.tvGetId:
                if (orderClickListener != null) {
                    if (dialog.isShowing()) {
                        dialog.dismiss();
                    }
                    orderClickListener.onGet();
                }
                break;
            default:
        }
    }

    public interface OrderClickListener {
        void onBind(String userId);

        void onGet();
    }
}

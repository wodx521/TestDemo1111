package com.lr.sia.ui.moudle5.dialog;

import android.app.Activity;
import android.os.Build;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupWindow;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.lr.sia.R;
import com.lr.sia.ui.moudle5.adapter.PhoneAreaAdapter;
import com.lr.sia.utils.tool.UtilTools;

import java.util.List;

public class PhoneAreaPopup {
    private static OnChooseContentListener mOnChooseContentListener;
    private long time = 0;
    private PopupWindow popupWindow;
    private Activity activity;
    private final PhoneAreaAdapter phoneAreaAdapter;

    public PhoneAreaPopup(Activity activity) {
        this.activity = activity;
        phoneAreaAdapter = new PhoneAreaAdapter(activity);
    }

    public void setOnChooseContentListener(OnChooseContentListener onChooseContentListener) {
        mOnChooseContentListener = onChooseContentListener;
    }

    public void getPopup(TextView textView, List<String> langList) {
        if (popupWindow == null) {
            popupWindow = new PopupWindow(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        }
        View view = View.inflate(activity, R.layout.choose_list, null);
        RecyclerView rvList = view.findViewById(R.id.rvList);

        rvList.setAdapter(phoneAreaAdapter);
        phoneAreaAdapter.setMapList(langList);
        popupWindow.setContentView(view);
        popupWindow.setFocusable(true);
        popupWindow.setOutsideTouchable(true);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            popupWindow.setBackgroundDrawable(activity.getResources().getDrawable(R.drawable.shape_gray_frame_whitebg_round5, null));
        } else {
            popupWindow.setBackgroundDrawable(activity.getResources().getDrawable(R.drawable.shape_gray_frame_whitebg_round5));
        }
        phoneAreaAdapter.setOnItemClickListener(new PhoneAreaAdapter.OnItemClickListener() {
            @Override
            public void onItemClickListener(View view, int position) {
                if (mOnChooseContentListener != null) {
                    mOnChooseContentListener.onChooseClickListener(position);
                    if (popupWindow.isShowing()) {
                        popupWindow.dismiss();
                    }
                }
            }
        });
        popupWindow.showAsDropDown(textView, 0, UtilTools.dip2px(activity, 5), Gravity.NO_GRAVITY);
    }

    public interface OnChooseContentListener {
        void onChooseClickListener(int position);
    }
}

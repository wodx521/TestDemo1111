package com.lr.sia.ui.moudle5.dialog;

import android.app.Activity;
import android.os.Build;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.PopupWindow;

import androidx.recyclerview.widget.RecyclerView;

import com.lr.sia.R;
import com.lr.sia.ui.moudle5.adapter.ChooseTimeAdapter;
import com.lr.sia.utils.tool.UtilTools;

import java.util.List;
import java.util.Map;

public class ChooseTimePopup {
    private static OnChooseContentListener mOnChooseContentListener;
    private long time = 0;
    private PopupWindow popupWindow;
    private Activity activity;

    public ChooseTimePopup(Activity activity) {
        this.activity = activity;
    }

    public void setOnChooseContentListener(OnChooseContentListener onChooseContentListener) {
        mOnChooseContentListener = onChooseContentListener;
    }

    public void getPopup(LinearLayout textView, List<Map<String, Object>> langList) {
        textView.measure(0, 0);
        int measuredWidth = textView.getMeasuredWidth();
        if (popupWindow == null) {
            popupWindow = new PopupWindow(measuredWidth, ViewGroup.LayoutParams.WRAP_CONTENT);
        }
        View view = View.inflate(activity, R.layout.choose_list, null);
        RecyclerView rvList = view.findViewById(R.id.rvList);

        ChooseTimeAdapter chooseTimeAdapter = new ChooseTimeAdapter(activity);
        rvList.setAdapter(chooseTimeAdapter);
        chooseTimeAdapter.setMapList(langList);
        popupWindow.setContentView(view);
        popupWindow.setFocusable(true);
        popupWindow.setOutsideTouchable(true);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            popupWindow.setBackgroundDrawable(activity.getResources().getDrawable(R.drawable.shape_gray_frame_whitebg_round5, null));
        } else {
            popupWindow.setBackgroundDrawable(activity.getResources().getDrawable(R.drawable.shape_gray_frame_whitebg_round5));
        }
        chooseTimeAdapter.setOnItemClickListener(new ChooseTimeAdapter.OnItemClickListener() {
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
        popupWindow.showAsDropDown(textView, (int) (measuredWidth + activity.getResources().getDimension(R.dimen.dp_10)), UtilTools.dip2px(activity, 5), Gravity.NO_GRAVITY);
    }

    public interface OnChooseContentListener {
        void onChooseClickListener(int position);
    }
}

package com.lr.sia.ui.moudle1.adapter;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.lr.sia.R;
import com.lr.sia.ui.moudle.adapter.ListBaseAdapter;


import java.text.DecimalFormat;
import java.util.Map;

public class TransferLogListAdapter extends ListBaseAdapter {

    private LayoutInflater mLayoutInflater;

    public TransferLogListAdapter(Context context) {
        mLayoutInflater = LayoutInflater.from(context);
        mContext = context;
    }

    public static String formatNumber(Object number, String pattern) {
        try {
            DecimalFormat decimalFormat = new DecimalFormat();
            decimalFormat.applyPattern(pattern);
            if (number instanceof Double) {
                return decimalFormat.format((double) number);
            } else if (number instanceof Long) {
                return decimalFormat.format((long) number);
            } else if (number instanceof String) {
                if (!TextUtils.isEmpty((String) number)) {
                    if (((String) number).contains(".")) {
                        Double aDouble = Double.valueOf((String) number);
                        return formatNumber(aDouble, pattern);
                    } else {
                        Long aLong = Long.valueOf((String) number);
                        return formatNumber(aLong, pattern);
                    }
                }
                return "0";
            } else {
                return decimalFormat.format(number);
            }
        } catch (Exception e) {
            return "0";
        }
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(mLayoutInflater.inflate(R.layout.item_transfer_log, parent, false));
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        final Map<String, Object> item = mDataList.get(position);
        ViewHolder viewHolder = (ViewHolder) holder;

        // 时间
        String createTime = item.get("create_time") + "";
        // 数量
        String coinAmount = item.get("amount") + "";
        // 币名
        String coinName = item.get("coin_name") + "";
        // 币图标
        String coinIcon = item.get("coin_icon") + "";
        String tradeAddress = item.get("trade_address") + "";
        viewHolder.tvType.setText(tradeAddress);
        viewHolder.tvMoneyRmb.setText(formatNumber(coinAmount, "#.####") + coinName);
        viewHolder.tvTime.setText(createTime);
        Glide.with(mContext)
                .load(coinIcon)
                .into(viewHolder.ivIcon);
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private ImageView ivIcon;
        private TextView tvType;
        private TextView tvTime;
        private TextView tvMoneyRmb;

        public ViewHolder(View itemView) {
            super(itemView);
            ivIcon = itemView.findViewById(R.id.iv_icon);
            tvType = itemView.findViewById(R.id.tv_type);
            tvTime = itemView.findViewById(R.id.tvTime);
            tvMoneyRmb = itemView.findViewById(R.id.tv_money_rmb);
        }
    }
}

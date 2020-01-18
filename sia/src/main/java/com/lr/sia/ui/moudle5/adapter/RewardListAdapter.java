package com.lr.sia.ui.moudle5.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.lr.sia.R;
import com.lr.sia.ui.moudle.adapter.ListBaseAdapter;

import java.util.Map;

public class RewardListAdapter extends ListBaseAdapter {

    private LayoutInflater mLayoutInflater;

    public RewardListAdapter(Context context) {
        mLayoutInflater = LayoutInflater.from(context);
        mContext = context;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new RewardListViewHolder(mLayoutInflater.inflate(R.layout.item_red_log, parent, false));
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        Map<String, Object> stringObjectMap = mDataList.get(position);
        RewardListViewHolder selectListViewHolder = (RewardListViewHolder) holder;
        String tranType = stringObjectMap.get("tran_type") + "";
        if ("5".equals(tranType)) {
            selectListViewHolder.tvId.setText("下级消费奖励");
        } else if ("6".equals(tranType)) {
            selectListViewHolder.tvId.setText("活跃度奖励");
        }
        selectListViewHolder.tvNumber.setText(stringObjectMap.get("amount") + "");
        selectListViewHolder.tvTime.setText(stringObjectMap.get("create_time") + "");
    }

    static class RewardListViewHolder extends RecyclerView.ViewHolder {
        private TextView tvId, tvNumber, tvTime;

        RewardListViewHolder(View view) {
            super(view);
            tvId = view.findViewById(R.id.tvId);
            tvNumber = view.findViewById(R.id.tvNumber);
            tvTime = view.findViewById(R.id.tvTime);
        }
    }
}

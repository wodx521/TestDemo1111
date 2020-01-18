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

public class RedLogListAdapter extends ListBaseAdapter {

    private LayoutInflater mLayoutInflater;

    public RedLogListAdapter(Context context) {
        mLayoutInflater = LayoutInflater.from(context);
        mContext = context;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new RedLogListViewHolder(mLayoutInflater.inflate(R.layout.item_red_log, parent, false));
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        Map<String, Object> stringObjectMap = mDataList.get(position);
        RedLogListViewHolder selectListViewHolder = (RedLogListViewHolder) holder;
        selectListViewHolder.tvId.setText(stringObjectMap.get("user_code") + "");
        selectListViewHolder.tvNumber.setText(stringObjectMap.get("money") + "");
        selectListViewHolder.tvTime.setText(stringObjectMap.get("time") + "");
        selectListViewHolder.tvDirection.setText(stringObjectMap.get("type") + "");
    }

    static class RedLogListViewHolder extends RecyclerView.ViewHolder {
        private TextView tvId, tvNumber, tvTime,tvDirection;

        RedLogListViewHolder(View view) {
            super(view);
            tvId = view.findViewById(R.id.tvId);
            tvNumber = view.findViewById(R.id.tvNumber);
            tvTime = view.findViewById(R.id.tvTime);
            tvDirection = view.findViewById(R.id.tvDirection);
        }
    }
}

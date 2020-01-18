package com.lr.sia.ui.moudle5.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.lr.sia.R;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class LockListAdapter extends RecyclerView.Adapter {
    private Context context;
    private LayoutInflater inflater;
    private List<Map<String, Object>> dataList;

    public LockListAdapter(Context context) {
        this.context = context;
        inflater = LayoutInflater.from(context);
    }

    public List<Map<String, Object>> getDataList() {
        if (dataList == null) {
            dataList = new ArrayList<>();
        }
        return dataList;
    }

    public void setDataList(List<Map<String, Object>> dataList) {
        this.dataList = dataList;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.item_lock_log, parent, false);
        return new LockListViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        LockListViewHolder lockListViewHolder = (LockListViewHolder) holder;
        Map<String, Object> stringObjectMap = dataList.get(position);
        String lockingAmount = stringObjectMap.get("locking_amount") + "";
        String createTime = stringObjectMap.get("create_time") + "";
        lockListViewHolder.tvLockNum.setText(lockingAmount + "SIA");
        lockListViewHolder.tvTime.setText(createTime);
    }

    @Override
    public int getItemCount() {
        if (dataList != null && dataList.size() > 0) {
            return dataList.size();
        }
        return 0;
    }

    static class LockListViewHolder extends RecyclerView.ViewHolder {
        private TextView tvLockNum, tvTime;

        public LockListViewHolder(@NonNull View itemView) {
            super(itemView);
            tvLockNum = itemView.findViewById(R.id.tvLockNum);
            tvTime = itemView.findViewById(R.id.tvTime);
        }
    }
}
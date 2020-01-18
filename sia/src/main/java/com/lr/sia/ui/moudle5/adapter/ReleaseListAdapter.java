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

public class ReleaseListAdapter extends RecyclerView.Adapter {
    private Context context;
    private LayoutInflater inflater;
    private String type;
    private List<Map<String, Object>> dataList;

    public ReleaseListAdapter(Context context, String type) {
        this.context = context;
        this.type = type;
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
        View view = inflater.inflate(R.layout.item_release_log, parent, false);
        return new LockListViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        LockListViewHolder lockListViewHolder = (LockListViewHolder) holder;
        Map<String, Object> stringObjectMap = dataList.get(position);
        String info = stringObjectMap.get("info") + "";
        String lockingAmount = stringObjectMap.get("release_amount") + "";
        String createTime = stringObjectMap.get("create_time") + "";
        if (type.equals("1")) {
            lockListViewHolder.tvLockInfo.setText(info + "SIA");
        } else if (type.equals("2")) {
            lockListViewHolder.tvLockInfo.setText(info);
        }

        lockListViewHolder.tvNumber.setText(lockingAmount + "SIA");
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
        private TextView tvLockInfo, tvTime, tvNumber;

        public LockListViewHolder(@NonNull View itemView) {
            super(itemView);
            tvLockInfo = itemView.findViewById(R.id.tvLockInfo);
            tvTime = itemView.findViewById(R.id.tvTime);
            tvNumber = itemView.findViewById(R.id.tvNumber);
        }
    }
}
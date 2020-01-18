package com.lr.sia.ui.moudle4.adapter;

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

public class PlacementLogAdapter extends RecyclerView.Adapter {
    private Context context;
    private LayoutInflater inflater;
    private List<Map<String, Object>> dataList;

    public PlacementLogAdapter(Context context) {
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
        View view = inflater.inflate(R.layout.item_placement_log, parent, false);
        return new PlacementLogViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        PlacementLogViewHolder placementLogViewHolder = (PlacementLogViewHolder) holder;
        Map<String, Object> stringObjectMap = dataList.get(position);
        String coinAmountSia = stringObjectMap.get("coin_amount_sia") + "";
        String createTime = stringObjectMap.get("create_time") + "";
        placementLogViewHolder.tvPlacementNum.setText(coinAmountSia + "SIA");
        placementLogViewHolder.tvTime.setText(createTime);
    }

    @Override
    public int getItemCount() {
        if (dataList != null && dataList.size() > 0) {
            return dataList.size();
        }
        return 0;
    }

    static class PlacementLogViewHolder extends RecyclerView.ViewHolder {
        private TextView tvPlacementNum, tvTime;

        public PlacementLogViewHolder(@NonNull View itemView) {
            super(itemView);
            tvPlacementNum = itemView.findViewById(R.id.tvPlacementNum);
            tvTime = itemView.findViewById(R.id.tvTime);
        }
    }
}

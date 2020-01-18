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

public class ChooseTimeAdapter extends RecyclerView.Adapter {
    protected Context mContext;
    private LayoutInflater inflater;
    private List<Map<String, Object>> mapList;
    private OnItemClickListener onItemClickListener;

    public ChooseTimeAdapter(Context context) {
        this.mContext = context;
        inflater = LayoutInflater.from(mContext);
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.item_choose, parent, false);
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onItemClickListener != null) {
                    onItemClickListener.onItemClickListener(v, (Integer) v.getTag());
                }
            }
        });
        return new ChooseListViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        holder.itemView.setTag(position);
        ChooseListViewHolder chooseListViewHolder = (ChooseListViewHolder) holder;
        Map<String, Object> stringObjectMap = mapList.get(position);
        chooseListViewHolder.tvChooseContent.setText(stringObjectMap.get("time_info") + "");
    }

    @Override
    public int getItemCount() {
        if (mapList != null && mapList.size() > 0) {
            return mapList.size();
        }
        return 0;
    }

    public List<Map<String, Object>> getMapList() {
        if (mapList == null) {
            mapList = new ArrayList<>();
        }
        return mapList;
    }

    public void setMapList(List<Map<String, Object>> mapList) {
        this.mapList = mapList;
    }

    /*设置点击事件*/
    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    public interface OnItemClickListener {
        void onItemClickListener(View view, int position);
    }

    static class ChooseListViewHolder extends RecyclerView.ViewHolder {
        private TextView tvChooseContent;

        public ChooseListViewHolder(@NonNull View itemView) {
            super(itemView);
            tvChooseContent = itemView.findViewById(R.id.tvChooseContent);
        }
    }

}

package com.lr.sia.ui.moudle.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.lr.sia.R;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class RecordListAdapter extends RecyclerView.Adapter {
    private final LayoutInflater mLayoutInflater;
    protected Context mContext;

    protected List<String> mDataList = new ArrayList<>();


    public RecordListAdapter(Context context) {
        mLayoutInflater = LayoutInflater.from(context);
        mContext = context;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new RecordListViewHolder(mLayoutInflater.inflate(R.layout.item_record, parent, false));
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        String s = mDataList.get(position);
        RecordListViewHolder recordListViewHolder = (RecordListViewHolder) holder;
        recordListViewHolder.tvRecord.setText(s);
    }

    @Override
    public int getItemCount() {
        return mDataList.size();
    }

    public List<String> getDataList() {
        return mDataList;
    }

    public void setDataList(Collection<String> list) {
        mDataList.clear();
        mDataList.addAll(list);
        notifyDataSetChanged();
    }

    public void addAll(Collection<String> list) {
        int lastIndex = this.mDataList.size();
        if (mDataList.addAll(list)) {
            notifyItemRangeInserted(lastIndex, list.size());
        }
    }

    public void delete(int position) {
        mDataList.remove(position);
        notifyItemRemoved(position);
    }

    public void clear() {
        mDataList.clear();
        //  notifyDataSetChanged();
    }

    static class RecordListViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.tvRecord)
        TextView tvRecord;

        public RecordListViewHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}

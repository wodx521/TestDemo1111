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

public class RecordListAdapter1 extends RecyclerView.Adapter {
    private final LayoutInflater mLayoutInflater;
    protected Context mContext;

    protected List<String> mDataList = new ArrayList<>();
    private List<String> mDataList1 = new ArrayList<>();
    private VerificationResultListener verificationResultListener;


    public RecordListAdapter1(Context context) {
        mLayoutInflater = LayoutInflater.from(context);
        mContext = context;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new RecordListViewHolder1(mLayoutInflater.inflate(R.layout.item_record1, parent, false));
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        RecordListViewHolder1 recordListViewHolder = (RecordListViewHolder1) holder;
        String s = mDataList.get(position);
        recordListViewHolder.tvRecord1.setHint(s);
        if (mDataList1.size() >= position + 1) {
            String s1 = mDataList1.get(position);
            recordListViewHolder.tvRecord1.setText(s1);
            if (s1.equals(s)) {
                if (verificationResultListener != null) {
                    verificationResultListener.onResultListener(position, true);
                }
                recordListViewHolder.tvRecord1.setBackgroundResource(R.drawable.shape_green_frame_whitebg_round);
            } else {
                if (verificationResultListener != null) {
                    verificationResultListener.onResultListener(position, false);
                }
                recordListViewHolder.tvRecord1.setBackgroundResource(R.drawable.shape_red_frame_whitebg_round);
            }
        } else {
            recordListViewHolder.tvRecord1.setText("");
            recordListViewHolder.tvRecord1.setBackgroundResource(R.drawable.shape_orange_frame_whitebg_round1);
        }
    }

    @Override
    public int getItemCount() {
        return mDataList.size();
    }

    public List<String> getDataList() {
        return mDataList;
    }

    public void setDataList(Collection<String> list, List<String> list2) {
        mDataList.clear();
        mDataList1.clear();
        mDataList.addAll(list);
        mDataList1.addAll(list2);
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

    public void setVerificationResultListener(VerificationResultListener verificationResultListener) {
        this.verificationResultListener = verificationResultListener;
    }

    public interface VerificationResultListener {
        void onResultListener(int position, boolean isRight);
    }

    static class RecordListViewHolder1 extends RecyclerView.ViewHolder {
        @BindView(R.id.tvRecord1)
        TextView tvRecord1;

        public RecordListViewHolder1(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}

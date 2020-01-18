package com.lr.sia.ui.moudle3.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.lr.sia.R;
import com.lr.sia.ui.moudle.adapter.ListBaseAdapter;
import com.lr.sia.utils.imageload.GlideApp;

import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;

public class SearchListAdapter extends ListBaseAdapter {

    private LayoutInflater mLayoutInflater;

    public SearchListAdapter(Context context) {
        mLayoutInflater = LayoutInflater.from(context);
        mContext = context;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new SelectListViewHolder(mLayoutInflater.inflate(R.layout.item_search, parent, false));
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        Map<String, Object> stringObjectMap = mDataList.get(position);
        String name = stringObjectMap.get("name") + "";
        String portrait = stringObjectMap.get("portrait") + "";
        String type = stringObjectMap.get("type") + "";
        String isFollow = stringObjectMap.get("is_follow") + "";
        SelectListViewHolder selectListViewHolder = (SelectListViewHolder) holder;
        GlideApp.with(mContext)
                .load(portrait)
                .into(selectListViewHolder.ivImage);
        selectListViewHolder.tvName.setText(name);
    }

    static class SelectListViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.ivImage)
        ImageView ivImage;
        @BindView(R.id.tvName)
        TextView tvName;

        SelectListViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }
}

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

public class MemberListAdapter extends ListBaseAdapter {

    private LayoutInflater mLayoutInflater;

    public MemberListAdapter(Context context) {
        mLayoutInflater = LayoutInflater.from(context);
        mContext = context;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new MemberListViewHolder(mLayoutInflater.inflate(R.layout.item_member_list, parent, false));
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        MemberListViewHolder memberListViewHolder = (MemberListViewHolder) holder;
        Map<String, Object> item = mDataList.get(position);
        if (position == 9) {
            memberListViewHolder.tvMore.setVisibility(View.VISIBLE);
            memberListViewHolder.ivIcon.setVisibility(View.INVISIBLE);
            memberListViewHolder.tvName.setVisibility(View.INVISIBLE);
        } else {
            memberListViewHolder.tvName.setText(item.get("name") + "");
            memberListViewHolder.tvMore.setVisibility(View.INVISIBLE);
            memberListViewHolder.tvName.setVisibility(View.VISIBLE);
            memberListViewHolder.ivIcon.setVisibility(View.VISIBLE);
            GlideApp.with(mContext)
                    .load(item.get("portrait") + "")
                    .placeholder(R.drawable.head)
                    .error(R.drawable.head)
                    .into(memberListViewHolder.ivIcon);
        }
    }

    @Override
    public int getItemCount() {
        return mDataList.size() > 10 ? 10 : mDataList.size();
    }

    static class MemberListViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.ivIcon)
        ImageView ivIcon;
        @BindView(R.id.tvName)
        TextView tvName;
        @BindView(R.id.tvMore)
        TextView tvMore;

        public MemberListViewHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

}

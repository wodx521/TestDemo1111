package com.lr.sia.ui.moudle.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.lr.sia.R;

import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;

public class HomeListAdapter extends ListBaseAdapter {


    private LayoutInflater mLayoutInflater;

    public HomeListAdapter(Context context) {
        mLayoutInflater = LayoutInflater.from(context);
        mContext = context;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(mLayoutInflater.inflate(R.layout.item_home, parent, false));
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        final Map<String, Object> item = mDataList.get(position);
        final ViewHolder viewHolder = (ViewHolder) holder;

        viewHolder.lay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /*Intent intent = new Intent(mContext, NoticeDetialActivity.class);
                intent.putExtra("DATA", (Serializable) mDataList.get(position));
                mContext.startActivity(intent);*/
            }
        });

    }


    public class ViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.iv_icon)
        ImageView ivIcon;
        @BindView(R.id.tv_type)
        TextView tvType;
        @BindView(R.id.tv_money_rmb)
        TextView tvMoneyRmb;
        @BindView(R.id.tv_money_usdt)
        TextView tvMoneyUsdt;
        @BindView(R.id.lay)
        LinearLayout lay;

        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }


}

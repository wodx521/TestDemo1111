package com.lr.sia.ui.moudle1.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.lr.sia.R;
import com.lr.sia.ui.moudle.adapter.ListBaseAdapter;
import com.lr.sia.utils.imageload.GlideApp;


import java.util.Map;

public class HomeCoinListAdapter extends ListBaseAdapter {

    private LayoutInflater mLayoutInflater;

    public HomeCoinListAdapter(Context context) {
        mLayoutInflater = LayoutInflater.from(context);
        mContext = context;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(mLayoutInflater.inflate(R.layout.item_hangqing1, parent, false));
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        final Map<String, Object> item = mDataList.get(position);
        ViewHolder viewHolder  =(ViewHolder)holder;
        // 币图片
        String coinIcon = item.get("coin_icon") + "";
        // 币符号
        String coinName = item.get("coin_name") + "";
        // 数量
        String coinAmount = item.get("coin_amount") + "";
        // 折合货币价格
        String coinTotalPrice = item.get("coin_total_price") + "";
        // 货币单位
        String curSymbol = item.get("cur_symbol") + "";
        viewHolder.tvType.setText(coinName);
        viewHolder.tvMoneyRmb.setText(coinAmount);
        viewHolder.tvMoneyUsdt.setText("≈"+curSymbol+coinTotalPrice);
        GlideApp.with(mContext)
                .load(coinIcon)
                .into(viewHolder.ivIcon);
    }


    public static class ViewHolder extends RecyclerView.ViewHolder {
        private ImageView ivIcon;
        private LinearLayout lay;
        private TextView tvType, tvMoneyRmb, tvMoneyUsdt;

        public ViewHolder(View itemView) {
            super(itemView);
            lay = itemView.findViewById(R.id.lay);
            ivIcon = itemView.findViewById(R.id.iv_icon);
            tvType = itemView.findViewById(R.id.tv_type);
            tvMoneyRmb = itemView.findViewById(R.id.tv_money_rmb);
            tvMoneyUsdt = itemView.findViewById(R.id.tv_money_usdt);
        }
    }
}

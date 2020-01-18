package com.lr.sia.ui.moudle2.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.lr.sia.R;
import com.lr.sia.ui.moudle.adapter.ListBaseAdapter;


import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;

public class HangqingListAdapter extends ListBaseAdapter {


    private LayoutInflater mLayoutInflater;

    public HangqingListAdapter(Context context) {
        mLayoutInflater = LayoutInflater.from(context);
        mContext = context;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(mLayoutInflater.inflate(R.layout.item_hangqing, parent, false));
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        final Map<String, Object> item = mDataList.get(position);
        final ViewHolder viewHolder = (ViewHolder) holder;
        // 涨跌类型
        String direction = item.get("change_type") + "";
        // 涨跌
        String change = item.get("change") + "";
        // 币图片
        String coinIcon = item.get("coin_icon") + "";
        // 币符号
        String coinName = item.get("coin_name") + "";
        // 币名称
        String coinNameOther = item.get("coin_name_other") + "";
        // 折合货币价格
        String price = item.get("price") + "";
        // usdt价格
        String priceUsdt = item.get("price_usdt") + "";
        // 货币单位
        String curSymbol = item.get("cur_symbol") + "";
        if ("1".equals(direction)) {
            // 上涨
            viewHolder.tvRate.setBackgroundResource(R.drawable.background_corners_green);
            viewHolder.tvRate.setText("+" + change);
        } else if ("0".equals(direction)) {
            // 下跌
            viewHolder.tvRate.setBackgroundResource(R.drawable.background_corners_red);
            viewHolder.tvRate.setText(change);
        }
        viewHolder.tvType.setText(coinName);
        viewHolder.tvTypeName.setText(coinNameOther);
        viewHolder.tvMoneyRmb.setText(curSymbol + price);
        viewHolder.tvMoneyUsdt.setText(priceUsdt + "USDT");
        Glide.with(mContext)
                .load(coinIcon)
                .into(viewHolder.ivIcon);

        viewHolder.lay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /*Intent intent = new Intent(mContext, NoticeDetialActivity.class);
                intent.putExtra("DATA", (Serializable) mDataList.get(position));
                mContext.startActivity(intent);*/
            }
        });

    }


    public static class ViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.iv_icon)
        ImageView ivIcon;
        @BindView(R.id.tv_type)
        TextView tvType;
        @BindView(R.id.tv_type_name)
        TextView tvTypeName;
        @BindView(R.id.tv_money_shi)
        TextView tvMoneyShi;
        @BindView(R.id.tv_money_rmb)
        TextView tvMoneyRmb;
        @BindView(R.id.tv_money_usdt)
        TextView tvMoneyUsdt;
        @BindView(R.id.tv_rate)
        TextView tvRate;
        @BindView(R.id.lay)
        LinearLayout lay;

        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}

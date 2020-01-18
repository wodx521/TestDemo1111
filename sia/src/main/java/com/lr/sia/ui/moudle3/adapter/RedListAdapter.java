package com.lr.sia.ui.moudle3.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.lr.sia.R;
import com.lr.sia.listener.OnChildClickListener;
import com.lr.sia.ui.moudle.adapter.ListBaseAdapter;
import com.lr.sia.utils.imageload.GlideUtils;
import com.lr.sia.utils.tool.LogUtilDebug;

import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;

public class RedListAdapter extends ListBaseAdapter {


    private LayoutInflater mLayoutInflater;

    private OnChildClickListener mListener;

    public void setmListener(OnChildClickListener mListener) {
        this.mListener = mListener;
    }

    public RedListAdapter(Context context) {
        mLayoutInflater = LayoutInflater.from(context);
        mContext = context;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(mLayoutInflater.inflate(R.layout.item_red_list, parent, false));
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        final Map<String, Object> item = mDataList.get(position);
        final ViewHolder viewHolder = (ViewHolder) holder;
        LogUtilDebug.i("show", "integer:" + item.get("account") + "");
        if ((item.get("type") + "").equals("0")) {
            viewHolder.shouqiTv.setVisibility(View.GONE);
        } else {
            viewHolder.shouqiTv.setVisibility(View.VISIBLE);
        }
        viewHolder.nameTv.setText(item.get("receive_user") + "");
        viewHolder.contentTv.setText(item.get("time") + "");
        viewHolder.moneyTv.setText(item.get("money") + " SIA");
        GlideUtils.loadImage(mContext, "", viewHolder.headIv, R.drawable.head);
        viewHolder.tradeLay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mListener != null) {
                    mListener.onChildClickListener(viewHolder.itemView, position, item);
                }
            }
        });
    }


    public class ViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.head_iv)
        ImageView headIv;
        @BindView(R.id.name_tv)
        TextView nameTv;
        @BindView(R.id.content_tv)
        TextView contentTv;
        @BindView(R.id.shouqi_tv)
        TextView shouqiTv;
        @BindView(R.id.money_tv)
        TextView moneyTv;

        @BindView(R.id.trade_lay)
        CardView tradeLay;

        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }


}

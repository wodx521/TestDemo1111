package com.lr.sia.ui.moudle4.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.lr.sia.R;
import com.lr.sia.ui.moudle.adapter.ListBaseAdapter;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Map;

public class NewsListAdapter1 extends ListBaseAdapter {

    private LayoutInflater mLayoutInflater;
    private SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm", Locale.CHINESE);

    public NewsListAdapter1(Context context) {
        mLayoutInflater = LayoutInflater.from(context);
        mContext = context;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(mLayoutInflater.inflate(R.layout.item_news, parent, false));
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        final Map<String, Object> item = mDataList.get(position);
        String title = item.get("title") + "";
        Map<String, Object> extra = (Map<String, Object>) item.get("extra");
        String summary = extra.get("summary") + "";
        String editTime = extra.get("published_at") + "";
        ViewHolder viewHolder = (ViewHolder) holder;
        viewHolder.tvNewsTitle.setText(title);
        viewHolder.tvNewsContent.setText(summary);
        viewHolder.tvNewsTime.setText(simpleDateFormat.format(new Date(Long.parseLong(editTime)*1000L)));
    }


    public static class ViewHolder extends RecyclerView.ViewHolder {
        private TextView tvNewsTitle;
        private TextView tvNewsContent;
        private TextView tvNewsTime;

        public ViewHolder(View itemView) {
            super(itemView);
            tvNewsTitle = itemView.findViewById(R.id.tvNewsTitle);
            tvNewsContent = itemView.findViewById(R.id.tvNewsContent);
            tvNewsTime = itemView.findViewById(R.id.tvNewsTime);
        }
    }
}

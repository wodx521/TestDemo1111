package com.lr.sia.ui.moudle4.adapter;

import android.content.Context;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.lr.sia.R;
import com.lr.sia.ui.moudle.adapter.ListBaseAdapter;

import java.util.Map;

public class NewsListAdapter extends ListBaseAdapter {

    private LayoutInflater mLayoutInflater;

    public NewsListAdapter(Context context) {
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
        String articleTitle = item.get("article_title")+"";
        String articleIntro = item.get("article_intro")+"";
        String editTime = item.get("edit_time")+"";
        ViewHolder viewHolder = (ViewHolder)holder;
        viewHolder.tvNewsTitle.setText(articleTitle);
        viewHolder.tvNewsContent.setText(Html.fromHtml(articleIntro));
        viewHolder.tvNewsTime.setText(editTime);
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

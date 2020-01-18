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
import com.lr.sia.utils.imageload.GlideUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import jp.wasabeef.glide.transformations.RoundedCornersTransformation;

public class MyFollowAdapter extends RecyclerView.Adapter {
    private Context context;
    private LayoutInflater inflater;
    private ItemClickListener itemClickListener;
    private List<Map<String, Object>> mFriendList;

    public MyFollowAdapter(Context context) {
        this.context = context;
        inflater = LayoutInflater.from(context);
    }

    public List<Map<String, Object>> getmFriendList() {
        if (mFriendList == null) {
            mFriendList = new ArrayList<>();
        }
        return mFriendList;
    }

    public void setmFriendList(List<Map<String, Object>> mFriendList) {
        this.mFriendList = mFriendList;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.item_my_recently, parent, false);
        MyFollowViewHolder myFollowViewHolder = new MyFollowViewHolder(view);
        myFollowViewHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (itemClickListener != null) {
                    itemClickListener.onItemClickListener((Integer) v.getTag());
                }
            }
        });
        return myFollowViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        holder.itemView.setTag(position);
        Map<String, Object> item = mFriendList.get(position);
        MyFollowViewHolder viewHolder = (MyFollowViewHolder) holder;
        viewHolder.nameTv.setText(item.get("rc_name") + "");
        GlideUtils.loadCustRoundCircleImage(context, item.get("rc_portrait") + "", viewHolder.headIv, RoundedCornersTransformation.CornerType.ALL);
    }

    @Override
    public int getItemCount() {
        if (mFriendList != null && mFriendList.size() > 0) {
            return mFriendList.size();
        }
        return 0;
    }

    public void setItemClickListener(ItemClickListener itemClickListener) {
        this.itemClickListener = itemClickListener;
    }

    public interface ItemClickListener {
        void onItemClickListener(int position);
    }

    static class MyFollowViewHolder extends RecyclerView.ViewHolder {
        private ImageView headIv;
        private TextView notRedTv, nameTv, contentTv, agreeTv, refuseTv, addedTv;

        public MyFollowViewHolder(@NonNull View itemView) {
            super(itemView);
            headIv = itemView.findViewById(R.id.head_iv);
            notRedTv = itemView.findViewById(R.id.not_red_tv);
            nameTv = itemView.findViewById(R.id.name_tv);
            contentTv = itemView.findViewById(R.id.content_tv);
            agreeTv = itemView.findViewById(R.id.agree_tv);
            refuseTv = itemView.findViewById(R.id.refuse_tv);
            addedTv = itemView.findViewById(R.id.added_tv);
        }
    }
}

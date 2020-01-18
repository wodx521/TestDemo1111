package com.lr.sia.ui.moudle3.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.lr.sia.R;
import com.lr.sia.utils.imageload.GlideUtils;
import com.lr.sia.utils.tool.LogUtilDebug;
import com.lr.sia.utils.tool.UtilTools;

import java.util.List;
import java.util.Map;

import jp.wasabeef.glide.transformations.RoundedCornersTransformation;

public class MyGroupAdapter extends RecyclerView.Adapter {
    private Context context;
    private LayoutInflater inflater;
    private ItemClickListener itemClickListener;
    private List<Map<String, Object>> mGroupList;

    public MyGroupAdapter(Context context) {
        this.context = context;
        inflater = LayoutInflater.from(context);
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.item_my_recently, parent, false);
        MyGroupViewHolder myGroupViewHolder = new MyGroupViewHolder(view);
        myGroupViewHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (itemClickListener != null) {
                    itemClickListener.onItemClickListener((Integer) v.getTag());
                }
            }
        });
        return myGroupViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        holder.itemView.setTag(position);
        Map<String, Object> item = mGroupList.get(position);
        MyGroupViewHolder viewHolder = (MyGroupViewHolder) holder;
//        private ImageView headIv;
//        private TextView notRedTv, nameTv, contentTv, agreeTv, refuseTv, addedTv;
        viewHolder.nameTv.setText(item.get("name") + "");
        GlideUtils.loadCustRoundCircleImage(context, item.get("portrait") + "", viewHolder.headIv, RoundedCornersTransformation.CornerType.ALL);
    }

    @Override
    public int getItemCount() {
        if (mGroupList != null && mGroupList.size() > 0) {
            return mGroupList.size();
        }
        return 0;
    }

    public void setmFriendList(List<Map<String, Object>> mFriendList) {
        this.mGroupList = mFriendList;
        notifyDataSetChanged();
    }

    public void setItemClickListener(ItemClickListener itemClickListener) {
        this.itemClickListener = itemClickListener;
    }

    public interface ItemClickListener {
        void onItemClickListener(int position);
    }

    static class MyGroupViewHolder extends RecyclerView.ViewHolder {
        private ImageView headIv;
        private TextView notRedTv, nameTv, contentTv, agreeTv, refuseTv, addedTv;
        private CardView tradeLay;

        public MyGroupViewHolder(@NonNull View itemView) {
            super(itemView);
            tradeLay = itemView.findViewById(R.id.trade_lay);
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

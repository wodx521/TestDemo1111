package com.lr.sia.ui.moudle5.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.lr.sia.R;

import com.lr.sia.utils.imageload.GlideApp;
import com.lr.sia.utils.tool.UtilTools;

import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import jp.wasabeef.glide.transformations.RoundedCornersTransformation;

public class ChatLogAdapter extends RecyclerView.Adapter {
    private static final int LEFT_INFO = 1;
    private static final int RIGHT_INFO = 2;
    private SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.CHINESE);
    private LayoutInflater inflater;
    private List<Map<String, Object>> list;
    private Context mContext;

    public ChatLogAdapter(Context context) {
        this.mContext = context;
        inflater = LayoutInflater.from(mContext);
    }

    public void setList(List<Map<String, Object>> list) {
        this.list = list;
        notifyDataSetChanged();
    }

    public List<Map<String, Object>> getList() {
        if (list==null) {
            list = new ArrayList<>();
        }
        return list;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view;
        if (i == RIGHT_INFO) {
            view = inflater.inflate(R.layout.item_chat_right_view, viewGroup, false);
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (onItemClickListener!=null) {
                        onItemClickListener.onItemClick((Integer) v.getTag());
                    }
                }
            });
            return new ChatRightViewHolder(view);
        } else {
            view = inflater.inflate(R.layout.item_chat_left_view, viewGroup, false);
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (onItemClickListener!=null) {
                        onItemClickListener.onItemClick((Integer) v.getTag());
                    }
                }
            });
            return new ChatLeftViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int i) {
        viewHolder.itemView.setTag(i);
        int itemViewType = getItemViewType(i);
        Map<String, Object> contentList = list.get(i);
        // 头像
        String avatar = contentList.get("avatar") + "";
        String content = contentList.get("content") + "";
        String time = contentList.get("time") + "";
        Date parse = simpleDateFormat.parse(time, new ParsePosition(0));
        if (i > 1) {
            Map<String, Object> lastContentList = list.get(i - 1);
            String lastTime = lastContentList.get("time") + "";
            Date lastDate = simpleDateFormat.parse(lastTime, new ParsePosition(0));
            long lastTimeSecond = lastDate.getTime();
            long currentSecond = parse.getTime();
            if (currentSecond - lastTimeSecond > 300000) {
                if (itemViewType == LEFT_INFO) {
                    ChatLeftViewHolder chatLeftViewHolder = (ChatLeftViewHolder) viewHolder;
                    chatLeftViewHolder.tvTime.setVisibility(View.VISIBLE);
                }else{
                    ChatRightViewHolder chatRightViewHolder = (ChatRightViewHolder) viewHolder;
                    chatRightViewHolder.tvTime.setVisibility(View.VISIBLE);
                }
            }else{
                if (itemViewType == LEFT_INFO) {
                    ChatLeftViewHolder chatLeftViewHolder = (ChatLeftViewHolder) viewHolder;
                    chatLeftViewHolder.tvTime.setVisibility(View.GONE);
                }else{
                    ChatRightViewHolder chatRightViewHolder = (ChatRightViewHolder) viewHolder;
                    chatRightViewHolder.tvTime.setVisibility(View.GONE);
                }
            }
        }


        String imageUrl;
        if (itemViewType == LEFT_INFO) {
            ChatLeftViewHolder chatLeftViewHolder = (ChatLeftViewHolder) viewHolder;
//            if (type == 2) {
//                if (content.contains("http://")) {
//                    imageUrl = content;
//                } else {
//                    imageUrl = "http://" + content;
//                }
//                Glide.with(mContext)
//                        .load(imageUrl)
//                        .error(R.drawable.load_image_error)
//                        .into(chatLeftViewHolder.ivChatImage);
//                chatLeftViewHolder.tvFrom.setVisibility(View.GONE);
//                chatLeftViewHolder.ivChatImage.setVisibility(View.VISIBLE);
//            } else {
            chatLeftViewHolder.ivChatImage.setVisibility(View.GONE);
            chatLeftViewHolder.tvFrom.setVisibility(View.VISIBLE);
            chatLeftViewHolder.tvFrom.setText(content);
            chatLeftViewHolder.tvTime.setText(time);
            GlideApp.with(mContext)
                    .load(avatar)
                    .transform(new RoundedCornersTransformation(UtilTools.dip2px(mContext, 35), 2))
                    .placeholder(R.drawable.head)
                    .error(R.drawable.head)
                    .into(chatLeftViewHolder.ivFrom);
//            }
        } else {
            ChatRightViewHolder chatRightViewHolder = (ChatRightViewHolder) viewHolder;
//            if (type == 2) {
//                if (content.contains("http://")) {
//                    imageUrl = content;
//                } else {
//                    imageUrl = "http://" + content;
//                }
//                Glide.with(mContext)
//                        .load(imageUrl)
//                        .error(R.drawable.load_image_error)
//                        .into(chatRightViewHolder.ivChatImage);
//                chatRightViewHolder.ivChatImage.setVisibility(View.VISIBLE);
//                chatRightViewHolder.tvFrom.setVisibility(View.GONE);
//            } else {
            chatRightViewHolder.ivChatImage.setVisibility(View.GONE);
            chatRightViewHolder.tvFrom.setVisibility(View.VISIBLE);
            chatRightViewHolder.tvFrom.setText(content);
            chatRightViewHolder.tvTime.setText(time);
            GlideApp.with(mContext)
                    .load(avatar)
                    .transform(new RoundedCornersTransformation(UtilTools.dip2px(mContext, 35), 2))
                    .placeholder(R.drawable.head)
                    .error(R.drawable.head)
                    .into(chatRightViewHolder.ivFrom);
//            }
        }
    }

    @Override
    public int getItemViewType(int position) {
        Map<String, Object> stringObjectMap = list.get(position);
        String adminId = stringObjectMap.get("admin_id") + "";
        if ("0".equals(adminId)) {
            return RIGHT_INFO;
        } else {
            return LEFT_INFO;
        }
    }

    @Override
    public int getItemCount() {
        if (list != null && list.size() > 0) {
            return list.size();
        }
        return 0;
    }

    static class ChatLeftViewHolder extends RecyclerView.ViewHolder {
        private ImageView ivChatImage, ivFrom;
        private TextView tvFrom, tvTime;

        public ChatLeftViewHolder(@NonNull View itemView) {
            super(itemView);
            tvFrom = itemView.findViewById(R.id.tvFrom);
            tvTime = itemView.findViewById(R.id.tvTime);
            ivChatImage = itemView.findViewById(R.id.ivChatImage);
            ivFrom = itemView.findViewById(R.id.ivFrom);
        }
    }

    static class ChatRightViewHolder extends RecyclerView.ViewHolder {
        private TextView tvFrom, tvTime;
        private ImageView ivChatImage;
        private ImageView ivFrom;

        public ChatRightViewHolder(@NonNull View itemView) {
            super(itemView);
            tvFrom = itemView.findViewById(R.id.tvFrom);
            tvTime = itemView.findViewById(R.id.tvTime);
            ivFrom = itemView.findViewById(R.id.ivFrom);
            ivChatImage = itemView.findViewById(R.id.ivChatImage);
        }
    }

    public interface OnItemClickListener{
        void onItemClick(int position);
    }
    private OnItemClickListener onItemClickListener;

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }
}

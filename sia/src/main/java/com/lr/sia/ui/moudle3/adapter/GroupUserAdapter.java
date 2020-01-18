package com.lr.sia.ui.moudle3.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.lr.sia.R;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class GroupUserAdapter extends RecyclerView.Adapter {
    private Context context;
    private LayoutInflater inflater;
    private List<Map<String, Object>> userList;
    private ItemClickListener itemClickListener;

    public GroupUserAdapter(Context context) {
        this.context = context;
        inflater = LayoutInflater.from(context);
    }

    public List<Map<String, Object>> getUserList() {
        if (userList == null) {
            userList = new ArrayList<>();
        }
        return userList;
    }

    public void setUserList(List<Map<String, Object>> userList) {
        this.userList = userList;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.item_user, parent, false);
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (itemClickListener != null) {
                    itemClickListener.onItemClickListener((int) view.getTag());
                }
            }
        });
        return new GroupUserViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        holder.itemView.setTag(position);
        GroupUserViewHolder groupUserViewHolder = (GroupUserViewHolder) holder;
        Map<String, Object> stringObjectMap = userList.get(position);
        String name = stringObjectMap.get("name") + "";
        String portrait = stringObjectMap.get("portrait") + "";
        Glide.with(context)
                .load(portrait)
                .into(groupUserViewHolder.profileIvGridMemberAvatar);
//        ImageLoaderUtils.displayUserPortraitImage(portrait, groupUserViewHolder.profileIvGridMemberAvatar);
        groupUserViewHolder.profileIvGridTvUsername.setText(name);
    }

    @Override
    public int getItemCount() {
        if (userList != null && userList.size() > 0) {
            return userList.size();
        }
        return 0;
    }

    public void setItemClickListener(ItemClickListener itemClickListener) {
        this.itemClickListener = itemClickListener;
    }

    public interface ItemClickListener {
        void onItemClickListener(int position);
    }

    static class GroupUserViewHolder extends RecyclerView.ViewHolder {
        private CircleImageView profileIvGridMemberAvatar;
        private TextView profileIvGridTvUsername;

        public GroupUserViewHolder(@NonNull View itemView) {
            super(itemView);
            profileIvGridMemberAvatar = itemView.findViewById(R.id.profile_iv_grid_member_avatar);
            profileIvGridTvUsername = itemView.findViewById(R.id.profile_iv_grid_tv_username);
        }
    }
}

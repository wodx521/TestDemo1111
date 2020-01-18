package com.lr.sia.ui.moudle3.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.lr.sia.R;
import com.lr.sia.api.MethodUrl;
import com.lr.sia.basic.BasicFragment;
import com.lr.sia.basic.MbsConstans;
import com.lr.sia.ui.moudle.activity.LoginActivity1;
import com.lr.sia.ui.moudle3.adapter.MyFollowAdapter;
import com.lr.sia.utils.tool.SPUtils;
import com.lr.sia.utils.tool.UtilTools;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cn.wildfire.chat.kit.conversation.ConversationActivity;
import cn.wildfirechat.model.Conversation;

public class MyFollowFragment extends BasicFragment {
    private RecyclerView rvList;
    private LinearLayout pageViewEmpty;
    private String mRequestTag;
    private List<Map<String, Object>> mFriendList;
    private MyFollowAdapter myFollowAdapter;

    @Override
    public int getLayoutId() {
        return R.layout.fragment_my_follow;
    }

    @Override
    public void init() {
        showProgressDialog();

    }

    @Override
    public void showProgress() {

    }

    @Override
    public void disimissProgress() {

    }

    @Override
    public void loadDataSuccess(Map<String, Object> tData, String mType) {
        Intent intent;
        switch (mType) {
            case MethodUrl.CHAT_MY_FRIENDS:
                dismissProgressDialog();
                switch (tData.get("code") + "") {
                    case "1": //请求成功
                        if (UtilTools.empty(tData.get("data") + "")) {
                            pageViewEmpty.setVisibility(View.VISIBLE);
                        } else {
                            Map<String, Object> dataMap = (Map<String, Object>) tData.get("data");
                            mFriendList = (List<Map<String, Object>>) dataMap.get("data");
                            if (UtilTools.empty(mFriendList)) {
                                pageViewEmpty.setVisibility(View.VISIBLE);
                            } else {
                                pageViewEmpty.setVisibility(View.GONE);
                                myFollowAdapter.setmFriendList(mFriendList);
                            }
                        }
                        break;
                    case "0": //请求失败
                        showToastMsg(tData.get("msg") + "");
                        break;
                    case "-1": //token过期
                        if (getActivity() != null) {
                            getActivity().finish();
                        }
                        intent = new Intent(getActivity(), LoginActivity1.class);
                        startActivity(intent);
                        break;
                    default:
                }
                break;
            default:
        }
    }

    @Override
    public void loadDataError(Map<String, Object> map, String mType) {

    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if (!hidden && getUserVisibleHint()) {
            getMyFriendsAction();
        }
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        rvList = view.findViewById(R.id.rvList);
        pageViewEmpty = view.findViewById(R.id.page_view_empty);
        myFollowAdapter = new MyFollowAdapter(getActivity());
        rvList.setAdapter(myFollowAdapter);
        myFollowAdapter.setItemClickListener(new MyFollowAdapter.ItemClickListener() {
            @Override
            public void onItemClickListener(int position) {
                List<Map<String, Object>> mapList = myFollowAdapter.getmFriendList();
                if (mapList.size() > 0) {
                    Map<String, Object> stringObjectMap = mapList.get(position);
                    String rcId = stringObjectMap.get("rc_id") + "";
                    Intent intent = new Intent(getActivity(), ConversationActivity.class);
                    Conversation conversation = new Conversation(Conversation.ConversationType.Single, rcId, 0);
                    intent.putExtra("conversation", conversation);
                    startActivity(intent);
                    getActivity().finish();
                }
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        getMyFriendsAction();
    }

    private void getMyFriendsAction() {
        mRequestTag = MethodUrl.CHAT_MY_FRIENDS;
        Map<String, Object> map = new HashMap<>();
        if (UtilTools.empty(MbsConstans.ACCESS_TOKEN)) {
            MbsConstans.ACCESS_TOKEN = SPUtils.get(getActivity(), MbsConstans.SharedInfoConstans.ACCESS_TOKEN, "").toString();
        }
        map.put("token", MbsConstans.ACCESS_TOKEN);
        mRequestPresenterImp.requestPostToMap(MethodUrl.CHAT_MY_FRIENDS, map);
    }
}

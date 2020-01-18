package com.lr.sia.ui.moudle3.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.core.content.ContextCompat;

import com.github.jdsjlzx.recyclerview.LRecyclerView;
import com.github.jdsjlzx.recyclerview.LRecyclerViewAdapter;
import com.jaeger.library.StatusBarUtil;
import com.lr.sia.R;
import com.lr.sia.api.MethodUrl;
import com.lr.sia.basic.BasicActivity;
import com.lr.sia.basic.MbsConstans;
import com.lr.sia.mywidget.view.LoadingWindow;
import com.lr.sia.ui.moudle.activity.LoginActivity1;
import com.lr.sia.ui.moudle3.adapter.MemberListAdapter;
import com.lr.sia.utils.tool.SPUtils;
import com.lr.sia.utils.tool.UtilTools;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class GroupTeamActivity extends BasicActivity {

    @BindView(R.id.left_back_lay)
    LinearLayout leftBackLay;
    @BindView(R.id.title_text)
    TextView titleText;
    @BindView(R.id.tvGroupName)
    TextView tvGroupName;
    @BindView(R.id.rvGroupList)
    LRecyclerView rvGroupList;
    @BindView(R.id.tvGroupNum)
    TextView tvGroupNum;
    @BindView(R.id.tvAddGroup)
    TextView tvAddGroup;
    private String mRequestTag;
    private String userId;
    private LoadingWindow mLoadingWindow;
    private MemberListAdapter memberListAdapter;
    private LRecyclerViewAdapter lRecyclerViewAdapter;
    private String groupName;
    private String groupId;

    @Override
    public void showProgress() {
        showProgressDialog();
    }

    @Override
    public void disimissProgress() {
        dismissProgressDialog();
    }

    @Override
    public void loadDataSuccess(Map<String, Object> tData, String mType) {
        mLoadingWindow.cancleView();
        Intent intent;
        switch (mType) {
            case MethodUrl.CHAT_GROUP_GETGROUPINFO:
                switch (tData.get("code") + "") {
                    case "1": //请求成功
                        Map<String, Object> data = (Map<String, Object>) tData.get("data");
                        groupName = data.get("name") + "";
                        groupId = data.get("id") + "";
                        tvGroupName.setText(groupName);
                        String groupNum = data.get("member_amount") + "";
                        tvGroupNum.setText(getString(R.string.groupNum).replace("0", groupNum));
                        List<Map<String, Object>> memberList = (List<Map<String, Object>>) data.get("member");
                        memberListAdapter.setDataList(memberList);
                        break;
                    case "0": //请求失败
                        showToastMsg(tData.get("msg") + "");
                        break;
                    case "-1": //token过期
                        showToastMsg(tData.get("msg") + "");
                        finish();
                        intent = new Intent(GroupTeamActivity.this, LoginActivity1.class);
                        startActivity(intent);
                        break;
                    default:
                }
                break;
            case MethodUrl.CHAT_GROUP_ADDCHATGROUP:
                switch (tData.get("code") + "") {
                    case "1": //请求成功
                        SPUtils.put(GroupTeamActivity.this, "friendId", userId);
                        SPUtils.put(GroupTeamActivity.this, "isGroup", true);

                        break;
                    case "0": //请求失败
                        showToastMsg(tData.get("msg") + "");
                        break;
                    case "-1": //token过期
                        showToastMsg(tData.get("msg") + "");
                        finish();
                        intent = new Intent(GroupTeamActivity.this, LoginActivity1.class);
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
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // TODO: add setContentView(...) invocation
        ButterKnife.bind(this);
    }

    @Override
    public int getContentView() {
        return R.layout.activity_group_team;
    }

    @Override
    public void init() {
        mLoadingWindow = new LoadingWindow(this, R.style.Dialog);
        StatusBarUtil.setColorForSwipeBack(this, ContextCompat.getColor(this, MbsConstans.TOP_BAR_COLOR), MbsConstans.ALPHA);
        userId = getIntent().getStringExtra("userId");
        getGroupInfoAction();
        titleText.setCompoundDrawablesRelativeWithIntrinsicBounds(0, 0, 0, 0);
        memberListAdapter = new MemberListAdapter(GroupTeamActivity.this);
        lRecyclerViewAdapter = new LRecyclerViewAdapter(memberListAdapter);
        rvGroupList.setAdapter(lRecyclerViewAdapter);
    }

    private void getGroupInfoAction() {
        mRequestTag = MethodUrl.CHAT_GROUP_GETGROUPINFO;
        Map<String, Object> map = new HashMap<>();
        if (UtilTools.empty(MbsConstans.ACCESS_TOKEN)) {
            MbsConstans.ACCESS_TOKEN = SPUtils.get(GroupTeamActivity.this, MbsConstans.SharedInfoConstans.ACCESS_TOKEN, "").toString();
        }
        map.put("token", MbsConstans.ACCESS_TOKEN);
        map.put("id", userId);
        mRequestPresenterImp.requestPostToMap(MethodUrl.CHAT_GROUP_GETGROUPINFO, map);
    }

    @OnClick({R.id.left_back_lay, R.id.tvAddGroup})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.left_back_lay:
                finish();
                break;
            case R.id.tvAddGroup:
                joinGroupAction();
                break;
            default:
        }
    }

    private void joinGroupAction() {
        mRequestTag = MethodUrl.CHAT_GROUP_ADDCHATGROUP;
        Map<String, Object> map = new HashMap<>();
        if (UtilTools.empty(MbsConstans.ACCESS_TOKEN)) {
            MbsConstans.ACCESS_TOKEN = SPUtils.get(GroupTeamActivity.this, MbsConstans.SharedInfoConstans.ACCESS_TOKEN, "").toString();
        }
        map.put("token", MbsConstans.ACCESS_TOKEN);
        map.put("id", userId);
        mRequestPresenterImp.requestPostToMap(MethodUrl.CHAT_GROUP_ADDCHATGROUP, map);
    }
}

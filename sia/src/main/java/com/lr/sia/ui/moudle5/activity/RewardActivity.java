package com.lr.sia.ui.moudle5.activity;

import android.content.Intent;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.github.jdsjlzx.interfaces.OnLoadMoreListener;
import com.github.jdsjlzx.interfaces.OnRefreshListener;
import com.github.jdsjlzx.recyclerview.LRecyclerView;
import com.github.jdsjlzx.recyclerview.LRecyclerViewAdapter;
import com.lr.sia.R;
import com.lr.sia.api.MethodUrl;
import com.lr.sia.basic.BasicActivity;
import com.lr.sia.basic.MbsConstans;
import com.lr.sia.ui.moudle.activity.LoginActivity1;
import com.lr.sia.ui.moudle5.adapter.RewardListAdapter;
import com.lr.sia.utils.tool.SPUtils;
import com.lr.sia.utils.tool.UtilTools;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RewardActivity extends BasicActivity implements View.OnClickListener {
    private ImageView backImg;
    private TextView tvTitle;
    private LRecyclerView lrvList;
    private RewardListAdapter rewardListAdapter;
    private LRecyclerViewAdapter lRecyclerViewAdapter;
    private int page = 1;
    private List<Map<String, Object>> tempDataList = new ArrayList<>();

    @Override
    public int getContentView() {
        return R.layout.activity_reward;
    }

    @Override
    public void init() {
        backImg = findViewById(R.id.backImg);
        tvTitle = findViewById(R.id.tvTitle);
        lrvList = findViewById(R.id.lrvList);

        backImg.setOnClickListener(this);

        tvTitle.setText(R.string.rewardLog);

        rewardListAdapter = new RewardListAdapter(RewardActivity.this);
        lRecyclerViewAdapter = new LRecyclerViewAdapter(rewardListAdapter);
        lrvList.setAdapter(lRecyclerViewAdapter);
        getUserInfoAction();
        lrvList.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh() {
                page = 1;
                tempDataList.clear();
                getUserInfoAction();
            }
        });

        lrvList.setOnLoadMoreListener(new OnLoadMoreListener() {
            @Override
            public void onLoadMore() {
                page += 1;
                getUserInfoAction();
            }
        });
    }

    private void getUserInfoAction() {
        Map<String, Object> map = new HashMap<>();
        if (UtilTools.empty(MbsConstans.ACCESS_TOKEN)) {
            MbsConstans.ACCESS_TOKEN = SPUtils.get(MbsConstans.SharedInfoConstans.ACCESS_TOKEN, "").toString();
        }
        map.put("token", MbsConstans.ACCESS_TOKEN);
        map.put("page", page);
        mRequestPresenterImp.requestPostToMap(MethodUrl.USER_GETUSERREWARDLIST, map);
    }

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
        dismissProgressDialog();
        Intent intent;
        switch (mType) {
            case MethodUrl.USER_GETUSERREWARDLIST:
                lrvList.refreshComplete(15);
                lRecyclerViewAdapter.notifyDataSetChanged();
                switch (tData.get("code") + "") {
                    case "1": //请求成功
                        Map<String, Object> dataMap = (Map<String, Object>) tData.get("data");
                        List<Map<String, Object>> dataList = (List<Map<String, Object>>) dataMap.get("data");
                        tempDataList.addAll(dataList);
                        rewardListAdapter.setDataList(tempDataList);
                        break;
                    case "0": //请求失败
                        showToastMsg(tData.get("msg") + "");
                        break;
                    case "-1": //token过期
                        finish();
                        intent = new Intent(RewardActivity.this, LoginActivity1.class);
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
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.backImg:
                finish();
                break;
            default:
        }
    }
}

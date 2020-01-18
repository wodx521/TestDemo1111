package com.lr.sia.ui.moudle5.activity;

import android.content.Intent;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.github.jdsjlzx.recyclerview.LRecyclerView;
import com.github.jdsjlzx.recyclerview.LRecyclerViewAdapter;
import com.lr.sia.R;
import com.lr.sia.api.MethodUrl;
import com.lr.sia.basic.BasicActivity;
import com.lr.sia.basic.MbsConstans;
import com.lr.sia.ui.moudle.activity.LoginActivity1;
import com.lr.sia.ui.moudle5.adapter.RedLogListAdapter;
import com.lr.sia.utils.tool.SPUtils;
import com.lr.sia.utils.tool.UtilTools;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.constant.RefreshState;
import com.scwang.smartrefresh.layout.listener.OnRefreshLoadMoreListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RedLogActivity extends BasicActivity implements View.OnClickListener {
    private ImageView backImg;
    private RadioGroup rgChoose;
    private RadioButton rbSend;
    private RadioButton rbReceive;
    private TextView tvListTitle1;
    private LRecyclerView lrvList;
    private String type = "1";
    private LRecyclerViewAdapter lRecyclerViewAdapter;
    private RedLogListAdapter redLogListAdapter;
    private String requestTag;
    private int page = 1;
    private List<Map<String, Object>> tempList = new ArrayList<>();
    private LinearLayout pageViewEmpty;
    private SmartRefreshLayout srlRefresh2;

    @Override
    public int getContentView() {
        return R.layout.activity_red_log;
    }

    @Override
    public void init() {
        backImg = findViewById(R.id.backImg);
        rgChoose = findViewById(R.id.rgChoose);
        rbSend = findViewById(R.id.rbSend);
        rbReceive = findViewById(R.id.rbReceive);
        tvListTitle1 = findViewById(R.id.tvListTitle1);
        lrvList = findViewById(R.id.lrvList);
        pageViewEmpty = findViewById(R.id.page_view_empty);
        srlRefresh2 = findViewById(R.id.srlRefresh2);
        tvListTitle1.setText(R.string.recivedId);
        backImg.setOnClickListener(this);
        rbSend.setOnClickListener(this);
        rbReceive.setOnClickListener(this);
        pageViewEmpty.setOnClickListener(this);

        redLogListAdapter = new RedLogListAdapter(RedLogActivity.this);
        lRecyclerViewAdapter = new LRecyclerViewAdapter(redLogListAdapter);
        lrvList.setAdapter(lRecyclerViewAdapter);
        lrvList.setPullRefreshEnabled(false);
        lrvList.setLoadMoreEnabled(false);
        rgChoose.check(R.id.rbSend);
        String logType = getIntent().getStringExtra("logType");
        if (!TextUtils.isEmpty(logType)) {
            if ("1".equals(logType)) {
                requestTag = MethodUrl.CHAT_RED_MYHONGBAOLOG;
            } else if ("2".equals(logType)) {
                requestTag = MethodUrl.TRANSFER_MYCHATTRANSLOG;
            }
            getUserInfoAction();
        }
        srlRefresh2.setOnRefreshLoadMoreListener(new OnRefreshLoadMoreListener() {
            @Override
            public void onLoadMore(@NonNull RefreshLayout refreshLayout) {
                page += 1;
                if (rbSend.isChecked()) {
                    requestTag = MethodUrl.TRANSFER_MYCHATTRANSLOG;
                    getUserInfoAction();
                } else if (rbReceive.isChecked()) {
                    requestTag = MethodUrl.CHAT_RED_MYHONGBAOLOG;
                    getUserInfoAction();
                }
            }

            @Override
            public void onRefresh(@NonNull RefreshLayout refreshLayout) {
                page = 1;
                if (rbSend.isChecked()) {
                    requestTag = MethodUrl.TRANSFER_MYCHATTRANSLOG;
                    getUserInfoAction();
                } else if (rbReceive.isChecked()) {
                    requestTag = MethodUrl.CHAT_RED_MYHONGBAOLOG;
                    getUserInfoAction();
                }
            }
        });
    }

    private void getUserInfoAction() {
        Map<String, Object> map = new HashMap<>();
        if (UtilTools.empty(MbsConstans.ACCESS_TOKEN)) {
            MbsConstans.ACCESS_TOKEN = SPUtils.get(MbsConstans.SharedInfoConstans.ACCESS_TOKEN, "").toString();
        }
        map.put("token", MbsConstans.ACCESS_TOKEN);
        map.put("page", page + "");
        mRequestPresenterImp.requestPostToMap(requestTag, map);
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
            case MethodUrl.CHAT_RED_MYHONGBAOLOG:
            case MethodUrl.TRANSFER_MYCHATTRANSLOG:
                if (srlRefresh2.getState() == RefreshState.Loading || srlRefresh2.getState() == RefreshState.Refreshing) {
                    srlRefresh2.finishRefresh();
                    srlRefresh2.finishLoadMore();
                }
                switch (tData.get("code") + "") {
                    case "1": //请求成功
                        Map<String, Object> dataMap = (Map<String, Object>) tData.get("data");
                        String currentPage = dataMap.get("current_page") + "";
                        String lastPage = dataMap.get("last_page") + "";
                        int pageCurrent = Integer.parseInt(currentPage);
                        int pageLast = Integer.parseInt(lastPage);
                        if (pageLast == 0 || pageCurrent == pageLast) {
                            srlRefresh2.setEnableLoadMore(false);
                        } else {
                            srlRefresh2.setEnableLoadMore(true);
                        }
                        if (pageCurrent == 1) {
                            tempList.clear();
                        }
                        List<Map<String, Object>> dataList = (List<Map<String, Object>>) dataMap.get("data");
                        tempList.addAll(dataList);
                        if (tempList != null && tempList.size() > 0) {
                            redLogListAdapter.setDataList(tempList);
                            pageViewEmpty.setVisibility(View.GONE);
                            lrvList.setVisibility(View.VISIBLE);
                        } else {
                            pageViewEmpty.setVisibility(View.VISIBLE);
                            lrvList.setVisibility(View.GONE);
                        }
                        break;
                    case "0": //请求失败
                        showToastMsg(tData.get("msg") + "");
                        break;
                    case "-1": //token过期
                        finish();
                        intent = new Intent(RedLogActivity.this, LoginActivity1.class);
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
            case R.id.page_view_empty:
                if (rbSend.isChecked()) {
                    requestTag = MethodUrl.TRANSFER_MYCHATTRANSLOG;
                    getUserInfoAction();
                } else if (rbReceive.isChecked()) {
                    requestTag = MethodUrl.CHAT_RED_MYHONGBAOLOG;
                    getUserInfoAction();
                }
                break;
            case R.id.rbSend:
                page = 1;
                requestTag = MethodUrl.TRANSFER_MYCHATTRANSLOG;
                getUserInfoAction();
                break;
            case R.id.rbReceive:
                page = 1;
                requestTag = MethodUrl.CHAT_RED_MYHONGBAOLOG;
                getUserInfoAction();
                break;
            default:
        }
    }
}

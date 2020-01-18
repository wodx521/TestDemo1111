package com.lr.sia.ui.moudle4.activity;

import android.content.Intent;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.jaeger.library.StatusBarUtil;
import com.lr.sia.R;
import com.lr.sia.api.MethodUrl;
import com.lr.sia.basic.BasicActivity;
import com.lr.sia.basic.MbsConstans;
import com.lr.sia.ui.moudle.activity.LoginActivity1;
import com.lr.sia.ui.moudle4.adapter.PlacementLogAdapter;
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

public class PrivatePlacementLogActivity extends BasicActivity implements View.OnClickListener {
    private ImageView backImg;
    private LinearLayout pageViewEmpty;
    private SmartRefreshLayout srlRefresh;
    private RecyclerView rvPlacementLog;
    private int page = 1;
    private PlacementLogAdapter placementLogAdapter;

    @Override
    public int getContentView() {
        return R.layout.activity_private_placement_log;
    }

    @Override
    public void init() {
        backImg = findViewById(R.id.backImg);
        srlRefresh = findViewById(R.id.srlRefresh);
        rvPlacementLog = findViewById(R.id.rvPlacementLog);
        pageViewEmpty = findViewById(R.id.page_view_empty);
        pageViewEmpty.setVisibility(View.GONE);
        srlRefresh.setEnableRefresh(true);
        srlRefresh.setEnableLoadMore(false);
        backImg.setOnClickListener(this);
        pageViewEmpty.setOnClickListener(this);
        StatusBarUtil.setColorForSwipeBack(this, ContextCompat.getColor(this, MbsConstans.TOP_BAR_COLOR), MbsConstans.ALPHA);

        placementLogAdapter = new PlacementLogAdapter(PrivatePlacementLogActivity.this);
        rvPlacementLog.setAdapter(placementLogAdapter);
        srlRefresh.setOnRefreshLoadMoreListener(new OnRefreshLoadMoreListener() {
            @Override
            public void onLoadMore(@NonNull RefreshLayout refreshLayout) {
                page += 1;
                getPrivatePlacementAction();
            }

            @Override
            public void onRefresh(@NonNull RefreshLayout refreshLayout) {
                page = 1;
                tempList.clear();
                getPrivatePlacementAction();
            }
        });
        getPrivatePlacementAction();
    }

    //获取行情列表
    private void getPrivatePlacementAction() {
        Map<String, Object> map = new HashMap<>();
        if (UtilTools.empty(MbsConstans.ACCESS_TOKEN)) {
            MbsConstans.ACCESS_TOKEN = SPUtils.get(this, MbsConstans.SharedInfoConstans.ACCESS_TOKEN, "").toString();
        }
        map.put("token", MbsConstans.ACCESS_TOKEN);
        map.put("page", page + "");
        Map<String, String> mHeaderMap = new HashMap<String, String>();
        mRequestPresenterImp.requestPostToMap(mHeaderMap, MethodUrl.PRE_IPO_GETMYPREIPOLIST, map);
    }

    @Override
    public void showProgress() {

    }

    @Override
    public void disimissProgress() {

    }
    private List<Map<String, Object>> tempList = new ArrayList<>();
    @Override
    public void loadDataSuccess(Map<String, Object> tData, String mType) {
        if (srlRefresh.getState() == RefreshState.Loading || srlRefresh.getState() == RefreshState.Refreshing) {
            srlRefresh.finishLoadMore();
            srlRefresh.finishRefresh();
        }
        Intent intent = null;
        switch (mType) {
            case MethodUrl.PRE_IPO_GETMYPREIPOLIST:
                switch (tData.get("code") + "") {
                    case "1":
                        Map<String, Object> dataMap = (Map<String, Object>) tData.get("data");
                        String lastPage = dataMap.get("last_page") + "";
                        String currentPage = dataMap.get("current_page") + "";
                        int last = Integer.parseInt(lastPage);
                        int current = Integer.parseInt(currentPage);
                        if (last > current) {
                            srlRefresh.setEnableLoadMore(true);
                        } else {
                            srlRefresh.setEnableLoadMore(false);
                        }
                        List<Map<String, Object>> dataList = (List<Map<String, Object>>) dataMap.get("data");
                        tempList.addAll(dataList);
                        if (tempList != null && tempList.size() > 0) {
                            placementLogAdapter.setDataList(tempList);
                            pageViewEmpty.setVisibility(View.GONE);
                            rvPlacementLog.setVisibility(View.VISIBLE);
                        }else{
                            pageViewEmpty.setVisibility(View.VISIBLE);
                            rvPlacementLog.setVisibility(View.GONE);
                        }
                        break;
                    case "-1":
                        finish();
                        intent = new Intent(PrivatePlacementLogActivity.this, LoginActivity1.class);
                        startActivity(intent);
                        break;
                    case "0":
                        showToastMsg(tData.get("msg") + "");
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
                getPrivatePlacementAction();
                break;
            default:
        }
    }
}

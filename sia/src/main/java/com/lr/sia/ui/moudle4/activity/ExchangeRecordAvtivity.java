package com.lr.sia.ui.moudle4.activity;

import android.content.Intent;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.RecyclerView;

import com.jaeger.library.StatusBarUtil;
import com.lr.sia.R;
import com.lr.sia.api.MethodUrl;
import com.lr.sia.basic.BasicActivity;
import com.lr.sia.basic.MbsConstans;
import com.lr.sia.ui.moudle.activity.LoginActivity1;
import com.lr.sia.ui.moudle4.adapter.ExchangeLogListAdapter;
import com.lr.sia.utils.tool.SPUtils;
import com.lr.sia.utils.tool.UtilTools;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ExchangeRecordAvtivity extends BasicActivity implements View.OnClickListener {
    private ImageView backImg;
    private SmartRefreshLayout srlRefresh;
    private RecyclerView rvLockLog;
    private LinearLayout pageViewEmpty;
    private List<Map<String, Object>> tempList = new ArrayList<>();
    private ExchangeLogListAdapter exchangeLogListAdapter;

    @Override
    public int getContentView() {
        return R.layout.activity_exchange_record;
    }

    @Override
    public void init() {
        backImg = findViewById(R.id.backImg);
        srlRefresh = findViewById(R.id.srlRefresh);
        rvLockLog = findViewById(R.id.rvLockLog);
        pageViewEmpty = findViewById(R.id.page_view_empty);
        backImg.setOnClickListener(this);
        pageViewEmpty.setOnClickListener(this);
        exchangeLogListAdapter = new ExchangeLogListAdapter(ExchangeRecordAvtivity.this);
        rvLockLog.setAdapter(exchangeLogListAdapter);
        rvLockLog.addItemDecoration(new DividerItemDecoration(ExchangeRecordAvtivity.this, DividerItemDecoration.VERTICAL));
        StatusBarUtil.setColorForSwipeBack(this, ContextCompat.getColor(this, MbsConstans.TOP_BAR_COLOR), MbsConstans.ALPHA);
        getLockListAction();
//        srlRefresh.setOnRefreshLoadMoreListener(new OnRefreshLoadMoreListener() {
//            @Override
//            public void onLoadMore(@NonNull RefreshLayout refreshLayout) {
//                page += 1;
//                getLockListAction();
//            }
//
//            @Override
//            public void onRefresh(@NonNull RefreshLayout refreshLayout) {
//                page = 1;
//                tempList.clear();
//                getLockListAction();
//            }
//        });
    }

    private void getLockListAction() {
        Map<String, Object> map = new HashMap<>();
        if (UtilTools.empty(MbsConstans.ACCESS_TOKEN)) {
            MbsConstans.ACCESS_TOKEN = SPUtils.get(ExchangeRecordAvtivity.this, MbsConstans.SharedInfoConstans.ACCESS_TOKEN, "").toString();
        }
        map.put("token", MbsConstans.ACCESS_TOKEN);
        Map<String, String> mHeaderMap = new HashMap<String, String>();
        mRequestPresenterImp.requestPostToMap(mHeaderMap, MethodUrl.SCORE_GETEXCHANGESCORELIST, map);
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
            case MethodUrl.SCORE_GETEXCHANGESCORELIST:
                switch (tData.get("code") + "") {
                    case "1": //请求成功
                        Map<String, Object> dataMap = (Map<String, Object>) tData.get("data");
                        String currentPage = dataMap.get("current_page") + "";
                        String lastPage = dataMap.get("last_page") + "";
                        int last = Integer.parseInt(lastPage);
                        int current = Integer.parseInt(currentPage);
                        if (last > current) {
                            srlRefresh.setEnableLoadMore(true);
                        } else {
                            srlRefresh.setEnableLoadMore(false);
                        }
                        List<Map<String, Object>> mapList = (List<Map<String, Object>>) dataMap.get("data");
                        tempList.addAll(mapList);
                        if (tempList != null && tempList.size() > 0) {
                            exchangeLogListAdapter.setDataList(tempList);
                            rvLockLog.setVisibility(View.VISIBLE);
                            pageViewEmpty.setVisibility(View.GONE);
                        } else {
                            pageViewEmpty.setVisibility(View.VISIBLE);
                            rvLockLog.setVisibility(View.GONE);
                        }
                        break;
                    case "0": //请求失败
                        showToastMsg(tData.get("msg") + "");
                        break;
                    case "-1": //token过期
                        finish();
                        intent = new Intent(ExchangeRecordAvtivity.this, LoginActivity1.class);
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
                getLockListAction();
                break;
            default:
        }
    }
}

package com.lr.sia.ui.moudle1.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.github.jdsjlzx.interfaces.OnItemClickListener;
import com.github.jdsjlzx.recyclerview.LRecyclerView;
import com.github.jdsjlzx.recyclerview.LRecyclerViewAdapter;
import com.github.jdsjlzx.recyclerview.ProgressStyle;
import com.google.gson.internal.LinkedTreeMap;
import com.jaeger.library.StatusBarUtil;
import com.lr.sia.R;
import com.lr.sia.api.MethodUrl;
import com.lr.sia.basic.BasicFragment;
import com.lr.sia.basic.MbsConstans;
import com.lr.sia.listener.ReLoadingData;
import com.lr.sia.mvp.presenter.RequestPresenterImp;
import com.lr.sia.mywidget.view.PageView;
import com.lr.sia.ui.moudle.activity.LoginActivity1;
import com.lr.sia.ui.moudle1.activity.CoinInfoActivity;
import com.lr.sia.ui.moudle1.activity.ReceiveActivity;
import com.lr.sia.ui.moudle1.adapter.HomeCoinListAdapter;
import com.lr.sia.utils.tool.SPUtils;
import com.lr.sia.utils.tool.UtilTools;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class HomeFragment1 extends BasicFragment implements View.OnClickListener, ReLoadingData {

    private TextView userAsset;
    private TextView tvCode;
    private PageView pageView;
    private LinearLayout content;
    private LRecyclerView mRefreshListView;
    private String mRequestTag;
    private List<Map<String, Object>> mDataList = new ArrayList<>();
    private LRecyclerViewAdapter mLRecyclerViewAdapter;
    private HomeCoinListAdapter homeCoinListAdapter;
    private Handler handler = new Handler();
    //HTTP请求  轮询
    private Runnable cnyRunnable = new Runnable() {
        @Override
        public void run() {
            //获取实时虚拟货币数据
            getUserInfoAssetAction();
            handler.postDelayed(this, MbsConstans.SECOND_TIME_5000);
        }
    };
    private ConstraintLayout clAddress;
    private String coinId;

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.clAddress:
                Intent intent = new Intent(getActivity(), ReceiveActivity.class);
                intent.putExtra("coinId", coinId);
                startActivity(intent);
                break;
            default:
        }
    }

    @Override
    public int getLayoutId() {
        return R.layout.fragment_home1;
    }

    @Override
    public void init() {
        setBarTextColor();
    }

    public void setBarTextColor() {
        StatusBarUtil.setLightMode(getActivity());
    }

    @Override
    public void reLoadingData() {
        pageView.showLoading();
        getUserInfoAssetAction();
    }

    private void getUserInfoAssetAction() {
        mRequestPresenterImp = new RequestPresenterImp(this, getActivity());
        mRequestTag = MethodUrl.ACCOUNT_GETUSERACCOUNT;
        Map<String, Object> map = new HashMap<>();
        if (UtilTools.empty(MbsConstans.ACCESS_TOKEN)) {
            MbsConstans.ACCESS_TOKEN = SPUtils.get(getActivity(), MbsConstans.SharedInfoConstans.ACCESS_TOKEN, "").toString();
        }
        map.put("token", MbsConstans.ACCESS_TOKEN);
        mRequestPresenterImp.requestPostToMap(MethodUrl.ACCOUNT_GETUSERACCOUNT, map);
    }

    @Override
    public void showProgress() {

    }

    @Override
    public void disimissProgress() {

    }

    @Override
    public void loadDataSuccess(Map<String, Object> tData, String mType) {
        Intent intent = null;
        switch (mType) {
            case MethodUrl.ACCOUNT_GETUSERACCOUNT:
                switch (tData.get("code") + "") {
                    case "1":
                        Map<String, Object> data = (Map<String, Object>) tData.get("data");
                        Map<String, Object> addressInfo = (Map<String, Object>) data.get("address_info");
                        Map<String, Object> totalAssets = (Map<String, Object>) data.get("total_assets");
                        coinId = addressInfo.get("coin_id") + "";
                        String coinAddress = addressInfo.get("coin_address") + "";
                        String totalAsset = totalAssets.get("total_assets") + "";
                        String curSymbol = totalAssets.get("cur_symbol") + "";
                        tvCode.setText(coinAddress);
//                        userAsset.setText("≈" + new BigDecimal(totalAsset).toPlainString());
                        userAsset.setText(curSymbol + totalAsset);
                        List<Map<String, Object>> list = (List<Map<String, Object>>) data.get("coin_list");
                        if (UtilTools.empty(list)) {
                            pageView.showEmpty();
                        } else {
                            pageView.showContent();
                            mDataList.clear();
                            mDataList.addAll(list);
                            responseData();
                        }
                        mRefreshListView.refreshComplete(10);
                        break;
                    case "-1":
                        if (getActivity() != null) {
                            getActivity().finish();
                        }
                        intent = new Intent(getActivity(), LoginActivity1.class);
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

    private void responseData() {
        if (mDataList.size() > 0) {
            homeCoinListAdapter.clear();
            homeCoinListAdapter.addAll(mDataList);
            homeCoinListAdapter.notifyDataSetChanged();
            mLRecyclerViewAdapter.notifyDataSetChanged();//必须调用此方法
        } else {
            pageView.showEmpty();
        }
    }

    @Override
    public void loadDataError(Map<String, Object> map, String mType) {

    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if (hidden) {
            if (handler != null && cnyRunnable != null) {
                handler.removeCallbacks(cnyRunnable);
            }
            setUserVisibleHint(false);
        } else {
            if (handler != null && cnyRunnable != null) {
                handler.post(cnyRunnable);
            }
            setUserVisibleHint(true);
        }
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        userAsset = view.findViewById(R.id.userAsset);
        clAddress = view.findViewById(R.id.clAddress);
        tvCode = view.findViewById(R.id.tvCode);
        pageView = view.findViewById(R.id.page_view);
        content = view.findViewById(R.id.content);
        mRefreshListView = view.findViewById(R.id.mRefreshListView);

        clAddress.setOnClickListener(this);
        pageView.setContentView(content);
        pageView.setReLoadingData(this);
        pageView.showLoading();

        LinearLayoutManager manager = new LinearLayoutManager(getActivity());
        manager.setOrientation(RecyclerView.VERTICAL);
        mRefreshListView.setLayoutManager(manager);

        homeCoinListAdapter = new HomeCoinListAdapter(getActivity());
        mLRecyclerViewAdapter = new LRecyclerViewAdapter(homeCoinListAdapter);
        mRefreshListView.setAdapter(mLRecyclerViewAdapter);
        mRefreshListView.setItemAnimator(new DefaultItemAnimator());
        mRefreshListView.setHasFixedSize(true);
        mRefreshListView.setNestedScrollingEnabled(false);

        mRefreshListView.setRefreshProgressStyle(ProgressStyle.BallSpinFadeLoader);
        mRefreshListView.setArrowImageView(R.drawable.ic_pulltorefresh_arrow);

        mRefreshListView.setPullRefreshEnabled(false);
        mRefreshListView.setLoadMoreEnabled(false);
        mLRecyclerViewAdapter.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                if (homeCoinListAdapter.getDataList().size() > 0) {
                    Map<String, Object> item = homeCoinListAdapter.getDataList().get(position);
                    Intent intent = new Intent(getActivity(), CoinInfoActivity.class);
                    intent.putExtra("coinInfo", (LinkedTreeMap<String, Object>) item);
                    startActivity(intent);
                }
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        if (getUserVisibleHint()) {
            handler.post(cnyRunnable);
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        handler.removeCallbacks(cnyRunnable);
    }
}


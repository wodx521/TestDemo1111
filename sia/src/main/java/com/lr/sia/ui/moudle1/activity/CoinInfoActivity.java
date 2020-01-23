package com.lr.sia.ui.moudle1.activity;

import android.content.Intent;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.DefaultItemAnimator;

import com.github.jdsjlzx.interfaces.OnItemClickListener;
import com.github.jdsjlzx.recyclerview.LRecyclerView;
import com.github.jdsjlzx.recyclerview.LRecyclerViewAdapter;
import com.github.jdsjlzx.recyclerview.ProgressStyle;
import com.jaeger.library.StatusBarUtil;
import com.lr.sia.R;
import com.lr.sia.api.MethodUrl;
import com.lr.sia.basic.BasicActivity;
import com.lr.sia.basic.MbsConstans;
import com.lr.sia.mvp.presenter.RequestPresenterImp;
import com.lr.sia.mywidget.view.PageView;
import com.lr.sia.ui.moudle.activity.LoginActivity1;
import com.lr.sia.ui.moudle1.adapter.TransferLogListAdapter;
import com.lr.sia.utils.tool.SPUtils;
import com.lr.sia.utils.tool.UtilTools;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CoinInfoActivity extends BasicActivity implements View.OnClickListener {
    private TextView tvPrice;
    private LRecyclerView rvTradeLog;
    private TextView tvTransfer;
    private TextView tvReceive;
    private ImageView backImg;
    private TextView tvTitle;
    private String mRequestTag;
    private PageView pageView;
    private String coinID;
    private TransferLogListAdapter transferLogListAdapter;
    private LRecyclerViewAdapter mLRecyclerViewAdapter;

    @Override
    public int getContentView() {
        return R.layout.activity_coin_info;
    }

    @Override
    public void init() {
        StatusBarUtil.setColorForSwipeBack(this, ContextCompat.getColor(this, MbsConstans.TOP_BAR_COLOR), MbsConstans.ALPHA);
        HashMap<String, Object> coinInfo = (HashMap<String, Object>) getIntent().getSerializableExtra("coinInfo");
        tvPrice = findViewById(R.id.tvPrice);
        rvTradeLog = findViewById(R.id.rvTradeLog);
        tvTransfer = findViewById(R.id.tvTransfer);
        tvReceive = findViewById(R.id.tvReceive);
        backImg = findViewById(R.id.back_img);
        tvTitle = findViewById(R.id.tvTitle);
        pageView = findViewById(R.id.page_view);

        coinID = coinInfo.get("id") + "";
        String coinName = coinInfo.get("coin_name") + "";
        String coinAmount = coinInfo.get("coin_amount") + "";
        String coinTotalPrice = coinInfo.get("coin_total_price") + "";
        String curSymbol = coinInfo.get("cur_symbol") + "";
        tvTitle.setText(coinName);
        tvPrice.setText(coinAmount + "≈" + curSymbol + coinTotalPrice);
        backImg.setOnClickListener(this);
        tvTransfer.setOnClickListener(this);
        tvReceive.setOnClickListener(this);
        pageView.setContentView(rvTradeLog);
        transferLogListAdapter = new TransferLogListAdapter(CoinInfoActivity.this);
        mLRecyclerViewAdapter = new LRecyclerViewAdapter(transferLogListAdapter);
        rvTradeLog.setAdapter(mLRecyclerViewAdapter);
        rvTradeLog.setItemAnimator(new DefaultItemAnimator());
        rvTradeLog.setHasFixedSize(true);
        rvTradeLog.setNestedScrollingEnabled(false);
        rvTradeLog.setItemAnimator(new DefaultItemAnimator());

        rvTradeLog.setRefreshProgressStyle(ProgressStyle.BallSpinFadeLoader);
        rvTradeLog.setArrowImageView(R.drawable.ic_pulltorefresh_arrow);

        rvTradeLog.setPullRefreshEnabled(false);
        rvTradeLog.setLoadMoreEnabled(false);
        mLRecyclerViewAdapter.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                if (transferLogListAdapter.getDataList().size() > 0) {
                    Map<String, Object> item = transferLogListAdapter.getDataList().get(position);
                    Intent intent = new Intent(CoinInfoActivity.this, TransferDetailActivity.class);
                    intent.putExtra("transferDetailId", item.get("id") + "");
                    startActivity(intent);
                }
            }
        });

        getTradeLogAction();
    }

    private void getTradeLogAction() {
        mRequestPresenterImp = new RequestPresenterImp(this, CoinInfoActivity.this);
        mRequestTag = MethodUrl.ACCOUNT_GETUSERTRADELIST;
        Map<String, Object> map = new HashMap<>();
        if (UtilTools.empty(MbsConstans.ACCESS_TOKEN)) {
            MbsConstans.ACCESS_TOKEN = SPUtils.get(CoinInfoActivity.this, MbsConstans.SharedInfoConstans.ACCESS_TOKEN, "").toString();
        }
        map.put("token", MbsConstans.ACCESS_TOKEN);
        map.put("id", coinID);
        mRequestPresenterImp.requestPostToMap(MethodUrl.ACCOUNT_GETUSERTRADELIST, map);
    }

    @Override
    public void showProgress() {

    }

    @Override
    public void disimissProgress() {

    }
    private List<Map<String, Object>> mDataList = new ArrayList<>();
    @Override
    public void loadDataSuccess(Map<String, Object> tData, String mType) {
        Intent intent = null;
        switch (mType) {
            case MethodUrl.ACCOUNT_GETUSERTRADELIST:
                switch (tData.get("code") + "") {
                    case "1":
                        Map<String, Object> mapData = (Map<String, Object>) tData.get("data");
                        List<Map<String, Object>> dataList = (List<Map<String, Object>>) mapData.get("data");

                        if (UtilTools.empty(dataList)) {
                            pageView.showEmpty();
                        } else {
                            pageView.showContent();
                            mDataList.clear();
                            mDataList.addAll(dataList);
                            responseData();
                        }
                        rvTradeLog.refreshComplete(10);
                        break;
                    case "-1":
                        finish();
                        intent = new Intent(CoinInfoActivity.this, LoginActivity1.class);
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
            transferLogListAdapter.clear();
            transferLogListAdapter.addAll(mDataList);
            transferLogListAdapter.notifyDataSetChanged();
            mLRecyclerViewAdapter.notifyDataSetChanged();//必须调用此方法
        } else {
            pageView.showEmpty();
        }
    }
    @Override
    public void loadDataError(Map<String, Object> map, String mType) {

    }

    @Override
    public void onClick(View v) {
        Intent intent;
        switch (v.getId()) {
            case R.id.back_img:
                finish();
                break;
            case R.id.tvTransfer:
                intent = new Intent(CoinInfoActivity.this, TransferActivity.class);
                intent.putExtra("coinId", coinID);
                startActivity(intent);
                break;
            case R.id.tvReceive:
                intent = new Intent(CoinInfoActivity.this, ReceiveActivity.class);
                intent.putExtra("coinId", coinID);
                startActivity(intent);
                break;
            default:
        }
    }
}

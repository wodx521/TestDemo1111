package com.lr.sia.ui.moudle2.activity;

import android.content.Intent;
import android.os.Build;
import android.os.Handler;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.core.content.ContextCompat;

import com.google.android.material.tabs.TabLayout;
import com.jaeger.library.StatusBarUtil;
import com.lr.sia.R;
import com.lr.sia.api.MethodUrl;
import com.lr.sia.basic.BasicActivity;
import com.lr.sia.basic.MbsConstans;
import com.lr.sia.ui.moudle.activity.LoginActivity1;
import com.lr.sia.utils.tool.SPUtils;
import com.lr.sia.utils.tool.UtilTools;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cn.github.fujianlian.klinechart.DataHelper;
import cn.github.fujianlian.klinechart.KLineChartAdapter;
import cn.github.fujianlian.klinechart.KLineChartView;
import cn.github.fujianlian.klinechart.entity.KLineEntity;
import cn.github.fujianlian.klinechart.formatter.BigValueFormatter;
import cn.github.fujianlian.klinechart.formatter.DateMiddleTimeFormatter;

public class MarketDetailActivity extends BasicActivity implements View.OnClickListener {
    private ImageView backImg;
    private TextView tvTitle, tvPrice, tvUpRatio, tvVolume, tvOutline, tvCoinName, tv24HVolume,
            tvPublishTime, tvOfficialWebsite;
    private TabLayout tlTimeChoose;
    private String mRequestTag;
    private String currencyId;
    private String coinId;
    private Handler handler = new Handler();
    private Runnable cnyRunnable = new Runnable() {
        @Override
        public void run() {
            //获取实时虚拟货币数据
            traderListAction();
            getKlineDataAction();
            handler.postDelayed(this, MbsConstans.SECOND_TIME_5000);
        }
    };
    private KLineChartView klineChartView;
    private KLineChartAdapter kLineChartAdapter = new KLineChartAdapter();
    private List<KLineEntity> kLineDataList = new ArrayList<>();
    private boolean isFirstLoad = true;
    private String colorType;

    @Override
    public int getContentView() {
        return R.layout.activity_market_detail;
    }

    @Override
    public void init() {
        backImg = findViewById(R.id.backImg);
        tvTitle = findViewById(R.id.tvTitle);
        tvPrice = findViewById(R.id.tvPrice);
        tvUpRatio = findViewById(R.id.tvUpRatio);
        tvVolume = findViewById(R.id.tvVolume);
        tlTimeChoose = findViewById(R.id.tlTimeChoose);
        tvOutline = findViewById(R.id.tvOutline);
        klineChartView = findViewById(R.id.klineChartView);

        tvCoinName = findViewById(R.id.tvCoinName);
        tv24HVolume = findViewById(R.id.tv24HVolume);
        tvPublishTime = findViewById(R.id.tvPublishTime);
        tvOfficialWebsite = findViewById(R.id.tvOfficialWebsite);

        backImg.setOnClickListener(this);
        String[] stringArray = getResources().getStringArray(R.array.klineChoose);
        //0 红跌绿涨   1红涨绿跌
        colorType = SPUtils.get(MarketDetailActivity.this, MbsConstans.SharedInfoConstans.COLOR_TYPE, "0").toString();
        klineChartView.setcolor(colorType);
        for (String tab : stringArray) {
            tlTimeChoose.addTab(tlTimeChoose.newTab().setText(tab));
        }

        initChartView();
        StatusBarUtil.setColorForSwipeBack(this, ContextCompat.getColor(this, MbsConstans.TOP_BAR_COLOR), MbsConstans.ALPHA);
        HashMap<String, Object> coinInfo = (HashMap<String, Object>) getIntent().getSerializableExtra("coinInfo");
        HashMap<String, Object> currency = (HashMap<String, Object>) getIntent().getSerializableExtra("currency");
        currencyId = currency.get("id") + "";
        coinId = coinInfo.get("id") + "";
        String coinName = coinInfo.get("coin_name") + "";
        String change = coinInfo.get("change") + "";
        String price = coinInfo.get("price") + "";
        String curSymbol = coinInfo.get("cur_symbol") + "";
        String changeType = coinInfo.get("change_type") + "";
        tvTitle.setText(coinName);
        tvPrice.setText(curSymbol + price);
        if ("1".equals(changeType)) {
            // 上涨
            tvUpRatio.setTextColor(getResources().getColor(R.color.green));
            tvPrice.setTextColor(getResources().getColor(R.color.green));
            tvUpRatio.setText("+" + change);
        } else if ("0".equals(changeType)) {
            // 下跌
            tvUpRatio.setTextColor(getResources().getColor(R.color.red));
            tvPrice.setTextColor(getResources().getColor(R.color.red));
            tvUpRatio.setText(change);
        }
        traderListAction();
        tlTimeChoose.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                isFirstLoad = true;
                klineChartView.justShowLoading();
                kLineChartAdapter.clearData();
                getKlineDataAction();
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
    }

    @Override
    protected void onPause() {
        super.onPause();
        handler.removeCallbacks(cnyRunnable);
    }

    @Override
    protected void onResume() {
        super.onResume();
        handler.post(cnyRunnable);
    }

    @Override
    public boolean isSupportSwipeBack() {
        return false;
    }

    //获取行情列表
    private void traderListAction() {
        mRequestTag = MethodUrl.MARKET_GETMARKETCOININFO;
        Map<String, Object> map = new HashMap<>();
        if (UtilTools.empty(MbsConstans.ACCESS_TOKEN)) {
            MbsConstans.ACCESS_TOKEN = SPUtils.get(this, MbsConstans.SharedInfoConstans.ACCESS_TOKEN, "").toString();
        }
        map.put("token", MbsConstans.ACCESS_TOKEN);
        map.put("id", coinId);
        map.put("cur_unit", currencyId);
        Map<String, String> mHeaderMap = new HashMap<String, String>();
        mRequestPresenterImp.requestPostToMap(mHeaderMap, MethodUrl.MARKET_GETMARKETCOININFO, map);
    }

    // 获取K线数据
    private void initChartView() {
        klineChartView.setAdapter(kLineChartAdapter);
        klineChartView.setDateTimeFormatter(new DateMiddleTimeFormatter());
        klineChartView.setGridColumns(4);
        klineChartView.hideChildDraw();
        klineChartView.setCandleSolid(false);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            klineChartView.setSelectedXLineColor(getColor(R.color.gray));
        } else {
            klineChartView.setSelectedXLineColor(getResources().getColor(R.color.gray));
        }
        klineChartView.hideSelectData();
        klineChartView.setLoadMoreEnd();
        klineChartView.justShowLoading();
    }

    private void getKlineDataAction() {
        mRequestTag = MethodUrl.MARKET_GETCOINKINE;
        Map<String, Object> map = new HashMap<>();
        if (UtilTools.empty(MbsConstans.ACCESS_TOKEN)) {
            MbsConstans.ACCESS_TOKEN = SPUtils.get(this, MbsConstans.SharedInfoConstans.ACCESS_TOKEN, "").toString();
        }
        map.put("token", MbsConstans.ACCESS_TOKEN);
        map.put("id", coinId);
        if (tlTimeChoose.getTabCount() > 0) {
            map.put("type", tlTimeChoose.getSelectedTabPosition() + 1 + "");
            mRequestPresenterImp.requestPostToMap(MethodUrl.MARKET_GETCOINKINE, map);
        }
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
            case MethodUrl.MARKET_GETMARKETCOININFO:
                switch (tData.get("code") + "") {
                    case "1":
                        Map<String, Object> mapData = (Map<String, Object>) tData.get("data");
                        String coinInfo = mapData.get("coin_intro") + "";
                        String coinName = mapData.get("coin_name") + "";
                        String change = mapData.get("change") + "";
                        String changeType = mapData.get("change_type") + "";
                        String curSymbol = mapData.get("cur_symbol") + "";
                        String price = mapData.get("price") + "";
                        String volume = mapData.get("volume") + "";
                        String issueDate = mapData.get("issue_date") + "";
                        String officialWebsite = mapData.get("official_website") + "";
                        tvPrice.setText(curSymbol + price);
                        BigValueFormatter bigValueFormatter = new BigValueFormatter(MarketDetailActivity.this);
                        tvVolume.setText(bigValueFormatter.format(Float.parseFloat(volume)));
                        tvOutline.setText(coinInfo);
                        tvCoinName.setText(coinName);
                        tv24HVolume.setText(new BigDecimal(volume).toPlainString());
                        tvPublishTime.setText(issueDate);
                        tvOfficialWebsite.setText(officialWebsite);
                        if ("1".equals(changeType)) {
                            // 上涨
                            tvUpRatio.setTextColor(getResources().getColor(R.color.green));
                            tvUpRatio.setText("+" + change);
                        } else if ("0".equals(changeType)) {
                            // 下跌
                            tvUpRatio.setTextColor(getResources().getColor(R.color.red));
                            tvUpRatio.setText(change);
                        }
                        break;
                    case "-1":
                        finish();
                        intent = new Intent(MarketDetailActivity.this, LoginActivity1.class);
                        startActivity(intent);
                        break;
                    case "0":
                        showToastMsg(tData.get("msg") + "");
                        break;
                    default:
                }
                break;
            case MethodUrl.MARKET_GETCOINKINE:
                switch (tData.get("code") + "") {
                    case "1":
                        List<List<Object>> data = (List<List<Object>>) tData.get("data");
                        kLineDataList.clear();
                        for (List<Object> klineDataContent : data) {
                            KLineEntity kLineEntity = new KLineEntity();
                            // 时间
                            kLineEntity.data = (String) klineDataContent.get(0);
                            // 开盘价
                            kLineEntity.open = Float.parseFloat(klineDataContent.get(1) + "");
                            // 最高
                            kLineEntity.close = Float.parseFloat(klineDataContent.get(2) + "");
                            // 最低
                            kLineEntity.low = Float.parseFloat(klineDataContent.get(3) + "");
                            // 收盘
                            kLineEntity.high = Float.parseFloat(klineDataContent.get(4) + "");
                            // 交易量
                            kLineEntity.amount = Float.parseFloat(klineDataContent.get(5) + "");
                            kLineDataList.add(kLineEntity);
                        }
                        DataHelper.calculate(kLineDataList);
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                if (isFirstLoad) {
                                    klineChartView.startAnimation();
                                    isFirstLoad = false;
                                    kLineChartAdapter.clearData();
                                }
                                kLineChartAdapter.addFooterData(kLineDataList);
                                kLineChartAdapter.notifyDataSetChanged();
                                klineChartView.refreshComplete();
                            }
                        });
                        break;
                    case "-1":
                        finish();
                        intent = new Intent(MarketDetailActivity.this, LoginActivity1.class);
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
            default:
        }
    }
}

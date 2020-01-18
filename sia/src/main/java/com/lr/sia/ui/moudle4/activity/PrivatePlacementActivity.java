package com.lr.sia.ui.moudle4.activity;

import android.content.Intent;
import android.os.Build;
import android.os.Handler;
import android.text.Html;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.core.content.ContextCompat;

import com.bumptech.glide.Glide;
import com.google.android.material.tabs.TabLayout;
import com.jaeger.library.StatusBarUtil;
import com.lr.sia.R;
import com.lr.sia.api.MethodUrl;
import com.lr.sia.basic.BasicActivity;
import com.lr.sia.basic.MbsConstans;
import com.lr.sia.mywidget.CustomerTextWatcher;
import com.lr.sia.mywidget.dialog.TradePassDialog;
import com.lr.sia.ui.moudle.activity.LoginActivity1;

import com.lr.sia.utils.tool.SPUtils;
import com.lr.sia.utils.tool.UtilTools;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cn.github.fujianlian.klinechart.DataHelper;
import cn.github.fujianlian.klinechart.KLineChartAdapter;
import cn.github.fujianlian.klinechart.KLineChartView;
import cn.github.fujianlian.klinechart.entity.KLineEntity;
import cn.github.fujianlian.klinechart.formatter.DateMiddleTimeFormatter;

public class PrivatePlacementActivity extends BasicActivity implements View.OnClickListener, TradePassDialog.PassFullListener {
    private TextView tvTitle, tvRight, tvName, tvRemaining, tvTime, tvPrice, tvMinAmount,
            tvTotalNum, tvMaxAmount, tvPlacementDes, tvDes, tvHave, tvSiaNum, tvPay, tvUsdtAmount, tvInfo;
    private ImageView backImg, ivIcon, ivImage;
    private TabLayout tlTimeChoose;
    private KLineChartView klineChartView;
    private EditText etPayNum;
    private String mRequestTag;
    private TradePassDialog mTradePassDialog;
    private boolean isFirstLoad = true;
    private KLineChartAdapter kLineChartAdapter = new KLineChartAdapter();
    private Handler handler = new Handler();
    private Runnable cnyRunnable = new Runnable() {
        @Override
        public void run() {
//            //获取实时虚拟货币数据
//            traderListAction();
            getKlineDataAction();
            handler.postDelayed(this, MbsConstans.SECOND_TIME_5000);
        }
    };
    private List<KLineEntity> kLineDataList = new ArrayList<>();
    private String colorType;
    private DecimalFormat decimalFormat;

    @Override
    public int getContentView() {
        return R.layout.activity_private_placement;
    }

    @Override
    public void init() {
        decimalFormat = new DecimalFormat();
        decimalFormat.applyPattern("#.########");
        backImg = findViewById(R.id.backImg);
        tvTitle = findViewById(R.id.tvTitle);
        tvRight = findViewById(R.id.tvRight);
        ivIcon = findViewById(R.id.ivIcon);
        tvName = findViewById(R.id.tvName);
        tvRemaining = findViewById(R.id.tvRemaining);
        tvTime = findViewById(R.id.tvTime);
        tvPrice = findViewById(R.id.tvPrice);
        tvMinAmount = findViewById(R.id.tvMinAmount);
        tvTotalNum = findViewById(R.id.tvTotalNum);
        tvMaxAmount = findViewById(R.id.tvMaxAmount);
        tvPlacementDes = findViewById(R.id.tvPlacementDes);
        tvDes = findViewById(R.id.tvDes);
        tlTimeChoose = findViewById(R.id.tlTimeChoose);
        klineChartView = findViewById(R.id.klineChartView);
        etPayNum = findViewById(R.id.etPayNum);
        tvHave = findViewById(R.id.tvHave);
        tvUsdtAmount = findViewById(R.id.tvUsdtAmount);
        tvSiaNum = findViewById(R.id.tvSiaNum);
        tvInfo = findViewById(R.id.tvInfo);
        tvPay = findViewById(R.id.tvPay);
        ivImage = findViewById(R.id.ivImage);

        backImg.setOnClickListener(this);
        tvRight.setOnClickListener(this);
        tvPlacementDes.setOnClickListener(this);
        tvDes.setOnClickListener(this);
        tvPay.setOnClickListener(this);
        tvRemaining.setText(Html.fromHtml(getString(R.string.remaining)));
        tvPrice.setText(Html.fromHtml(getString(R.string.defaultPrice)));
        tvTotalNum.setText(Html.fromHtml(getString(R.string.totalNum1)));
        tvMaxAmount.setText(Html.fromHtml(getString(R.string.defaultMaxAmount)));
        tvMinAmount.setText(Html.fromHtml(getString(R.string.defaultMinAmount)));
        StatusBarUtil.setColorForSwipeBack(this, ContextCompat.getColor(this, MbsConstans.TOP_BAR_COLOR), MbsConstans.ALPHA);
        tvTitle.setText(R.string.privatePlacementLockUp);
        initChartView();
        tvRight.setText(R.string.privatePlacementLog);
        getPrivatePlacementAction();
        //0 红跌绿涨   1红涨绿跌
        colorType = SPUtils.get(PrivatePlacementActivity.this, MbsConstans.SharedInfoConstans.COLOR_TYPE, "0").toString();
        klineChartView.setcolor(colorType);
        String[] stringArray = getResources().getStringArray(R.array.klineChoose1);
        for (String tab : stringArray) {
            tlTimeChoose.addTab(tlTimeChoose.newTab().setText(tab));
        }
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

        etPayNum.addTextChangedListener(new CustomerTextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() > 0) {
                    String number = s.toString();
                    if (number.startsWith(".")) {
                        etPayNum.setText("");
                    } else {
                        String price = tvPrice.getHint().toString();
                        BigDecimal bigNumber = new BigDecimal(number);
                        BigDecimal bigPrice = new BigDecimal(price);
                        String s1 = bigNumber.multiply(bigPrice).toPlainString();
                        tvUsdtAmount.setText(getString(R.string.defaultUsdt).replace("0.00", decimalFormat.format(Double.parseDouble(s1))));
                    }
                }
            }
        });
    }

    private void getKlineDataAction() {
        mRequestTag = MethodUrl.PRE_IPO_GETPREIPOKLINE;
        Map<String, Object> map = new HashMap<>();
        if (UtilTools.empty(MbsConstans.ACCESS_TOKEN)) {
            MbsConstans.ACCESS_TOKEN = SPUtils.get(this, MbsConstans.SharedInfoConstans.ACCESS_TOKEN, "").toString();
        }
        map.put("token", MbsConstans.ACCESS_TOKEN);
        if (tlTimeChoose.getTabCount() > 0) {
            map.put("type", tlTimeChoose.getSelectedTabPosition() + 1 + "");
            mRequestPresenterImp.requestPostToMap(MethodUrl.PRE_IPO_GETPREIPOKLINE, map);
        }
    }

    //获取行情列表
    private void getPrivatePlacementAction() {
        mRequestTag = MethodUrl.PRE_IPO_GETPREIPOINFO;
        Map<String, Object> map = new HashMap<>();
        if (UtilTools.empty(MbsConstans.ACCESS_TOKEN)) {
            MbsConstans.ACCESS_TOKEN = SPUtils.get(this, MbsConstans.SharedInfoConstans.ACCESS_TOKEN, "").toString();
        }
        map.put("token", MbsConstans.ACCESS_TOKEN);
        Map<String, String> mHeaderMap = new HashMap<String, String>();
        mRequestPresenterImp.requestPostToMap(mHeaderMap, MethodUrl.PRE_IPO_GETPREIPOINFO, map);
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
            case MethodUrl.PRE_IPO_GETPREIPOINFO:
                switch (tData.get("code") + "") {
                    case "1":
                        Map<String, Object> mapData = (Map<String, Object>) tData.get("data");
                        String coinName = mapData.get("coin_name") + "";
                        String coinIcon = mapData.get("coin_icon") + "";
                        String coinPrice = mapData.get("coin_price") + "";
                        String totalAmount = mapData.get("total_amount") + "";
                        String surplusAmount = mapData.get("surplus_amount") + "";
                        String tradeAmountMin = mapData.get("trade_amount_min") + "";
                        String tradeAmountMax = mapData.get("trade_amount_max") + "";
                        String startTime = mapData.get("start_time") + "";
                        String endTime = mapData.get("end_time") + "";
                        String ipoIntro = mapData.get("ipo_intro") + "";
                        String useableUsdt = mapData.get("useable_usdt") + "";
                        tvName.setText(coinName);
                        tvTime.setText(getString(R.string.defaultTime) + startTime + "--" + endTime);
                        tvRemaining.setText(Html.fromHtml(getString(R.string.remaining).replace("0", decimalFormat.format(Double.parseDouble(surplusAmount)))));
                        tvPrice.setText(Html.fromHtml(getString(R.string.defaultPrice).replace("0", decimalFormat.format(Double.parseDouble(coinPrice)))));
                        tvPrice.setHint(coinPrice);
                        tvTotalNum.setText(Html.fromHtml(getString(R.string.totalNum1).replace("0", decimalFormat.format(Double.parseDouble(totalAmount)))));
                        tvMaxAmount.setText(Html.fromHtml(getString(R.string.defaultMaxAmount).replace("0", decimalFormat.format(Double.parseDouble(tradeAmountMax)))));
                        tvMinAmount.setText(Html.fromHtml(getString(R.string.defaultMinAmount).replace("0", decimalFormat.format(Double.parseDouble(tradeAmountMin)))));
                        tvMaxAmount.setHint(tradeAmountMax);
                        tvMinAmount.setHint(tradeAmountMin);
                        tvHave.setText(getString(R.string.currentlyHave) + decimalFormat.format(Double.parseDouble(useableUsdt)) + "USDT");
                        BigDecimal usdtAbleBig = new BigDecimal(useableUsdt);
                        BigDecimal coinPriceBig = new BigDecimal(coinPrice);
                        tvSiaNum.setText("≈" + decimalFormat.format(usdtAbleBig.divide(coinPriceBig, RoundingMode.CEILING).doubleValue()) + "SIA");
                        Glide.with(PrivatePlacementActivity.this)
                                .load(coinIcon)
                                .into(ivIcon);
                        tvInfo.setText(Html.fromHtml(ipoIntro));
                        break;
                    case "-1":
                        finish();
                        intent = new Intent(PrivatePlacementActivity.this, LoginActivity1.class);
                        startActivity(intent);
                        break;
                    case "0":
                        showToastMsg(tData.get("msg") + "");
                        break;
                    default:
                }
                break;
            case MethodUrl.PRE_IPO_PAYFORPREIPO:
                switch (tData.get("code") + "") {
                    case "1":
                        // TODO: 2019/12/3
                        if (mTradePassDialog != null) {
                            mTradePassDialog.dismiss();
                        }
                        intent = new Intent(PrivatePlacementActivity.this, PrivatePlacementLogActivity.class);
                        startActivity(intent);
                        break;
                    case "-1":
                        finish();
                        intent = new Intent(PrivatePlacementActivity.this, LoginActivity1.class);
                        startActivity(intent);
                        break;
                    case "0":
                        showToastMsg(tData.get("msg") + "");
                        break;
                    default:
                }
                break;
            case MethodUrl.PRE_IPO_GETPREIPOKLINE:
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
                        intent = new Intent(PrivatePlacementActivity.this, LoginActivity1.class);
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
        Intent intent;
        switch (v.getId()) {
            case R.id.backImg:
                finish();
                break;
            case R.id.tvRight:
                intent = new Intent(PrivatePlacementActivity.this, PrivatePlacementLogActivity.class);
                startActivity(intent);
                break;
            case R.id.tvPlacementDes:
                intent = new Intent(PrivatePlacementActivity.this, PrivatePlacementDesActivity.class);
                startActivity(intent);
                break;
            case R.id.tvDes:
                intent = new Intent(PrivatePlacementActivity.this, ProjectDesActivity.class);
                startActivity(intent);
                break;
            case R.id.tvPay:
                String payNum = etPayNum.getText().toString();
                if (!TextUtils.isEmpty(payNum)) {
                    showPassDialog();
                } else {
                    showToastMsg(getString(R.string.inputPayNum));
                }
                break;
            default:
        }
    }

    private void showPassDialog() {
        if (mTradePassDialog == null) {
            mTradePassDialog = new TradePassDialog(this, true);
            mTradePassDialog.setPassFullListener(PrivatePlacementActivity.this);
            mTradePassDialog.showAtLocation(Gravity.BOTTOM, 0, 0);
            mTradePassDialog.mPasswordEditText.setText(null);
        } else {
            mTradePassDialog.showAtLocation(Gravity.BOTTOM, 0, 0);
            mTradePassDialog.mPasswordEditText.setText(null);
        }
    }

    @Override
    public void onPassFullListener(String pass) {
        mTradePassDialog.mPasswordEditText.setText(null);
        Map<String, Object> map = new HashMap<>();
        if (UtilTools.empty(MbsConstans.ACCESS_TOKEN)) {
            MbsConstans.ACCESS_TOKEN = SPUtils.get(PrivatePlacementActivity.this, MbsConstans.ACCESS_TOKEN, "").toString();
        }
        map.put("token", MbsConstans.ACCESS_TOKEN);
        map.put("buy_amount", etPayNum.getText().toString());
        map.put("pay_pwd", pass);
        Map<String, String> mHeaderMap = new HashMap<String, String>();
        mRequestPresenterImp.requestPostToMap(mHeaderMap, MethodUrl.PRE_IPO_PAYFORPREIPO, map);
    }
}

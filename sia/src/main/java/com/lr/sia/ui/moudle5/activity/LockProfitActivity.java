package com.lr.sia.ui.moudle5.activity;

import android.content.Intent;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.jaeger.library.StatusBarUtil;
import com.lr.sia.R;
import com.lr.sia.api.MethodUrl;
import com.lr.sia.basic.BasicActivity;
import com.lr.sia.basic.MbsConstans;
import com.lr.sia.mywidget.CustomerTextWatcher;
import com.lr.sia.mywidget.dialog.TradePassDialog;
import com.lr.sia.ui.moudle.activity.LoginActivity1;
import com.lr.sia.ui.moudle5.adapter.LockListAdapter;
import com.lr.sia.ui.moudle5.dialog.ChooseTimePopup;
import com.lr.sia.utils.tool.SPUtils;
import com.lr.sia.utils.tool.UtilTools;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.constant.RefreshState;
import com.scwang.smartrefresh.layout.listener.OnRefreshLoadMoreListener;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class LockProfitActivity extends BasicActivity implements View.OnClickListener, TradePassDialog.PassFullListener {
    private ImageView backImg;
    private EditText etLockNum;
    private LinearLayout llLockTime;
    private TextView tvChooseTime, tvProfitRatio, tvExpectedProfit, tvProfitLog;
    private SmartRefreshLayout srlRefresh;
    private Button btLock;
    private RecyclerView rvLockLog;
    private ChooseTimePopup chooseTimePopup;
    private List<Map<String, Object>> dataList;
    private int page = 1;
    private LockListAdapter lockListAdapter;
    private LinearLayout pageViewEmpty;
    private List<Map<String, Object>> tempList = new ArrayList<>();
    private TradePassDialog mTradePassDialog;

    @Override
    public int getContentView() {
        return R.layout.activity_lock_profit;
    }

    @Override
    public void init() {
        backImg = findViewById(R.id.backImg);
        etLockNum = findViewById(R.id.etLockNum);
        llLockTime = findViewById(R.id.llLockTime);
        tvChooseTime = findViewById(R.id.tvChooseTime);
        tvProfitRatio = findViewById(R.id.tvProfitRatio);
        tvExpectedProfit = findViewById(R.id.tvExpectedProfit);
        tvProfitLog = findViewById(R.id.tvProfitLog);
        btLock = findViewById(R.id.btLock);
        rvLockLog = findViewById(R.id.rvLockLog);
        srlRefresh = findViewById(R.id.srlRefresh);
        pageViewEmpty = findViewById(R.id.page_view_empty);
        pageViewEmpty.setVisibility(View.GONE);
        backImg.setOnClickListener(this);
        btLock.setOnClickListener(this);
        pageViewEmpty.setOnClickListener(this);
        tvChooseTime.setOnClickListener(this);
        tvProfitLog.setOnClickListener(this);
        StatusBarUtil.setColorForSwipeBack(this, ContextCompat.getColor(this, MbsConstans.TOP_BAR_COLOR), MbsConstans.ALPHA);
        chooseTimePopup = new ChooseTimePopup(LockProfitActivity.this);
        tvProfitRatio.setText(getString(R.string.defaultProfitRatio).replace("s%", "0 %"));
        tvExpectedProfit.setText(getString(R.string.estimatedTotalProfit).replace("s%", "0"));
        etLockNum.addTextChangedListener(new CustomerTextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() > 0) {
                    String number = s.toString();
                    if (number.startsWith(".")) {
                        etLockNum.setText("");
                    } else {
                        BigDecimal bigNumber = new BigDecimal(number);
                        String profitRatio = tvProfitRatio.getText().toString();
                        String[] split = profitRatio.split(":");
                        if (split.length > 0) {
                            String ratio = split[1].replace("%", "");
                            BigDecimal bigRatio = new BigDecimal(ratio.trim());
                            String hint = tvChooseTime.getHint().toString();
                            String time = hint.split(":")[1];
                            tvExpectedProfit.setText(getString(R.string.estimatedTotalProfit).replace("s%", bigNumber.multiply(bigRatio).divide(new BigDecimal("100"), 4, RoundingMode.CEILING).multiply(new BigDecimal(time)).toPlainString()));
                        }
                    }
                }
            }
        });
        lockListAdapter = new LockListAdapter(LockProfitActivity.this);
        rvLockLog.setAdapter(lockListAdapter);
        getLockInfoAction();
        getLockListAction();
        srlRefresh.setOnRefreshLoadMoreListener(new OnRefreshLoadMoreListener() {
            @Override
            public void onLoadMore(@NonNull RefreshLayout refreshLayout) {
                page += 1;
                getLockListAction();
            }

            @Override
            public void onRefresh(@NonNull RefreshLayout refreshLayout) {
                page = 1;
                tempList.clear();
                getLockListAction();
            }
        });
    }

    private void getLockInfoAction() {
        Map<String, Object> map = new HashMap<>();
        if (UtilTools.empty(MbsConstans.ACCESS_TOKEN)) {
            MbsConstans.ACCESS_TOKEN = SPUtils.get(LockProfitActivity.this, MbsConstans.SharedInfoConstans.ACCESS_TOKEN, "").toString();
        }
        map.put("token", MbsConstans.ACCESS_TOKEN);
        Map<String, String> mHeaderMap = new HashMap<String, String>();
        mRequestPresenterImp.requestPostToMap(mHeaderMap, MethodUrl.SELF_LOCKING_GETSELFLOCKINGINFO, map);
    }

    private void getLockListAction() {
        Map<String, Object> map = new HashMap<>();
        if (UtilTools.empty(MbsConstans.ACCESS_TOKEN)) {
            MbsConstans.ACCESS_TOKEN = SPUtils.get(LockProfitActivity.this, MbsConstans.SharedInfoConstans.ACCESS_TOKEN, "").toString();
        }
        map.put("token", MbsConstans.ACCESS_TOKEN);
        map.put("page", page + "");
        Map<String, String> mHeaderMap = new HashMap<String, String>();
        mRequestPresenterImp.requestPostToMap(mHeaderMap, MethodUrl.SELF_LOCKING_GETMYSELFLOCKING, map);
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
            case MethodUrl.SELF_LOCKING_GETSELFLOCKINGINFO:
                switch (tData.get("code") + "") {
                    case "1": //请求成功
                        dataList = (List<Map<String, Object>>) tData.get("data");
                        if (dataList != null && dataList.size() > 0) {
                            Map<String, Object> stringObjectMap = dataList.get(0);
                            tvChooseTime.setText(stringObjectMap.get("time_info") + "");
                            tvChooseTime.setHint(stringObjectMap.get("id") + ":" + stringObjectMap.get("locking_time"));
                            tvProfitRatio.setText(getString(R.string.defaultProfitRatio).replace("s", stringObjectMap.get("reward_percent") + ""));
                        }
                        break;
                    case "0": //请求失败
                        showToastMsg(tData.get("msg") + "");
                        break;
                    case "-1": //token过期
                        finish();
                        intent = new Intent(LockProfitActivity.this, LoginActivity1.class);
                        startActivity(intent);
                        break;
                    default:
                }
                break;
            case MethodUrl.SELF_LOCKING_GETMYSELFLOCKING:
                if (srlRefresh.getState() == RefreshState.Loading || srlRefresh.getState() == RefreshState.Refreshing) {
                    srlRefresh.finishRefresh();
                    srlRefresh.finishLoadMore();
                }
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
                            lockListAdapter.setDataList(tempList);
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
                        intent = new Intent(LockProfitActivity.this, LoginActivity1.class);
                        startActivity(intent);
                        break;
                    default:
                }
                break;
            case MethodUrl.SELF_LOCKING_ADDSELFLOCKINGREWARD:
                if (mTradePassDialog != null) {
                    mTradePassDialog.dismiss();
                }
                switch (tData.get("code") + "") {
                    case "1": //请求成功
                        getLockListAction();
                        break;
                    case "0": //请求失败
                        showToastMsg(tData.get("msg") + "");
                        break;
                    case "-1": //token过期
                        finish();
                        intent = new Intent(LockProfitActivity.this, LoginActivity1.class);
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
            case R.id.btLock:
                String number = etLockNum.getText().toString();
                if (!TextUtils.isEmpty(number)) {
                    showPassDialog();
                } else {
                    showToastMsg(getString(R.string.inputLockNum));
                }
                break;
            case R.id.tvChooseTime:
                chooseTimePopup.getPopup(llLockTime, dataList);
                chooseTimePopup.setOnChooseContentListener(new ChooseTimePopup.OnChooseContentListener() {
                    @Override
                    public void onChooseClickListener(int position) {
                        if (dataList != null && dataList.size() > 0) {
                            Map<String, Object> stringObjectMap = dataList.get(position);
                            tvChooseTime.setText(stringObjectMap.get("time_info") + "");
                            String lockingTime = stringObjectMap.get("locking_time") + "";
                            tvProfitRatio.setText(getString(R.string.defaultProfitRatio).replace("s", stringObjectMap.get("reward_percent") + ""));
                            String number = etLockNum.getText().toString();
                            if (!TextUtils.isEmpty(number)) {
                                BigDecimal bigNumber = new BigDecimal(number);
                                String profitRatio = tvProfitRatio.getText().toString();
                                String[] split = profitRatio.split(":");
                                if (split.length > 0) {
                                    String ratio = split[1].replace("%", "");
                                    BigDecimal bigRatio = new BigDecimal(ratio.trim());
                                    tvExpectedProfit.setText(getString(R.string.estimatedTotalProfit).replace("s%", bigNumber.multiply(bigRatio).divide(new BigDecimal("100"), 4, RoundingMode.CEILING).multiply(new BigDecimal(lockingTime)).toPlainString()));
                                }
                            } else {
                                tvExpectedProfit.setText(getString(R.string.estimatedTotalProfit).replace("s%", "0"));
                            }
                        }
                    }
                });
                break;
            case R.id.page_view_empty:
                getLockListAction();
                break;
            case R.id.tvProfitLog:
                Intent intent = new Intent(LockProfitActivity.this, ProfitLogActivity.class);
                startActivity(intent);
                break;
            default:
        }
    }

    private void showPassDialog() {
        if (mTradePassDialog == null) {
            mTradePassDialog = new TradePassDialog(this, true);
            mTradePassDialog.setPassFullListener(LockProfitActivity.this);
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
        getLockAction(pass);
    }

    private void getLockAction(String pass) {
        Map<String, Object> map = new HashMap<>();
        if (UtilTools.empty(MbsConstans.ACCESS_TOKEN)) {
            MbsConstans.ACCESS_TOKEN = SPUtils.get(LockProfitActivity.this, MbsConstans.SharedInfoConstans.ACCESS_TOKEN, "").toString();
        }
        map.put("token", MbsConstans.ACCESS_TOKEN);
        String hint = tvChooseTime.getHint().toString();
        map.put("selflocking_id", hint.split(":")[0]);
        map.put("amount", etLockNum.getText().toString());
        map.put("pay_pwd", pass);
        Map<String, String> mHeaderMap = new HashMap<String, String>();
        mRequestPresenterImp.requestPostToMap(mHeaderMap, MethodUrl.SELF_LOCKING_ADDSELFLOCKINGREWARD, map);
    }
}

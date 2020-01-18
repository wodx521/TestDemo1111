package com.lr.sia.ui.moudle4.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;

import com.jaeger.library.StatusBarUtil;
import com.lr.sia.R;
import com.lr.sia.api.MethodUrl;
import com.lr.sia.basic.BasicActivity;
import com.lr.sia.basic.MbsConstans;
import com.lr.sia.mvp.presenter.RequestPresenterImp;
import com.lr.sia.mywidget.dialog.TradePassDialog;
import com.lr.sia.ui.moudle.activity.LoginActivity1;
import com.lr.sia.ui.moudle4.dialog.BindMarketIdDialog;
import com.lr.sia.ui.moudle4.dialog.ChooseCoinPopup;
import com.lr.sia.utils.tool.JSONUtil;
import com.lr.sia.utils.tool.SPUtils;
import com.lr.sia.utils.tool.UtilTools;
import com.tencent.mm.opensdk.constants.ConstantsAPI;
import com.tencent.mm.opensdk.modelbiz.WXLaunchMiniProgram;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ExchangeActivity extends BasicActivity implements View.OnClickListener, TradePassDialog.PassFullListener {
    private ImageView backImg;
    private EditText etExchangeNumber;
    private TextView tvExchangeRecord, tvAssets, tvAvailable, tvBuy;
    private ConstraintLayout constraintLayout3;
    private List<Map<String, Object>> list;
    private ChooseCoinPopup chooseCoinPopup;
    private String requestTag;
    private BindMarketIdDialog bindMarketIdDialog;
    private IWXAPI api;
    private TradePassDialog mTradePassDialog;
    private String coinId;
    private BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            // 将该app注册到微信
            api.registerApp(MbsConstans.APP_ID);
        }
    };

    @Override
    public int getContentView() {
        return R.layout.activity_exchange;
    }

    @Override
    public void init() {
        regToWx();
        StatusBarUtil.setColorForSwipeBack(this, ContextCompat.getColor(this, MbsConstans.TOP_BAR_COLOR), MbsConstans.ALPHA);
        backImg = findViewById(R.id.backImg);
        tvExchangeRecord = findViewById(R.id.tvExchangeRecord);
        tvAssets = findViewById(R.id.tvAssets);
        etExchangeNumber = findViewById(R.id.etExchangeNumber);
        tvAvailable = findViewById(R.id.tvAvailable);
        constraintLayout3 = findViewById(R.id.constraintLayout3);
        tvBuy = findViewById(R.id.tvBuy);
        chooseCoinPopup = new ChooseCoinPopup(ExchangeActivity.this);
        bindMarketIdDialog = new BindMarketIdDialog(ExchangeActivity.this);
        backImg.setOnClickListener(this);
        tvExchangeRecord.setOnClickListener(this);
        tvBuy.setOnClickListener(this);
        getUserInfoAssetAction();
        getMoneyInfoAction();
        String defaultAvailable = getString(R.string.defaultAvailable) + "";
        tvAvailable.setText(String.format(defaultAvailable, "0.00", "0.00", "0.00"));
    }

    private void getUserInfoAssetAction() {
        mRequestPresenterImp = new RequestPresenterImp(this, ExchangeActivity.this);
        requestTag = MethodUrl.ACCOUNT_GETUSERACCOUNT;
        Map<String, Object> map = new HashMap<>();
        if (UtilTools.empty(MbsConstans.ACCESS_TOKEN)) {
            MbsConstans.ACCESS_TOKEN = SPUtils.get(ExchangeActivity.this, MbsConstans.SharedInfoConstans.ACCESS_TOKEN, "").toString();
        }
        map.put("token", MbsConstans.ACCESS_TOKEN);
        mRequestPresenterImp.requestPostToMap(MethodUrl.ACCOUNT_GETUSERACCOUNT, map);
    }

    public void getMoneyInfoAction() {
        mRequestPresenterImp = new RequestPresenterImp(this, ExchangeActivity.this);
        requestTag = MethodUrl.USER_GETUSERCENTERMESSAGE;
        Map<String, Object> map = new HashMap<>();
        if (UtilTools.empty(MbsConstans.ACCESS_TOKEN)) {
            MbsConstans.ACCESS_TOKEN = SPUtils.get(ExchangeActivity.this, MbsConstans.ACCESS_TOKEN, "").toString();
        }
        map.put("token", MbsConstans.ACCESS_TOKEN);
        mRequestPresenterImp.requestPostToMap(MethodUrl.USER_GETUSERCENTERMESSAGE, map);
    }

    private void regToWx() {
        // 通过WXAPIFactory工厂，获取IWXAPI的实例
        api = WXAPIFactory.createWXAPI(this, MbsConstans.APP_ID, true);

        // 将应用的appId注册到微信
        api.registerApp(MbsConstans.APP_ID);

        //建议动态监听微信启动广播进行注册到微信
        registerReceiver(broadcastReceiver, new IntentFilter(ConstantsAPI.ACTION_REFRESH_WXAPP));

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
                        list = (List<Map<String, Object>>) data.get("coin_list");
                        if (list != null && list.size() > 0) {
                            Map<String, Object> stringObjectMap = list.get(0);
                            coinId = (String) stringObjectMap.get("id");
                            String coinName = (String) stringObjectMap.get("coin_name");
                            String coinAmount = (String) stringObjectMap.get("coin_amount");
                            String coinTotalPrice = (String) stringObjectMap.get("coin_total_price");
                            tvAssets.setText(coinName);
                            String defaultAvailable = getString(R.string.defaultAvailable) + "";
                            tvAvailable.setText(String.format(defaultAvailable, coinAmount, coinName, coinTotalPrice));
                        }
                        break;
                    case "-1":
                        intent = new Intent(ExchangeActivity.this, LoginActivity1.class);
                        startActivity(intent);
                        finish();
                        break;
                    case "0":
                        showToastMsg(tData.get("msg") + "");
                        break;
                    default:
                }
                break;
            case MethodUrl.USER_GETUSERCENTERMESSAGE://用户信息
                switch (tData.get("code") + "") {
                    case "1": //请求成功
                        MbsConstans.USER_MAP = tData;
                        SPUtils.put(ExchangeActivity.this, MbsConstans.SharedInfoConstans.LOGIN_INFO, JSONUtil.getInstance().objectToJson(MbsConstans.USER_MAP));
                        Map<String, Object> data = (Map<String, Object>) tData.get("data");
                        // 微信码
                        String weixinShopCode = data.get("weixin_shop_code") + "";
                        if (TextUtils.isEmpty(weixinShopCode)) {
                            bindMarketIdDialog.getDialog();
                            bindMarketIdDialog.setOrderClickListener(new BindMarketIdDialog.OrderClickListener() {
                                @Override
                                public void onBind(String marketId) {
                                    if (!TextUtils.isEmpty(marketId)) {
                                        confirmBind(marketId);
                                    } else {
                                        showToastMsg(getString(R.string.inputMarketId));
                                    }
                                }

                                @Override
                                public void onGet() {
                                    IWXAPI api = WXAPIFactory.createWXAPI(ExchangeActivity.this, MbsConstans.APP_ID);
                                    WXLaunchMiniProgram.Req req = new WXLaunchMiniProgram.Req();
                                    req.userName = "gh_0094f1b72fa9"; // 填小程序原始id
                                    req.path = "";                  //拉起小程序页面的可带参路径，不填默认拉起小程序首页，对于小游戏，可以只传入 query 部分，来实现传参效果，如：传入 "?foo=bar"。
                                    req.miniprogramType = WXLaunchMiniProgram.Req.MINIPTOGRAM_TYPE_RELEASE;// 可选打开 开发版，体验版和正式版
                                    api.sendReq(req);
                                }
                            });
                        }
                        break;
                    case "0": //请求失败
                        showToastMsg(tData.get("msg") + "");
                        break;
                    case "-1": //token过期
                        finish();
                        intent = new Intent(ExchangeActivity.this, LoginActivity1.class);
                        startActivity(intent);
                        break;
                    default:
                }
                break;
            case MethodUrl.SCORE_GETUSERSCORE:// 兑换积分
                mTradePassDialog.dismiss();
                switch (tData.get("code") + "") {
                    case "1": //请求成功
                        // 兑换成功跳转到小程序
                        IWXAPI api = WXAPIFactory.createWXAPI(ExchangeActivity.this, MbsConstans.APP_ID);
                        WXLaunchMiniProgram.Req req = new WXLaunchMiniProgram.Req();
                        req.userName = "gh_0094f1b72fa9"; // 填小程序原始id
                        req.path = "";                  //拉起小程序页面的可带参路径，不填默认拉起小程序首页，对于小游戏，可以只传入 query 部分，来实现传参效果，如：传入 "?foo=bar"。
                        req.miniprogramType = WXLaunchMiniProgram.Req.MINIPTOGRAM_TYPE_RELEASE;// 可选打开 开发版，体验版和正式版
                        api.sendReq(req);
                        break;
                    case "0": //请求失败
                        showToastMsg(tData.get("msg") + "");
                        break;
                    case "-1": //token过期
                        finish();
                        intent = new Intent(ExchangeActivity.this, LoginActivity1.class);
                        startActivity(intent);
                        break;
                    default:
                }
                break;
            default:
        }
    }

    public void confirmBind(String marketId) {
        mRequestPresenterImp = new RequestPresenterImp(this, ExchangeActivity.this);
        requestTag = MethodUrl.SCORE_SETUSERWINXINSHOPCODE;
        Map<String, Object> map = new HashMap<>();
        if (UtilTools.empty(MbsConstans.ACCESS_TOKEN)) {
            MbsConstans.ACCESS_TOKEN = SPUtils.get(ExchangeActivity.this, MbsConstans.ACCESS_TOKEN, "").toString();
        }
        map.put("token", MbsConstans.ACCESS_TOKEN);
        map.put("weixin_shop_code", marketId);
        mRequestPresenterImp.requestPostToMap(MethodUrl.SCORE_SETUSERWINXINSHOPCODE, map);
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
            case R.id.constraintLayout3:
                if (list != null && list.size() > 0) {
                    chooseCoinPopup.getPopup(tvExchangeRecord, list);
                    chooseCoinPopup.setOnChooseContentListener(new ChooseCoinPopup.OnChooseContentListener() {
                        @Override
                        public void onChooseClickListener(int position) {
                            Map<String, Object> stringObjectMap = list.get(position);
                            coinId = (String) stringObjectMap.get("id");
                            String coinName = (String) stringObjectMap.get("coin_name");
                            String coinAmount = (String) stringObjectMap.get("coin_amount");
                            String coinTotalPrice = (String) stringObjectMap.get("coin_total_price");
                            tvAssets.setText(coinName);
                            String defaultAvailable = getString(R.string.defaultAvailable) + "";
                            tvAvailable.setText(String.format(defaultAvailable, coinAmount, coinName, coinTotalPrice));
                        }
                    });
                }
                break;
            case R.id.tvBuy:
                String number = etExchangeNumber.getText().toString();
                if (!TextUtils.isEmpty(number)) {
                    showPassDialog();
                } else {
                    showToastMsg(getString(R.string.inputExchangeNumber));
                }
                break;
            case R.id.tvExchangeRecord:
               Intent intent = new Intent(ExchangeActivity.this, ExchangeRecordAvtivity.class);
                startActivity(intent);
                break;
            default:
        }
    }

    private void showPassDialog() {
        if (mTradePassDialog == null) {
            mTradePassDialog = new TradePassDialog(this, true);
            mTradePassDialog.setPassFullListener(ExchangeActivity.this);
            mTradePassDialog.showAtLocation(Gravity.BOTTOM, 0, 0);
            mTradePassDialog.mPasswordEditText.setText(null);
        } else {
            mTradePassDialog.showAtLocation(Gravity.BOTTOM, 0, 0);
            mTradePassDialog.mPasswordEditText.setText(null);
        }
    }

    @Override
    public void onPassFullListener(String pass) {
        exchangeRecord(pass);
    }

    public void exchangeRecord(String pass) {
        mRequestPresenterImp = new RequestPresenterImp(this, ExchangeActivity.this);
        requestTag = MethodUrl.SCORE_GETUSERSCORE;
        Map<String, Object> map = new HashMap<>();
        if (UtilTools.empty(MbsConstans.ACCESS_TOKEN)) {
            MbsConstans.ACCESS_TOKEN = SPUtils.get(ExchangeActivity.this, MbsConstans.ACCESS_TOKEN, "").toString();
        }
        map.put("token", MbsConstans.ACCESS_TOKEN);
        map.put("pay_pwd", pass);
        map.put("coin_id", coinId);
        map.put("score_amount", etExchangeNumber.getText().toString());
        mRequestPresenterImp.requestPostToMap(MethodUrl.SCORE_GETUSERSCORE, map);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(broadcastReceiver);
    }
}

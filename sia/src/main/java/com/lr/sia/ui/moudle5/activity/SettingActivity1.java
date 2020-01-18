package com.lr.sia.ui.moudle5.activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;

import com.jaeger.library.StatusBarUtil;
import com.lr.sia.R;
import com.lr.sia.api.MethodUrl;
import com.lr.sia.basic.BasicActivity;
import com.lr.sia.basic.MbsConstans;
import com.lr.sia.mvp.presenter.RequestPresenterImp;
import com.lr.sia.ui.moudle.activity.LoginActivity1;
import com.lr.sia.ui.moudle5.dialog.ChoosePopup;
import com.lr.sia.utils.LanguageChangeUtils;
import com.lr.sia.utils.tool.JSONUtil;
import com.lr.sia.utils.tool.SPUtils;
import com.lr.sia.utils.tool.UtilTools;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SettingActivity1 extends BasicActivity implements View.OnClickListener {
    private CardView cardView1, cardView2, cardView3, cardView4, cardView5, cardView6;
    private TextView tvEmailAddress, tvChangeLanguage, tvChangeCurrency, tvPhoneBind;
    private ImageView backImg;
    private ChoosePopup choosePopup;
    private List<Map<String, Object>> langList;
    private List<Map<String, Object>> currencyList;
    private String mRequestTag;
    private String chooseType;
    private int languagePosition = -1;
    private int languageCode = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (savedInstanceState != null) {
            int resultCode = savedInstanceState.getInt("resultCode", 0);
            setResult(resultCode);
        }
    }

    @Override
    public int getContentView() {
        return R.layout.activity_setting1;
    }

    @Override
    public void init() {
        choosePopup = new ChoosePopup(SettingActivity1.this);
        StatusBarUtil.setColorForSwipeBack(this, ContextCompat.getColor(this, MbsConstans.TOP_BAR_COLOR), MbsConstans.ALPHA);
        cardView1 = findViewById(R.id.cardView1);
        backImg = findViewById(R.id.backImg);

        cardView2 = findViewById(R.id.cardView2);
        tvEmailAddress = findViewById(R.id.tvEmailAddress);
        tvPhoneBind = findViewById(R.id.tvPhoneBind);
        cardView3 = findViewById(R.id.cardView3);
        cardView4 = findViewById(R.id.cardView4);
        cardView5 = findViewById(R.id.cardView5);
        tvChangeLanguage = findViewById(R.id.tvChangeLanguage);
        cardView6 = findViewById(R.id.cardView6);
        tvChangeCurrency = findViewById(R.id.tvChangeCurrency);

        backImg.setOnClickListener(this);
        cardView1.setOnClickListener(this);
        cardView2.setOnClickListener(this);
        cardView3.setOnClickListener(this);
        cardView4.setOnClickListener(this);
        tvChangeLanguage.setOnClickListener(this);
        tvChangeCurrency.setOnClickListener(this);
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);
        savedInstanceState.putInt("resultCode", languageCode);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (UtilTools.empty(MbsConstans.USER_MAP)) {
            String s = SPUtils.get(SettingActivity1.this, MbsConstans.SharedInfoConstans.LOGIN_INFO, "").toString();
            MbsConstans.USER_MAP = JSONUtil.getInstance().jsonMap(s);
        }
        Map<String, Object> data = (Map<String, Object>) MbsConstans.USER_MAP.get("data");
        String emailAddress = data.get("user_email") + "";
        String userPhone = data.get("user_phone") + "";
        langList = (List<Map<String, Object>>) data.get("sys_lang_list");
        currencyList = (List<Map<String, Object>>) data.get("sys_currency_unit_list");
        String sysLang = data.get("sys_lang") + "";
        String sysCurrencyUnit = data.get("sys_currency_unit") + "";
        for (Map<String, Object> stringObjectMap : langList) {
            if ((stringObjectMap.get("key") + "").equals(sysLang)) {
                tvChangeLanguage.setText(stringObjectMap.get("value") + "");
                break;
            }
        }
        for (Map<String, Object> stringObjectMap : currencyList) {
            if ((stringObjectMap.get("key") + "").equals(sysCurrencyUnit)) {
                tvChangeCurrency.setText(stringObjectMap.get("value") + "");
                break;
            }
        }
        if (!TextUtils.isEmpty(emailAddress)) {
            tvEmailAddress.setText(emailAddress);
        } else {
            tvEmailAddress.setText(getString(R.string.bind));
        }
        if (!TextUtils.isEmpty(userPhone)) {
            tvPhoneBind.setText(getString(R.string.change));
        } else {
            tvPhoneBind.setText(getString(R.string.bind));
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
        Intent intent;
        dismissProgressDialog();
        switch (mType) {
            case MethodUrl.USER_EDITUSERMESSAGE:
                switch (tData.get("code") + "") {
                    case "1": //请求成功
                        String s = SPUtils.get(SettingActivity1.this, MbsConstans.SharedInfoConstans.LOGIN_INFO, "").toString();
                        Map<String, Object> data = JSONUtil.getInstance().jsonMap(s);
                        Map<String, Object> infoData = (Map<String, Object>) data.get("data");
                        infoData.put(chooseType, SPUtils.get(SettingActivity1.this, chooseType, ""));
                        if (chooseType.equals("sys_lang")) {
                            SPUtils.put(SettingActivity1.this, "languageSelect", languagePosition);
                            LanguageChangeUtils.setLanguage(SettingActivity1.this, languagePosition);
                            languageCode = RESULT_OK;
                            recreate();
                        }
                        data.put("data", infoData);
                        MbsConstans.USER_MAP = data;
                        SPUtils.put(SettingActivity1.this, MbsConstans.SharedInfoConstans.LOGIN_INFO, JSONUtil.getInstance().objectToJson(MbsConstans.USER_MAP));
                        showToastMsg(tData.get("msg") + "");
                        break;
                    case "0": //请求失败
                        showToastMsg(tData.get("msg") + "");
                        break;
                    case "-1": //token过期
                        finish();
                        intent = new Intent(SettingActivity1.this, LoginActivity1.class);
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
        Map<String, Object> data;
        Intent intent = null;
        switch (v.getId()) {
            case R.id.backImg:
                finish();
                break;
            case R.id.cardView1:
                if (UtilTools.empty(MbsConstans.USER_MAP)) {
                    String s = SPUtils.get(SettingActivity1.this, MbsConstans.SharedInfoConstans.LOGIN_INFO, "").toString();
                    MbsConstans.USER_MAP = JSONUtil.getInstance().jsonMap(s);
                }
                data = (Map<String, Object>) MbsConstans.USER_MAP.get("data");
                String userPhone = data.get("user_phone") + "";
                if (TextUtils.isEmpty(userPhone)) {
                    // 绑定手机号
                    intent = new Intent(SettingActivity1.this, ChangePhoneActivity2.class);
                    intent.putExtra("type", "1");
                    intent.putExtra("from", "1");
                    startActivity(intent);
                } else {
                    // 修改手机号
                    intent = new Intent(SettingActivity1.this, ChangePhoneActivity1.class);
                    intent.putExtra("type", "1");
                    startActivity(intent);
                }
                break;
            case R.id.cardView2:
                if (UtilTools.empty(MbsConstans.USER_MAP)) {
                    String s = SPUtils.get(SettingActivity1.this, MbsConstans.SharedInfoConstans.LOGIN_INFO, "").toString();
                    MbsConstans.USER_MAP = JSONUtil.getInstance().jsonMap(s);
                }
                data = (Map<String, Object>) MbsConstans.USER_MAP.get("data");
                String userEmail = data.get("user_email") + "";
                if (TextUtils.isEmpty(userEmail)) {
                    // 绑定邮箱
                    intent = new Intent(SettingActivity1.this, ChangePhoneActivity2.class);
                    intent.putExtra("type", "2");
                    intent.putExtra("from", "1");
                    startActivity(intent);
                } else {
                    // 修改邮箱
                    intent = new Intent(SettingActivity1.this, ChangePhoneActivity1.class);
                    intent.putExtra("type", "2");
                    startActivity(intent);
                }
                break;
            case R.id.cardView3:
                // 修改登录密码
                intent = new Intent(SettingActivity1.this, ChangePassActivity.class);
                intent.putExtra("type", "1");
                startActivity(intent);
                break;
            case R.id.cardView4:
                // 修改交易密码
                intent = new Intent(SettingActivity1.this, ChangePassActivity.class);
                intent.putExtra("type", "2");
                startActivity(intent);
                break;
            case R.id.tvChangeLanguage:
                chooseType = "sys_lang";
                choosePopup.getPopup(tvChangeLanguage, langList);
                choosePopup.setOnChooseContentListener(new ChoosePopup.OnChooseContentListener() {
                    @Override
                    public void onChooseClickListener(int position) {
                        if (languagePosition != position) {
                            languagePosition = position;
                            Map<String, Object> stringObjectMap = langList.get(position);
                            tvChangeLanguage.setText(stringObjectMap.get("value") + "");
                            SPUtils.put(SettingActivity1.this, "sys_lang", stringObjectMap.get("key") + "");
                            editUserInfoAction("sys_lang", stringObjectMap.get("key") + "");
                        }
                    }
                });
                break;
            case R.id.tvChangeCurrency:
                chooseType = "sys_currency_unit";
                choosePopup.getPopup(tvChangeCurrency, currencyList);
                choosePopup.setOnChooseContentListener(new ChoosePopup.OnChooseContentListener() {
                    @Override
                    public void onChooseClickListener(int position) {
                        Map<String, Object> stringObjectMap = currencyList.get(position);
                        tvChangeCurrency.setText(stringObjectMap.get("value") + "");
                        SPUtils.put(SettingActivity1.this, "sys_currency_unit", stringObjectMap.get("key") + "");
                        editUserInfoAction("sys_currency_unit", stringObjectMap.get("key") + "");
                    }
                });
                break;
            default:
        }
    }

    private void editUserInfoAction(String type, String value) {
        mRequestPresenterImp = new RequestPresenterImp(this, SettingActivity1.this);
        mRequestTag = MethodUrl.USER_EDITUSERMESSAGE;
        Map<String, Object> map = new HashMap<>();
        if (UtilTools.empty(MbsConstans.ACCESS_TOKEN)) {
            MbsConstans.ACCESS_TOKEN = SPUtils.get(SettingActivity1.this, MbsConstans.ACCESS_TOKEN, "").toString();
        }
        map.put("token", MbsConstans.ACCESS_TOKEN);
        map.put("item", type);
        map.put("value", value);
        mRequestPresenterImp.requestPostToMap(MethodUrl.USER_EDITUSERMESSAGE, map);
    }
}

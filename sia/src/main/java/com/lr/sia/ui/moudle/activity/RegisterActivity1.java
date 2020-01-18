package com.lr.sia.ui.moudle.activity;

import android.content.Intent;
import android.text.Html;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.android.material.tabs.TabLayout;
import com.jaeger.library.StatusBarUtil;
import com.lr.sia.BuildConfig;
import com.lr.sia.R;
import com.lr.sia.api.MethodUrl;
import com.lr.sia.basic.BasicActivity;
import com.lr.sia.basic.MbsConstans;
import com.lr.sia.ui.moudle5.dialog.ChoosePopup;
import com.lr.sia.ui.moudle5.dialog.PhoneAreaPopup;
import com.lr.sia.utils.CountDownUtils;
import com.lr.sia.utils.tool.SPUtils;
import com.lr.sia.utils.tool.UtilTools;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RegisterActivity1 extends BasicActivity implements View.OnClickListener {
    private TextView tvChooseLanguage, tvGetCode, tvHave, tvChoosePhone;
    private TabLayout tlRegisterAccount;
    private EditText editUid, editPsw, editEmail;
    private Button btnLogin;
    private ChoosePopup choosePopup;
    private List<Map<String, Object>> mapList = new ArrayList<>();
    private List<String> phoneArea;
    private RelativeLayout rlEmail, rlPhone;
    private CountDownUtils countDownUtils;
    private PhoneAreaPopup phoneAreaPopup;

    @Override
    public int getContentView() {
        return R.layout.activity_register1;
    }

    @Override
    public void init() {
        StatusBarUtil.setTranslucentForImageView(this, MbsConstans.ALPHA, null);
        tvChooseLanguage = findViewById(R.id.tvChooseLanguage);
        tlRegisterAccount = findViewById(R.id.tlRegisterAccount);
        editUid = findViewById(R.id.edit_uid);
        editEmail = findViewById(R.id.editEmail);
        editPsw = findViewById(R.id.edit_psw);
        tvGetCode = findViewById(R.id.tvGetCode);
        tvChoosePhone = findViewById(R.id.tvChoosePhone);
        btnLogin = findViewById(R.id.btn_login);
        tvHave = findViewById(R.id.tvHave);
        rlEmail = findViewById(R.id.rlEmail);
        rlPhone = findViewById(R.id.rlPhone);

        tvChooseLanguage.setOnClickListener(this);
        tvGetCode.setOnClickListener(this);
        btnLogin.setOnClickListener(this);
        tvHave.setOnClickListener(this);
        tvChoosePhone.setOnClickListener(this);
        tvHave.setText(Html.fromHtml(getString(R.string.haveAccount)));
        StatusBarUtil.setTranslucentForImageView(this, MbsConstans.ALPHA, null);
        choosePopup = new ChoosePopup(RegisterActivity1.this);
        phoneAreaPopup = new PhoneAreaPopup(RegisterActivity1.this);
        countDownUtils = new CountDownUtils();
        mapList.clear();
        String[] spinnerItems = {"中文简体", "English", "日本語", "한국어"};
        String[] accountType = getResources().getStringArray(R.array.accountType);
        for (String tabContent : accountType) {
            tlRegisterAccount.addTab(tlRegisterAccount.newTab().setText(tabContent));
        }
        tlRegisterAccount.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                int position = tab.getPosition();
                if (position == 0) {
                    rlPhone.setVisibility(View.VISIBLE);
                    rlEmail.setVisibility(View.GONE);
                    tvChoosePhone.setVisibility(View.VISIBLE);
                    editPsw.setText("");
                } else if (position == 1) {
                    rlPhone.setVisibility(View.GONE);
                    rlEmail.setVisibility(View.VISIBLE);
                    tvChoosePhone.setVisibility(View.GONE);
                    editPsw.setText("");
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
        for (int i = 0; i < spinnerItems.length; i++) {
            Map<String, Object> langMap = new HashMap<>();
            langMap.put("key", i + 1 + "");
            langMap.put("value", spinnerItems[i]);
            mapList.add(langMap);
        }
        int languageSelect = (int) SPUtils.get("languageSelect", -1);
        tvChooseLanguage.setText(spinnerItems[languageSelect == -1 ? 0 : languageSelect]);

        getPhoneArea();
    }

    private void getPhoneArea() {
        Map<String, Object> map = new HashMap<>();
        mRequestPresenterImp.requestPostToMap(MethodUrl.COMMON_GETPHONEPRELIST, map);
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
            case MethodUrl.COMMON_GETPHONEPRELIST:
                switch (tData.get("code") + "") {
                    case "1":// 成功
                        phoneArea = (List<String>) tData.get("data");
                        tvChoosePhone.setText("+86");
                        break;
                    case "-1": // 超时
                        showToastMsg(tData.get("msg") + "");
                        break;
                    case "0": // 失败
                        showToastMsg(tData.get("msg") + "");
                        break;
                    default:
                }
                break;
            case MethodUrl.COMMON_SENDSMSVERIFY:
                dismissProgressDialog();
                switch (tData.get("code") + "") {
                    case "1":
                        tvGetCode.setEnabled(false);
                        if (BuildConfig.DEBUG) {
                            countDownUtils.getTimer(MbsConstans.SECOND_TIME_10, tvGetCode, getString(R.string.sendVerificationCode));
                        } else {
                            countDownUtils.getTimer(MbsConstans.SECOND_TIME_60, tvGetCode, getString(R.string.sendVerificationCode));
                        }
                        countDownUtils.setTimeFinishListener(new CountDownUtils.CountTimeFinishListener() {
                            @Override
                            public void onTimeFinishListener() {
                                tvGetCode.setEnabled(true);
                            }
                        });
                        break;
                    case "-1":
                        showToastMsg(tData.get("msg") + "");
                        break;
                    case "0":
                        showToastMsg(tData.get("msg") + "");
                        break;
                    default:
                }
                break;
            case MethodUrl.COMMON_CHECKSMSVERIFY:
                dismissProgressDialog();
                switch (tData.get("code") + "") {
                    case "1":// 成功
                        intent = new Intent(RegisterActivity1.this, RegistActivity.class);
                            if (tlRegisterAccount.getSelectedTabPosition() == 0) {
                                intent.putExtra("type", "1");
                                String account = editUid.getText().toString().trim();
                                intent.putExtra("account", account);
                            }else if (tlRegisterAccount.getSelectedTabPosition() == 1){
                                intent.putExtra("type", "2");
                                String account = editEmail.getText().toString().trim();
                                intent.putExtra("account", account);
                            }
                            intent.putExtra("phoneArea", tvChoosePhone.getText().toString());
                        startActivityForResult(intent, MbsConstans.IS_APPROVE_RIGHT);
                        break;
                    case "-1": // 超时
                        showToastMsg(tData.get("msg") + "");
                        break;
                    case "0": // 失败
                        editPsw.setText("");
                        showToastMsg(tData.get("msg") + "");
                        break;
                    default:
                }
            default:
        }
    }

    @Override
    public void loadDataError(Map<String, Object> map, String mType) {

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tvChooseLanguage:
                choosePopup.getPopup(tvChooseLanguage, mapList);
                choosePopup.setOnChooseContentListener(new ChoosePopup.OnChooseContentListener() {
                    @Override
                    public void onChooseClickListener(int position) {
                        SPUtils.put(RegisterActivity1.this, "languageSelect", position);
                        recreate();
                    }
                });
                break;
            case R.id.tvGetCode:
                showProgressDialog();
                if (tlRegisterAccount.getSelectedTabPosition() == 0) {
                    getCodeAction();
                } else {
                    getCodeAction1();
                }
                break;
            case R.id.btn_login:
                showProgressDialog();
                checkCodeAction();
                break;
            case R.id.tvChoosePhone:
                phoneAreaPopup.getPopup(tvChoosePhone, phoneArea);
                phoneAreaPopup.setOnChooseContentListener(new PhoneAreaPopup.OnChooseContentListener() {
                    @Override
                    public void onChooseClickListener(int position) {
                        String s = phoneArea.get(position);
                        tvChoosePhone.setText(s);
                    }
                });
                break;
            case R.id.tvHave:
                finish();
                break;
            default:
        }
    }

    private void getCodeAction() {
        // TODO: 2019/10/23 验证码获取
        if (UtilTools.isEmpty(editUid, getString(R.string.phoneNumber))) {
            btnLogin.setEnabled(true);
            return;
        }
        Map<String, Object> map = new HashMap<>();
        String account = editUid.getText().toString().trim();
        String phoneArea = tvChoosePhone.getText().toString().trim();
        map.put("account_type", "1");
        map.put("account", phoneArea + account);
        map.put("verify_type", "1");
        Map<String, String> mHeaderMap = new HashMap<String, String>();
        mRequestPresenterImp.requestPostToMap(mHeaderMap, MethodUrl.COMMON_SENDSMSVERIFY, map);
    }

    private void getCodeAction1() {
        // TODO: 2019/10/23 验证码获取
        if (UtilTools.isEmpty(editEmail, getString(R.string.email))) {
            btnLogin.setEnabled(true);
            return;
        }
        Map<String, Object> map = new HashMap<>();
        String account = editEmail.getText().toString().trim();
        map.put("account_type", "2");
        map.put("account", account);
        map.put("verify_type", "1");
        Map<String, String> mHeaderMap = new HashMap<String, String>();
        mRequestPresenterImp.requestPostToMap(mHeaderMap, MethodUrl.COMMON_SENDSMSVERIFY, map);
    }

    private void checkCodeAction() {
        String loginPass;
        Map<String, Object> map = new HashMap<>();
        if (tlRegisterAccount.getSelectedTabPosition() == 0) {
            if (UtilTools.isEmpty(editUid, getString(R.string.phoneNumber))) {
                btnLogin.setEnabled(true);
                disimissProgress();
                return;
            }
            loginPass = editUid.getText().toString().trim();
            map.put("account_type", "1");
            map.put("account", tvChoosePhone.getText().toString() + loginPass);
        } else {
            if (UtilTools.isEmpty(editEmail, getString(R.string.email))) {
                btnLogin.setEnabled(true);
                disimissProgress();
                return;
            }
            loginPass = editEmail.getText().toString().trim();
            map.put("account_type", "2");
            map.put("account", loginPass);
        }

        map.put("verify_type", "1");
        map.put("verify_code", editPsw.getText().toString().trim() + "");
        mRequestPresenterImp.requestPostToMap(MethodUrl.COMMON_CHECKSMSVERIFY, map);
    }
}

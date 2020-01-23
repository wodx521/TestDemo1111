package com.lr.sia.ui.testmoudle;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.text.Html;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.google.android.material.tabs.TabLayout;
import com.jaeger.library.StatusBarUtil;
import com.lr.sia.BuildConfig;
import com.lr.sia.R;
import com.lr.sia.api.MethodUrl;
import com.lr.sia.basic.BasicActivity;
import com.lr.sia.basic.MbsConstans;
import com.lr.sia.ui.moudle.activity.RegistActivity;
import com.lr.sia.ui.moudle5.dialog.ChoosePopup;
import com.lr.sia.ui.moudle5.dialog.PhoneAreaPopup;
import com.lr.sia.utils.CountDownUtils;
import com.lr.sia.utils.tool.SPUtils;
import com.lr.sia.utils.tool.UtilTools;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cn.wildfire.chat.kit.ChatManagerHolder;

public class TestRegisterActivity extends BasicActivity implements View.OnClickListener {
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
        choosePopup = new ChoosePopup(TestRegisterActivity.this);
        phoneAreaPopup = new PhoneAreaPopup(TestRegisterActivity.this);
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
                        intent = new Intent(TestRegisterActivity.this, RegistActivity.class);
                        if (tlRegisterAccount.getSelectedTabPosition() == 0) {
                            intent.putExtra("type", "1");
                            String account = editUid.getText().toString().trim();
                            intent.putExtra("account", account);
                        } else if (tlRegisterAccount.getSelectedTabPosition() == 1) {
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
                break;
            case MethodUrl.USER_USERREG:
                dismissProgressDialog();
                switch (tData.get("code") + "") {
                    case "1":// 成功
                        Map<String, Object> userInfoMap = (Map<String, Object>) tData.get("data");
                        String userId = userInfoMap.get("user") + "";
                        String imToken = userInfoMap.get("im_token") + "";
                        List<String> groupsId = (List<String>) userInfoMap.get("parent_group_ids");
                        SharedPreferences sp = getSharedPreferences("config", Context.MODE_PRIVATE);
                        sp.edit().putString("id", userId)
                                .putString("token", imToken)
                                .apply();
                        intent = new Intent();
                        intent.putExtra("userId",userId);
                        intent.putExtra("imToken",imToken);
                        intent.putStringArrayListExtra("groupsId", (ArrayList<String>) groupsId);
                        showToastMsg(tData.get("msg") + "");
                        setResult(RESULT_OK,intent);
                        finish();
                        break;
                    case "-1": // 超时
                        showToastMsg(tData.get("msg") + "");
                        break;
                    case "0": // 失败
                        showToastMsg(tData.get("msg") + "");
                        finish();
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
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case MbsConstans.IS_APPROVE_RIGHT:
                    if (data != null) {
                        setResult(RESULT_OK, data);
                        finish();
                    }
                    break;
                default:
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }
    private HashMap<String, Object> mapParams = new HashMap<>();
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tvChooseLanguage:
                choosePopup.getPopup(tvChooseLanguage, mapList);
                choosePopup.setOnChooseContentListener(new ChoosePopup.OnChooseContentListener() {
                    @Override
                    public void onChooseClickListener(int position) {
                        SPUtils.put(TestRegisterActivity.this, "languageSelect", position);
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
                try {
                    mapParams.clear();
                    mapParams.put("user_phone", editUid.getText().toString().trim() );
                    mapParams.put("user_email", "");
                    mapParams.put("user_phone_pre", "+86");
                    mapParams.put("login_pwd", "qwe111111");
                    mapParams.put("pay_pwd", "111111");
                    mapParams.put("invite_code",  editPsw.getText().toString().trim() );
                    mapParams.put("client_id", ChatManagerHolder.gChatManager.getClientId());
                    mRequestPresenterImp.requestPostToMap(MethodUrl.USER_USERREG, mapParams);
//                    Intent intent = new Intent();
//                    ArrayList<String> strings = new ArrayList<>();
//                    strings.add("xGy4x4uu");
//                    strings.add("PKQQPQdd");
//                    intent.putExtra("userId","KDEIQ9");
//                    intent.putExtra("imToken","wscWuEYbeAq2R7ClYwRiDyjTZVRciOKw26a936fvrxC58klG6+y87ZSucGSGLhGVgIkzGR0VBcgxBMyBI9nWCe7CwvKubdm01DSjftZln9WON45NdmyEmZ96CVNpTmpJqZ+\\/bZM5UAyLPGR0NWOxNrQH5LnfTldM4Rurvg0keYY=");
//                    intent.putStringArrayListExtra("groupsId",strings);
//                    setResult(RESULT_OK,intent);
//                    finish();
                }catch (Exception e){
                    e.printStackTrace();
                }
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

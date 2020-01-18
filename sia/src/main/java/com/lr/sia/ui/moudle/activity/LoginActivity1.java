package com.lr.sia.ui.moudle.activity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.jaeger.library.StatusBarUtil;
import com.lr.sia.R;
import com.lr.sia.api.MethodUrl;
import com.lr.sia.basic.BasicActivity;
import com.lr.sia.basic.MbsConstans;
import com.lr.sia.ui.moudle5.dialog.ChoosePopup;
import com.lr.sia.utils.tool.JSONUtil;
import com.lr.sia.utils.tool.SPUtils;
import com.lr.sia.utils.tool.UtilTools;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cn.wildfire.chat.kit.ChatManagerHolder;

/**
 * @author LaiRui
 */
public class LoginActivity1 extends BasicActivity implements CompoundButton.OnCheckedChangeListener, View.OnClickListener {
    private ImageView imgLoginClearUid, imgLoginClearPsw;
    private TextView tvChooseLanguage, forgetPassTv, codeRegister;
    private EditText editUid, editPsw;
    private ToggleButton togglePwd;
    private Button btnLogin;
    private String mAccount;
    private String mPassWord;
    private ChoosePopup choosePopup;
    private List<Map<String, Object>> mapList = new ArrayList<>();

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

    }

    @Override
    public int getContentView() {
        return R.layout.activity_login1;
    }

    @Override
    public void init() {
        choosePopup = new ChoosePopup(LoginActivity1.this);
        tvChooseLanguage = findViewById(R.id.tvChooseLanguage);
        editUid = findViewById(R.id.edit_uid);
        imgLoginClearUid = findViewById(R.id.img_login_clear_uid);
        editPsw = findViewById(R.id.edit_psw);
        imgLoginClearPsw = findViewById(R.id.img_login_clear_psw);
        togglePwd = findViewById(R.id.togglePwd);
        btnLogin = findViewById(R.id.btn_login);
        forgetPassTv = findViewById(R.id.forget_pass_tv);
        codeRegister = findViewById(R.id.code_register);

        tvChooseLanguage.setOnClickListener(this);
        imgLoginClearUid.setOnClickListener(this);
        imgLoginClearPsw.setOnClickListener(this);
        togglePwd.setOnClickListener(this);
        btnLogin.setOnClickListener(this);
        forgetPassTv.setOnClickListener(this);
        codeRegister.setOnClickListener(this);
        StatusBarUtil.setTranslucentForImageView(this, MbsConstans.ALPHA, null);
        SPUtils.put(this, MbsConstans.SharedInfoConstans.LOGIN_OUT, true);
        String account = SPUtils.get(this, MbsConstans.SharedInfoConstans.LOGIN_ACCOUNT, "") + "";

        editUid.setText(account);
        if (!UtilTools.empty(account)) {
            editUid.setSelection(account.length());
        }
        editUid.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (editUid.getText().toString().length() > 0) {
                    imgLoginClearUid.setVisibility(View.VISIBLE);
                    if (editPsw.getText().toString().length() > 0) {
                        btnLogin.setEnabled(true);
                    } else {
                        editPsw.setText("");
                        btnLogin.setEnabled(false);
                    }
                } else {
                    editPsw.setText("");
                    btnLogin.setEnabled(false);
                    imgLoginClearUid.setVisibility(View.INVISIBLE);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
        editPsw.addTextChangedListener(new TextWatcher() {

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count,
                                          int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (editPsw.getText().toString().length() > 0) {
                    imgLoginClearPsw.setVisibility(View.VISIBLE);
                    if (editUid.getText().toString().length() > 0) {
                        btnLogin.setEnabled(true);
                    } else {
                        btnLogin.setEnabled(false);
                    }
                } else {
                    btnLogin.setEnabled(false);
                    imgLoginClearPsw.setVisibility(View.INVISIBLE);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        editUid.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    if (!UtilTools.empty(editUid.getText().toString())) {
                        imgLoginClearUid.setVisibility(View.VISIBLE);
                        imgLoginClearPsw.setVisibility(View.GONE);
                    } else {
                        imgLoginClearUid.setVisibility(View.GONE);
                    }
                } else {
                    imgLoginClearUid.setVisibility(View.GONE);
                }
            }
        });
        editPsw.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    if (!UtilTools.empty(editPsw.getText().toString())) {
                        imgLoginClearPsw.setVisibility(View.VISIBLE);
                        imgLoginClearUid.setVisibility(View.GONE);
                    } else {
                        imgLoginClearPsw.setVisibility(View.GONE);
                    }

                } else {
                    imgLoginClearPsw.setVisibility(View.GONE);
                }
            }
        });

        togglePwd.setOnCheckedChangeListener(this);

        mapList.clear();
        String[] spinnerItems = {"中文简体", "English", "日本語", "한국어"};
        for (int i = 0; i < spinnerItems.length; i++) {
            Map<String, Object> langMap = new HashMap<>();
            langMap.put("key", i + 1 + "");
            langMap.put("value", spinnerItems[i]);
            mapList.add(langMap);
        }
        int languageSelect = (int) SPUtils.get("languageSelect", -1);
        tvChooseLanguage.setText(spinnerItems[languageSelect == -1 ? 0 : languageSelect]);
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
            case MethodUrl.USER_USERLOGIN://登录操作返回结果
                switch (tData.get("code") + "") {
                    case "1":
                        Map<String, Object> dataMap = (Map<String, Object>) tData.get("data");
                        MbsConstans.ACCESS_TOKEN = dataMap.get("token") + "";
//                        if (!UtilTools.empty(dataMap.get("ry_data"))) {
//                            MbsConstans.RONGYUN_MAP = (Map<String, Object>) dataMap.get("ry_data");
//                            SPUtils.put(LoginActivity1.this, MbsConstans.SharedInfoConstans.RONGYUN_DATA, JSONUtil.getInstance().objectToJson(MbsConstans.RONGYUN_MAP));
//                        }
//                        SPUtils.put(LoginActivity1.this, MbsConstans.SharedInfoConstans.ACCESS_TOKEN, MbsConstans.ACCESS_TOKEN);
//                        SPUtils.put(LoginActivity1.this, MbsConstans.SharedInfoConstans.LOGIN_ACCOUNT, mAccount + "");
//                        SPUtils.put(LoginActivity1.this, MbsConstans.SharedInfoConstans.LOGIN_PASSWORD, mPassWord + "");
                        if (!UtilTools.empty(dataMap.get("im_data"))) {
                            MbsConstans.RONGYUN_MAP = (Map<String, Object>) dataMap.get("im_data");
                            SPUtils.put(LoginActivity1.this, MbsConstans.SharedInfoConstans.RONGYUN_DATA, JSONUtil.getInstance().objectToJson(MbsConstans.RONGYUN_MAP));
                        }
                        SPUtils.put(LoginActivity1.this, MbsConstans.SharedInfoConstans.ACCESS_TOKEN, MbsConstans.ACCESS_TOKEN);
                        SPUtils.put(LoginActivity1.this, MbsConstans.SharedInfoConstans.LOGIN_ACCOUNT, mAccount + "");
                        SPUtils.put(LoginActivity1.this, MbsConstans.SharedInfoConstans.LOGIN_PASSWORD, mPassWord + "");
                        SharedPreferences sp = getSharedPreferences("config", Context.MODE_PRIVATE);
                        sp.edit().putString("id", MbsConstans.RONGYUN_MAP.get("im_id") + "")
                                .putString("token", MbsConstans.RONGYUN_MAP.get("im_token") + "")
                                .apply();
                        intent = new Intent(LoginActivity1.this, MainActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);
                        finish();
                        break;
                    case "-1":
                        showToastMsg(tData.get("msg") + "");
                        break;
                    case "0":
                        showToastMsg(tData.get("msg") + "");
                        break;
                    default:
                }
                btnLogin.setEnabled(true);
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
            case R.id.tvChooseLanguage:
                choosePopup.getPopup(tvChooseLanguage, mapList);
                choosePopup.setOnChooseContentListener(new ChoosePopup.OnChooseContentListener() {
                    @Override
                    public void onChooseClickListener(int position) {
                        SPUtils.put(LoginActivity1.this, "languageSelect", position);
                        recreate();
                    }
                });
                break;
            case R.id.img_login_clear_uid:
                clearText(editUid);
                break;
            case R.id.img_login_clear_psw:
                clearText(editPsw);
                break;
            case R.id.togglePwd:
                if (togglePwd.isChecked()) {
                    //显示密码
                    editPsw.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                } else {
                    //隐藏密码
                    editPsw.setTransformationMethod(PasswordTransformationMethod.getInstance());
                }
                break;
            case R.id.btn_login:
                loginAction();
                btnLogin.setEnabled(false);
                break;
            case R.id.forget_pass_tv:
                intent = new Intent(LoginActivity1.this, ForgetPassActivity1.class);
                startActivity(intent);
                break;
            case R.id.code_register:
                intent = new Intent(LoginActivity1.this, RegisterActivity1.class);
                startActivity(intent);
                break;
            default:
        }
    }

    /**
     * 清空控件文本
     */
    private void clearText(EditText edit) {
        edit.setText("");
    }

    private void loginAction() {
        try {
            if (UtilTools.isEmpty(editUid, getResources().getString(R.string.login_account_tip2))) {
                btnLogin.setEnabled(true);
                return;
            }

            if (UtilTools.isEmpty(editPsw, getResources().getString(R.string.please_input_pass_code))) {
                btnLogin.setEnabled(true);
                return;
            }
            mAccount = editUid.getText() + "";
            mPassWord = editPsw.getText() + "";
            Map<String, Object> map = new HashMap<>();
            Map<String, String> headMap = new HashMap<>();
            map.put("user_account", mAccount);
            map.put("login_pwd", mPassWord);
            map.put("client_id", ChatManagerHolder.gChatManager.getClientId());
            mRequestPresenterImp.requestPostToMap(headMap, MethodUrl.USER_USERLOGIN, map);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}


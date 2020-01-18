package com.lr.sia.ui.moudle.activity;

import android.content.Intent;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.lr.sia.BuildConfig;
import com.lr.sia.R;
import com.lr.sia.api.MethodUrl;
import com.lr.sia.basic.BasicActivity;
import com.lr.sia.basic.MbsConstans;
import com.lr.sia.ui.moudle5.dialog.PhoneAreaPopup;
import com.lr.sia.utils.CountDownUtils;
import com.lr.sia.utils.tool.SPUtils;
import com.lr.sia.utils.tool.UtilTools;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ForgetPassActivity extends BasicActivity implements View.OnClickListener {
    private ImageView ivBack, ivLogin, ivRegister;
    private TextView tvTitle, tvGetCode, tvChoosePhone;
    private LinearLayout llLoginView, llRegisterView, llPass, llCheck;
    private EditText etAccount, etVerification, etPass, etPassAgain;
    private Button btnLogin;
    private CountDownUtils countDownUtils;
    private ToggleButton togglePwd1, togglePwd;
    private List<String> phoneArea;
    private PhoneAreaPopup phoneAreaPopup;

    @Override
    public int getContentView() {
        return R.layout.activity_forget_pass;
    }

    @Override
    public void init() {
        phoneAreaPopup = new PhoneAreaPopup(ForgetPassActivity.this);
        countDownUtils = new CountDownUtils();
        ivBack = findViewById(R.id.ivBack);
        tvTitle = findViewById(R.id.tvTitle);
        llLoginView = findViewById(R.id.llLoginView);
        ivLogin = findViewById(R.id.ivLogin);
        llRegisterView = findViewById(R.id.llRegisterView);
        llPass = findViewById(R.id.llPass);
        llCheck = findViewById(R.id.llCheck);
        ivRegister = findViewById(R.id.ivRegister);
        etAccount = findViewById(R.id.etAccount);
        etVerification = findViewById(R.id.etVerification);
        tvGetCode = findViewById(R.id.tvGetCode);
        tvChoosePhone = findViewById(R.id.tvChoosePhone);
        btnLogin = findViewById(R.id.btn_login);
        etPass = findViewById(R.id.etPass);
        togglePwd1 = findViewById(R.id.togglePwd1);
        togglePwd = findViewById(R.id.togglePwd);
        etPassAgain = findViewById(R.id.etPassAgain);

        ivBack.setOnClickListener(this);
        btnLogin.setOnClickListener(this);
        tvGetCode.setOnClickListener(this);
        llLoginView.setOnClickListener(this);
        llRegisterView.setOnClickListener(this);
        togglePwd.setOnClickListener(this);
        togglePwd1.setOnClickListener(this);
        tvChoosePhone.setOnClickListener(this);

        llLoginView.setSelected(true);

        etAccount.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count,
                                          int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (etAccount.getText().toString().length() > 0) {
                    if (etVerification.getText().toString().length() > 0) {
                        btnLogin.setEnabled(true);
                    } else {
                        etVerification.setText("");
                        btnLogin.setEnabled(false);
                    }
                } else {
                    etVerification.setText("");
                    btnLogin.setEnabled(false);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        etVerification.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count,
                                          int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (etVerification.getText().toString().length() > 0) {
                    if (etAccount.getText().toString().length() > 0) {
                        btnLogin.setEnabled(true);
                    } else {
                        btnLogin.setEnabled(false);
                    }
                } else {
                    btnLogin.setEnabled(false);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
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
            case MethodUrl.COMMON_SENDSMSVERIFY:
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
                switch (tData.get("code") + "") {
                    case "1":// 成功
                        btnLogin.setText(R.string.finish);
                        llPass.setVisibility(View.VISIBLE);
                        llCheck.setVisibility(View.GONE);
                        break;
                    case "-1": // 超时
                        btnLogin.setText(R.string.next);
                        showToastMsg(tData.get("msg") + "");
                        break;
                    case "0": // 失败
                        etVerification.setText("");
                        btnLogin.setText(R.string.next);
                        showToastMsg(tData.get("msg") + "");
                        break;
                    default:
                }
                break;
            case MethodUrl.USER_EDITLOGINPWD:
                switch (tData.get("code") + "") {
                    case "1":// 成功
                        finish();
                        break;
                    case "-1": // 超时
                        btnLogin.setText(R.string.next);
                        showToastMsg(tData.get("msg") + "");
                        break;
                    case "0": // 失败
                        etVerification.setText("");
                        btnLogin.setText(R.string.next);
                        showToastMsg(tData.get("msg") + "");
                        break;
                    default:
                }
                break;
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
                        etVerification.setText("");
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
            case R.id.ivBack:
                if (llPass.getVisibility() == View.VISIBLE) {
                    llCheck.setVisibility(View.VISIBLE);
                    llPass.setVisibility(View.GONE);
                } else if (llCheck.getVisibility() == View.VISIBLE) {
                    finish();
                }
                break;
            case R.id.btn_login:
                if (llCheck.getVisibility() == View.VISIBLE) {
                    checkCodeAction();
//                    btnLogin.setText(R.string.finish);
//                    llPass.setVisibility(View.VISIBLE);
//                    llCheck.setVisibility(View.GONE);
                } else if (llPass.getVisibility() == View.VISIBLE) {
                    changePassAction();
                }
                break;
            case R.id.tvGetCode:
                getCodeAction();
                break;
            case R.id.llLoginView:
                llLoginView.setSelected(true);
                llRegisterView.setSelected(false);
                etAccount.setHint(R.string.inputPhoneNum);
                etAccount.setInputType(InputType.TYPE_CLASS_PHONE);
                ivLogin.setVisibility(View.VISIBLE);
                tvChoosePhone.setVisibility(View.VISIBLE);
                ivRegister.setVisibility(View.INVISIBLE);
                ivRegister.setVisibility(View.GONE);
                break;
            case R.id.llRegisterView:
                llLoginView.setSelected(false);
                llRegisterView.setSelected(true);
                etAccount.setInputType(InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS);
                etAccount.setHint(R.string.inputEmailAccount);
                ivLogin.setVisibility(View.INVISIBLE);
                ivRegister.setVisibility(View.VISIBLE);
                tvChoosePhone.setVisibility(View.GONE);
                break;
            case R.id.togglePwd:
                if (togglePwd.isChecked()) {
                    // 显示密码
                    etPass.setTransformationMethod(
                            HideReturnsTransformationMethod.getInstance());
                } else {
                    // 隐藏密码
                    etPass.setTransformationMethod(
                            PasswordTransformationMethod.getInstance());
                }
                break;
            case R.id.togglePwd1:
                if (togglePwd1.isChecked()) {
                    //显示密码
                    etPassAgain.setTransformationMethod(
                            HideReturnsTransformationMethod.getInstance());
                } else {
                    //隐藏密码
                    etPassAgain.setTransformationMethod(
                            PasswordTransformationMethod.getInstance());
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
            default:
        }
    }

    private void changePassAction() {
        Map<String, Object> map = new HashMap<>();
        String oldPass = etPass.getText().toString().trim() + "";
        String newPass = etPassAgain.getText().toString().trim() + "";
        if (UtilTools.empty(MbsConstans.ACCESS_TOKEN)) {
            MbsConstans.ACCESS_TOKEN = SPUtils.get(MbsConstans.ACCESS_TOKEN, "").toString();
        }

        map.put("token", MbsConstans.ACCESS_TOKEN);
        map.put("login_pwd", oldPass);
        map.put("re_login_pwd", newPass);
        map.put("verify_code", etVerification.getText().toString());
        map.put("user_account", etAccount.getText().toString());
        if (llLoginView.isSelected()) {
            map.put("account_type", "1");
        } else {
            map.put("account_type", "2");
        }
        mRequestPresenterImp.requestPostToMap(MethodUrl.USER_EDITLOGINPWD, map);
    }

    private void getCodeAction() {
        // TODO: 2019/10/23 验证码获取
        Map<String, Object> map = new HashMap<>();
        String account = etAccount.getText().toString().trim();
        if (llLoginView.isSelected()) {
            String phoneArea = tvChoosePhone.getText().toString();
            map.put("account_type", "1");
            map.put("account", phoneArea + account);
            if (UtilTools.isEmpty(etAccount, getResources().getString(R.string.phoneNumber))) {
                btnLogin.setEnabled(true);
                return;
            }
        } else if (llRegisterView.isSelected()) {
            map.put("account_type", "2");
            map.put("account", account);
            if (UtilTools.isEmpty(etAccount, getResources().getString(R.string.email))) {
                btnLogin.setEnabled(true);
                return;
            }
        } else {
            return;
        }
        map.put("verify_type", "3");
        Map<String, String> mHeaderMap = new HashMap<String, String>();
        mRequestPresenterImp.requestPostToMap(mHeaderMap, MethodUrl.COMMON_SENDSMSVERIFY, map);
    }

    private void checkCodeAction() {
        if (UtilTools.isEmpty(etAccount, getResources().getString(R.string.login_account_tip2))) {
            btnLogin.setEnabled(true);
            return;
        }

        if (UtilTools.isEmpty(etVerification, getResources().getString(R.string.test_code))) {
            btnLogin.setEnabled(true);
            return;
        }
        Map<String, Object> map = new HashMap<>();
        String account = etAccount.getText().toString().trim() + "";
        String phoneArea = tvChoosePhone.getText().toString().trim() + "";

        if (llLoginView.isSelected()) {
            map.put("account_type", "1");
            map.put("account", phoneArea + account);
        } else if (llRegisterView.isSelected()) {
            map.put("account_type", "2");
            map.put("account", account);
        } else {
            return;
        }
        map.put("verify_type", "3");
        map.put("verify_code", etVerification.getText().toString().trim() + "");
        mRequestPresenterImp.requestPostToMap(MethodUrl.COMMON_CHECKSMSVERIFY, map);
    }
}

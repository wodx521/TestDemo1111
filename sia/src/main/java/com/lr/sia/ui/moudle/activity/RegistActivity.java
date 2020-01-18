package com.lr.sia.ui.moudle.activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.ToggleButton;

import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;

import com.jaeger.library.StatusBarUtil;
import com.lr.sia.BuildConfig;
import com.lr.sia.R;
import com.lr.sia.api.MethodUrl;
import com.lr.sia.basic.BasicActivity;
import com.lr.sia.basic.MbsConstans;
import com.lr.sia.mvp.view.RequestView;
import com.lr.sia.mywidget.CustomerTextWatcher;
import com.lr.sia.utils.CountDownUtils;
import com.lr.sia.utils.tool.UtilTools;

import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class RegistActivity extends BasicActivity implements RequestView {

    @BindView(R.id.left_back_lay)
    LinearLayout leftBackLay;
    @BindView(R.id.title_text)
    TextView titleText;
    @BindView(R.id.etInviteCode)
    EditText etInviteCode;
    @BindView(R.id.etLoginPass)
    EditText etLoginPass;
    @BindView(R.id.etLoginPass1)
    EditText etLoginPass1;
    @BindView(R.id.etTradePass)
    EditText etTradePass;
    @BindView(R.id.etTradePass1)
    EditText etTradePass1;
    @BindView(R.id.etInputAccount)
    EditText etInputAccount;
    @BindView(R.id.ivClearAccount)
    ImageView ivClearAccount;
    @BindView(R.id.ivAccount)
    ImageView ivAccount;
    @BindView(R.id.etVerification)
    EditText etVerification;
    @BindView(R.id.togglePwd)
    ToggleButton togglePwd;
    @BindView(R.id.togglePwd_again)
    ToggleButton togglePwdAgain;
    @BindView(R.id.togglePay)
    ToggleButton togglePay;
    @BindView(R.id.togglePay_again)
    ToggleButton togglePayAgain;
    @BindView(R.id.tvGetCode)
    TextView tvGetCode;
    @BindView(R.id.bt_next)
    Button btNext;
    @BindView(R.id.llLoginNow1)
    LinearLayout llLoginNow1;
    private String type1; // 1 传递过来的是手机号 2, 传递的是邮箱
    private CountDownUtils countDownUtils;
    private String previousAccount;
    private HashMap<String, Object> mapParams = new HashMap<>();
    private String phoneArea;

    /**
     * @descriptoin 请求前加载progress
     * @author dc
     * @date 2017/2/16 11:00
     */
    @Override
    public void showProgress() {
        showProgressDialog();
    }

    /**
     * @descriptoin 请求结束之后隐藏progress
     * @author dc
     * @date 2017/2/16 11:01
     */
    @Override
    public void disimissProgress() {
        dismissProgressDialog();
    }

    /**
     * @param tData 数据类型
     * @param mType
     * @descriptoin 请求数据成功
     * @author dc
     * @date 2017/2/16 11:01
     */
    @Override
    public void loadDataSuccess(Map<String, Object> tData, String mType) {
        btNext.setEnabled(true);
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
                mapParams.clear();
                switch (tData.get("code") + "") {
                    case "1":// 成功
                        String loginPass = etLoginPass.getText().toString().trim();
                        String tradePass = etTradePass.getText().toString().trim();
                        String account = etInputAccount.getText().toString().trim();
                        if (account.matches(MbsConstans.PHONE_REGEX) || account.matches(MbsConstans.EMAIL_REGEX)) {
                            mapParams.put("user_phone", previousAccount);
                            mapParams.put("user_email", account);
                            mapParams.put("user_phone_pre", phoneArea);
                            mapParams.put("login_pwd", loginPass);
                            mapParams.put("pay_pwd", tradePass);
                            mapParams.put("invite_code", etInviteCode.getText().toString().trim());
                        }
                        Intent intent = new Intent(RegistActivity.this, RemeberCodeActivity.class);
                        intent.putExtra("registParam", mapParams);
                        startActivityForResult(intent, MbsConstans.IS_APPROVE_RIGHT);
//                        registAction();
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
            case MethodUrl.USER_USERREG:
                switch (tData.get("code") + "") {
                    case "1":// 成功
                        Intent intent = new Intent(RegistActivity.this, RemeberCodeActivity.class);
                        startActivityForResult(intent, MbsConstans.IS_APPROVE_RIGHT);
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
            default:
        }
    }

    /**
     * @param map
     * @param mType
     * @descriptoin 请求数据错误
     * @date 2017/2/16 11:01
     */
    @Override
    public void loadDataError(Map<String, Object> map, String mType) {

    }

    public void registAction() {
        // TODO: 2019/10/23 验证码获取
        String notice = "";
        if ("1".equals(type1)) {
            notice = getString(R.string.sign_up_email_hint);
        } else if ("2".equals(type1)) {
            notice = getString(R.string.phoneNumber);
        }
        if (UtilTools.isEmpty(etInputAccount, notice)) {
            return;
        }
        if (UtilTools.isEmpty(etInviteCode, getString(R.string.inviteCode))) {
            return;
        }
        if (UtilTools.isEmpty(etTradePass, getString(R.string.tradePass))) {
            return;
        }
        if (UtilTools.isEmpty(etTradePass1, getString(R.string.tradePass))) {
            return;
        }
        if (UtilTools.isEmpty(etLoginPass, getString(R.string.loginPass))) {
            return;
        }
        if (UtilTools.isEmpty(etLoginPass1, getString(R.string.loginPass))) {
            return;
        }
        if (UtilTools.isEmpty(etVerification, getString(R.string.verificationCode))) {
            return;
        }
        String loginPass = etLoginPass.getText().toString().trim();
        String loginPass1 = etLoginPass1.getText().toString().trim();
        String tradePass = etTradePass.getText().toString().trim();
        String tradePass1 = etTradePass1.getText().toString().trim();

        // todo
        if (loginPass.matches(MbsConstans.PASS_REGEX)
                && loginPass1.matches(MbsConstans.PASS_REGEX)
                && loginPass.equals(loginPass1)
                && tradePass.equals(tradePass1)) {
            Map<String, Object> map = new HashMap<>();
            String account = etInputAccount.getText().toString().trim() + "";
            String inviteCode = etInviteCode.getText().toString().trim() + "";
            if (account.matches(MbsConstans.PHONE_REGEX) || account.matches(MbsConstans.EMAIL_REGEX)) {
                if (account.matches(MbsConstans.PHONE_REGEX)) {
                    map.put("user_phone", account);
                    map.put("user_email", previousAccount);
                } else {
                    map.put("user_phone", previousAccount);
                    map.put("user_email", account);
                }
                map.put("login_pwd", loginPass);
                map.put("pay_pwd", tradePass);
                map.put("invite_code", inviteCode);
                mRequestPresenterImp.requestPostToMap(MethodUrl.USER_USERREG, map);
            } else {
                if (!account.matches(MbsConstans.PHONE_REGEX)) {
                    showToastMsg(R.string.inputRightPhone);
                } else {
                    showToastMsg(R.string.inputRigthEmail);
                }
            }
        } else {
            if (!loginPass.matches(MbsConstans.PASS_REGEX)) {
                showToastMsg(R.string.seal_login_toast_passwords_invalid);
            } else if (!loginPass1.matches(MbsConstans.PASS_REGEX)) {
                showToastMsg(R.string.seal_login_toast_passwords_invalid);
            } else if (!loginPass.equals(loginPass1)) {
                showToastMsg(R.string.loginPassErrorNotice);
            } else {
                showToastMsg(R.string.tradePassErrorNotice);
            }
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case MbsConstans.IS_APPROVE_RIGHT:
                    setResult(RESULT_OK);
                    finish();
                    break;
                default:
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // TODO: add setContentView(...) invocation
        ButterKnife.bind(this);
    }

    @Override
    public int getContentView() {
        return R.layout.activity_regist;
    }

    @Override
    public void init() {
        StatusBarUtil.setColorForSwipeBack(this, ContextCompat.getColor(this, MbsConstans.TOP_BAR_COLOR), MbsConstans.ALPHA);

        titleText.setText(R.string.registerUser);
        titleText.setCompoundDrawables(null, null, null, null);
        countDownUtils = new CountDownUtils();

        Intent intent = getIntent();
        type1 = intent.getStringExtra("type");
        previousAccount = intent.getStringExtra("account");
        phoneArea = intent.getStringExtra("phoneArea");
//        if ("1".equals(type1)) {
//            etInputAccount.setHint(R.string.inputEmailAccount);
//        } else if ("2".equals(type1)) {
//            etInputAccount.setHint(R.string.inputPhoneAccount);
//        }

        etInviteCode.addTextChangedListener(new CustomerTextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.toString().trim().length() > 0
                        && etTradePass.getText().toString().trim().length() > 5
                        && etTradePass1.getText().toString().trim().length() > 5
                        && etLoginPass.getText().toString().trim().length() > 0
                        && etLoginPass1.getText().toString().trim().length() > 0
//                        && etInputAccount.getText().toString().trim().length() > 0
//                        && etVerification.getText().toString().trim().length() > 0
                ) {
                    btNext.setEnabled(true);
                } else {
                    btNext.setEnabled(false);
                }
            }
        });
        etLoginPass.addTextChangedListener(new CustomerTextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.toString().trim().length() > 5
                        && etTradePass.getText().toString().trim().length() > 5
                        && etTradePass1.getText().toString().trim().length() > 5
                        && etInviteCode.getText().toString().trim().length() > 0
                        && etLoginPass1.getText().toString().trim().length() > 5
//                        && etInputAccount.getText().toString().trim().length() > 0
//                        && etVerification.getText().toString().trim().length() > 0
                ) {
                    btNext.setEnabled(true);
                } else {
                    btNext.setEnabled(false);
                }
            }
        });
        etLoginPass1.addTextChangedListener(new CustomerTextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.toString().trim().length() > 5
                        && etTradePass.getText().toString().trim().length() > 5
                        && etTradePass1.getText().toString().trim().length() > 5
                        && etInviteCode.getText().toString().trim().length() > 0
                        && etLoginPass.getText().toString().trim().length() > 5
//                        && etInputAccount.getText().toString().trim().length() > 0
//                        && etVerification.getText().toString().trim().length() > 0
                ) {
                    btNext.setEnabled(true);
                } else {
                    btNext.setEnabled(false);
                }
            }
        });
        etTradePass.addTextChangedListener(new CustomerTextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.toString().trim().length() > 5
                        && etLoginPass1.getText().toString().trim().length() > 5
                        && etTradePass1.getText().toString().trim().length() > 5
                        && etInviteCode.getText().toString().trim().length() > 0
                        && etLoginPass.getText().toString().trim().length() > 5
//                        && etInputAccount.getText().toString().trim().length() > 0
//                        && etVerification.getText().toString().trim().length() > 0
                ) {
                    btNext.setEnabled(true);
                } else {
                    btNext.setEnabled(false);
                }
            }
        });
        etTradePass1.addTextChangedListener(new CustomerTextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.toString().trim().length() > 5
                        && etLoginPass1.getText().toString().trim().length() > 5
                        && etTradePass.getText().toString().trim().length() > 5
                        && etInviteCode.getText().toString().trim().length() > 0
                        && etLoginPass.getText().toString().trim().length() > 5
//                        && etInputAccount.getText().toString().trim().length() > 0
//                        && etVerification.getText().toString().trim().length() > 0
                ) {
                    btNext.setEnabled(true);
                } else {
                    btNext.setEnabled(false);
                }
            }
        });

//        etInputAccount.addTextChangedListener(new CustomerTextWatcher() {
//            @Override
//            public void onTextChanged(CharSequence s, int start, int before, int count) {
//                if (s.toString().trim().length() > 0
//                        && etLoginPass1.getText().toString().trim().length() > 5
//                        && etTradePass.getText().toString().trim().length() > 5
//                        && etInviteCode.getText().toString().trim().length() > 0
//                        && etLoginPass.getText().toString().trim().length() > 5
//                        && etTradePass1.getText().toString().trim().length() > 5
//                        && etVerification.getText().toString().trim().length() > 0) {
//                    ivClearAccount.setVisibility(View.VISIBLE);
//                    btNext.setEnabled(true);
//                } else {
//                    btNext.setEnabled(false);
//                    ivClearAccount.setVisibility(View.INVISIBLE);
//                }
//            }
//        });
//        etVerification.addTextChangedListener(new CustomerTextWatcher() {
//            @Override
//            public void onTextChanged(CharSequence s, int start, int before, int count) {
//                if (s.toString().trim().length() > 0
//                        && etLoginPass1.getText().toString().trim().length() > 5
//                        && etTradePass.getText().toString().trim().length() > 5
//                        && etInviteCode.getText().toString().trim().length() > 0
//                        && etLoginPass.getText().toString().trim().length() > 5
//                        && etTradePass1.getText().toString().trim().length() > 5
//                        && etInputAccount.getText().toString().trim().length() > 0) {
//                    btNext.setEnabled(true);
//                } else {
//                    btNext.setEnabled(false);
//                }
//            }
//        });
    }

    @OnClick({R.id.left_back_lay, R.id.tvGetCode, R.id.bt_next, R.id.llLoginNow1, R.id.ivClearAccount,
            R.id.togglePwd, R.id.togglePwd_again, R.id.togglePay, R.id.togglePay_again})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.left_back_lay:
                finish();
                break;
            case R.id.ivClearAccount:
                etInputAccount.setText("");
                break;
            case R.id.tvGetCode:
                getCodeAction();
                break;
            case R.id.togglePwd:
                if (togglePwd.isChecked()) {
                    //显示密码
                    etLoginPass.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                } else {
                    //隐藏密码
                    etLoginPass.setTransformationMethod(PasswordTransformationMethod.getInstance());
                }
                break;
            case R.id.togglePwd_again:
                if (togglePwdAgain.isChecked()) {
                    //显示密码
                    etLoginPass1.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                } else {
                    //隐藏密码
                    etLoginPass1.setTransformationMethod(PasswordTransformationMethod.getInstance());
                }
                break;
            case R.id.togglePay:
                if (togglePay.isChecked()) {
                    //显示密码
                    etTradePass.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                } else {
                    //隐藏密码
                    etTradePass.setTransformationMethod(PasswordTransformationMethod.getInstance());
                }
                break;
            case R.id.togglePay_again:
                if (togglePayAgain.isChecked()) {
                    //显示密码
                    etTradePass1.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                } else {
                    //隐藏密码
                    etTradePass1.setTransformationMethod(PasswordTransformationMethod.getInstance());
                }
                break;
            case R.id.bt_next:

                if (UtilTools.isEmpty(etInviteCode, getString(R.string.inviteCode))) {
                    return;
                }
//                if (UtilTools.isEmpty(etInputAccount, notice)) {
//                    return;
//                }
                if (UtilTools.isEmpty(etTradePass, getString(R.string.tradePass))) {
                    return;
                }
                if (UtilTools.isEmpty(etTradePass1, getString(R.string.tradePass))) {
                    return;
                }
                if (UtilTools.isEmpty(etLoginPass, getString(R.string.loginPass))) {
                    return;
                }
                if (UtilTools.isEmpty(etLoginPass1, getString(R.string.loginPass))) {
                    return;
                }
//                if (UtilTools.isEmpty(etVerification, getString(R.string.verificationCode))) {
//                    return;
//                }

                checkCodeAction();
                break;
            case R.id.llLoginNow1:
                setResult(RESULT_OK);
                finish();
                break;
            default:
        }
    }

    private void checkCodeAction() {
        String loginPass = etLoginPass.getText().toString().trim();
        String loginPass1 = etLoginPass1.getText().toString().trim();
        String tradePass = etTradePass.getText().toString().trim();
        String tradePass1 = etTradePass1.getText().toString().trim();
        if (loginPass.matches(MbsConstans.PASS_REGEX)
                && loginPass1.matches(MbsConstans.PASS_REGEX)
                && loginPass.equals(loginPass1)
                && tradePass.equals(tradePass1)) {
            if ("1".equals(type1)) {
                mapParams.put("user_phone", previousAccount);
                mapParams.put("user_phone_pre", phoneArea);
                mapParams.put("user_email", "");
            } else if ("2".equals(type1)) {
                mapParams.put("user_email", previousAccount);
                mapParams.put("user_phone_pre", "");
                mapParams.put("user_phone", "");
            }
            mapParams.put("login_pwd", loginPass);
            mapParams.put("pay_pwd", tradePass);
            mapParams.put("invite_code", etInviteCode.getText().toString().trim());
            Intent intent = new Intent(RegistActivity.this, RemeberCodeActivity.class);
            intent.putExtra("registParam", mapParams);
            startActivityForResult(intent, MbsConstans.IS_APPROVE_RIGHT);
//            String account = etInputAccount.getText().toString().trim() + "";
//            map.put("account", account);
//            if (account.matches(MbsConstans.EMAIL_REGEX)) {
//                map.put("account_type", "2");
//                map.put("verify_type", "1");
//                map.put("verify_code", etVerification.getText().toString().trim() + "");
//                mRequestPresenterImp.requestPostToMap(MethodUrl.COMMON_CHECKSMSVERIFY, map);
//            } else {
//                if (!account.matches(MbsConstans.PHONE_REGEX)) {
//                    showToastMsg(R.string.inputRightPhone);
//                } else {
//                    showToastMsg(R.string.inputRigthEmail);
//                }
//            }
        } else {
            if (!loginPass.matches(MbsConstans.PASS_REGEX)) {
                showToastMsg(R.string.seal_login_toast_passwords_invalid);
            } else if (!loginPass1.matches(MbsConstans.PASS_REGEX)) {
                showToastMsg(R.string.seal_login_toast_passwords_invalid);
            } else if (!loginPass.equals(loginPass1)) {
                showToastMsg(R.string.loginPassErrorNotice);
            } else {
                showToastMsg(R.string.tradePassErrorNotice);
            }
        }
    }

    private void getCodeAction() {
        // TODO: 2019/10/23 验证码获取
        Map<String, Object> map = new HashMap<>();
        String account = etInputAccount.getText().toString().trim();
        if ("1".equals(type1)) {
            if (account.matches(MbsConstans.EMAIL_REGEX)) {
                map.put("account_type", "2");
                map.put("account", account);
                map.put("verify_type", "1");
                mRequestPresenterImp.requestPostToMap(MethodUrl.COMMON_SENDSMSVERIFY, map);
            } else {
                showToastMsg(R.string.inputEmail);
            }
        } else if ("2".equals(type1)) {
            if (account.matches(getString(R.string.phoneRegex))) {
                map.put("account_type", "1");
                map.put("account", account);
                map.put("verify_type", "1");
                mRequestPresenterImp.requestPostToMap(MethodUrl.COMMON_SENDSMSVERIFY, map);
            } else {
                showToastMsg(R.string.inputPhone);
            }
        }
    }
}

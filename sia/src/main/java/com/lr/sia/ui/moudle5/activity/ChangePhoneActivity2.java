package com.lr.sia.ui.moudle5.activity;

import android.text.InputType;
import android.text.TextUtils;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.ToggleButton;

import androidx.core.content.ContextCompat;

import com.jaeger.library.StatusBarUtil;
import com.lr.sia.BuildConfig;
import com.lr.sia.R;
import com.lr.sia.api.MethodUrl;
import com.lr.sia.basic.BasicActivity;
import com.lr.sia.basic.MbsConstans;
import com.lr.sia.ui.moudle5.dialog.PhoneAreaPopup;
import com.lr.sia.utils.CountDownUtils;
import com.lr.sia.utils.tool.JSONUtil;
import com.lr.sia.utils.tool.SPUtils;
import com.lr.sia.utils.tool.UtilTools;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ChangePhoneActivity2 extends BasicActivity implements View.OnClickListener {
    private ImageView backImg, iv1, iv2;
    private TextView tvTitle, tvGetCode1, tvConfirm, tvChoosePhone1, tvChoosePhone;
    private ToggleButton tbNewPass;
    private EditText etOldAccount, etTradePass, etNewPhone, etCode1;
    private String type1,from;
    private CountDownUtils countDownUtils;
    private PhoneAreaPopup phoneAreaPopup;
    private List<String> phoneArea;

    @Override
    public int getContentView() {
        return R.layout.activity_change_phone2;
    }

    @Override
    public void init() {
        phoneAreaPopup = new PhoneAreaPopup(ChangePhoneActivity2.this);
        StatusBarUtil.setColorForSwipeBack(this, ContextCompat.getColor(this, MbsConstans.TOP_BAR_COLOR), MbsConstans.ALPHA);
        countDownUtils = new CountDownUtils();
        backImg = findViewById(R.id.backImg);
        iv1 = findViewById(R.id.iv1);
        iv2 = findViewById(R.id.iv2);
        tvTitle = findViewById(R.id.tvTitle);
        tvChoosePhone = findViewById(R.id.tvChoosePhone);
        tvChoosePhone1 = findViewById(R.id.tvChoosePhone1);
        etOldAccount = findViewById(R.id.etOldAccount);
        etTradePass = findViewById(R.id.etTradePass);
        tbNewPass = findViewById(R.id.tbNewPass);
        etNewPhone = findViewById(R.id.etNewPhone);
        etCode1 = findViewById(R.id.etCode1);
        tvGetCode1 = findViewById(R.id.tvGetCode1);
        tvConfirm = findViewById(R.id.tvConfirm);

        backImg.setOnClickListener(this);
        tvGetCode1.setOnClickListener(this);
        tbNewPass.setOnClickListener(this);
        tvConfirm.setOnClickListener(this);
        tvChoosePhone1.setOnClickListener(this);
        tvChoosePhone.setOnClickListener(this);

        type1 = getIntent().getStringExtra("type");
        from = getIntent().getStringExtra("from");
        if ("1".equals(from)) {
            if ("1".equals(type1)) {
                tvTitle.setText(R.string.bindPhone);
            }else if("2".equals(type1)){
                tvTitle.setText(R.string.bindEmail);
            }
            tvConfirm.setText(getString(R.string.bind));
        } else if ("2".equals(from)) {
            tvConfirm.setText(getString(R.string.confirm));
        }
        if ("1".equals(type1)) {
            tvChoosePhone1.setVisibility(View.VISIBLE);
            tvChoosePhone.setVisibility(View.GONE);
            etOldAccount.setHint(R.string.inputEmailAccount);
            iv1.setImageResource(R.drawable.icon_email);
            iv2.setImageResource(R.drawable.icon_phone);
            etOldAccount.setInputType(InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS);
            etNewPhone.setHint(R.string.inputNewPhoneNum);
            etNewPhone.setInputType(InputType.TYPE_CLASS_PHONE);
            tvTitle.setText(R.string.changePhone);
        } else if ("2".equals(type1)) {
            tvChoosePhone1.setVisibility(View.GONE);
            tvChoosePhone.setVisibility(View.VISIBLE);
            etOldAccount.setHint(R.string.inputPhoneNum);
            iv1.setImageResource(R.drawable.icon_phone);
            iv2.setImageResource(R.drawable.icon_email);
            etOldAccount.setInputType(InputType.TYPE_CLASS_PHONE);
            etNewPhone.setHint(R.string.inputNewEmailNum);
            etNewPhone.setInputType(InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS);
            tvTitle.setText(R.string.changeEmail);
        }
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
        switch (mType) {
            case MethodUrl.COMMON_SENDSMSVERIFY:
                switch (tData.get("code") + "") {
                    case "1":
                        showToastMsg(tData.get("msg") + "");
                        tvGetCode1.setEnabled(false);
                        if (BuildConfig.DEBUG) {
                            countDownUtils.getTimer(MbsConstans.SECOND_TIME_10, tvGetCode1, getString(R.string.sendVerificationCode));
                        } else {
                            countDownUtils.getTimer(MbsConstans.SECOND_TIME_60, tvGetCode1, getString(R.string.sendVerificationCode));
                        }
                        countDownUtils.setTimeFinishListener(new CountDownUtils.CountTimeFinishListener() {
                            @Override
                            public void onTimeFinishListener() {
                                tvGetCode1.setEnabled(true);
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
            case MethodUrl.USER_EDITUSERACCOUNT2:
                switch (tData.get("code") + "") {
                    case "1":
                        String s = SPUtils.get(ChangePhoneActivity2.this, MbsConstans.SharedInfoConstans.LOGIN_INFO, "").toString();
                        MbsConstans.USER_MAP = JSONUtil.getInstance().jsonMap(s);
                        Map<String, Object> data = (Map<String, Object>) MbsConstans.USER_MAP.get("data");
                        if ("1".equals(type1)) {
                            data.put("user_phone", etNewPhone.getText().toString());
                        } else {
                            data.put("user_email", etNewPhone.getText().toString());
                        }
                        MbsConstans.USER_MAP.put("data", data);
                        SPUtils.put(ChangePhoneActivity2.this, MbsConstans.SharedInfoConstans.LOGIN_INFO, JSONUtil.getInstance().objectToJson(MbsConstans.USER_MAP));
                        showToastMsg(tData.get("msg") + "");
                        setResult(RESULT_OK);
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
                break;
            case MethodUrl.COMMON_GETPHONEPRELIST:
                switch (tData.get("code") + "") {
                    case "1":// 成功
                        phoneArea = (List<String>) tData.get("data");
                        tvChoosePhone1.setText("+86");
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

    @Override
    public void loadDataError(Map<String, Object> map, String mType) {

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.backImg:
                finish();
                break;
            case R.id.tbNewPass:
                if (tbNewPass.isChecked()) {
                    //显示密码
                    etTradePass.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                } else {
                    //隐藏密码
                    etTradePass.setTransformationMethod(PasswordTransformationMethod.getInstance());
                }
                break;
            case R.id.tvGetCode1:
                getCodeAction(etNewPhone);
                break;
            case R.id.tvConfirm:
                checkCodeAction();
                break;
            case R.id.tvChoosePhone1:
                phoneAreaPopup.getPopup(tvChoosePhone1, phoneArea);
                phoneAreaPopup.setOnChooseContentListener(new PhoneAreaPopup.OnChooseContentListener() {
                    @Override
                    public void onChooseClickListener(int position) {
                        String s = phoneArea.get(position);
                        tvChoosePhone1.setText(s);
                    }
                });
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

    private void getCodeAction(EditText editText) {
        Map<String, Object> map = new HashMap<>();
        String account = editText.getText().toString().trim();
        if ("1".equals(type1)) {
            if (!TextUtils.isEmpty(account)) {
                map.put("account_type", "1");
                map.put("account", tvChoosePhone1.getText().toString() + account);
                map.put("verify_type", "2");
                mRequestPresenterImp.requestPostToMap(MethodUrl.COMMON_SENDSMSVERIFY, map);
            } else {
                if (TextUtils.isEmpty(account)) {
                    showToastMsg(getString(R.string.inputPhone));
                } else {
                    showToastMsg(getString(R.string.inputRightPhone));
                }
            }
        } else if ("2".equals(type1)) {
            if (account.matches(MbsConstans.EMAIL_REGEX)) {
                map.put("account_type", "2");
                map.put("account", account);
                map.put("verify_type", "2");
                mRequestPresenterImp.requestPostToMap(MethodUrl.COMMON_SENDSMSVERIFY, map);
            } else {
                if (TextUtils.isEmpty(account)) {
                    showToastMsg(getString(R.string.inputEmail));
                } else {
                    showToastMsg(getString(R.string.inputRigthEmail));
                }
            }
        }
    }

    private void checkCodeAction() {
        Map<String, Object> map = new HashMap<>();
        String oldPhone = etOldAccount.getText().toString().trim() + "";
        String newPhone = etNewPhone.getText().toString().trim() + "";
        String code = etCode1.getText().toString().trim() + "";
        String tradePass = etTradePass.getText().toString().trim() + "";
        if (UtilTools.empty(MbsConstans.ACCESS_TOKEN)) {
            MbsConstans.ACCESS_TOKEN = SPUtils.get(MbsConstans.ACCESS_TOKEN, "").toString();
        }
        map.put("token", MbsConstans.ACCESS_TOKEN);
        if ("1".equals(type1)) {
            if (!TextUtils.isEmpty(oldPhone)
                    && !TextUtils.isEmpty(newPhone)
                    && !TextUtils.isEmpty(code)
                    && !TextUtils.isEmpty(tradePass)
                    && !oldPhone.equals(newPhone)) {
                map.put("account_type", "1");
                map.put("account_old", oldPhone);
                map.put("account_new", tvChoosePhone1.getText().toString() + "_" + newPhone);
                map.put("account_new_verify", code);
                map.put("pay_pwd", tradePass);
                mRequestPresenterImp.requestPostToMap(MethodUrl.USER_EDITUSERACCOUNT2, map);
            } else {
                if (TextUtils.isEmpty(oldPhone)) {
                    showToastMsg(getString(R.string.inputOldPhoneNum));
                } else if (TextUtils.isEmpty(newPhone)) {
                    showToastMsg(getString(R.string.inputNewPhoneNum));
                } else if (TextUtils.isEmpty(code)) {
                    showToastMsg(getString(R.string.inputVerificationCode));
                } else if (TextUtils.isEmpty(tradePass)) {
                    showToastMsg(getString(R.string.please_input_jiaoyi_code));
                }
            }
        } else {
            if (!TextUtils.isEmpty(oldPhone)
                    && !TextUtils.isEmpty(newPhone)
                    && !TextUtils.isEmpty(code)
                    && !TextUtils.isEmpty(tradePass)
                    && !oldPhone.equals(newPhone)) {
                map.put("account_type", "2");
                map.put("account_old", tvChoosePhone.getText().toString() + "_" + oldPhone);
                map.put("account_new", newPhone);
                map.put("account_new_verify", code);
                map.put("pay_pwd", tradePass);
                mRequestPresenterImp.requestPostToMap(MethodUrl.USER_EDITUSERACCOUNT2, map);
            } else {
                if (TextUtils.isEmpty(oldPhone)) {
                    showToastMsg(getString(R.string.inputOldEmail));
                } else if (TextUtils.isEmpty(newPhone)) {
                    showToastMsg(getString(R.string.inputNewEmailNum));
                } else if (TextUtils.isEmpty(code)) {
                    showToastMsg(getString(R.string.inputVerificationCode));
                } else if (TextUtils.isEmpty(tradePass)) {
                    showToastMsg(getString(R.string.please_input_jiaoyi_code));
                }
            }
        }
    }
}

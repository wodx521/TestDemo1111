package com.lr.sia.ui.moudle5.activity;

import android.text.InputFilter;
import android.text.InputType;
import android.text.TextUtils;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.ToggleButton;

import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;

import com.jaeger.library.StatusBarUtil;
import com.lr.sia.BuildConfig;
import com.lr.sia.R;
import com.lr.sia.api.MethodUrl;
import com.lr.sia.basic.BasicActivity;
import com.lr.sia.basic.MbsConstans;
import com.lr.sia.utils.CountDownUtils;
import com.lr.sia.utils.tool.JSONUtil;
import com.lr.sia.utils.tool.SPUtils;
import com.lr.sia.utils.tool.UtilTools;

import java.util.HashMap;
import java.util.Map;

public class ChangePassActivity extends BasicActivity implements View.OnClickListener {
    private ImageView backImg;
    private TextView tvTitle, tvGetCode, tvConfirm;
    private ToggleButton tbOldPass, tbNewPass, tbNewPassAgain;
    private CardView cvEmailCode;
    private EditText etOldPass, etNewPass, etNewPassAgain, etEmailCode;
    private String type1;
    private CountDownUtils countDownUtils;

    @Override
    public int getContentView() {
        return R.layout.activity_change_pass;
    }

    @Override
    public void init() {
        StatusBarUtil.setColorForSwipeBack(this, ContextCompat.getColor(this, MbsConstans.TOP_BAR_COLOR), MbsConstans.ALPHA);
        countDownUtils = new CountDownUtils();
        backImg = findViewById(R.id.backImg);
        tvTitle = findViewById(R.id.tvTitle);
        etOldPass = findViewById(R.id.etOldPass);
        tbOldPass = findViewById(R.id.tbOldPass);
        etNewPass = findViewById(R.id.etNewPass);
        tbNewPass = findViewById(R.id.tbNewPass);
        etNewPassAgain = findViewById(R.id.etNewPassAgain);
        tbNewPassAgain = findViewById(R.id.tbNewPassAgain);
        cvEmailCode = findViewById(R.id.cvEmailCode);
        etEmailCode = findViewById(R.id.etEmailCode);
        tvGetCode = findViewById(R.id.tvGetCode);
        tvConfirm = findViewById(R.id.tvConfirm);
        backImg.setOnClickListener(this);
        tvConfirm.setOnClickListener(this);
        tvGetCode.setOnClickListener(this);
        tbOldPass.setOnClickListener(this);
        tbNewPass.setOnClickListener(this);
        tbNewPassAgain.setOnClickListener(this);

        type1 = getIntent().getStringExtra("type");

        if ("1".equals(type1)) {
            tvTitle.setText(R.string.changeLoginPass);
            cvEmailCode.setVisibility(View.GONE);
            etNewPass.setInputType(InputType.TYPE_TEXT_VARIATION_PASSWORD|InputType.TYPE_CLASS_TEXT);
            etNewPass.setInputType(InputType.TYPE_TEXT_VARIATION_PASSWORD|InputType.TYPE_CLASS_TEXT);
            etNewPassAgain.setInputType(InputType.TYPE_TEXT_VARIATION_PASSWORD|InputType.TYPE_CLASS_TEXT);
            etNewPass.setFilters(new InputFilter[]{new InputFilter.LengthFilter(20)});
            etOldPass.setFilters(new InputFilter[]{new InputFilter.LengthFilter(20)});
            etNewPassAgain.setFilters(new InputFilter[]{new InputFilter.LengthFilter(20)});
        } else if ("2".equals(type1)) {
            tvTitle.setText(R.string.changeTradePass);
            cvEmailCode.setVisibility(View.VISIBLE);
            etOldPass.setInputType(InputType.TYPE_NUMBER_VARIATION_PASSWORD|InputType.TYPE_CLASS_NUMBER);
            etNewPass.setInputType(InputType.TYPE_NUMBER_VARIATION_PASSWORD|InputType.TYPE_CLASS_NUMBER);
            etNewPassAgain.setInputType(InputType.TYPE_NUMBER_VARIATION_PASSWORD|InputType.TYPE_CLASS_NUMBER);
            etNewPass.setFilters(new InputFilter[]{new InputFilter.LengthFilter(6)});
            etOldPass.setFilters(new InputFilter[]{new InputFilter.LengthFilter(6)});
            etNewPassAgain.setFilters(new InputFilter[]{new InputFilter.LengthFilter(6)});
            etEmailCode.setFilters(new InputFilter[]{new InputFilter.LengthFilter(6)});
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
        switch (mType) {
            case MethodUrl.COMMON_SENDSMSVERIFY:
                switch (tData.get("code") + "") {
                    case "1":
                        showToastMsg(tData.get("msg") + "");
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
            case MethodUrl.USER_EDITUSERPWD:
                switch (tData.get("code") + "") {
                    case "1":
                        showToastMsg(tData.get("msg") + "");
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
            case R.id.tvGetCode:
                getCodeAction();
                break;
            case R.id.tbOldPass:
                if (tbOldPass.isChecked()) {
                    //显示密码
                    etOldPass.setTransformationMethod(
                            HideReturnsTransformationMethod.getInstance());
                } else {
                    //隐藏密码
                    etOldPass.setTransformationMethod(
                            PasswordTransformationMethod.getInstance());
                }
                break;
            case R.id.tbNewPass:
                if (tbNewPass.isChecked()) {
                    //显示密码
                    etNewPass.setTransformationMethod(
                            HideReturnsTransformationMethod.getInstance());
                } else {
                    //隐藏密码
                    etNewPass.setTransformationMethod(
                            PasswordTransformationMethod.getInstance());
                }
                break;
            case R.id.tbNewPassAgain:
                if (tbNewPassAgain.isChecked()) {
                    //显示密码
                    etNewPassAgain.setTransformationMethod(
                            HideReturnsTransformationMethod.getInstance());
                } else {
                    //隐藏密码
                    etNewPassAgain.setTransformationMethod(
                            PasswordTransformationMethod.getInstance());
                }
                break;
            case R.id.tvConfirm:
                String oldPass = etOldPass.getText().toString();
                String newPass = etNewPass.getText().toString();
                String newPassAgain = etNewPassAgain.getText().toString();
                if ("1".equals(type1)) {
                    if (!TextUtils.isEmpty(oldPass)
                            && !TextUtils.isEmpty(newPass)
                            && !TextUtils.isEmpty(newPassAgain)
                            && newPass.equals(newPassAgain)) {
                        changePassAction();
                    } else {
                        if (TextUtils.isEmpty(oldPass)) {
                            showToastMsg(R.string.inputOldPass);
                        } else if (TextUtils.isEmpty(newPass)) {
                            showToastMsg(R.string.inputNewPass);
                        } else if (TextUtils.isEmpty(newPass)) {
                            showToastMsg(R.string.inputNewPassAgain);
                        } else if (!newPass.equals(newPassAgain)) {
                            showToastMsg(R.string.seal_login_toast_passwords_do_not_match);
                        }
                    }
                } else if ("2".equals(type1)) {
                    String emailCode = etEmailCode.getText().toString();
                    if (!TextUtils.isEmpty(oldPass)
                            && !TextUtils.isEmpty(newPass)
                            && !TextUtils.isEmpty(newPassAgain)
                            && !TextUtils.isEmpty(emailCode)
                            && newPass.equals(newPassAgain)) {
                        changePassAction();
                    } else {
                        if (TextUtils.isEmpty(oldPass)) {
                            showToastMsg(R.string.inputOldPass);
                        } else if (TextUtils.isEmpty(newPass)) {
                            showToastMsg(R.string.inputNewPass);
                        } else if (TextUtils.isEmpty(newPass)) {
                            showToastMsg(R.string.inputNewPassAgain);
                        } else if (TextUtils.isEmpty(emailCode)) {
                            showToastMsg(R.string.inputEmailCode);
                        } else if (!newPass.equals(newPassAgain)) {
                            showToastMsg(R.string.seal_login_toast_passwords_do_not_match);
                        }
                    }
                }
                break;
            default:
        }
    }

    private void changePassAction() {
        Map<String, Object> map = new HashMap<>();
        String oldPass = etOldPass.getText().toString().trim() + "";
        String newPass = etNewPass.getText().toString().trim() + "";
        String newPassAgain = etNewPassAgain.getText().toString().trim() + "";
        String emailCode = etEmailCode.getText().toString();
        if (UtilTools.empty(MbsConstans.ACCESS_TOKEN)) {
            MbsConstans.ACCESS_TOKEN = SPUtils.get(MbsConstans.ACCESS_TOKEN, "").toString();
        }
        map.put("token", MbsConstans.ACCESS_TOKEN);
        map.put("pwd_old", oldPass);
        map.put("pwd_new", newPass);
        map.put("re_pwd_new", newPassAgain);
        if ("1".equals(type1)) {
            map.put("pwd_type", "1");
            mRequestPresenterImp.requestPostToMap(MethodUrl.USER_EDITUSERPWD, map);
        } else {
            map.put("pwd_type", "2");
            map.put("account_verify", emailCode);
            mRequestPresenterImp.requestPostToMap(MethodUrl.USER_EDITUSERPWD, map);
        }
    }

    private void getCodeAction() {
        Map<String, Object> map = new HashMap<>();
        if (UtilTools.empty(MbsConstans.USER_MAP)) {
            String s = SPUtils.get(ChangePassActivity.this, MbsConstans.SharedInfoConstans.LOGIN_INFO, "").toString();
            MbsConstans.USER_MAP = JSONUtil.getInstance().jsonMap(s);
        }
        Map<String, Object> data = (Map<String, Object>) MbsConstans.USER_MAP.get("data");
        String emailAddress = data.get("user_email") + "";
        map.put("account_type", "2");
        map.put("account", emailAddress);
        map.put("verify_type", "3");
        mRequestPresenterImp.requestPostToMap(MethodUrl.COMMON_SENDSMSVERIFY, map);
    }
}

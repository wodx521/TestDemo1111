package com.lr.sia.ui.moudle5.activity;

import android.content.Intent;
import android.text.TextUtils;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
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

public class ChangePhoneActivity1 extends BasicActivity implements View.OnClickListener {
    private ImageView backImg,iv1,iv2;
    private TextView tvGetCode, tvGetCode1, tvConfirm, tvNoVerification, tvTitle, tvChoosePhone1,
            tvChoosePhone;
    private EditText etOldPhone, etCode, etNewPhone, etCode1;
    private CardView cvOldPhone, cvVerificationCode, cvNewPhone, cvVerificationCode1;
    private String type1;
    private CountDownUtils countDownUtils;
    private CountDownUtils countDownUtils1;
    private List<String> phoneArea;

    @Override
    public int getContentView() {
        return R.layout.activity_change_phone1;
    }

    @Override
    public void init() {
        phoneAreaPopup = new PhoneAreaPopup(ChangePhoneActivity1.this);
        countDownUtils = new CountDownUtils();
        countDownUtils1 = new CountDownUtils();
        StatusBarUtil.setColorForSwipeBack(this, ContextCompat.getColor(this, MbsConstans.TOP_BAR_COLOR), MbsConstans.ALPHA);
        backImg = findViewById(R.id.backImg);
        iv1 = findViewById(R.id.iv1);
        iv2 = findViewById(R.id.iv2);
        tvTitle = findViewById(R.id.tvTitle);
        cvOldPhone = findViewById(R.id.cvOldPhone);
        etOldPhone = findViewById(R.id.etOldPhone);
        cvVerificationCode = findViewById(R.id.cvVerificationCode);
        etCode = findViewById(R.id.etCode);
        tvGetCode = findViewById(R.id.tvGetCode);
        tvChoosePhone = findViewById(R.id.tvChoosePhone);
        tvChoosePhone1 = findViewById(R.id.tvChoosePhone1);
        cvNewPhone = findViewById(R.id.cvNewPhone);
        etNewPhone = findViewById(R.id.etNewPhone);
        cvVerificationCode1 = findViewById(R.id.cvVerificationCode1);
        etCode1 = findViewById(R.id.etCode1);
        tvGetCode1 = findViewById(R.id.tvGetCode1);
        tvConfirm = findViewById(R.id.tvConfirm);
        tvNoVerification = findViewById(R.id.tvNoVerification);

        backImg.setOnClickListener(this);
        tvGetCode.setOnClickListener(this);
        tvGetCode1.setOnClickListener(this);
        tvConfirm.setOnClickListener(this);
        tvNoVerification.setOnClickListener(this);
        tvChoosePhone.setOnClickListener(this);
        tvChoosePhone1.setOnClickListener(this);

        type1 = getIntent().getStringExtra("type");
        if ("1".equals(type1)) {
            tvChoosePhone.setVisibility(View.VISIBLE);
            tvChoosePhone1.setVisibility(View.VISIBLE);
            etOldPhone.setHint(R.string.inputOldPhoneNum);
            etNewPhone.setHint(R.string.inputNewPhoneNum);
            iv1.setImageResource(R.drawable.icon_phone);
            iv2.setImageResource(R.drawable.icon_phone);
            etOldPhone.setInputType(EditorInfo.TYPE_CLASS_PHONE);
            etNewPhone.setInputType(EditorInfo.TYPE_CLASS_PHONE);
            tvTitle.setText(R.string.changePhone);
            tvNoVerification.setText(R.string.noVerificationPhone);
        } else if ("2".equals(type1)) {
            tvChoosePhone.setVisibility(View.GONE);
            tvChoosePhone1.setVisibility(View.GONE);
            etOldPhone.setHint(R.string.inputOldEmail);
            etNewPhone.setHint(R.string.inputNewEmailNum);
            iv1.setImageResource(R.drawable.icon_email);
            iv2.setImageResource(R.drawable.icon_email);
            etOldPhone.setInputType(EditorInfo.TYPE_TEXT_VARIATION_EMAIL_ADDRESS);
            etNewPhone.setInputType(EditorInfo.TYPE_TEXT_VARIATION_EMAIL_ADDRESS);
            tvTitle.setText(R.string.changeEmail);
            tvNoVerification.setText(R.string.noVerificationEmail);
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
                        if (etOldPhone.isSelected()) {
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
                        } else if (etNewPhone.isSelected()) {
                            tvGetCode1.setEnabled(false);
                            if (BuildConfig.DEBUG) {
                                countDownUtils1.getTimer(MbsConstans.SECOND_TIME_10, tvGetCode1, getString(R.string.sendVerificationCode));
                            } else {
                                countDownUtils1.getTimer(MbsConstans.SECOND_TIME_60, tvGetCode1, getString(R.string.sendVerificationCode));
                            }
                            countDownUtils1.setTimeFinishListener(new CountDownUtils.CountTimeFinishListener() {
                                @Override
                                public void onTimeFinishListener() {
                                    tvGetCode1.setEnabled(true);
                                }
                            });
                        }
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
            case MethodUrl.USER_EDITUSERACCOUNT:
                switch (tData.get("code") + "") {
                    case "1":
                        String s = SPUtils.get(ChangePhoneActivity1.this, MbsConstans.SharedInfoConstans.LOGIN_INFO, "").toString();
                        MbsConstans.USER_MAP = JSONUtil.getInstance().jsonMap(s);
                        Map<String, Object> data = (Map<String, Object>) MbsConstans.USER_MAP.get("data");
                        if ("1".equals(type1)) {
                            data.put("user_phone", etNewPhone.getText().toString());
                        } else {
                            data.put("user_email", etNewPhone.getText().toString());
                        }
                        MbsConstans.USER_MAP.put("data", data);
                        SPUtils.put(ChangePhoneActivity1.this, MbsConstans.SharedInfoConstans.LOGIN_INFO, JSONUtil.getInstance().objectToJson(MbsConstans.USER_MAP));
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
            case R.id.backImg:
                finish();
                break;
            case R.id.tvGetCode:
                etOldPhone.setSelected(true);
                etNewPhone.setSelected(false);
                getCodeAction(etOldPhone);
                break;
            case R.id.tvGetCode1:
                etOldPhone.setSelected(false);
                etNewPhone.setSelected(true);
                getCodeAction(etNewPhone);
                break;
            case R.id.tvConfirm:
                checkCodeAction();
                break;
            case R.id.tvNoVerification:
                if ("1".equals(type1)) {
                    intent = new Intent(ChangePhoneActivity1.this, ChangePhoneActivity2.class);
                    intent.putExtra("type", "1");
                    intent.putExtra("from", "2");
                    startActivityForResult(intent, MbsConstans.CHANGE_ACCOUNT);
                } else if ("2".equals(type1)) {
                    intent = new Intent(ChangePhoneActivity1.this, ChangePhoneActivity2.class);
                    intent.putExtra("type", "2");
                    intent.putExtra("from", "2");
                    startActivityForResult(intent, MbsConstans.CHANGE_ACCOUNT);
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
            case R.id.tvChoosePhone1:
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
    private PhoneAreaPopup phoneAreaPopup;
    private void getCodeAction(EditText editText) {
        Map<String, Object> map = new HashMap<>();
        String account = editText.getText().toString().trim();
        if ("1".equals(type1)) {
            if (!TextUtils.isEmpty(account)) {
                map.put("account_type", "1");
                if (editText==etNewPhone) {
                    map.put("account", tvChoosePhone.getText().toString()+account);
                }else{
                    map.put("account", tvChoosePhone1.getText().toString()+account);
                }

                map.put("verify_type", "2");
                mRequestPresenterImp.requestPostToMap(MethodUrl.COMMON_SENDSMSVERIFY, map);
            } else {
                if (TextUtils.isEmpty(account)) {
                    showToastMsg(R.string.inputPhone);
                } else {
                    showToastMsg(R.string.inputRightPhone);
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
                    showToastMsg(R.string.inputEmail);
                } else {
                    showToastMsg(R.string.inputRigthEmail);
                }
            }
        }
    }

    private void checkCodeAction() {
        Map<String, Object> map = new HashMap<>();
        String oldPhone = etOldPhone.getText().toString().trim() + "";
        String newPhone = etNewPhone.getText().toString().trim() + "";
        String code = etCode.getText().toString().trim() + "";
        String code1 = etCode.getText().toString().trim() + "";
        if (UtilTools.empty(MbsConstans.ACCESS_TOKEN)) {
            MbsConstans.ACCESS_TOKEN = SPUtils.get(MbsConstans.ACCESS_TOKEN, "").toString();
        }
        map.put("token", MbsConstans.ACCESS_TOKEN);
        if ("1".equals(type1)) {
            if (!TextUtils.isEmpty(oldPhone)
                    && !TextUtils.isEmpty(newPhone)
                    && !TextUtils.isEmpty(code)
                    && !TextUtils.isEmpty(code1)
                    && !oldPhone.equals(newPhone)) {
                map.put("account_type", "1");
                map.put("account_old", tvChoosePhone.getText().toString()+"_"+oldPhone);
                map.put("account_new", tvChoosePhone1.getText().toString()+"_"+newPhone);
                map.put("account_old_verify", code);
                map.put("account_new_verify", code1);
                mRequestPresenterImp.requestPostToMap(MethodUrl.USER_EDITUSERACCOUNT, map);
            } else {
                if (TextUtils.isEmpty(oldPhone)) {
                    showToastMsg(R.string.inputOldPhoneNum);
                } else if (TextUtils.isEmpty(newPhone)) {
                    showToastMsg(R.string.inputNewPhoneNum);
                } else if (TextUtils.isEmpty(code) || TextUtils.isEmpty(code1)) {
                    showToastMsg(R.string.inputVerificationCode);
                }
            }
        } else {
            if (!TextUtils.isEmpty(oldPhone)
                    && !TextUtils.isEmpty(newPhone)
                    && !TextUtils.isEmpty(code)
                    && !TextUtils.isEmpty(code1)
                    && !oldPhone.equals(newPhone)) {
                map.put("account_type", "2");
                map.put("account_old", oldPhone);
                map.put("account_new", newPhone);
                map.put("account_old_verify", code);
                map.put("account_new_verify", code1);
                mRequestPresenterImp.requestPostToMap(MethodUrl.USER_EDITUSERACCOUNT, map);
            } else {
                if (TextUtils.isEmpty(oldPhone)) {
                    showToastMsg(R.string.inputOldEmail);
                } else if (TextUtils.isEmpty(newPhone)) {
                    showToastMsg(R.string.inputNewEmailNum);
                } else if (TextUtils.isEmpty(code) || TextUtils.isEmpty(code1)) {
                    showToastMsg(R.string.inputVerificationCode);
                }
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case MbsConstans.CHANGE_ACCOUNT:
                    finish();
                    break;
                default:
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }
}

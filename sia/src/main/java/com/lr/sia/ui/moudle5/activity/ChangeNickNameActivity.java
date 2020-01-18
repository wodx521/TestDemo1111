package com.lr.sia.ui.moudle5.activity;

import android.content.Intent;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.core.content.ContextCompat;

import com.jaeger.library.StatusBarUtil;
import com.lr.sia.R;
import com.lr.sia.api.MethodUrl;
import com.lr.sia.basic.BasicActivity;
import com.lr.sia.basic.MbsConstans;

import com.lr.sia.mvp.presenter.RequestPresenterImp;
import com.lr.sia.ui.moudle.activity.LoginActivity1;
import com.lr.sia.utils.tool.JSONUtil;
import com.lr.sia.utils.tool.SPUtils;
import com.lr.sia.utils.tool.UtilTools;

import java.util.HashMap;
import java.util.Map;

public class ChangeNickNameActivity extends BasicActivity implements View.OnClickListener {
    private ImageView backImg;
    private EditText etNickname;
    private TextView tvConfirm;
    private String mRequestTag;

    @Override
    public int getContentView() {
        return R.layout.activity_change_nick_name;
    }

    @Override
    public void init() {
        StatusBarUtil.setColorForSwipeBack(this, ContextCompat.getColor(this, MbsConstans.TOP_BAR_COLOR), MbsConstans.ALPHA);
        backImg = findViewById(R.id.back_img);
        etNickname = findViewById(R.id.etNickname);
        tvConfirm = findViewById(R.id.tvConfirm);

        backImg.setOnClickListener(this);
        tvConfirm.setOnClickListener(this);
        if (UtilTools.empty(MbsConstans.USER_MAP)) {
            String s = SPUtils.get(ChangeNickNameActivity.this, MbsConstans.SharedInfoConstans.LOGIN_INFO, "").toString();
            MbsConstans.USER_MAP = JSONUtil.getInstance().jsonMap(s);
        }
        Map<String, Object> data = (Map<String, Object>) MbsConstans.USER_MAP.get("data");
        String nickName = data.get("nick_name") + "";
        etNickname.setHint(nickName);
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
                        String s = SPUtils.get(ChangeNickNameActivity.this, MbsConstans.SharedInfoConstans.LOGIN_INFO, "").toString();
                        Map<String, Object> data = JSONUtil.getInstance().jsonMap(s);
                        Map<String, Object> infoData = (Map<String, Object>) data.get("data");
                        infoData.put("nick_name", etNickname.getText().toString());
                        data.put("data", infoData);
                        MbsConstans.USER_MAP = data;
                        if (UtilTools.empty(MbsConstans.RONGYUN_MAP)) {
                            String rongyunInfo = SPUtils.get(ChangeNickNameActivity.this, MbsConstans.SharedInfoConstans.RONGYUN_DATA, "").toString();
                            MbsConstans.RONGYUN_MAP = JSONUtil.getInstance().jsonMap(rongyunInfo);
                        }
                        SPUtils.put(ChangeNickNameActivity.this, MbsConstans.SharedInfoConstans.LOGIN_INFO, JSONUtil.getInstance().objectToJson(MbsConstans.USER_MAP));
                        showToastMsg(tData.get("msg") + "");
                        finish();
                        break;
                    case "0": //请求失败
                        showToastMsg(tData.get("msg") + "");
                        break;
                    case "-1": //token过期
                        finish();
                        intent = new Intent(ChangeNickNameActivity.this, LoginActivity1.class);
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
        switch (v.getId()) {
            case R.id.back_img:
                finish();
                break;
            case R.id.tvConfirm:
                if (TextUtils.isEmpty(etNickname.getText())) {
                    showToastMsg(R.string.inputNickName);
                } else {
                    showProgressDialog();
                    editUserInfoAction(etNickname.getText().toString());
                }
                break;
            default:
        }
    }

    private void editUserInfoAction(String value) {
        mRequestPresenterImp = new RequestPresenterImp(this, ChangeNickNameActivity.this);
        mRequestTag = MethodUrl.USER_EDITUSERMESSAGE;
        Map<String, Object> map = new HashMap<>();
        if (UtilTools.empty(MbsConstans.ACCESS_TOKEN)) {
            MbsConstans.ACCESS_TOKEN = SPUtils.get(ChangeNickNameActivity.this, MbsConstans.ACCESS_TOKEN, "").toString();
        }
        map.put("token", MbsConstans.ACCESS_TOKEN);
        map.put("item", "nick_name");
        map.put("value", value);
        mRequestPresenterImp.requestPostToMap(MethodUrl.USER_EDITUSERMESSAGE, map);
    }

}

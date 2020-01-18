package com.lr.sia.ui.moudle5.activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.github.jdsjlzx.recyclerview.LRecyclerView;
import com.github.jdsjlzx.recyclerview.LRecyclerViewAdapter;
import com.lr.sia.R;
import com.lr.sia.api.MethodUrl;
import com.lr.sia.basic.BasicActivity;
import com.lr.sia.basic.MbsConstans;
import com.lr.sia.mvp.view.RequestView;
import com.lr.sia.ui.moudle.activity.LoginActivity1;
import com.lr.sia.ui.moudle5.adapter.FansFavoriteListAdapter;
import com.lr.sia.utils.tool.SPUtils;
import com.lr.sia.utils.tool.UtilTools;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class FansListActivity extends BasicActivity implements RequestView {
    @BindView(R.id.back_img)
    ImageView backImg;
    @BindView(R.id.rbFavorite)
    RadioButton rbFavorite;
    @BindView(R.id.rbFans)
    RadioButton rbFans;
    @BindView(R.id.rgChoose)
    RadioGroup rgChoose;
    @BindView(R.id.lrvList)
    LRecyclerView lrvList;
    private String type;
    private int page = 1;
    private LRecyclerViewAdapter lRecyclerViewAdapter;
    private FansFavoriteListAdapter fansFavoriteListAdapter;

    @Override
    public void showProgress() {
        showProgressDialog();
    }

    @Override
    public void disimissProgress() {
        dismissProgressDialog();
    }

    @Override
    public void loadDataSuccess(Map<String, Object> tData, String mType) {
        dismissProgressDialog();
        Intent intent;
        switch (mType) {
            case MethodUrl.USER_GETUSERFOLLOWSLIST:
                switch (tData.get("code") + "") {
                    case "1": //请求成功
                        Map<String, Object> data = (Map<String, Object>) tData.get("data");
                        List<Map<String, Object>> dataList = (List<Map<String, Object>>) data.get("data");
                        fansFavoriteListAdapter.setDataList(dataList);
                        break;
                    case "0": //请求失败
                        showToastMsg(tData.get("msg") + "");
                        break;
                    case "-1": //token过期
                        showToastMsg(tData.get("msg") + "");
                        finish();
                        intent = new Intent(FansListActivity.this, LoginActivity1.class);
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
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // TODO: add setContentView(...) invocation
        ButterKnife.bind(this);
    }

    @Override
    public int getContentView() {
        return R.layout.activity_fans_list;
    }

    @Override
    public void init() {
        String listType = getIntent().getStringExtra("type");
        if (!TextUtils.isEmpty(listType)) {
            type = listType;
            if ("1".equals(type)) {
                rgChoose.check(R.id.rbFavorite);
            } else if ("2".equals(type)) {
                rgChoose.check(R.id.rbFans);
            }
            getUserInfoAction();
        }
        fansFavoriteListAdapter = new FansFavoriteListAdapter(FansListActivity.this);
        lRecyclerViewAdapter = new LRecyclerViewAdapter(fansFavoriteListAdapter);
        lrvList.setAdapter(lRecyclerViewAdapter);
    }

    private void getUserInfoAction() {
        Map<String, Object> map = new HashMap<>();
        if (UtilTools.empty(MbsConstans.ACCESS_TOKEN)) {
            MbsConstans.ACCESS_TOKEN = SPUtils.get(MbsConstans.SharedInfoConstans.ACCESS_TOKEN, "").toString();
        }
        map.put("token", MbsConstans.ACCESS_TOKEN);
        map.put("type", type);
        map.put("page", page);
        mRequestPresenterImp.requestPostToMap(MethodUrl.USER_GETUSERFOLLOWSLIST, map);
    }

    @OnClick({R.id.back_img, R.id.rbFavorite, R.id.rbFans})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.back_img:
                finish();
                break;
            case R.id.rbFavorite:
                type = "1";
                getUserInfoAction();
                break;
            case R.id.rbFans:
                type = "2";
                getUserInfoAction();
                break;
            default:
        }
    }

}

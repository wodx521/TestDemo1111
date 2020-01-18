package com.lr.sia.ui.moudle4.activity;

import android.content.Intent;
import android.text.Html;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.core.content.ContextCompat;

import com.jaeger.library.StatusBarUtil;
import com.lr.sia.R;
import com.lr.sia.api.MethodUrl;
import com.lr.sia.basic.BasicActivity;
import com.lr.sia.basic.MbsConstans;
import com.lr.sia.ui.moudle.activity.LoginActivity1;
import com.lr.sia.utils.tool.SPUtils;
import com.lr.sia.utils.tool.UtilTools;

import java.util.HashMap;
import java.util.Map;

public class PrivatePlacementDesActivity extends BasicActivity implements View.OnClickListener {
    private ImageView backImg;
    private TextView tvTitle, tvValuationMethod, tvUnit, tvInviteDes, tvMathData, tvCancelDes,
            tvResponsibilityDes;

    @Override
    public int getContentView() {
        return R.layout.activity_private_placement_des;
    }

    @Override
    public void init() {
        backImg = findViewById(R.id.backImg);
        tvTitle = findViewById(R.id.tvTitle);
        tvValuationMethod = findViewById(R.id.tvValuationMethod);
        tvUnit = findViewById(R.id.tvUnit);
        tvInviteDes = findViewById(R.id.tvInviteDes);
        tvMathData = findViewById(R.id.tvMathData);
        tvCancelDes = findViewById(R.id.tvCancelDes);
        tvResponsibilityDes = findViewById(R.id.tvResponsibilityDes);
        backImg.setOnClickListener(this);
        StatusBarUtil.setColorForSwipeBack(this, ContextCompat.getColor(this, MbsConstans.TOP_BAR_COLOR), MbsConstans.ALPHA);
        getDesAction();
    }


    private void getDesAction() {
        Map<String, Object> map = new HashMap<>();
        if (UtilTools.empty(MbsConstans.ACCESS_TOKEN)) {
            MbsConstans.ACCESS_TOKEN = SPUtils.get(this, MbsConstans.SharedInfoConstans.ACCESS_TOKEN, "").toString();
        }
        map.put("token", MbsConstans.ACCESS_TOKEN);
        mRequestPresenterImp.requestPostToMap(MethodUrl.PRE_IPO_GETSIAITEMINTRO, map);
    }

    @Override
    public void showProgress() {

    }

    @Override
    public void disimissProgress() {

    }

    @Override
    public void loadDataSuccess(Map<String, Object> tData, String mType) {
        Intent intent = null;
        switch (mType) {
            case MethodUrl.PRE_IPO_GETSIAITEMINTRO:
                switch (tData.get("code") + "") {
                    case "1":
                        Map<String, Object> mapData = (Map<String, Object>) tData.get("data");
                        String coinName = mapData.get("coin_name") + "";
                        String coinIcon = mapData.get("coin_icon") + "";
                        String coinPrice = mapData.get("coin_price") + "";
                        String publishAmount = mapData.get("publish_amount") + "";
                        String coinFullName = mapData.get("coin_full_name") + "";
                        String valuationMethod = mapData.get("valuation_method") + "";
                        String unit = mapData.get("unit") + "";
                        String remarkRecommend = mapData.get("remark_recommend") + "";
                        String remarkDate = mapData.get("remark_date") + "";
                        String remarkRefund = mapData.get("remark_refund") + "";
                        String remarkDuty = mapData.get("remark_duty") + "";
                        tvValuationMethod.setText(valuationMethod);
                        tvUnit.setText(unit);
                        tvInviteDes.setText(Html.fromHtml(remarkRecommend));
                        tvMathData.setText(Html.fromHtml(remarkDate));
                        tvCancelDes.setText(Html.fromHtml(remarkRefund));
                        tvResponsibilityDes.setText(Html.fromHtml(remarkDuty));
                        break;
                    case "-1":
                        finish();
                        intent = new Intent(PrivatePlacementDesActivity.this, LoginActivity1.class);
                        startActivity(intent);
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
            default:
        }
    }
}

package com.lr.sia.ui.moudle4.activity;

import android.content.Intent;
import android.text.Html;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.core.content.ContextCompat;

import com.bumptech.glide.Glide;
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

public class ProjectDesActivity extends BasicActivity implements View.OnClickListener {
    private ImageView backImg, ivIcon;
    private TextView tvName, tvDes, tvTotalIssued, tvPrivatePrice, tvApplicationDirection,
            tvContractType, tvAmountPaid, tvCommunity, tvWhitePaper, tvLockDes;
    private String whitePaper;

    @Override
    public int getContentView() {
        return R.layout.activity_project_des;
    }

    @Override
    public void init() {
        backImg = findViewById(R.id.backImg);
        ivIcon = findViewById(R.id.ivIcon);
        tvName = findViewById(R.id.tvName);
        tvDes = findViewById(R.id.tvDes);
        tvTotalIssued = findViewById(R.id.tvTotalIssued);
        tvPrivatePrice = findViewById(R.id.tvPrivatePrice);
        tvApplicationDirection = findViewById(R.id.tvApplicationDirection);
        tvContractType = findViewById(R.id.tvContractType);
        tvAmountPaid = findViewById(R.id.tvAmountPaid);
        tvCommunity = findViewById(R.id.tvCommunity);
        tvWhitePaper = findViewById(R.id.tvWhitePaper);
        tvLockDes = findViewById(R.id.tvLockDes);
        StatusBarUtil.setColorForSwipeBack(this, ContextCompat.getColor(this, MbsConstans.TOP_BAR_COLOR), MbsConstans.ALPHA);
        backImg.setOnClickListener(this);
        tvWhitePaper.setOnClickListener(this);
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
                        String applyDirection = mapData.get("apply_direction") + "";
                        String contractType = mapData.get("contract_type") + "";
                        String remarkLock = mapData.get("remark_lock") + "";
                        String group = mapData.get("group") + "";
                        whitePaper = mapData.get("white_paper") + "";
                        tvName.setText(coinName);
                        tvDes.setText(coinFullName);
                        tvTotalIssued.setText(publishAmount);
                        tvPrivatePrice.setText(coinPrice);
                        tvApplicationDirection.setText(applyDirection);
                        tvContractType.setText(contractType);
                        tvLockDes.setText(Html.fromHtml(remarkLock));
                        tvCommunity.setText(group);
                        Glide.with(ProjectDesActivity.this)
                                .load(coinIcon)
                                .into(ivIcon);
                        break;
                    case "-1":
                        finish();
                        intent = new Intent(ProjectDesActivity.this, LoginActivity1.class);
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
            case R.id.tvWhitePaper:
                Intent intent = new Intent(ProjectDesActivity.this, WhitePaperActivity.class);
                if (!TextUtils.isEmpty(whitePaper)) {
                    intent.putExtra("whitePaper",whitePaper);
                    startActivity(intent);
                }
                break;
            default:
        }
    }
}

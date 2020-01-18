package com.lr.sia.ui.moudle3.activity;

import android.content.Intent;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.core.content.ContextCompat;

import com.jaeger.library.StatusBarUtil;
import com.lr.sia.R;
import com.lr.sia.api.MethodUrl;
import com.lr.sia.basic.BasicActivity;
import com.lr.sia.basic.MbsConstans;
import com.lr.sia.manage.ActivityManager;
import com.lr.sia.mvp.presenter.RequestPresenterImp;
import com.lr.sia.ui.moudle.activity.LoginActivity1;
import com.lr.sia.ui.moudle5.activity.RedLogActivity;
import com.lr.sia.utils.tool.SPUtils;
import com.lr.sia.utils.tool.UtilTools;

import java.util.HashMap;
import java.util.Map;

import cn.wildfire.chat.kit.utils.ToastUtils;

public class TransferInfoActivity extends BasicActivity implements View.OnClickListener {
    private ImageView ivBack, ivStatus;
    private TextView tvStatus, tvTransferNum, tvConfirm, tvTime, tvTransferLog;
    private HashMap<String, Object> transferInfo;
    private String id;

    @Override
    public int getContentView() {
        return R.layout.activity_transfer_info;
    }

    @Override
    public void init() {
        StatusBarUtil.setColorForSwipeBack(this, ContextCompat.getColor(this, MbsConstans.TOP_BAR_COLOR), MbsConstans.ALPHA);
        transferInfo = (HashMap<String, Object>) getIntent().getSerializableExtra("transferInfo");
        ivBack = findViewById(R.id.ivBack);
        ivStatus = findViewById(R.id.ivStatus);
        tvStatus = findViewById(R.id.tvStatus);
        tvTransferNum = findViewById(R.id.tvTransferNum);
        tvConfirm = findViewById(R.id.tvConfirm);
        tvTransferLog = findViewById(R.id.tvTransferLog);
        tvTime = findViewById(R.id.tvTime);

        ivBack.setOnClickListener(this);
        tvConfirm.setOnClickListener(this);
        tvTransferLog.setOnClickListener(this);

        if (!UtilTools.empty(transferInfo)) {
            id = transferInfo.get("id") + "";
            String total = transferInfo.get("total") + "";
            String tranStatus = transferInfo.get("tran_status") + "";
            String sendTime = transferInfo.get("send_time") + "";
            String tranStatusMsg = transferInfo.get("tran_status_msg") + "";
            tvStatus.setText(tranStatusMsg);
            tvTransferNum.setText(total + "SIA");
            if ("0".equals(tranStatus)) {
                ivStatus.setImageResource(R.drawable.icon_wait);
                tvConfirm.setVisibility(View.VISIBLE);
                tvTransferLog.setVisibility(View.GONE);
            } else if ("1".equals(tranStatus)) {
                ivStatus.setImageResource(R.drawable.icon_success);
                tvConfirm.setVisibility(View.GONE);
                tvTransferLog.setVisibility(View.VISIBLE);
            }
            tvTime.setText(getString(R.string.transferTime) + sendTime);
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
            case MethodUrl.TRANSFER_RECEIVECHATTRANSFER:
                switch (tData.get("code") + "") {
                    case "1": //请求成功
                        ToastUtils.showToast(tData.get("msg") + "");
                        finish();
                        break;
                    case "0": //请求失败
                        ToastUtils.showToast(tData.get("msg") + "");
                        break;
                    case "-1": //token过期
                        ActivityManager.getInstance().close();
                        Intent intent = new Intent(TransferInfoActivity.this, LoginActivity1.class);
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

    private void getTransferMoneyAction(String id) {
        RequestPresenterImp requestPresenterImp = new RequestPresenterImp(this, this);
        Map<String, Object> map = new HashMap<>();
        if (UtilTools.empty(MbsConstans.ACCESS_TOKEN)) {
            MbsConstans.ACCESS_TOKEN = SPUtils.get(MbsConstans.SharedInfoConstans.ACCESS_TOKEN, "").toString();
        }
        map.put("token", MbsConstans.ACCESS_TOKEN);
        map.put("id", id);
        Map<String, String> mHeaderMap = new HashMap<String, String>();
        requestPresenterImp.requestPostToMap(mHeaderMap, MethodUrl.TRANSFER_RECEIVECHATTRANSFER, map);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ivBack:
                finish();
                break;
            case R.id.tvConfirm:
                getTransferMoneyAction(id);
                break;
            case R.id.tvTransferLog:
                Intent intent = new Intent(TransferInfoActivity.this, RedLogActivity.class);
                intent.putExtra("logType", "2");
                startActivity(intent);
                break;
            default:
        }
    }
}

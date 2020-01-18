package com.lr.sia.ui.moudle1.activity;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.lr.sia.R;
import com.lr.sia.api.MethodUrl;
import com.lr.sia.basic.BasicActivity;
import com.lr.sia.basic.BasicApplication;
import com.lr.sia.basic.MbsConstans;
import com.lr.sia.mvp.presenter.RequestPresenterImp;
import com.lr.sia.ui.moudle.activity.LoginActivity1;
import com.lr.sia.ui.moudle4.activity.NewsDetailActivity;
import com.lr.sia.utils.tool.SPUtils;
import com.lr.sia.utils.tool.UtilTools;

import java.util.HashMap;
import java.util.Map;

public class TransferDetailActivity extends BasicActivity implements View.OnClickListener {
    private TextView tvTitle, tvTime, tvJump, tvCopy, tvCopy1, tvCopy2;
    private ImageView backImg, ivTransferStatus;
    private EditText etAmount, etMinerFee, etRecivedAddress, etPayAddress, etRemarks, etTradeOrder, etBlockNum;
    private String transferDetailId;

    @Override
    public int getContentView() {
        return R.layout.activity_transfer_detail;
    }

    @Override
    public void init() {
        transferDetailId = getIntent().getStringExtra("transferDetailId");

        tvCopy = findViewById(R.id.tvCopy);
        tvCopy1 = findViewById(R.id.tvCopy1);
        tvCopy2 = findViewById(R.id.tvCopy2);
        tvTitle = findViewById(R.id.tvTitle);
        tvJump = findViewById(R.id.tvJump);
        backImg = findViewById(R.id.back_img);
        ivTransferStatus = findViewById(R.id.ivTransferStatus);
        tvTime = findViewById(R.id.tvTime);
        etAmount = findViewById(R.id.etAmount);
        etMinerFee = findViewById(R.id.etMinerFee);
        etRecivedAddress = findViewById(R.id.etRecivedAddress);
        etPayAddress = findViewById(R.id.etPayAddress);
        etRemarks = findViewById(R.id.etRemarks);
        etTradeOrder = findViewById(R.id.etTradeOrder);
        etBlockNum = findViewById(R.id.etBlockNum);
        tvTitle.setText(R.string.transferDetail);
        backImg.setOnClickListener(this);
        tvJump.setOnClickListener(this);
        tvCopy.setOnClickListener(this);
        tvCopy1.setOnClickListener(this);
        tvCopy2.setOnClickListener(this);

        getDetailAction();
    }

    private void getDetailAction() {
        mRequestPresenterImp = new RequestPresenterImp(this, TransferDetailActivity.this);
        Map<String, Object> map = new HashMap<>();
        if (UtilTools.empty(MbsConstans.ACCESS_TOKEN)) {
            MbsConstans.ACCESS_TOKEN = SPUtils.get(TransferDetailActivity.this, MbsConstans.SharedInfoConstans.ACCESS_TOKEN, "").toString();
        }
        map.put("token", MbsConstans.ACCESS_TOKEN);
        map.put("id", transferDetailId);
        mRequestPresenterImp.requestPostToMap(MethodUrl.ACCOUNT_GETTRADEINFO, map);
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
            case MethodUrl.ACCOUNT_GETTRADEINFO:
                switch (tData.get("code") + "") {
                    case "1":
                        Map<String, Object> data = (Map<String, Object>) tData.get("data");
                        String amount = data.get("amount") + "";
                        String gasFee = data.get("gas_fee") + "";
                        String fromAddress = data.get("from_address") + "";
                        String toAddress = data.get("to_address") + "";
                        String remark = data.get("remark") + "";
                        String tradeHash = data.get("trade_hash") + "";
                        String blockNumber = data.get("block_number") + "";
                        String tradeStatus = data.get("trade_status") + "";
                        String createTime = data.get("create_time") + "";
                        String coinName = data.get("coin_name") + "";
                        etAmount.setText(amount + coinName);
                        etMinerFee.setText(gasFee + "ETH");
                        etRecivedAddress.setText(toAddress);
                        etPayAddress.setText(fromAddress);
                        etRemarks.setText(remark);
                        etTradeOrder.setText(tradeHash);
                        if (UtilTools.empty(blockNumber)) {
                            etBlockNum.setText("");
                        } else {
                            etBlockNum.setText(blockNumber);
                        }
                        tvTime.setText(createTime);
                        if ("1".equals(tradeStatus)) {
                            ivTransferStatus.setImageResource(R.drawable.dengdai);
                        } else if ("2".equals(tradeStatus)) {
                            ivTransferStatus.setImageResource(R.drawable.chenggong);
                        } else {
                            ivTransferStatus.setImageResource(R.drawable.shibai);
                        }
                        break;
                    case "-1":
                        finish();
                        intent = new Intent(TransferDetailActivity.this, LoginActivity1.class);
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
        ClipboardManager cm;
        String url;
        switch (v.getId()) {
            case R.id.back_img:
                finish();
                break;
            case R.id.tvJump:
                String tradeOrder = etTradeOrder.getText().toString().trim();
                if (!TextUtils.isEmpty(tradeOrder)) {
                    String baseUrl = "https://etherscan.io/tx/";
                    Intent intent = new Intent(TransferDetailActivity.this, NewsDetailActivity.class);
                    intent.putExtra("url", baseUrl + tradeOrder);
                    startActivity(intent);
                }
                break;
            case R.id.tvCopy:
                cm = (ClipboardManager) BasicApplication.getContext().getSystemService(Context.CLIPBOARD_SERVICE);
                url = etRecivedAddress.getText().toString();
                if (!TextUtils.isEmpty(url)) {
                    // 创建普通字符型ClipData
                    ClipData mClipData = ClipData.newPlainText("Label", url);
                    // 将ClipData内容放到系统剪贴板里。
                    if (cm != null) {
                        cm.setPrimaryClip(mClipData);
                        showToastMsg(R.string.copy_success);
                    }
                    finish();
                } else {
                    showToastMsg(R.string.copy_fail);
                }
                break;
            case R.id.tvCopy1:
                cm = (ClipboardManager) BasicApplication.getContext().getSystemService(Context.CLIPBOARD_SERVICE);
                url = etPayAddress.getText().toString();
                if (!TextUtils.isEmpty(url)) {
                    // 创建普通字符型ClipData
                    ClipData mClipData = ClipData.newPlainText("Label", url);
                    // 将ClipData内容放到系统剪贴板里。
                    if (cm != null) {
                        cm.setPrimaryClip(mClipData);
                        showToastMsg(R.string.copy_success);
                    }
                    finish();
                } else {
                    showToastMsg(R.string.copy_fail);
                }
                break;
            case R.id.tvCopy2:
                cm = (ClipboardManager) BasicApplication.getContext().getSystemService(Context.CLIPBOARD_SERVICE);
                url = etTradeOrder.getText().toString();
                if (!TextUtils.isEmpty(url)) {
                    // 创建普通字符型ClipData
                    ClipData mClipData = ClipData.newPlainText("Label", url);
                    // 将ClipData内容放到系统剪贴板里。
                    if (cm != null) {
                        cm.setPrimaryClip(mClipData);
                        showToastMsg(R.string.copy_success);
                    }
                    finish();
                } else {
                    showToastMsg(R.string.copy_fail);
                }
                break;
            default:
        }
    }
}

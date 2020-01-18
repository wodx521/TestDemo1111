package com.lr.sia.ui.moudle1.activity;

import android.content.Intent;
import android.text.Editable;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.lr.sia.R;
import com.lr.sia.api.MethodUrl;
import com.lr.sia.basic.BasicActivity;
import com.lr.sia.basic.MbsConstans;
import com.lr.sia.mvp.presenter.RequestPresenterImp;
import com.lr.sia.mywidget.CustomerTextWatcher;
import com.lr.sia.mywidget.dialog.TradePassDialog;
import com.lr.sia.ui.moudle.activity.LoginActivity1;
import com.lr.sia.ui.moudle.activity.TestScanActivity;
import com.lr.sia.utils.permission.PermissionsUtils;
import com.lr.sia.utils.permission.RePermissionResultBack;
import com.lr.sia.utils.tool.JSONUtil;
import com.lr.sia.utils.tool.SPUtils;
import com.lr.sia.utils.tool.UtilTools;
import com.yanzhenjie.permission.runtime.Permission;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.Map;

public class TransferActivity extends BasicActivity implements View.OnClickListener, TradePassDialog.PassFullListener {
    private TextView tvTitle, tvBalance, tvNext;
    private ImageView backImg, ivScan;
    private EditText etWalletAddress, etTransferAmount, etMinerFee, etRemarks;
    private String mRequestTag;
    private String coinId;
    private long tempTime;
    private TradePassDialog mTradePassDialog;
    private DecimalFormat decimalFormat = new DecimalFormat("#.###########");

    @Override
    public int getContentView() {
        return R.layout.activity_transfer;
    }

    @Override
    public void init() {
        coinId = getIntent().getStringExtra("coinId");
        tvTitle = findViewById(R.id.tvTitle);
        backImg = findViewById(R.id.back_img);
        ivScan = findViewById(R.id.ivScan);
        tvBalance = findViewById(R.id.tvBalance);
        etWalletAddress = findViewById(R.id.etWalletAddress);
        etTransferAmount = findViewById(R.id.etTransferAmount);
        etMinerFee = findViewById(R.id.etMinerFee);
        etRemarks = findViewById(R.id.etRemarks);
        tvNext = findViewById(R.id.tvNext);
        backImg.setOnClickListener(this);
        tvNext.setOnClickListener(this);
        ivScan.setOnClickListener(this);

        tvTitle.setText(R.string.transfer);

        etTransferAmount.addTextChangedListener(new CustomerTextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {
                super.afterTextChanged(s);
                String temp = s.toString();
                int posDot = temp.indexOf(".");
                if (posDot <= 0) return;
                if (temp.length() - posDot - 1 > 4) {
                    s.delete(posDot + 5, posDot + 6);
                }
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String s1 = s.toString();
                if (s1.length() > 0) {
                    if (s1.startsWith(".")) {
                        etTransferAmount.setText("");
                    } else {
                        String feeJson = SPUtils.get(TransferActivity.this, "minnerFee", "") + "";
                        if (!TextUtils.isEmpty(feeJson)) {
                            Map<String, Object> stringObjectMap = JSONUtil.getInstance().jsonMap(feeJson);
                            String gasFee = stringObjectMap.get("gas_fee") + "";
                            String sysFeeRatio = stringObjectMap.get("sys_fee_ratio") + "";
                            BigDecimal bigAmount = new BigDecimal(s1);
                            BigDecimal bigFee = new BigDecimal(gasFee);
                            BigDecimal bigRatio = new BigDecimal(sysFeeRatio);
                            etMinerFee.setText(decimalFormat.format(bigAmount.multiply(bigRatio).add(bigFee).doubleValue()) + "ETH");
                        }
                    }
                } else {
                    etMinerFee.setText("");
                }
            }
        });

        etWalletAddress.requestFocus();
        etWalletAddress.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    String walletAddress = etWalletAddress.getText().toString();
                    if (!TextUtils.isEmpty(walletAddress)) {
                        getFeeAction(walletAddress);
                    } else {
                        showToastMsg(R.string.inputReceiveAddress);
                    }
                }
            }
        });

        etWalletAddress.addTextChangedListener(new CustomerTextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() > 0) {
                    etTransferAmount.setEnabled(true);
                    etRemarks.setEnabled(true);
                }else{
                    etTransferAmount.setEnabled(false);
                    etRemarks.setEnabled(false);
                }
            }
        });
        getBalanceAction();
    }

    private void getBalanceAction() {
        mRequestPresenterImp = new RequestPresenterImp(this, TransferActivity.this);
        Map<String, Object> map = new HashMap<>();
        if (UtilTools.empty(MbsConstans.ACCESS_TOKEN)) {
            MbsConstans.ACCESS_TOKEN = SPUtils.get(TransferActivity.this, MbsConstans.SharedInfoConstans.ACCESS_TOKEN, "").toString();
        }
        map.put("token", MbsConstans.ACCESS_TOKEN);
        map.put("id", coinId);
        mRequestPresenterImp.requestPostToMap(MethodUrl.ACCOUNT_GETUSERBALANCE, map);
    }

    private void getFeeAction(String walletAddress) {
        mRequestPresenterImp = new RequestPresenterImp(this, TransferActivity.this);
        Map<String, Object> map = new HashMap<>();
        if (UtilTools.empty(MbsConstans.ACCESS_TOKEN)) {
            MbsConstans.ACCESS_TOKEN = SPUtils.get(TransferActivity.this, MbsConstans.SharedInfoConstans.ACCESS_TOKEN, "").toString();
        }
        map.put("token", MbsConstans.ACCESS_TOKEN);
        map.put("to_address", walletAddress);
        mRequestPresenterImp.requestPostToMap(MethodUrl.ACCOUNT_ESTIMATEGAS, map);
    }

    @Override
    protected void onPause() {
        super.onPause();
        SPUtils.remove(TransferActivity.this, "minnerFee");
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
            case MethodUrl.ACCOUNT_GETUSERBALANCE:
                switch (tData.get("code") + "") {
                    case "1":
                        Map<String, Object> data = (Map<String, Object>) tData.get("data");
                        String coinAmount = data.get("coin_amount") + "";
                        String coinTotalPrice = data.get("coin_total_price") + "";
                        String curSymbol = data.get("cur_symbol") + "";
                        tvBalance.setText(coinAmount);
                        break;
                    case "-1":
                        finish();
                        intent = new Intent(TransferActivity.this, LoginActivity1.class);
                        startActivity(intent);
                        break;
                    case "0":
                        showToastMsg(tData.get("msg") + "");
                        break;
                    default:
                }
                break;
            case MethodUrl.ACCOUNT_ESTIMATEGAS:
                switch (tData.get("code") + "") {
                    case "1":
                        Map<String, Object> data = (Map<String, Object>) tData.get("data");
                        SPUtils.put(TransferActivity.this, "minnerFee", JSONUtil.getInstance().objectToJson(data));
                        String amount = etTransferAmount.getText().toString();
                        if (!TextUtils.isEmpty(amount)) {
                            BigDecimal bigAmount = new BigDecimal(amount);
                            String gasFee = data.get("gas_fee") + "";
                            String sysFeeRatio = data.get("sys_fee_ratio") + "";
                            BigDecimal bigFee = new BigDecimal(gasFee);
                            BigDecimal bigRatio = new BigDecimal(sysFeeRatio);
                            etMinerFee.setText(decimalFormat.format(bigAmount.multiply(bigRatio).add(bigFee).doubleValue()) + "ETH");
                        }
                        break;
                    case "-1":
                        finish();
                        intent = new Intent(TransferActivity.this, LoginActivity1.class);
                        startActivity(intent);
                        break;
                    case "0":
                        showToastMsg(tData.get("msg") + "");
                        break;
                    default:
                }
                break;
            case MethodUrl.TRANSFER_SENDCOINTRADE:
                mTradePassDialog.dismiss();
                switch (tData.get("code") + "") {
                    case "1":
                        String data = tData.get("data") + "";
                        intent = new Intent(TransferActivity.this, TransferDetailActivity.class);
                        intent.putExtra("transferDetailId", data);
                        startActivity(intent);
                        finish();
                        break;
                    case "-1":
                        finish();
                        intent = new Intent(TransferActivity.this, LoginActivity1.class);
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
            case R.id.back_img:
                finish();
                break;
            case R.id.tvNext:
                etWalletAddress.clearFocus();
                String address = etWalletAddress.getText().toString();
                String amount = etTransferAmount.getText().toString();
                if (!TextUtils.isEmpty(address) && !TextUtils.isEmpty(amount)) {
                    showPassDialog();
                } else {
                    if (TextUtils.isEmpty(address)) {
                        showToastMsg(R.string.inputReceiveAddress);
                    } else {
                        showToastMsg(R.string.inputTransferAmount);
                    }
                }
                break;
            case R.id.ivScan:
                PermissionsUtils.requsetRunPermission(TransferActivity.this, new RePermissionResultBack() {
                    @Override
                    public void requestSuccess() {
                        Intent intent = new Intent(TransferActivity.this, TestScanActivity.class);
                        intent.putExtra("type", "5");
                        startActivityForResult(intent, MbsConstans.SCAN_CODE);
                    }

                    @Override
                    public void requestFailer() {
                        showToastMsg(R.string.failure);
                    }
                }, Permission.Group.CAMERA);
                break;
            default:
        }
    }

    private void showPassDialog() {
        if (mTradePassDialog == null) {
            mTradePassDialog = new TradePassDialog(this, true);
            mTradePassDialog.setPassFullListener(TransferActivity.this);
            mTradePassDialog.showAtLocation(Gravity.BOTTOM, 0, 0);
            mTradePassDialog.mPasswordEditText.setText(null);

            //忘记密码
            mTradePassDialog.mForgetPassTv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //getMsgCodeAction();
                }
            });

        } else {
            mTradePassDialog.showAtLocation(Gravity.BOTTOM, 0, 0);
            mTradePassDialog.mPasswordEditText.setText(null);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case MbsConstans.SCAN_CODE:
                    if (data != null) {
                        String result = data.getStringExtra("result");
                        etWalletAddress.setText(result);
                    }
                    break;
                default:
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onPassFullListener(String pass) {
        mTradePassDialog.mPasswordEditText.setText(null);
        Map<String, Object> map = new HashMap<>();
        if (UtilTools.empty(MbsConstans.ACCESS_TOKEN)) {
            MbsConstans.ACCESS_TOKEN = SPUtils.get(TransferActivity.this, MbsConstans.ACCESS_TOKEN, "").toString();
        }
        map.put("token", MbsConstans.ACCESS_TOKEN);
        map.put("coin_amount", etTransferAmount.getText().toString());
        map.put("to_address", etWalletAddress.getText() + "");
        if (!UtilTools.empty(etRemarks.getText().toString())) {
            map.put("remark", etRemarks.getText().toString());
        } else {
            map.put("remark", "");
        }
        map.put("gas", etMinerFee.getText().toString());
        map.put("coin_id", coinId);
        map.put("pay_pwd", pass);
        Map<String, String> mHeaderMap = new HashMap<String, String>();
        mRequestPresenterImp.requestPostToMap(mHeaderMap, MethodUrl.TRANSFER_SENDCOINTRADE, map);
    }
}

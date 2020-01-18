package com.lr.sia.ui.moudle3.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.core.content.ContextCompat;

import com.jaeger.library.StatusBarUtil;
import com.lr.sia.R;
import com.lr.sia.api.MethodUrl;
import com.lr.sia.basic.BasicActivity;
import com.lr.sia.basic.MbsConstans;
import com.lr.sia.mvp.view.RequestView;
import com.lr.sia.mywidget.CustomerTextWatcher;
import com.lr.sia.mywidget.dialog.TradePassDialog;
import com.lr.sia.ui.moudle.activity.LoginActivity1;
import com.lr.sia.utils.tool.SPUtils;
import com.lr.sia.utils.tool.UtilTools;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * 转账
 */
public class TransferMoneyActivity1 extends BasicActivity implements RequestView, TradePassDialog.PassFullListener {
    @BindView(R.id.title_text)
    TextView mTitleText;
    @BindView(R.id.left_back_lay)
    LinearLayout mLeftBackLay;
    @BindView(R.id.tvReceiveName)
    TextView tvReceiveName;
    @BindView(R.id.et_money)
    EditText etMoney;
    @BindView(R.id.tvFee)
    TextView tvFee;
    @BindView(R.id.tvBalance)
    TextView tvBalance;
    @BindView(R.id.btConfirm)
    Button btConfirm;
    @BindView(R.id.tvTransferDes)
    TextView tvTransferDes;

    private String tarid = "";
    private String type = "";
    private String id = "";
    private TradePassDialog mTradePassDialog;

    @Override
    public void onPassFullListener(String pass) {
        mTradePassDialog.mPasswordEditText.setText(null);
        Map<String, Object> map = new HashMap<>();
        if (UtilTools.empty(MbsConstans.ACCESS_TOKEN)) {
            MbsConstans.ACCESS_TOKEN = SPUtils.get(TransferMoneyActivity1.this, MbsConstans.ACCESS_TOKEN, "").toString();
        }
        map.put("token", MbsConstans.ACCESS_TOKEN);
        map.put("total", etMoney.getText() + "");
        map.put("receiver_id", id);
        map.put("pay_pwd", pass);
        Map<String, String> mHeaderMap = new HashMap<String, String>();
        mRequestPresenterImp.requestPostToMap(mHeaderMap, MethodUrl.TRANSFER_CHATTRANSFERACTIVE, map);
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
        switch (mType) {
            case MethodUrl.TRANSFER_CHATTRANSFERACTIVE:
                switch (tData.get("code") + "") {
                    case "1": //请求成功
                        mTradePassDialog.dismiss();
                        String red_id = tData.get("data") + "";
                        finish();
                        // 发送转账信息

                        break;
                    case "0": //请求失败
                        showToastMsg(tData.get("msg") + "");
                        break;

                    case "-1": //token过期
                        closeAllActivity();
                        intent = new Intent(TransferMoneyActivity1.this, LoginActivity1.class);
                        startActivity(intent);
                        break;
                    default:
                }
                break;
            case MethodUrl.TRANSFER_CHATTRANSPAGE:
                switch (tData.get("code") + "") {
                    case "1": //请求成功
                        if (!UtilTools.empty(tData.get("data") + "")) {
                            Map<String, Object> mapData = (Map<String, Object>) tData.get("data");
                            String balance = mapData.get("balance") + "";
                            String transFee = mapData.get("trans_fee") + "";
                            tvBalance.setText(getString(R.string.userBalance).replace("%s", balance));
                            tvFee.setHint(transFee);
                        }
                        break;
                    case "0": //请求失败
                        showToastMsg(tData.get("msg") + "");
                        break;

                    case "-1": //token过期
                        closeAllActivity();
                        intent = new Intent(TransferMoneyActivity1.this, LoginActivity1.class);
                        startActivity(intent);
                        break;
                    default:
                }
                break;
            case MethodUrl.USER_GETUSERINFO:
                switch (tData.get("code") + "") {
                    case "1": //请求成功
                        Map<String, Object> data = (Map<String, Object>) tData.get("data");
                        tvReceiveName.setText(data.get("rc_name") + "");
                        break;
                    case "0": //请求失败
                        showToastMsg(tData.get("msg") + "");
                        break;
                    case "-1": //token过期
                        finish();
                        intent = new Intent(TransferMoneyActivity1.this, LoginActivity1.class);
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
        dealFailInfo(map, mType);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // TODO: add setContentView(...) invocation
        ButterKnife.bind(this);
    }

    @Override
    public int getContentView() {
        return R.layout.activity_transfer_money1;
    }

    @Override
    public void init() {
        //getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
        StatusBarUtil.setColorForSwipeBack(this, ContextCompat.getColor(this, MbsConstans.TOP_BAR_COLOR), MbsConstans.ALPHA);
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            tarid = bundle.getString("tarid");
            type = bundle.getString("type");
            id = bundle.getString("id");
            getMoneyAction();
            searchAction(id);
        }
        mTitleText.setText(R.string.transferInternal);
        mTitleText.setCompoundDrawables(null, null, null, null);
        tvBalance.setText(getString(R.string.userBalance).replace("%s", "0.00"));
        tvFee.setText(getString(R.string.defaultFee).replace("%s", "0.00"));
        etMoney.addTextChangedListener(new CustomerTextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() > 0) {
                    btConfirm.setEnabled(true);
                    if (s.toString().startsWith(".")) {
                        etMoney.setText("0");
                        etMoney.setSelection(etMoney.getText().toString().length());
                    } else {
                        String feeRatio = tvFee.getHint().toString();
                        BigDecimal transferNum = new BigDecimal(s.toString());
                        BigDecimal fee = new BigDecimal(feeRatio);
                        tvFee.setText(getString(R.string.defaultFee).replace("%s", transferNum.multiply(fee).toPlainString()));
                    }
                } else {
                    btConfirm.setEnabled(false);
                    tvFee.setText(getString(R.string.defaultFee).replace("%s", "0.00"));
                }
            }
        });
        tvTransferDes.setText(R.string.notice4);
    }

    private void searchAction(String content) {
        Map<String, Object> map = new HashMap<>();
        if (UtilTools.empty(MbsConstans.ACCESS_TOKEN)) {
            MbsConstans.ACCESS_TOKEN = SPUtils.get(MbsConstans.ACCESS_TOKEN, "").toString();
        }
        map.put("token", MbsConstans.ACCESS_TOKEN);
        map.put("id", content);
        mRequestPresenterImp.requestPostToMap(MethodUrl.USER_GETUSERINFO, map);
    }

    private void getMoneyAction() {
        Map<String, Object> map = new HashMap<>();
        if (UtilTools.empty(MbsConstans.ACCESS_TOKEN)) {
            MbsConstans.ACCESS_TOKEN = SPUtils.get(TransferMoneyActivity1.this, MbsConstans.ACCESS_TOKEN, "").toString();
        }
        map.put("token", MbsConstans.ACCESS_TOKEN);
        map.put("id", id);
        Map<String, String> mHeaderMap = new HashMap<String, String>();
        mRequestPresenterImp.requestPostToMap(mHeaderMap, MethodUrl.TRANSFER_CHATTRANSPAGE, map);
    }

    /**
     * activity销毁前触发的方法
     */
    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);
    }

    /**
     * activity恢复时触发的方法
     */
    @Override
    public void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
    }

    @OnClick({R.id.left_back_lay, R.id.btConfirm})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.left_back_lay:
                finish();
                break;
            case R.id.btConfirm:
                showPassDialog();
                break;
            default:
        }
    }

    private void showPassDialog() {
        if (mTradePassDialog == null) {
            mTradePassDialog = new TradePassDialog(this, true);
            mTradePassDialog.setPassFullListener(TransferMoneyActivity1.this);
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
}

package com.lr.sia.ui.moudle3.activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.text.TextUtils;
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
import com.lr.sia.mywidget.CustomerTextWatcher;
import com.lr.sia.mywidget.dialog.TradePassDialog;
import com.lr.sia.ui.moudle.activity.LoginActivity1;
import com.lr.sia.utils.tool.SPUtils;
import com.lr.sia.utils.tool.UtilTools;

import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * 红包
 */
public class RedMoneyActivity1 extends BasicActivity implements TradePassDialog.PassFullListener {

    @BindView(R.id.left_back_lay)
    LinearLayout leftBackLay;
    @BindView(R.id.title_text)
    TextView titleText;
    @BindView(R.id.etInputAmount)
    EditText etInputAmount;
    @BindView(R.id.tvBalance)
    TextView tvBalance;
    @BindView(R.id.beizhu_et)
    EditText beizhuEt;
    @BindView(R.id.etNumber)
    EditText etNumber;
    @BindView(R.id.tvTotalAmount)
    TextView tvTotalAmount;
    @BindView(R.id.tvTotalNum)
    TextView tvTotalNum;
    @BindView(R.id.btConfirm)
    Button btConfirm;
    @BindView(R.id.number_lay)
    LinearLayout numberLay;
    private TradePassDialog mTradePassDialog;
    private String type;
    private String id;
    private String tarid;
    private String userAmount;

    @Override
    public void onPassFullListener(String pass) {
        mTradePassDialog.mPasswordEditText.setText(null);
        Map<String, Object> map = new HashMap<>();
        if (UtilTools.empty(MbsConstans.ACCESS_TOKEN)) {
            MbsConstans.ACCESS_TOKEN = SPUtils.get(RedMoneyActivity1.this, MbsConstans.ACCESS_TOKEN, "").toString();
        }
        map.put("token", MbsConstans.ACCESS_TOKEN);
        map.put("number", etNumber.getText().toString());
        map.put("total", etInputAmount.getText().toString() + "");
        if (UtilTools.empty(beizhuEt.getText().toString())) {
            map.put("remark", getString(R.string.redMoneyDes));
        } else {
            map.put("remark", beizhuEt.getText() + "");
        }

        if ("1".equals(type)) {
            map.put("receiver_id", id);
        } else {
            map.put("receiver_id", tarid);
        }
        map.put("hongbao_type", type);
        map.put("pay_pwd", pass);
        Map<String, String> mHeaderMap = new HashMap<String, String>();
        mRequestPresenterImp.requestPostToMap(mHeaderMap, MethodUrl.CHAT_SEND_RED, map);
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
            case MethodUrl.CHAT_SEND_RED:
                switch (tData.get("code") + "") {
                    case "1": //请求成功
                        mTradePassDialog.dismiss();
                        String red_id = tData.get("data") + "";
                        setResult(RESULT_OK);
                        finish();
                        // TODO: 2020/1/17 发送红包

                        break;
                    case "0": //请求失败
                        showToastMsg(tData.get("msg") + "");
                        break;

                    case "-1": //token过期
                        closeAllActivity();
                        intent = new Intent(RedMoneyActivity1.this, LoginActivity1.class);
                        startActivity(intent);
                        break;
                    default:
                }
                break;
            case MethodUrl.CHAT_RED_GETSENDHONGBAOINFO:
                switch (tData.get("code") + "") {
                    case "1": //请求成功
                        if (!UtilTools.empty(tData.get("data") + "")) {
                            Map<String, Object> mapData = (Map<String, Object>) tData.get("data");
                            tvBalance.setText(Html.fromHtml(getString(R.string.userBalance).replace("%s", mapData.get("balance") + "")));
                            userAmount = mapData.get("user_amount") + "";
                            tvTotalNum.setText(getString(R.string.totalNum).replace("%s", userAmount));
                        }
                        break;
                    case "0": //请求失败
                        showToastMsg(tData.get("msg") + "");
                        break;
                    case "-1": //token过期
                        closeAllActivity();
                        intent = new Intent(RedMoneyActivity1.this, LoginActivity1.class);
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
        return R.layout.activity_red_money1;
    }

    @Override
    public void init() {
        titleText.setText(R.string.sendRed);
        tvTotalNum.setText(getString(R.string.totalNum).replace("%s", "0"));
        titleText.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
        //getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
        StatusBarUtil.setColorForSwipeBack(this, ContextCompat.getColor(this, MbsConstans.TOP_BAR_COLOR), MbsConstans.ALPHA);
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            type = getIntent().getStringExtra("type");
            id = getIntent().getStringExtra("id");
            tarid = getIntent().getStringExtra("tarid");
            getRedInfoAction();
        }
        if (type.equals("1")) {
            numberLay.setVisibility(View.GONE);
            etNumber.setText("1");
        } else {
            numberLay.setVisibility(View.VISIBLE);
        }
        tvBalance.setText(Html.fromHtml(getString(R.string.userBalance).replace("%s", "0.00")));
        tvTotalAmount.setText(Html.fromHtml(getString(R.string.defaultRed)));
        etInputAmount.addTextChangedListener(new CustomerTextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() > 0) {
                    if (s.toString().startsWith(".")) {
                        etInputAmount.setText("");
                    }
                    if (etNumber.getText().length() > 0) {
                        btConfirm.setEnabled(true);
                    } else {
                        btConfirm.setEnabled(false);
                    }
                    tvTotalAmount.setText(Html.fromHtml(getString(R.string.defaultRed).replace("0.00", s.toString())));
                } else {
                    btConfirm.setEnabled(false);
                    tvTotalAmount.setText(Html.fromHtml(getString(R.string.defaultRed)));
                }
            }
        });
        etNumber.addTextChangedListener(new CustomerTextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() > 0 && etInputAmount.getText().length() > 0) {
                    int number = Integer.parseInt(s.toString());
                    int totalNumber = Integer.parseInt(userAmount);
                    if (number > totalNumber) {
                        btConfirm.setEnabled(false);
                        showToastMsg("红包个数不能群总人数");
                    } else {
                        btConfirm.setEnabled(true);
                    }
                } else {
                    btConfirm.setEnabled(false);
                }
            }
        });
    }

    public void getRedInfoAction() {
        Map<String, Object> map = new HashMap<>();
        if (UtilTools.empty(MbsConstans.ACCESS_TOKEN)) {
            MbsConstans.ACCESS_TOKEN = SPUtils.get(RedMoneyActivity1.this, MbsConstans.ACCESS_TOKEN, "").toString();
        }
        map.put("token", MbsConstans.ACCESS_TOKEN);
        map.put("hongbao_type", type);
        if ("1".equals(type)) {
            map.put("receiver_id", id);
        } else if ("2".equals(type)) {
            map.put("receiver_id", tarid);
        }
        Map<String, String> mHeaderMap = new HashMap<String, String>();
        mRequestPresenterImp.requestPostToMap(mHeaderMap, MethodUrl.CHAT_RED_GETSENDHONGBAOINFO, map);

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
                String amount = etInputAmount.getText().toString();
                String number = etNumber.getText().toString();

//                int totalNum = Integer.parseInt(userAmount);
//                int inputNum = Integer.parseInt(number);
//                if (inputNum > totalNum) {
//
//                }
                if (!TextUtils.isEmpty(amount) && !TextUtils.isEmpty(number)) {
                    showPassDialog();
                } else {
                    if (TextUtils.isEmpty(amount)) {
                        showToastMsg(R.string.inputRedAmount);
                    } else {
                        showToastMsg(R.string.inputRedNumber);
                    }
                }
                break;
            default:
        }
    }

    private void showPassDialog() {
        if (mTradePassDialog == null) {
            mTradePassDialog = new TradePassDialog(this, true);
            mTradePassDialog.setPassFullListener(RedMoneyActivity1.this);
            mTradePassDialog.showAtLocation(Gravity.BOTTOM, 0, 0);
            mTradePassDialog.mPasswordEditText.setText(null);
        } else {
            mTradePassDialog.showAtLocation(Gravity.BOTTOM, 0, 0);
            mTradePassDialog.mPasswordEditText.setText(null);
        }
    }
}

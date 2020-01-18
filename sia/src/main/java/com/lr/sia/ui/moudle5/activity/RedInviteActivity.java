package com.lr.sia.ui.moudle5.activity;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.text.Html;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.core.content.ContextCompat;

import com.jaeger.library.StatusBarUtil;
import com.lr.sia.R;
import com.lr.sia.api.MethodUrl;
import com.lr.sia.basic.BasicActivity;
import com.lr.sia.basic.BasicApplication;
import com.lr.sia.basic.MbsConstans;
import com.lr.sia.mywidget.CustomerTextWatcher;
import com.lr.sia.mywidget.dialog.TradePassDialog;
import com.lr.sia.ui.moudle.activity.LoginActivity1;
import com.lr.sia.ui.moudle5.dialog.SendSuccessDialog;
import com.lr.sia.utils.tool.SPUtils;
import com.lr.sia.utils.tool.UtilTools;

import java.util.HashMap;
import java.util.Map;

public class RedInviteActivity extends BasicActivity implements View.OnClickListener, TradePassDialog.PassFullListener {
    private ImageView backImg;
    private TextView titleText;
    private EditText etInputAmount;
    private TextView tvBalance;
    private EditText etNumber;
    private Button btConfirm;
    private TradePassDialog mTradePassDialog;
    private String url;
    private SendSuccessDialog sendSuccessDialog;

    @Override
    public int getContentView() {
        return R.layout.activity_red_invite;
    }

    @Override
    public void init() {
        StatusBarUtil.setColorForSwipeBack(this, ContextCompat.getColor(this, MbsConstans.TOP_BAR_COLOR), MbsConstans.ALPHA);
        sendSuccessDialog = new SendSuccessDialog(RedInviteActivity.this);
        backImg = findViewById(R.id.back_img);
        titleText = findViewById(R.id.title_text);
        etInputAmount = findViewById(R.id.etInputAmount);
        tvBalance = findViewById(R.id.tvBalance);
        etNumber = findViewById(R.id.etNumber);
        btConfirm = findViewById(R.id.btConfirm);

        backImg.setOnClickListener(this);
        btConfirm.setOnClickListener(this);
        titleText.setText(R.string.redEnvelopeInvitation);

        etInputAmount.addTextChangedListener(new CustomerTextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                try {
                    if (s.length() > 0 && etNumber.getText().toString().length() > 0) {
                        btConfirm.setEnabled(true);
                    } else {
                        btConfirm.setEnabled(false);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        etNumber.addTextChangedListener(new CustomerTextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() > 0 && etInputAmount.getText().toString().length() > 0) {
                    btConfirm.setEnabled(true);
                } else {
                    btConfirm.setEnabled(false);
                }
            }
        });
        getRedInfoAction();
    }

    public void getRedInfoAction() {
        Map<String, Object> map = new HashMap<>();
        if (UtilTools.empty(MbsConstans.ACCESS_TOKEN)) {
            MbsConstans.ACCESS_TOKEN = SPUtils.get(RedInviteActivity.this, MbsConstans.ACCESS_TOKEN, "").toString();
        }
        map.put("token", MbsConstans.ACCESS_TOKEN);
        Map<String, String> mHeaderMap = new HashMap<String, String>();
        mRequestPresenterImp.requestPostToMap(mHeaderMap, MethodUrl.INVITE_GETSENDHONGBAOINFO, map);
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
            case MethodUrl.INVITE_SENDHONGBAO:
                switch (tData.get("code") + "") {
                    case "1": //请求成功
                        mTradePassDialog.dismiss();
                        if (!UtilTools.empty(tData.get("data") + "")) {
                            String url = tData.get("data") + "";
                            sendSuccessDialog.getDialog(true,getString(R.string.sendSuccess));
                            sendSuccessDialog.setOrderClickListener(new SendSuccessDialog.OrderClickListener() {
                                @Override
                                public void onCopy() {
                                    ClipboardManager cm = (ClipboardManager) BasicApplication.getContext().getSystemService(Context.CLIPBOARD_SERVICE);
                                    if (!TextUtils.isEmpty(url)) {
                                        // 创建普通字符型ClipData
                                        ClipData mClipData = ClipData.newPlainText("Label", url);
                                        // 将ClipData内容放到系统剪贴板里。
                                        if (cm != null) {
                                            cm.setPrimaryClip(mClipData);
                                            showToastMsg(getString(R.string.copy_success));
                                        }
                                        finish();
                                    } else {
                                        showToastMsg(getString(R.string.copy_fail));
                                    }
                                }
                            });
                        }
                        break;
                    case "0": //请求失败
                        showToastMsg(tData.get("msg") + "");
                        break;
                    case "-1": //token过期
                        closeAllActivity();
                        intent = new Intent(RedInviteActivity.this, LoginActivity1.class);
                        startActivity(intent);
                        break;
                    default:
                }
                break;
            case MethodUrl.INVITE_GETSENDHONGBAOINFO:
                switch (tData.get("code") + "") {
                    case "1": //请求成功
                        if (!UtilTools.empty(tData.get("data") + "")) {
                            Map<String, Object> mapData = (Map<String, Object>) tData.get("data");
                            String curCode = mapData.get("cur_code") + "";
                            String balanceCur = mapData.get("balance_cur") + "";
                            String uncollected = mapData.get("uncollected") + "";
                            String url = mapData.get("url") + "";
                            if (uncollected.equals("1")) {
                                btConfirm.setEnabled(false);
                                sendSuccessDialog.getDialog(false,getString(R.string.sendNotice));
                                sendSuccessDialog.setOrderClickListener(new SendSuccessDialog.OrderClickListener() {
                                    @Override
                                    public void onCopy() {
                                        ClipboardManager cm = (ClipboardManager) BasicApplication.getContext().getSystemService(Context.CLIPBOARD_SERVICE);
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
                                    }
                                });
                            } else {
                                btConfirm.setEnabled(true);
                            }
//                            url = mapData.get("url") + "";
                            tvBalance.setText(Html.fromHtml(getString(R.string.userBalance1)
                                    .replace("%s", mapData.get("balance") + "")
                                    .replace("%w", balanceCur)
                                    .replace("CNY", curCode)));
                        }
                        break;
                    case "0": //请求失败
                        showToastMsg(tData.get("msg") + "");
                        break;
                    case "-1": //token过期
                        closeAllActivity();
                        intent = new Intent(RedInviteActivity.this, LoginActivity1.class);
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
            case R.id.btConfirm:
                String amount = etInputAmount.getText().toString();
                String number = etNumber.getText().toString();
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
            mTradePassDialog.setPassFullListener(RedInviteActivity.this);
            mTradePassDialog.showAtLocation(Gravity.BOTTOM, 0, 0);
            mTradePassDialog.mPasswordEditText.setText(null);
        } else {
            mTradePassDialog.showAtLocation(Gravity.BOTTOM, 0, 0);
            mTradePassDialog.mPasswordEditText.setText(null);
        }
    }

    @Override
    public void onPassFullListener(String pass) {
        mTradePassDialog.mPasswordEditText.setText("");
        getRedAction(pass);
    }

    public void getRedAction(String pass) {
        Map<String, Object> map = new HashMap<>();
        if (UtilTools.empty(MbsConstans.ACCESS_TOKEN)) {
            MbsConstans.ACCESS_TOKEN = SPUtils.get(RedInviteActivity.this, MbsConstans.ACCESS_TOKEN, "").toString();
        }
        map.put("token", MbsConstans.ACCESS_TOKEN);
        map.put("total", etInputAmount.getText().toString());
        map.put("number", etNumber.getText().toString());
        map.put("pay_pwd", pass);
        Map<String, String> mHeaderMap = new HashMap<String, String>();
        mRequestPresenterImp.requestPostToMap(mHeaderMap, MethodUrl.INVITE_SENDHONGBAO, map);
    }
}

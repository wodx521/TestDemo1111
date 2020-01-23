package com.lr.sia.ui.moudle.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;

import com.github.jdsjlzx.recyclerview.LRecyclerView;
import com.github.jdsjlzx.recyclerview.LRecyclerViewAdapter;
import com.jaeger.library.StatusBarUtil;
import com.lr.sia.R;
import com.lr.sia.basic.BasicActivity;
import com.lr.sia.basic.MbsConstans;
import com.lr.sia.listener.SelectBackListener;
import com.lr.sia.mvp.view.RequestView;
import com.lr.sia.ui.moudle.adapter.RecordListAdapter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class RemeberCodeActivity extends BasicActivity implements RequestView, SelectBackListener {


    @BindView(R.id.bt_next)
    Button mBtNext;
    @BindView(R.id.left_back_lay)
    LinearLayout mLeftBackLay;
    @BindView(R.id.title_text)
    TextView mTitleText;
    @BindView(R.id.back_img)
    ImageView backImg;
    @BindView(R.id.back_text)
    TextView backText;
    @BindView(R.id.spinner)
    Spinner spinner;
    @BindView(R.id.rvRecordList)
    LRecyclerView rvRecordList;

    private ArrayList<String> mnemonicList = new ArrayList<>();
    private LRecyclerViewAdapter lRecyclerViewAdapter;
    private String invcode;
    private HashMap<String, Object> registParam;

    @OnClick({R.id.bt_next, R.id.left_back_lay})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.left_back_lay:
                setResult(RESULT_CANCELED);
                finish();
                break;
            case R.id.bt_next:
                Intent intent = new Intent(RemeberCodeActivity.this, CodeCheckActivity.class);
                intent.putStringArrayListExtra("code", mnemonicList);
                intent.putExtra("registParam", registParam);
                startActivityForResult(intent, MbsConstans.IS_APPROVE_RIGHT);
                break;
            default:
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    /**
     * @descriptoin 请求前加载progress
     * @author dc
     * @date 2017/2/16 11:00
     */
    @Override
    public void showProgress() {
        showProgressDialog();
    }

    /**
     * @descriptoin 请求结束之后隐藏progress
     * @author dc
     * @date 2017/2/16 11:01
     */
    @Override
    public void disimissProgress() {
        dismissProgressDialog();
    }

    /**
     * @param tData 数据类型
     * @param mType
     * @descriptoin 请求数据成功
     * @author dc
     * @date 2017/2/16 11:01
     */
    @Override
    public void loadDataSuccess(Map<String, Object> tData, String mType) {

    }

    /**
     * @param map
     * @param mType
     * @descriptoin 请求数据错误
     * @date 2017/2/16 11:01
     */
    @Override
    public void loadDataError(Map<String, Object> map, String mType) {

    }

    @Override
    public void onSelectBackListener(Map<String, Object> map, int type) {

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // TODO: add setContentView(...) invocation
        ButterKnife.bind(this);
    }

    @Override
    public int getContentView() {
        return R.layout.activity_remeber_code;
    }

    @Override
    public void init() {
        StatusBarUtil.setColorForSwipeBack(this, ContextCompat.getColor(this, MbsConstans.TOP_BAR_COLOR), MbsConstans.ALPHA);
        registParam = (HashMap<String, Object>) getIntent().getSerializableExtra("registParam");
        mTitleText.setText(R.string.registerUser);
        mTitleText.setCompoundDrawables(null, null, null, null);
        RecordListAdapter recordListAdapter = new RecordListAdapter(RemeberCodeActivity.this);
        lRecyclerViewAdapter = new LRecyclerViewAdapter(recordListAdapter);
        rvRecordList.setAdapter(lRecyclerViewAdapter);
        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        if (bundle != null) {
            invcode = bundle.getString("result") + "";
        }
        mnemonicList.clear();

        for (int i = 0; i < 8; i++) {
            mnemonicList.add(getRandomString(4));
        }
        rvRecordList.setPullRefreshEnabled(false);
        recordListAdapter.setDataList(mnemonicList);

    }

    public static String getRandomString(int length) {
        String str = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ";
        Random random = new Random();
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < length; i++) {
            int number = random.nextInt(52);
            char c = str.charAt(number);
            sb.append(c);
        }
        return sb.toString();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case MbsConstans.IS_APPROVE_RIGHT:
                    if (data != null) {
                        setResult(RESULT_OK, data);
                        finish();
                    }
                    break;
                default:
            }
        } else if (resultCode == RESULT_CANCELED) {
            switch (requestCode) {
                case MbsConstans.IS_APPROVE_RIGHT:
                    finish();
                    break;
                default:
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }
}

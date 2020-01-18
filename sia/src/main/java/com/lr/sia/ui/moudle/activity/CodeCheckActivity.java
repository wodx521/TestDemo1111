package com.lr.sia.ui.moudle.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;

import com.github.jdsjlzx.interfaces.OnItemClickListener;
import com.github.jdsjlzx.recyclerview.LRecyclerView;
import com.github.jdsjlzx.recyclerview.LRecyclerViewAdapter;
import com.jaeger.library.StatusBarUtil;
import com.lr.sia.R;
import com.lr.sia.api.MethodUrl;
import com.lr.sia.basic.BasicActivity;
import com.lr.sia.basic.MbsConstans;
import com.lr.sia.ui.moudle.adapter.RecordListAdapter;
import com.lr.sia.ui.moudle.adapter.RecordListAdapter1;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class CodeCheckActivity extends BasicActivity {

    @BindView(R.id.left_back_lay)
    LinearLayout leftBackLay;
    @BindView(R.id.back_img)
    ImageView backImg;
    @BindView(R.id.title_text)
    TextView titleText;
    @BindView(R.id.rvRecordEmpty)
    LRecyclerView rvRecordEmpty;
    @BindView(R.id.rvRecordList)
    LRecyclerView rvRecordList;
    @BindView(R.id.bt_next)
    Button btNext;
    private LRecyclerViewAdapter lRecyclerViewAdapter, lRecyclerViewAdapter1;
    private ArrayList<String> tempCode = new ArrayList<>();
    private ArrayList<String> tempCode1 = new ArrayList<>();
    private HashMap<String, Object> registerParam;
    private Map<String, String> headMap = new HashMap<>();

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
     * @param mType 接口类型
     * @descriptoin 请求数据成功
     * @author dc
     * @date 2017/2/16 11:01
     */
    @Override
    public void loadDataSuccess(Map<String, Object> tData, String mType) {
        switch (mType) {
            case MethodUrl.USER_USERREG:
                switch (tData.get("code") + "") {
                    case "1":// 成功
                        showToastMsg(tData.get("msg") + "");
                        setResult(RESULT_OK);
                        finish();
                        break;
                    case "-1": // 超时
                        showToastMsg(tData.get("msg") + "");
                        break;
                    case "0": // 失败
                        showToastMsg(tData.get("msg") + "");
                        finish();
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
        return R.layout.activity_check_code;
    }

    @Override
    public void init() {
        rvRecordList.setPullRefreshEnabled(false);
        rvRecordEmpty.setPullRefreshEnabled(false);
        ArrayList<String> code = getIntent().getStringArrayListExtra("code");
        StatusBarUtil.setColorForSwipeBack(this, ContextCompat.getColor(this, MbsConstans.TOP_BAR_COLOR), MbsConstans.ALPHA);
        registerParam = (HashMap<String, Object>) getIntent().getSerializableExtra("registParam");
        titleText.setText(R.string.registerUser);
        titleText.setCompoundDrawables(null, null, null, null);
        tempCode.addAll(code);
        // 随机集合
        RecordListAdapter recordListAdapter = new RecordListAdapter(CodeCheckActivity.this);
        // 选择集合
        RecordListAdapter1 recordListAdapter1 = new RecordListAdapter1(CodeCheckActivity.this);
        // 选择集合1
        lRecyclerViewAdapter1 = new LRecyclerViewAdapter(recordListAdapter1);
        // 随机集合1
        lRecyclerViewAdapter = new LRecyclerViewAdapter(recordListAdapter);

        Collections.shuffle(tempCode);
        rvRecordEmpty.setAdapter(lRecyclerViewAdapter1);
        rvRecordList.setAdapter(lRecyclerViewAdapter);
        recordListAdapter.setDataList(tempCode);

        lRecyclerViewAdapter.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                if (tempCode1.size() < tempCode.size()) {
                    String s = tempCode.get(position);
                    tempCode1.add(s);
                    recordListAdapter1.setDataList(code, tempCode1);
                }
            }
        });
        recordListAdapter1.setVerificationResultListener(new RecordListAdapter1.VerificationResultListener() {
            @Override
            public void onResultListener(int position, boolean isRight) {
                if (!isRight) {
                    tempCode1.remove(position);
                }
                if (tempCode1.size() == tempCode.size() && isRight) {
                    btNext.setEnabled(true);
                } else {
                    btNext.setEnabled(false);
                }
            }
        });
    }

    @OnClick({R.id.left_back_lay, R.id.bt_next})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.left_back_lay:
                setResult(RESULT_CANCELED);
                finish();
                break;
            case R.id.bt_next:
//                int languageSelect = (int) SPUtils.get("languageSelect", -1);
//                headMap.put("lang", ((languageSelect == -1 ? 0 : languageSelect) + 1) + "");
//                mRequestPresenterImp.requestPostToMap(MethodUrl.USER_USERREG, registerParam);
                Intent intent = new Intent(CodeCheckActivity.this, VerifyRobotActivity.class);
                intent.putExtra("registParam", registerParam);
                startActivityForResult(intent, MbsConstans.IS_APPROVE_RIGHT);
                break;
            default:
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case MbsConstans.IS_APPROVE_RIGHT:
                    setResult(RESULT_OK);
                    finish();
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

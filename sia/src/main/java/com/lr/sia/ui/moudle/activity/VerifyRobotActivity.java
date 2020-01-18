package com.lr.sia.ui.moudle.activity;

import android.view.View;
import android.widget.ImageView;
import android.widget.SeekBar;

import androidx.core.content.ContextCompat;

import com.jaeger.library.StatusBarUtil;
import com.lr.sia.R;
import com.lr.sia.api.MethodUrl;
import com.lr.sia.basic.BasicActivity;
import com.lr.sia.basic.MbsConstans;
import com.lr.sia.mywidget.view.DouYuView;

import java.util.HashMap;
import java.util.Map;

public class VerifyRobotActivity extends BasicActivity implements View.OnClickListener {
    private ImageView backImg;
    private DouYuView dyV;
    private ImageView imageView;
    private SeekBar sbDy;
    private float timeUse;
    private long timeTemp;
    private HashMap<String, Object> registerParam;
    private int[] checkImgArray = {R.drawable.yanzheng_1, R.drawable.yanzheng_2, R.drawable.yanzheng_3,
            R.drawable.yanzheng_4, R.drawable.yanzheng_5, R.drawable.yanzheng_6, R.drawable.yanzheng_7,
            R.drawable.yanzheng_8, R.drawable.yanzheng_9, R.drawable.yanzheng_10, R.drawable.yanzheng_11,
            R.drawable.yanzheng_12, R.drawable.yanzheng_13, R.drawable.yanzheng_14, R.drawable.yanzheng_15,
            R.drawable.yanzheng_16, R.drawable.yanzheng_17, R.drawable.yanzheng_18, R.drawable.yanzheng_19,
            R.drawable.yanzheng_20};

    @Override
    public int getContentView() {
        return R.layout.activity_verify_robot;
    }

    @Override
    public void init() {
        backImg = findViewById(R.id.back_img);
        dyV = findViewById(R.id.dyV);
        imageView = findViewById(R.id.imageView);
        sbDy = findViewById(R.id.sb_dy);
        backImg.setOnClickListener(this);
        int i = (int) (Math.random() * checkImgArray.length);
        dyV.setImageResource(checkImgArray[i]);
        StatusBarUtil.setColorForSwipeBack(this, ContextCompat.getColor(this, MbsConstans.TOP_BAR_COLOR), MbsConstans.ALPHA);
        registerParam = (HashMap<String, Object>) getIntent().getSerializableExtra("registParam");
        sbDy.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                dyV.setUnitMoveDistance(dyV.getAverageDistance(seekBar.getMax()) * i);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                dyV.testPuzzle();
                timeUse = (System.currentTimeMillis() - timeTemp) / 1000.f;
            }
        });
        dyV.setPuzzleListener(new DouYuView.onPuzzleListener() {
            @Override
            public void onSuccess() {
                sbDy.setEnabled(false);
                showProgressDialog();
                mRequestPresenterImp.requestPostToMap(MethodUrl.USER_USERREG, registerParam);
            }

            @Override
            public void onFail() {
                showToastMsg(getString(R.string.checkError));
                sbDy.setProgress(0);
            }
        });
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
            case MethodUrl.USER_USERREG:
                dismissProgressDialog();
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
                        dyV.reSet();
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
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.back_img:
                finish();
                break;
            default:
        }
    }
}

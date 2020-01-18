package com.lr.sia.ui.moudle4.activity;

import android.text.Html;
import android.text.method.ScrollingMovementMethod;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.core.content.ContextCompat;

import com.jaeger.library.StatusBarUtil;
import com.lr.sia.R;
import com.lr.sia.basic.BasicActivity;
import com.lr.sia.basic.MbsConstans;

import java.util.Map;

public class WhitePaperActivity extends BasicActivity implements View.OnClickListener {
    private ImageView backImg;
    private TextView tvWhitePaper;

    @Override
    public int getContentView() {
        return R.layout.activity_white_paper;
    }

    @Override
    public void init() {
        backImg = findViewById(R.id.backImg);
        tvWhitePaper = findViewById(R.id.tvWhitePaper);
        StatusBarUtil.setColorForSwipeBack(this, ContextCompat.getColor(this, MbsConstans.TOP_BAR_COLOR), MbsConstans.ALPHA);
        backImg.setOnClickListener(this);
        String whitePaper = getIntent().getStringExtra("whitePaper");
        tvWhitePaper.setMovementMethod(ScrollingMovementMethod.getInstance());
        tvWhitePaper.setText(Html.fromHtml(whitePaper));
    }

    @Override
    public void showProgress() {

    }

    @Override
    public void disimissProgress() {

    }

    @Override
    public void loadDataSuccess(Map<String, Object> tData, String mType) {

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
            default:
        }
    }
}

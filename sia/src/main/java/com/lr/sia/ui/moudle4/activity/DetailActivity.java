package com.lr.sia.ui.moudle4.activity;

import android.content.Intent;
import android.text.Html;
import android.text.method.ScrollingMovementMethod;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.lr.sia.R;
import com.lr.sia.api.MethodUrl;
import com.lr.sia.basic.BasicActivity;
import com.lr.sia.basic.MbsConstans;
import com.lr.sia.ui.moudle.activity.LoginActivity1;
import com.lr.sia.utils.HtmlImageGetter;
import com.lr.sia.utils.tool.JSONUtil;
import com.lr.sia.utils.tool.SPUtils;
import com.lr.sia.utils.tool.UtilTools;

import java.util.HashMap;
import java.util.Map;

public class DetailActivity extends BasicActivity implements View.OnClickListener {
    private ImageView backImg;
    private TextView tvTitle;
    private TextView tvNewsTitle;
    private TextView tvNewsTime;
    private TextView tvNewsContent;
    private Map<String, Object> stringObjectMap;

    @Override
    public int getContentView() {
        return R.layout.activity_detail;
    }

    @Override
    public void init() {
        String newInfo = getIntent().getStringExtra("newInfo");
        stringObjectMap = JSONUtil.getInstance().jsonMap(newInfo);
        backImg = findViewById(R.id.backImg);
        tvTitle = findViewById(R.id.tvTitle);
        tvNewsTitle = findViewById(R.id.tvNewsTitle);
        tvNewsTime = findViewById(R.id.tvNewsTime);
        tvNewsContent = findViewById(R.id.tvNewsContent);
        tvNewsContent.setMovementMethod(ScrollingMovementMethod.getInstance());
        String id = stringObjectMap.get("id") + "";
        String articleType = stringObjectMap.get("article_type") + "";
        if ("1".equals(articleType)) {
            tvTitle.setText(R.string.newsDetail);
        } else if ("2".equals(articleType)) {
            tvTitle.setText(R.string.systemDetail);
        }else{
            tvTitle.setText(R.string.announcementContent);
        }

        backImg.setOnClickListener(this);

        getNewsDetailAction(id);
    }

    private void getNewsDetailAction(String id) {
        Map<String, Object> map = new HashMap<>();
        if (UtilTools.empty(MbsConstans.ACCESS_TOKEN)) {
            MbsConstans.ACCESS_TOKEN = SPUtils.get(MbsConstans.SharedInfoConstans.ACCESS_TOKEN, "").toString();
        }
        map.put("token", MbsConstans.ACCESS_TOKEN);
        map.put("id", id);
        mRequestPresenterImp.requestPostToMap(MethodUrl.ARTICLE_GETARTICLEINFO, map);
    }

    @Override
    public void showProgress() {
        showProgressDialog();
    }

    @Override
    public void disimissProgress() {
        dismissProgressDialog();
    }

    @Override
    public void loadDataSuccess(Map<String, Object> tData, String mType) {
        dismissProgressDialog();
        Intent intent;
        switch (mType) {
            case MethodUrl.ARTICLE_GETARTICLEINFO:
                switch (tData.get("code") + "") {
                    case "1": //请求成功
                        Map<String, Object> dataMap = (Map<String, Object>) tData.get("data");
                        String articelTitle = dataMap.get("article_title") + "";
                        String articleContent = dataMap.get("article_content") + "";
                        String editTime = dataMap.get("edit_time") + "";
                        tvNewsTitle.setText(articelTitle);
                        tvNewsTime.setText(editTime);
                        tvNewsContent.setText(Html.fromHtml(articleContent,new HtmlImageGetter(DetailActivity.this, tvNewsContent), null));
                        break;
                    case "0": //请求失败
                        showToastMsg(tData.get("msg") + "");
                        break;
                    case "-1": //token过期
                        finish();
                        intent = new Intent(DetailActivity.this, LoginActivity1.class);
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
            case R.id.backImg:
                finish();
                break;
            default:
        }
    }
}

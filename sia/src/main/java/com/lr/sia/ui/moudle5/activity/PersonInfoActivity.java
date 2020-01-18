package com.lr.sia.ui.moudle5.activity;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.jaeger.library.StatusBarUtil;
import com.lr.sia.R;
import com.lr.sia.basic.BasicActivity;
import com.lr.sia.basic.MbsConstans;
import com.lr.sia.mywidget.view.LoadingWindow;
import com.lr.sia.utils.tool.JSONUtil;
import com.lr.sia.utils.tool.SPUtils;
import com.lr.sia.utils.tool.UtilTools;

import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import jp.wasabeef.glide.transformations.RoundedCornersTransformation;

public class PersonInfoActivity extends BasicActivity {
    @BindView(R.id.ivUserIcon)
    ImageView ivUserIcon;
    @BindView(R.id.tvUserName)
    TextView tvUserName;
    @BindView(R.id.tvFansNum)
    TextView tvFansNum;
    @BindView(R.id.tvFavoriteNum)
    TextView tvFavoriteNum;
    @BindView(R.id.left_back_lay)
    LinearLayout leftBackLay;
    @BindView(R.id.tvTitle)
    TextView tvTitle;

    @Override
    public void showProgress() {

    }

    @Override
    public void disimissProgress() {
        dismissProgressDialog();
    }

    @Override
    public void loadDataSuccess(Map<String, Object> tData, String mType) {
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
    public void setBarTextColor() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            StatusBarUtil.setDarkMode(this);
        } else {
            StatusBarUtil.setLightMode(this);
        }
        StatusBarUtil.setDarkMode(this);
    }

    @Override
    public int getContentView() {
        return R.layout.activity_person_info;
    }

    @Override
    public void init() {
        StatusBarUtil.setTranslucentForImageView(this, MbsConstans.ALPHA, null);
        tvTitle.setText(R.string.personInfo);
        tvFansNum.setText(R.string.defaultFan);
        mLoadingWindow = new LoadingWindow(PersonInfoActivity.this, R.style.Dialog);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (UtilTools.empty(MbsConstans.USER_MAP)) {
            String s = SPUtils.get(PersonInfoActivity.this, MbsConstans.SharedInfoConstans.LOGIN_INFO, "").toString();
            MbsConstans.USER_MAP = JSONUtil.getInstance().jsonMap(s);
        }
        Map<String, Object> data = (Map<String, Object>) MbsConstans.USER_MAP.get("data");
        String nickName = data.get("nick_name") + "";
        String avatarImage = data.get("avatar_image") + "";
        String followAmount = data.get("follow_amount") + "";
        String fansAmount = data.get("fans_amount") + "";
        tvUserName.setText(nickName);
        Glide.with(this)
                .load(avatarImage)
                .transform(new RoundedCornersTransformation(UtilTools.dip2px(PersonInfoActivity.this, 55), 2))
                .placeholder(R.drawable.head)
                .error(R.drawable.head)
                .into(ivUserIcon);
        tvFansNum.setText(getString(R.string.defaultFan).replace("0", fansAmount));
        tvFavoriteNum.setText(getString(R.string.defaultAttention).replace("0", followAmount));
    }

    @OnClick({R.id.tvFansNum, R.id.tvFavoriteNum, R.id.left_back_lay, R.id.ivUserIcon})
    public void onViewClicked(View view) {
        Intent intent;
        switch (view.getId()) {
            case R.id.left_back_lay:
                finish();
                break;
            case R.id.ivUserIcon:
                intent = new Intent(PersonInfoActivity.this, EditUserInfoActivity.class);
                startActivity(intent);
                break;
            case R.id.tvFansNum:
                intent = new Intent(PersonInfoActivity.this, FansListActivity.class);
                intent.putExtra("type", "1");
                startActivity(intent);
                break;
            case R.id.tvFavoriteNum:
                intent = new Intent(PersonInfoActivity.this, FansListActivity.class);
                intent.putExtra("type", "2");
                startActivity(intent);
                break;
            default:
        }
    }
}

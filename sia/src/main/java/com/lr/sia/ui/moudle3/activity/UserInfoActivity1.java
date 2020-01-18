package com.lr.sia.ui.moudle3.activity;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.jaeger.library.StatusBarUtil;
import com.lr.sia.R;
import com.lr.sia.api.MethodUrl;
import com.lr.sia.basic.BasicActivity;
import com.lr.sia.basic.MbsConstans;
import com.lr.sia.mvp.view.RequestView;
import com.lr.sia.mywidget.view.LoadingWindow;
import com.lr.sia.ui.moudle.activity.LoginActivity1;
import com.lr.sia.utils.imageload.GlideApp;
import com.lr.sia.utils.tool.SPUtils;
import com.lr.sia.utils.tool.UtilTools;

import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.wildfire.chat.kit.conversation.ConversationActivity;
import cn.wildfirechat.model.Conversation;
import de.hdodenhof.circleimageview.CircleImageView;

public class UserInfoActivity1 extends BasicActivity implements RequestView {
    @BindView(R.id.tvFavorite)
    TextView tvFavorite;
    @BindView(R.id.tvChat)
    TextView tvChat;
    @BindView(R.id.ivUserIcon)
    CircleImageView ivUserIcon;
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
    private LoadingWindow mLoadingWindow;
    private String userId;
    private String rcName;
    private String inviteCode;

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
        mLoadingWindow.cancleView();
        Intent intent;
        switch (mType) {
            case MethodUrl.USER_GETUSERINFO:
                switch (tData.get("code") + "") {
                    case "1": //请求成功
                        Map<String, Object> data = (Map<String, Object>) tData.get("data");
                        rcName = data.get("rc_name") + "";
                        inviteCode = data.get("invite_code") + "";
                        String rcPortrait = data.get("rc_portrait") + "";
                        String followAmount = data.get("follow_amount") + "";
                        String fansAmount = data.get("fans_amount") + "";
                        String isFollow = data.get("is_follow") + "";

                        if ("0".equals(isFollow)) {
                            tvFavorite.setText(R.string.favorite);
                            tvFavorite.setSelected(false);
                        } else if ("1".equals(isFollow)) {
                            tvFavorite.setText(R.string.beenFavorite);
                            tvFavorite.setSelected(true);
                        }
                        tvFansNum.setText(getString(R.string.defaultFans).replace("0", fansAmount));
                        tvFavoriteNum.setText(getString(R.string.defaultFollow).replace("0", followAmount));
                        tvUserName.setText(rcName);
                        GlideApp.with(UserInfoActivity1.this)
                                .load(rcPortrait)
                                .into(ivUserIcon);
                        break;
                    case "0": //请求失败
                        showToastMsg(tData.get("msg") + "");
                        break;
                    case "-1": //token过期
                        showToastMsg(tData.get("msg") + "");
                        finish();
                        intent = new Intent(UserInfoActivity1.this, LoginActivity1.class);
                        startActivity(intent);
                        break;
                    default:
                }
                break;
            case MethodUrl.USER_ADDUSERFOLLOW:
                switch (tData.get("code") + "") {
                    case "1": //请求成功
                        tvFavorite.setText(R.string.beenFavorite);
                        tvFavorite.setSelected(true);
                        break;
                    case "0": //请求失败
                        showToastMsg(tData.get("msg") + "");
                        break;
                    case "-1": //token过期
                        showToastMsg(tData.get("msg") + "");
                        finish();
                        intent = new Intent(UserInfoActivity1.this, LoginActivity1.class);
                        startActivity(intent);
                        break;
                    default:
                }
                break;
            case MethodUrl.USER_CANCELUSERFOLLOW:
                switch (tData.get("code") + "") {
                    case "1": //请求成功
                        tvFavorite.setText(R.string.favorite);
                        tvFavorite.setSelected(false);
                        break;
                    case "0": //请求失败
                        showToastMsg(tData.get("msg") + "");
                        break;
                    case "-1": //token过期
                        showToastMsg(tData.get("msg") + "");
                        finish();
                        intent = new Intent(UserInfoActivity1.this, LoginActivity1.class);
                        startActivity(intent);
                        break;
                    default:
                }
                break;
            case MethodUrl.CHAT_QUERY_USERINFO:
                switch (tData.get("code") + "") {
                    case "1": //请求成功
                        if (!UtilTools.empty(tData.get("data") + "")) {
                            Map<String, Object> map = (Map<String, Object>) tData.get("data");
                            String id = map.get("id") + "";
                            getUserInfoAction(id);
                        }
                        break;
                    case "0": //请求失败
                        showToastMsg(tData.get("msg") + "");
                        break;
                    case "-1": //token过期
                        closeAllActivity();
                        intent = new Intent(UserInfoActivity1.this, LoginActivity1.class);
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

    private void getUserInfoAction(String userId) {
        Map<String, Object> map = new HashMap<>();
        if (UtilTools.empty(MbsConstans.ACCESS_TOKEN)) {
            MbsConstans.ACCESS_TOKEN = SPUtils.get(MbsConstans.SharedInfoConstans.ACCESS_TOKEN, "").toString();
        }
        map.put("token", MbsConstans.ACCESS_TOKEN);
        map.put("id", userId);
        mRequestPresenterImp.requestPostToMap(MethodUrl.USER_GETUSERINFO, map);
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
        return R.layout.activity_user_info1;
    }

    @Override
    public void init() {
        StatusBarUtil.setTranslucentForImageView(this, MbsConstans.ALPHA, null);
        tvTitle.setText(R.string.seal_conversation_title_defult);
        mLoadingWindow = new LoadingWindow(UserInfoActivity1.this, R.style.Dialog);
        userId = getIntent().getStringExtra("userId");
        int from = getIntent().getIntExtra("from", -1);
        if (from == 1) {
            getInfoAction();
        } else {
            getUserInfoAction(userId);
        }
    }

    private void getInfoAction() {
        Map<String, Object> map = new HashMap<>();
        if (UtilTools.empty(MbsConstans.ACCESS_TOKEN)) {
            MbsConstans.ACCESS_TOKEN = SPUtils.get(MbsConstans.SharedInfoConstans.ACCESS_TOKEN, "").toString();
        }
        map.put("token", MbsConstans.ACCESS_TOKEN);
        map.put("rc_id", userId);
        mRequestPresenterImp.requestPostToMap(MethodUrl.CHAT_QUERY_USERINFO, map);
    }

    @OnClick({R.id.tvFavorite, R.id.tvChat, R.id.left_back_lay})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.tvFavorite:
                Map<String, Object> map = new HashMap<>();
                if (UtilTools.empty(MbsConstans.ACCESS_TOKEN)) {
                    MbsConstans.ACCESS_TOKEN = SPUtils.get(MbsConstans.SharedInfoConstans.ACCESS_TOKEN, "").toString();
                }
                map.put("token", MbsConstans.ACCESS_TOKEN);
                map.put("id", userId);
                if (tvFavorite.isSelected()) {
                    // 已关注
                    mRequestPresenterImp.requestPostToMap(MethodUrl.USER_CANCELUSERFOLLOW, map);
                } else {
                    // 取消关注
                    mRequestPresenterImp.requestPostToMap(MethodUrl.USER_ADDUSERFOLLOW, map);
                }
                break;
            case R.id.tvChat:
                SPUtils.put(UserInfoActivity1.this, "friendId", userId);
                SPUtils.put(UserInfoActivity1.this, "isGroup", false);
                Intent intent = new Intent(UserInfoActivity1.this, ConversationActivity.class);
                Conversation conversation = new Conversation(Conversation.ConversationType.Single, inviteCode, 0);
                intent.putExtra("conversation", conversation);
                startActivity(intent);
                finish();
                break;
            case R.id.left_back_lay:
                finish();
                break;
            default:
        }
    }
}

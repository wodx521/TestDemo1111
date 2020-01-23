package com.lr.sia.ui.moudle.activity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.SeekBar;

import androidx.core.content.ContextCompat;
import androidx.lifecycle.ViewModelProviders;

import com.jaeger.library.StatusBarUtil;
import com.lr.sia.R;
import com.lr.sia.api.MethodUrl;
import com.lr.sia.basic.BasicActivity;
import com.lr.sia.basic.MbsConstans;
import com.lr.sia.mvp.presenter.RequestPresenterImp;
import com.lr.sia.mywidget.view.DouYuView;
import com.lr.sia.utils.tool.LogUtilDebug;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cn.wildfire.chat.kit.ChatManagerHolder;
import cn.wildfire.chat.kit.contact.model.UIUserInfo;
import cn.wildfire.chat.kit.group.GroupViewModel;
import cn.wildfire.chat.kit.third.utils.UIUtils;
import cn.wildfirechat.model.GroupInfo;
import cn.wildfirechat.model.UserInfo;
import cn.wildfirechat.remote.ChatManager;
import cn.wildfirechat.remote.GeneralCallback;
import cn.wildfirechat.remote.GetGroupsCallback;

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
    private GroupViewModel groupViewModel;
    private boolean isCreate = false;
    private boolean isAdd = false;

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
                        Map<String, Object> userInfoMap = (Map<String, Object>) tData.get("data");
                        String userId = userInfoMap.get("user") + "";
                        String imToken = userInfoMap.get("im_token") + "";
                        List<String> groupsId = (List<String>) userInfoMap.get("parent_group_ids");
                        SharedPreferences sp = getSharedPreferences("config", Context.MODE_PRIVATE);
                        sp.edit().putString("id", userId)
                                .putString("token", imToken)
                                .apply();
                        Intent intent = new Intent();
                        intent.putExtra("userId",userId);
                        intent.putExtra("imToken",imToken);
                        intent.putStringArrayListExtra("groupsId", (ArrayList<String>) groupsId);
                        showToastMsg(tData.get("msg") + "");
                        setResult(RESULT_OK,intent);
                        finish();
//                        ChatManagerHolder.gChatManager.connect(userId, imToken);
//                        groupViewModel = ViewModelProviders.of(VerifyRobotActivity.this).get(GroupViewModel.class);
//                        ChatManager.Instance().getMyGroups(new GetGroupsCallback() {
//                            @Override
//                            public void onSuccess(List<GroupInfo> groupInfos) {
//                                GroupInfo groupInfo1 = new GroupInfo();
//                                groupInfo1.owner = userId;
//                                if (groupInfos == null || groupInfos.isEmpty()) {
//                                    // 如果群组为空创建群组
//                                    UserInfo userInfo = ChatManager.Instance().getUserInfo(userId, false);
//                                    List<UIUserInfo> userInfos = new ArrayList<>();
//                                    userInfos.add(new UIUserInfo(userInfo));
//                                    groupViewModel.createGroup(VerifyRobotActivity.this, userInfos).observe(VerifyRobotActivity.this, result -> {
//                                        if (result.isSuccess()) {
////                                            UIUtils.showToast(UIUtils.getString(R.string.create_group_success));
//                                            LogUtilDebug.i("show", "创建群聊成功:" + result.getResult());
//                                            groupViewModel.setFavGroup(result.getResult(), true);
//                                            bindIdAction(userId, result.getResult());
//                                        }
//                                    });
//                                } else {
//                                    if (!groupInfos.contains(groupInfo1)) {
//                                        UserInfo userInfo = ChatManager.Instance().getUserInfo(userId, false);
//                                        List<UIUserInfo> userInfos = new ArrayList<>();
//                                        userInfos.add(new UIUserInfo(userInfo));
//                                        groupViewModel.createGroup(VerifyRobotActivity.this, userInfos).observe(VerifyRobotActivity.this, result -> {
//                                            if (result.isSuccess()) {
////                                            UIUtils.showToast(UIUtils.getString(R.string.create_group_success));
//                                                LogUtilDebug.i("show", "创建群聊成功:" + result.getResult());
//                                                groupViewModel.setFavGroup(result.getResult(), true);
//                                                bindIdAction(userId, result.getResult());
//                                            }
//                                        });
//                                    }
//                                }
//                            }
//
//                            @Override
//                            public void onFail(int errorCode) {
//                            }
//                        });
//                        List<String> memberIds = new ArrayList<>();
//                        memberIds.add(userId);
//                        if (groupsId != null && groupsId.size() > 0) {
//                            for (String group : groupsId) {
//                                ChatManager.Instance().addGroupMembers(group, memberIds, Arrays.asList(0), null, new GeneralCallback() {
//                                    @Override
//                                    public void onSuccess() {
//                                        groupViewModel.setFavGroup(group, true);
//                                        Log.e("add", "加群成功");
//                                        showToastMsg(tData.get("msg") + "");
//                                        setResult(RESULT_OK);
//                                        finish();
//                                    }
//
//                                    @Override
//                                    public void onFail(int errorCode) {
//                                        Log.e("add", "加群失败");
//
//                                    }
//                                });
//
//                            }
//                            showToastMsg(tData.get("msg") + "");
//                                        setResult(RESULT_OK);
//                                        finish();
//                        } else {
//                            showToastMsg(tData.get("msg") + "");
//                            setResult(RESULT_OK);
//                            finish();
//                        }

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

    private void bindIdAction(String userId, String groupId) {
        mRequestPresenterImp = new RequestPresenterImp(this, VerifyRobotActivity.this);
        Map<String, Object> bindParams = new HashMap<>();
        bindParams.put("user_im_code", userId);
        bindParams.put("group_im_code", groupId);
        mRequestPresenterImp.requestPostToMap(MethodUrl.USER_SETIMGROUPID, bindParams);
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

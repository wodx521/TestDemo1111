package com.lr.sia.ui.moudle5.activity;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.jaeger.library.StatusBarUtil;
import com.lr.sia.R;
import com.lr.sia.api.MethodUrl;
import com.lr.sia.basic.BasicActivity;
import com.lr.sia.basic.BasicApplication;
import com.lr.sia.basic.MbsConstans;
import com.lr.sia.ui.moudle.activity.LoginActivity1;
import com.lr.sia.ui.moudle5.adapter.ChatLogAdapter;
import com.lr.sia.utils.RealmUtils;
import com.lr.sia.utils.tool.SPUtils;
import com.lr.sia.utils.tool.UtilTools;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ConnectServiceActivity extends BasicActivity implements View.OnClickListener {
    private ConstraintLayout constraintLayout2;
    private ImageView backImg, ivChooseImage, ivSend;
    private SmartRefreshLayout srlRefresh1;
    private RecyclerView rvChatList;
    private EditText etContent;
    private int page = 1;
    private ChatLogAdapter chatLogAdapter;
    private Handler handler = new Handler();
    private Runnable runnable = new Runnable() {
        @Override
        public void run() {
            getChatListAction();
            handler.postDelayed(this, 5000);
        }
    };

    @Override
    public int getContentView() {
        return R.layout.activity_service;
    }

    @Override
    public void init() {
        RealmUtils.getRealm();
        StatusBarUtil.setColorForSwipeBack(this, ContextCompat.getColor(this, MbsConstans.TOP_BAR_COLOR), MbsConstans.ALPHA);

        backImg = findViewById(R.id.back_img);
        srlRefresh1 = findViewById(R.id.srlRefresh1);
        rvChatList = findViewById(R.id.rvChatList);
        constraintLayout2 = findViewById(R.id.constraintLayout2);
        etContent = findViewById(R.id.etContent);
        ivChooseImage = findViewById(R.id.ivChooseImage);
        ivSend = findViewById(R.id.ivSend);
        ivChooseImage.setOnClickListener(this);
        backImg.setOnClickListener(this);
        ivSend.setOnClickListener(this);

        chatLogAdapter = new ChatLogAdapter(ConnectServiceActivity.this);
        rvChatList.setAdapter(chatLogAdapter);
        chatLogAdapter.setOnItemClickListener(new ChatLogAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                List<Map<String, Object>> list = chatLogAdapter.getList();
                if (list.size() > 0) {
                    Map<String, Object> stringObjectMap = list.get(position);
                    String content = stringObjectMap.get("content") + "";
                    ClipboardManager cm = (ClipboardManager) BasicApplication.getContext().getSystemService(Context.CLIPBOARD_SERVICE);
                    if (!TextUtils.isEmpty(content)) {
                        // 创建普通字符型ClipData
                        ClipData mClipData = ClipData.newPlainText("Label", content);
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
            }
        });
    }

    @Override
    protected void onPause() {
        super.onPause();
        handler.removeCallbacks(runnable);
    }

    @Override
    protected void onResume() {
        super.onResume();
        handler.post(runnable);
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
            case MethodUrl.CHAT_SERVICE_ADDCHAT:// 发送消息
                switch (tData.get("code") + "") {
                    case "1": //请求成功
                        getChatListAction();
                        etContent.setText("");
                        break;
                    case "0": //请求失败
                        showToastMsg(tData.get("msg") + "");
                        break;
                    case "-1": //token过期
                        finish();
                        intent = new Intent(ConnectServiceActivity.this, LoginActivity1.class);
                        startActivity(intent);
                        break;
                    default:
                }
                break;
            case MethodUrl.CHAT_SERVICE_GETCHATLIST:// 消息列表
                switch (tData.get("code") + "") {
                    case "1": // 请求成功
                        List<Map<String, Object>> dataList = (List<Map<String, Object>>) tData.get("data");
                        Collections.reverse(dataList);
                        chatLogAdapter.setList(dataList);
                        rvChatList.scrollToPosition(dataList.size() - 1);
//                        if (UtilTools.empty(MbsConstans.RONGYUN_MAP)) {
//                            MbsConstans.RONGYUN_MAP = (Map<String, Object>) SPUtils.get(ConnectServiceActivity.this, MbsConstans.SharedInfoConstans.RONGYUN_DATA, "");
//                        }
//                        DataCache serviceCache = RealmUtils.queryData(MbsConstans.RONGYUN_MAP.get("token") + "");
//                        if (serviceCache != null) {
//                            String resultContent = serviceCache.getResultContent();
//                            List<Map<String, Object>> historyList = JSONUtil.getInstance().jsonToList(resultContent);
//                            Map<String, Object> stringObjectMap1 = historyList.get(historyList.size() - 1);
//                            Map<String, Object> stringObjectMap2 = dataList.get(0);
//                            long id1 = Long.parseLong(stringObjectMap1.get("id") + "");
//                            long id2 = Long.parseLong(stringObjectMap2.get("id") + "");
//                            if (id2 > id1) {
//                                RealmUtils.put(MbsConstans.RONGYUN_MAP.get("token") + "", JSONUtil.getInstance().objectToJson(dataList));
//                            } else if (id2 == id1) {
//                                historyList.addAll(dataList);
//                                RealmUtils.put(MbsConstans.RONGYUN_MAP.get("token") + "", JSONUtil.getInstance().objectToJson(historyList));
//                            } else {
//                                for (Map<String, Object> stringObjectMap : dataList) {
//                                    long id = Long.parseLong(stringObjectMap.get("id") + "");
//                                    if (id > id1) {
//                                        historyList.add(stringObjectMap);
//                                    }
//                                }
//                                RealmUtils.put(MbsConstans.RONGYUN_MAP.get("token") + "", JSONUtil.getInstance().objectToJson(historyList));
//                            }
//                        } else {
//                            if (dataList != null && dataList.size() > 0) {
//                                RealmUtils.put(MbsConstans.RONGYUN_MAP.get("token") + "", JSONUtil.getInstance().objectToJson(dataList));
//                                chatLogAdapter.setList(dataList);
//                                rvChatList.scrollToPosition(dataList.size() - 1);
//                            }
//                        }
                        break;
                    case "0": // 请求失败
                        showToastMsg(tData.get("msg") + "");
                        break;
                    case "-1": // token过期
                        finish();
                        intent = new Intent(ConnectServiceActivity.this, LoginActivity1.class);
                        startActivity(intent);
                        break;
                    default:
                }
                break;
            default:
        }
    }

    private void getChatListAction() {
        Map<String, Object> map = new HashMap<>();
        if (UtilTools.empty(MbsConstans.ACCESS_TOKEN)) {
            MbsConstans.ACCESS_TOKEN = SPUtils.get(ConnectServiceActivity.this, MbsConstans.SharedInfoConstans.ACCESS_TOKEN, "").toString();
        }
        map.put("token", MbsConstans.ACCESS_TOKEN);
        map.put("page", page);
        Map<String, String> mHeaderMap = new HashMap<String, String>();
        mRequestPresenterImp.requestPostToMap(mHeaderMap, MethodUrl.CHAT_SERVICE_GETCHATLIST, map);
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
            case R.id.ivChooseImage:

                break;
            case R.id.ivSend:
                String content = etContent.getText().toString();
                if (!TextUtils.isEmpty(content)) {
                    sendMessageAction(content);
                } else {
                    showToastMsg(getString(R.string.inputContent));
                }
                break;
            default:
        }
    }

    private void sendMessageAction(String content) {
        Map<String, Object> map = new HashMap<>();
        if (UtilTools.empty(MbsConstans.ACCESS_TOKEN)) {
            MbsConstans.ACCESS_TOKEN = SPUtils.get(ConnectServiceActivity.this, MbsConstans.SharedInfoConstans.ACCESS_TOKEN, "").toString();
        }
        map.put("token", MbsConstans.ACCESS_TOKEN);
        map.put("content", content);
        Map<String, String> mHeaderMap = new HashMap<String, String>();
        mRequestPresenterImp.requestPostToMap(mHeaderMap, MethodUrl.CHAT_SERVICE_ADDCHAT, map);
    }
}

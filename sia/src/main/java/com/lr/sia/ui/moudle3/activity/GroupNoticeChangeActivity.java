package com.lr.sia.ui.moudle3.activity;

import android.content.Intent;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.core.content.ContextCompat;

import com.jaeger.library.StatusBarUtil;
import com.lr.sia.R;
import com.lr.sia.api.MethodUrl;
import com.lr.sia.basic.BasicActivity;
import com.lr.sia.basic.MbsConstans;
import com.lr.sia.ui.moudle.activity.LoginActivity1;
import com.lr.sia.utils.tool.SPUtils;
import com.lr.sia.utils.tool.UtilTools;

import java.util.HashMap;
import java.util.Map;

public class GroupNoticeChangeActivity extends BasicActivity implements View.OnClickListener {
    private ImageView backImg;
    private EditText etEditNotice;
    private TextView tvConfirm;
    private TextView tvEdit;
    private String groupId;
    private boolean isAdmin;
    @Override
    public int getContentView() {
        return R.layout.activity_group_notice_change;
    }

    @Override
    public void init() {
        groupId = getIntent().getStringExtra("DATA");
        isAdmin = getIntent().getBooleanExtra("IsAdmin", false);
        StatusBarUtil.setColorForSwipeBack(this, ContextCompat.getColor(this, MbsConstans.TOP_BAR_COLOR), MbsConstans.ALPHA);
        backImg = findViewById(R.id.back_img);
        etEditNotice = findViewById(R.id.etEditNotice);
        tvConfirm = findViewById(R.id.tvConfirm);
        tvEdit = findViewById(R.id.tvEdit);

        backImg.setOnClickListener(this);
        tvConfirm.setOnClickListener(this);
        tvEdit.setOnClickListener(this);
        Map<String, Object>   groupInfoMap = (Map<String, Object>) getIntent().getSerializableExtra("groupInfoMap");
        Map<String, Object>  mapNotice  = (Map<String, Object>) groupInfoMap.get("notice");
        String content = mapNotice.get("content") + "";
        etEditNotice.setText(content);
        etEditNotice.setEnabled(false);
        if (isAdmin) {
            tvEdit.setVisibility(View.VISIBLE);
            tvConfirm.setVisibility(View.VISIBLE);
        } else {
            tvEdit.setVisibility(View.INVISIBLE);
            tvConfirm.setVisibility(View.INVISIBLE);
        }
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
            case MethodUrl.CHAT_GROUP_EDITGROUPNOTICE:
                switch (tData.get("code") + "") {
                    case "1":
                        showToastMsg(tData.get("msg") + "");
                        etEditNotice.setEnabled(false);
                        break;
                    case "-1":
                        closeAllActivity();
                        intent = new Intent(GroupNoticeChangeActivity.this, LoginActivity1.class);
                        startActivity(intent);
                        break;
                    case "0":
                        showToastMsg(tData.get("msg") + "");
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
            case R.id.tvEdit:
                etEditNotice.setEnabled(true);
                etEditNotice.requestFocus();
                break;
            case R.id.tvConfirm:
                String notice = etEditNotice.getText().toString();
                if (!TextUtils.isEmpty(notice)) {
                    editGroupNameAction(notice);
                } else {
                    showToastMsg(R.string.inputGroupNotice);
                }
                break;
            default:
        }
    }

    private void editGroupNameAction(String name) {
        Map<String, Object> map = new HashMap<>();
        if (UtilTools.empty(MbsConstans.ACCESS_TOKEN)) {
            MbsConstans.ACCESS_TOKEN = SPUtils.get(GroupNoticeChangeActivity.this, MbsConstans.SharedInfoConstans.ACCESS_TOKEN, "").toString();
        }
        map.put("token", MbsConstans.ACCESS_TOKEN);
        map.put("id", groupId);
        map.put("content", name);
        Map<String, String> mHeaderMap = new HashMap<String, String>();
        mRequestPresenterImp.requestPostToMap(mHeaderMap, MethodUrl.CHAT_GROUP_EDITGROUPNOTICE, map);
    }
}

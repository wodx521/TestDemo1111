package com.lr.sia.ui.moudle3.activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.core.content.ContextCompat;

import com.github.jdsjlzx.interfaces.OnItemClickListener;
import com.github.jdsjlzx.recyclerview.LRecyclerView;
import com.github.jdsjlzx.recyclerview.LRecyclerViewAdapter;
import com.jaeger.library.StatusBarUtil;
import com.lr.sia.R;
import com.lr.sia.api.MethodUrl;
import com.lr.sia.basic.BasicActivity;
import com.lr.sia.basic.MbsConstans;
import com.lr.sia.listener.ReLoadingData;
import com.lr.sia.mywidget.CustomerTextWatcher;
import com.lr.sia.mywidget.view.LoadingWindow;
import com.lr.sia.mywidget.view.PageView;
import com.lr.sia.ui.moudle.activity.LoginActivity1;
import com.lr.sia.ui.moudle3.adapter.SearchListAdapter;
import com.lr.sia.utils.tool.SPUtils;
import com.lr.sia.utils.tool.UtilTools;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * 添加朋友 界面
 */
public class AddFriendActivity1 extends BasicActivity implements ReLoadingData {

    @BindView(R.id.et_search)
    EditText etSearch;
    @BindView(R.id.tvClick)
    TextView tvClick;
    @BindView(R.id.rvSearch)
    LRecyclerView rvSearch;
    @BindView(R.id.ivBack)
    ImageView ivBack;
    @BindView(R.id.pageView)
    PageView pageView;
    private String mRequestTag = "";
    private Map<String, Object> mapData;

    private String friendId = "";
    private String Id = "";
    private String groupId = "";
    private String groupName = "";

    private List<Map<String, Object>> mImageList = new ArrayList<>();
    private LoadingWindow mLoadingWindow;
    private LRecyclerViewAdapter lRecyclerViewAdapter;
    private SearchListAdapter searchListAdapter;

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
            case MethodUrl.CHAT_SELECTFRIEND:
                switch (tData.get("code") + "") {
                    case "1": //请求成功
                        List<Map<String, Object>> dataList = (List<Map<String, Object>>) tData.get("data");
                        if (dataList != null && dataList.size() > 0) {
                            searchListAdapter.setDataList(dataList);
                            pageView.showContent();
                        } else {
                            pageView.showEmpty();
                        }
                        break;
                    case "0": //请求失败
                        showToastMsg(tData.get("msg") + "");
                        break;
                    case "-1": //token过期
                        finish();
                        intent = new Intent(AddFriendActivity1.this, LoginActivity1.class);
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
        mLoadingWindow.cancleView();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // TODO: add setContentView(...) invocation
        ButterKnife.bind(this);
    }

    @Override
    public int getContentView() {
        return R.layout.activity_add_friend1;
    }


    /**
     * ---------------------------------------------------------------------以下代码申请权限---------------------------------------------
     * Request permissions.
     */

    @Override
    public void init() {
        //getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
        StatusBarUtil.setColorForSwipeBack(this, ContextCompat.getColor(this, MbsConstans.TOP_BAR_COLOR), MbsConstans.ALPHA);
        etSearch.addTextChangedListener(new CustomerTextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String content = s.toString().trim();
                if (!TextUtils.isEmpty(content)) {
                    tvClick.setText(R.string.confirm);
                } else {
                    tvClick.setText(R.string.cancel);
                }
            }
        });
        pageView.setContentView(rvSearch);
        pageView.setReLoadingData(this);
        pageView.showEmpty();
        searchListAdapter = new SearchListAdapter(AddFriendActivity1.this);
        lRecyclerViewAdapter = new LRecyclerViewAdapter(searchListAdapter);
        rvSearch.setAdapter(lRecyclerViewAdapter);
        rvSearch.setPullRefreshEnabled(false);
        rvSearch.setLoadMoreEnabled(false);
        mLoadingWindow = new LoadingWindow(AddFriendActivity1.this, R.style.Dialog);
        lRecyclerViewAdapter.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                List<Map<String, Object>> dataList = searchListAdapter.getDataList();
                if (dataList.size() > 0) {
                    Map<String, Object> stringObjectMap = dataList.get(position);
                    String name = stringObjectMap.get("name") + "";
                    String portrait = stringObjectMap.get("portrait") + "";
                    // 1 用户 2 群组
                    String type = stringObjectMap.get("type") + "";
                    // 0 未关注/未入群  1 已关注/已入群
                    String isFollow = stringObjectMap.get("is_follow") + "";
                    Intent intent = new Intent();
                    intent.putExtra("userId", stringObjectMap.get("id") + "");
                    if ("1".equals(type)) {
                        // 1 用户
                        intent.setClass(AddFriendActivity1.this, UserInfoActivity1.class);
                    } else {
                        intent.setClass(AddFriendActivity1.this, GroupTeamActivity.class);
                    }
                    startActivity(intent);
                }
            }
        });
    }

    /**
     * activity销毁前触发的方法
     */
    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);
    }

    /**
     * activity恢复时触发的方法
     */
    @Override
    public void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);

    }

    @OnClick({R.id.tvClick, R.id.ivBack})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.tvClick:
                String content = etSearch.getText().toString().trim();
                if (!TextUtils.isEmpty(content)) {
                    // 输入框内容不为空,点击搜索
                    mLoadingWindow.showView();
                    searchAction(content);
                } else {
                    // 输入框内容为空点击退出
                    finish();
                }
                break;
            case R.id.ivBack:
                finish();
                break;
            default:
        }
    }

    private void searchAction(String content) {
        Map<String, Object> map = new HashMap<>();
        if (UtilTools.empty(MbsConstans.ACCESS_TOKEN)) {
            MbsConstans.ACCESS_TOKEN = SPUtils.get(MbsConstans.SharedInfoConstans.ACCESS_TOKEN, "").toString();
        }
        map.put("token", MbsConstans.ACCESS_TOKEN);
        map.put("code", content);
        mRequestPresenterImp.requestPostToMap(MethodUrl.CHAT_SELECTFRIEND, map);
    }

    @Override
    public void reLoadingData() {
        String content = etSearch.getText().toString().trim();
        searchAction(content);
    }
}

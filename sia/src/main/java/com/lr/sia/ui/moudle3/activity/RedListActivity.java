package com.lr.sia.ui.moudle3.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.github.jdsjlzx.recyclerview.LRecyclerView;
import com.github.jdsjlzx.recyclerview.LRecyclerViewAdapter;
import com.github.jdsjlzx.recyclerview.ProgressStyle;
import com.jaeger.library.StatusBarUtil;
import com.lr.sia.R;
import com.lr.sia.api.MethodUrl;
import com.lr.sia.basic.BasicActivity;
import com.lr.sia.basic.MbsConstans;
import com.lr.sia.listener.ReLoadingData;
import com.lr.sia.listener.SelectBackListener;
import com.lr.sia.mvp.view.RequestView;
import com.lr.sia.mywidget.view.PageView;
import com.lr.sia.ui.moudle.activity.LoginActivity1;
import com.lr.sia.ui.moudle3.adapter.RedListAdapter;
import com.lr.sia.ui.moudle5.activity.RedLogActivity;
import com.lr.sia.utils.imageload.GlideUtils;
import com.lr.sia.utils.tool.SPUtils;
import com.lr.sia.utils.tool.UtilTools;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * 红包领取
 */
public class RedListActivity extends BasicActivity implements RequestView, ReLoadingData, SelectBackListener {
    @BindView(R.id.back_iv)
    ImageView backIv;
    @BindView(R.id.record_iv)
    TextView recordIv;
    @BindView(R.id.head_iv)
    ImageView headIv;
    @BindView(R.id.name_tv)
    TextView nameTv;
    @BindView(R.id.money_tv)
    TextView moneyTv;
    @BindView(R.id.tvRedName)
    TextView tvRedName;
    @BindView(R.id.tip_tv)
    TextView tipTv;
    @BindView(R.id.refresh_list_view)
    LRecyclerView refreshListView;
    @BindView(R.id.content)
    LinearLayout content;
    @BindView(R.id.page_view)
    PageView pageView;
    @BindView(R.id.total_tv)
    TextView totalTv;
    @BindView(R.id.divide_line)
    View divideLine;

    private LRecyclerViewAdapter mLRecyclerViewAdapter1 = null;
    private RedListAdapter mListAdapter;
    private List<Map<String, Object>> mDataList = new ArrayList<>();

    private String type = "";

    @Override
    public void setBarTextColor() {
        StatusBarUtil.setDarkMode(this);
    }

    @Override
    public int getContentView() {
        return R.layout.activity_red_list;
    }

    @Override
    public void init() {
        StatusBarUtil.setColorForSwipeBack(this, ContextCompat.getColor(this, R.color.red_package), MbsConstans.ALPHA);
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            String id = bundle.getString("id");
            type = bundle.getString("type");
            if (!UtilTools.empty(type)) {
                if ("1".equals(type)) {
                    getRedStatusAction(id);
                } else {
                    getTransferStatusAction(id);
                }
            }
        }
        initView();
    }

    private void initView() {
        pageView.setContentView(content);
        pageView.setReLoadingData(this);
        pageView.showLoading();
        LinearLayoutManager manager = new LinearLayoutManager(RedListActivity.this);
        manager.setOrientation(RecyclerView.VERTICAL);
        refreshListView.setLayoutManager(manager);
    }

    private void getRedStatusAction(String id) {
        Map<String, Object> map = new HashMap<>();
        if (UtilTools.empty(MbsConstans.ACCESS_TOKEN)) {
            MbsConstans.ACCESS_TOKEN = SPUtils.get(RedListActivity.this, MbsConstans.SharedInfoConstans.ACCESS_TOKEN, "").toString();
        }
        map.put("token", MbsConstans.ACCESS_TOKEN);
        map.put("id", id + "");
        Map<String, String> mHeaderMap = new HashMap<String, String>();
        mRequestPresenterImp.requestPostToMap(mHeaderMap, MethodUrl.CHAT_RED_GETHONGBAORECEIVEINFO, map);
    }

    private void getTransferStatusAction(String id) {
        Map<String, Object> map = new HashMap<>();
        if (UtilTools.empty(MbsConstans.ACCESS_TOKEN)) {
            MbsConstans.ACCESS_TOKEN = SPUtils.get(RedListActivity.this, MbsConstans.SharedInfoConstans.ACCESS_TOKEN, "").toString();
        }
        map.put("token", MbsConstans.ACCESS_TOKEN);
        map.put("id", id + "");
        Map<String, String> mHeaderMap = new HashMap<String, String>();
        mRequestPresenterImp.requestPostToMap(mHeaderMap, MethodUrl.CHAT_ZHUANZHANG_INFO, map);
    }


    @OnClick({R.id.back_iv, R.id.record_iv})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.back_iv:
                finish();
                break;
            case R.id.record_iv:
                Intent intent = new Intent(RedListActivity.this, RedLogActivity.class);
                intent.putExtra("logType", "1");
                startActivity(intent);
                break;
            default:
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
            case MethodUrl.CHAT_RED_GETHONGBAORECEIVEINFO:
                switch (tData.get("code") + "") {
                    case "1": //请求成功
                        Map<String, Object> mapData = (Map<String, Object>) tData.get("data");
                        String receiveMoney = mapData.get("my_receive") + "";
                        String remark = mapData.get("remark") + "";
                        String number = mapData.get("number") + "";
                        String allNumber = mapData.get("all_number") + "";
                        String sendUser = mapData.get("send_user") + "";
                        if (number.equals("0")) {
                            totalTv.setText(getString(R.string.remainingRed1).replace("%s", allNumber));
                        } else {
                            totalTv.setText(getString(R.string.remainingRed).replace("%s", allNumber).replace("%w", number));
                        }
                        tvRedName.setText(getString(R.string.reRed).replace("%s", sendUser));
                        tipTv.setText(remark);

                        if (UtilTools.empty(mapData.get("receive_log") + "")) {
//                            totalTv.setVisibility(View.GONE);
                            pageView.setVisibility(View.GONE);
                        } else {
//                            totalTv.setVisibility(View.VISIBLE);
                            pageView.setVisibility(View.VISIBLE);
                            mDataList = (List<Map<String, Object>>) mapData.get("receive_log");
                            if (UtilTools.empty(mDataList)) {
                                pageView.showEmpty();
                            } else {
//                                totalTv.setText(mDataList.size() + "个红包");
                                pageView.showContent();
                                responseData();
                                refreshListView.refreshComplete(10);
                            }
                        }
                        break;
                    case "0": //请求失败
                        showToastMsg(tData.get("msg") + "");
                        pageView.showNetworkError();
                        break;
                    case "-1": //token过期
                        closeAllActivity();
                        intent = new Intent(RedListActivity.this, LoginActivity1.class);
                        startActivity(intent);
                        break;
                    default:
                }
                break;
            case MethodUrl.CHAT_ZHUANZHANG_INFO:
                switch (tData.get("code") + "") {
                    case "1": //请求成功
                        Map<String, Object> mapData = (Map<String, Object>) tData.get("data");
                        Map<String, Object> mapInfo = (Map<String, Object>) mapData.get("info");
                        GlideUtils.loadImage(RedListActivity.this, mapInfo.get("portrait") + "", headIv);
                        nameTv.setText(mapInfo.get("name") + "");
                        moneyTv.setText(mapInfo.get("money") + " " + mapInfo.get("symbol"));
                        tipTv.setText(mapInfo.get("text") + "");

                        if (UtilTools.empty(mapData.get("list") + "")) {
                            totalTv.setVisibility(View.GONE);
                            pageView.setVisibility(View.GONE);
                        } else {
                            totalTv.setVisibility(View.VISIBLE);
                            pageView.setVisibility(View.VISIBLE);
                            mDataList = (List<Map<String, Object>>) mapData.get("list");
                            if (UtilTools.empty(mDataList)) {
                                pageView.showEmpty();
                            } else {
                                totalTv.setText(mDataList.size() + "个红包");
                                pageView.showContent();
                                responseData();
                                refreshListView.refreshComplete(10);
                            }
                        }

                        break;
                    case "0": //请求失败
                        showToastMsg(tData.get("msg") + "");
                        pageView.showNetworkError();
                        break;

                    case "-1": //token过期
                        closeAllActivity();
                        intent = new Intent(RedListActivity.this, LoginActivity1.class);
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
        dealFailInfo(map, mType);
    }

    private void responseData() {
        if (mListAdapter == null) {
            mListAdapter = new RedListAdapter(RedListActivity.this);
            mListAdapter.addAll(mDataList);

            /*AnimationAdapter adapter = new ScaleInAnimationAdapter(mDataAdapter);
            adapter.setFirstOnly(false);
            adapter.setDuration(500);
            adapter.setInterpolator(new OvershootInterpolator(.5f));*/

            mLRecyclerViewAdapter1 = new LRecyclerViewAdapter(mListAdapter);

//            SampleHeader headerView = new SampleHeader(BankCardActivity.this, R.layout.item_bank_bind);
//            mLRecyclerViewAdapter.addHeaderView(headerView);

            refreshListView.setAdapter(mLRecyclerViewAdapter1);
            refreshListView.setItemAnimator(new DefaultItemAnimator());
            refreshListView.setHasFixedSize(true);
            refreshListView.setNestedScrollingEnabled(false);

            refreshListView.setRefreshProgressStyle(ProgressStyle.BallSpinFadeLoader);
            refreshListView.setArrowImageView(R.drawable.ic_pulltorefresh_arrow);

            refreshListView.setPullRefreshEnabled(true);
            refreshListView.setLoadMoreEnabled(true);


        } else {
            mListAdapter.clear();
            mListAdapter.addAll(mDataList);
            refreshListView.setAdapter(mLRecyclerViewAdapter1);
        }
     /*   //设置底部加载颜色
        mRecyclerView.setFooterViewColor(R.color.colorAccent, R.color.black, android.R.color.white);

        mRecyclerView.setRefreshProgressStyle(ProgressStyle.LineScalePulseOut); //设置下拉刷新Progress的样式
        //mRecyclerView.setArrowImageView(R.drawable.iconfont_downgrey);  //设置下拉刷新箭头
        //设置头部加载颜色
        mRecyclerView.setHeaderViewColor(R.color.colorAccent, R.color.red ,android.R.color.white);
//设置底部加载颜色
        mRecyclerView.setFooterViewColor(R.color.colorAccent, R.color.red ,android.R.color.white);*/

        refreshListView.setFooterViewHint("拼命加载中", "已经全部为你呈现了", "网络不给力啊，点击再试一次吧");
        if (mDataList.size() < 10) {
            refreshListView.setNoMore(true);
        } else {
            refreshListView.setNoMore(false);
        }

        refreshListView.refreshComplete(10);
        mListAdapter.notifyDataSetChanged();
        if (mListAdapter.getDataList().size() <= 0) {
            pageView.showEmpty();
        } else {
            pageView.showContent();
        }


    }

    @Override
    public void onSelectBackListener(Map<String, Object> map, int type) {
        switch (type) {
            case 10:
                String s = (String) map.get("name"); //选择账户

                break;
            case 30: //选择币种
                String str = (String) map.get("name"); //选择币种

                break;
        }
    }

    @Override
    public void reLoadingData() {

    }
}

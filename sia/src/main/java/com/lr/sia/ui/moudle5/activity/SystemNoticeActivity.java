package com.lr.sia.ui.moudle5.activity;

import android.content.Intent;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.github.jdsjlzx.interfaces.OnItemClickListener;
import com.github.jdsjlzx.interfaces.OnLoadMoreListener;
import com.github.jdsjlzx.interfaces.OnRefreshListener;
import com.github.jdsjlzx.recyclerview.LRecyclerView;
import com.github.jdsjlzx.recyclerview.LRecyclerViewAdapter;
import com.github.jdsjlzx.recyclerview.ProgressStyle;
import com.jaeger.library.StatusBarUtil;
import com.lr.sia.R;
import com.lr.sia.api.MethodUrl;
import com.lr.sia.basic.BasicActivity;
import com.lr.sia.basic.MbsConstans;
import com.lr.sia.listener.ReLoadingData;
import com.lr.sia.mywidget.view.PageView;
import com.lr.sia.ui.moudle.activity.LoginActivity1;
import com.lr.sia.ui.moudle4.activity.DetailActivity;
import com.lr.sia.ui.moudle4.adapter.NewsListAdapter;
import com.lr.sia.utils.tool.JSONUtil;
import com.lr.sia.utils.tool.SPUtils;
import com.lr.sia.utils.tool.UtilTools;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SystemNoticeActivity extends BasicActivity implements View.OnClickListener, ReLoadingData {
    private ImageView backImg;
    private PageView pageView;
    private LinearLayout content;
    private LRecyclerView mRefreshListView;
    private String mRequestTag;
    private int mPage = 1;
    private LRecyclerViewAdapter mLRecyclerViewAdapter;
    private List<Map<String, Object>> mDataList = new ArrayList<>();
    private NewsListAdapter newsAdapter;

    @Override
    public int getContentView() {
        return R.layout.activity_system_notice;
    }

    @Override
    public void init() {
        StatusBarUtil.setColorForSwipeBack(this, ContextCompat.getColor(this, MbsConstans.TOP_BAR_COLOR), MbsConstans.ALPHA);
        backImg = findViewById(R.id.backImg);
        pageView = findViewById(R.id.page_view);
        content = findViewById(R.id.content);
        mRefreshListView = findViewById(R.id.mRefreshListView);
        pageView.setContentView(content);
        pageView.setReLoadingData(this);
        pageView.showLoading();
        pageView.showEmpty();
        backImg.setOnClickListener(this);

        LinearLayoutManager manager = new LinearLayoutManager(SystemNoticeActivity.this);
        manager.setOrientation(RecyclerView.VERTICAL);
        mRefreshListView.setLayoutManager(manager);

        mRefreshListView.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh() {
                mPage = 1;
                traderListAction();
            }
        });

        mRefreshListView.setOnLoadMoreListener(new OnLoadMoreListener() {
            @Override
            public void onLoadMore() {
                mPage += 1;
                traderListAction();
            }
        });

        newsAdapter = new NewsListAdapter(this);
        mLRecyclerViewAdapter = new LRecyclerViewAdapter(newsAdapter);
        mRefreshListView.setAdapter(mLRecyclerViewAdapter);
        mRefreshListView.setItemAnimator(new DefaultItemAnimator());
        mRefreshListView.setHasFixedSize(true);
        mRefreshListView.setNestedScrollingEnabled(false);

        mRefreshListView.setRefreshProgressStyle(ProgressStyle.BallSpinFadeLoader);
        mRefreshListView.setArrowImageView(R.drawable.ic_pulltorefresh_arrow);

        mRefreshListView.setPullRefreshEnabled(true);
        mRefreshListView.setLoadMoreEnabled(true);
        mLRecyclerViewAdapter.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                Map<String, Object> item = newsAdapter.getDataList().get(position);
                Intent intent = new Intent(SystemNoticeActivity.this, DetailActivity.class);
                intent.putExtra("newInfo", JSONUtil.getInstance().objectToJson(item));
                startActivity(intent);
            }
        });
        traderListAction();
    }

    //获取文章列表
    private void traderListAction() {
        mRequestTag = MethodUrl.ARTICLE_GETARTICLELIST;
        Map<String, Object> map = new HashMap<>();
        if (UtilTools.empty(MbsConstans.ACCESS_TOKEN)) {
            MbsConstans.ACCESS_TOKEN = SPUtils.get(MbsConstans.ACCESS_TOKEN, "").toString();
        }
        map.put("token", MbsConstans.ACCESS_TOKEN);
        map.put("type", "3");
        map.put("page", mPage + "");
        Map<String, String> mHeaderMap = new HashMap<String, String>();
        mRequestPresenterImp.requestPostToMap(mHeaderMap, MethodUrl.ARTICLE_GETARTICLELIST, map);
    }

    @Override
    public void showProgress() {

    }

    @Override
    public void disimissProgress() {

    }

    @Override
    public void loadDataSuccess(Map<String, Object> tData, String mType) {
        Intent intent = null;
        switch (mType) {
            case MethodUrl.ARTICLE_GETARTICLELIST:
                switch (tData.get("code") + "") {
                    case "1": //请求成功
                        Map<String, Object> data = (Map<String, Object>) tData.get("data");
                        List<Map<String, Object>> mapList = (List<Map<String, Object>>) data.get("data");
                        if (mapList != null && mapList.size() > 0) {
                            mDataList.clear();
                            mDataList.addAll(mapList);
                            responseData();
                        }
                        break;
                    case "0": //请求失败
                        showToastMsg(tData.get("msg") + "");
                        break;

                    case "-1": //token过期
                        finish();
                        intent = new Intent(SystemNoticeActivity.this, LoginActivity1.class);
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

    private void responseData() {
        if (mPage == 1) {
            newsAdapter.clear();
        }
        newsAdapter.addAll(mDataList);
        newsAdapter.notifyDataSetChanged();
        mLRecyclerViewAdapter.notifyDataSetChanged();//必须调用此方法
     /*   //设置底部加载颜色
        mRecyclerView.setFooterViewColor(R.color.colorAccent, R.color.black, android.R.color.white);

        mRecyclerView.setRefreshProgressStyle(ProgressStyle.LineScalePulseOut); //设置下拉刷新Progress的样式
        //mRecyclerView.setArrowImageView(R.drawable.iconfont_downgrey);  //设置下拉刷新箭头
        //设置头部加载颜色
        mRecyclerView.setHeaderViewColor(R.color.colorAccent, R.color.red ,android.R.color.white);
//设置底部加载颜色
        mRecyclerView.setFooterViewColor(R.color.colorAccent, R.color.red ,android.R.color.white);*/

        mRefreshListView.setFooterViewHint("拼命加载中", "已经全部为你呈现了", "网络不给力啊，点击再试一次吧");
        if (mDataList.size() < 10) {
            mRefreshListView.setNoMore(true);
        } else {
            mRefreshListView.setNoMore(false);
            mPage++;
        }

        mRefreshListView.refreshComplete(10);
        newsAdapter.notifyDataSetChanged();
        if (newsAdapter.getDataList().size() <= 0) {
            pageView.showEmpty();
        } else {
            pageView.showContent();
        }
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

    @Override
    public void reLoadingData() {
        traderListAction();
    }
}

package com.lr.sia.ui.moudle4.fragment;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.DividerItemDecoration;
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
import com.lr.sia.basic.BasicFragment;
import com.lr.sia.basic.MbsConstans;
import com.lr.sia.db.CustomViewsInfo;
import com.lr.sia.listener.ReLoadingData;
import com.lr.sia.mywidget.view.PageView;
import com.lr.sia.ui.moudle.activity.LoginActivity1;
import com.lr.sia.ui.moudle4.activity.ExchangeActivity;
import com.lr.sia.ui.moudle4.activity.NewsDetailActivity;
import com.lr.sia.ui.moudle4.activity.PrivatePlacementActivity;
import com.lr.sia.ui.moudle4.adapter.AppListAdapter;
import com.lr.sia.ui.moudle4.adapter.NewsListAdapter;
import com.lr.sia.ui.moudle4.adapter.NewsListAdapter1;
import com.lr.sia.utils.imageload.GlideUtils;
import com.lr.sia.utils.tool.AnimUtil;
import com.lr.sia.utils.tool.LogUtilDebug;
import com.lr.sia.utils.tool.SPUtils;
import com.lr.sia.utils.tool.UtilTools;
import com.stx.xhb.xbanner.XBanner;
import com.tencent.mm.opensdk.constants.ConstantsAPI;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ZiXunFragment extends BasicFragment implements ReLoadingData, View.OnClickListener {

    private final int QUEST_CODE = 500;
    private String mRequestTag = "";
    private List<Map<String, Object>> mDataList = new ArrayList<>();
    private List<CustomViewsInfo> localImageInfos = new ArrayList<>();
    private List<Map<String, Object>> listUp = new ArrayList<>();
    private List<Map<String, Object>> noticeList;
    private AnimUtil mAnimUtil;
    private String biType = "1";
    private int mPage = 1;
    //    private Handler handler = new Handler();
    private String selectArea = "";
    private String selectSymbol = "";
    private RadioGroup rgChoose;
    private RadioButton rbNews;
    private RadioButton rbSystem;
    private RecyclerView rvApp;
    private XBanner xBanner;
    private PageView pageView;
    private RelativeLayout content;
    private LRecyclerView mNewsListView;

    //    //HTTP请求  轮询
//    private Runnable cnyRunnable = new Runnable() {
//        @Override
//        public void run() {
//            handler.postDelayed(this, MbsConstans.SECOND_TIME_5000);
//        }
//    };
    private NewsListAdapter newsAdapter;
    private AppListAdapter appListAdapter;
    private List<Map<String, Object>> mapList;
    private ImageView ivRecruit;
    private String bottomId = "0";
    private NewsListAdapter1 newsAdapter1;
    private LRecyclerViewAdapter mLRecyclerViewAdapter;
    private List<Map<String, Object>> tempNewsList = new ArrayList<>();
    // IWXAPI 是第三方app和微信通信的openApi接口
    private IWXAPI api;
    @Override
    public int getLayoutId() {
        return R.layout.fragment_zixun;
    }

    @Override
    public void init() {
        setBarTextColor();

        getBannerInfoAction();

    }

    public void setBarTextColor() {
        StatusBarUtil.setLightMode(getActivity());
    }

    private void getBannerInfoAction() {
        mRequestTag = MethodUrl.ARTICLE_GETARTICLEBANNER;
        Map<String, Object> map = new HashMap<>();
        if (UtilTools.empty(MbsConstans.ACCESS_TOKEN)) {
            MbsConstans.ACCESS_TOKEN = SPUtils.get(getActivity(), MbsConstans.SharedInfoConstans.ACCESS_TOKEN, "").toString();
        }
        map.put("token", MbsConstans.ACCESS_TOKEN);
        Map<String, String> mHeaderMap = new HashMap<String, String>();
        mRequestPresenterImp.requestPostToMap(mHeaderMap, MethodUrl.ARTICLE_GETARTICLEBANNER, map);
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
            case MethodUrl.ARTICLE_GETARTICLEBANNER:
                switch (tData.get("code") + "") {
                    case "1": //请求成功
                        List<Map<String, Object>> bannerMapList = (List<Map<String, Object>>) tData.get("data");
                        if (bannerMapList != null && bannerMapList.size() > 0) {
                            localImageInfos.clear();
                            for (Map<String, Object> map : bannerMapList) {
                                String bannerImage = map.get("banner_image") + "";
                                String imageUrl;
                                if (bannerImage.startsWith("http://") || bannerImage.startsWith("https://")) {
                                    imageUrl = bannerImage;
                                } else {
                                    imageUrl = MbsConstans.IMAGE_URL + bannerImage;
                                }
                                CustomViewsInfo customViewsInfo = new CustomViewsInfo(imageUrl);
                                localImageInfos.add(customViewsInfo);
                            }
                            xBanner.setBannerData(localImageInfos);
                            xBanner.setOnItemClickListener(new XBanner.OnItemClickListener() {
                                @Override
                                public void onItemClick(XBanner banner, Object model, View view, int position) {
                                    Intent intent = new Intent(getActivity(), PrivatePlacementActivity.class);
                                    startActivity(intent);
                                }
                            });

                            xBanner.loadImage(new XBanner.XBannerAdapter() {
                                @Override
                                public void loadBanner(XBanner banner, Object model, View view, int position) {
                                    GlideUtils.loadRoundCircleImage(getActivity(), ((CustomViewsInfo) model).getXBannerUrl(), (ImageView) view);
                                }
                            });
                        }
                        break;
                    case "0": //请求失败
                        showToastMsg(tData.get("msg") + "");
                        break;

                    case "-1": //token过期
                        if (getActivity() != null) {
                            getActivity().finish();
                        }
                        intent = new Intent(getActivity(), LoginActivity1.class);
                        startActivity(intent);
                        break;
                    default:
                }
                break;
            case MethodUrl.ARTICLE_GETARTICLELIST:
                switch (tData.get("code") + "") {
                    case "1": //请求成功
                        Map<String, Object> data = (Map<String, Object>) tData.get("data");
                        List<Map<String, Object>> mapList = (List<Map<String, Object>>) data.get("data");
                        if (mapList != null && mapList.size() > 0) {
                            mDataList.clear();
                            mDataList.addAll(mapList);
                            responseData();
                        } else {
                            pageView.showEmpty();
                        }
                        break;
                    case "0": //请求失败
                        showToastMsg(tData.get("msg") + "");
                        break;
                    case "-1": //token过期
                        if (getActivity() != null) {
                            getActivity().finish();
                        }
                        intent = new Intent(getActivity(), LoginActivity1.class);
                        startActivity(intent);
                        break;
                    default:
                }
                break;
            case MethodUrl.NEWS_INFO:
                mNewsListView.refreshComplete(20);
                if (!UtilTools.empty(tData)) {
                    bottomId = tData.get("bottom_id") + "";
                    List<Map<String, Object>> mapList = (List<Map<String, Object>>) tData.get("list");
                    tempNewsList.addAll(mapList);
                    if (tempNewsList != null && tempNewsList.size() > 0) {
                        newsAdapter1.setDataList(tempNewsList);
                        pageView.showContent();
                    } else {
                        pageView.showEmpty();
                    }
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

        mNewsListView.setFooterViewHint("拼命加载中", "已经全部为你呈现了", "网络不给力啊，点击再试一次吧");
        if (mDataList.size() < 10) {
            mNewsListView.setNoMore(true);
        } else {
            mNewsListView.setNoMore(false);
            mPage++;
        }

        mNewsListView.refreshComplete(10);
        newsAdapter.notifyDataSetChanged();
        if (newsAdapter.getDataList().size() <= 0) {
            pageView.showEmpty();
        } else {
            pageView.showContent();
        }
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if (hidden) {
//            if (handler != null && cnyRunnable != null) {
//                handler.removeCallbacks(cnyRunnable);
//            }
            setUserVisibleHint(false);
            LogUtilDebug.i("show", "onHiddenChanged()*******HomeFragment不可见");
        } else {
//            if (handler != null && cnyRunnable != null) {
//                handler.post(cnyRunnable);
//            }
            getNewsInfoAction();
            setUserVisibleHint(true);
            LogUtilDebug.i("show", "onHiddenChanged()*******HomeFragment可见");
        }
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        rgChoose = view.findViewById(R.id.rgChoose);
        rbNews = view.findViewById(R.id.rbNews);
        rbSystem = view.findViewById(R.id.rbSystem);
        xBanner = view.findViewById(R.id.xBanner);
        pageView = view.findViewById(R.id.page_view);
        content = view.findViewById(R.id.content);
        ivRecruit = view.findViewById(R.id.ivRecruit);
        rvApp = view.findViewById(R.id.rvApp);
        mNewsListView = view.findViewById(R.id.mNewsListView);

        rbNews.setOnClickListener(this);
        rbSystem.setOnClickListener(this);
        ivRecruit.setOnClickListener(this);
        pageView.setContentView(content);
        pageView.setReLoadingData(this);
        pageView.showLoading();
        pageView.showEmpty();
        LinearLayoutManager manager = new LinearLayoutManager(getActivity());
        manager.setOrientation(RecyclerView.VERTICAL);
        mNewsListView.setLayoutManager(manager);
        mNewsListView.addItemDecoration(new DividerItemDecoration(getActivity(),DividerItemDecoration.VERTICAL));
        newsAdapter1 = new NewsListAdapter1(getActivity());
        mNewsListView.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh() {
                bottomId = "0";
                tempNewsList.clear();
                getNewsInfoAction();
            }
        });

        mNewsListView.setOnLoadMoreListener(new OnLoadMoreListener() {
            @Override
            public void onLoadMore() {
                getNewsInfoAction();
            }
        });

        newsAdapter = new NewsListAdapter(getActivity());
        appListAdapter = new AppListAdapter(getActivity());
        mLRecyclerViewAdapter = new LRecyclerViewAdapter(newsAdapter1);
        mapList = new ArrayList<>();
        for (int i = 0; i < 2; i++) {
            HashMap<String, Object> stringObjectHashMap = new HashMap<>();
            if (i == 0) {
                stringObjectHashMap.put("name", getString(R.string.market));
                stringObjectHashMap.put("icon", R.drawable.icon_market);
            } else {
                stringObjectHashMap.put("name", getString(R.string.game));
                stringObjectHashMap.put("icon", R.drawable.icon_game);
            }
            mapList.add(stringObjectHashMap);
        }

        rgChoose.check(R.id.rbNews);
        getNewsInfoAction();
//        getBannerInfoAction();
//        traderListAction();
        rbNews.performClick();
        mNewsListView.setAdapter(mLRecyclerViewAdapter);

        mNewsListView.setItemAnimator(new DefaultItemAnimator());
        mNewsListView.setHasFixedSize(true);
        mNewsListView.setNestedScrollingEnabled(false);
        mNewsListView.setRefreshProgressStyle(ProgressStyle.BallSpinFadeLoader);
        mNewsListView.setArrowImageView(R.drawable.ic_pulltorefresh_arrow);

        rvApp.setAdapter(appListAdapter);

        mNewsListView.setPullRefreshEnabled(true);
        mNewsListView.setLoadMoreEnabled(true);
        mLRecyclerViewAdapter.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                Map<String, Object> item = newsAdapter1.getDataList().get(position);
                Map<String, Object> extra = (Map<String, Object>) item.get("extra");
                String topicUrl = extra.get("topic_url") + "";
                Intent intent = new Intent(getActivity(), NewsDetailActivity.class);
                intent.putExtra("url", topicUrl);
                startActivity(intent);
            }
        });
        appListAdapter.setClickListener(new AppListAdapter.ClickListener() {
            @Override
            public void onItemClickListener(View view, int position) {
                switch (position) {
                    case 0:
                        Intent intent = new Intent(getActivity(), ExchangeActivity.class);
                        startActivity(intent);
                        break;
                    case 1:
                        showToastMsg(getString(R.string.stayTuned));
                        break;
                    default:
                }
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        if (getUserVisibleHint()) {
//            if (handler != null && cnyRunnable != null) {
//                handler.post(cnyRunnable);
//                LogUtilDebug.i("show", "&&&&&&&&ZiXunFragment visible");
//            } else {
//                LogUtilDebug.i("show", "&&&&&&&&ZiXunFragment gone");
//            }
            getNewsInfoAction();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
//        if (handler != null && cnyRunnable != null) {
//            handler.removeCallbacks(cnyRunnable);
//        }
    }

    private void getNewsInfoAction() {
        mRequestTag = MethodUrl.NEWS_INFO;
        Map<String, String> mHeaderMap = new HashMap<>();
        Map<String, String> mParamMap = new HashMap<>();
//        catelogue_key=news&limit=20&information_id=s%&flag=down&version=9.9.9
        mParamMap.put("catelogue_key", "news");
        mParamMap.put("limit", "20");
        mParamMap.put("information_id", bottomId);
        mParamMap.put("flag", "down");
        mParamMap.put("version", "9.9.9");
        mRequestPresenterImp.requestGetToMap(mHeaderMap, MethodUrl.NEWS_INFO, mParamMap);
    }

    //获取文章列表
    private void traderListAction() {
        mRequestTag = MethodUrl.ARTICLE_GETARTICLELIST;
        Map<String, Object> map = new HashMap<>();
        if (UtilTools.empty(MbsConstans.ACCESS_TOKEN)) {
            MbsConstans.ACCESS_TOKEN = SPUtils.get(getActivity(), MbsConstans.ACCESS_TOKEN, "").toString();
        }
        map.put("token", MbsConstans.ACCESS_TOKEN);
        if (rbNews.isChecked()) {
            map.put("type", "1");
        } else if (rbSystem.isChecked()) {
            map.put("type", "2");
        }
        map.put("page", mPage + "");
        Map<String, String> mHeaderMap = new HashMap<String, String>();
        mRequestPresenterImp.requestPostToMap(mHeaderMap, MethodUrl.ARTICLE_GETARTICLELIST, map);
    }

    @Override
    public void reLoadingData() {
//        traderListAction();
        getNewsInfoAction();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.rbNews:
                mNewsListView.setVisibility(View.VISIBLE);
                rvApp.setVisibility(View.GONE);
                bottomId = "0";
                getNewsInfoAction();
                break;
            case R.id.rbSystem:
                mNewsListView.setVisibility(View.GONE);
                rvApp.setVisibility(View.VISIBLE);
                appListAdapter.setmDataList(mapList);
                pageView.showContent();

                break;
            case R.id.ivRecruit:
                Intent intent = new Intent(getActivity(), PrivatePlacementActivity.class);
                startActivity(intent);
                break;
            default:
        }
    }
}

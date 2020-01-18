package com.lr.sia.ui.moudle2.fragment;

import android.animation.Animator;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.github.jdsjlzx.interfaces.OnItemClickListener;
import com.github.jdsjlzx.interfaces.OnLoadMoreListener;
import com.github.jdsjlzx.interfaces.OnRefreshListener;
import com.github.jdsjlzx.recyclerview.LRecyclerView;
import com.github.jdsjlzx.recyclerview.LRecyclerViewAdapter;
import com.github.jdsjlzx.recyclerview.ProgressStyle;
import com.google.gson.internal.LinkedTreeMap;
import com.jaeger.library.StatusBarUtil;
import com.lr.sia.R;
import com.lr.sia.api.MethodUrl;
import com.lr.sia.basic.BasicFragment;
import com.lr.sia.basic.MbsConstans;
import com.lr.sia.db.CustomViewsInfo;
import com.lr.sia.listener.ReLoadingData;
import com.lr.sia.mywidget.view.PageView;
import com.lr.sia.ui.moudle.activity.LoginActivity1;
import com.lr.sia.ui.moudle2.activity.MarketDetailActivity;
import com.lr.sia.ui.moudle2.adapter.HangqingListAdapter;
import com.lr.sia.utils.tool.AnimUtil;
import com.lr.sia.utils.tool.LogUtilDebug;
import com.lr.sia.utils.tool.SPUtils;
import com.lr.sia.utils.tool.UtilTools;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;

import static android.app.Activity.RESULT_OK;

public class HangqingFragment1 extends BasicFragment implements ReLoadingData {
    private final int QUEST_CODE = 500;
    @BindView(R.id.left_back_lay)
    LinearLayout mLeftBackLay;
    @BindView(R.id.title_text)
    TextView mTitleText;
    @BindView(R.id.back_img)
    ImageView backImg;
    @BindView(R.id.back_text)
    TextView backText;
    @BindView(R.id.spinner)
    Spinner spinner;
    @BindView(R.id.content)
    LinearLayout mContent;
    @BindView(R.id.page_view)
    PageView mPageView;
    @BindView(R.id.mRefreshListView)
    LRecyclerView mRefreshListView;
    @BindView(R.id.top_layout)
    RelativeLayout topLayout;
    private String mRequestTag = "";
    private HangqingListAdapter mListAdapter;
    private LRecyclerViewAdapter mLRecyclerViewAdapter = null;
    private List<Map<String, Object>> mDataList = new ArrayList<>();
    private List<CustomViewsInfo> localImageInfos = new ArrayList<>();
    private List<Map<String, Object>> listUp = new ArrayList<>();
    private List<Map<String, Object>> noticeList;
    private AnimUtil mAnimUtil;
    private String biType = "1";
    private int mPage = 1;
    private Handler handler = new Handler();
    private String selectArea = "";
    private String selectSymbol = "";
    private View popView;
    private PopupWindow mConditionDialog;
    private boolean bright = false;
    private TextView tvPhone;
    private TextView tvUID;

    private ArrayAdapter<String> spinnerAdapter;
    private List<Map<String, Object>> mapDataList;
    //HTTP请求  轮询
    private Runnable cnyRunnable = new Runnable() {
        @Override
        public void run() {
            //获取实时虚拟货币数据
            traderListAction();
            handler.postDelayed(this, MbsConstans.SECOND_TIME_5000);
        }
    };

    @Override
    public int getLayoutId() {
        return R.layout.fragment_hangqing;
    }

    @Override
    public void init() {
      /*  LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, (int) getActivity().getResources().getDimension(R.dimen.title_item_height) + UtilTools.getStatusHeight2(getActivity()));
        layoutParams.topMargin = UtilTools.dip2px(getActivity(),10);
        topLayout.setLayoutParams(layoutParams);
        topLayout.setPadding(0, UtilTools.getStatusHeight2(getActivity()), 0, 0);*/
        setBarTextColor();

        mAnimUtil = new AnimUtil();

//        Resources resources = getActivity().getResources();
//        Drawable drawable = resources.getDrawable(R.drawable.icon0_logo);
//        titleText.setCompoundDrawables(drawable,null,null,null);


        mTitleText.setText(R.string.bottom_heyue);
        mTitleText.setCompoundDrawables(null, null, null, null);

//        String[] spinnerItems = {"人民币", "美元", "韩币", "日元"};
        spinnerAdapter = new ArrayAdapter<String>(getActivity(), R.layout.spinner_item_select);
        //自定义下拉的字体样式
        spinnerAdapter.setDropDownViewResource(R.layout.spinner_item_drop);
        //这个在不同的Theme下，显示的效果是不同的
        //spinnerAdapter.setDropDownViewTheme(Theme.LIGHT);
        spinner.setAdapter(spinnerAdapter);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                TextView tv = (TextView) view;
                tv.setTextColor(Color.GRAY);
                tv.setGravity(Gravity.CENTER_HORIZONTAL);//设置居中
                traderListAction();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });


        mPageView.setContentView(mContent);
        mPageView.setReLoadingData(this);
        mPageView.showLoading();

        LinearLayoutManager manager = new LinearLayoutManager(getActivity());
        manager.setOrientation(RecyclerView.VERTICAL);
        mRefreshListView.setLayoutManager(manager);
        mListAdapter = new HangqingListAdapter(getActivity());
        mLRecyclerViewAdapter = new LRecyclerViewAdapter(mListAdapter);
        mRefreshListView.setAdapter(mLRecyclerViewAdapter);
        mRefreshListView.setItemAnimator(new DefaultItemAnimator());
        mRefreshListView.setHasFixedSize(true);
        mRefreshListView.setNestedScrollingEnabled(false);

        mRefreshListView.setRefreshProgressStyle(ProgressStyle.BallSpinFadeLoader);
        mRefreshListView.setArrowImageView(R.drawable.ic_pulltorefresh_arrow);

        mLRecyclerViewAdapter.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                if (mListAdapter.getDataList().size() > 0) {
                    Map<String, Object> item = mListAdapter.getDataList().get(position);
                    Intent intent = new Intent(getActivity(), MarketDetailActivity.class);
                    int selectedItemPosition = spinner.getSelectedItemPosition();
                    Map<String, Object> stringObjectMap = mapDataList.get(selectedItemPosition);
                    intent.putExtra("coinInfo", (LinkedTreeMap<String, Object>) item);
                    intent.putExtra("currency", (LinkedTreeMap<String, Object>) stringObjectMap);
                    startActivity(intent);
                }
            }
        });
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
                traderListAction();
            }
        });


        //获取资讯数据
        getBiInfoAction();
    }

    //获取行情列表
    private void traderListAction() {
        mRequestTag = MethodUrl.MARKET_GETMARKETCOINLIST;
        Map<String, Object> map = new HashMap<>();
        if (UtilTools.empty(MbsConstans.ACCESS_TOKEN)) {
            MbsConstans.ACCESS_TOKEN = SPUtils.get(getActivity(), MbsConstans.SharedInfoConstans.ACCESS_TOKEN, "").toString();
        }
        if (mapDataList != null && mapDataList.size() > 0) {
            map.put("token", MbsConstans.ACCESS_TOKEN);
            int selectedItemPosition = spinner.getSelectedItemPosition();
            Map<String, Object> stringObjectMap = mapDataList.get(selectedItemPosition);
            map.put("cur_unit", stringObjectMap.get("id") + "");
            Map<String, String> mHeaderMap = new HashMap<String, String>();
            mRequestPresenterImp.requestPostToMap(mHeaderMap, MethodUrl.MARKET_GETMARKETCOINLIST, map);
        }
    }

    private void getBiInfoAction() {
        mRequestTag = MethodUrl.MARKET_GETCURRENCYLIST;
        Map<String, Object> map = new HashMap<>();
        if (UtilTools.empty(MbsConstans.ACCESS_TOKEN)) {
            MbsConstans.ACCESS_TOKEN = SPUtils.get(getActivity(), MbsConstans.SharedInfoConstans.ACCESS_TOKEN, "").toString();
        }
        map.put("token", MbsConstans.ACCESS_TOKEN);
        Map<String, String> mHeaderMap = new HashMap<String, String>();
        mRequestPresenterImp.requestPostToMap(mHeaderMap, MethodUrl.MARKET_GETCURRENCYLIST, map);
    }

    public void setBarTextColor() {
        StatusBarUtil.setLightMode(getActivity());
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
            case MethodUrl.MARKET_GETMARKETCOINLIST:
                switch (tData.get("code") + "") {
                    case "1":
                        List<Map<String, Object>> list = (List<Map<String, Object>>) tData.get("data");
                        if (UtilTools.empty(list)) {
                            mPageView.showEmpty();
                        } else {
                            mPageView.showContent();
                            mDataList.clear();
                            mDataList.addAll(list);
                            responseData();
                        }
                        mRefreshListView.refreshComplete(10);
                        break;
                    case "-1":
                        if (getActivity() != null) {
                            getActivity().finish();
                        }
                        intent = new Intent(getActivity(), LoginActivity1.class);
                        startActivity(intent);
                        break;
                    case "0":
                        mPageView.showNetworkError();
                        showToastMsg(tData.get("msg") + "");
                        break;
                    default:
                }
                break;
            case MethodUrl.MARKET_GETCURRENCYLIST:
                switch (tData.get("code") + "") {
                    case "1": //请求成功
                        mapDataList = (List<Map<String, Object>>) tData.get("data");
                        if (mapDataList != null && mapDataList.size() > 0) {
                            for (int i = 0; i < mapDataList.size(); i++) {
                                Map<String, Object> stringObjectMap = mapDataList.get(i);
                                String isDefault = stringObjectMap.get("is_default") + "";
                                if ("1".equals(isDefault)) {
                                    spinner.setSelection(i);
                                }
                                spinnerAdapter.add(stringObjectMap.get("cur_name") + "");
                            }
                            traderListAction();
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
            default:
        }
    }

    private void responseData() {
        if (mListAdapter == null) {

            mListAdapter.addAll(mDataList);

            /*AnimationAdapter adapter = new ScaleInAnimationAdapter(mDataAdapter);
            adapter.setFirstOnly(false);
            adapter.setDuration(500);
            adapter.setInterpolator(new OvershootInterpolator(.5f));*/


            mRefreshListView.setPullRefreshEnabled(true);
            mRefreshListView.setLoadMoreEnabled(true);
//            SampleHeader headerView = new SampleHeader(BankCardActivity.this, R.layout.item_bank_bind);
//            mLRecyclerViewAdapter.addHeaderView(headerView);


        } else {
            if (mPage == 1) {
                mListAdapter.clear();
            }
            mListAdapter.addAll(mDataList);
            mListAdapter.notifyDataSetChanged();
            mLRecyclerViewAdapter.notifyDataSetChanged();//必须调用此方法
        }
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
        mListAdapter.notifyDataSetChanged();
        if (mListAdapter.getDataList().size() <= 0) {
            mPageView.showEmpty();
        } else {
            mPageView.showContent();
        }

    }

    @Override
    public void loadDataError(Map<String, Object> map, String mType) {
        dealFailInfo(map, mType);
    }

    private void toggleBright() {
        //三个参数分别为： 起始值 结束值 时长 那么整个动画回调过来的值就是从0.5f--1f的
        mAnimUtil.setValueAnimator(0.7f, 1f, 300);
        mAnimUtil.addUpdateListener(new AnimUtil.UpdateListener() {
            @Override
            public void progress(float progress) {
                //此处系统会根据上述三个值，计算每次回调的值是多少，我们根据这个值来改变透明度
                float bgAlpha = bright ? progress : (1.7f - progress);//三目运算，应该挺好懂的。
                //bgAlpha = progress;//三目运算，应该挺好懂的。
                bgAlpha(bgAlpha);//在此处改变背景，这样就不用通过Handler去刷新了。
            }
        });
        mAnimUtil.addEndListner(new AnimUtil.EndListener() {
            @Override
            public void endUpdate(Animator animator) {
                //在一次动画结束的时候，翻转状态
                bright = !bright;
            }
        });
        mAnimUtil.startAnimator();
    }

    private void bgAlpha(float alpha) {
        WindowManager.LayoutParams lp = getActivity().getWindow().getAttributes();
        lp.alpha = alpha;// 0.0-1.0
        getActivity().getWindow().setAttributes(lp);
    }

    private void getIsIdentityAction() {
        mRequestTag = MethodUrl.IS_IDENTITY;
        Map<String, Object> map = new HashMap<>();
        if (UtilTools.empty(MbsConstans.ACCESS_TOKEN)) {
            MbsConstans.ACCESS_TOKEN = SPUtils.get(getActivity(), MbsConstans.SharedInfoConstans.ACCESS_TOKEN, "").toString();
        }
        map.put("token", MbsConstans.ACCESS_TOKEN);
        Map<String, String> mHeaderMap = new HashMap<String, String>();
        mRequestPresenterImp.requestPostToMap(mHeaderMap, MethodUrl.IS_IDENTITY, map);

    }

    @Override
    public void reLoadingData() {

    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if (hidden) {
            if (handler != null && cnyRunnable != null) {
                handler.removeCallbacks(cnyRunnable);
            }
            setUserVisibleHint(false);
            LogUtilDebug.i("show", "onHiddenChanged()*******HomeFragment不可见");
        } else {
            if (handler != null && cnyRunnable != null) {
                handler.post(cnyRunnable);
            }
            setUserVisibleHint(true);
            LogUtilDebug.i("show", "onHiddenChanged()*******HomeFragment可见");
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == QUEST_CODE) {
                LogUtilDebug.i("show", "ZiXunFragment onActivityResult()");
                Bundle bundle = data.getExtras();
                if (bundle != null) {

                }
            }
        }
    }


    @Override
    public void onResume() {
        super.onResume();
        if (getUserVisibleHint()) {
            handler.post(cnyRunnable);
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        handler.removeCallbacks(cnyRunnable);
    }
}

package com.lr.sia.ui.moudle3.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import com.androidkun.xtablayout.XTabLayout;
import com.jaeger.library.StatusBarUtil;
import com.lr.sia.R;
import com.lr.sia.api.MethodUrl;
import com.lr.sia.basic.BasicFragment;
import com.lr.sia.basic.MbsConstans;
import com.lr.sia.ui.moudle3.ThirdFragmentFactory;
import com.lr.sia.ui.moudle3.activity.AddFriendActivity1;
import com.lr.sia.utils.tool.JSONUtil;
import com.lr.sia.utils.tool.LogUtilDebug;
import com.lr.sia.utils.tool.SPUtils;
import com.lr.sia.utils.tool.UtilTools;

import java.util.List;
import java.util.Map;

import cn.wildfire.chat.kit.ChatManagerHolder;
import cn.wildfire.chat.kit.IMConnectionStatusViewModel;
import cn.wildfire.chat.kit.IMServiceStatusViewModel;
import cn.wildfirechat.client.ConnectionStatus;
import cn.wildfirechat.model.GroupInfo;
import cn.wildfirechat.model.GroupSearchResult;
import cn.wildfirechat.remote.ChatManager;

public class ChatViewFragment1 extends BasicFragment {
    private ImageView rightImg;
    private EditText etSearch;
    private XTabLayout tabLayout;
    private FrameLayout fragmentContainer;
    private LinearLayout rightLay;
    private String mRequestTag;
    private List<Map<String, Object>> mFriendList;
    private boolean isInitialized = false;
    private Observer<Boolean> imStatusLiveDataObserver = status -> {
        if (status && !isInitialized) {
            //init();
            isInitialized = true;
        }
    };

    @Override
    public int getLayoutId() {
        return R.layout.fragment_chat_view1;
    }

    @Override
    public void init() {
        setBarTextColor();
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
        Intent intent;
        switch (mType) {
            case MethodUrl.CHAT_MY_FRIENDS:

                break;
            case MethodUrl.CHAT_MY_GROUPS:

                break;
            default:
        }
    }

    @Override
    public void loadDataError(Map<String, Object> map, String mType) {

    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if (!hidden) {
            isConnncted();
        }
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        rightImg = view.findViewById(R.id.rightImg);
        etSearch = view.findViewById(R.id.etSearch);
        tabLayout = view.findViewById(R.id.tab_layout);
        rightLay = view.findViewById(R.id.right_lay);
        fragmentContainer = view.findViewById(R.id.fragment_container);

        tabLayout.addTab(tabLayout.newTab().setText(R.string.lastChat));
        tabLayout.addTab(tabLayout.newTab().setText(R.string.myAttention));
        tabLayout.addTab(tabLayout.newTab().setText(R.string.myGroup));

        tabLayout.addOnTabSelectedListener(new XTabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(XTabLayout.Tab tab) {
                switch (tab.getPosition()) {
                    case 0:
                        // 近期聊天
                        addFragment(0, tab.getText().toString());
                        break;
                    case 1:
                        addFragment(1, tab.getText().toString());
                        break;
                    case 2:
                        addFragment(2, tab.getText().toString());
                        break;
                    default:
                }
            }

            @Override
            public void onTabUnselected(XTabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(XTabLayout.Tab tab) {
                switch (tab.getPosition()) {
                    case 0:
                        // 近期聊天
                        addFragment(0, tab.getText().toString());
                        break;
                    case 1:
                        addFragment(1, tab.getText().toString());
                        break;
                    case 2:
                        addFragment(2, tab.getText().toString());
                        break;
                    default:
                }
            }
        });
        tabLayout.getTabAt(tabLayout.getSelectedTabPosition()).select();
        rightLay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), AddFriendActivity1.class);
                startActivity(intent);
            }
        });

        etSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

    }

    protected void addFragment(int position, String title) {
        FragmentTransaction fragmentTransaction = getChildFragmentManager().beginTransaction();
        Fragment fragmentByTag = getChildFragmentManager().findFragmentByTag(title);
        List<Fragment> fragments = getChildFragmentManager().getFragments();
        for (Fragment fragment : fragments) {
            fragment.setUserVisibleHint(false);
            fragmentTransaction.hide(fragment);
        }
        if (fragmentByTag != null) {
            fragmentByTag.setUserVisibleHint(true);
            fragmentTransaction.show(fragmentByTag);
        } else {
            Fragment fragment = ThirdFragmentFactory.getFragment(position);
            fragmentTransaction.add(R.id.fragment_container, fragment, title);
            fragment.setUserVisibleHint(true);
            fragmentTransaction.show(fragment);
        }
        fragmentTransaction.commitAllowingStateLoss();
    }

    @Override
    public void onResume() {
        super.onResume();
        isConnncted();
    }

    private void isConnncted() {
        IMServiceStatusViewModel imServiceStatusViewModel = ViewModelProviders.of(this).get(IMServiceStatusViewModel.class);
        imServiceStatusViewModel.imServiceStatusLiveData().observe(this, imStatusLiveDataObserver);
        IMConnectionStatusViewModel connectionStatusViewModel = ViewModelProviders.of(this).get(IMConnectionStatusViewModel.class);
        connectionStatusViewModel.connectionStatusLiveData().observe(this, status -> {
            Log.e("tag", status + "");
            if (status == ConnectionStatus.ConnectionStatusTokenIncorrect ||
                    status == ConnectionStatus.ConnectionStatusSecretKeyMismatch ||
                    status == ConnectionStatus.ConnectionStatusRejected ||
                    status == ConnectionStatus.ConnectionStatusLogout ||
                    status == ConnectionStatus.ConnectionStatusUnconnected) {
                LogUtilDebug.i("show", "重新连接聊天服务器");
                ChatManager.Instance().disconnect(true);
                //重新连接登录
                if (UtilTools.empty(MbsConstans.RONGYUN_MAP)) {
                    String s = SPUtils.get(getActivity(), MbsConstans.SharedInfoConstans.RONGYUN_DATA, "").toString();
                    MbsConstans.RONGYUN_MAP = JSONUtil.getInstance().jsonMap(s);
                }
                ChatManagerHolder.gChatManager.connect(MbsConstans.RONGYUN_MAP.get("im_id") + "", MbsConstans.RONGYUN_MAP.get("im_token") + "");
            } else {
                LogUtilDebug.i("show", "已经连接聊天服务器!!!");
            }
        });
    }
}

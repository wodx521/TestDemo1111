package com.lr.sia.ui.moudle.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.PowerManager;
import android.os.Process;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProviders;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;
import com.jaeger.library.StatusBarUtil;
import com.king.zxing.Intents;
import com.lr.sia.R;
import com.lr.sia.api.MethodUrl;
import com.lr.sia.basic.BasicActivity;
import com.lr.sia.basic.MbsConstans;
import com.lr.sia.db.IndexData;
import com.lr.sia.mywidget.dialog.UpdateDialog;
import com.lr.sia.service.DownloadService;
import com.lr.sia.ui.moudle1.fragment.HomeFragment1;
import com.lr.sia.ui.moudle2.fragment.HangqingFragment1;
import com.lr.sia.ui.moudle3.fragment.ChatViewFragment1;
import com.lr.sia.ui.moudle4.fragment.ZiXunFragment;
import com.lr.sia.ui.moudle5.fragment.PersonFragment;
import com.lr.sia.utils.permission.PermissionsUtils;
import com.lr.sia.utils.permission.RePermissionResultBack;
import com.lr.sia.utils.tool.LogUtilDebug;
import com.lr.sia.utils.tool.SPUtils;
import com.lr.sia.utils.tool.UtilTools;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.yanzhenjie.permission.Permission;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.OnClick;
import cn.wildfire.chat.kit.WfcScheme;
import cn.wildfire.chat.kit.contact.ContactViewModel;
import cn.wildfire.chat.kit.contact.model.UIUserInfo;
import cn.wildfire.chat.kit.conversationlist.ConversationListViewModel;
import cn.wildfire.chat.kit.conversationlist.ConversationListViewModelFactory;
import cn.wildfire.chat.kit.group.GroupInfoActivity;
import cn.wildfire.chat.kit.group.GroupViewModel;
import cn.wildfire.chat.kit.user.ChangeMyNameActivity;
import cn.wildfire.chat.kit.user.UserInfoActivity;
import cn.wildfire.chat.kit.user.UserViewModel;
import cn.wildfirechat.client.ConnectionStatus;
import cn.wildfirechat.model.GroupInfo;
import cn.wildfirechat.model.UserInfo;
import cn.wildfirechat.remote.ChatManager;
import cn.wildfirechat.remote.GetGroupsCallback;
import cn.wildfirechat.remote.OnConnectionStatusChangeListener;
import q.rorbin.badgeview.QBadgeView;

public class MainActivity extends BasicActivity {
    private static final int REQUEST_IGNORE_BATTERY_CODE = 101;
    private static final int REQUEST_CODE_SCAN_QR_CODE = 100;
    public static MainActivity mInstance;
    @BindView(R.id.btn_cart)
    ImageView btnCart;
    @BindView(R.id.btn_cart2)
    ImageView btnCart2;
    @BindView(R.id.btn_container_index)
    RelativeLayout rlay1;
    @BindView(R.id.btn_container_get)
    RelativeLayout rlay2;
    @BindView(R.id.btn_container_return)
    RelativeLayout rlay3;
    @BindView(R.id.btn_container_person)
    RelativeLayout rlay4;
    @BindView(R.id.btn_container_zichan)
    RelativeLayout rlay5;
    private TextView unreadNewLable;
    private ImageView[] mTabs;
    private TextView[] mTextViews;
    private HomeFragment1 mHomeFrament1;
    private HangqingFragment1 mHangqingFragment;
    private ChatViewFragment1 mIndexFragment;
    private ZiXunFragment mZiXunFragment;
    private PersonFragment mPersonFragment;
    private Fragment[] fragments;
    private TextView mAutoScrollTextView;
    private int index;
    private int currentTabIndex;
    private RelativeLayout mIndexBottomLay;
    private Snackbar snackbar;
    private Intent mDownIntent;
    private BroadcastReceiver mBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (action.equals(MbsConstans.BroadcastReceiverAction.USER_INFO_UPDATE)) {

            }
        }
    };
    private Handler mHandler;
    private boolean isOnKeyBacking;
    private long mExitTime;
    private Runnable onBackTimeRunnable = new Runnable() {
        @Override
        public void run() {
            isOnKeyBacking = false;
            if (snackbar != null) {
                snackbar.dismiss();
            }
        }
    };
    private UpdateDialog mUpdateDialog;
    private String mRequestTag;
    private IndexData mIndexData;
    private ConversationListViewModel conversationListViewModel;
    private ContactViewModel contactViewModel;
    private QBadgeView unreadMessageUnreadBadgeView;
    private IWXAPI api;
    private GroupViewModel groupViewModel;
    private int newsUnReadCount = 0;
    private int requestUnReadCount = 0;
    private boolean isOwner = false;
    private String id;
    private String token;

    @Override
    public int getContentView() {
        return R.layout.activity_main;
    }

    @Override
    public void init() {

        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(MbsConstans.BroadcastReceiverAction.USER_INFO_UPDATE);
        registerReceiver(mBroadcastReceiver, intentFilter);
        Intent intent = new Intent();
        intent.setAction(MbsConstans.BroadcastReceiverAction.MAIN_ACTIVITY);
        sendBroadcast(intent);
        SPUtils.put(this, MbsConstans.SharedInfoConstans.LOGIN_OUT, false);
        StatusBarUtil.setTranslucentForImageView(this, MbsConstans.ALPHA, null);
        initView();
        mDownIntent = new Intent(this, DownloadService.class);
        startService(mDownIntent);
        mIndexData = IndexData.getInstance();
        mHandler = new Handler(Looper.getMainLooper());
        mInstance = this;
        //0 红跌绿涨   1红涨绿跌
        String colorType = SPUtils.get(MainActivity.this, MbsConstans.SharedInfoConstans.COLOR_TYPE, "0").toString();
        if (colorType.equals("0")) {
            MbsConstans.COLOR_LOW = MbsConstans.COLOR_RED;
            MbsConstans.COLOR_TOP = MbsConstans.COLOR_GREEN;
        } else {
            MbsConstans.COLOR_LOW = MbsConstans.COLOR_GREEN;
            MbsConstans.COLOR_TOP = MbsConstans.COLOR_RED;
        }

        SharedPreferences sp = getSharedPreferences("config", Context.MODE_PRIVATE);
        id = sp.getString("id", "");
        token = sp.getString("token", "");
        ChatManager.Instance().connect(id, token);
        if (!ChatManager.Instance().getUserId().equals(id)) {
            ChatManager.Instance().disconnect(false);
            ChatManager.Instance().connect(id, token);
        } else {
            ChatManager.Instance().getMyGroups(new GetGroupsCallback() {
                @Override
                public void onSuccess(List<GroupInfo> groupInfos) {
                    if (groupInfos != null && groupInfos.size() > 0) {
                        for (GroupInfo groupInfo : groupInfos) {
                            if (groupInfo != null && groupInfo.owner.equals(id)) {
                                isOwner = true;
                                return;
                            }
                        }
                    }
                    createGroup();
                }

                @Override
                public void onFail(int errorCode) {

                }
            });
        }

        ChatManager.Instance().addConnectionChangeListener(new OnConnectionStatusChangeListener() {
            @Override
            public void onConnectionStatusChange(int status) {
                switch (status) {
                    case ConnectionStatus.ConnectionStatusTokenIncorrect:
                    case ConnectionStatus.ConnectionStatusLogout:
                    case ConnectionStatus.ConnectionStatusUnconnected:
//                        Intent intent1 = new Intent(MainActivity.this, LoginActivity1.class);
//                        startActivity(intent1);
//                        finish();
                        break;
                    default:
                        Log.e("connectStatus", status + "");
                }
            }
        });

        //会话列表ViewModel
        ConversationListViewModel conversationListViewModel = ViewModelProviders
                .of(this, new ConversationListViewModelFactory(Arrays.asList(cn.wildfirechat.model.Conversation.ConversationType.Single, cn.wildfirechat.model.Conversation.ConversationType.Group, cn.wildfirechat.model.Conversation.ConversationType.Channel), Arrays.asList(0)))
                .get(ConversationListViewModel.class);
        conversationListViewModel.unreadCountLiveData().observe(this, unreadCount -> {
            if (unreadCount == null) {
                if (requestUnReadCount == 0) {
                    unreadNewLable.setVisibility(View.GONE);
                } else {
                    unreadNewLable.setVisibility(View.VISIBLE);
                    if (requestUnReadCount < 99) {
                        unreadNewLable.setText(requestUnReadCount + "");
                    } else {
                        unreadNewLable.setText("99+");
                    }
                }

            } else {
                newsUnReadCount = unreadCount.unread;
                if (newsUnReadCount == 0 && requestUnReadCount == 0) {
                    unreadNewLable.setVisibility(View.GONE);
                } else {
                    unreadNewLable.setVisibility(View.VISIBLE);
                    if ((newsUnReadCount + requestUnReadCount) < 100) {
                        unreadNewLable.setText(newsUnReadCount + requestUnReadCount + "");
                    } else {
                        unreadNewLable.setText("99+");
                    }
                }
            }
        });
        if (checkDisplayName()) {
            ignoreBatteryOption();
        }


    }

    private void createGroup() {
        groupViewModel = ViewModelProviders.of(MainActivity.this).get(GroupViewModel.class);
        UserInfo userInfo = ChatManager.Instance().getUserInfo(id, false);
        List<UIUserInfo> userInfos = new ArrayList<>();
        userInfos.add(new UIUserInfo(userInfo));
        groupViewModel.createGroup(MainActivity.this, userInfos).observe(MainActivity.this, result -> {
            if (result.isSuccess()) {
                LogUtilDebug.i("show", "创建群聊成功:" + result.getResult());
                groupViewModel.setFavGroup(result.getResult(), true);
                bindIdAction(id, result.getResult());
            }
        });
    }

    private void bindIdAction(String userId, String groupId) {
        Map<String, Object> bindParams = new HashMap<>();
        bindParams.put("user_im_code", userId);
        bindParams.put("group_im_code", groupId);
        mRequestPresenterImp.requestPostToMap(MethodUrl.USER_SETIMGROUPID, bindParams);
    }

    private void ignoreBatteryOption() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            try {
                Intent intent = new Intent();
                String packageName = getPackageName();
                PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
                if (!pm.isIgnoringBatteryOptimizations(packageName)) {
                    intent.setAction(Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS);
                    intent.setData(Uri.parse("package:" + packageName));
                    startActivityForResult(intent, REQUEST_IGNORE_BATTERY_CODE);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private boolean checkDisplayName() {
        UserViewModel userViewModel = ViewModelProviders.of(this).get(UserViewModel.class);
        SharedPreferences sp = getSharedPreferences("config", Context.MODE_PRIVATE);
        UserInfo userInfo = userViewModel.getUserInfo(userViewModel.getUserId(), false);
        if (userInfo != null && TextUtils.equals(userInfo.displayName, userInfo.mobile)) {
            if (!sp.getBoolean("updatedDisplayName", false)) {
                sp.edit().putBoolean("updatedDisplayName", true).apply();
                updateDisplayName();
                return false;
            }
        }
        return true;
    }

    private void updateDisplayName() {
        MaterialDialog dialog = new MaterialDialog.Builder(this)
                .content("修改个人昵称？")
                .positiveText("修改")
                .negativeText("取消")
                .onPositive(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        Intent intent = new Intent(MainActivity.this, ChangeMyNameActivity.class);
                        startActivity(intent);
                    }
                }).build();
        dialog.show();
    }

    private void initView() {
        mIndexBottomLay = findViewById(R.id.btn_container_index);
        unreadNewLable = findViewById(R.id.unread_address_number);
        mTabs = new ImageView[5];
        mTabs[0] = findViewById(R.id.btn_conversation);
        mTabs[1] = findViewById(R.id.btn_address_list);
        mTabs[2] = findViewById(R.id.btn_cart);
        mTabs[3] = findViewById(R.id.btn_setting);
        mTabs[4] = findViewById(R.id.btn_zichan);

        mTextViews = new TextView[5];
        mTextViews[0] = findViewById(R.id.one_tv);
        mTextViews[1] = findViewById(R.id.two_tv);
        mTextViews[2] = findViewById(R.id.three_tv);
        mTextViews[3] = findViewById(R.id.four_tv);
        mTextViews[4] = findViewById(R.id.five_tv);
        // select first tab
        mTabs[0].setSelected(true);
        mTextViews[0].setSelected(true);

        mHomeFrament1 = new HomeFragment1();
        mIndexFragment = new ChatViewFragment1();
        mHangqingFragment = new HangqingFragment1();
        mZiXunFragment = new ZiXunFragment();
        mPersonFragment = new PersonFragment();
        fragments = new Fragment[]{mHomeFrament1, mHangqingFragment, mIndexFragment, mZiXunFragment, mPersonFragment};
        getSupportFragmentManager().beginTransaction().add(R.id.fragment_container, mHomeFrament1)
                .show(mHomeFrament1)
                .commitAllowingStateLoss();

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(mBroadcastReceiver);
    }

    /**
     * activity销毁前触发的方法
     */
    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);
    }

    @Override
    protected void onPause() {
        super.onPause();
        mIsRefresh = false;
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    /**
     * activity恢复时触发的方法
     */
    @Override
    public void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);

    }

    /**
     * 主界面不需要支持滑动返回，重写该方法永久禁用当前界面的滑动返回功能
     *
     * @return
     */
    @Override
    public boolean isSupportSwipeBack() {
        return false;
    }

    private void showUnreadMessageBadgeView(int count) {
        unreadNewLable.setVisibility(View.VISIBLE);
        unreadNewLable.setText(count);
    }

    private void hideUnreadMessageBadgeView() {
        unreadNewLable.setVisibility(View.GONE);
        unreadNewLable.setText("");
    }

    public void hideUnreadFriendRequestBadgeView() {
        unreadNewLable.setVisibility(View.GONE);
        unreadNewLable.setText("");
    }

    private void showUnreadFriendRequestBadgeView(int count) {
        unreadNewLable.setVisibility(View.VISIBLE);
        unreadNewLable.setText(count);
    }

    /**
     * on tab clicked
     *
     * @param view
     */
    public void onTabClicked(View view) {
        switch (view.getId()) {
            case R.id.btn_container_index:
                index = 0;
                btnCart2.setSelected(false);
                break;
            case R.id.btn_container_get:
                btnCart2.setSelected(false);
                index = 1;
                break;
            case R.id.btn_container_return:
                index = 2;
                btnCart2.setSelected(true);
                break;
            case R.id.btn_container_person:
                btnCart2.setSelected(false);
                index = 3;
                break;
            case R.id.btn_container_zichan:
                btnCart2.setSelected(false);
                index = 4;
                break;
            default:
        }
        if (currentTabIndex != index) {
            FragmentTransaction trx = getSupportFragmentManager().beginTransaction();
            List<Fragment> fragmentList = getSupportFragmentManager().getFragments();
            for (Fragment fragment : fragmentList) {
                trx.hide(fragment);
            }
            if (!fragments[index].isAdded()) {
                trx.add(R.id.fragment_container, fragments[index]);
            } else {
                switch (index) {// mIndexFragment, mBorrowFragment, mRepaymentFragment,mPersonFragment
                    case 0:
                        ((HomeFragment1) fragments[index]).setBarTextColor();
                        break;
                    case 1:
                        ((HangqingFragment1) fragments[index]).setBarTextColor();
                        break;
                    case 2:
                        ((ChatViewFragment1) fragments[index]).setBarTextColor();
                        break;
                    case 3:
                        ((ZiXunFragment) fragments[index]).setBarTextColor();
                        break;
                    case 4:
                        ((PersonFragment) fragments[index]).setBarTextColor();
                        break;
                    default:
                }
            }
            trx.show(fragments[index]).commitAllowingStateLoss();
        }
        mTabs[currentTabIndex].setSelected(false);
        // set current tab selected
        mTabs[index].setSelected(true);

        mTextViews[currentTabIndex].setSelected(false);
        mTextViews[index].setSelected(true);
        currentTabIndex = index;


    }

    @OnClick({R.id.btn_cart, R.id.btn_cart2})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.btn_cart:
            case R.id.btn_cart2:
                btnCart2.setSelected(true);
                index = 2;
                if (currentTabIndex != index) {
                    FragmentTransaction trx = getSupportFragmentManager().beginTransaction();
                    List<Fragment> fragmentList = getSupportFragmentManager().getFragments();
                    for (Fragment fragment : fragmentList) {
                        trx.hide(fragment);
                    }
                    if (!fragments[index].isAdded()) {
                        trx.add(R.id.fragment_container, fragments[index]);
                    } else {
                        switch (index) {// mIndexFragment, mBorrowFragment, mRepaymentFragment,mPersonFragment
                            case 0:
                                ((HomeFragment1) fragments[index]).setBarTextColor();
                                break;
                            case 1:
                                ((HangqingFragment1) fragments[index]).setBarTextColor();
                                break;
                            case 2:
                                ((ChatViewFragment1) fragments[index]).setBarTextColor();
                                break;
                            case 3:
                                ((ZiXunFragment) fragments[index]).setBarTextColor();
                                break;
                            case 4:
                                ((PersonFragment) fragments[index]).setBarTextColor();
                                break;
                            default:
                        }
                    }
                    trx.show(fragments[index]).commitAllowingStateLoss();
                }
                mTabs[currentTabIndex].setSelected(false);
                // set current tab selected
                mTabs[index].setSelected(true);

                mTextViews[currentTabIndex].setSelected(false);
                mTextViews[index].setSelected(true);
                currentTabIndex = index;
                break;
            default:
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (currentTabIndex != 0) {
                mIndexBottomLay.performClick();
            } else {
                if (isOnKeyBacking) {
                    mHandler.removeCallbacks(onBackTimeRunnable);
                    if (snackbar != null) {
                        snackbar.dismiss();
                    }
                    closeAllActivity();
                    Process.killProcess(Process.myPid());
                    System.exit(0);
                    return true;
                } else {
                    isOnKeyBacking = true;
                    if (snackbar == null) {
                        snackbar = Snackbar.make(findViewById(R.id.fragment_container), "再次点击退出应用", Snackbar.LENGTH_SHORT);
                        snackbar.setDuration(BaseTransientBottomBar.LENGTH_INDEFINITE);
                    }
                    snackbar.show();
                    mHandler.postDelayed(onBackTimeRunnable, 2000);
                    return true;
                }
            }
            return true;
        }
        //拦截MENU按钮点击事件，让他无任何操作
        if (keyCode == KeyEvent.KEYCODE_MENU) {
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        switch (requestCode) {
            case REQUEST_CODE_SCAN_QR_CODE:
                if (resultCode == RESULT_OK) {
                    String result = data.getStringExtra(Intents.Scan.RESULT);
                    onScanPcQrCode(result);
                }
                break;
            case REQUEST_IGNORE_BATTERY_CODE:
                if (resultCode == RESULT_CANCELED) {
                    Toast.makeText(this, "允许野火IM后台运行，更能保证消息的实时性", Toast.LENGTH_SHORT).show();
                }
                break;
            default:
                super.onActivityResult(requestCode, resultCode, data);
                break;
        }
    }

    private void onScanPcQrCode(String qrcode) {
        String prefix = qrcode.substring(0, qrcode.lastIndexOf('/') + 1);
        String value = qrcode.substring(qrcode.lastIndexOf("/") + 1);
        switch (prefix) {
            case WfcScheme.QR_CODE_PREFIX_PC_SESSION:
//                pcLogin(value);
                break;
            case WfcScheme.QR_CODE_PREFIX_USER:
                showUser(value);
                break;
            case WfcScheme.QR_CODE_PREFIX_GROUP:
                joinGroup(value);
                break;
            case WfcScheme.QR_CODE_PREFIX_CHANNEL:
//                subscribeChannel(value);
                break;
            default:
                Toast.makeText(this, "qrcode: " + qrcode, Toast.LENGTH_SHORT).show();
                break;
        }
    }

    private void showUser(String uid) {
        UserViewModel userViewModel = ViewModelProviders.of(this).get(UserViewModel.class);
        UserInfo userInfo = userViewModel.getUserInfo(uid, true);
        if (userInfo == null) {
            return;
        }
        Intent intent = new Intent(this, UserInfoActivity.class);
        intent.putExtra("userInfo", userInfo);
        startActivity(intent);
    }

    private void joinGroup(String groupId) {
        Intent intent = new Intent(this, GroupInfoActivity.class);
        intent.putExtra("groupId", groupId);
        startActivity(intent);
    }

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

        // {"id":1,"appCode":"phb","downUrl":"http://ys.51zhir.cn/app_api/apk/dr20190514.apk","fileName":"dr20190514.apk",
        // "fileSize":null,"isMust":"0","md5Code":"722f70f68c262e9c585f7dd800ae327c",
        // "memo":null,"osType":"android","verCode":"1","verName":"V1.0.1 Beta","verUpdateMsg":"版本更新内容"}
        switch (mType) {
            case MethodUrl.appVersion:
                if (tData != null && !tData.isEmpty()) {
                    //网络版本号
                    MbsConstans.UpdateAppConstans.VERSION_NET_CODE = UtilTools.getIntFromStr(tData.get("verCode") + "");
                    //MbsConstans.UpdateAppConstans.VERSION_NET_CODE = 3;
                    //网络下载url
                    MbsConstans.UpdateAppConstans.VERSION_NET_APK_URL = tData.get("downUrl") + "";
                    //MbsConstans.UpdateAppConstans.VERSION_NET_APK_URL = "https://qd.myapp.com/myapp/qqteam/AndroidQQ/mobileqq_android.apk";
                    //网络版本名称
                    MbsConstans.UpdateAppConstans.VERSION_NET_APK_NAME = tData.get("verName") + "";
                    //网络MD5值
                    MbsConstans.UpdateAppConstans.VERSION_MD5_CODE = tData.get("md5Code") + "";
                    //本次更新内容
                    MbsConstans.UpdateAppConstans.VERSION_NET_UPDATE_MSG = tData.get("verUpdateMsg") + "";

                    String mustUpdate = tData.get("isMust") + "";
                    MbsConstans.UpdateAppConstans.VERSION_UPDATE_FORCE = !mustUpdate.equals("0");
                    showUpdateDialog();
                }
                break;
            case MethodUrl.nameCode:
                String result = tData.get("result") + "";
                SPUtils.put(MainActivity.this, MbsConstans.SharedInfoConstans.NAME_CODE_DATA, result);
                break;
            case MethodUrl.REFRESH_TOKEN://获取refreshToken返回结果
                MbsConstans.REFRESH_TOKEN = tData.get("refresh_token") + "";
                mIsRefreshToken = false;
                for (String stag : mRequestTagList) {
                    switch (stag) {
                        case MethodUrl.nameCode://{
                            getNameCodeInfo();
                            break;
                        default:
                    }
                    mRequestTagList = new ArrayList<>();
                    break;
                }
            default:
        }
    }

    /**
     * 获取全局字典配置信息
     */
    public void getNameCodeInfo() {
        mRequestTag = MethodUrl.nameCode;
        Map<String, String> map = new HashMap<>();
        Map<String, String> mHeaderMap = new HashMap<String, String>();
        mRequestPresenterImp.requestGetToRes(mHeaderMap, MethodUrl.nameCode, map);
    }

    @Override
    public void loadDataError(Map<String, Object> map, String mType) {
        switch (mType) {
            case MethodUrl.appVersion:
                break;
        }
        dealFailInfo(map, mType);
    }

    private void showUpdateDialog() {
        if (MbsConstans.UpdateAppConstans.VERSION_NET_CODE > MbsConstans.UpdateAppConstans.VERSION_APP_CODE) {
            mUpdateDialog = new UpdateDialog(this, true);
            View.OnClickListener onClickListener = new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    switch (v.getId()) {
                        case R.id.cancel:
                            if (MbsConstans.UpdateAppConstans.VERSION_UPDATE_FORCE) {
                                showToastMsg("本次升级为必须更新，请您更新！");
                            } else {
                                mUpdateDialog.dismiss();
                            }
                            break;
                        case R.id.confirm:
                            PermissionsUtils.requsetRunPermission(MainActivity.this, new RePermissionResultBack() {
                                @Override
                                public void requestSuccess() {
                                    mUpdateDialog.getProgressLay().setVisibility(View.VISIBLE);
                                    DownloadService.downNewFile(MbsConstans.UpdateAppConstans.VERSION_NET_APK_URL, 1003,
                                            MbsConstans.UpdateAppConstans.VERSION_NET_APK_NAME, MbsConstans.UpdateAppConstans.VERSION_MD5_CODE, MainActivity.this);
                                }

                                @Override
                                public void requestFailer() {

                                }
                            }, Permission.Group.STORAGE);

                            if (!MbsConstans.UpdateAppConstans.VERSION_UPDATE_FORCE) {
                                mUpdateDialog.dismiss();
                            }
                            break;
                        default:
                    }
                }
            };
            mUpdateDialog.setCanceledOnTouchOutside(false);
            mUpdateDialog.setCancelable(false);
            String ss = "";
            if (MbsConstans.UpdateAppConstans.VERSION_UPDATE_FORCE) {
                ss = "必须更新";
            } else {
                ss = "建议更新";
            }
            mUpdateDialog.setOnClickListener(onClickListener);
            mUpdateDialog.initValue("检查新版本" + "(" + ss + ")", "更新内容:\n" + MbsConstans.UpdateAppConstans.VERSION_NET_UPDATE_MSG);
            mUpdateDialog.show();

            if (MbsConstans.UpdateAppConstans.VERSION_UPDATE_FORCE) {
                mUpdateDialog.setCancelable(false);
                mUpdateDialog.tv_cancel.setEnabled(false);
                mUpdateDialog.tv_cancel.setVisibility(View.GONE);
            } else {
                mUpdateDialog.setCancelable(true);
                mUpdateDialog.tv_cancel.setEnabled(true);
                mUpdateDialog.tv_cancel.setVisibility(View.VISIBLE);
            }
            mUpdateDialog.getProgressLay().setVisibility(View.GONE);
            DownloadService.mProgressBar = mUpdateDialog.getProgressBar();
            DownloadService.mTextView = mUpdateDialog.getPrgText();
        }
    }
}
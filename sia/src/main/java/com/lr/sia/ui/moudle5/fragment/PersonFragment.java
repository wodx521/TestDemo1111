package com.lr.sia.ui.moudle5.fragment;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.ToggleButton;

import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;

import com.jaeger.library.StatusBarUtil;
import com.lr.sia.R;
import com.lr.sia.api.MethodUrl;
import com.lr.sia.basic.BasicFragment;
import com.lr.sia.basic.MbsConstans;
import com.lr.sia.mvp.presenter.RequestPresenterImp;
import com.lr.sia.mvp.view.RequestView;
import com.lr.sia.mywidget.pulltozoomview.PullToZoomScrollViewEx;
import com.lr.sia.ui.moudle.activity.AboutActivity;
import com.lr.sia.ui.moudle.activity.LoginActivity1;
import com.lr.sia.ui.moudle5.activity.ConnectServiceActivity;
import com.lr.sia.ui.moudle5.activity.LockEmptyActivity;
import com.lr.sia.ui.moudle5.activity.LockProfitActivity;
import com.lr.sia.ui.moudle5.activity.PersonInfoActivity;
import com.lr.sia.ui.moudle5.activity.PrivateLockActivity;
import com.lr.sia.ui.moudle5.activity.RedLogActivity;
import com.lr.sia.ui.moudle5.activity.SettingActivity1;
import com.lr.sia.ui.moudle5.activity.ShareActivity;
import com.lr.sia.ui.moudle5.activity.SystemNoticeActivity;

import com.lr.sia.utils.imageload.GlideApp;
import com.lr.sia.utils.imageload.GlideUtils;
import com.lr.sia.utils.tool.JSONUtil;
import com.lr.sia.utils.tool.SPUtils;
import com.lr.sia.utils.tool.UtilTools;

import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import cn.wildfirechat.remote.ChatManager;
import jp.wasabeef.glide.transformations.RoundedCornersTransformation;

public class PersonFragment extends BasicFragment implements View.OnClickListener, RequestView {

    View headView;
    View zoomView;
    View contentView;

    @BindView(R.id.person_scroll_view)
    PullToZoomScrollViewEx mPersonScrollView;
    CardView mMySettingLay;
    CardView myRedLog;
    CardView myTransferLog;
    CardView myRewardLog;
    CardView myShare;
    CardView systemNotice;
    CardView setting;
    CardView aboutUs;
    CardView contactService;
    LinearLayout mLinearLayout;

    @BindView(R.id.back_view)
    ImageView mBackView;
    @BindView(R.id.titleText)
    TextView mTitleText;
    @BindView(R.id.personal_more_button)
    ImageView mPersonalMoreButton;
    @BindView(R.id.index_top_layout_person)
    LinearLayout mIndexTopLayoutPerson;
    private ImageView mZoomImage;
    private ImageView mRoundImageView;
    private TextView mUserName, tvUserNumber;
    //private RelativeLayout mShowYueLay;

    private ToggleButton mToggleButton;


    private String mRequestTag = "";
    private BroadcastReceiver mBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (action.equals(MbsConstans.BroadcastReceiverAction.USER_INFO_UPDATE)) {
                if (MbsConstans.USER_MAP == null) {
                    getUserInfoAction();
                } else {
                    //initHeadPic();
                }
            } else if (action.equals(MbsConstans.BroadcastReceiverAction.MONEY_UPDATE)) {
                getMoneyInfoAction();
            }
        }
    };
    private TextView exitTv;
    private TextView tvAirdropLock;
    private TextView tvPrivatePlacementLockUp;
    private CardView myLockProfit;
    private LinearLayout llLockEmpty;
    private LinearLayout llLockPrivate;
    private DecimalFormat decimalFormat;
    private String ktFrozenAmount;
    private String smFrozenAmount;

    public PersonFragment() {
        // Required empty public constructor
    }

    @Override
    public int getLayoutId() {
        return R.layout.fragment_person;
    }

    @Override
    public void init() {
        decimalFormat = new DecimalFormat("#.########");
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(MbsConstans.BroadcastReceiverAction.USER_INFO_UPDATE);
        intentFilter.addAction(MbsConstans.BroadcastReceiverAction.MONEY_UPDATE);
        getActivity().registerReceiver(mBroadcastReceiver, intentFilter);

        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, (int) getActivity().getResources().getDimension(R.dimen.title_item_height) + UtilTools.getStatusHeight2(getActivity()));
        mIndexTopLayoutPerson.setLayoutParams(layoutParams);
        mIndexTopLayoutPerson.setPadding(0, UtilTools.getStatusHeight2(getActivity()), 0, 0);
        //mTitleText.setText(getResources().getString(R.string.bottom_person));


        setBarTextColor();
        //StatusBarUtil.setColor(getActivity(), ContextCompat.getColor(getActivity(), R.color.transparent), 0);
        headView = LayoutInflater.from(getActivity()).inflate(R.layout.profile_head_view, null, false);
        zoomView = LayoutInflater.from(getActivity()).inflate(R.layout.profile_zoom_view, null, false);
        contentView = LayoutInflater.from(getActivity()).inflate(R.layout.profile_content_view, null, false);
        mZoomImage = zoomView.findViewById(R.id.iv_zoom);
        //  Glide.with(this).load(R.drawable.per_bg).into(mZoomImage);
        //Glide.with(this).load(R.drawable.per_bg).into(mZoomImage);

        mPersonScrollView.setHeaderView(headView);
        mPersonScrollView.setZoomView(zoomView);
        mPersonScrollView.setScrollContentView(contentView);
        mLinearLayout = headView.findViewById(R.id.user_info_lay);
        tvAirdropLock = headView.findViewById(R.id.tvAirdropLock);
        tvPrivatePlacementLockUp = headView.findViewById(R.id.tvPrivatePlacementLockUp);
        llLockEmpty = headView.findViewById(R.id.llLockEmpty);
        llLockPrivate = headView.findViewById(R.id.llLockPrivate);
        myRedLog = contentView.findViewById(R.id.myRedLog);
        myTransferLog = contentView.findViewById(R.id.myTransferLog);
        myRewardLog = contentView.findViewById(R.id.myRewardLog);
        myShare = contentView.findViewById(R.id.myShare);
        systemNotice = contentView.findViewById(R.id.systemNotice);
        setting = contentView.findViewById(R.id.setting);
        aboutUs = contentView.findViewById(R.id.aboutUs);
        contactService = contentView.findViewById(R.id.contactService);
        exitTv = contentView.findViewById(R.id.exit_tv);
        myLockProfit = contentView.findViewById(R.id.myLockProfit);
        mRoundImageView = mPersonScrollView.findViewById(R.id.my_image);
        mUserName = mPersonScrollView.findViewById(R.id.username_tv);
        tvUserNumber = mPersonScrollView.findViewById(R.id.tvUserNumber);
        mToggleButton = mPersonScrollView.findViewById(R.id.togglePwd);

        mLinearLayout.setOnClickListener(this);
        myRedLog.setOnClickListener(this);
        myTransferLog.setOnClickListener(this);
        myRewardLog.setOnClickListener(this);
        myShare.setOnClickListener(this);
        systemNotice.setOnClickListener(this);
        setting.setOnClickListener(this);
        contactService.setOnClickListener(this);
        aboutUs.setOnClickListener(this);
        exitTv.setOnClickListener(this);
        myLockProfit.setOnClickListener(this);
        llLockEmpty.setOnClickListener(this);
        llLockPrivate.setOnClickListener(this);
        if (MbsConstans.USER_MAP == null) {
            getUserInfoAction();
        } else {
            mUserName.setText(MbsConstans.USER_MAP.get("name") + "");
            //initHeadPic();
        }
        getMoneyInfoAction();

        // showProgressDialog();
    }

    public void setBarTextColor() {
        StatusBarUtil.setDarkMode(getActivity());
    }

    public void getMoneyInfoAction() {
        mRequestPresenterImp = new RequestPresenterImp(this, getActivity());
        mRequestTag = MethodUrl.USER_GETUSERCENTERMESSAGE;
        Map<String, Object> map = new HashMap<>();
        if (UtilTools.empty(MbsConstans.ACCESS_TOKEN)) {
            MbsConstans.ACCESS_TOKEN = SPUtils.get(getActivity(), MbsConstans.ACCESS_TOKEN, "").toString();
        }
        map.put("token", MbsConstans.ACCESS_TOKEN);
        mRequestPresenterImp.requestPostToMap(MethodUrl.USER_GETUSERCENTERMESSAGE, map);
    }

    /**
     * 获取用户信息
     */
    public void getUserInfoAction() {
      /*  mRequestPresenterImp = new RequestPresenterImp(this,getActivity());
        mRequestTag = MethodUrl.userInfo;
        Map<String, String> map = new HashMap<>();
        Map<String,String> mHeaderMap = new HashMap<String,String>();
        mRequestPresenterImp.requestGetData(mHeaderMap, MethodUrl.userInfo, map);*/
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if (hidden) {
            setUserVisibleHint(false);
        } else {
            setUserVisibleHint(true);
            getMoneyInfoAction();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (resultCode == Activity.RESULT_OK) {
            switch (requestCode) {
                case MbsConstans.CHANGE_LANGUAGE:
                    getActivity().recreate();
                    break;
                default:
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onResume() {
        super.onResume();
        getMoneyInfoAction();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        getActivity().unregisterReceiver(mBroadcastReceiver);
    }

    private void initHeadPic() {
        String headUrl = MbsConstans.USER_MAP.get("weHeadPic") + "";
        if (headUrl.contains("http")) {
            GlideUtils.loadImage(getActivity(), headUrl, mRoundImageView);
        } else {
            headUrl = MbsConstans.PIC_URL + headUrl;
            GlideUtils.loadImage(getActivity(), headUrl, mRoundImageView);
        }
    }

    @Override
    public void onClick(View view) {
        Intent intent;
        switch (view.getId()) {
            case R.id.user_info_lay://个人中心
            case R.id.my_image:  //个人中心
                intent = new Intent(getActivity(), PersonInfoActivity.class);
                startActivity(intent);
                break;
            case R.id.myRedLog:
                intent = new Intent(getActivity(), RedLogActivity.class);
                intent.putExtra("logType", "1");
                startActivity(intent);
                break;
            case R.id.myTransferLog:
                intent = new Intent(getActivity(), RedLogActivity.class);
                intent.putExtra("logType", "2");
                startActivity(intent);
                break;
            case R.id.myLockProfit:
                intent = new Intent(getActivity(), LockProfitActivity.class);
                startActivity(intent);
                break;
            case R.id.myRewardLog:
                intent = new Intent(getActivity(), RedLogActivity.class);
                intent.putExtra("logType", "2");
                startActivity(intent);
                break;
            case R.id.myShare:
                intent = new Intent(getActivity(), ShareActivity.class);
                startActivity(intent);
                break;
            case R.id.systemNotice:
                intent = new Intent(getActivity(), SystemNoticeActivity.class);
                startActivity(intent);
                break;
            case R.id.contactService:
                intent = new Intent(getActivity(), ConnectServiceActivity.class);
                startActivity(intent);
                break;
            case R.id.aboutUs:
                intent = new Intent(getActivity(), AboutActivity.class);
                startActivity(intent);
                break;
            case R.id.setting:  //更多设置
                intent = new Intent(getActivity(), SettingActivity1.class);
                startActivityForResult(intent, MbsConstans.CHANGE_LANGUAGE);
                break;
            case R.id.exit_tv:
                if (getActivity() != null) {
                    getActivity().finish();
                }
                intent = new Intent(getActivity(), LoginActivity1.class);
                startActivity(intent);
                ChatManager.Instance().disconnect(false);
                break;
            case R.id.llLockEmpty:
                intent = new Intent(getActivity(), LockEmptyActivity.class);
                intent.putExtra("empty", ktFrozenAmount);
                startActivity(intent);
                break;
            case R.id.llLockPrivate:
                intent = new Intent(getActivity(), PrivateLockActivity.class);
                intent.putExtra("lock", smFrozenAmount);
                startActivity(intent);
                break;
            default:
        }
    }

    @Override
    public void showProgress() {
        //showProgressDialog();
    }

    @Override
    public void disimissProgress() {
        //dismissProgressDialog();
    }

    @Override
    public void loadDataSuccess(Map<String, Object> tData, String mType) {
        Intent intent;
        switch (mType) {
            case MethodUrl.USER_GETUSERCENTERMESSAGE://用户信息
                switch (tData.get("code") + "") {
                    case "1": //请求成功
                        MbsConstans.USER_MAP = tData;
                        SPUtils.put(getActivity(), MbsConstans.SharedInfoConstans.LOGIN_INFO, JSONUtil.getInstance().objectToJson(MbsConstans.USER_MAP));
                        Map<String, Object> data = (Map<String, Object>) tData.get("data");
                        String nickName = data.get("nick_name") + "";
                        String userCode = data.get("user_code") + "";
                        String avatarImage = data.get("avatar_image") + "";
                        ktFrozenAmount = data.get("kt_frozen_amount") + "";
                        smFrozenAmount = data.get("sm_frozen_amount") + "";
                        tvAirdropLock.setText(decimalFormat.format(Double.parseDouble(ktFrozenAmount)));
                        tvPrivatePlacementLockUp.setText(decimalFormat.format(Double.parseDouble(smFrozenAmount)));
                        mUserName.setText(nickName);
                        tvUserNumber.setText("ID: " + userCode);
                        GlideApp.with(getActivity())
                                .load(avatarImage)
                                .transform(new RoundedCornersTransformation(UtilTools.dip2px(getActivity(), 65), 2))
                                .placeholder(R.drawable.head)
                                .error(R.drawable.head)
                                .into(mRoundImageView);
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

    @Override
    public void loadDataError(Map<String, Object> map, String mType) {
        //dismissProgressDialog();
        dealFailInfo(map, mType);
    }
}

package cn.wildfire.chat.kit.conversation.message.viewholder;

import android.content.Intent;
import android.text.style.ImageSpan;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;
import com.google.gson.internal.LinkedTreeMap;
import com.lqr.emoji.MoonUtils;
import com.lr.sia.R;
import com.lr.sia.api.MethodUrl;
import com.lr.sia.basic.MbsConstans;
import com.lr.sia.manage.ActivityManager;
import com.lr.sia.mvp.base.IBaseRequestCallBack;
import com.lr.sia.mvp.model.RequestModelImp;
import com.lr.sia.mvp.presenter.RequestPresenterImp;
import com.lr.sia.mywidget.redpackage.CustomDialog;
import com.lr.sia.mywidget.redpackage.OnRedPacketDialogClickListener;
import com.lr.sia.ui.moudle.activity.LoginActivity1;
import com.lr.sia.ui.moudle3.activity.RedListActivity;
import com.lr.sia.ui.moudle3.activity.TransferInfoActivity;
import com.lr.sia.utils.tool.JSONUtil;
import com.lr.sia.utils.tool.LogUtilDebug;
import com.lr.sia.utils.tool.SPUtils;
import com.lr.sia.utils.tool.UtilTools;

import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.OnClick;
import cn.wildfire.chat.kit.annotation.EnableContextMenu;
import cn.wildfire.chat.kit.annotation.MessageContentType;
import cn.wildfire.chat.kit.annotation.ReceiveLayoutRes;
import cn.wildfire.chat.kit.annotation.SendLayoutRes;
import cn.wildfire.chat.kit.conversation.ConversationFragment;
import cn.wildfire.chat.kit.conversation.message.model.UiMessage;
import cn.wildfire.chat.kit.utils.ToastUtils;
import cn.wildfire.chat.kit.widget.RedPacketViewHolder;
import cn.wildfirechat.message.RedPacketMessageContent;
import cn.wildfirechat.message.core.MessageDirection;
import cn.wildfirechat.model.Conversation;
import okhttp3.MediaType;
import okhttp3.RequestBody;

@MessageContentType(RedPacketMessageContent.class)
@SendLayoutRes(resId = R.layout.conversation_item_redpacket_send)
@ReceiveLayoutRes(resId = R.layout.conversation_item_redpacket_receive)
@EnableContextMenu
public class RedPacketMessageContentViewHolder extends NormalMessageContentViewHolder {
    @NonNull
    protected ConversationFragment fragment;
    @BindView(R.id.contentTextView)
    TextView contentTextView;
    @BindView(R.id.llRed)
    LinearLayout llRed;
    @BindView(R.id.imageView)
    ImageView imageView;
    @BindView(R.id.red_tv)
    TextView redTv;

    String redId;
    String redPackType;
    UiMessage uiMessage;
    private RequestModelImp mRequestModelImp;
    private View mRedPacketDialogView;
    private RedPacketViewHolder mRedPacketViewHolder;
    private CustomDialog mRedPacketDialog;
    private RedPacketMessageContent redPacketMessageContent;

    public RedPacketMessageContentViewHolder(ConversationFragment fragment, RecyclerView.Adapter adapter, View itemView) {
        super(fragment, adapter, itemView);
        this.fragment = fragment;
    }

    @Override
    public void onBind(UiMessage message) {
        this.uiMessage = message;
        redPacketMessageContent = (RedPacketMessageContent) message.message.content;
        MoonUtils.identifyFaceExpression(fragment.getContext(), contentTextView, (redPacketMessageContent).getContent(), ImageSpan.ALIGN_BOTTOM);
        redId = (redPacketMessageContent).cid;
        redPackType = (redPacketMessageContent).redPackType;
        if (redPackType.equals("1")) {
            redTv.setText("SIA红包");
            imageView.setVisibility(View.VISIBLE);
        } else {
            redTv.setText("SIA转账");
            imageView.setVisibility(View.GONE);
        }

        LogUtilDebug.i("show", "cid***:" + redId);
        LogUtilDebug.i("show", "redPackType***:" + redPackType);

    }

    @OnClick(R.id.llRed)
    public void onClickTest(View view) {
        if (redPackType.equals("1")) {
            getRedPacketAction(redId);
        } else {
            getTransferInfoAction(redId);
        }
    }
    private void getTransferInfoAction(String id) {
        mRequestModelImp = new RequestModelImp(fragment.getActivity());
        Map<String, Object> map = new HashMap<>();
        if (UtilTools.empty(MbsConstans.ACCESS_TOKEN)) {
            MbsConstans.ACCESS_TOKEN = SPUtils.get(MbsConstans.SharedInfoConstans.ACCESS_TOKEN, "").toString();
        }
        map.put("token", MbsConstans.ACCESS_TOKEN);
        map.put("id", id);
        Map<String, String> mHeaderMap = new HashMap<String, String>();
        Gson gson = new Gson();
        String jsonStr = gson.toJson(map);
        RequestBody body = RequestBody.create(MediaType.parse("application/json; charset=utf-8"), jsonStr);
        mRequestModelImp.requestPostToMap(mHeaderMap, MethodUrl.TRANSFER_GETCHATTRANSINFO, body, new IBaseRequestCallBack<Map<String, Object>>() {
            @Override
            public void beforeRequest() {

            }

            @Override
            public void requestError(Map<String, Object> errorInfo, String mType) {

            }

            @Override
            public void requestComplete() {

            }

            @Override
            public void requestSuccess(Map<String, Object> callBack, String mType) {
                switch (callBack.get("code") + "") {
                    case "1": //请求成功
                        if (!UtilTools.empty(callBack.get("data"))) {
                            Map<String, Object> mapData = (Map<String, Object>) callBack.get("data");
                            String transferStatus = mapData.get("tran_status") + "";
                            if (UtilTools.empty(MbsConstans.RONGYUN_MAP)) {
                                String s = SPUtils.get(fragment.getActivity(), MbsConstans.SharedInfoConstans.RONGYUN_DATA, "").toString();
                                MbsConstans.RONGYUN_MAP = JSONUtil.getInstance().jsonMap(s);
                            }
                            if (uiMessage.message.direction == MessageDirection.Receive) {
                                if ("0".equals(transferStatus) || "1".equals(transferStatus)) {
//                                    // 未收款
//                                    getTransferMoneyAction(mapData.get("id") + "");
//                                } else if () {
                                    // 已收款
                                    Intent intent = new Intent(fragment.getActivity(), TransferInfoActivity.class);
                                    intent.putExtra("transferInfo", (LinkedTreeMap) mapData);
                                    fragment.startActivity(intent);
                                } else if ("2".equals(transferStatus)) {
                                    // 已取消
                                    ToastUtils.showToast(mapData.get("tran_status_msg") + "");
                                }
                            }
                        }
                        break;
                    case "0": //请求失败
                        ToastUtils.showToast(callBack.get("msg") + "");
                        break;
                    case "-1": //token过期
                        ActivityManager.getInstance().close();
                        Intent intent = new Intent(fragment.getActivity(), LoginActivity1.class);
                        fragment.startActivity(intent);
                        break;
                    default:
                }
            }
        });
    }

    public void getRedPacketAction(String redIdInfo) {
        mRequestModelImp = new RequestModelImp(fragment.getActivity());
        Map<String, Object> map = new HashMap<>();
        if (UtilTools.empty(MbsConstans.ACCESS_TOKEN)) {
            MbsConstans.ACCESS_TOKEN = SPUtils.get(MbsConstans.SharedInfoConstans.ACCESS_TOKEN, "").toString();
        }
        map.put("token", MbsConstans.ACCESS_TOKEN);
        map.put("id", redIdInfo);
        Map<String, String> mHeaderMap = new HashMap<>();
        Gson gson = new Gson();
        String jsonStr = gson.toJson(map);
        RequestBody body = RequestBody.create(MediaType.parse("application/json; charset=utf-8"), jsonStr);
        mRequestModelImp.requestPostToMap(mHeaderMap, MethodUrl.CHAT_RED_GETHONGBAOINFO, body, new IBaseRequestCallBack<Map<String, Object>>() {
            @Override
            public void beforeRequest() {

            }

            @Override
            public void requestError(Map<String, Object> errorInfo, String mType) {

            }

            @Override
            public void requestComplete() {

            }

            @Override
            public void requestSuccess(Map<String, Object> callBack, String mType) {
                switch (callBack.get("code") + "") {
                    case "1": //请求成功
                        if (!UtilTools.empty(callBack.get("data"))) {
                            Map<String, Object> mapData = (Map<String, Object>) callBack.get("data");
                            if (!UtilTools.empty(mapData)) {
                                String sendId = mapData.get("send_user_rc_id") + "";
                                String isReceive = mapData.get("is_receive") + "";
                                String number = mapData.get("number") + "";
                                if (UtilTools.empty(MbsConstans.RONGYUN_MAP)) {
                                    String s = SPUtils.get(fragment.getActivity(), MbsConstans.SharedInfoConstans.RONGYUN_DATA, "").toString();
                                    MbsConstans.RONGYUN_MAP = JSONUtil.getInstance().jsonMap(s);
                                }
                                String currentId = MbsConstans.RONGYUN_MAP.get("id") + "";
                                if (uiMessage.message.conversation.type == Conversation.ConversationType.Single) {
                                    if (currentId.equals(sendId) || "0".equals(number)) {
                                        // 领取或本人
                                        Intent intent = new Intent(fragment.getActivity(), RedListActivity.class);
                                        intent.putExtra("id", redId);
                                        intent.putExtra("type", "1");
                                        fragment.startActivity(intent);
                                    } else {
                                        if ("0".equals(isReceive)) {
                                            // 未领取
                                            showRedPacketDialog(redPacketMessageContent);
                                        } else {
                                            // 领取或本人
                                            Intent intent = new Intent(fragment.getActivity(), RedListActivity.class);
                                            intent.putExtra("id", redId);
                                            intent.putExtra("type", "1");
                                            fragment.startActivity(intent);
                                        }
                                    }
                                } else if (uiMessage.message.conversation.type == Conversation.ConversationType.Group) {
                                    if ("0".equals(number)) {
                                        if ("0".equals(isReceive)) {
                                            Intent intent = new Intent(fragment.getActivity(), RedListActivity.class);
                                            intent.putExtra("id", redId);
                                            intent.putExtra("type", "1");
                                            fragment.startActivity(intent);
                                        } else {
                                            ToastUtils.showToast(R.string.redNotice);
                                        }
                                    } else {
                                        if ("0".equals(isReceive)) {
                                            // 未领取
                                            showRedPacketDialog(redPacketMessageContent);
                                        } else {
                                            // 领取或本人
                                            Intent intent = new Intent(fragment.getActivity(), RedListActivity.class);
                                            intent.putExtra("id", redId);
                                            intent.putExtra("type", "1");
                                            fragment.startActivity(intent);
                                        }
                                    }
                                }
                            }
                        }
                        break;
                    case "0": //请求失败
                        ToastUtils.showToast(callBack.get("msg") + "");
                        break;
                    case "-1": //token过期
                        ActivityManager.getInstance().close();
                        Intent intent = new Intent(fragment.getActivity(), LoginActivity1.class);
                        fragment.startActivity(intent);
                        break;
                    default:
                }
            }
        });
    }

    public void showRedPacketDialog(RedPacketMessageContent entity) {
        mRedPacketDialogView = View.inflate(fragment.getActivity(), R.layout.dialog_red_packet, null);
        mRedPacketViewHolder = new RedPacketViewHolder(fragment.getActivity(), mRedPacketDialogView);
        mRedPacketDialog = new CustomDialog(fragment.getActivity(), mRedPacketDialogView, R.style.custom_dialog);
        mRedPacketDialog.setCancelable(false);

        mRedPacketViewHolder.setData(entity);
        mRedPacketViewHolder.setOnRedPacketDialogClickListener(new OnRedPacketDialogClickListener() {
            @Override
            public void onCloseClick() {
                mRedPacketDialog.dismiss();
            }

            @Override
            public void onOpenClick() {
                //领取红包,调用接口
                demolitionRedPacket(entity.cid);
            }
        });
        mRedPacketDialog.show();
    }

    private void demolitionRedPacket(String redId) {
        mRequestModelImp = new RequestModelImp(fragment.getActivity());
        Map<String, Object> map = new HashMap<>();
        if (UtilTools.empty(MbsConstans.ACCESS_TOKEN)) {
            MbsConstans.ACCESS_TOKEN = SPUtils.get(MbsConstans.SharedInfoConstans.ACCESS_TOKEN, "").toString();
        }
        map.put("token", MbsConstans.ACCESS_TOKEN);
        map.put("id", redId);
        Map<String, String> mHeaderMap = new HashMap<>();
        Gson gson = new Gson();
        String jsonStr = gson.toJson(map);
        RequestBody body = RequestBody.create(MediaType.parse("application/json; charset=utf-8"), jsonStr);
        mRequestModelImp.requestPostToMap(mHeaderMap, MethodUrl.CHAT_RED_GETHONGBAORECEIVE, body, new IBaseRequestCallBack<Map<String, Object>>() {
            @Override
            public void beforeRequest() {

            }

            @Override
            public void requestError(Map<String, Object> errorInfo, String mType) {

            }

            @Override
            public void requestComplete() {

            }

            @Override
            public void requestSuccess(Map<String, Object> callBack, String mType) {
                // 领取成功
                Intent intent;
                switch (callBack.get("code") + "") {
                    case "1": //请求成功
                        // 领取或本人
                        intent = new Intent(fragment.getActivity(), RedListActivity.class);
                        intent.putExtra("id", redId);
                        intent.putExtra("type", "1");
                        fragment.startActivity(intent);
                        break;
                    case "0": //请求失败
                        ToastUtils.showToast(callBack.get("msg") + "");
                        break;
                    case "-1": //token过期
                        ActivityManager.getInstance().close();
                        intent = new Intent(fragment.getActivity(), LoginActivity1.class);
                        fragment.startActivity(intent);
                        break;
                    default:
                }
            }
        });
    }

}

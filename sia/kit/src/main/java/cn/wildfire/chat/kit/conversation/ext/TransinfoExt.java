package cn.wildfire.chat.kit.conversation.ext;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.lr.sia.R;
import com.lr.sia.ui.moudle3.activity.TransferMoneyActivity1;
import com.lr.sia.utils.tool.LogUtilDebug;
import com.lr.sia.utils.tool.SPUtils;

import cn.wildfire.chat.kit.annotation.ExtContextMenuItem;
import cn.wildfire.chat.kit.conversation.ext.core.ConversationExt;
import cn.wildfirechat.message.RedPacketMessageContent;
import cn.wildfirechat.model.Conversation;

import static android.app.Activity.RESULT_OK;

public class TransinfoExt extends ConversationExt {
    /**
     * @param containerView 扩展view的container
     * @param conversation
     */
    @ExtContextMenuItem(title = "转账")
    public void transInfo(View containerView, Conversation conversation) {

            Intent intent = new Intent(activity, TransferMoneyActivity1.class);
            intent.putExtra("tarid", conversation.target);
            intent.putExtra("type", "1");
            intent.putExtra("id", (String) SPUtils.get("friendId", ""));
            activity.startActivity(intent);

    }

    @Override
    public boolean filter(Conversation conversation) {
        if (conversation.type == Conversation.ConversationType.Single) {
            return false;
        }
        return true;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            Bundle bundle = data.getExtras();
            if (bundle != null){
                String cid = bundle.get("red_id")+"";
                String content = bundle.get("text")+"";

                RedPacketMessageContent messageContent = new RedPacketMessageContent();
                messageContent.setContent(content);
                messageContent.cid = cid;
                messageContent.redPackType = "2";
                messageViewModel.sendRedMessage(conversation,messageContent);
            }
        }
    }

    @Override
    public int priority() {
        return 100;
    }

    @Override
    public int iconResId() {
        return R.mipmap.jrmf_ic_zhuanzhang;
    }

    @Override
    public String title(Context context) {
        return "转账";
    }
}

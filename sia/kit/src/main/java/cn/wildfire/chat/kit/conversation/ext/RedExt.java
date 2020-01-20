package cn.wildfire.chat.kit.conversation.ext;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.lr.sia.R;
import com.lr.sia.basic.MbsConstans;
import com.lr.sia.ui.moudle3.activity.RedMoneyActivity1;
import com.lr.sia.utils.tool.SPUtils;
import com.lr.sia.utils.tool.UtilTools;

import cn.wildfire.chat.kit.annotation.ExtContextMenuItem;
import cn.wildfire.chat.kit.conversation.ext.core.ConversationExt;
import cn.wildfirechat.message.RedPacketMessageContent;
import cn.wildfirechat.model.Conversation;

import static android.app.Activity.RESULT_OK;

public class RedExt extends ConversationExt {
    /**
     * @param containerView 扩展view的container
     * @param conversation
     */
    @ExtContextMenuItem(title = "红包")
    public void redPacket(View containerView, Conversation conversation) {
        Intent intent = new Intent(activity, RedMoneyActivity1.class);
        intent.putExtra("tarid", conversation.target);
        if (Conversation.ConversationType.Single == conversation.type) {
            intent.putExtra("type", "1");
            intent.putExtra("id", (String) SPUtils.get("friendId", ""));
        } else {
            intent.putExtra("type", "2");
        }
        activity.startActivityForResult(intent, MbsConstans.SEND_RED);
    }

    @Override
    public int priority() {
        return 100;
    }

    @Override
    public int iconResId() {
        return R.mipmap._ic_hongbao;
    }

    @Override
    public String title(Context context) {
        return "红包";
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            Bundle bundle = data.getExtras();
            if (bundle != null) {
                String cid = bundle.get("red_id") + "";
                String content;
                if (UtilTools.empty(bundle.get("text"))) {
                    content = activity.getString(R.string.redMoneyDes);
                } else {
                    content = bundle.get("text") + "";
                }

                RedPacketMessageContent messageContent = new RedPacketMessageContent();
                messageContent.setContent(content);
                messageContent.cid = cid;
                messageContent.redPackType = "1";
                messageViewModel.sendRedMessage(conversation, messageContent);
            }
        }
    }
}

package cn.wildfire.chat.kit.group;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.lr.sia.R;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import cn.wildfire.chat.kit.ChatManagerHolder;
import cn.wildfire.chat.kit.conversation.ConversationActivity;
import cn.wildfirechat.model.Conversation;
import cn.wildfirechat.model.GroupInfo;
import cn.wildfirechat.model.GroupMember;
import cn.wildfirechat.remote.ChatManager;
import cn.wildfirechat.remote.GeneralCallback;
import cn.wildfirechat.remote.GetGroupsCallback;

public class GroupListFragment extends Fragment implements OnGroupItemClickListener {
    @BindView(R.id.groupRecyclerView)
    RecyclerView recyclerView;
    @BindView(R.id.tipTextView)
    TextView tipTextView;
    @BindView(R.id.groupsLinearLayout)
    LinearLayout groupsLinearLayout;
    private List<GroupInfo> groupInfoList = new ArrayList<>();
    private GroupListAdapter groupListAdapter;
    private OnGroupItemClickListener onGroupItemClickListener;
    private boolean isContain = false;

    public void setOnGroupItemClickListener(OnGroupItemClickListener onGroupItemClickListener) {
        this.onGroupItemClickListener = onGroupItemClickListener;
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (groupListAdapter != null && isVisibleToUser) {
            reloadGroupList();
        }
    }

    public void reloadGroupList() {
        ChatManager.Instance().getMyGroups(new GetGroupsCallback() {
            @Override
            public void onSuccess(List<GroupInfo> groupInfos) {
                groupInfoList.clear();
                if (groupInfos == null || groupInfos.isEmpty()) {
                    groupsLinearLayout.setVisibility(View.GONE);
                    tipTextView.setVisibility(View.VISIBLE);
                } else {
                    for (GroupInfo groupInfo : groupInfos) {
                        if (groupInfo != null) {
                            groupInfoList.add(groupInfo);
                        }
                    }
                    groupListAdapter.setGroupInfos(groupInfoList);
                    groupListAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onFail(int errorCode) {
                groupsLinearLayout.setVisibility(View.GONE);
                tipTextView.setVisibility(View.VISIBLE);
                tipTextView.setText("error: " + errorCode);
            }
        });
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.group_list_fragment, container, false);
        ButterKnife.bind(this, view);
        init();
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        reloadGroupList();
    }

    private void init() {
        groupListAdapter = new GroupListAdapter(this);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setAdapter(groupListAdapter);
        groupListAdapter.setOnGroupItemClickListener(this);

    }

    @Override
    public void onGroupClick(GroupInfo groupInfo) {
        if (onGroupItemClickListener != null) {
            onGroupItemClickListener.onGroupClick(groupInfo);
            return;
        }
        if (groupInfo.owner.equals(ChatManager.Instance().getUserId())) {
            Intent intent = new Intent(getActivity(), ConversationActivity.class);
            Conversation conversation = new Conversation(Conversation.ConversationType.Group, groupInfo.target);
            intent.putExtra("conversation", conversation);
            startActivity(intent);
        } else {
            GroupMember groupMember1 = ChatManager.Instance().getGroupMember(groupInfo.target, ChatManager.Instance().getUserId());
            if (groupMember1 != null) {
                Intent intent = new Intent(getActivity(), ConversationActivity.class);
                Conversation conversation = new Conversation(Conversation.ConversationType.Group, groupInfo.target);
                intent.putExtra("conversation", conversation);
                startActivity(intent);
            } else {
                ChatManagerHolder.gChatManager.addGroupMembers(groupInfo.target, Arrays.asList(ChatManager.Instance().getUserId()), Arrays.asList(0), null, new GeneralCallback() {
                    @Override
                    public void onSuccess() {
                        Intent intent = new Intent(getActivity(), ConversationActivity.class);
                        Conversation conversation = new Conversation(Conversation.ConversationType.Group, groupInfo.target);
                        intent.putExtra("conversation", conversation);
                        startActivity(intent);
                        Log.e("add", "加群成功");
                    }

                    @Override
                    public void onFail(int errorCode) {
                        Log.e("add", "加群失败");
                    }
                });
            }
        }

    }
}

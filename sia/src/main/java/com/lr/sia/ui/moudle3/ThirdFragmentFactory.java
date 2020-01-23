package com.lr.sia.ui.moudle3;

import android.util.SparseArray;

import androidx.fragment.app.Fragment;

import com.lr.sia.ui.moudle3.fragment.MyFollowFragment;
import com.lr.sia.ui.moudle3.fragment.MyGroupFragment;

import cn.wildfire.chat.kit.contact.ContactListFragment;
import cn.wildfire.chat.kit.conversationlist.ConversationListFragment;
import cn.wildfire.chat.kit.group.GroupListFragment;


public class ThirdFragmentFactory {
    public static SparseArray<Fragment> fragmentMainMap = new SparseArray<>();

    public static Fragment getFragment(int position) {
        Fragment baseFragment = null;
        //尝试从内存中读取需要的对象
        baseFragment = fragmentMainMap.get(position);
        if (baseFragment != null) {
            return baseFragment;
        } else {
            switch (position) {
                case 0:
                    baseFragment = new ConversationListFragment();
                    break;
                case 1:
                    baseFragment = new MyFollowFragment();
//                    baseFragment = new ContactListFragment();
                    break;
                case 2:
                    baseFragment = new GroupListFragment();
//                    baseFragment = new MyGroupFragment();
                    break;
                default:
            }
            fragmentMainMap.put(position, baseFragment);
            return baseFragment;
        }
    }
}

package com.lr.sia.basic;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;

import androidx.multidex.MultiDexApplication;

import com.lr.sia.Config;
import com.lr.sia.ui.moudle3.location.viewholder.LocationMessageContentViewHolder;
import com.lr.sia.utils.RealmUtils;
import com.lr.sia.utils.tool.AppContextUtil;

import com.lr.sia.BuildConfig;
import com.tencent.bugly.Bugly;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

import cn.bingoogolapple.swipebacklayout.BGASwipeBackHelper;
import cn.wildfire.chat.kit.WfcUIKit;
import cn.wildfire.chat.kit.conversation.message.viewholder.MessageViewHolderManager;

public class BasicApplication extends MultiDexApplication {
    private static Context mContext;
    int appCount = 0;
    private static int mMainThreadId;
    private static Handler mHandler;

    public static Context getContext() {
        return mContext;
    }

    public static void setContext(Context mContext) {
        BasicApplication.mContext = mContext;
    }

    /**
     * 获取进程号对应的进程名
     *
     * @param pid 进程号
     * @return 进程名
     */
    private static String getProcessName(int pid) {
        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new FileReader("/proc/" + pid + "/cmdline"));
            String processName = reader.readLine();
            if (!TextUtils.isEmpty(processName)) {
                processName = processName.trim();
            }
            return processName;
        } catch (Throwable throwable) {
            throwable.printStackTrace();
        } finally {
            try {
                if (reader != null) {
                    reader.close();
                }
            } catch (IOException exception) {
                exception.printStackTrace();
            }
        }
        return null;
    }

    public static Context getmContext() {
        return mContext;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mContext = getApplicationContext();
        Config.validateConfig();
        mMainThreadId = android.os.Process.myTid();
        mHandler = new Handler();
        RealmUtils.init(this);
        WfcUIKit wfcUIKit = new WfcUIKit();
        wfcUIKit.init(this);
        MessageViewHolderManager.getInstance().registerMessageViewHolder(LocationMessageContentViewHolder.class);
        setupWFCDirs();

        registerActivityListener();
        //setTypeface();
        AppContextUtil.init(this);

        /**
         * 必须在 Application 的 onCreate 方法中执行 BGASwipeBackHelper.init 来初始化滑动返回
         * 第一个参数：应用程序上下文
         * 第二个参数：如果发现滑动返回后立即触摸界面时应用崩溃，请把该界面里比较特殊的 View 的 class 添加到该集合中，目前在库中已经添加了 WebView 和 SurfaceView
         */
        BGASwipeBackHelper.init(this, null);

        Bugly.init(this, MbsConstans.BUGLY_KEY, false);

        // 只在主进程初始化
//        if (getCurProcessName(this).equals(BuildConfig.APPLICATION_ID)) {
//            WfcUIKit wfcUIKit = new WfcUIKit();
//            wfcUIKit.init(this);
////            PushService.init(this, BuildConfig.APPLICATION_ID);
//            MessageViewHolderManager.getInstance().registerMessageViewHolder(LocationMessageContentViewHolder.class);
//            setupWFCDirs();
//        }
    }

    private void setupWFCDirs() {
        File file = new File(Config.VIDEO_SAVE_DIR);
        if (!file.exists()) {
            file.mkdirs();
        }
        file = new File(Config.AUDIO_SAVE_DIR);
        if (!file.exists()) {
            file.mkdirs();
        }
        file = new File(Config.FILE_SAVE_DIR);
        if (!file.exists()) {
            file.mkdirs();
        }
        file = new File(Config.PHOTO_SAVE_DIR);
        if (!file.exists()) {
            file.mkdirs();
        }
    }

    /* (non-Javadoc)
     * @see android.app.Application#onTerminate()
     */
    @Override
    public void onTerminate() {
        super.onTerminate();
    }

    public static String getCurProcessName(Context context) {

        int pid = android.os.Process.myPid();

        ActivityManager activityManager = (ActivityManager) context
                .getSystemService(Context.ACTIVITY_SERVICE);

        for (ActivityManager.RunningAppProcessInfo appProcess : activityManager
                .getRunningAppProcesses()) {

            if (appProcess.pid == pid) {
                return appProcess.processName;
            }
        }
        return null;
    }

    private void registerActivityListener() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
            registerActivityLifecycleCallbacks(
                    new ActivityLifecycleCallbacks() {

                        @Override
                        public void onActivityCreated(Activity activity, Bundle
                                savedInstanceState) {
                            // TODO Auto-generated method stub
                        }

                        @Override
                        public void onActivityStarted(Activity activity) {
                            // TODO Auto-generated method stub
                            appCount++;
                        }

                        @Override
                        public void onActivityResumed(Activity activity) {
                            // TODO Auto-generated method stub
                        }

                        @Override
                        public void onActivityPaused(Activity activity) {
                            // TODO Auto-generated method stub
                        }

                        @Override
                        public void onActivityStopped(Activity activity) {
                            // TODO Auto-generated method stub
                            appCount--;
							/*if(appCount==0){
								Toast.makeText(getApplicationContext(),
										getResources().getString(R.string.app_name_main)+"应用进入后台运行",
										Toast.LENGTH_LONG).show();
							}*/
                        }

                        @Override
                        public void onActivitySaveInstanceState(Activity activity, Bundle
                                outState) {
                            // TODO Auto-generated method stub
                        }

                        @Override
                        public void onActivityDestroyed(Activity activity) {
                            // TODO Auto-generated method stub
                        }
                    }
            );
        }
    }

    public static  int getmMainThreadId() {
        return mMainThreadId;
    }

    public static Handler getmHandler() {
        return mHandler;
    }
}

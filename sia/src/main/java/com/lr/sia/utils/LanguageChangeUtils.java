package com.lr.sia.utils;

import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Build;
import android.os.LocaleList;
import android.util.DisplayMetrics;

import java.util.Locale;

public class LanguageChangeUtils {
    /**
     * 获取当前系统语言
     * @return 当前系统语言
     */
    public static String getSystemLanguage() {
        Locale locale;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            locale = LocaleList.getDefault().get(0);
        } else {
            locale = Locale.getDefault();
        }
        return locale.getLanguage();
    }

    /**
     * 设置 App 语言
     * @param context　
     * @param language
     */
    public static void setLanguage(Context context, String language) {
        Resources resources = context.getResources();
        Configuration configuration = resources.getConfiguration();
        DisplayMetrics displayMetrics = resources.getDisplayMetrics();
        switch (language) {
            case "zh":
                configuration.locale = Locale.CHINESE;
                break;
            case "en":
                configuration.locale = Locale.ENGLISH;
                break;
            case "ja":
                configuration.locale = Locale.ENGLISH;
                break;
            case "ko":
                configuration.locale = Locale.ENGLISH;
                break;
            default:
                configuration.locale = Locale.CHINESE;
                break;
        }
        resources.updateConfiguration(configuration, displayMetrics);
    }

    public static void setLanguage(Context context, int language) {
        Resources resources = context.getResources();
        Configuration configuration = resources.getConfiguration();
        DisplayMetrics displayMetrics = resources.getDisplayMetrics();
        switch (language) {
            case 0:
                configuration.locale = Locale.CHINESE;
                break;
            case 1:
                configuration.locale = Locale.ENGLISH;
                break;
            case 2:
                configuration.locale = Locale.JAPANESE;
                break;
            case 3:
                configuration.locale = Locale.KOREA;
                break;
            default:
                configuration.locale = Locale.CHINESE;
                break;
        }
        resources.updateConfiguration(configuration, displayMetrics);
    }

    /**
     * 重启App
     * @param context
     */
    public static void resetApp(Context context) {
        Intent intent = context.getPackageManager()
                .getLaunchIntentForPackage(context.getPackageName());
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
        context.startActivity(intent);
    }
}

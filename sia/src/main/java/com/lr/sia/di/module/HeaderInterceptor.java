package com.lr.sia.di.module;

import com.lr.sia.db.TestClass;
import com.lr.sia.mvp.base.UnitClass;
import com.lr.sia.ui.moudle.AtextClass;
import com.lr.sia.utils.UtilClass;
import com.lr.sia.utils.secret.MD5;
import com.lr.sia.utils.tool.SPUtils;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

public class HeaderInterceptor implements Interceptor {
    @Override
    public Response intercept(Chain chain) throws IOException {
        Request request = chain.request();
        int languageSelect = (int) SPUtils.get("languageSelect", -1);
//        switch (languageSelect) {
//            case 1:
//                requestBuilder = request.newBuilder().addHeader("lang", "en");
//                break;
//            case 2:
//                requestBuilder = request.newBuilder().addHeader("lang", "zh-cn");
//                break;
//            case 3:
//                requestBuilder = request.newBuilder().addHeader("lang", "zh-cn");
//                break;
//            case 0:
//            default:
//                requestBuilder = request.newBuilder().addHeader("lang", "zh-cn");
//                break;
//        }
        Request.Builder builder = request.newBuilder();
        builder.addHeader("lang", ((languageSelect == -1 ? 0 : languageSelect) + 1) + "");
        long currentTimeMillis = System.currentTimeMillis();
        String MD5_SALT = UnitClass.DRED + UtilClass.IJDVBH + AtextClass.FDGER + TestClass.QWE;
        builder.addHeader("sign", MD5.md5String(MD5_SALT + currentTimeMillis / 1000L).toUpperCase());
        builder.addHeader("time", currentTimeMillis / 1000L + "");
        request = builder.build();
        return chain.proceed(request);
    }
}

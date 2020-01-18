package com.lr.sia.di.module;

import com.lr.sia.utils.tool.TextViewUtils;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

/**
 * 各个Object 类的实例化
 */
@Module
public class ObjectModule {

    //实例化TextViewUtils
    @Provides
    @Singleton
    TextViewUtils providerTextViewUtils() {
        return new TextViewUtils();
    }


}

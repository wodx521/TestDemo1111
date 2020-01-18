package com.lr.sia.di.component;

import com.lr.sia.di.module.MVPModule;
import com.lr.sia.di.module.NetModule;
import com.lr.sia.utils.tool.JSONUtil;

import javax.inject.Singleton;

import dagger.Component;

/**
 * 网络连接的DaggeR的注射器
 */
@Singleton
@Component(modules = NetModule.class)
public interface NetComponent {

    //void  injectTo(ApiManager apiManager);
    void injectTo(MVPModule mvpModule);

    void injectTo(JSONUtil jsonUtil);
}

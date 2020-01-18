package com.lr.sia.mvp.base;

import com.lr.sia.api.ApiManagerService;
import com.lr.sia.di.component.DaggerMVPComponent;

import javax.inject.Inject;

/**
 * Model层的基类
 * 网络请求的配置
 */
public class BaseModel {
    //retrofit 请求数据的管理类
    @Inject
    public ApiManagerService mApiManagerService;

    public BaseModel() {
        DaggerMVPComponent.create().InjectinTo(this);
    }
}

package com.lr.sia.di.component;

import com.lr.sia.di.module.MVPModule;
import com.lr.sia.mvp.base.BaseModel;
import com.lr.sia.mvp.model.RequestModelImp;

import javax.inject.Singleton;

import dagger.Component;

/**
 * MVP的DaggeR的注射器
 */

@Singleton
@Component(modules = MVPModule.class)
public interface MVPComponent {

    //ApiManagerService
    void InjectinTo(BaseModel baseModel);

    //CompositeDisposable
    void InjectinTo(RequestModelImp requestModel);

}

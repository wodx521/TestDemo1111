package com.lr.sia.di.component;


import com.lr.sia.basic.BasicActivity;
import com.lr.sia.basic.BasicFragment;
import com.lr.sia.di.module.PersenerModule;
import com.lr.sia.di.scope.ActivityScope;

import javax.inject.Singleton;

import dagger.Component;


@ActivityScope
@Singleton
@Component(modules = PersenerModule.class)
public interface PersenerComponent {
    //RequestPresenterImp
    void injectTo(BasicActivity activity);

    void injectTo(BasicFragment fragment);

}

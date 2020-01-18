package com.lr.sia.di.component;

import com.lr.sia.di.scope.ActivityScope;

import javax.inject.Singleton;

import com.lr.sia.di.module.ObjectModule;
import dagger.Component;

@ActivityScope
@Singleton
@Component(modules = ObjectModule.class)
public interface ObjectComponent {
    // void injectTo(PayHistoryAdapter adapter);
}

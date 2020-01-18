package com.lr.sia.api;

import com.lr.sia.mvp.model.RequestModelImp;

import io.reactivex.disposables.CompositeDisposable;


public interface RxActionManager<T> {

    void add(T tag, RequestModelImp subscription);

    void addCompositeDisposable(T tag, CompositeDisposable compositeDisposable);

    void remove(T tag);

    void cancel(T tag);

    void cancelActivityAll(T tag);

    void cancelAll();
}

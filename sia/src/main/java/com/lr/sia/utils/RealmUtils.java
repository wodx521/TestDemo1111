package com.lr.sia.utils;

import android.content.Context;


import com.lr.sia.db.DataCache;

import io.realm.Realm;
import io.realm.RealmConfiguration;

public class RealmUtils {
    private static Realm realm;

    public static void init(Context context) {
        Realm.init(context);
        RealmConfiguration config = new RealmConfiguration.Builder()
                .name("cache.realm") //文件名
                .schemaVersion(0) //版本号
                .build();
        Realm.setDefaultConfiguration(config);
    }

    public static void put(String dataName, String cacheContent) {
        realm.beginTransaction();
        DataCache dataCache = queryData(dataName);
        if (dataCache == null) {
            dataCache = realm.createObject(DataCache.class);
            dataCache.setInterfaceName(dataName);
            dataCache.setResultContent(cacheContent);
        } else {
            if (!dataCache.getResultContent().equals(cacheContent)) {
                dataCache.setResultContent(cacheContent);
            }
        }
        realm.commitTransaction();
    }

    public static DataCache queryData(String dataName) {
        return realm.where(DataCache.class).equalTo("interfaceName", dataName).findFirst();
    }

    public static void deleteData() {
        realm.delete(DataCache.class);
    }

    public static Realm getRealm() {
        realm = Realm.getDefaultInstance();
        return realm;
    }
}

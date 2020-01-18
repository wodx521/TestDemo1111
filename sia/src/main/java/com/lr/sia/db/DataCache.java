package com.lr.sia.db;

import io.realm.RealmObject;

public class DataCache extends RealmObject {
    private String interfaceName;
    private String resultContent;

    public DataCache() {
    }

    public DataCache(String interfaceName, String resultContent) {
        this.interfaceName = interfaceName;
        this.resultContent = resultContent;
    }

    public String getInterfaceName() {
        return interfaceName == null ? "" : interfaceName;
    }

    public void setInterfaceName(String interfaceName) {
        this.interfaceName = interfaceName;
    }

    public String getResultContent() {
        return resultContent == null ? "" : resultContent;
    }

    public void setResultContent(String resultContent) {
        this.resultContent = resultContent;
    }
}

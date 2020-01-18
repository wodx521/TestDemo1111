package com.lr.sia.db;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.lr.sia.basic.MbsConstans;
import com.lr.sia.utils.tool.LogUtilDebug;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DacheData {

    private static DacheData sqliteDBHelp;
    private SQLiteDatabase db;
    private String dbPath = MbsConstans.DATABASE_PATH + "/" + MbsConstans.DATABASE_NAME;

    private DacheData() {

    }

    public static DacheData getInstance() {
        if (sqliteDBHelp == null) {
            sqliteDBHelp = new DacheData();
        }
        return sqliteDBHelp;
    }

    /**
     * 执行SQL语句
     *
     * @param sql
     */
    public void execSQL(String sql) {
        openDb();
        if (db.isOpen()) {
            db.execSQL(sql);
            closeDB();
        }
    }

    /**
     * 打开数据库
     */
    public void openDb() {
        db = SQLiteDatabase.openOrCreateDatabase(dbPath, null);
    }

    /**
     * 关闭数据库
     */
    public void closeDB() {
        if (db != null && db.isOpen()) {
            db.close();
        }
    }

    /**
     * 查询数据库列表
     *
     * @return
     */
    public List<Map<String, Object>> selectDB() {
        List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
        openDb();
        if (db.isOpen()) {
            Cursor cursor = db.rawQuery("select * from tb_dache_data", null);
            int idIndex = cursor.getColumnIndex("id");
            int dache_nameIndex = cursor.getColumnIndex("dache_name");
            int dache_contentIndex = cursor.getColumnIndex("dache_content");

            while (cursor.moveToNext()) {
                int id = cursor.getInt(idIndex);
                String dache_name = cursor.getString(dache_nameIndex);
                String dache_content = cursor.getString(dache_contentIndex);

                Map<String, Object> map = new HashMap<String, Object>();
                map.put("id", id);
                map.put("dache_name", dache_name);
                map.put("dache_content", dache_content);
                list.add(map);
            }
            cursor.close();
            closeDB();
        }
        return list;
    }

    /**
     * 批量查询数据库
     *
     * @return
     */
    public String selectDBByListKey(String dacheName) {
        openDb();
        if (db.isOpen()) {
            Cursor cursor = db.rawQuery("select * from tb_dache_data", null);
            int idIndex = cursor.getColumnIndex("id");
            int dache_nameIndex = cursor.getColumnIndex("dache_name");
            int dache_contentIndex = cursor.getColumnIndex("dache_content");

            while (cursor.moveToNext()) {
                int id = cursor.getInt(idIndex);
                String dache_name = cursor.getString(dache_nameIndex);
                String dache_content = cursor.getString(dache_contentIndex);
                if (dacheName.equals(dache_name)) {
                    return dache_content;
                }
            }
            cursor.close();
            closeDB();
        }
        return "";
    }

    /**
     * 插入数据
     *
     * @param
     */
    public void insertDB(String dacheName, String dacheContent) {
        openDb();
        if (db.isOpen()) {
            ContentValues values = new ContentValues();
            values.put("dache_name", dacheName);
            values.put("dache_content", dacheContent);
            db.insert("tb_dache_data", "id", values);
            closeDB();
        }
    }

    /***
     * 清空数据
     */
    public void clearData() {
        openDb();
        if (db.isOpen()) {
            String sql = "DELETE FROM tb_dache_data;";
            db.execSQL(sql);
            LogUtilDebug.i("show", "清空成功");
            closeDB();
        }
    }


}

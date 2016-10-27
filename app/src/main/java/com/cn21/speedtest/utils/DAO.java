package com.cn21.speedtest.utils;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by shenpeng on 2016/8/4.
 * 数据库CRUD操作
 */
public class DAO {
    Context mContext;
    public DAO(Context mContext){
        this.mContext=mContext;
    }
    public void insertData(SQLiteDatabase db,String table_name,String text,int img,String content){

        String sql="INSERT OR IGNORE INTO "+table_name+"(id,text,img,content)"+" VALUES(null,?,?,?)";
        Object args[]=new Object[]{text,img,content};
        db.execSQL(sql,args);
        db.close();
    }

    public void update(SQLiteDatabase db,String table_name,String text,int img,String content){
        String sql="UPDATE "+table_name+" SET text=?,imag=?,content=? WHERE id=?";
        Object args[]=new Object[]{text,img,content};
        db.execSQL(sql,args);
        db.close();
    }

    public void deleteData(SQLiteDatabase db,String table_name){
        String sql="DELETE FROM "+table_name;
        db.execSQL(sql);
        db.close();
    }

    public ArrayList<Map<String,Object>> queryData(Cursor cursor) {
        ArrayList<Map<String, Object>> result = new ArrayList<Map<String, Object>>();
        while (cursor.moveToNext()){
            Map<String,Object> map=new HashMap<String,Object>();
            map.put("img",cursor.getInt(1));
            map.put("text",cursor.getString(2));
            map.put("content",cursor.getString(3));
            result.add(map);
        }
        return result;
    }


}

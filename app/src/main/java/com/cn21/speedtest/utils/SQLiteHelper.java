package com.cn21.speedtest.utils;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by shenpeng on 2016/8/4.
 * 存储主页数据
 */
public class SQLiteHelper extends SQLiteOpenHelper{

    private static final String DATABASE_NAME="mydb.db";
    private static final int DATABASE_VERSION=1;
    private static final String TABLE_SHOW_NAME="table_show";
    private static final String TABLE_SELECT_NAME="table_select";

    public SQLiteHelper(Context context,String database,int i,int version){

       super(context,database,null,version);

    }
    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        String sql_select="CREATE TABLE "+TABLE_SELECT_NAME+"("+"id INTEGER PRIMARY KEY AUTOINCREMENT,"+"img INTEGER NOT NULL UNIQUE,"+"text VARCHAR(45),"+"content VARCHAR(45)"+")";
        String sql_show="CREATE TABLE "+TABLE_SHOW_NAME+"("+"id INTEGER PRIMARY KEY AUTOINCREMENT,"+"img INTEGER NOT NULL UNIQUE,"+"text VARCHAR(45),"+"content VARCHAR(45)"+")";
        sqLiteDatabase.execSQL(sql_select);
        sqLiteDatabase.execSQL(sql_show);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        String sql_select="DROP TABLE IF EXISTS"+TABLE_SELECT_NAME;
        String sql_show="DROP TABLE IF EXISTS"+TABLE_SHOW_NAME;
        sqLiteDatabase.execSQL(sql_select);
        sqLiteDatabase.execSQL(sql_show);
        this.onCreate(sqLiteDatabase);
    }
}

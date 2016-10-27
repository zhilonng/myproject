package com.cn21.speedtest.model;

import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * Created by lenovo on 2016/8/17.
 */
public class SettingDatabase {

    private SQLiteDatabase mSQLiteDatabase = null;

    private final static String DATABASE_NAME = "Setting.db";

    private final static String TABLE_NAME = "table1";

    private final static String TABLE_ID = "SettingID";

    private final static String TABLE_AUTO = "AutoStartup";

    private final static String TABLE_STAT = "StartStatistic";

    private final static String TABLE_WINDOW = "FloatWindow";

    private final static String TABLE_GSM_LIMIT = "GSMLimit";

    private final static String TABLE_WIFI_LIMIT = "WIFILimit";

    private final static int DB_VERSION = 1;

    private Context mContext = null;


    private final static String CREATE_TABLE = "CREATE TABLE " + TABLE_NAME
            + " (" + TABLE_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
            + TABLE_AUTO + " INTEGER," + TABLE_STAT
            + " INTEGER," + TABLE_WINDOW + " INTEGER," + TABLE_GSM_LIMIT + " Long," + TABLE_WIFI_LIMIT + " Long)";


    private DatabaseHelper mDatabaseHelper = null;

    private static class DatabaseHelper extends SQLiteOpenHelper {
        DatabaseHelper(Context context) {
            super(context, DATABASE_NAME, null, DB_VERSION);
        }


        @Override
        public void onCreate(SQLiteDatabase db) {
            // TODO Auto-generated method stub
            db.execSQL(CREATE_TABLE);

        }

        /*
         * �ƾڮw��s
         *�R����í��s�Ыطs��
         * */
        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            // TODO Auto-generated method stub
            db.execSQL("DROP TABLE IF EXISTS notes");
            onCreate(db);
        }
    }


    public SettingDatabase(Context context) {
        mContext = context;
    }


    public void open() throws SQLException {
        mDatabaseHelper = new DatabaseHelper(mContext);
        mSQLiteDatabase = mDatabaseHelper.getWritableDatabase();
    }


    public void close() {
        mDatabaseHelper.close();
    }


    public void insertData(int AutoStartup, int StartStatistic,
                           int FloatWindow, Long GSMLimit, Long WIFILimit) {
        String insertData = " INSERT INTO " + TABLE_NAME + " ("
                + TABLE_AUTO + ", " + TABLE_STAT + "," + TABLE_WINDOW + ","
                + TABLE_GSM_LIMIT + "," + TABLE_WIFI_LIMIT + " ) values(" + AutoStartup + ", "
                + StartStatistic + "," + FloatWindow + "," + GSMLimit + "," + WIFILimit + ");";
        mSQLiteDatabase.execSQL(insertData);

    }


    public void updateData(int AutoStartup, int StartStatistic,
                           int FloatWindow, Long GSMLimit, Long WIFILimit) {
        String updateData = " UPDATE " + TABLE_NAME + " SET " + TABLE_AUTO + "=" + AutoStartup + " , " + TABLE_STAT + "=" + StartStatistic +
                TABLE_WINDOW + "=" + FloatWindow + " , " + TABLE_GSM_LIMIT + "=" + GSMLimit + " , " + TABLE_WIFI_LIMIT + "=" + WIFILimit +
                " WHERE " + TABLE_ID + " = 1";
        mSQLiteDatabase.execSQL(updateData);
    }

    public boolean check() {
        Cursor mCursor = mSQLiteDatabase.query(TABLE_NAME, null, null, null, null, null, null, null);
        if (mCursor != null) {
            int iRow = mCursor.getCount(); // ��o��ưO���
            mCursor.close();
            if (iRow == 1)
                return true;
        }
        return false;
    }


    public void updateAutoStartup(int autoStartup) {
        String updateData = " UPDATE " + TABLE_NAME + " SET " + TABLE_AUTO + "=" + autoStartup + " WHERE " + TABLE_ID + " = 1";
        mSQLiteDatabase.execSQL(updateData);
        Log.i("update", updateData);
    }


    public void updateStartStatistic(int startStatistic) {
        String updateData = " UPDATE " + TABLE_NAME + " SET " + TABLE_STAT + "=" + startStatistic + " WHERE " + TABLE_ID + " = 1";
        mSQLiteDatabase.execSQL(updateData);
        Log.i("update", updateData);
    }


    public void updateFloatWindow(int floatWindow) {
        String updateData = " UPDATE " + TABLE_NAME + " SET " + TABLE_WINDOW + "=" + floatWindow + " WHERE " + TABLE_ID + " = 1";
        mSQLiteDatabase.execSQL(updateData);
        Log.i("update", updateData);
    }


    public void updateGSMLimit(Long GSMLimit) {
        String updateData = " UPDATE " + TABLE_NAME + " SET " + TABLE_GSM_LIMIT + "=" + GSMLimit + " WHERE " + TABLE_ID + " = 1";
        mSQLiteDatabase.execSQL(updateData);
    }


    public void updateWIFILimit(Long WIFILimit) {
        String updateData = " UPDATE " + TABLE_NAME + " SET " + TABLE_WIFI_LIMIT + "=" + WIFILimit + " WHERE " + TABLE_ID + " = 1";
        mSQLiteDatabase.execSQL(updateData);
    }


    public boolean checkautoStartup() {

        Cursor cursor;
        String autoStartup = null;
        cursor = mSQLiteDatabase.query(TABLE_NAME, null, null, null, null, null, null);
        if (cursor != null) {
            int iRow = cursor.getCount(); // ��o��ưO���
            cursor.moveToFirst();
            for (int i = 0; i < iRow; i++) {
                autoStartup = cursor.getString(cursor.getColumnIndex(TABLE_AUTO));
                cursor.moveToNext();
            }
            cursor.close();
        } else return false;

        if (Integer.parseInt(autoStartup) == 1) {
            return true;
        }

        return false;
    }


    public boolean checkStartStat() {

        Cursor cursor;
        String startStat = null;
        cursor = mSQLiteDatabase.query(TABLE_NAME, null, null, null, null, null, null);
        if (cursor != null) {
            int iRow = cursor.getCount(); // ��o��ưO���
            cursor.moveToFirst();
            for (int i = 0; i < iRow; i++) {
                startStat = cursor.getString(cursor.getColumnIndex(TABLE_STAT));
                cursor.moveToNext();
            }
            cursor.close();
        } else return false;

        if (Integer.parseInt(startStat) == 1) {
            return true;
        }

        return false;
    }


    public boolean checkFloatWindow() {

        Cursor cursor;
        String floatWindow = null;
        cursor = mSQLiteDatabase.query(TABLE_NAME, null, null, null, null, null, null);
        if (cursor != null) {
            int iRow = cursor.getCount(); // ��o��ưO���
            cursor.moveToFirst();
            for (int i = 0; i < iRow; i++) {
                floatWindow = cursor.getString(cursor.getColumnIndex(TABLE_WINDOW));
                cursor.moveToNext();
            }
            cursor.close();
        } else return false;

        if (Integer.parseInt(floatWindow) == 1) {
            return true;
        }

        return false;
    }


    public Long checkGSMLimit() {

        Cursor cursor;
        String gsmLimit = null;
        cursor = mSQLiteDatabase.query(TABLE_NAME, null, null, null, null, null, null);
        if (cursor != null) {
            int iRow = cursor.getCount();
            cursor.moveToFirst();
            for (int i = 0; i < iRow; i++) {
                gsmLimit = cursor.getString(cursor.getColumnIndex(TABLE_GSM_LIMIT));
                cursor.moveToNext();
            }
            cursor.close();
        } else return (long) 0;

        return Long.parseLong(gsmLimit);
    }


    public Long checkWIFILimit() {

        Cursor cursor;
        String wifiLimit = null;
        cursor = mSQLiteDatabase.query(TABLE_NAME, null, null, null, null, null, null);
        if (cursor != null) {
            int iRow = cursor.getCount();
            cursor.moveToFirst();
            for (int i = 0; i < iRow; i++) {
                wifiLimit = cursor.getString(cursor.getColumnIndex(TABLE_WIFI_LIMIT));
                cursor.moveToNext();
            }
            cursor.close();
        } else return (long) 0;

        return Long.parseLong(wifiLimit);
    }


    public void deleteAll() {
        mSQLiteDatabase.execSQL("DROP TABLE " + TABLE_NAME);
    }

    public void converDate() {

    }

    public void clear() {
        mSQLiteDatabase.delete(TABLE_NAME, null, null);
    }
}
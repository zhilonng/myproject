package com.cn21.speedtest.adapter;

import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by lenovo on 2016/8/17.
 */
public class DatabaseAdapter {

    private SQLiteDatabase mSQLiteDatabase = null;

    private final static String DATABASE_NAME = "Flow.db";

    private final static String TABLE_NAME = "table1";

    private final static String TABLE_ID = "FlowID";

    private final static String TABLE_UPF = "UpFlow";

    private final static String TABLE_DPF = "DownFlow";

    private final static String TABLE_TIME = "Time";

    private final static String TABLE_WEB = "WebType";

    private final static int DB_VERSION = 1;

    private Context mContext = null;


    private final static String CREATE_TABLE = "CREATE TABLE " + TABLE_NAME
            + " (" + TABLE_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
            + TABLE_UPF + " Long," + TABLE_DPF
            + " Long," + TABLE_WEB + " INTEGER," + TABLE_TIME + " DATETIME)";


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


        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            // TODO Auto-generated method stub
            db.execSQL("DROP TABLE IF EXISTS notes");
            onCreate(db);
        }
    }


    public DatabaseAdapter(Context context) {
        mContext = context;
    }


    public void open() throws SQLException {
        mDatabaseHelper = new DatabaseHelper(mContext);
        mSQLiteDatabase = mDatabaseHelper.getWritableDatabase();
    }


    public void close() {
        mDatabaseHelper.close();
    }


    public void insertData(long UpFlow, long DownFlow,
                           int WebType, Date date) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:MM");
        String dataString = sdf.format(date);
        String insertData = " INSERT INTO " + TABLE_NAME + " ("
                + TABLE_UPF + ", " + TABLE_DPF + "," + TABLE_WEB + ","
                + TABLE_TIME + " ) values(" + UpFlow + ", "
                + DownFlow + "," + WebType + "," + "datetime('" + dataString
                + "'));";
        mSQLiteDatabase.execSQL(insertData);
        System.out.println("----"+UpFlow+"-----"+DownFlow+"--------"+WebType+"_________"+dataString);
        Log.d("123123123", "+++++++++++++++++++++");

    }



    public void updateData(long upFlow,long downFlow, int webType, Date date){
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        String dataString = sdf.format(date);
        String updateData = " UPDATE " + TABLE_NAME + " SET "+ TABLE_UPF+"=" +upFlow+" , " +TABLE_DPF+"="+downFlow+
                " WHERE " + TABLE_WEB+"=" + webType+" and "+ TABLE_TIME +" like '"+dataString+"%'";
        mSQLiteDatabase.execSQL(updateData);
    }



    public Cursor check(int netType, Date date) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        String dataString = sdf.format(date);

        Cursor mCursor = mSQLiteDatabase.query(TABLE_NAME, new String[] {
                        TABLE_UPF+" AS upPro",TABLE_DPF+" AS dwPro"},  TABLE_WEB + "=" + netType+" and "+ TABLE_TIME +" like '"+dataString+"%'", null, null,
                null, null, null);

        return mCursor;
    }



    public Cursor fetchDayFlow(int year, int month, int day, int netType) {
        StringBuffer date = new StringBuffer();
        date.append(String.valueOf(year) + "-");
        if (month < 10) {
            date.append("0" + String.valueOf(month) + "-");
        } else {
            date.append(String.valueOf(month) + "-");
        }
        if (day < 10) {
            date.append("0" + String.valueOf(day));
        } else {
            date.append(String.valueOf(day));
        }
        Cursor mCursor = mSQLiteDatabase.query(TABLE_NAME, new String[] {
                        "sum(" + TABLE_UPF + ") AS sumUp",
                        "sum(" + TABLE_DPF + ") as sumDw" }, TABLE_WEB + "=" + netType
                        + " and " + TABLE_TIME + " LIKE '" + date.toString() + "%'",
                null, null, null, null, null);

        return mCursor;
    }

    /* �d�ߨC��y�q �i�Ω����M��y�q�έp */
    public Cursor fetchMonthFlow(int year, int Month, int netType) {
        StringBuffer date = new StringBuffer();
        date.append(String.valueOf(year) + "-");
        if (Month < 10) {
            date.append("0" + String.valueOf(Month) + "-");
        } else {
            date.append(String.valueOf(Month) + "-");
        }
        Cursor mCursor = mSQLiteDatabase.query(TABLE_NAME, new String[] {
                "sum(" + TABLE_UPF + ") AS monthUp",
                "sum(" + TABLE_DPF + ") as monthDw" }, TABLE_WEB + "="
                + netType + " and " + TABLE_TIME + " LIKE '" + date.toString()
                + "%'", null, null, null, null, null);
//		mCursor.close();
        return mCursor;
    }

    public long getProFlowUp(int netType, Date date){
        Cursor cur = check( netType, date);
        long UP=0 ;
        if(cur.moveToNext()){
            int up = cur.getColumnIndex("upPro");
            UP = cur.getLong(up);

        }
        cur.close();
        return UP ;

    }

    public long getProFlowDw(int netType, Date date){
        Cursor cur = check( netType, date);
        long UP=0 ;
        if(cur.moveToNext()){
            int up = cur.getColumnIndex("dwPro");
            UP = cur.getLong(up);
        }
        cur.close();
        return UP ;
    }

    public long calculate(int year, int month, int day, int netType) {
        Cursor calCurso = fetchDayFlow(year, month, day, netType);
        long sum = 0;
        if (calCurso != null) {
            if (calCurso.moveToFirst()) {
                do {
                    int upColumn = calCurso.getColumnIndex("sumUp");
                    int dwColumn = calCurso.getColumnIndex("sumDw");
                    sum = calCurso.getLong(upColumn)
                            + calCurso.getLong(dwColumn);
                } while (calCurso.moveToNext());
            }
        }

        return sum;
    }

    public long weekUpFloew(int netType){
        Calendar date1 = Calendar.getInstance();
        date1.set(Calendar.DAY_OF_WEEK, date1.getFirstDayOfWeek());
        long flowUp = 0 ;
        for (int i=0 ; i<7 ; i++){

            int y = date1.get(Calendar.YEAR);
            int m = date1.get(Calendar.MONTH)+1;
            int d = date1.get(Calendar.DAY_OF_MONTH);
            flowUp +=calculateUp(y, m, d,  netType);
            date1.add(Calendar.DAY_OF_WEEK, 1);

        }
        return flowUp ;
    }



    public long weekDownFloew(int netType){
        Calendar date1 = Calendar.getInstance();//�o��{�b�����
        date1.set(Calendar.DAY_OF_WEEK, date1.getFirstDayOfWeek());//�N����אּ���ѩҦb�P���Ĥ@��
        long flowDw = 0 ;
        for (int i=0 ; i<7 ; i++){

            int y = date1.get(Calendar.YEAR);
            int m = date1.get(Calendar.MONTH)+1;
            int d = date1.get(Calendar.DAY_OF_MONTH);
            flowDw +=calculateDw(y, m, d,  netType);
            date1.add(Calendar.DAY_OF_WEEK, 1);   //date1�[�@��
        }

        return flowDw ;
    }


    public long calculateUpForMonth(int year, int Month, int netType) {
        Cursor lCursor = fetchMonthFlow(year, Month, netType);
        long sum = 0;

        if (lCursor != null) {
            if (lCursor.moveToFirst()) {
                do {
                    int upColumn = lCursor.getColumnIndex("monthUp");
                    sum += lCursor.getLong(upColumn);
                } while (lCursor.moveToNext());
            }
            lCursor.close();
        }
        return sum;
    }

    public long calculateDnForMonth(int year, int Month, int netType) {
        Cursor lCursor = fetchMonthFlow(year, Month, netType);
        long sum =0;

        if (lCursor != null) {
            if (lCursor.moveToFirst()) {
                do {
                    int dwColumn = lCursor.getColumnIndex("monthDw");
                    sum += lCursor.getLong(dwColumn);
                } while (lCursor.moveToNext());
            }
            lCursor.close();
        }
        return sum;
    }

    public long calculateForMonth(int year, int Month, int netType) {
        Cursor lCursor = fetchMonthFlow(year, Month, netType);
        long sum;
        long monthSum = 0;

        if (lCursor != null) {
            if (lCursor.moveToFirst()) {
                do {
                    int upColumn = lCursor.getColumnIndex("monthUp");
                    int dwColumn = lCursor.getColumnIndex("monthDw");
                    sum = lCursor.getLong(upColumn) + lCursor.getLong(dwColumn);
                    monthSum += sum;
                } while (lCursor.moveToNext());
            }
            lCursor.close();
        }
        return monthSum;
    }




    public long calculateUp(int year, int month, int day, int netType) {
        Cursor calCurso = fetchDayFlow(year, month, day, netType);
        long sum = 0;
        if (calCurso != null) {
            if (calCurso.moveToFirst()) {
                do {
                    int upColumn = calCurso.getColumnIndex("sumUp");
                    sum = calCurso.getLong(upColumn);
                } while (calCurso.moveToNext());
            }
        }

        return sum;
    }

    public long calculateDw(int year, int month, int day, int netType) {
        Cursor calCurso = fetchDayFlow(year, month, day, netType);
        long sum = 0;
        if (calCurso != null) {
            if (calCurso.moveToFirst()) {
                do {
                    int dwColumn = calCurso.getColumnIndex("sumDw");
                    sum = calCurso.getLong(dwColumn);
                } while (calCurso.moveToNext());
            }
        }

        return sum;
    }




    public void deleteAll() {
        mSQLiteDatabase.execSQL("DROP TABLE " + TABLE_NAME);
    }

    public void converDate() {

    }
    public void clear(){
        mSQLiteDatabase.delete(TABLE_NAME, null, null);
    }
}

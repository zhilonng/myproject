package com.cn21.speedtest.activity;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.cn21.speedtest.R;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;

/**
 * Created by luwy on 2016/8/16.
 */
public class DbFileActivity extends BaseActivity{

    ListView fileInfo;
    File file;
    @Override
    protected void initView() {
        setContentView(R.layout.dbfilelayout);
        fileInfo=(ListView)findViewById(R.id.tableinfo);

    }
    @Override
    protected void initData() {
        Bundle bundle = getIntent().getExtras();
       // String[] tableInfo=bundle.getStringArray("tableNameArray");
        String filePath=bundle.getString("file");
        file=new File(filePath);

        String tableNames = bundle.getString("tableName");
        String[] tableInfo = tableNames.split("\\s+");
        Log.d("xinxi", tableNames);
        Log.d("xinxi", tableInfo.length + "");

        fileInfo.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_expandable_list_item_1, tableInfo));
        fileInfo.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
              String tableName=(String)fileInfo.getItemAtPosition(i);
                Log.v("表名",tableName);
                selectTable(tableName);
             //   selectTable2(tableName);
            }
        });
    }

    private void selectTable2(String tableName) {
        SQLiteDatabase db=SQLiteDatabase.openOrCreateDatabase(this.getDatabasePath(file.getAbsolutePath()).getAbsolutePath(),null);
        Cursor cursor = db.rawQuery("PRAGMA table_info("+tableName+")", null);
        String name="";
        String[] names=new String[cursor.getCount()];
        int i=0;
        do{
            cursor.moveToNext();
            names[i] = cursor.getString(1);
            Log.i("System.out", names[i]);
            i++;
            name+=names[i];
        } while(!cursor.isLast());
    }

    private void selectTable(String tableName) {
        Process process = null;
        StringBuilder sbReader = new StringBuilder();;
        BufferedWriter bWriter = null;
        InputStream is;
        try {
            process = new ProcessBuilder("su").start();
            bWriter = new BufferedWriter(new OutputStreamWriter(process.getOutputStream()));
            bWriter.write("cd "+file.getParentFile().getAbsolutePath()+"\n");
            bWriter.write("sqlite3 "+file.getName()+"\n");
           // bWriter.write(".tables\n");
            bWriter.write(".mode column\n");
            bWriter.write(".header on\n");
            bWriter.write("select * from "+tableName+";\n");
            bWriter.write("exit\n");
            bWriter.flush();
            bWriter.close();
            is=process.getInputStream();
            String s;
            byte[] bytes=new byte[1024];
            while (is.read(bytes)!=-1){
                s=new String(bytes);
                sbReader.append(s);
            }
            Log.v("shuchu2", sbReader.toString());

        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            process.destroy();
        }
        Intent intent=new Intent(DbFileActivity.this,TableShowActivity.class);
        intent.putExtra("info",sbReader.toString());
        startActivity(intent);
    }


    protected void initEvent() {


    }




}

package com.cn21.speedtest.utils;


import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;

import android.util.Log;


import com.cn21.speedtest.activity.DbFileActivity;


import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


/**
 * Created by luwy on 2016/8/10
 * 根据传进的文件进行类型判断，选择可以得应用供用户打开。
 */
public class OpenFileUtil {
    Context context;
    String extension;
    HashMap<String, String> map;
    ArrayList<HashMap<String, String>> mylist = new ArrayList<HashMap<String, String>>();
    String parentPath;
    File file;

    public OpenFileUtil(Context context) {
        this.context = context;
    }

    public void openFile(File file) {
        parentPath = file.getParentFile().getAbsolutePath();
        this.file = file;
        if (!file.isDirectory()) {
            //获取后缀名
            extension = getExtension(file);
            Log.d("houzhui", extension.equals(".db") + "");
            if (extension.equals(".db")) {
                //sqlite3方式进行查看
              execute(file);
              //sql语句进行查询结构及数据
              //open(file);
            } else {
                Intent intent = new Intent();
                //设置intent的Action属性——查看指定数据
                intent.setAction(Intent.ACTION_VIEW);
                //获取文件file的MIME类型
                String type = getMIMEType(extension);
                //设置intent的data和Type属性。
                intent.setDataAndType(Uri.fromFile(file), type);
                //跳转
                context.startActivity(intent);

            }
        }
    }

    public  void open(File file){
        //context.getDatabasePath(file.getAbsolutePath()).getAbsolutePath()为要打开的数据库名称字符串。只有这样才能打开其他应用数据库
        SQLiteDatabase db=SQLiteDatabase.openOrCreateDatabase(context.getDatabasePath(file.getAbsolutePath()).getAbsolutePath(),null);
        Cursor cursor = db.rawQuery("SELECT name FROM  sqlite_master  WHERE type='table' ORDER BY name;", null);
        String[] names=new String[cursor.getCount()];
        int i=0;
        do{
            cursor.moveToNext();
            names[i] = cursor.getString(0);
            Log.i("System.out", names[i]);
            i++;
        } while(!cursor.isLast());
        Intent intent=new Intent(context,DbFileActivity.class);
        Bundle bundle=new Bundle();
        bundle.putStringArray("tableNameArray",names);
        intent.putExtras(bundle);
        context.startActivity(intent);

    }
    //file 为数据库文件。如mooc.db
    public void execute(File file) {
        Process process = null;
        StringBuilder sbReader = new StringBuilder();
        BufferedWriter bWriter ;
        InputStream is;
        try {
            process = new ProcessBuilder("su").start();
            bWriter = new BufferedWriter(new OutputStreamWriter(process.getOutputStream()));
            bWriter.write("cd "+file.getParentFile().getAbsolutePath()+"\n");
            bWriter.write("sqlite3 "+file.getName()+"\n");
            bWriter.write(".tables\n");
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
            is.close();
            Log.v("shuchu", sbReader.toString());
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
               process.destroy();
        }
        Intent intent=new Intent(context,DbFileActivity.class);
        intent.putExtra("tableName",sbReader.toString());
        intent.putExtra("file",file.getAbsolutePath());
        context.startActivity(intent);
}


    private  String getExtension(File file){
        String fileName=file.getName();
        //获取后缀名前的分隔符"."在fileName中的位置。
        int dotIndex = fileName.lastIndexOf(".");
        //没有分隔符“ 。”
        if(dotIndex < 0){
            return "";
        }
        /* 获取文件的后缀名 */
        String end=fileName.substring(dotIndex,fileName.length()).toLowerCase();
        return  end;
    }
    private String getMIMEType(String extension)
    {
        String type="text/plain";
        if(extension==""){
            return type;
        }
        //在MIME和文件类型的匹配表中找到对应的MIME类型
        for(int i=0;i<MIME_MapTable.length;i++){
            if(extension.equals(MIME_MapTable[i][0]))
                type = MIME_MapTable[i][1];
            }
        return type;
    }

    //建立一个MIME类型与文件后缀名的匹配表
    private final String[][] MIME_MapTable={
            //{后缀名，    MIME类型}
            {".3gp",    "video/3gpp"},
            {".apk",    "application/vnd.android.package-archive"},
            {".asf",    "video/x-ms-asf"},
            {".avi",    "video/x-msvideo"},
            {".bin",    "application/octet-stream"},
            {".bmp",    "image/bmp"},
            {".c",       "text/plain"},
            {".class",   "application/octet-stream"},
            {".conf",    "text/plain"},
            {".cpp",     "text/plain"},
            {".doc",     "application/msword"},
            {".exe",     "application/octet-stream"},
            {".gif",     "image/gif"},
            {".gtar",    "application/x-gtar"},
            {".gz",      "application/x-gzip"},
            {".h",       "text/plain"},
            {".htm",    "text/html"},
            {".html",    "text/html"},
            {".jar",    "application/java-archive"},
            {".java",    "text/plain"},
            {".jpeg",    "image/jpeg"},
            {".jpg",    "image/jpeg"},
            {".js",        "application/x-javascript"},
            {".log",    "text/plain"},
            {".m3u",    "audio/x-mpegurl"},
            {".m4a",    "audio/mp4a-latm"},
            {".m4b",    "audio/mp4a-latm"},
            {".m4p",    "audio/mp4a-latm"},
            {".m4u",    "video/vnd.mpegurl"},
            {".m4v",    "video/x-m4v"},
            {".mov",    "video/quicktime"},
            {".mp2",    "audio/x-mpeg"},
            {".mp3",    "audio/x-mpeg"},
            {".mp4",    "video/mp4"},
            {".mpc",    "application/vnd.mpohun.certificate"},
            {".mpe",    "video/mpeg"},
            {".mpeg",    "video/mpeg"},
            {".mpg",    "video/mpeg"},
            {".mpg4",    "video/mp4"},
            {".mpga",    "audio/mpeg"},
            {".msg",    "application/vnd.ms-outlook"},
            {".ogg",    "audio/ogg"},
            {".pdf",    "application/pdf"},
            {".png",    "image/png"},
            {".pps",    "application/vnd.ms-powerpoint"},
            {".ppt",    "application/vnd.ms-powerpoint"},
            {".prop",    "text/plain"},
            {".rar",    "application/x-rar-compressed"},
            {".rc",      "text/plain"},
            {".rmvb",    "audio/x-pn-realaudio"},
            {".rtf",    "application/rtf"},
            {".sh",      "text/plain"},
            {".tar",    "application/x-tar"},
            {".tgz",    "application/x-compressed"},
            {".txt",    "text/plain"},
            {".wav",    "audio/x-wav"},
            {".wma",    "audio/x-ms-wma"},
            {".wmv",    "audio/x-ms-wmv"},
            {".wps",    "application/vnd.ms-works"},
            {".xml",    "text/xml"},
            {".xml",    "text/plain"},
            {".z",       "application/x-compress"},
            {".zip",    "application/zip"}
    };
}

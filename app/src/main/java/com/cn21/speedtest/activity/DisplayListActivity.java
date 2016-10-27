package com.cn21.speedtest.activity;

import android.app.ListActivity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.cn21.speedtest.R;

import java.io.File;

public class DisplayListActivity extends ListActivity {
    File[] logs;
    String[] logsname;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_list);
        File loglist = new File(Environment.getExternalStorageDirectory().getPath() + "/Goastlogs");
        logs = loglist.listFiles();
        logsname = loglist.list();
        getListView().setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_list_item_activated_1, logsname));

    }
    protected void onListItemClick(ListView l, View v, int position, long id){
        Intent intent = new Intent();
        String type = "text/plain";
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.setAction(Intent.ACTION_VIEW);
        intent.setDataAndType(Uri.fromFile(logs[position]), type);
        startActivity(intent);
    }

}


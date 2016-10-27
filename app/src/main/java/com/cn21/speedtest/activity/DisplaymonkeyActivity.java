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

public class DisplaymonkeyActivity extends ListActivity {
    File[] testresult;
    String[] testname;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_monkey_test);
        File testlist = new File(Environment.getExternalStorageDirectory().getPath() + "/Monkey");
        testresult = testlist.listFiles();
        testname = testlist.list();
        getListView().setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_list_item_activated_1, testname));

    }
    protected void onListItemClick(ListView l, View v, int position, long id){
        Intent intent = new Intent();
        String type = "text/plain";
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.setAction(Intent.ACTION_VIEW);
        intent.setDataAndType(Uri.fromFile(testresult[position]), type);
        startActivity(intent);
    }

}

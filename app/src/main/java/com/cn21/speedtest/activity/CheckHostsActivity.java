package com.cn21.speedtest.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;
import android.view.ContextMenu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import com.cn21.speedtest.R;
import com.cn21.speedtest.adapter.FileInfoAdapter;
import com.cn21.speedtest.utils.ShellUtils;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * 当前所有hosts方案
 * Created by liangzhj on 2016/8/23.
 */
public class CheckHostsActivity extends BaseActivity {
    private List<File> fileList = new ArrayList<File>();
    private ListView listView;
    private FileInfoAdapter adapter;
    private ImageView add;
    private TextView textView;
    @Override
    protected void initView() {
        setContentView(R.layout.fileinfolayout);
        add = (ImageView) findViewById(R.id.add);
        listView = (ListView) findViewById(R.id.listview);
        textView = (TextView) findViewById(R.id.sure);
        textView.setText("设置DNS");
        adapter = new FileInfoAdapter(this,fileList);
        listView.setAdapter(adapter);
    }

    @Override
    protected void initData() {
        super.initData();
        SharedPreferences setting = getSharedPreferences("restrat", 0);
        Boolean user_first = setting.getBoolean("FIRST",true);
        if(user_first){
            setting.edit().putBoolean("FIRST", false).commit();   //第一次启动
            saveOriginalHosts();
        }

    }

    @Override
    protected void initEvent() {
        listView.setOnCreateContextMenuListener(new View.OnCreateContextMenuListener() {
            @Override
            public void onCreateContextMenu(ContextMenu contextMenu, View view, ContextMenu.ContextMenuInfo contextMenuInfo) {
                contextMenu.add(0,1,0,"删除该方案");
                contextMenu.add(0,2,0,"使用该方案");
            }
        });
        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(CheckHostsActivity.this,CreateNewHost.class);
                startActivity(intent);
            }
        });

        textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(CheckHostsActivity.this,ChangeAvtivity.class);
                startActivity(intent);
                showHostList();
            }
        });


    }

    @Override
    protected void onStart() {
        super.onStart();
        showHostList();
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        int id = (int) info.id;
        File file = fileList.get(id);
        switch (item.getItemId()){
            case 1:
                deleteFile(file);
                if (!file.exists())
                    fileList.remove(id);
                adapter.notifyDataSetChanged();
                break;
            case 2:
                String filePath = file.getAbsolutePath();
                List<String> commnandList = new ArrayList<String>();
                commnandList.add("mount -o rw,remount /system");
                commnandList.add("cat "+filePath+" > /etc/hosts");
                commnandList.add("chmod 644 /etc/hosts");
                ShellUtils.CommandResult commandResult = ShellUtils.execCommand(commnandList,true,true);
                if (commandResult.result==0){
                    Toast.makeText(this, "替换成功", Toast.LENGTH_SHORT).show();
                }break;
            default:
                break;
        }
        return super.onContextItemSelected(item);
    }

    private void saveOriginalHosts(){
        String cmd = "mkdir /data/data/com.cn21.speedtest/hostsfile";
        ShellUtils.CommandResult commandResultCreate = ShellUtils.execCommand(cmd,false,true);
        Log.v("信息：",commandResultCreate.errorMsg);
        List<String> commnandList = new ArrayList<String>();
        commnandList.add("mount -o rw,remount /system");
        commnandList.add("cat etc/hosts > /data/data/com.cn21.speedtest/hostsfile/hosts");  //备份
        commnandList.add("chmod 644 /etc/hosts");
        ShellUtils.CommandResult commandResult = ShellUtils.execCommand(commnandList,true,true);
        Log.v("信息：",commandResult.errorMsg);
    }


    private void showHostList(){
        String cmd = "chmod 777 /data/data/com.cn21.speedtest/hostsfile";
        fileList.clear();
        ShellUtils.execCommand(cmd,true);
        File mfile = new File("/data/data/com.cn21.speedtest/hostsfile");
        if (mfile.exists()) {
            if (mfile.isDirectory()) {
                if (mfile.listFiles() != null) {
                    File[] files = mfile.listFiles();
                    for (File file : files) {
                        fileList.add(file);
                    }
                } else {
                    Toast.makeText(this, "文件目录为空", Toast.LENGTH_SHORT).show();
                }
            } else {
                fileList.add(mfile);
            }
        } else {
            Toast.makeText(this, "文件目录不存在", Toast.LENGTH_SHORT).show();
        }
        adapter.notifyDataSetChanged();
        Log.d("更新ListView","更新");
    }

    private void deleteFile(File file) {
        //删除非目录文件及空文件夹
        if (file.isFile()||file.listFiles()==null||file.listFiles().length==0) {
            file.delete();
        }
        //删除非空文件夹
        else{
            for (File file1 : file.listFiles()) {
                deleteFile(file1);
            }
            file.delete();
        }
    }


}

package com.cn21.speedtest.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.Environment;
import android.util.Log;
import android.view.ContextMenu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.cn21.speedtest.R;
import com.cn21.speedtest.adapter.PackageInfoAdapter;
import com.cn21.speedtest.model.PackageClass;
import com.cn21.speedtest.utils.ShellUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * 展示应用信息，提供应用文件查看入口
 */
public class PackageInfoActivity extends BaseActivity {
    PackageManager mPackageManager;
    List<PackageClass> mPackageList;
    ListView mListView;
    String packageName;
    Boolean flag;

    @Override
    protected void initView() {
        setContentView(R.layout.packageinfolayout);
        mListView = (ListView) findViewById(R.id.listview);
    }
    @Override
    protected void initData(){
        mPackageList =new ArrayList<>();
        mPackageManager = getPackageManager();
    };

    @Override
    protected void initEvent() {
        Log.v("xinxi",flag+"");
        //加载类信息并显示到列表
        List<ApplicationInfo> listApplications = mPackageManager.getInstalledApplications(0);
        for (ApplicationInfo app : listApplications) {
            mPackageList.add(setApplication(app));
        }
        mListView.setAdapter(new PackageInfoAdapter(this, mPackageList));
        //注册上下文菜单
        registerForContextMenu(mListView);

    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        menu.setHeaderTitle("选项");
        menu.add(0, 1, 0, "内部存储文件");
        menu.add(0, 2, 0, "外部存储文件");
        menu.add(0, 3, 0, "外部私有文件");
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
           AdapterView.AdapterContextMenuInfo info=(AdapterView.AdapterContextMenuInfo)item.getMenuInfo();
           int id=info.position;
           packageName= mPackageList.get(id).getmPackageName();
        switch (item.getItemId()) {
            case 1:
                if(ShellUtils.checkRootPermission()) {
                    getRootPerms();
                    loadInnerFile();
                }else {
                    showNeedRootDialog();
                }
                break;
            case 2:
                loadOuterFile();
                break;
            case 3:
                loadOuterSelfFile();
                break;
        }
        return false;
    }

    //内部存储
    private  void loadInnerFile(){
        Intent intent=new Intent(this,FileInfoActivity.class);
        intent.putExtra("flag",1);
        intent.putExtra("fileinfo", "/data/data/" + packageName);
        startActivity(intent);

    }
    //外部私有存储
    private void loadOuterSelfFile(){
        File path=this.getExternalFilesDir(null);
        String path2 = path.getAbsolutePath();
        int totallength = path2.length();
        int packagelength = "com.cn21.speedtest".length();
        int filelength = "/files".length();
        int length = totallength - packagelength - filelength;
        String path3 = path2.substring(0, length);
        Intent intent=new Intent(this,FileInfoActivity.class);
        intent.putExtra("flag",2);
        intent.putExtra("fileinfo", path3 + packageName);
        startActivity(intent);
    }

    //外部存储
    private void loadOuterFile(){
        Intent intent=new Intent(this,FileInfoActivity.class);
        intent.putExtra("flag",3);
        intent.putExtra("fileinfo", Environment.getExternalStorageDirectory().toString());
        startActivity(intent);
    }

    private void getRootPerms() {
        Process  process=null;
        try {
            process=new ProcessBuilder("su").start();
        } catch (Exception e) {
        }finally {
        }
    }

    /**
     * 需要root权限
     */
    private void showNeedRootDialog() {
        android.support.v7.app.AlertDialog.Builder builder =
                new android.support.v7.app.AlertDialog.Builder(this);
        builder.setTitle(R.string.fps_needroot);
        builder.setMessage(R.string.fps_message);
        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });
        builder.show();
    }

    private PackageClass setApplication(ApplicationInfo app) {
        PackageClass packageClass = new PackageClass();
        packageClass.setmIcon(app.loadIcon(mPackageManager));
        packageClass.setmLable((String) app.loadLabel(mPackageManager));
        packageClass.setmPackageName(app.packageName);
        return packageClass;
    }

}

package com.cn21.speedtest.fragment;

import android.content.ComponentName;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.cn21.speedtest.R;
import com.cn21.speedtest.adapter.AppAdapter;
import com.cn21.speedtest.model.AppInfo;
import com.cn21.speedtest.model.DefaultApplication;
import com.cn21.speedtest.utils.GetAppInfo;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by 梁照江 on 2016/8/9.
 */
public class ProcessFragment extends BaseFragment{

    private ListView listView;
    private List<AppInfo> appInfoList = new ArrayList<AppInfo>();
    private AppAdapter adapter;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_process_running, container, false);
        listView = (ListView) rootView.findViewById(R.id.process_list_view);
        appInfoList.clear();
        adapter= new AppAdapter(getActivity().getApplicationContext(),R.layout.app_item,appInfoList);
        listView.setAdapter(adapter);
        new Thread(new Runnable() {
            @Override
            public void run() {
                for(int i = 0; i<DefaultApplication.allAppInfo.size();i++){
                    if (DefaultApplication.allAppInfo.get(i).getFlagRunning()){
                        appInfoList.add(DefaultApplication.allAppInfo.get(i));
                        Log.d("被添加","添加成功");
                    }
                }
                myHandle.sendEmptyMessage(1);
            }
        }).start();
        return rootView;
    }

    @Override
    protected View initView() {
        return null;
    }

    private Handler myHandle = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            int msgId = msg.what;
            switch (msgId){
                case 1:

                    adapter.notifyDataSetChanged();
                    listView.setOnCreateContextMenuListener(new View.OnCreateContextMenuListener(){
                        @Override
                        public void onCreateContextMenu(ContextMenu contextMenu, View view, ContextMenu.ContextMenuInfo contextMenuInfo) {
                            contextMenu.setHeaderTitle("选择你的操作");
                            contextMenu.add(0,0,0,"打开");
                            contextMenu.add(0,1,0,"删除");
                            contextMenu.add(0,2,0,"设置为测试应用");
                        }
                    });break;
            }
        }
    };

    public boolean onContextItemSelected(MenuItem item) {
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        AppInfo appInfo = appInfoList.get(info.position);    //此处为你所长按点击那个列表单元  数据从这直接获取 里面又方法
        switch (item.getItemId()){
            case 0:
                openApp(appInfo.getPkgName());break;
            case 1:
                unInstallApp(appInfo.getPkgName());
                appInfoList.remove(info.position);
                adapter.notifyDataSetChanged();break;
            case 2:
                GetAppInfo.setDefaultApplication(appInfo);
            default:
                break;
        }
        return super.onContextItemSelected(item);
    }

    public void unInstallApp (String pkgName){
        if(haveRootPerssion())
            clientUninstall(pkgName);
        else {
            Uri uri = Uri.fromParts("package", pkgName, null);
            Intent intent = new Intent(Intent.ACTION_DELETE, uri);
            startActivity(intent);
        }
    }

    private void openApp(String packageName) {
        PackageManager packageManager = getActivity().getPackageManager();
        Intent resolveIntent = new Intent(Intent.ACTION_MAIN, null);
        resolveIntent.addCategory(Intent.CATEGORY_LAUNCHER);
        resolveIntent.setPackage(packageName);
        List<ResolveInfo> apps = packageManager.queryIntentActivities(resolveIntent, 0);
        if(apps.size()==0){
            Toast.makeText(getActivity(),"你选择的应用只有后台服务",Toast.LENGTH_SHORT).show();
            return;
        }
        ResolveInfo ri = apps.iterator().next();
        if (ri != null ) {
            //       String mPackageName = ri.activityInfo.packageName;
            String className = ri.activityInfo.name;
            Intent intent = new Intent(Intent.ACTION_MAIN);
            intent.addCategory(Intent.CATEGORY_LAUNCHER);
            ComponentName cn = new ComponentName(packageName, className);
            intent.setComponent(cn);
            startActivity(intent);
        }
    }
    private boolean haveRootPerssion(){
        PrintWriter printWriter = null;
        Process process = null;
        try{
            process = Runtime.getRuntime().exec("su");
            printWriter = new PrintWriter(process.getOutputStream());
            printWriter.flush();
            printWriter.close();
            int value = process.waitFor();
            if (value == 0)
                return true;
            else
                return false;
        }catch (Exception e){
            e.printStackTrace();
        }
        finally {
            if (process!=null)
                process.destroy();
        }
        return false;
    }
    private static boolean clientUninstall(String packageName){
        PrintWriter printWriter = null;
        Process process = null;
        try {
            process = Runtime.getRuntime().exec("su");
            printWriter = new PrintWriter(process.getOutputStream());
            printWriter.println("LD_LIBRARY_PATH=/vendor/lib:/system/lib ");
            printWriter.println("pm uninstall "+packageName);
            printWriter.flush();
            printWriter.close();
            int value = process.waitFor();
            if (value == 0 )
                return true;
            else
                return false;
        } catch (Exception e) {
            e.printStackTrace();
        }finally{
            if(process!=null){
                process.destroy();
            }
        }
        return false;
    }

    @Override
    public void onStart() {
        super.onStart();
        appInfoList.clear();
        for(int i = 0; i<DefaultApplication.allAppInfo.size();i++){
            if (DefaultApplication.allAppInfo.get(i).getFlagRunning()){
                appInfoList.add(DefaultApplication.allAppInfo.get(i));
                Log.d("被添加","添加成功");
            }
        }
        adapter.notifyDataSetChanged();
        Log.d("startFragment","fang");
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.d("ResumeFragment","f");
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if (hidden){
            onStart();
        }
    }
}

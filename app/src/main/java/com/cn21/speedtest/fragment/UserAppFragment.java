package com.cn21.speedtest.fragment;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
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
import com.cn21.speedtest.R;
import com.cn21.speedtest.adapter.AppAdapter;
import com.cn21.speedtest.model.AppInfo;
import com.cn21.speedtest.model.DefaultApplication;
import com.cn21.speedtest.utils.GetAppInfo;

import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

/**
 * 显示用户应用
 * Created by 梁照江 on 2016/8/9.
 */
public class UserAppFragment extends BaseFragment {
    private ListView listView;
    private List<AppInfo> appInfoList = new ArrayList<AppInfo>();
    private AppAdapter adapter;
    private int packageNumber = -1;
    private UninstallReceiver uninstallReceiver;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    protected View initView() {
        return null;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_user_app,container,false);
        listView = (ListView) rootView.findViewById(R.id.user_list_view);
        uninstallReceiver = new UninstallReceiver();
        IntentFilter intentFilter = new IntentFilter(Intent.ACTION_PACKAGE_REMOVED);
        getActivity().registerReceiver(uninstallReceiver,intentFilter);
        appInfoList.clear();
        new Thread(new Runnable() {
            @Override
            public void run() {
                if(DefaultApplication.allAppInfo.size()==0){
                    GetAppInfo.getAllappInfo(getActivity());
                }
                for(int i = 0;i<DefaultApplication.allAppInfo.size();i++){
                    if (!DefaultApplication.allAppInfo.get(i).getFlagSystem()){
                        appInfoList.add(DefaultApplication.allAppInfo.get(i));
                    }
                }
                myHandle.sendEmptyMessage(1);
            }
        }).start();


        return rootView;
    }

    private Handler myHandle = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                case 1:
                    adapter = new AppAdapter(getActivity().getApplicationContext(), R.layout.app_item, appInfoList);
                    listView.setAdapter(adapter);
                    listView.setOnCreateContextMenuListener(new View.OnCreateContextMenuListener() {
                        @Override
                        public void onCreateContextMenu(ContextMenu contextMenu, View view, ContextMenu.ContextMenuInfo contextMenuInfo) {
                            contextMenu.setHeaderTitle("选择你的操作");
                            contextMenu.add(0,0,0,"打开该应用");
                            contextMenu.add(0,1,0,"删除该应用");
                        }
                    });break;
            }
        }
    };

    public void unInstallApp (String pkgName){
        if (haveRootPerssion())
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

    public boolean onContextItemSelected(MenuItem item) {
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        AppInfo appinfo = appInfoList.get(info.position);
        switch (item.getItemId()){
            case 0:
                openApp(appinfo.getPkgName());break;
            case 1:
                unInstallApp(appinfo.getPkgName());
                packageNumber = info.position;
                adapter.notifyDataSetChanged();break;
            default:
                break;
        }
        return super.onContextItemSelected(item);
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
 //       adapter.notifyDataSetChanged();
    }

    private class UninstallReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            Log.d("Myaction",action);
            Log.d("packitem",Integer.toString(packageNumber));
            appInfoList.remove(packageNumber);
            adapter.notifyDataSetChanged();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        getActivity().unregisterReceiver(uninstallReceiver);
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if (!hidden){
            appInfoList.clear();
            for(int i = 0;i<DefaultApplication.allAppInfo.size();i++){
                if (!DefaultApplication.allAppInfo.get(i).getFlagSystem()){
                    appInfoList.add(DefaultApplication.allAppInfo.get(i));
                }
            }
            adapter.notifyDataSetChanged();

        }
    }
}

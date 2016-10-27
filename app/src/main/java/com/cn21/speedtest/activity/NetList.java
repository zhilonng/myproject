package com.cn21.speedtest.activity;

import android.app.Activity;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.net.TrafficStats;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.cn21.speedtest.R;
import com.cn21.speedtest.model.Programme;
import com.cn21.speedtest.utils.TrafficMonitoring;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by lenovo on 2016/8/17.
 */
public class NetList extends Activity {

    //Use ArrayList to store the installed non-system apps
    ArrayList<Programme> appList = new ArrayList<Programme>();
    //ListView app_listView;
    /** Called when the activity is first created. */

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.net_list);


        ListView viewSnsLayout = (ListView)findViewById(R.id.listview);
        viewSnsLayout.setLongClickable(true);

        getList();
        ListView app_listView=(ListView)findViewById(R.id.listview);
        AppAdapter appAdapter=new AppAdapter(NetList.this,appList);
        app_listView.setAdapter(appAdapter);

    }

    public void getList(){

        // List<ApplicationInfo> applications = getPackageManager().getInstalledApplications(PackageManager.GET_UNINSTALLED_PACKAGES);
        List<ApplicationInfo> applications = getPackageManager().getInstalledApplications(PackageManager.GET_META_DATA);
        for(int i=0;i<applications.size();i++) {
            ApplicationInfo application = applications.get(i);


            int uid = application.uid;

            if (application.processName.equals("system")
                    || application.processName.equals("com.android.phone")) {
                continue;
            }
            if (monitoringEachApplicationSend( uid)==0&&monitoringEachApplicationReceive(uid)==0){
                continue;
            }
            Programme Programme = new Programme();
            Programme.setName( application.packageName);
            Programme.setUid(uid);
            // Log.e("uid",Programme.getUid()+"");
            Programme.setIcon(application.loadIcon(getPackageManager()));
            Programme.setReceive(monitoringEachApplicationReceive(uid));
            //Log.e("Re",Programme.getReceive()+"");
            Programme.setSend(monitoringEachApplicationSend( uid)) ;
            //Log.e("Se",Programme.getSend()+"");
            appList.add(Programme);
        }

    }

    public class AppAdapter extends BaseAdapter {

        Context context;
        ArrayList<Programme> dataList=new ArrayList<Programme>();
        public AppAdapter(Context context,ArrayList<Programme> inputDataList)
        {
            this.context=context;
            dataList.clear();
            for(int i=0;i<inputDataList.size();i++)
            {
                dataList.add(inputDataList.get(i));
            }
        }

        public int getCount() {
            // TODO Auto-generated method stub
            return dataList.size();
        }


        public Object getItem(int position) {
            // TODO Auto-generated method stub
            return dataList.get(position);
        }


        public long getItemId(int position) {
            // TODO Auto-generated method stub
            return position;
        }


        public View getView(int position, View convertView, ViewGroup parent) {

            View v=convertView;
            final Programme appUnit=dataList.get(position);
            if(v==null)
            {
                LayoutInflater vi=(LayoutInflater)getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                v=vi.inflate(R.layout.app_row, null);
            }

            TextView appName=(TextView)v.findViewById(R.id.appName);
            TextView appRecieve=(TextView)v.findViewById(R.id.app_recieve);
            TextView appSend=(TextView)v.findViewById(R.id.app_send);
            TextView appTotal=(TextView)v.findViewById(R.id.app_total);
            ImageView appIcon=(ImageView)v.findViewById(R.id.icon);
            if(appName!=null)
                appName.setText(appUnit.getName());
            if(appIcon!=null)
                appIcon.setImageDrawable(appUnit.getIcon());
            if(appRecieve!=null)
                appRecieve.setText(TrafficMonitoring.convertTraffic(appUnit.getReceive()));

            if(appSend!=null)
                appSend.setText(TrafficMonitoring.convertTraffic(appUnit.getSend()));
            Log.e("APPSEND",(TrafficMonitoring.convertTraffic(appUnit.getSend())));
            if(appTotal!=null)
                appTotal.setText(TrafficMonitoring.convertTraffic(appUnit.getReceive()+appUnit.getSend()));
            return v;
        }
    }

    public static long monitoringEachApplicationReceive(int uid) {
        long   receive= TrafficStats.getUidRxBytes(uid);
        if(receive==-1)receive=0;
        return receive;
    }

    public static long monitoringEachApplicationSend(int uid) {
        long send=TrafficStats.getUidTxBytes(uid);
        if(send==-1)send=0;
        return send;
    }
    public void DisplayToast(String str){
        Toast.makeText(this,str,Toast.LENGTH_SHORT).show();
    }

    public boolean onKeyDown(int keyCode, KeyEvent event) {

        if (keyCode == KeyEvent.KEYCODE_BACK) {

            NetList.this.finish();
        }
        return true;
    }
}
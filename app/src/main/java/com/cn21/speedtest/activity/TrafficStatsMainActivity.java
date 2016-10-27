package com.cn21.speedtest.activity;

/**
 * Created by lenovo on 2016/8/16.
 */

import android.app.Activity;
import android.content.pm.ApplicationInfo;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.TrafficStats;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.ListView;
import android.widget.TextView;

import com.cn21.speedtest.R;
import com.cn21.speedtest.model.ApplicationItem;

import java.util.Comparator;


/**
 * Created by lenovo on 2016/8/10.
 */
public class TrafficStatsMainActivity extends Activity {
    private TextView tvSupported, tvDataUsageWiFi, tvDataUsageMobile, tvDataUsageTotal;
    private ListView lvApplications;

    private long dataUsageTotalLast = 0;

    ArrayAdapter<ApplicationItem> adapterApplications;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.trafficactivity_main);

        tvSupported = (TextView) findViewById(R.id.tvSupported);
        tvDataUsageWiFi = (TextView) findViewById(R.id.tvDataUsageWiFi);
        tvDataUsageMobile = (TextView) findViewById(R.id.tvDataUsageMobile);
        tvDataUsageTotal = (TextView) findViewById(R.id.tvDataUsageTotal);

        if (TrafficStats.getTotalRxBytes() != TrafficStats.UNSUPPORTED && TrafficStats.getTotalTxBytes() != TrafficStats.UNSUPPORTED) {
            handler.postDelayed(runnable, 0);


            initAdapter();
            lvApplications = (ListView) findViewById(R.id.lvInstallApplication);
            lvApplications.setAdapter(adapterApplications);
        } else {
            tvSupported.setVisibility(View.VISIBLE);
        }
    }

    public Handler handler = new Handler();
    public Runnable runnable = new Runnable() {
        public void run() {
            long mobile = TrafficStats.getMobileRxBytes() + TrafficStats.getMobileTxBytes();
            long total = TrafficStats.getTotalRxBytes() + TrafficStats.getTotalTxBytes();
            tvDataUsageWiFi.setText("" + (total - mobile) / 1024 + "KB");
            tvDataUsageMobile.setText("" + mobile / 1024 + " KB");
            tvDataUsageTotal.setText("" + total / 1024 + " KB");
            if (dataUsageTotalLast != total) {
                dataUsageTotalLast = total;
                updateAdapter();
            }
            handler.postDelayed(runnable, 5000);
        }
    };

    public void initAdapter(){

        adapterApplications = new ArrayAdapter<ApplicationItem>(getApplicationContext(), R.layout.traffic_item) {
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                ApplicationItem app = getItem(position);

                final View result;
                if (convertView == null) {
                    result = LayoutInflater.from(parent.getContext()).inflate(R.layout.traffic_item, parent, false);
                } else {
                    result = convertView;
                }

                TextView tvAppName = (TextView) result.findViewById(R.id.tvAppName);
                TextView tvAppTraffic = (TextView) result.findViewById(R.id.tvAppTraffic);

                // TODO: resize once
                final int iconSize = Math.round(32 * getResources().getDisplayMetrics().density);
                tvAppName.setCompoundDrawablesWithIntrinsicBounds(
                        //app.icon,
                        new BitmapDrawable(getResources(), Bitmap.createScaledBitmap(
                                ((BitmapDrawable) app.getIcon(getApplicationContext().getPackageManager())).getBitmap(), iconSize, iconSize, true)
                        ),
                        null, null, null
                );
                tvAppName.setText(app.getApplicationLabel(getApplicationContext().getPackageManager()));
                //   tvAppTraffic.setText(Integer.toString(app.getTotalUsageKb()) + " Kb");
                tvAppTraffic.setText("接收流量"+Integer.toString(app.getRKB())+"KB"+"\n"+"发送流量"+Integer.toString(app.getTKB())+"KB"+"\n"+"接收数据流量"+Integer.toString(app.getMobileRKB())+"KB"+"\n"
                        +"发送数据流量"+Integer.toString(app.getMobileTKB())+"KB"+"\n"+"接收WIFI流量"+Integer.toString(app.getWiFiRKB())+"KB"+"\n"+"发送WIFI流量"+Integer.toString(app.getWiFiTKB())+"KB");
                return result;
            }
            @Override
            public int getCount() {
                return super.getCount();
            }

            @Override
            public Filter getFilter() {
                return super.getFilter();
            }
        };

// TODO: resize icon once
        for (ApplicationInfo app : getApplicationContext().getPackageManager().getInstalledApplications(0)) {
            ApplicationItem item = ApplicationItem.create(app);
            if(item != null) {
                adapterApplications.add(item);
            }
        }
    }

    public void updateAdapter() {
        for (int i = 0, l = adapterApplications.getCount(); i < l; i++) {
            ApplicationItem app = adapterApplications.getItem(i);
            app.update();
        }

        adapterApplications.sort(new Comparator<ApplicationItem>() {
            @Override
            public int compare(ApplicationItem lhs, ApplicationItem rhs) {
                return (int)(rhs.getTotalUsageKb() - lhs.getTotalUsageKb());
            }
        });
        adapterApplications.notifyDataSetChanged();
    }
}

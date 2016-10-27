package com.cn21.speedtest.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SeekBar;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import com.cn21.speedtest.R;
import com.cn21.speedtest.model.DefaultApplication;
import com.cn21.speedtest.utils.GetAppInfo;
import com.cn21.speedtest.utils.User;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * @author shenpeng email:sx_shenp@corp.21cn.com
 *         时间;2016年8月4日
 * @Description:主页
 */
public class MainActivity extends BaseActivity {
    private ListView listView;
    private ImageView iv_setting;
    private SimpleAdapter myGridAdapter;
    private ArrayList<HashMap<String, Object>> arrayList = new ArrayList<>();
    private static int time_interval=1;
    private String[] img_text = {"应用管理", "通讯录","流量管理", "设备信息", "文件管理", "监控项", "日志分析", "Monkey","hosts与DNS设置", "设置" ,"权限管理","图表分析", "便签","ANR信息"};
    private int[] imgs = {R.drawable.app_battle, R.drawable.app_best, R.drawable.app_boat, R.drawable.app_common,
            R.drawable.app_dream_world,  R.drawable.app_badge, R.drawable.app_green_bird, R.drawable.app_worthy,
            R.drawable.app_tortise, R.drawable.app_orc, R.drawable.app_love, R.drawable.app_lottery, R.drawable.app_hide,R.drawable.app_badge};
    private String[] content = {"查看设备系统应用","添加／修改通讯录", "读取当前、历史流量","获取当前设备信息", "部分功能需root,管理本地文件", "部分功能需root，监控cpu、fps、内存、耗电量", "需root，log日志分析", "需root，Monkey工具",
               "需root，修改hosts与DNS", "需root，设置时间、网速限制、截图", "the description of the model","the description of the model", "the description of the model" ,"the description of the model"};

    //初始化VIEW
    protected void initView() {
        setContentView(R.layout.main_activity_layout);
        listView = (ListView) findViewById(R.id.listView);
        iv_setting = (ImageView)findViewById(R.id.right_icon);
    }

    //初始化数据
    @Override
    protected void initData() {
        for(int i=0;i<img_text.length;i++){
            HashMap<String, Object> map = new HashMap<>();
            map.put("img",imgs[i]);
            map.put("text",img_text[i]);
            map.put("content",content[i]);
            arrayList.add(map);
        }
        myGridAdapter = new SimpleAdapter(mContext, arrayList, R.layout.grid_item, new String[]{"img", "text", "content"}, new int[]{R.id.iv_item, R.id.tv_item, R.id.tv_content});
        listView.setAdapter(myGridAdapter);
        iv_setting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDialog();
            }
        });
        new Thread(new Runnable() {
            @Override
            public void run() {
                GetAppInfo.getAllappInfo(getContext());
                Intent intent = new Intent();
                if (DefaultApplication.loadFinish == false) {
                    intent.setAction("com.cn21.loadfinish");
                    getContext().sendBroadcast(intent);
                    Log.d("发送广播", "done");
                }
                DefaultApplication.loadFinish = true;
            }
        }).start();
    }

    //初始化事件
    protected void initEvent() {
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                Map map = arrayList.get(position);
                //跳转到内存管理模块，@author:shenpeng
                /**
                 * @author:shenpeng
                 * 各模块添加接口，listView和GridView都要添加
                 * 示例： if(map.get("text").equals("通讯录")){
                Intent intent = new Intent(mContext, BrowseProcessInfoActivity.class);
                startActivity(intent);
                }
                 */
                if(map.get("text").equals("流量管理")){
                    Intent intent = new Intent(mContext,NetworkMonitor.class);
                    startActivity(intent);
                }
                if(map.get("text").equals("通讯录")){
                    Intent intent = new Intent(mContext,ContactMainActivity.class);
                    startActivity(intent);
                }
                if (map.get("text").equals("应用管理")) {
                    Intent intent = new Intent(mContext, ProcessActivity.class);
                    startActivity(intent);
                }

                if (map.get("text").equals("设备信息")) {
                    Intent intent = new Intent(mContext, DeviceInfoActivity.class);
                    startActivity(intent);
                }
                if (map.get("text").equals("文件管理")) {
                    Intent intent = new Intent(mContext, PackageInfoActivity.class);
                    startActivity(intent);
                }
                if (map.get("text").equals("监控项")) {
                    Intent intent = new Intent(mContext, CpuMemoryActivity.class);
                    startActivity(intent);
                }
                if (map.get("text").equals("日志分析")) {
                    Intent intent = new Intent(mContext, LogcatActivity.class);
                    startActivity(intent);
                }
                if (map.get("text").equals("Monkey")) {
                    Intent intent = new Intent(mContext, MonkeyTestActivity.class);
                    startActivity(intent);
                }
                if (map.get("text").equals("设置")) {
                    Intent intent = new Intent(mContext, SetInfoActivity.class);
                    startActivity(intent);
                }
                if (map.get("text").equals("hosts设置")){
                    Intent intent = new Intent(mContext,CheckHostsActivity.class);
                    startActivity(intent);
                }
                if(map.get("text").equals("hosts与DNS设置")){
                    Intent intent = new Intent(mContext,CheckHostsActivity.class);
                    startActivity(intent);
                }
            }
        });
    }

    /**
     * 提示框
     */
    private void showDialog() {
        LayoutInflater factory = LayoutInflater.from(getContext());
        View view =factory.inflate(R.layout.alertdialog_view,null);
        android.support.v7.app.AlertDialog.Builder builder =
                new android.support.v7.app.AlertDialog.Builder(getContext());
        builder.setView(view);
        final SeekBar timeSeek = (SeekBar)view.findViewById(R.id.seekBar);
        final TextView tv_time = (TextView)view.findViewById(R.id.tv_time);
        timeSeek.setMax(4);
        timeSeek.setProgress(time_interval - 1);
        tv_time.setText(String.valueOf(time_interval)+"s");
        timeSeek.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                tv_time.setText((progress+1)+"s");
                time_interval = progress+1;
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });
        builder.setPositiveButton("设置", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                User.thread_time_interval = time_interval * 1000;
                if (User.totalCpuRate != null) {
                    for (int i = 0; i < User.totalCpuRate.size(); i++) {
                        User.totalCpuRate.get(i).clear();
                    }
                }
            }
        });
        builder.show();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    //程序退出时，释放资源，@author：shenpeng
    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}

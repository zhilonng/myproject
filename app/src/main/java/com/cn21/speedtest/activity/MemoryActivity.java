package com.cn21.speedtest.activity;

import android.app.ActivityManager;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.cn21.speedtest.R;
import com.cn21.speedtest.adapter.BrowseMemoryAdapter;
import com.cn21.speedtest.model.DefaultApplication;
import com.cn21.speedtest.model.Programe;
import com.cn21.speedtest.service.CpuReaderService;
import com.cn21.speedtest.utils.ProcessUtil;
import com.cn21.speedtest.utils.User;

import java.util.ArrayList;
import java.util.List;

/**
 * @author shenpeng email:sx_shenp@corp.21cn.com
 * 时间;2016年8月10日
 * @Description:内存监控模块
 */
public class MemoryActivity extends BaseActivity {
    private ListView listViewProcess;
    private TextView tvTotalProcessNo;
    private ActivityManager mActivityManager;
    private List<Programe> programeList = null;
    static int PID;
    View view=null;
    String flag;
    int i = 0;//平移图片
    public static final int GET_PROCESS_FINISH = 2;
    private Handler myHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            int msgId = msg.what;
            switch (msgId) {
                case GET_PROCESS_FINISH:
                    BrowseMemoryAdapter mprocessInfoAdapter = new BrowseMemoryAdapter(MemoryActivity.this, programeList);
                    listViewProcess.setAdapter(mprocessInfoAdapter);
                    tvTotalProcessNo.setText("当前系统进程共有：" + programeList.size() + "个");
                    break;
            }
        }
    };

    //初始化View
    @Override
    protected void initView() {
        setContentView(R.layout.browse_process_list);
        tvTotalProcessNo = (TextView) findViewById(R.id.tvTotalProcessNo);
        listViewProcess = (ListView) findViewById(R.id.listviewProcess);
        this.registerForContextMenu(listViewProcess);
        mActivityManager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);

    }

    //初始化数据
    @Override
    protected void initData() {
        programeList = new ArrayList<Programe>();
//        memSize = new ArrayList();
        getRunningAppProcessInfo();
        Intent intent1 = this.getIntent();
        flag = intent1.getStringExtra("Cpu");
    }
    //初始化事件
    @Override
    protected void initEvent() {
        //获取点击应用的PID，并监控点击应用内存，author：shenp
        listViewProcess.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, final int position, long l) {
                if (flag !=null){
                    if (flag.equals("true")) {
                            PID = programeList.get(position).getPid();
                        String PAKAGE = programeList.get(position).getProcessName();
                        if (PAKAGE.equals("sh")) {
                            Toast.makeText(MemoryActivity.this, "choose other", Toast.LENGTH_LONG).show();
                        } else {
                            if (PAKAGE.equals("toolbox")) {
                                Toast.makeText(MemoryActivity.this, "choose other", Toast.LENGTH_LONG).show();
                            } else {
                                    for (int i=0;i<User.totalCpuRate.size();i++){
                                        User.totalCpuRate.get(i).clear();
                                    }
                                    User.inChoosePakageName = PAKAGE;
                                    DefaultApplication.setPkgName(PAKAGE);
                                    User.inChooseProcessPid = PID;
                                    CpuReaderService.runningCpu = true;
                                    CpuReaderService.runningMemory = true;
                                    CpuReaderService.isFirst = 0;
                                    Intent intent = new Intent("com.cn21.speedtest.MemoryAct2AppHolder");
                                    view.getContext().sendBroadcast(intent);
                                    finish();
                            }
                        }
                    } }else {
                    PID = programeList.get(position).getPid();
                    Intent intent = new Intent(mContext, MemoryView.class);
                    Bundle bundle = new Bundle();
                    bundle.putInt("PID", PID);
                    intent.putExtras(bundle);
                    startActivity(intent);
                }
            }
        });
        //长按弹出对话框，用于杀死进程，author：shenp
        listViewProcess.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, final int position, long l) {
                new AlertDialog.Builder(mContext).setMessage("是否杀死该进程").setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int i) {
                        mActivityManager.killBackgroundProcesses(programeList.get(position).getPackageName());
                        getRunningAppProcessInfo();
                        BrowseMemoryAdapter mprocessInfoAdapter = new BrowseMemoryAdapter(mContext, programeList);
                        listViewProcess.setAdapter(mprocessInfoAdapter);
                        tvTotalProcessNo.setText("当前系统进程共有：" + programeList.size() + "个");
                    }
                }).setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int i) {
                        dialog.cancel();
                    }
                }).create().show();
                return false;
            }
        });
    }
    /**
     * 获取正在运行的应用
     */
    public void getRunningAppProcessInfo() {

        final List<Programe> programes = new ArrayList<>();
        new Thread(new Runnable() {
            @Override
            public void run() {
                List<ProcessUtil.Process> processes = ProcessUtil.getRunningProcesses();
//                LogUtil.e(processes.get(0) + "shenpeng");
                for (ProcessUtil.Process p : processes) {
//                    LogUtil.e(processes.get(0) + "shenpeng2");
                    int pid = p.pid;
                    int uid = p.uid;
                    int cpu=p.cpu;
                    String packageName = p.getPackageName();
                    double memSize = p.vsize/(1024);
                    //去掉空packagename
                    if (packageName == null) continue;
                    Programe programe = new Programe();
                    programe.setPid(pid);
                    programe.setUid(uid);
                    programe.setCpu(cpu);
                    programe.setMemSize(memSize);
                    programe.setProcessName(packageName);
                    programes.add(programe);
                }
                programeList=programes;
                myHandler.sendEmptyMessage(GET_PROCESS_FINISH);
            }
        }).start();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    //关闭线程，回收资源
    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
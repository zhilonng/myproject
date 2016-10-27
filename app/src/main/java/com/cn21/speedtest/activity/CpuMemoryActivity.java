package com.cn21.speedtest.activity;

import android.app.ActivityManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PixelFormat;
import android.net.Uri;
import android.support.v7.widget.ActionMenuView;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.OrientationHelper;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.WindowManager;
import android.widget.Toast;

import com.cn21.speedtest.R;
import com.cn21.speedtest.adapter.CpuMemoryRecyclerAdapter;
import com.cn21.speedtest.service.CpuReaderService;
import com.cn21.speedtest.utils.ExcelUtil;
import com.cn21.speedtest.utils.LogUtil;
import com.cn21.speedtest.utils.ShellUtils;
import com.cn21.speedtest.utils.User;
import com.cn21.speedtest.view.FloatingWindows;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by huangzhilong on 16/8/9.
 * content:cpu占用率
 */
public class CpuMemoryActivity extends BaseActivity {
    private RecyclerView list_recycler;
    private CpuMemoryRecyclerAdapter recycleAdapter;
    private List<String> mDatas;
    private Toolbar toolbar;
    ActionMenuView actionMenuView;

    //浮动窗口
    ActivityManager activityManager;
    WindowManager windowManager;
    WindowManager.LayoutParams  layoutParams;
    FloatingWindows myLayout;

    @Override
    protected void initView() {
        setContentView(R.layout.activity_cpu_memory);
        list_recycler = (RecyclerView)findViewById(R.id.list_recycler);
        toolbar = (Toolbar)findViewById(R.id.toolbar);
        actionMenuView = (ActionMenuView)findViewById(R.id.action_menu_view);
    }

    @Override
    protected void initEvent() {
        User.documents.clear();
        //toolbar设置
        generateFloatingWindows();
        settoolbar();
        //检查User.totalCpuRate长度是否为4
        for (int i=User.totalCpuRate.size();i<4;i++){
            User.totalCpuRate.add(new ArrayList<Float>());
        }

        //开启CpuReaderService服务
        if (!isServiceRunning(getContext(),"CpuReaderService")) {
            Intent startIntent = new Intent(this, CpuReaderService.class);
            startService(startIntent);
        }
        initData1();
        recycleAdapter = new CpuMemoryRecyclerAdapter(CpuMemoryActivity.this , mDatas);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        //设置布局管理器
        list_recycler.setLayoutManager(layoutManager);
        //设置为垂直布局，这也是默认的
        layoutManager.setOrientation(OrientationHelper.VERTICAL);
        //设置Adapter
        list_recycler.setAdapter( recycleAdapter);
        //设置增加或删除条目的动画
        list_recycler.setItemAnimator(new DefaultItemAnimator());
    }

    /**
     * 初始化toolbar
     */
    private void settoolbar() {
        toolbar.setTitle("监测项");
        toolbar.setTitleTextColor(Color.WHITE);
        toolbar.inflateMenu(R.menu.chart_toolbar_menu);
        toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                int menuItemId = item.getItemId();
                switch (menuItemId){
                    case R.id.action_item1 :
                        LogUtil.e("CHOOSE CPU");
                        //cpu
                        if (CpuReaderService.runningCpu){
                            CpuReaderService.runningCpu = false;
                            User.totalCpuRate.get(CpuReaderService.cpu_id).clear();
                        } else CpuReaderService.runningCpu = true;
                        break;
                    case R.id.action_item2:
                        //fps
                        if(ShellUtils.checkRootPermission()) {
                            LogUtil.e("CHOOSE FPS");
                            if (CpuReaderService.runningFps){
                                try {
                                    //移除浮动窗口
                                    windowManager.removeView(myLayout);
                                }catch (Exception e){
                                }
                                CpuReaderService.runningFps = false;
                                User.totalCpuRate.get(CpuReaderService.fps_id).clear();
                            }else {
                                //添加浮动窗口
                                windowManager.addView(myLayout, layoutParams);
                                CpuReaderService.runningFps = true;
                            }
                        }else showNeedRootDialog();
                        break;
                    case R.id.action_item3:
                        //memory
                        LogUtil.e("CHOOSE MEMORY");
                        if (CpuReaderService.runningMemory){
                            CpuReaderService.runningMemory = false;
                            User.totalCpuRate.get(CpuReaderService.memory_id).clear();
                        } else CpuReaderService.runningMemory = true;
                        break;
                    case  R.id.action_item4 :
                        //耗电量
                        //fps
                        if(ShellUtils.checkRootPermission()) {
                            LogUtil.e("CHOOSE FPS");
                            if (CpuReaderService.runningPower){
                                CpuReaderService.runningPower = false;
                                User.totalCpuRate.get(CpuReaderService.power_id).clear();
                            }else CpuReaderService.runningPower = true;
                        }else showNeedRootDialog();
                        break;
                    case R.id.item_save :
                        //生成excel
                        try {
                            ExcelUtil.writeExcel(getContext());
                            Toast.makeText(getContext(),"保存成功",Toast.LENGTH_SHORT).show();
                        } catch (Exception e) {
                            Toast.makeText(getContext(),"Failed",Toast.LENGTH_SHORT).show();
                            e.printStackTrace();
                        }
                        break;
                    case R.id.item_send :
                        if (User.documents!=null) {
                            Intent email = new Intent(android.content.Intent.ACTION_SEND_MULTIPLE);
                            email.setType("application/octet-stream");
                            //邮件接收者（数组，可以是多位接收者）
                            String[] emailReciver = new String[]{User.receiver};
                            String emailTitle = "监测项数据";
                            //设置邮件地址
                            email.putExtra(android.content.Intent.EXTRA_EMAIL, emailReciver);
                            //设置邮件标题
                            email.putExtra(android.content.Intent.EXTRA_SUBJECT, emailTitle);
                            //附件
                            ArrayList imageUris = new ArrayList();
                            for (int i=0;i<User.documents.size();i++)
                            imageUris.add(Uri.fromFile(
                                    new File(getContext().getExternalFilesDir(null).getPath(),User.documents.get(i))));
                            email.putParcelableArrayListExtra(Intent.EXTRA_STREAM, imageUris);
                            //调用系统的邮件系统
                            startActivity(Intent.createChooser(email, "请选择邮件发送软件"));
                        }else{

                        }
                    default:break;
                }
                return true;
            }
        });

    }

    /**
     * 生成浮动窗口
     */
    private void generateFloatingWindows(){
        //初始化浮动窗口
        windowManager = (WindowManager) getApplicationContext().getSystemService(getApplicationContext().WINDOW_SERVICE);
        layoutParams = new WindowManager.LayoutParams();
        layoutParams.type = WindowManager.LayoutParams.TYPE_SYSTEM_ALERT;
        layoutParams.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE | WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL;
        layoutParams.format = PixelFormat.RGBA_8888;
        layoutParams.gravity = Gravity.TOP | Gravity.LEFT;
        layoutParams.width = WindowManager.LayoutParams.WRAP_CONTENT;
        layoutParams.height = WindowManager.LayoutParams.WRAP_CONTENT;
        layoutParams.x = 0;
        layoutParams.y = 0;
        // myLayout is the customized layout which contains textview
        myLayout = new FloatingWindows(getApplicationContext());
        //初始化User类全局变量
        User.fpsPakage = new StringBuffer("com.cn21.speedtest");
        User.inChoosePid =new ArrayList<>();
        User.PrcessCpuRate = new ArrayList();
        User.processCpuRate = new Float[10];
        User.totalCpuRate = new ArrayList<>();
        User.totalCpuRate.add(new ArrayList<Float>());
        activityManager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
    }

    /**
     *生成view的数目
     */
    private void initData1() {
        mDatas = new ArrayList<>();
        for ( int i=0; i < 4; i++) {
            mDatas.add( "item"+i);
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

    /**
     * 判断service是否启动
     * @param mContext
     * @param className
     * @return
     */
    public static boolean isServiceRunning(Context mContext,String className) {

        boolean isRunning = false;
        ActivityManager activityManager = (ActivityManager)
                mContext.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningServiceInfo> serviceList
                = activityManager.getRunningServices(30);

        if (!(serviceList.size()>0)) {
            return false;
        }

        for (int i=0; i<serviceList.size(); i++) {
            if (serviceList.get(i).service.getClassName().equals(className) == true) {
                isRunning = true;
                break;
            }
        }
        return isRunning;
    }

}

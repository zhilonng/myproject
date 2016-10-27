package com.cn21.speedtest.view;

import android.app.ProgressDialog;
import android.support.design.widget.TabLayout;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.cn21.speedtest.R;
import com.cn21.speedtest.adapter.CpuInfoAdapter;
import com.cn21.speedtest.service.CpuReaderService;
import com.cn21.speedtest.utils.User;
import com.cn21.speedtest.utils.calculate.Calculate;
import com.cn21.speedtest.utils.calculate.ColumnChartDataCalculate;

import java.util.ArrayList;
import java.util.List;

import lecho.lib.hellocharts.listener.ColumnChartOnValueSelectListener;
import lecho.lib.hellocharts.model.ColumnChartData;
import lecho.lib.hellocharts.model.SubcolumnValue;
import lecho.lib.hellocharts.view.ColumnChartView;

/**
 * Created by huangzhilong on 16/8/9.
 * content：获取cpu使用信息
 */
public class CpuInfoViewHolder extends RecyclerView.ViewHolder {

    //主界面，选项卡
    private TabLayout tabLayout;
    private ViewPager viewPager;
    private LayoutInflater inflater;
    private List<String> titlelist = new ArrayList<>();
    private View view1,view2;
    private List<View> viewList = new ArrayList<>();
    //view1
    private SlideCutListView list_process;
    private TextView tv_percent;
    private TextView tv_pakage;
    private ImageView iv_control;
    CpuInfoAdapter adapter;
    private String[] project = {"The Total CPU Usage"};
    private String[] pid={"123"};
    private String[] percent={"50%"};
    //view2
    private ColumnChartView columnChart;
    private ColumnChartData data_column;
    private Button btn_generatedata;
    private TextView tv_record;
    private List<String> record = new ArrayList<>();
    private List<List<Float>> AverageCpu = new ArrayList<>();
    private ProgressDialog progDialog = null;// 搜索时进度条
    private static boolean cpu_running = true;

    public CpuInfoViewHolder(final View itemView) {
        super(itemView);
        AverageCpu.add(new ArrayList<Float>());
        inflater = LayoutInflater. from(itemView.getContext());
        //通过ID获得页面控件
        viewPager = (ViewPager) itemView.findViewById(R.id.viewpager);
        tabLayout = (TabLayout) itemView.findViewById(R.id.tablayout);

        //inflater找到页卡的页面view
        view1 = inflater.inflate(R.layout.viewpager_cpu_list,null);
        view2 = inflater.inflate(R.layout.viewpager_cpu_statistics,null);

        //页面列表添加页面view
        viewList.add(view1);
        viewList.add(view2);

        //添加页面的标题
        titlelist.add("进程列表");
        titlelist.add("统计分析");

        //设置TAB的模式
        tabLayout.setTabMode(TabLayout.MODE_FIXED);

        //给tab添加tab栏
        tabLayout.addTab(tabLayout.newTab().setText(titlelist.get(0)));
        tabLayout.addTab(tabLayout.newTab().setText(titlelist.get(1)));

        //新建viewpage的适配器adapter，传入参数是页面列表viewlist
        MyPageAdapter adapter1 = new MyPageAdapter(viewList);

        //将viewpage跟adapter绑定
        viewPager.setAdapter(adapter1);
        //将tab跟viewpage连接
        tabLayout.setupWithViewPager(viewPager);
        tabLayout.setTabsFromPagerAdapter(adapter1);

        view1setting();
        view2setting();

    }

    /**
     * cpu/Fps列表
     */
    private void view1setting() {
        list_process = (SlideCutListView)view1.findViewById(R.id.list_process);
        tv_pakage = (TextView)view1.findViewById(R.id.tv_project);
        tv_percent = (TextView)view1.findViewById(R.id.tv_percent);
        iv_control = (ImageView)view1.findViewById(R.id.iv_control);
        iv_control.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (CpuReaderService.isbegin){
                    iv_control.setBackgroundResource(R.drawable.ic_stop);
                    CpuReaderService.isbegin = false;
                } else {
                    iv_control.setBackgroundResource(R.drawable.ic_begin);
                    CpuReaderService.isbegin = true;
                }
            }
        });
        list_process.setRemoveListener(new SlideCutListView.RemoveListener() {
            @Override
            public void removeItem(SlideCutListView.RemoveDirection direction, int position) {

            }
        });
        adapter = new CpuInfoAdapter(itemView.getContext());
        adapter.arr.add("");
        adapter.AllPakage.add("CPU占用率");
        adapter.arr.add("");
        adapter.AllPid.add("");
        adapter.AllPakage.add("FPS");
        adapter.arr.add("");
        adapter.AllPid.add("");
        adapter.AllPakage.add("Memory");
        adapter.arr.add("");
        adapter.AllPid.add("");
        adapter.AllPakage.add("耗电量");
        adapter.AllPid.add("");
        list_process.setAdapter(adapter);
    }

    /**
     * 统计分析
     */
    private void view2setting(){
        columnChart = (ColumnChartView)view2.findViewById(R.id.cpu_columnchart);
        columnChart.setOnValueTouchListener(new ValueTouchListener());//设置点击监听
        tv_record = (TextView)view2.findViewById(R.id.tv_record);

        btn_generatedata = (Button)view2.findViewById(R.id.btn_generate);
        btn_generatedata.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showProgressDialog();
                generateColumnChartData();//生成数据
                dissmissProgressDialog();
            }
        });

    }

    /**
     * 生成柱状图
     */
    private void generateColumnChartData() {
        AverageCpu.get(0).clear();
        record.clear();
        for (int i=0;i<User.totalCpuRate.size();i++){
            float totalnumber = 0;
            for (int j=0;j<User.totalCpuRate.get(i).size();j++){
                totalnumber +=User.totalCpuRate.get(i).get(j);
            }
            AverageCpu.get(0).add(totalnumber/User.totalCpuRate.get(i).size());
            if (i==0){
                if (CpuReaderService.runningCpu) {
                    record.add("CPU总占用率在" + User.totalCpuRate.get(i).size() + "次统计中，平均占用率为" + AverageCpu.get(0).get(i) + "%\n");
                }else {
                    record.add("FPS在" + User.totalCpuRate.get(i).size() + "次统计中，平均值为" + AverageCpu.get(0).get(i) + "\n");
                }
            }else {
                if (CpuReaderService.runningCpu) {
                    record.add("process " + i + ":在" + User.totalCpuRate.get(i).size() + "次统计中，平均占用率为" + AverageCpu.get(0).get(i) + "%.\n");
                }else {
                    if (i == 1) {
                        record.add("frames number " + ":在" + User.totalCpuRate.get(i).size() + "次统计中，平均值为" + AverageCpu.get(0).get(i) + "\n");
                    }else {
                        record.add("jank "  + ":在" + User.totalCpuRate.get(i).size() + "次统计中，平均值为" + AverageCpu.get(0).get(i) + "\n");
                    }
                }
            }
        }
        Calculate cal = new ColumnChartDataCalculate();
        data_column = cal.calculateColumnChartData(AverageCpu,0);
        columnChart.setColumnChartData(data_column);
        String allrecord = "如下：\n";
        for (int i=0;i<record.size();i++){
            allrecord +=record.get(i);
        }
        tv_record.setText(allrecord);
    }
    /**
     * 显示进度框
     */
    private void showProgressDialog() {
        if (progDialog == null)
            progDialog = new ProgressDialog(view2.getContext());
        progDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progDialog.setIndeterminate(false);
        progDialog.setCancelable(false);
        progDialog.setMessage("正在拼命加载数据...");
        progDialog.show();
    }

    /**
     * 隐藏进度框
     */
    private void dissmissProgressDialog() {
        if (progDialog != null) {
            progDialog.dismiss();
        }
    }

    /**
     * 重写viewPager的适配器
     */
    class MyPageAdapter extends PagerAdapter {
        private List<View> viewList;

        //构造方法，参数是页卡的list
        public MyPageAdapter(List<View> viewList) {
            this.viewList = viewList;
        }

        //返回页卡的数量
        @Override
        public int getCount() {
            return viewList.size();
        }

        //官方提示这样写，为什么呢？
        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }

        //实例化页卡
        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            container.addView(viewList.get(position));  //添加
            return viewList.get(position);
        }

        //删除页卡
        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView(viewList.get(position));
        }

        //取得页卡的标题
        @Override
        public CharSequence getPageTitle(int position) {
            return titlelist.get(position);
        }
    }

    private class ValueTouchListener implements ColumnChartOnValueSelectListener {
        @Override
        public void onValueSelected(int i, int i1, SubcolumnValue value) {
            //Toast.makeText(view2.getContext(), "Selected: " + value, Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onValueDeselected() {

        }
    }
}

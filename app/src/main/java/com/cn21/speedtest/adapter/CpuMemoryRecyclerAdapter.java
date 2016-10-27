package com.cn21.speedtest.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.cn21.speedtest.R;
import com.cn21.speedtest.view.AppInfoViewHolder;
import com.cn21.speedtest.view.CpuCalInfoViewHolder;
import com.cn21.speedtest.view.CpuChartViewHolder;
import com.cn21.speedtest.view.CpuInfoViewHolder;

import java.util.List;

/**
 * Created by huangzhilong on 16/8/9.
 * content:适配页面，card_cpu_chart与card_cpuinfo
 */
public class CpuMemoryRecyclerAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{
    private List<String> mDatas;
    private Context mContext;
    private LayoutInflater inflater;
    //建立枚举 2个item 类型
    public enum ITEM_TYPE {
        ITEM1,
        ITEM2,
        ITEM3,
        ITEM4
    }

    /**
     * 构造函数,传入datas对item进行绘制
     * @param context
     * @param datas
     */
    public CpuMemoryRecyclerAdapter(Context context, List<String> datas){
        this. mContext=context;
        this. mDatas=datas;
        inflater=LayoutInflater. from(mContext);
    }

    /**
     * 这个方法主要生成为每个Item inflater出一个View，但是该方法返回的是一个ViewHolder。
     * 该方法把View直接封装在ViewHolder中，然后我们面向的是ViewHolder这个实例，当然这个
     * ViewHolder需要我们自己去编写。直接省去了当初的convertView.setTag(holder)和
     * convertView.getTag()这些繁琐的步骤。
     * @param parent
     * @param viewType
     * @return
     */
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == ITEM_TYPE.ITEM1.ordinal()) {
            View view = inflater.inflate(R.layout.card_appinfo, parent, false);
            AppInfoViewHolder holder = new AppInfoViewHolder(view);
            return holder;
        } else {
            if (viewType == ITEM_TYPE.ITEM2.ordinal()) {
                View view = inflater.inflate(R.layout.card_cpuinfomation,parent, false);
                CpuCalInfoViewHolder holder= new CpuCalInfoViewHolder(view);
                return holder;

            }else if(viewType == ITEM_TYPE.ITEM3.ordinal()){
                View view = inflater.inflate(R.layout.card_cpu_chart,parent, false);
                CpuChartViewHolder holder= new CpuChartViewHolder(view);
                return holder;

            }else if (viewType == ITEM_TYPE.ITEM4.ordinal()){
                View view = inflater.inflate(R.layout.card_cpuinfo, parent, false);
                CpuInfoViewHolder holder = new CpuInfoViewHolder(view);
                return holder;
            }
        }
        return null;
    }


    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {

    }

    @Override
    public int getItemViewType(int position) {
        //Enum类提供了一个ordinal()方法，返回枚举类型的序数，这里ITEM_TYPE.ITEM1.ordinal()代表0， ITEM_TYPE.ITEM2.ordinal()代表1
        if (position% 6 == 0){
            return ITEM_TYPE.ITEM1.ordinal();
        }else {
            if (position% 6 == 1) {
                return ITEM_TYPE.ITEM2.ordinal();
            }else {
                if (position% 6 ==2){
                    return  ITEM_TYPE.ITEM3.ordinal();
                }
                else {
                    if (position% 6 ==3){
                        return ITEM_TYPE.ITEM4.ordinal();
                    }
                }
            }
        }
        return position;
    }


    //获取item数目
    @Override
    public int getItemCount() {
        return mDatas.size();
    }
}
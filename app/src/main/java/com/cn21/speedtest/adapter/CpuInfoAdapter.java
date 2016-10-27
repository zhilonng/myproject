package com.cn21.speedtest.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.cn21.speedtest.R;
import com.cn21.speedtest.view.ProcessListHolder;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by huangzhilong on 16/8/10.
 */
public class CpuInfoAdapter extends BaseAdapter {

    private Context context;
    private LayoutInflater inflater;
    public ArrayList<String> arr;
    public List<String> AllPakage;
    public List<String> AllPid;
    public CpuInfoAdapter(Context context) {
        super();
        this.context = context;
        inflater = LayoutInflater.from(context);
        arr = new ArrayList<String>();
        AllPakage = new ArrayList<String>();
        AllPid = new ArrayList<String>();
    }
    @Override
    public int getCount() {
        // TODO Auto-generated method stub
        return arr.size();
    }
    @Override
    public Object getItem(int arg0) {
        // TODO Auto-generated method stub
        return arg0;
    }
    @Override
    public long getItemId(int arg0) {
        // TODO Auto-generated method stub
        return arg0;
    }
    @Override
    public View getView(final int position, View view, ViewGroup arg2) {
        // TODO Auto-generated method stub
        if(view == null){
            view = inflater.inflate(R.layout.item_list_process, null);
        }
        ProcessListHolder holder= new ProcessListHolder(view,AllPakage.get(position),AllPid.get(position),position);
        return view;
    }
}



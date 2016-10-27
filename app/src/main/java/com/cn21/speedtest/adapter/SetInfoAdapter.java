package com.cn21.speedtest.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.cn21.speedtest.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by luwy on 2016/8/26.
 */
public class SetInfoAdapter extends BaseAdapter {
    Context context;
    List<String> list=new ArrayList<>();

    public SetInfoAdapter(Context context, List<String> list) {
        this.context=context;
        this.list=list;
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int i) {
        return list.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        ViewHolder viewHolder;
        if(view==null){
            viewHolder=new ViewHolder();
            view= LayoutInflater.from(context).inflate(R.layout.activity_setinfo_item,null);
            viewHolder.info=(TextView)view.findViewById(R.id.info);
            view.setTag(viewHolder);
        }
        else{
            viewHolder=(ViewHolder)view.getTag();
        }
        viewHolder.info.setText(list.get(i));
        return  view;
    }

    private  class ViewHolder{
        TextView info;
    }
}

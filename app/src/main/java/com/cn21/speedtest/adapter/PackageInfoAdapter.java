package com.cn21.speedtest.adapter;

import android.content.Context;
import android.media.Image;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.cn21.speedtest.R;
import com.cn21.speedtest.model.PackageClass;

import java.util.ArrayList;
import java.util.List;


public class PackageInfoAdapter extends BaseAdapter {
    Context context;
    List<PackageClass> mpackageList=new ArrayList<>();


    LayoutInflater layoutInflater;

    public PackageInfoAdapter(Context context, List<PackageClass> list){
        this.context=context;
        this.mpackageList=list;
        layoutInflater= LayoutInflater.from(context);


    }

    @Override
    public int getCount() {
        return  mpackageList.size();
    }

    @Override
    public Object getItem(int position) {
        return mpackageList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        if(convertView==null){
            viewHolder=new ViewHolder();
            convertView= layoutInflater.inflate(R.layout.packageitemlayout,null);
            viewHolder.name=(TextView)convertView.findViewById(R.id.name);
            viewHolder.lable=(TextView)convertView.findViewById(R.id.lable);
            viewHolder.image=(ImageView)convertView.findViewById(R.id.icon);
            convertView.setTag(viewHolder);
        }
        else{
            viewHolder=(ViewHolder)convertView.getTag();
        }
        viewHolder.name.setText(mpackageList.get(position).getmPackageName());
        viewHolder.lable.setText(mpackageList.get(position).getmLable());
        viewHolder.image.setImageDrawable(mpackageList.get(position).getmIcon());
        return convertView;
    }

private  class ViewHolder{
    private  TextView   name;
    private  TextView   lable;
    private  ImageView  image;
}

}

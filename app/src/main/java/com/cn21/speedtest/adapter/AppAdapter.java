package com.cn21.speedtest.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.cn21.speedtest.R;
import com.cn21.speedtest.model.AppInfo;

import java.util.List;

/**
 * 应用的适配器
 * Created by 梁照江 on 2016/8/10.
 */
public class AppAdapter extends ArrayAdapter<AppInfo>{
    private int resourceId;

    public AppAdapter(Context context , int textViewResourceId, List<AppInfo> objects){
        super(context,textViewResourceId,objects);
        resourceId = textViewResourceId;

    }

    public View getView(int position, View convertView, ViewGroup parent){
        View view;
        ViewHolder holder;
        if(convertView != null){
            view = convertView;
            holder = (ViewHolder) view.getTag();
        }
        else {
            view = LayoutInflater.from(getContext()).inflate(resourceId, null);
            holder = new ViewHolder();
            holder.image = (ImageView) view.findViewById(R.id.imageApp);
            holder.tvAppLabel = (TextView) view.findViewById(R.id.tvAppLabel);
            holder.tvPackageName = (TextView) view.findViewById(R.id.tvPkgName);
            view.setTag(holder);
        }
        AppInfo appInfo = getItem(position);

        holder.image.setImageDrawable(appInfo.getAppIcon());
        holder.tvAppLabel.setText(appInfo.getAppLabel());
        holder.tvPackageName.setText(appInfo.getPkgName());
        return view;

    }

    private static  class ViewHolder{
        private ImageView image;
        private TextView tvAppLabel;
        private TextView tvPackageName;
    }



}

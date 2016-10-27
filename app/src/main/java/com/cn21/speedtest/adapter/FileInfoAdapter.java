package com.cn21.speedtest.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.cn21.speedtest.R;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by luwy on 2016/8/10
 */

public class FileInfoAdapter  extends BaseAdapter {
    List<File>  fileList=new ArrayList<>();
    LayoutInflater mLayoutInflater;
    public FileInfoAdapter(Context context,List<File> fileList) {
        mLayoutInflater = LayoutInflater.from(context);
        this.fileList = fileList;
    }

    @Override
    public int getCount() {
        return fileList.size();
    }

    @Override
    public Object getItem(int position) {
        return fileList.get(position);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        if(convertView==null){
            viewHolder=new ViewHolder();
            convertView=  mLayoutInflater.inflate(R.layout.fileitemlayout,null);
            viewHolder.text=(TextView)convertView.findViewById(R.id.filetext);
            viewHolder.image=(ImageView)convertView.findViewById(R.id.image);
            convertView.setTag(viewHolder);
        }
        else{
            viewHolder = (ViewHolder)convertView.getTag();
        }
        if(fileList.get(position).isDirectory()){
            viewHolder.image.setImageResource(R.drawable.dirt);
        }
        else{
            viewHolder.image.setImageResource(R.drawable.file);
        }
        viewHolder.text.setText( fileList.get(position).getName());
        return convertView;
    }

    private  class ViewHolder{
        private  TextView text;
        private ImageView image;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }
}

package com.cn21.speedtest.adapter;

/**
 * Created by luwy on 2016/8/10.
 */

import android.view.View;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import com.cn21.speedtest.R;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2016/8/1 0001.
 */
public class DeviceInfoAdapter extends BaseAdapter {

    //存储内容
    List<String> mListCont = new ArrayList<>();
    //存储名称
    List<String> mListName = new ArrayList<>();
    LayoutInflater mLayoutInflater;

    public DeviceInfoAdapter(Context context, List<String> mListCont, List<String> mListName) {

        this.mListCont = mListCont;
        this.mListName = mListName;
        mLayoutInflater = LayoutInflater.from(context);


    }

    @Override
    public int getCount() {
        return mListName.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
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
            convertView = mLayoutInflater.inflate(R.layout.deviceitemlayout, null);
            viewHolder.name=(TextView) convertView.findViewById(R.id.name);
            viewHolder.con=(TextView) convertView.findViewById(R.id.con);
            convertView.setTag(viewHolder);
        }

       else{
            viewHolder = (ViewHolder)convertView.getTag();
        }
        viewHolder.name.setText(mListName.get(position));
        viewHolder.con.setText(mListCont.get(position));
        return convertView;

    }

    private  class ViewHolder{
          private  TextView name;
          private TextView con;
    }
}

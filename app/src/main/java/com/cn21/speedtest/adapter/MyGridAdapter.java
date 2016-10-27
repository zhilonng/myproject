package com.cn21.speedtest.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.cn21.speedtest.R;

import java.util.ArrayList;
import java.util.Map;

/**
 * @Description:gridview的Adapter
 * @author shenpeng email:sx_shenp@corp.21cn.com
 */
public class MyGridAdapter extends BaseAdapter {
    private Context mContext;
    int showArrayList=-1;
    private boolean isShowDelete;//根据这个值判断是否显示或删除图表，true显示，false删除
    private ArrayList<Map<String,Object>> arrayList=new ArrayList<Map<String,Object>>();

    public MyGridAdapter(Context mContext) {
        super();
        this.mContext = mContext;
    }
    public MyGridAdapter(Context mContext,ArrayList<Map<String, Object>> data){
        super();
        this.mContext=mContext;
    }

    @Override
    public int getCount() {
        // TODO Auto-generated method stub
        return arrayList.size();
    }

    @Override
    public Object getItem(int position) {
        // TODO Auto-generated method stub
        return position;
    }

    @Override
    public long getItemId(int position) {
        // TODO Auto-generated method stub
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder=null;
        if (convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(
                    R.layout.grid_item, parent, false);
            viewHolder=new ViewHolder();
            viewHolder.textView = (TextView)convertView.findViewById(R.id.tv_item);
            viewHolder.imageView = (ImageView) convertView.findViewById(R.id.iv_item);
            viewHolder.deleteView=(ImageView) convertView.findViewById(R.id.delete_markView);
            convertView.setTag(viewHolder);
        }else {
            viewHolder=(ViewHolder) convertView.getTag();
        }

        viewHolder.textView.setText(arrayList.get(position).entrySet().iterator().next().getKey());
        viewHolder.imageView.setBackgroundResource((int)arrayList.get(position).entrySet().iterator().next().getValue());
        viewHolder.deleteView.setVisibility(isShowDelete?View.VISIBLE:View.GONE);//设置是否删除图标
        return convertView;
    }
    static class ViewHolder{
        TextView textView;
        ImageView imageView;
        ImageView deleteView;
    }
    public void setIsShowDelete(boolean isShowDelete,int showArrayList){
        this.isShowDelete=isShowDelete;
        this.showArrayList=showArrayList;
        arrayList.remove(showArrayList);
        notifyDataSetChanged();
    }
}

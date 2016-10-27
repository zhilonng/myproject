package com.cn21.speedtest.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.cn21.speedtest.R;
import com.cn21.speedtest.model.ProcessInfo;
import com.cn21.speedtest.model.Programe;

import java.util.List;

/**
 * Created by shenpeng on 2016/8/9.
 */
public class BrowseMemoryAdapter extends BaseAdapter{
    private List<Programe> mListProcessInfo=null;
    LayoutInflater inflater=null;

    public BrowseMemoryAdapter(Context context, List<Programe> apps){
        inflater=(LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mListProcessInfo=apps;
    }
    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public int getCount() {
        return mListProcessInfo.size();
    }

    @Override
    public Object getItem(int position) {
        return mListProcessInfo.get(position);
    }

    @Override
    public View getView(int position, View convertview, ViewGroup viewGroup) {
        View view=null;
        ViewHolder holder=null;
        if(convertview==null||convertview.getTag()==null){
            view=inflater.inflate(R.layout.browse_process_item,null);
            holder=new ViewHolder(view);
            view.setTag(holder);
        }else {
            view=convertview;
            holder=(ViewHolder) convertview.getTag();
        }
        Programe programe=(Programe)getItem(position);
        holder.tvPID.setText(programe.getPid()+"");
        holder.tvUID.setText(programe.getUid()+"");
        holder.tvProcessMemSize.setText(programe.getMemSize()+"KB");
        holder.tvProcessName.setText(programe.getProcessName());

        return view;
    }
    class ViewHolder{
        TextView tvPID;
        TextView tvUID;
        TextView tvProcessMemSize;
        TextView tvProcessName;
        public ViewHolder(View view){
            this.tvPID=(TextView) view.findViewById(R.id.tvProcessPID);
            this.tvUID=(TextView) view.findViewById(R.id.tvProcessUID);
            this.tvProcessMemSize=(TextView) view.findViewById(R.id.tvProcessMemSize);
            this.tvProcessName=(TextView) view.findViewById(R.id.tvProcessName);
        }
    }
}

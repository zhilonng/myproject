package com.cn21.speedtest.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.cn21.speedtest.model.Programe;

import java.util.List;

/**
 * 进程的适配器
 * Created by 梁照江 on 2016/8/10.
 */
public class ProcessAdapter extends ArrayAdapter<Programe> {
    private int resourceId;

    public ProcessAdapter(Context context, int textViewResourceId, List<Programe> objects){
        super(context,textViewResourceId,objects);
        resourceId = textViewResourceId;
    }



    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view;
        final ViewHolder holder;
        if(convertView != null){
            view = convertView;
            holder = (ViewHolder) view.getTag();
        }
        else {
            view = LayoutInflater.from(getContext()).inflate(resourceId, null);
            holder = new ViewHolder();
//            holder.processPkgName = (TextView) view.findViewById(R.id.process_name);
//            holder.processPID = (TextView) view.findViewById(R.id.process_pid);
//            holder.processUID = (TextView) view.findViewById(R.id.process_uid);
//            holder.lyt_process = (LinearLayout)view.findViewById(R.id.lyt_process);
//            view.setTag(holder);
        }
        Programe programe = getItem(position);
        holder.processPkgName.setText(programe.getProcessName());
        holder.processPID.setText(Integer.toString(programe.getPid()));
        holder.processUID.setText(Integer.toString(programe.getUid()));

        /*
        holder.lyt_process.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!User.inChoosePid.contains(holder.processPID.getText().toString())) {
                    Toast.makeText(view.getContext(), holder.processPID.getText().toString(), Toast.LENGTH_LONG).show();
                    User.inChooseProcessPid = holder.processPID.getText().toString();
                    User.inChoosePakageName = holder.processPkgName.getText().toString();
                    Intent intent = new Intent("com.cn21.speedtest.getprocess");
                    intent.putExtra("pid", holder.processPID.getText().toString());
                    intent.putExtra("pakagename", holder.processPkgName.getText().toString());
                    view.getContext().sendBroadcast(intent);

                    Intent intent1 = new Intent("com.cn21.speedtest.CpuReaderService");
                    intent1.putExtra("pid", holder.processPID.getText().toString());
                    intent1.putExtra("pakagename", holder.processPkgName.getText().toString());
                    view.getContext().sendBroadcast(intent1);
                }else {
                    Toast.makeText(view.getContext(),"该进程已经获取了,请选择其他进程",Toast.LENGTH_SHORT).show();
                }
            }
        });
        */
        return view;
    }
    private static  class ViewHolder{
        
        private LinearLayout lyt_process;
        private TextView processPkgName;
        private TextView processUID;
        private TextView processPID;
    }
}

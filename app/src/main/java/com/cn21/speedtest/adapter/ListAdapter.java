package com.cn21.speedtest.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.TextView;

import com.cn21.speedtest.R;
import com.cn21.speedtest.model.ProcessInfo;
import com.cn21.speedtest.model.Programe;
import com.cn21.speedtest.utils.LogUtil;

import java.util.List;

/**
 * Created by shenpeng on 2016/8/9.
 */
public class ListAdapter extends BaseAdapter {

    private List<Programe> programeList = null;
    public Programe checkeProg;
    ProcessInfo processInfo;
    LayoutInflater inflater = null;
    int lastCheckedPosition = -1;

    public ListAdapter(Context mContext) {
        inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        processInfo=new ProcessInfo();
        programeList = processInfo.getAllPackages(mContext);
    }

    @Override
    public Object getItem(int position) {
        return programeList.get(position);
    }

    @Override
    public int getCount() {
        return programeList.size();
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertview, ViewGroup viewGroup) {
        View view = null;
        ViewHolder holder = null;
        Programe programe=programeList.get(position);
        if (convertview == null || convertview.getTag() == null) {
            view = inflater.inflate(R.layout.list_item, null);
            holder = new ViewHolder(view);
            holder.rdoBtnApp.setFocusable(false);
            holder.rdoBtnApp.setOnCheckedChangeListener(checkedChangeListener);
            view.setTag(holder);
        } else {
            view = convertview;
            holder = (ViewHolder) convertview.getTag();
        }
        holder.imgViAppIcon.setImageDrawable(programe.getIcon());
        holder.txtAppName.setText(programe.getProcessName());
        holder.rdoBtnApp.setId(position);
        holder.rdoBtnApp.setChecked(checkeProg!=null&&getItem(position)==checkeProg);
        return view;
    }

    class ViewHolder {
       TextView txtAppName;
        ImageView imgViAppIcon;
        RadioButton rdoBtnApp;

        public ViewHolder(View view) {
            this.txtAppName = (TextView) view.findViewById(R.id.text_item);
            this.imgViAppIcon = (ImageView) view.findViewById(R.id.image_item);
            this.rdoBtnApp = (RadioButton) view.findViewById(R.id.rb_item);

        }
    }
    CompoundButton.OnCheckedChangeListener checkedChangeListener = new CompoundButton.OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            if (isChecked) {
                final int checkedPosition = buttonView.getId();
                if (lastCheckedPosition != -1) {
                    RadioButton tempButton = (RadioButton) buttonView.findViewById(lastCheckedPosition);
                    if ((tempButton != null) && (lastCheckedPosition != checkedPosition)) {
                        tempButton.setChecked(false);
                    }
                }
                checkeProg = programeList.get(checkedPosition);
                lastCheckedPosition = checkedPosition;
            }
        }
    };
}

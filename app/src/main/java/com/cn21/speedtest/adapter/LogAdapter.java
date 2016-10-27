/*
 * Copyright (C) 2013 readyState Software Ltd
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.cn21.speedtest.adapter;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.cn21.speedtest.R;
import com.cn21.speedtest.model.LogLine;

import java.util.List;

public class LogAdapter extends BaseAdapter {

    private Context mContext;
    private LayoutInflater mInflater;
    private SharedPreferences mPrefs;
    private List<LogLine> mData;
    private float mDensity;


    public LogAdapter(Context context, List<LogLine> objects) {
        mContext = context.getApplicationContext();
        mData = objects;
        mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mPrefs = PreferenceManager.getDefaultSharedPreferences(context);
        mDensity = mContext.getResources().getDisplayMetrics().density;

    }

    public void setData(List<LogLine> objects) {
        mData = objects;
        notifyDataSetChanged();
    }



    @Override
    public int getCount() {
        return mData.size();
    }

    @Override
    public LogLine getItem(int i) {
        return mData.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        ViewHolder holder;

        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.list_item_log, parent, false);
            holder = new ViewHolder();
            holder.time = (TextView) convertView.findViewById(R.id.time);
            holder.tag = (TextView) convertView.findViewById(R.id.tag);
            holder.msg = (TextView) convertView.findViewById(R.id.msg);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        final LogLine line = getItem(position);
        final int color = line.getColor();

        holder.time.setText(line.getTime());
        holder.tag.setText(line.getTag());
        holder.msg.setText(line.getMessage());

        holder.time.setTextColor(color);
        holder.tag.setTextColor(color);
        holder.msg.setTextColor(color);

        holder.time.setTextSize(7);
        holder.tag.setTextSize(7);
        holder.msg.setTextSize(7);

        ViewGroup.LayoutParams lp = holder.time.getLayoutParams();

        holder.time.setLayoutParams(lp);

        lp = holder.tag.getLayoutParams();

        holder.tag.setLayoutParams(lp);


        return convertView;
    }



    private int dipToPixel(int value) {
        return (int) (value * mDensity + 0.5f);
    }

    private class ViewHolder {
        public TextView time;
        public TextView tag;
        public TextView msg;
    }
}

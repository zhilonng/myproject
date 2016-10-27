package com.cn21.speedtest.fragment;

import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * 基类fragment，提供通用fragment
 *
 * Copyright (c) 2016. shenpeng (sx_shenp@corp.21cn.com)
 * @version 1.0
 * 创建时间：2016/8/1.
 * 创建人：申鹏
 */

public abstract class BaseFragment extends Fragment{
    //这个mActivity是Fragment依附的Activity
    public Context mContext;

    //Frament被创建
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext=getActivity();
    }

    //初始化fragment布局
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view=initView();
        return view;
    }

    /**
     * fragment销毁时，要进行资源回收
     */
    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    /**
     * 初始化布局，子类必须实现
     */
    protected abstract View initView();

}

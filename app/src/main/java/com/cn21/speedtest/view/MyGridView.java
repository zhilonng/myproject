package com.cn21.speedtest.view;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.GridView;

/**
 * @author shenpeng email:sx_shenp@corp.21cn.com
 */
public class MyGridView extends GridView{
    public MyGridView(Context context, AttributeSet attrs){
        super(context,attrs);
    }
    public MyGridView(Context context){
        super(context);
    }
    public MyGridView(Context context,AttributeSet attrs,int defStyle){
        super(context,attrs,defStyle);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int expandSpec=MeasureSpec.makeMeasureSpec(Integer.MAX_VALUE>>2,MeasureSpec.AT_MOST);
        super.onMeasure(widthMeasureSpec, expandSpec);
    }

}

package com.cn21.speedtest.activity;

import android.content.Intent;
import android.graphics.PixelFormat;
import android.view.ContextMenu;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;

import com.cn21.speedtest.R;
import com.cn21.speedtest.adapter.SetInfoAdapter;
import com.cn21.speedtest.service.ScreenShotService;
import com.cn21.speedtest.service.SensorService;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by luwy on 2016/8/26.
 */
public class SetInfoActivity extends BaseActivity {
    ListView mListView;
    List<String> mList=new ArrayList<>();

    //定义浮动窗口布局
    LinearLayout mFloatLayout;
    WindowManager.LayoutParams mWMParams;
    //创建浮动窗口设置布局参数的对象
    WindowManager mWindowManager;
    Button mFloatView;

    @Override
    protected void initView() {
        setContentView(R.layout.activity_setinfo);
        mListView=(ListView)findViewById(R.id.list);
    }

    @Override
    protected void initData() {
        mList.add("设置时间");
        mList.add("网速限制");
        mList.add("截图");
    }

    @Override
    protected void initEvent() {
        mListView.setAdapter(new SetInfoAdapter(this,mList));
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
            String str=mList.get(i);
            if(str.equals("设置时间")){
               Intent intent=new Intent(SetInfoActivity.this,SetTimeActivity.class);
                startActivity(intent);
           }
            if(str.equals("网速限制")){
                Intent intent=new Intent(SetInfoActivity.this,LimitNetSpeedActivity.class);
                startActivity(intent);
            }
            if(str.equals("截图")) {
                Intent intent=new Intent(SetInfoActivity.this,SensorService.class);
                startService(intent);
                Toast.makeText(getContext(),"摇一摇即可完成截图",Toast.LENGTH_SHORT).show();
            }
    }
    });
    }
}

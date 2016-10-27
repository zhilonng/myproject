package com.cn21.speedtest.activity;

import android.widget.TextView;

import com.cn21.speedtest.R;

public class TableShowActivity extends BaseActivity {
TextView textView;

    @Override
    protected void initView() {
        setContentView(R.layout.activity_table_show);
        textView=(TextView)findViewById(R.id.info);

    }

    @Override
    protected void initEvent() {
        String info= getIntent().getStringExtra("info");
        textView.setText(info);
    }
}

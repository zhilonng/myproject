package com.cn21.speedtest.activity;

import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import com.cn21.speedtest.R;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;


/**
 * 新建一个hosts方案
 * Created by liangzhj on 2016/8/24.
 */
public class CreateNewHost extends BaseActivity {
    private EditText ehostsName;
    private EditText ehosts;
    private Button cancel,sure;

    @Override
    protected void initView() {
        setContentView(R.layout.create_new_hosts);
        setContentView(R.layout.create_new_hosts);
        ehosts = (EditText) findViewById(R.id.hosts_file);
        ehostsName = (EditText) findViewById(R.id.input_hosts_name);
        cancel = (Button) findViewById(R.id.create_cancel);
        sure = (Button) findViewById(R.id.create_sure);
    }

    @Override
    protected void initData() {
        super.initData();
    }

    @Override
    protected void initEvent() {
        sure.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String hostName = ehostsName.getText().toString();
                String hosts = ehosts.getText().toString();
                File file = new File("/data/data/com.cn21.speedtest/hostsfile/"+hostName);
                try{
                    if(!file.exists()){
                        if (file.createNewFile())
                            Log.v("文件操作","创建成功");
                        Log.v("文件操作","文件不存在");
                    }
                    FileOutputStream fos = new FileOutputStream(file);
                    byte[] bytes = hosts.getBytes();
                    fos.write(bytes);
                    fos.close();
                    finish();
                }catch (IOException e){
                    e.printStackTrace();
                }
            }
        });

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }



}

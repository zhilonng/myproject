package com.cn21.speedtest.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.cn21.speedtest.R;
import com.cn21.speedtest.adapter.DatabaseAdapter;
import com.cn21.speedtest.model.SettingDatabase;
import com.cn21.speedtest.service.TrafficMonitorService;

/**
 * Created by lenovo on 2016/8/17.
 */
public class Setting extends Activity {

     SettingDatabase settingDbAdapter;
    private DatabaseAdapter dbAdapter;
    LinearLayout linearLayoutClear,linearLayoutGSMLimit,linearLayoutWIFILimit;
    CheckBox checkboxAutoStartup,checkboxStartStat,checkboxShowWindow;
    TextView tv_gsmLimit,tv_wifiLimit;
    private View View_quota;
    private EditText dt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        setContentView(R.layout.setting_layout);
        setTitle("Setting");

        checkboxAutoStartup=(CheckBox) this.findViewById(R.id.checkbox_auto_startup);
        checkboxStartStat=(CheckBox) this.findViewById(R.id.checkbox_statistic);
//        checkboxShowWindow=(CheckBox) this.findViewById(R.id.checkbox_show_float_window);

        linearLayoutClear=(LinearLayout) this.findViewById(R.id.linearlayout_cleardata);
        linearLayoutGSMLimit=(LinearLayout) this.findViewById(R.id.linearlayout_gsmlimit);
        linearLayoutWIFILimit=(LinearLayout) this.findViewById(R.id.linearlayout_wifilimit);

        tv_gsmLimit=(TextView) this.findViewById(R.id.gms_month_flow_limit);
        tv_wifiLimit=(TextView) this.findViewById(R.id.wifi_month_flow_limit);


        settingDbAdapter = new SettingDatabase(this);
        settingDbAdapter.open();
        dbAdapter = new DatabaseAdapter(this);
        dbAdapter.open();
        if(!settingDbAdapter.check())
        {
            settingDbAdapter.insertData(1, 1, 1, (long)0,(long)0);
        }


        if(settingDbAdapter.checkautoStartup())
        {
            checkboxAutoStartup.setChecked(true);
        }
        else
        {
            checkboxAutoStartup.setChecked(false);
        }

        if(settingDbAdapter.checkStartStat())
        {
            checkboxStartStat.setChecked(true);
        }
        else
        {
            checkboxStartStat.setChecked(false);
        }



        Long gsmLimit=settingDbAdapter.checkGSMLimit();
        if(gsmLimit>0)
        {
            tv_gsmLimit.setText(Long.toString(gsmLimit)+" Mb");
        }
        else
        {
            tv_gsmLimit.setText("no limit");
        }

        Long wifiLimit=settingDbAdapter.checkWIFILimit();
        if(wifiLimit>0)
        {
            tv_wifiLimit.setText(Long.toString(wifiLimit)+" Mb");
        }
        else
        {
            tv_wifiLimit.setText("no limit");
        }


        checkboxAutoStartup.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener(){

            @Override
            public void onCheckedChanged(CompoundButton arg0, boolean bStatus) {
                // TODO Auto-generated method stub
                int autoStartup=0;

                if(bStatus)
                {
                    autoStartup=1;
                }
                else
                {
                    autoStartup=0;
                }
                settingDbAdapter.updateAutoStartup(autoStartup);
            }});


        checkboxStartStat.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener(){

            @Override
            public void onCheckedChanged(CompoundButton buttonView,	boolean bStatus) {
                // TODO Auto-generated method stub
                int startStat=0;

                if(bStatus)
                {
                    startStat=1;
                    Intent intent = new Intent(Setting.this,TrafficMonitorService.class);
                    startService(intent);
                }
                else
                {
                    startStat=0;
                }
                settingDbAdapter.updateStartStatistic(startStat);
            }});


//        checkboxShowWindow.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener(){
//
//            @Override
//            public void onCheckedChanged(CompoundButton arg0, boolean bStatus) {
                // TODO Auto-generated method stub
//                int floatWindow=0;
//
//                if(bStatus)
//                {
//                    floatWindow=1;
//                }
//                else
//                {
//                    floatWindow=0;
//                }
//                settingDbAdapter.updateFloatWindow(floatWindow);
//            }});


        linearLayoutGSMLimit.setOnClickListener(new Button.OnClickListener() {
            public void onClick(View v) {
                // TODO Auto-generated method stub

                LayoutInflater factory = LayoutInflater.from(Setting.this);
                View_quota = factory.inflate(R.layout.setup_quota, null);

                new AlertDialog.Builder(Setting.this).setTitle("Monthly 3G Limit(enter 0 for no limit)").setIcon(
                        android.R.drawable.ic_dialog_info).setView(View_quota)
                        .setPositiveButton("OK",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog,
                                                        int which) {
                                        // TODO Auto-generated method stub

                                        dt = (EditText) View_quota
                                                .findViewById(R.id.quota12);
                                        String limit="0";
                                        if(dt.getText().toString().equals("")||dt.getText().toString().equals(" "))
                                        {
                                            limit="0";
                                        }
                                        else
                                        {
                                            limit = dt.getText().toString();
                                        }
                                        tv_gsmLimit.setText(limit+" Mb");
                                        settingDbAdapter.updateGSMLimit(Long.parseLong(limit));
                                    }

                                }).setNegativeButton("Cancel", null).show();
            }
        });


        linearLayoutWIFILimit.setOnClickListener(new Button.OnClickListener() {
            public void onClick(View v) {
                // TODO Auto-generated method stub

                LayoutInflater factory = LayoutInflater.from(Setting.this);
                View_quota = factory.inflate(R.layout.setup_quota, null);

                new AlertDialog.Builder(Setting.this).setTitle("Monthly WIFI Limit(enter 0 for no limit)").setIcon(
                        android.R.drawable.ic_dialog_info).setView(View_quota)
                        .setPositiveButton("OK",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog,
                                                        int which) {
                                        // TODO Auto-generated method stub

                                        dt = (EditText) View_quota
                                                .findViewById(R.id.quota12);
                                        String limit="0";
                                        if(dt.getText().toString().equals("")||dt.getText().toString().equals(" "))
                                        {
                                            limit="0";
                                        }
                                        else
                                        {
                                            limit = dt.getText().toString();
                                        }
                                        tv_wifiLimit.setText(limit+" Mb");
                                        settingDbAdapter.updateWIFILimit(Long.parseLong(limit));
                                    }

                                }).setNegativeButton("Cancel", null).show();
            }
        });


        linearLayoutClear.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // TODO Auto-generated method stub
                new AlertDialog.Builder(Setting.this).setTitle("Are you sure you want to erase all statistical records?")
                        .setPositiveButton("Yes",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog,
                                                        int which) {

                                        // TODO Auto-generated method stub
                                        dbAdapter.clear();
                                        Toast.makeText(Setting.this, "Records have been deleted!", Toast.LENGTH_SHORT).show();
                                    }

                                }).setNegativeButton("Cancel", null).show();
            }
        });
    }



    public boolean BoxState(String s) {
        if (s.equals("")) {
            return false;
        } else if (Integer.parseInt(s) == 1) {
            return true;
        } else {
            return false;
        }
    }

    public boolean onKeyDown(int keyCode, KeyEvent event) {



        if (keyCode == KeyEvent.KEYCODE_BACK) {
            Setting.this.finish();
            settingDbAdapter.close();
        }
        return true;
    }

}

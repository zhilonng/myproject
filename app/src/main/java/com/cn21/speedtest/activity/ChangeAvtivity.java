package com.cn21.speedtest.activity;

import android.net.wifi.WifiManager;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import com.cn21.speedtest.R;
import com.cn21.speedtest.utils.ShellUtils;
import java.util.ArrayList;
import java.util.List;


/**
 * Created by Host on 2016/8/23.
 */
public class ChangeAvtivity extends BaseActivity {
    private TextView netInfoTextView;
    private WifiManager wifiManager;
    private EditText dns1Set,dns2Set;
    private Button button;
    @Override
    protected void initView() {
        setContentView(R.layout.activty_test_dhcp);
        netInfoTextView = (TextView) findViewById(R.id.test_ip);
        button = (Button) findViewById(R.id.commit);
        dns1Set = (EditText) findViewById(R.id.dns1_test);
        dns2Set = (EditText) findViewById(R.id.dns2_test);
    }

    @Override
    protected void initData() {
        super.initData();
        wifiManager = (WifiManager) getSystemService(WIFI_SERVICE);
        String str ;

        String cmd1,cmd2;
        cmd1 = "getprop net.dns1";
        cmd2 = "getprop net.dns2";
        ShellUtils.CommandResult result1 = ShellUtils.execCommand(cmd1,false,true);
        ShellUtils.CommandResult result2 = ShellUtils.execCommand(cmd2,false,true);
        if (result1.result ==0){
            str = "DNS1:  "+result1.successMsg;
            str = str +
                    "\nDNS2:  "+result2.successMsg;
        }else {
            str = "失败了";
        }
        netInfoTextView.setText(str);
    }

    @Override
    protected void initEvent() {
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String dns1Str = dns1Set.getText().toString();
                String dns2Str = dns2Set.getText().toString();
                setDNS(dns1Str,dns2Str);
            }
        });

    }


    private String intToIp(int paramInt) {
        return (paramInt & 0xFF) + "." + (0xFF & paramInt >> 8) + "." + (0xFF & paramInt >> 16) + "."
                + (0xFF & paramInt >> 24);
    }


    private void setDNS(String DNS1,String DNS2){
        String cmd1 = "setprop net.dns1"+" "+DNS1;
        String cmd2 = "setprop net.dns2"+" "+DNS2;
        List<String> cmdList = new ArrayList<String>();
        cmdList.clear();
        cmdList.add(cmd1);
        cmdList.add(cmd2);
        ShellUtils.CommandResult commandResult1 = ShellUtils.execCommand(cmdList,true,true);

        if (commandResult1.result == 0) {
         //   netInfoTextView.setText(commandResult.successMsg);
            Toast.makeText(this, "成功"+commandResult1.successMsg, Toast.LENGTH_LONG).show();
        }else {
            Toast.makeText(this,"失败"+commandResult1.errorMsg,Toast.LENGTH_LONG).show();
        }
    }




}

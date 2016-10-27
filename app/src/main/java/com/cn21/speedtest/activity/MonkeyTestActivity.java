package com.cn21.speedtest.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Environment;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.cn21.speedtest.R;
import com.cn21.speedtest.model.DefaultApplication;
import com.cn21.speedtest.model.Programe;
import com.cn21.speedtest.utils.SuperUserHelper;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class MonkeyTestActivity extends PreferenceActivity {
    SharedPreferences mPrefs;
    Preference seed;
    Preference level;
    Preference throttle;
    Preference count;
    Preference touch;
    Preference security;
    Preference crashed;
    Preference timeouts;
    String testPkg;
    String Pkgvalue;
    String seedvalue;
    String levelvalue;
    String levelstring;
    String throttlevalue;
    String countvalue;
    String touchvalue;
    String savepathvalue;
    boolean seurityvalue;
    boolean crashedvalue;
    boolean timeoutsvalue;
    Programe programe=new Programe();
    SimpleDateFormat simpleDateFormat=new SimpleDateFormat("HH:mm:ss");
    Process process;
    ArrayList<String> args=new ArrayList<String>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.monkeytestpref);
       seed=findPreference(getString(R.string.seed));
        level=findPreference(getString(R.string.level));
        throttle=findPreference(getString(R.string.throttle));
        count=findPreference(getString(R.string.count));
        touch=findPreference(getString(R.string.touch));
        security=findPreference(getString(R.string.security));
        crashed=findPreference(getString(R.string.crashes));
        timeouts=findPreference(getString(R.string.timeouts));
        seed.setOnPreferenceChangeListener(sBindPreferenceSummaryToValueListener);
        level.setOnPreferenceChangeListener(sBindPreferenceSummaryToValueListener);
        throttle.setOnPreferenceChangeListener(sBindPreferenceSummaryToValueListener);
        count.setOnPreferenceChangeListener(sBindPreferenceSummaryToValueListener);
        touch.setOnPreferenceChangeListener(sBindPreferenceSummaryToValueListener);
        try {
            process=Runtime.getRuntime().exec("su");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    private  Preference.OnPreferenceChangeListener sBindPreferenceSummaryToValueListener=new Preference.OnPreferenceChangeListener(){

        @Override
        public boolean onPreferenceChange(Preference preference, Object newValue) {
            String stringValue =newValue.toString();
            preference.setSummary(stringValue);
            return true;
        }
    };


    private void getArgs() {
        mPrefs=PreferenceManager.getDefaultSharedPreferences(this);
        args.clear();
        args.add("monkey");
        getTestPkg();
        getSavepath();
        args.add("-p");
        args.add(testPkg);
       seedvalue=mPrefs.getString(getString(R.string.seed),"10");
        args.add("-s");
        args.add(seedvalue);
        levelvalue=mPrefs.getString(getString(R.string.level),"1");
        switch(levelvalue){
            case "1":
                levelstring="-v";
                break;
            case "2":
                levelstring="-v -v";
                break;
            case "3":
                levelstring="-v -v -v";
                break;
        }
        args.add(levelstring);
        throttlevalue=mPrefs.getString(getString(R.string.throttle),"0");
        args.add("--throttle");
        args.add(throttlevalue);
        touchvalue=mPrefs.getString(getString(R.string.touch),"15");
        args.add("--pct-touch");
        args.add(touchvalue);
        seurityvalue=mPrefs.getBoolean(getString(R.string.security),false);
        if(seurityvalue){
            args.add("--ignore-security-exceptions");
        }
        crashedvalue=mPrefs.getBoolean(getString(R.string.crashes),false);
        if(seurityvalue){
            args.add("--ignore-crashes");
        }
        timeoutsvalue=mPrefs.getBoolean(getString(R.string.timeouts),false);
        if(timeoutsvalue){
            args.add("--ignore-timeouts");
        }
        countvalue=mPrefs.getString(getString(R.string.count),"100");
        args.add(countvalue);
        args.add(savepathvalue);
    }

    private void getSavepath() {
        File sdcard= Environment.getExternalStorageDirectory();
        String  savepath=sdcard.getPath()+"/Monkey";
        File Savepath=new File(savepath);
        if(!Savepath.exists()){
            Savepath.mkdir();
        }
        String save=savepath+"/"+testPkg+simpleDateFormat.format(new Date())+".txt";
        savepathvalue=">"+save;
    }

    private void getTestPkg() {
       //testPkg="com.tencent.mm";
        testPkg= DefaultApplication.getPkgName();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.starttest, menu);
        return true;
    }
    public boolean onOptionsItemSelected(MenuItem item){
        switch (item.getItemId()) {
            case R.id.starttest:
                if (!SuperUserHelper.requestRoot()) {
                    Toast.makeText(this, "未获取root权限", Toast.LENGTH_SHORT).show();
                } else {
                    try {
                        process = Runtime.getRuntime().exec("su");
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    getArgs();
                    PrintStream printStream = null;
                    try {
                        printStream = new PrintStream(new BufferedOutputStream(process.getOutputStream(), 8192));
                        printStream.println(TextUtils.join(" ", args));
                        printStream.flush();
                    } finally {
                        if (printStream != null) {
                            printStream.close();
                        }
                    }
                }
                break;
                    case R.id.result:
                        File loglist = new File(Environment.getExternalStorageDirectory().getPath() + "/Monkey");
                        if (!loglist.exists()) {
                            Toast.makeText(MonkeyTestActivity.this, "无测试日志", Toast.LENGTH_SHORT).show();
                        } else if (loglist.exists()) {
                            String[] logs = loglist.list();
                            if (logs == null || logs.length == 0) {
                                Toast.makeText(MonkeyTestActivity.this, "无测试日志", Toast.LENGTH_SHORT).show();
                            } else {
                                Intent intent = new Intent(MonkeyTestActivity.this, DisplaymonkeyActivity.class);
                                MonkeyTestActivity.this.startActivity(intent);
                            }
                        }
                        break;

        }
        return true;
    }
}

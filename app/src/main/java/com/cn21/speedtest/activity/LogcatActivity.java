package com.cn21.speedtest.activity;

import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Environment;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceCategory;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.support.v4.content.LocalBroadcastManager;
import android.view.Gravity;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.Toast;

import com.cn21.speedtest.R;
import com.cn21.speedtest.service.LogService;

import java.io.File;
import java.io.IOException;
import java.util.List;



public class LogcatActivity extends BasePreferenceActivity {

    private static final int CODE_TAG_FILTER = 1;
    private static final int CODE_KEY_FILTER = 2;
    private static Preference sTagFilterPref;
    private static Preference sKeyFilterPref;
    private static Preference sDisplayPref;
    private SharedPreferences mPrefs;

    private BroadcastReceiver mRootFailReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            processRootFail();//获取root权限
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Switch mainSwitch = new Switch(this);
        mainSwitch.setChecked(LogService.isRunning());
        //点击按钮，启动后台服务或者关闭服务
        mainSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                Intent intent = new Intent(LogcatActivity.this, LogService.class);
                if (b) {
                    if (!LogService.isRunning()) {
                        startService(intent);
                    }
                } else {
                    stopService(intent);
                }
            }
        });

        final ActionBar bar = getActionBar();
        final ActionBar.LayoutParams lp = new ActionBar.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        lp.gravity = Gravity.RIGHT | Gravity.CENTER_VERTICAL;
        lp.rightMargin = getResources().getDimensionPixelSize(R.dimen.main_switch_margin_right);
        bar.setCustomView(mainSwitch, lp);
        bar.setDisplayShowCustomEnabled(true);
        //设置界面
        mPrefs = PreferenceManager.getDefaultSharedPreferences(this);
        if (!mPrefs.getBoolean(PreferenceManager.KEY_HAS_SET_DEFAULT_VALUES, false)) {
            PreferenceManager.setDefaultValues(this, R.xml.pref_filters, true);
            SharedPreferences.Editor edit = mPrefs.edit();
            edit.putBoolean(PreferenceManager.KEY_HAS_SET_DEFAULT_VALUES, true);
            edit.apply();
        }

    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        setupSimplePreferencesScreen();
    }

    @Override
    protected void onResume() {
        super.onResume();
        try {
            Runtime.getRuntime().exec("su");
        } catch (IOException e) {
            e.printStackTrace();
        }
        IntentFilter f = new IntentFilter();
        f.addAction(LogService.ACTION_ROOT_FAILED);
        LocalBroadcastManager.getInstance(this).registerReceiver(mRootFailReceiver, f);
    }

    @Override
    protected void onPause() {
        super.onPause();
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mRootFailReceiver);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        //获取tagfilter的返回值
        if (requestCode == CODE_TAG_FILTER) {
            if (resultCode == RESULT_OK) {
                mPrefs.edit().putString(getString(R.string.pref_tag_filter), data.getAction()).apply();
                sTagFilterPref.setSummary(data.getAction());
            }
        }
        //获取keyfilter的返回值
        if (requestCode == CODE_KEY_FILTER) {
            if (resultCode == RESULT_OK) {
                mPrefs.edit().putString(getString(R.string.pref_key_filter), data.getAction()).apply();
                sKeyFilterPref.setSummary(data.getAction());
            }
        }
    }

    @Override
    public void onBuildHeaders(List<Header> target) {
        if (!isSimplePreferences(this)) {
            loadHeadersFromResource(R.xml.pref_headers, target);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        sTagFilterPref = null;
        sKeyFilterPref=null;
        sDisplayPref=null;
    }

    /**
     * Shows the simplified settings UI if the device configuration if the
     * device configuration dictates that a simplified, single-pane UI should be
     * shown.
     */
    @SuppressWarnings("deprecation")
    private void setupSimplePreferencesScreen() {
        if (!isSimplePreferences(this)) {
            return;
        }

        addPreferencesFromResource(R.xml.pref_blank);

        // Add 'filters' preferences.
        PreferenceCategory fakeHeader = new PreferenceCategory(this);
        fakeHeader.setTitle(R.string.filters);
        getPreferenceScreen().addPreference(fakeHeader);
        addPreferencesFromResource(R.xml.pref_filters);
        bindPreferenceSummaryToValue(findPreference(getString(R.string.pref_log_level)));
        setupTagFilterPreference(this, findPreference(getString(R.string.pref_tag_filter)));//设置tagfilter
        setupKeyFilterPreference(this,findPreference(getString(R.string.pref_key_filter)));
        sTagFilterPref = findPreference(getString(R.string.pref_tag_filter));
        sKeyFilterPref=findPreference(getString(R.string.pref_key_filter));
        //添加'保存'设置
        fakeHeader = new PreferenceCategory(this);
        fakeHeader.setTitle(R.string.Save_logs);
        getPreferenceScreen().addPreference(fakeHeader);
        addPreferencesFromResource(R.xml.pref_text);
        setupDisplayPreference(this, findPreference(getString(R.string.pref_display_logs)));//设置tagfilter
        sDisplayPref=findPreference(getString(R.string.display_logs));

    }
    private static void setupDisplayPreference(final Activity activity, Preference preference) {
        preference.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                File loglist = new File(Environment.getExternalStorageDirectory().getPath() + "/Goastlogs");
                if(!loglist.exists()){
                    Toast.makeText(activity,R.string.no_logs_saved, Toast.LENGTH_SHORT).show();
                }else if(loglist.exists()) {
                    String[] logs = loglist.list();
                    if (logs == null || logs.length == 0) {
                        Toast.makeText(activity, R.string.no_logs_saved, Toast.LENGTH_SHORT).show();
                    }else {
                        Intent intent = new Intent(activity, DisplayListActivity.class);
                        activity.startActivity(intent);
                    }
                }
                return true;
            }
        });
    }


    private static void setupKeyFilterPreference(final Activity activity, Preference preference) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(activity);
        preference.setSummary(prefs.getString(activity.getString(R.string.pref_key_filter), null));
        preference.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                Intent intent = new Intent(activity, KeyFilterListActivity.class);
                activity.startActivityForResult(intent, CODE_KEY_FILTER);
                return true;
            }
        });
    }
    private void processRootFail() {

        int failCount = mPrefs.getInt(getString(R.string.pref_root_fail_count), 0);//获取root权限失败的次数
        if (failCount == 0) {
            // show dialog first time
            //第一次显示获取root权限失败的对话框
            AlertDialog dlg = new AlertDialog.Builder(this)
                    .setTitle(R.string.no_root)
                    .setMessage(R.string.no_root_dialog)
                    .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int whichButton) {
                            // ok, do nothing
                        }
                    })
                    .create();
            dlg.show();
            mPrefs.edit().putInt(getString(R.string.pref_root_fail_count), failCount+1).apply();
        } else if (failCount <= 3) {
            // show toast 3 more times
            Toast.makeText(this, R.string.toast_no_root, Toast.LENGTH_LONG).show();
            mPrefs.edit().putInt(getString(R.string.pref_root_fail_count), failCount+1).apply();
        }

    }
    //设置tagfilter的内容
    private static void setupTagFilterPreference(final Activity activity, Preference preference) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(activity);
        preference.setSummary(prefs.getString(activity.getString(R.string.pref_tag_filter), null));
        preference.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                Intent intent = new Intent(activity, TagFilterListActivity.class);
                activity.startActivityForResult(intent, CODE_TAG_FILTER);
                return true;
            }
        });
    }


    public static class FilterPreferenceFragment extends PreferenceFragment {
        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            // Load the preferences from an XML resource
            addPreferencesFromResource(R.xml.pref_filters);
            bindPreferenceSummaryToValue(findPreference(getString(R.string.pref_log_level)));
            setupTagFilterPreference(getActivity(), findPreference(getString(R.string.pref_tag_filter)));
            setupKeyFilterPreference(getActivity(),findPreference(getString(R.string.pref_key_filter)));
            sTagFilterPref = findPreference(getString(R.string.pref_tag_filter));
            sKeyFilterPref=findPreference(getString(R.string.pref_key_filter));
        }
    }
    public static class SavePreferenceFragment extends PreferenceFragment {
        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            // Load the preferences from an XML resource
            addPreferencesFromResource(R.xml.pref_text);
            setupTagFilterPreference(getActivity(), findPreference(getString(R.string.pref_display_logs)));
            sDisplayPref = findPreference(getString(R.string.pref_display_logs));
        }
    }
}


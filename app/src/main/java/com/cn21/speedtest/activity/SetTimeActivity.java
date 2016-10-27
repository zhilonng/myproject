package com.cn21.speedtest.activity;

import android.view.View;
import android.view.WindowManager;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.TimePicker;
import com.cn21.speedtest.R;
import com.cn21.speedtest.utils.SetTimeUtil;
import com.cn21.speedtest.utils.ChangeSize;
import java.io.IOException;

/**
 * Created by luwy on 2016/8/25.
 * 工具类：SetTImeUtil
 */
public class SetTimeActivity extends BaseActivity {
    ImageView set;
    DatePicker datePicker;
    TimePicker timePicker;
    int mYear;
    int mMonth;
    int mDay;
    int mHour;
    int mMinute;

    @Override
    protected void initView() {
        //不弹出软键盘
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
        setContentView(R.layout.timepicker);
        set=(ImageView) findViewById(R.id.set);
        datePicker=(DatePicker)findViewById(R.id.datePicker);
        timePicker=(TimePicker)findViewById(R.id.timePicker);
        new ChangeSize(this).resizePikcer(datePicker);//调整datepicker大小
        new ChangeSize(this).resizePikcer(timePicker);//调整timepicker大小
        timePicker.setIs24HourView(true);
    }

    @Override
    protected void initEvent() {
        SetTimeUtil.requestPermission();
        set.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            SetTimeUtil.setDateTime(mYear,mMonth,mDay,mHour,mMinute);
                        } catch (IOException e) {
                            e.printStackTrace();
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }).start();
            }
        });

        timePicker.setOnTimeChangedListener(new TimePicker.OnTimeChangedListener() {
            @Override
            public void onTimeChanged(TimePicker timePicker, int hour, int minute) {
                mHour=hour;
                mMinute=minute;
            }
        });
        datePicker.init(2016, 8, 25, new DatePicker.OnDateChangedListener() {
            @Override
            public void onDateChanged(DatePicker datePicker, int year, int month, int day) {
                mYear=year;
                mMonth=month;
                mDay=day;
            }
        });
    }


}

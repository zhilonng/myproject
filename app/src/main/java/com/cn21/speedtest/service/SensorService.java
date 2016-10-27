package com.cn21.speedtest.service;

import android.app.Service;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.util.Log;
import android.widget.Toast;


/**
 * Created by liujin on 2016/9/8.
 */
public class SensorService extends Service {
    private SensorManager sensorManager;
    private final int SENSOR_SHAKE = 0;
    private boolean allowShake = true;

    public IBinder onBind(Intent intent) {
        return null;
    }

    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.v("信息","fuwuyiqidong");
        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        if (sensorManager != null) {
            // 第一个参数是Listener，第二个参数是所得传感器类型，第三个参数值获取传感器信息的频率
            sensorManager.registerListener(sensorEventListener,
                                           sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),//加速度传感器
                                           SensorManager.SENSOR_DELAY_NORMAL);
        }
        return START_NOT_STICKY;
    }

    Handler handler = new Handler() {
        public void handleMessage(Message msg) {
            if (msg.what == SENSOR_SHAKE){
                Intent intent=new Intent(getApplicationContext(),ScreenShotService.class);
                startService(intent);
            }
        }
    };

    SensorEventListener  sensorEventListener=new SensorEventListener() {
        public void onSensorChanged(SensorEvent sensorEvent) {
            float[] values = sensorEvent.values;
            float x = values[0];
            float y = values[1];
            float z = values[2];
            int medumValue = 15;//不同手机阈值不同
            if (Math.abs(x) > medumValue || Math.abs(y) > medumValue || Math.abs(z) > medumValue) {
                if (allowShake) {
                    allowShake = false;
                    Message msg = new Message();
                    msg.what = SENSOR_SHAKE;
                    handler.sendMessage(msg);
                    Thread thread = new Thread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                Thread.sleep(2000);
                                allowShake = true;
                            }catch (InterruptedException e){
                            }
                        }
                    });
                    thread.start();
                }
                else {
                    Toast.makeText(getApplicationContext(),"摇动太频繁，请两秒后再进行摇动",Toast.LENGTH_SHORT).show();
                }
            }
        }

        @Override
        public void onAccuracyChanged(Sensor sensor, int i) {

        }
    };
}

package com.cn21.speedtest.activity;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.widget.ListView;
import android.widget.Toast;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.cn21.speedtest.R;
import com.cn21.speedtest.adapter.DeviceInfoAdapter;
import com.cn21.speedtest.utils.DeviceInfoUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * 展示设备信息
 * 工具类：DeviceInfoUtil
 * 定位：百度定位sdk
 */
public class DeviceInfoActivity extends BaseActivity {

    List<String> mListCont = new ArrayList<>();
    List<String> mListName = new ArrayList<>();
    LocationClient mLocationClient = null;
    BDLocationListener myListener = new MyLocationListener();
    String mLocationInfo;
    Handler mhandler=new Handler(){
        public void handleMessage(Message msg) {
            if (msg.what == 2) {
                Bundle bundle = msg.getData();
                String add = bundle.getString("address");
                mListCont.set(9, add);
                mListName.set(9, "出口地址");
            }
        }
    };

    @Override
    protected void initView() {
        setContentView(R.layout.deviceinfolayout);
    }

    @Override
    protected void initData() {
        //声明LocationClient类
        mLocationClient = new LocationClient(getApplicationContext());
    }


    @Override
    protected void initEvent() {
        //注册监听函数
        mLocationClient.registerLocationListener(myListener);
        //初始化
        initLocation();
        //start（）；
        mLocationClient.start();
        loadlist();
        mListCont.add(mLocationInfo);
        mListName.add("位置信息");

        ListView mListView = (ListView) findViewById(R.id.listview);
        mListView.setAdapter(new DeviceInfoAdapter(this, mListCont, mListName));

    }

    private void loadlist() {
        DeviceInfoUtil deviceInfoUtil=new DeviceInfoUtil();
        //MODEL 版本(即最终用户可见的)名称
        mListCont.add(deviceInfoUtil.getModel());
        mListName.add("型号");
        //BRAND 系统定制商
        mListCont.add(deviceInfoUtil.getBrand());
        mListName.add("品牌");
        //SDK_INT
        mListCont.add(deviceInfoUtil.getSdk()+"");
        mListName.add("SDK版本");
        //RELEASE版本字符串
        mListCont.add(deviceInfoUtil.getRelease());
        mListName.add("系统版本");
        //设备分辨率
        mListCont.add("像素 高  "+deviceInfoUtil.getDisplayMetricsHeight(this)+"  宽  "+deviceInfoUtil.getDisplayMetricsWidth(this)+"  尺寸  "+deviceInfoUtil.getDisplayMetricsSize(this));
        mListName.add("尺寸信息");
        //CPU型号
        mListCont.add("型号 " + deviceInfoUtil.getCpuName() + "   " + deviceInfoUtil.getNumCores() + "核   频率 " + deviceInfoUtil.getMaxCpuFreq()+"HZ");
        mListName.add("CPU信息");
        //内存
        mListCont.add("RAM  "+deviceInfoUtil.getRamMemory(this)+"   ROM  "+deviceInfoUtil.getRomMemorySize(this));
        mListName.add("内存大小");
        //mac地址
        mListCont.add(deviceInfoUtil.getMacAdrs(this));
        mListName.add("mac地址");
        //IP地址
        mListCont.add(deviceInfoUtil.getIpInfo(this));
        mListName.add("IP地址");
        //外网IP地址
        deviceInfoUtil.getNetIp(mhandler);
        mListCont.add("0.0.0.0");
        mListName.add("出口地址");

        mListCont.add(deviceInfoUtil.getImsi(this));
        mListName.add("IMSI");
        mListCont.add(deviceInfoUtil.getImei(this));
        mListName.add("IMEI");
        //BOARD 主板
        mListCont.add(deviceInfoUtil.getBoard());
        mListName.add("主板");
        // DEVICE 设备参数
        mListCont.add(deviceInfoUtil.getDevice());
        mListName.add("设备参数");
        //DISPLAY 显示屏参数
        mListCont.add(deviceInfoUtil.getDisplay());
        mListName.add("显示屏参数");
        // MANUFACTURER 硬件制造商
        mListCont.add(deviceInfoUtil.getManufacturer());
        mListName.add("硬件制造商");
        //PRODUCT 整个产品的名称
        mListCont.add(deviceInfoUtil.getProduct());
        mListName.add("产品名称");
        //SERIAL 硬件序列号
        mListCont.add(deviceInfoUtil.getSerial());
        mListName.add("硬件序列号");
        //TIME
        mListCont.add(deviceInfoUtil.getTime());
        mListName.add("出厂时间");
        //ROOT
        mListCont.add(deviceInfoUtil.getRooted());
        mListName.add("是否ROOT");

    }

    private void initLocation(){
        LocationClientOption option = new LocationClientOption();
        int span=1000;
        option.setScanSpan(span);
        //可选，默认0，即仅定位一次，设置发起定位请求的间隔需要大于等于1000ms才是有效的
        option.setIsNeedAddress(true);
        mLocationClient.setLocOption(option);
    }


    class MyLocationListener implements BDLocationListener {
        @Override
        public void onReceiveLocation(BDLocation location) {
            StringBuffer sb = new StringBuffer(256);
            sb.append("time : ");
            sb.append(location.getTime());
            sb.append("\nlatitude : ");
            sb.append(location.getLatitude());
            sb.append("\nlontitude : ");
            sb.append(location.getLongitude());
            sb.append("\nradius : ");
            sb.append(location.getRadius());
            if (location.getLocType() == BDLocation.TypeGpsLocation){// GPS定位结果
                sb.append("\nspeed : ");
                sb.append(location.getSpeed());// 单位：公里每小时
                sb.append("\nsatellite : ");
                sb.append(location.getSatelliteNumber());
                sb.append("\nheight : ");
                sb.append(location.getAltitude());// 单位：米
                sb.append("\ndirection : ");
                sb.append(location.getDirection());// 单位度
                sb.append("\naddr : ");
                sb.append(location.getAddrStr());
                sb.append("\ndescribe : ");
                sb.append("gps定位成功");
            } else if (location.getLocType() == BDLocation.TypeNetWorkLocation){// 网络定位结果
                sb.append("\naddr : ");
                sb.append(location.getAddrStr());
                sb.append("\ndescribe : ");
                sb.append("网络定位成功");
            } else if (location.getLocType() == BDLocation.TypeOffLineLocation) {// 离线定位结果
                sb.append("\ndescribe : ");
                sb.append("离线定位成功，离线定位结果也是有效的");
            } else if (location.getLocType() == BDLocation.TypeServerError) {
                sb.append("\ndescribe : ");
                sb.append("服务端网络定位失败，可以反馈IMEI号和大体定位时间到loc-bugs@baidu.com，会有人追查原因");
            } else if (location.getLocType() == BDLocation.TypeNetWorkException) {
                sb.append("\ndescribe : ");
                sb.append("网络不同导致定位失败，请检查网络是否通畅");
            } else if (location.getLocType() == BDLocation.TypeCriteriaException) {
                sb.append("\ndescribe : ");
                sb.append("无法获取有效定位依据导致定位失败，一般是由于手机的原因，处于飞行模式下一般会造成这种结果，可以试着重启手机");
            }
            mLocationInfo = sb.toString();
            mListCont.set(20, mLocationInfo);
        }
    }



}



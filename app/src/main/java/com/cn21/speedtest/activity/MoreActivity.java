package com.cn21.speedtest.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import com.cn21.speedtest.R;
import com.cn21.speedtest.model.Node;
import com.cn21.speedtest.utils.DAO;
import com.cn21.speedtest.utils.LogUtil;
import com.cn21.speedtest.utils.SQLiteHelper;

import java.util.ArrayList;
import java.util.Map;

/**
 * @author shenpeng email:sx_shenp@corp.21cn.com
 * 时间;2016年8月4日
 * @Description:添加更多模块界面
 */
public class MoreActivity extends BaseActivity {
    private GridView gridView;
    private ImageView switch_style;    //切换显示的样式
    private boolean isListView=true;   //用于判断当前使用的是listView还是gridView;
    private ListView listView;
    private SimpleAdapter myGridAdapter;
    private SimpleAdapter gridViewAdapter;
    private ArrayList<Map<String, Object>> arrayList;
    private Map<String, Object> map;
    private Intent intent;
    SQLiteHelper db;
    DAO dao = null;
    SharedPreferences sharedPreferences = null;
    boolean isFirstLogin = true;
    private static final String TABLE_SELECT_NAME = "table_select";
    private static final String DATABASE_NAME = "mydb.db";
    public static final String FIRST_RUN = "morelogin";

    public String[] img_text = {"通讯录", "应用管理", "日志分析", "CPU管理", "内存管理", "流量管理", "耗电量", "文件管理", "Monkey", "ANR信息", "权限管理", "设备信息", "图表分析", "便签", "设置","hosts与DNS设置"};
    public int[] imgs = {R.drawable.app_battle, R.drawable.app_best, R.drawable.app_boat, R.drawable.app_common, R.drawable.app_black_bird,
            R.drawable.app_dream_world, R.drawable.app_fire_bird, R.drawable.app_badge, R.drawable.app_green_bird, R.drawable.app_worthy,
            R.drawable.app_tortise, R.drawable.app_orc, R.drawable.app_love, R.drawable.app_lottery, R.drawable.app_hide,R.drawable.app_battery};
    String content = "the description of the model";


    protected void initView() {
        setContentView(R.layout.more_activity_layout);
        listView = (ListView) findViewById(R.id.listView);
        gridView= (GridView) findViewById(R.id.gridView);
        switch_style= (ImageView) findViewById(R.id.right_icon);
        intent = new Intent();
        arrayList = new ArrayList<Map<String, Object>>();
        dao = new DAO(mContext);
        db = new SQLiteHelper(mContext, DATABASE_NAME, 0, 1);

    }

    @Override
    protected void initData() {
        sharedPreferences = getSharedPreferences("config", 0);
        isFirstLogin = sharedPreferences.getBoolean(FIRST_RUN, true);
        if (isFirstLogin) {
            for (int i = 0; i < imgs.length; i++) {
                LogUtil.e(img_text[i]);
                dao.insertData(db.getWritableDatabase(), TABLE_SELECT_NAME, img_text[i], imgs[i], content);

            }
        }
        Cursor cursor = db.getReadableDatabase().rawQuery("select * from table_select", null);
        arrayList = dao.queryData(cursor);
        myGridAdapter = new SimpleAdapter(mContext, arrayList, R.layout.grid_item, new String[]{"img", "text", "content"}, new int[]{R.id.iv_item, R.id.tv_item, R.id.tv_content});
        gridViewAdapter=new SimpleAdapter(mContext,arrayList,R.layout.gridview_item_layout,new String[]{"img","text"},new int[]{R.id.app_icon,R.id.app_name});
        listView.setAdapter(myGridAdapter);
        gridView.setAdapter(gridViewAdapter);
    }

    @Override
    protected void initEvent() {

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, final int position, long l) {
                Map map = arrayList.get(position);
                Node node = new Node((String) map.get("text"), (int) map.get("img"), content);
                intent.putExtra("select", node);
                arrayList.remove(position);
                myGridAdapter.notifyDataSetChanged();
                setResult(RESULT_OK, intent);
                finish();
            }
        });
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Map map = arrayList.get(position);
                Node node = new Node((String) map.get("text"), (int) map.get("img"), content);
                intent.putExtra("select", node);
                arrayList.remove(position);
                gridViewAdapter.notifyDataSetChanged();
                setResult(RESULT_OK, intent);
                finish();
            }
        });

        switch_style.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isListView) {
                    isListView=false;
                    switch_style.setImageResource(R.drawable.icon_pic_list_type);
                    gridView.setVisibility(View.VISIBLE);
                    listView.setVisibility(View.GONE);
                }else {
                    isListView=true;
                    switch_style.setImageResource(R.drawable.icon_pic_grid_type);
                    gridView.setVisibility(View.GONE);
                    listView.setVisibility(View.VISIBLE);
                }
            }
        });
    }

    @Override
    protected void onStop() {
        sharedPreferences = getSharedPreferences("config", 0);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        if (isFirstLogin) {
            editor.putBoolean(FIRST_RUN, false).commit();
        }
//        dao.deleteData(db.getWritableDatabase(),TABLE_SELECT_NAME);
//        Iterator iterator=arrayList.iterator();
//        while (iterator.hasNext()){
//            map=new HashMap<>();
//            map=(Map)iterator.next();
//            dao.insertData(db.getWritableDatabase(), TABLE_SELECT_NAME, (String) map.get("text"), (int) map.get("img"), content);
//        }
        super.onStop();
    }

    @Override
    protected void onDestroy() {

        if (db != null) {
            db.close();
        }
        super.onDestroy();
    }
//    class MyHandler extends Handler{
//
//    }
}

package com.cn21.speedtest.activity;

/**
 * Created by lenovo on 2016/8/16.
 */

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.cn21.speedtest.R;
import com.cn21.speedtest.utils.User;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * Created by lenovo on 2016/8/16.
 */
public class MulSelectActivity extends Activity implements View.OnClickListener {
    private ListView listview;
    private Context context;
    private List<String> selectid = new ArrayList<String>();//存储被选id的数组
    private boolean isMulChoice = false; //是否多选
    private Adapter adapter;
    private RelativeLayout layout;
    private Button cancle, delete;
    private ProgressDialog progDialog = null;// 搜索时进度条
    private TextView txtcount;
   // private List<Map<String, String>> mListItems = new ArrayList<Map<String, String>>();//存储姓名和电话号码
    private List<Map<String, String>> mDeleteListItems = new ArrayList<Map<String, String>>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        setContentView(R.layout.contact_select);
        context = this;
        listview = (ListView) findViewById(R.id.list);
        layout = (RelativeLayout) findViewById(R.id.relative);
        txtcount = (TextView) findViewById(R.id.txtcount);
        cancle = (Button) findViewById(R.id.cancle);
        delete = (Button) findViewById(R.id.delete);
        cancle.setOnClickListener(this);
        delete.setOnClickListener(this);
        //myinit();
        if (User.mListItems.size()>0){
            adapter = new Adapter(context, txtcount);
            listview.setAdapter(adapter);
        }else {
            showProgressDialogDrive();
            new TaskThread().start();
        }
    }

    public void myinit(){
        ContentResolver cr = getContentResolver();
        Uri uri = Uri.parse("content://com.android.contacts/raw_contacts");
        Uri datauri = Uri.parse("content://com.android.contacts/data");
        Cursor cur1 = cr.query(uri, new String[]{"contact_id"}, null, null, null);
        Cursor cur2 = null;
        while (cur1.moveToNext()) {
            //    contact_entity entity=new contact_entity();
            Map<String, String> map = new HashMap<String, String>();
            String id = cur1.getString(0);

            if (!TextUtils.isEmpty(id)) {
                cur2 = cr.query(datauri, new String[]{"mimetype", "data1"}, "contact_id=?", new String[]{id}, null);
                while (cur2.moveToNext()) {
                    String mime = cur2.getString(0);
                    String data = cur2.getString(1);
                    if (mime.equals("vnd.android.cursor.item/name")) {
                        //  entity.setName(data);
                        String name = data;
                        map.put("name", name);
                    } else if (mime.equals("vnd.android.cursor.item/phone_v2")) {
                        //  entity.setNumber(data);
                        String phone = data;
                        if (data == null) {
                            continue;
                        } else {
                            map.put("phone", phone);
                        }
                    }
                    System.out.println(map.get("name") + map.get("phone") + "           +++                   ");

                }

                User.mListItems.add(map);
                cur2.close();
            }

        }

    }

    public void mydelete(){

        ContentResolver cr = getContentResolver();
        Uri uri = Uri.parse("content://com.android.contacts/raw_contacts");
        Uri datauri = Uri.parse("content://com.android.contacts/data");


       for(int i=0;i<mDeleteListItems.size();i++){
          // Cursor cur1 = cr.query(uri, new String[]{"contact_id"}, null, null, null);
           Cursor cur1 = cr.query(uri, new String[]{"contact_id"}, null, null, null);

        //   String id1 = cur1.getString(0);
           Cursor cur2 = null;
         //  Log.e("mydeleteid",id1);
           while (cur1.moveToNext()){
               String id1 = cur1.getString(0);
               Log.e("_ID",id1);
               if (!TextUtils.isEmpty(id1)){
                   cur2 = cr.query(datauri, new String[]{"mimetype", "data1"}, "raw_contact_id=?", new String[]{id1}, null);
                   while (cur2.moveToNext()){
                       String mime = cur2.getString(0);
                       String data = cur2.getString(1);
                       //根据电话号码进行删除

                       if (mime.equals("vnd.android.cursor.item/phone_v2")&&(data.equals(mDeleteListItems.get(i).get("phone")))){
                           Log.e("Delete","GOTODELETE");
                           cr.delete(datauri,"raw_contact_id=?", new String[]{id1});
                           cr.delete(uri,"contact_id=?", new String[]{id1});
                           continue;

                       }
                   }
               }
           }
           cur1.close();
           cur2.close();
       }

    }
    Handler handler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 0:
                {
                    dissmissProgressDialogDrive();
                    adapter = new Adapter(context, txtcount);
                    listview.setAdapter(adapter);
                }
                break;

                default:
                    break;
            }
        };
    };
    class TaskThread extends Thread {
        public void run() {
            myinit();
            handler.sendEmptyMessage(0);
        };
    };



    public void onClick(View v) {
        // TODO Auto-generated method stub
        switch (v.getId()) {
            case R.id.cancle:
                isMulChoice = false;
                selectid.clear();
                adapter = new Adapter(context, txtcount);
                listview.setAdapter(adapter);
                layout.setVisibility(View.INVISIBLE);
                break;
            case R.id.delete:
                isMulChoice = false;

                for (int i = 0; i < selectid.size(); i++) {

                    for (int j = 0; j < User.mListItems.size(); j++) {
                        if (selectid.get(i).equals(User.mListItems.get(j).get("name"))) {
                            // Toast.makeText(context,"shanchu",Toast.LENGTH_LONG).show();
                            Log.e("Delete", User.mListItems.get(j).get("name"));

                            //把需要删除的联系人和号码存入到mDeleteListItem中
                            mDeleteListItems.add(User.mListItems.get(j));

       //                     Log.e("DeletMen",mDeleteListItems.get(j).get("name"));
                            User.mListItems.remove(j);

                        }
                    }
                }
                adapter.notifyDataSetChanged();

//新增线程
                new Thread(new Runnable() {
                    @Override
                    public void run() {
//                        deleteall();
//                        insert(mListItems);
                      mydelete();
                    }
                }).start();

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        selectid.clear();
                        adapter = new Adapter(context, txtcount);
                        listview.setAdapter(adapter);

                        adapter.notifyDataSetChanged();
                        layout.setVisibility(View.INVISIBLE);
                    }
                });

                break;
            default:
                break;
        }

    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v,
                                    ContextMenu.ContextMenuInfo menuInfo) {
        // TODO Auto-generated method stub
        super.onCreateContextMenu(menu, v, menuInfo);
        menu.setHeaderTitle("操作");
    }
    private void showProgressDialogDrive() {
        if (progDialog == null)
            progDialog = new ProgressDialog(context);
        progDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progDialog.setIndeterminate(false);
        progDialog.setCancelable(true);
        progDialog.setMessage("正在努力加载...");
        progDialog.show();
    }
    private void dissmissProgressDialogDrive() {
        if (progDialog != null) {
            progDialog.dismiss();
        }
    }


    class Adapter extends BaseAdapter {
        private Context context;
        private LayoutInflater inflater = null;
        private HashMap<Integer, View> mView;
        public HashMap<Integer, Integer> visiblecheck;//用来记录是否显示checkBox
        public HashMap<Integer, Boolean> ischeck;
        private TextView txtcount;
        private List<Map<String, String>> mItemList;

        public Adapter(Context context, TextView txtcount) {
            this.context = context;
            this.txtcount = txtcount;
            inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            mView = new HashMap<Integer, View>();

            visiblecheck = new HashMap<Integer, Integer>();
            ischeck = new HashMap<Integer, Boolean>();
            if (isMulChoice) {
                for (int i = 0; i < User.mListItems.size(); i++) {
                    ischeck.put(i, false);
                    visiblecheck.put(i, CheckBox.VISIBLE);
                }
            } else {
                for (int i = 0; i < User.mListItems.size(); i++) {
                    ischeck.put(i, false);
                    visiblecheck.put(i, CheckBox.INVISIBLE);
                }
            }
        }

        public int getCount() {
            // TODO Auto-generated method stub
            return User.mListItems.size();
        }

        public Object getItem(int position) {
            // TODO Auto-generated method stub
            return User.mListItems.size();
        }

        public long getItemId(int position) {
            // TODO Auto-generated method stub
            return 0;
        }

        public View getView(final int position, View convertView, ViewGroup parent) {
            // TODO Auto-generated method stub
            View view = mView.get(position);
            if (view == null) {
                view = inflater.inflate(R.layout.contact_item, null);
                final TextView txtname = (TextView) view.findViewById(R.id.txtName);
                final TextView txtphone = (TextView) view.findViewById(R.id.txtPhone);
                final CheckBox ceb = (CheckBox) view.findViewById(R.id.check);

                txtname.setText(User.mListItems.get(position).get("name"));
                txtphone.setText(User.mListItems.get(position).get("phone"));
                ceb.setChecked(ischeck.get(position));

                if (isMulChoice) {
                    ceb.setVisibility(View.VISIBLE);
                } else {
                    ceb.setVisibility(View.INVISIBLE);
                }

                view.setOnLongClickListener(new Onlongclick());

                view.setOnClickListener(new View.OnClickListener() {

                    public void onClick(View v) {
                        // TODO Auto-generated method stub
                        if (isMulChoice) {
                            if (ceb.isChecked()) {
                                ceb.setChecked(false);
                                selectid.remove(User.mListItems.get(position));
                            } else {
                                ceb.setChecked(true);
                                selectid.add(User.mListItems.get(position).get("name"));//通过姓名来删除电话号码

                            }
                            txtcount.setText("共选择了" + selectid.size() + "项");
                        } else {
                        }
                    }
                });

                mView.put(position, view);
            }
            return view;
        }

        class Onlongclick implements View.OnLongClickListener {

            public boolean onLongClick(View v) {
                // TODO Auto-generated method stub

                isMulChoice = true;
                selectid.clear();
                layout.setVisibility(View.VISIBLE);

                for (int i = 0; i < User.mListItems.size(); i++) {
                    adapter.visiblecheck.put(i, CheckBox.VISIBLE);
                }

                adapter = new Adapter(context, txtcount);
                listview.setAdapter(adapter);

                adapter.notifyDataSetChanged();
                return true;
            }
        }
    }

}

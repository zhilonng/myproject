package com.cn21.speedtest.activity;

/**
 * Created by lenovo on 2016/8/16.
 */

import android.app.AlertDialog;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.provider.ContactsContract;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import com.cn21.speedtest.R;


public class ContactMainActivity extends BaseActivity{
    private Button btnAdd;
    private Button btnDelete;

    @Override
    public void onCreate(Bundle savedInstanceState, PersistableBundle persistentState) {
        super.onCreate(savedInstanceState, persistentState);
        initView();


    }

    @Override
    protected void initData() {
        super.initData();
    }

    @Override
    protected void initEvent() {
        btnDelete=(Button)findViewById(R.id.btn_delete);
        btnAdd = (Button) findViewById(R.id.btn_add);
        btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i=new Intent(ContactMainActivity.this,MulSelectActivity.class);
                startActivity(i);
            }
        });
        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showAddDialog();
            }
        });



    }
        private void showAddDialog() {
        View dialog_add = getLayoutInflater().inflate(R.layout.contact_add, null);
        final EditText etName = (EditText) dialog_add.findViewById(R.id.et_name);
        final EditText etPhone = (EditText) dialog_add.findViewById(R.id.et_phone);
        AlertDialog.Builder dialog = new AlertDialog.Builder(ContactMainActivity.this);
        dialog.setTitle("添加联系人").setView(dialog_add).setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String mName = etName.getText().toString();
                String mPhone = etPhone.getText().toString();

                if (mName.equals("") || mPhone.equals(""))
                    Toast.makeText(ContactMainActivity.this, "联系人姓名或电话不能为空", Toast.LENGTH_SHORT).show();
                else
                    addMyContact(mName, mPhone);
            }
        }).setNegativeButton("取消",null).create().show();
    }
        private void addMyContact(String mName, String mPhone) {
        //创建一个空的ContentValues
        ContentValues values = new ContentValues();
        //向ContactsContract.RawContacts.CONTENT_URI执行一个空值插入
        //目的是获取系统返货的rawContactId，以便添加联系人名字和电话使用同一个id

        Uri rawContactUri = getContentResolver().insert(
                ContactsContract.RawContacts.CONTENT_URI, values);
        long rawContactId = ContentUris.parseId(rawContactUri);

        //清空values
        //设置id
        //设置内容类型
        //设置联系人姓名
        values.clear();
        values.put(ContactsContract.Data.RAW_CONTACT_ID, rawContactId);
        values.put(ContactsContract.Data.MIMETYPE,
                ContactsContract.CommonDataKinds.StructuredName.CONTENT_ITEM_TYPE);
        values.put(ContactsContract.CommonDataKinds.StructuredName.GIVEN_NAME, mName);
        //向联系人URI添加联系人姓名
        getContentResolver().insert(ContactsContract.Data.CONTENT_URI, values);

        //清空values
        //设置id
        //设置内容类型
        //设置联系人电话
        //设置电话类型
        values.clear();
        values.put(ContactsContract.Data.RAW_CONTACT_ID, rawContactId);
        values.put(ContactsContract.Data.MIMETYPE, ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE);
        values.put(ContactsContract.CommonDataKinds.Phone.NUMBER, mPhone);
        values.put(ContactsContract.CommonDataKinds.Phone.TYPE, ContactsContract.CommonDataKinds.Phone.TYPE_MOBILE);
        getContentResolver().insert(ContactsContract.Data.CONTENT_URI, values);

        //使用toast提示用户信息添加成功
        Toast.makeText(ContactMainActivity.this, "联系人数据添加成功！", Toast.LENGTH_SHORT).show();
    }
//    private void addCoantact(String name,String phone){
//        ContentValues values = new ContentValues();
//        ContentResolver cr = getContentResolver();
//        Uri uri = Uri.parse("content://com.android.contacts/raw_contacts");
//        Uri datauri = Uri.parse("content://com.android.contacts/data");
//        cr.insert(uri,values);
//
//    //   String id=uri.parseId()
//       // cr.insert()
//    }

    @Override
    protected void initView() {
        setContentView(R.layout.contact_main);
    }
}

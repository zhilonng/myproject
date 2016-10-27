package com.cn21.speedtest.activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Message;
import android.util.Log;
import android.view.ContextMenu;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import com.cn21.speedtest.R;
import com.cn21.speedtest.adapter.FileInfoAdapter;
import com.cn21.speedtest.utils.OpenFileUtil;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

/**
 * Created by luwy on 2016/8/8 0008.
 * 浏览应用文件，并进行删除，复制，移动等。
 */
public class FileInfoActivity  extends BaseActivity {
    ListView mListView;
    List<File> mFileList;
    FileInfoAdapter mFileInfoAdapter;
    List<String> mPathList ;
    String mFilePath;
    ImageView add;
    LinearLayout topView;
    //记录要复制的文件
    File mFile;
    //记录文件的原始读写权限
    Stack<String> preInfo;


    public String getPreInfo(File file) {
        BufferedWriter bw=null;
        InputStream is=null;
        Process process=null;
        String res="";
        try {
            process= new ProcessBuilder("su").start();
            bw=new BufferedWriter(new OutputStreamWriter(process.getOutputStream()));
            bw.write("cd "+file.getAbsolutePath()+"\n");
            bw.write("ls -ld\n");
            bw.write("exit\n");
            bw.flush();
            is=process.getInputStream();
            String s;
            StringBuilder sbReader=new StringBuilder();
            byte[] bytes=new byte[1024];
            while (is.read(bytes)!=-1){
                s=new String(bytes);
                sbReader.append(s);
            }
            String sbString=sbReader.toString();
            Log.v("原始权限", sbString);
            int index=sbString.indexOf(" ");
            res=sbString.substring(1,index);
            Log.v("原始权限提取", res);
        }  catch (IOException e) {
            e.printStackTrace();
        }finally {
                try {
                    if(bw != null) {
                        bw.close();
                        is.close();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
        }
        return res;
    }

    public void setPreInfo(File file) {
        String info;
        if(!preInfo.isEmpty()) {
            info = preInfo.pop();
            int n1 = 0;
            for (int i = 0; i < 3; i++) {
                if (info.charAt(i) == "rwxrwxrwx".charAt(i)) {
                    n1 = n1 * 2 + 1;
                } else {
                    n1 = n1 * 2;
                }
            }
            Log.v("shuju1", n1 + "");
            int n2 = 0;
            for (int i = 3; i < 6; i++) {
                if (info.charAt(i) == "rwxrwxrwx".charAt(i)) {
                    n2 = n2 * 2 + 1;
                } else {
                    n2 = n2 * 2;
                }
            }
            Log.v("shuju2", n2 + "");
            int n3 = 0;
            for (int i = 6; i < 9; i++) {
                if (info.charAt(i) == "rwxrwxrwx".charAt(i)) {
                    n3 = n3 * 2 + 1;
                } else {
                    n3 = n3 * 2;
                }
            }
            Log.v("shuju3", n3 + "");
            String res = String.valueOf(n1) + String.valueOf(n2) + String.valueOf(n3);
            Log.v("quanxian", res);
            Process process1 = null;
            try {
                process1 = new ProcessBuilder("su").start();
                BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(process1.getOutputStream()));
                bw.write("chmod " + res + " " + file.getAbsolutePath() + "\n");
                bw.write("exit\n");
                bw.flush();
                bw.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
    private void changePer(File file) {
        Log.v("lujing2", file.getAbsolutePath());
        Process process1 = null;
        try {
            process1 = new ProcessBuilder("su").start();
            BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(process1.getOutputStream()));
            bw.write("chmod 777 " + file.getAbsolutePath() + "\n");
            bw.write("exit\n");
            bw.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    android.os.Handler mhandler=new android.os.Handler(){
        public void handleMessage(Message msg) {
            if (msg.what == 1) {
                File[] files =new File(mFilePath).listFiles();
                mFileList.clear();
                for (File file : files) {
                    mFileList.add(file);
                }
                mFileInfoAdapter.notifyDataSetChanged();

            }
            if (msg.what == 2) {
                File[] files =new File(mFilePath).listFiles();
                mFileList.clear();
                for (File file : files) {
                    mFileList.add(file);
                }
                mFileInfoAdapter.notifyDataSetChanged();
                deleteFile(mFile);

            }

        }
    };

    protected void initView() {
        setContentView(R.layout.fileinfolayout);
        mListView = (ListView) findViewById(R.id.listview);
        add=(ImageView)findViewById(R.id.add);
        topView = (LinearLayout)findViewById(R.id.top);
    }

    protected void initData() {
        preInfo=new Stack<>();
        mFilePath = getIntent().getStringExtra("fileinfo");
        int flag=getIntent().getIntExtra("flag",0);
        switch (flag){
            case 1:
                //获取及更改包的读写权限
                //保存到全局变量中
                preInfo.push(getPreInfo(new File(mFilePath)));;
                changePer(new File(mFilePath));
                getPreInfo(new File(mFilePath));

        }
        mPathList = new ArrayList<>();
        mFileList = new ArrayList<>();
    }



    protected void initEvent() {
        //加载初始根目录
        mFileList = loadFile(mFilePath);
        mFileInfoAdapter =new FileInfoAdapter(this,mFileList);
        mListView.setAdapter(mFileInfoAdapter);
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                TextView fileText = (TextView) view.findViewById(R.id.filetext);
                String name = fileText.getText().toString();
                //展开目录
                expandFile(name);
            }
        });
        mListView.setOnCreateContextMenuListener(new View.OnCreateContextMenuListener() {
            @Override
            public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
                menu.add(0,1,0,"删除");
                menu.add(0,2,0,"重命名");
                menu.add(0,3,0,"复制");
                menu.add(0,4,0,"移动");

            }
        });
        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createFile();
            }
        });

    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        // 得到当前被选中的item信息
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        int id = (int) info.id;
        File file = mFileList.get(id);
        switch (item.getItemId()) {
            //删除
            case 1:
                deleteFile(file);
                mFileList.remove(id);
                mFileInfoAdapter.notifyDataSetChanged();
                break;
            //重命名
            case 2:
                rename(file,id);
                break;
            //复制
            case 3:
                // topView.setVisibility(View.VISIBLE);
                copy(file,1);
                //topView.setVisibility(View.INVISIBLE);
                break;
            //移动
            case 4:
                moveFile(file);
                break;
        }
        return false;
    }
    //加载初始目录
    private List<File> loadFile(String path) {
        List<File> fileList = new ArrayList<>();
        File mfile = new File(path);
        if (mfile.exists()) {
            if (mfile.isDirectory()) {
                if (mfile.listFiles() != null) {
                    File[] files = mfile.listFiles();
                    for (File file : files) {
                        fileList.add(file);
                    }
                } else {
                    Toast.makeText(this, "文件目录为空", Toast.LENGTH_SHORT).show();
                }
            } else {
                fileList.add(mfile);
            }
        } else {
            Toast.makeText(this, "文件目录不存在", Toast.LENGTH_SHORT).show();
        }
        return fileList;
    }

    //展开目录
    private void expandFile(String name) {
        File file = new File(mFilePath + "/" + name);
        //保存，更改权限
        preInfo.push(getPreInfo(file));
        changePer(file);
        getPreInfo(file);
        if (file.isDirectory()) {
            //将上层目录保存到列表
            mPathList.add(mFilePath);
            //更新目录
            mFilePath = mFilePath + "/" + name;
            if (file.listFiles() == null || file.listFiles().length == 0) {
                mFileList.clear();
                mFileInfoAdapter.notifyDataSetChanged();
                Toast.makeText(FileInfoActivity.this, "目录为空", Toast.LENGTH_SHORT).show();
            } else {
                File[] files = file.listFiles();
                mFileList.clear();
                for (File file1 : files) {
                    mFileList.add(file1);
                }
                mFileInfoAdapter.notifyDataSetChanged();
            }
        } else {
            new OpenFileUtil(this).openFile(file);
            setPreInfo(file);
        }
    }

    //后退加载列表
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            setPreInfo(new File(mFilePath));
            //getPreInfo(new File(mFilePath));
            if ((mPathList.size() > 0)) {
                File files = new File(mPathList.get(mPathList.size() - 1));

                mFilePath=mPathList.get(mPathList.size() - 1);
                mPathList.remove(mPathList.size() - 1);
                mFileList.clear();
                for (File file : files.listFiles()) {
                    mFileList.add(file);
                }
                mFileInfoAdapter.notifyDataSetChanged();
                return true;
            }
            else
                return super.onKeyDown(keyCode,event);
        }
        else
            return super.onKeyDown(keyCode,event);
    }
    private void sendMsg(int n){
        Message msg=new Message();
        msg.what=n;
        mhandler.sendMessage(msg);
    }

   //移动
    private void moveFile(File file) {
        copy(file,2);
    }
    //复制
    private void copy(File file,int n) {
        mFile = file;
        final int num=n;
        new Thread(new Runnable() {
            public void run() {
                TextView copy = (TextView) topView.findViewById(R.id.sure);
                copy.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        File toFile = new File(mFilePath + "/" + mFile.getName());
                        if (toFile.exists()) {
                            Toast.makeText(FileInfoActivity.this, "文件已存在", Toast.LENGTH_SHORT).show();
                        } else {
                            if (mFile.isFile()) {
                                copyFile(mFile, toFile);
                            } else {
                                copyDirectory(mFile, toFile);
                            }
                        }
                        sendMsg(num);
                    }
                });
            }
        }).start();
    }

   //复制文件
    private void copyFile(File fromFile,File toFile) {
        InputStream is ;
        OutputStream os;
        byte[] arr=new byte[1024];
        int len;
        try {
            toFile.createNewFile();
            is= new FileInputStream(fromFile);
            os=new FileOutputStream(toFile);
            while ((len=is.read(arr))!=-1){
                os.write(arr,0,len);
                os.flush();
                }
            is.close();
            os.close();

            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }


    //复制目录

    private void copyDirectory(File fromFile,File toFile) {
        toFile.mkdir();
        File[] files=fromFile.listFiles();
        if(files.length==0||files==null){
           return;
        }
        else{
            for(File file :files){
                if(file.isFile()){
                    copyFile(file,new File(toFile.getAbsolutePath()+"/"+file.getName()));
                }
                else{
                    copyDirectory(file,new File(toFile.getAbsolutePath()+"/"+file.getName()));
                }
            }
        }
    }

    //重命名
    private void rename(File file,int id) {
        final View view=getLayoutInflater().from(this).inflate(R.layout.renamealertdialoglayout,null);
        final String oldPath=file.getAbsolutePath();
        final int fileId=id;
        new AlertDialog.Builder(this)
                .setTitle("重命名")
                .setView(view)
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        EditText name=(EditText)view.findViewById(R.id.name);
                        if(name.getText().toString().equals("")){
                            Toast.makeText(FileInfoActivity.this,"文件名不能为空",Toast.LENGTH_SHORT).show();
                        }
                        else{
                            File oldFile=new File(oldPath);
                            String newPath = mFilePath + "/" + name.getText().toString().trim();
                            File newFile=new File(newPath);
                            if(newFile.exists()) {
                                Toast.makeText(FileInfoActivity.this, "文件已存在", Toast.LENGTH_SHORT).show();
                            }else{
                                boolean res=oldFile.renameTo(newFile);
                                if(res==true){
                                    mFileList.clear();
                                    for(File file:new File(mFilePath).listFiles()){
                                        mFileList.add(file);
                                    }
                                    mFileInfoAdapter.notifyDataSetChanged();
                                    mListView.setSelection(fileId);
                                } else{
                                    Toast.makeText(FileInfoActivity.this,"重命名失败",Toast.LENGTH_SHORT).show();
                                }
                            }
                        }
                    }
                })
                .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                })
                .create().show();
    }


    //新建文件
    private void createFile() {

        final View view=getLayoutInflater().from(this).inflate(R.layout.alertdialoglayout,null);
        new AlertDialog.Builder(this)
                .setTitle("新建文件夹")
                .setView(view)
                .setPositiveButton("新建", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        EditText name=(EditText)view.findViewById(R.id.name);
                        if(name.getText().toString().equals("")){
                            Toast.makeText(FileInfoActivity.this,"文件名不能为空",Toast.LENGTH_SHORT).show();
                        }
                        else{
                            String path = mFilePath + "/" + name.getText().toString().trim();
                            File file=new File(path);
                            if(file.exists()){
                                Toast.makeText(FileInfoActivity.this,"文件已存在",Toast.LENGTH_SHORT).show();

                            }else{
                                file.mkdir();
                                mFileList.add(file);
                                mFileInfoAdapter.notifyDataSetChanged();
                                mListView.setSelection(mFileList.size()-1);
                            }

                        }

                        }

                })
                .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                })
                .create().show();

    }

    //删除文件
    private void deleteFile(File file) {
        //删除非目录文件及空文件夹
        if (file.isFile()||file.listFiles()==null||file.listFiles().length==0) {
            file.delete();
        }
        //删除非空文件夹
        else{
            for (File file1 : file.listFiles()) {
                deleteFile(file1);
            }
            file.delete();
        }
       /** if(!(file.exists())){
            Toast.makeText(FileInfoActivity.this,"删除成功",Toast.LENGTH_SHORT).show();
        }
        */
    }


}


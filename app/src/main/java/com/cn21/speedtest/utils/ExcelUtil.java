package com.cn21.speedtest.utils;

import android.content.Context;
import android.widget.Toast;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Date;

import jxl.Workbook;
import jxl.format.Border;
import jxl.format.BorderLineStyle;
import jxl.format.Colour;
import jxl.write.Label;
import jxl.write.WritableCellFormat;
import jxl.write.WritableFont;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;
import jxl.write.WriteException;

/**
 * Created by huangzhilong on 16/9/13.
 */
public class ExcelUtil {

    /**
     * 绘表
     * @param context
     * @throws Exception
     */
    public static void writeExcel(Context context) throws Exception {
        if(!SDCardUtils.isMounted()&&SDCardUtils.getAvailableStorage(context)>10000){
            Toast.makeText(context,"SD卡不可用",Toast.LENGTH_LONG).show();
        }else {
            File file;
            File dir;
            if (User.inChoosePakageName !=null){
                String url = User.inChoosePakageName +new Date().toString()+".xls";
                User.documents.add(url);
                 dir= new File(context.getExternalFilesDir(null).getPath());
                file = new File(dir, User.inChoosePakageName +new Date().toString()+".xls");
            }else {
                String url = "record_" + new Date().toString() + ".xls";
                User.documents.add(url);
                dir = new File(context.getExternalFilesDir(null).getPath());
                file = new File(dir, "record_" + new Date().toString() + ".xls");
            }
            if (!dir.exists()) {
                dir.mkdirs();
            }
            WritableWorkbook wwb = null;
            OutputStream os = null;
            try {
                LogUtil.e("a");
                os = new FileOutputStream(file);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
            try {
                LogUtil.e("b");
                wwb = Workbook.createWorkbook(os);
            } catch (IOException e) {
                e.printStackTrace();
            }
            WritableSheet sheet = wwb.createSheet("监测项记录", 0);
            String[] title = { "cpu %", "fps 帧/s", "Memory MB", "耗电量 mah/s","采集频率 ms/次" };
            Label label = null;
            for (int i = 0; i < title.length; i++) {
                // Label(x,y,z) 代表单元格的第x+1列，第y+1行, 内容z
                // 在Label对象的子对象中指明单元格的位置和内容
                label = new Label(i, 0, title[i], getHeader());
                // 将定义好的单元格添加到工作表中
                try {
                    sheet.addCell(label);
                } catch (WriteException e) {
                    e.printStackTrace();
                }
                if (i == 4) {
                    label = new Label(i, 1, String.valueOf(User.thread_time_interval), getHeader());
                    try {
                        sheet.addCell(label);
                    } catch (WriteException e) {
                        e.printStackTrace();
                    }
                }
                if (i < 4) {
                    for (int j = 0; j < User.totalCpuRate.get(i).size(); j++) {
                        label = new Label(i, j + 1, String.valueOf(User.totalCpuRate.get(i).get(j)), getHeader());
                        // 将定义好的单元格添加到工作表中
                        try {
                            sheet.addCell(label);
                        } catch (WriteException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
            // 写入数据
            try {
                wwb.write();
            } catch (IOException e) {
                e.printStackTrace();
            }
            // 关闭文件
            try {
                wwb.close();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (WriteException e) {
                e.printStackTrace();
            }
        }

    }

    /**
     * 表样式
     * @return
     */
    public static WritableCellFormat getHeader() {
        WritableFont font = new WritableFont(WritableFont.TIMES, 10,
                WritableFont.BOLD);// 定义字体
        try {
            font.setColour(Colour.BLUE);// 蓝色字体
        } catch (WriteException e1) {
            e1.printStackTrace();
        }
        WritableCellFormat format = new WritableCellFormat(font);
        try {
            format.setAlignment(jxl.format.Alignment.CENTRE);// 左右居中
            format.setVerticalAlignment(jxl.format.VerticalAlignment.CENTRE);// 上下居中
            format.setBorder(Border.ALL, BorderLineStyle.THIN,
                    Colour.BLACK);// 黑色边框
            format.setBackground(Colour.YELLOW);// 黄色背景
        } catch (WriteException e) {
            e.printStackTrace();
        }
        return format;
    }
}

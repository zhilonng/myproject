package com.cn21.speedtest.utils;

import android.content.Context;
import android.os.Environment;
import android.os.StatFs;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

public class SDCardUtils {
	/**
	 * 判断SD卡是否挂载
	 * 
	 * @return
	 */
	public static boolean isMounted() {
		return Environment.getExternalStorageState().equals(
				Environment.MEDIA_MOUNTED);
	}

	/**
	 * 得到SD卡的根路径
	 * @return
     */
	public static String getSDPath() {

		if (isMounted()) {
			return Environment.getExternalStorageDirectory().getAbsolutePath();
		}
		return null;
	}

	/**
	 * 将文件保存到SD卡中
	 * @param data
	 * @param path
	 * @param fileName
     * @return
     */
	public static boolean saveFileIntoSDCard(byte[] data, String path,
			String fileName) {

		if (isMounted()) {

			BufferedOutputStream bos = null;
			try {
				String filePath = getSDPath() + File.separator + path;
				File file = new File(filePath);
				if (!file.exists()) {
					file.mkdirs();
				}

				bos = new BufferedOutputStream(new FileOutputStream(new File(
						file, fileName)));
				bos.write(data, 0, data.length);
				bos.flush();

				return true;
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				if (bos != null) {
					try {
						bos.close();
					} catch (IOException e1) {
						e1.printStackTrace();
					}
				}
			}

		}

		return false;
	}

	/**
	 * 从SD卡中取出存储的文件
	 * @param filePath
	 * @return
     */
	public static byte[] getFileFromSDCard(String filePath) {

		if (isMounted()) {
			File file = new File(filePath);
			BufferedInputStream bis = null;
			ByteArrayOutputStream baos = null;
			if (file.exists()) {
				try {
					baos = new ByteArrayOutputStream();
					bis = new BufferedInputStream(new FileInputStream(file));
					int len = 0;
					byte[] buffer = new byte[1024 * 8];
					while ((len = bis.read(buffer)) != -1) {
						baos.write(buffer, 0, len);
						baos.flush();
					}

					return baos.toByteArray();
				} catch (Exception e) {
					e.printStackTrace();
				} finally {
					if (bis != null) {
						try {
							bis.close();
							baos.close();
						} catch (IOException e) {
							e.printStackTrace();
						}
					}
				}

			}
		}

		return null;

	}
	/** 获取SD可用容量 */
	public static long getAvailableStorage(Context context) {
		String root = context.getExternalFilesDir(null).getPath();
		StatFs statFs = new StatFs(root);
		long blockSize = statFs.getBlockSize();
		long availableBlocks = statFs.getAvailableBlocks();
		long availableSize = blockSize * availableBlocks;
		// Formatter.formatFileSize(context, availableSize);
		return availableSize;
	}
	public static boolean deleteFile(File file){
		if (file.exists() == false){
			return false;
		}else {
			if (file.isFile()) {
				file.delete();
				return true;
			}
			if (file.isDirectory()) {
				File[] childFile = file.listFiles();
				if (childFile == null || childFile.length == 0) {
					return true;
				}
				for (File f : childFile) {
					deleteFile(f);
				}
			}
		}
		return true;
	}
}

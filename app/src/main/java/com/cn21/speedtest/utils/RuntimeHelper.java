package com.cn21.speedtest.utils;

import android.os.Build;
import android.text.TextUtils;

import com.cn21.speedtest.utils.LogUtil;

import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.util.List;

/**
 * Helper functions for running processes.
 * @author nolan
 *
 */
public class RuntimeHelper {

    /**
     * Exec the arguments, using root if necessary.
     *
     * @param args
     */
    public static Process exec(List<String> args) throws IOException {
        // since JellyBean, su do is required to read other apps' logs
        //若sdk版本大于4.1，想要获取日志必须加上su
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN
            //&& !SuperUserHelper.isFailedToObtainRoot()
                ) {
            Process process = Runtime.getRuntime().exec("su");

            PrintStream outputStream = null;
            try {
                outputStream = new PrintStream(new BufferedOutputStream(process.getOutputStream(), 8192));
                outputStream.println(TextUtils.join(" ", args));
                outputStream.flush();
            } finally {
                if (outputStream != null) {
                    outputStream.close();
                }
            }

            return process;
        }

        //若版本小于4.1，可直接获取，不用加su，也不用root权限
        return Runtime.getRuntime().exec(ArrayUtil.toArray(args, String.class));
    }
    public static void destroy(Process process) {
        // if we're in JellyBean, then we need to kill the process as root, which requires all this
        // extra UnixProcess logic
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN
                && !SuperUserHelper.isFailedToObtainRoot()) {
            SuperUserHelper.destroy(process);
        } else {
            process.destroy();
        }
    }


}
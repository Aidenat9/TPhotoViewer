package com.github.tianmu19.tphotoviewerlibrary;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Environment;

import com.klogutil.KLog;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * @author sunwei
 * email：tianmu19@gmail.com
 * date：2019/11/19 10:37
 * package：com.github.tianmu19.tphotoviewerlibrary
 * version：1.0
 * <p>description：              </p>
 */
public class FileUtil {
    private static final FileUtil ourInstance = new FileUtil();

    public static FileUtil getInstance() {
        return ourInstance;
    }

    private FileUtil() {
    }
    public static final String FOLDER = "/savedPictures/";
    public void saveIMG(String fileName, Bitmap bitmap, Context context) {
        //可访问的图片文件夹
        String dirPath = Environment.getExternalStorageDirectory().getAbsolutePath()+FOLDER;
        //在图片文件夹下新建自己的文件夹，保存图片
        File appDir = new File(dirPath);
        if (!appDir.exists()) {
            appDir.mkdirs();
        }
        //命名图片并保存
        File currentFile = new File(appDir, fileName);
        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(currentFile);
            bitmap.compress(Bitmap.CompressFormat.JPEG,86 , fos);//质量为100表示设置压缩率为0
            KLog.e("download: "+currentFile.getAbsolutePath());
            fos.flush();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (fos != null) {
                    fos.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        // 通知图库更新
        context.sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE,
                Uri.fromFile(new File(currentFile.getPath()))));
    }
}

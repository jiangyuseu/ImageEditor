package com.baozou.imageeditor.utils;

import android.graphics.Bitmap;
import android.os.Environment;

import com.baozou.imageeditor.event.SaveImgeEvent;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import de.greenrobot.event.EventBus;

/**
 * Created by jiangyu on 2016/4/11.
 */
public class BitmapUtils {

    /**
     * 将合成图片保存到本地相册
     */
    public static void saveImage(Bitmap bmp) {
        File appDir = new File(Environment.getExternalStorageDirectory() + "/DCIM/Camera");
        if (!appDir.exists()) {
            appDir.mkdir();
        }
        String fileName = System.currentTimeMillis() + ".jpg";
        File file = new File(appDir, fileName);

        try {
            FileOutputStream fos = new FileOutputStream(file);
            bmp.compress(Bitmap.CompressFormat.JPEG, 100, fos);
            fos.flush();
            fos.close();
            EventBus.getDefault().post(new SaveImgeEvent(appDir + "/" + fileName));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

package com.ftp.client.engine.app.folders;

import android.content.Context;
import android.os.Environment;
import android.os.storage.StorageManager;
import java.lang.reflect.Array;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import com.ftp.client.AppController;

public class Storage {

    private static volatile Storage Instance = null;
    public static Storage getInstance() {
        Storage localInstance = Instance;
        if (localInstance == null) {
            synchronized (Storage.class) {
                localInstance = Instance;
                if (localInstance == null) {
                    Instance = localInstance = new Storage();
                }
            }
        }
        return localInstance;
    }

    public String getExternalStorageDirectory(){
        return Environment.getExternalStorageDirectory().getAbsolutePath();
    }

    public String getInternalStorageDirectory(){
        return getStoragePath(true);
    }


    /**
     *get the internal or outside sd card path
     * @param is_removale true is is outside sd card
     * */
    public static String getStoragePath(boolean is_removale) {

        StorageManager mStorageManager = (StorageManager) AppController.getContext().getSystemService(Context.STORAGE_SERVICE);
        Class<?> storageVolumeClazz = null;
        try {
            storageVolumeClazz = Class.forName("android.os.storage.StorageVolume");
            Method getVolumeList = mStorageManager.getClass().getMethod("getVolumeList");
            Method getPath = storageVolumeClazz.getMethod("getPath");
            Method isRemovable = storageVolumeClazz.getMethod("isRemovable");
            Object result = getVolumeList.invoke(mStorageManager);
            final int length = Array.getLength(result);
            for (int i = 0; i < length; i++) {
                Object storageVolumeElement = Array.get(result, i);
                String path = (String) getPath.invoke(storageVolumeElement);
                boolean removable = (Boolean) isRemovable.invoke(storageVolumeElement);
                if (is_removale == removable) {
                    return path;
                }
            }
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        return null;
    }
}



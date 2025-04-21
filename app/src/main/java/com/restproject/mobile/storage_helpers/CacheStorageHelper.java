package com.restproject.mobile.storage_helpers;

import android.content.Context;

import com.restproject.mobile.exception.ApplicationException;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.nio.charset.StandardCharsets;

public class CacheStorageHelper {

    private void writeFC(Context context, String fileName, String data) throws ApplicationException {
        File cacheDir = context.getCacheDir();
        try (FileOutputStream fileOut = new FileOutputStream(new File(cacheDir, fileName))) {
            fileOut.write(data.getBytes(StandardCharsets.UTF_8));
            System.out.println("'writeES' successfully in " + fileName);
        } catch (Exception e) {
            throw new ApplicationException("Error from writeFC: " + e.getMessage(), e);
        }

    }

    private String readFC(Context context, String filename) throws ApplicationException{
        File cacheDir = context.getCacheDir();
        try (FileInputStream fin= new FileInputStream(new File(cacheDir, filename))){
            byte[] buffer = new byte[1024];
            int length = 0;
            length = fin.read(buffer);
            return new String(buffer,0,length);
        }  catch (Exception e) {
            throw new ApplicationException("Error from readFC: " + e.getMessage(), e);
        }
    }

}

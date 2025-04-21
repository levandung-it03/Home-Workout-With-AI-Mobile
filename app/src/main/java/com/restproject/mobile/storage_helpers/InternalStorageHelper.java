package com.restproject.mobile.storage_helpers;

import static android.content.Context.MODE_PRIVATE;

import android.content.Context;

import com.restproject.mobile.exception.ApplicationException;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

public class InternalStorageHelper {

    public static void writeIS(Context context, String fileName, String dataLine)
            throws ApplicationException{
        try(FileOutputStream fot = context.openFileOutput(fileName, MODE_PRIVATE)) {
            fot.write(dataLine.getBytes(StandardCharsets.UTF_8));
            System.out.println("'writeIS' successfully in " + fileName);
        } catch (IOException e) {
            throw new ApplicationException("Error from writeIS: " + e.getMessage(), e);
        }
    }

    public static String readIS(Context context, String fileName) throws ApplicationException {
        try(FileInputStream fin = context.openFileInput(fileName)) {
            byte[] buffer = new byte[1024];
            int length = 0;
            length = fin.read(buffer);
            return new String(buffer, 0, length);
        } catch (IOException e) {
            throw new ApplicationException("Error from readIS: " + e.getMessage(), e);
        }
    }
}

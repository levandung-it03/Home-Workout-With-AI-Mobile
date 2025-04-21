package com.restproject.mobile.storage_helpers;

import android.content.Context;

import com.restproject.mobile.exception.ApplicationException;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.util.Scanner;

public class ExternalStorageHelper {

    private void writeES(Context context, String fileName, String data) throws ApplicationException {
        String sdcard = context.getExternalFilesDir("") + "/" + fileName;
        try {
            OutputStreamWriter writer = new OutputStreamWriter(new FileOutputStream(sdcard));
            writer.write(data);
            writer.close();
            System.out.println("'writeES' successfully in " + fileName);
        } catch (Exception e) {
            throw new ApplicationException("Error from readIS: " + e.getMessage(), e);
        }
    }

    private String readES(Context context, String filename) throws ApplicationException {
        String sdcard = context.getExternalFilesDir("") + "/" + filename;
        try {
            Scanner scan = new Scanner(new File(sdcard));
            StringBuilder data = new StringBuilder();
            while (scan.hasNext()) {
                data.append(scan.nextLine()).append("\n");
            }
            scan.close();
            return data.toString();
        } catch (Exception e) {
            throw new ApplicationException("Error from readIS: " + e.getMessage(), e);
        }
    }
}

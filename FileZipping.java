package com.segmentationfault;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

public class FileZipping {
    public static void zipFile(String filePath) {
        byte[] buffer = new byte[1024];
        try{
            FileOutputStream fos = new FileOutputStream(filePath+".zip");
            ZipOutputStream zos = new ZipOutputStream(fos);
            File file = new File(filePath);
            ZipEntry ze= new ZipEntry(file.getName());
            zos.putNextEntry(ze);
            FileInputStream in = new FileInputStream(filePath);
            int len;
            while ((len = in.read(buffer)) > 0) {
                zos.write(buffer, 0, len);
            }
            in.close();
            zos.closeEntry();
            zos.close();
        }catch(IOException ex){
            ex.printStackTrace();
        }
    }
}

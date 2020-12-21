package com.segmentationfault;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

public class FolderZipping {
    List fileList;
    private String outputZipFile;
    private String srcFolder;
    FolderZipping(){
        fileList = new ArrayList();
        srcFolder = "";
        outputZipFile = "";
    }
    //zips to file location
    public void zipIt(String zipFile){
        //C:\Users\HP\Desktop\School\Fall 2020\Astro
        //C:\Users\HP\Desktop\School\Fall 2020\Astro.zip
        byte[] buffer = new byte[1024];
        try{
            FileOutputStream fos = new FileOutputStream(zipFile);
            ZipOutputStream zos = new ZipOutputStream(fos);
            for(Object file : this.fileList){
                ZipEntry ze= new ZipEntry((String) file);
                zos.putNextEntry(ze);
                FileInputStream in =
                        new FileInputStream(srcFolder + File.separator + file);
                int len;
                while ((len = in.read(buffer)) > 0) {
                    zos.write(buffer, 0, len);
                }
                in.close();
            }
            zos.closeEntry();
            zos.close();
        }catch(IOException ex){
            ex.printStackTrace();
        }
    }

    public void emptyFileList(){
        fileList.clear();
    }

    public void setDirectory(String directory){
        srcFolder = directory;
    }
    //this function traverses the given directory and obtains all the files and adds the files into the FileList, the paramter is the directory
    public void generateFileList(File node){
        //add file only
        if(node.isFile()){
            fileList.add(generateZipEntry(node.getAbsoluteFile().toString()));
        }
        if(node.isDirectory()){
            String[] subNote = node.list();
            for(String filename : subNote){
                generateFileList(new File(node, filename));
            }
        }
    }

    //this method formats the file path for the zip, its parameter is the file path of the file and the return value is the formatted file path
    private String generateZipEntry(String file){
        return file.substring(srcFolder.length()+1, file.length());
    }
}

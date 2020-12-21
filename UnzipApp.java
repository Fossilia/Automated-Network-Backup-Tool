import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public class UnzipApp {

    List fileList;
    private static final String inputZipFile = "/Users/ahmadalghizi/Desktop/Assignment_3.zip";
    private static final String outputFolder = "/Users/ahmadalghizi/Documents/Assignment_3";

    public static void main( String[] args )
    {
        UnzipApp unZip = new UnzipApp();
        unZip.unZipIt(inputZipFile,outputFolder);
    }
    public void unZipIt(String zipFile, String outputFolder){
        byte[] buffer = new byte[1024];
        try{
            File folder = new File(outputFolder);
            if(!folder.exists()){
                folder.mkdir();
            }
            //get the zip file content
            ZipInputStream zis =  new ZipInputStream(new FileInputStream(zipFile));
            //get the zipped file list entry
            ZipEntry ze = zis.getNextEntry();
            while(ze!=null){
                String fileName = ze.getName();
                File newFile = new File(outputFolder + File.separator + fileName);
                System.out.println("file unzip : "+ newFile.getAbsoluteFile());
                new File(newFile.getParent()).mkdirs();
                FileOutputStream fos = new FileOutputStream(newFile);
                int len;
                while ((len = zis.read(buffer)) > 0) {
                    fos.write(buffer, 0, len);
                }
                fos.close();
                ze = zis.getNextEntry();
            }
            zis.closeEntry();
            zis.close();
            System.out.println("Done");
        }catch(IOException ex){
            ex.printStackTrace();
        }
    }
}

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

public class FileZipping {
    public static void main(String[] args) {
        FileZipping.zipFile();
    }
    public static void zipFile() {
        byte[] buffer = new byte[1024];
        try{
            FileOutputStream fos = new FileOutputStream("/Users/ahmadalghizi/Desktop/test.zip");
            ZipOutputStream zos = new ZipOutputStream(fos);
            ZipEntry ze= new ZipEntry("testing1.txt");
            zos.putNextEntry(ze);
            FileInputStream in = new FileInputStream("/Users/ahmadalghizi/Desktop/test.txt");
            int len;
            while ((len = in.read(buffer)) > 0) {
                zos.write(buffer, 0, len);
            }
            in.close();
            zos.closeEntry();
            //remember close it
            zos.close();
            System.out.println("Finished!");
        }catch(IOException ex){
            ex.printStackTrace();
        }
    }
}

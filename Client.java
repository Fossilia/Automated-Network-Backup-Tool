import java.awt.*;
import java.io.*;
import java.net.Socket;
import java.net.UnknownHostException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

public class Client {
    Controller controller;
    FileInputStream fis;
    BufferedInputStream bis;
    OutputStream os;
    Socket sock;
    DataOutputStream dos;
    DataInputStream dis;

    File fileToSend;
    String filename;

    public Client(){}

    public void start(String ip) throws IOException, InterruptedException {
        sock = new Socket(ip, 55588);
        System.out.println("Connecting...");
        dis = new DataInputStream(new BufferedInputStream(sock.getInputStream()));
        System.out.println("Connected.");
        startSleeper();
        sock.close();

    }

    public void startSleeper(){
        Thread t1 = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    runBackupProcess();
                } catch (InterruptedException e) {
                    System.out.println("Run Backup failed");
                }
            }
        });
        t1.start();
    }

    public void runBackupProcess() throws InterruptedException {
        while(true){
            try {
                List<String> allLines = Files.readAllLines(Paths.get("applicationInfo"));
                int counter = 1;
                for (String line : allLines){
                    if (counter == 3) {
                        sendFile(line);
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            Thread.sleep(controller.getTime());
        }
    }

    private void sendFile(String filepath){
        try {

            // send file
            //----------------sending file name------------------------------------
            dos = new DataOutputStream(new BufferedOutputStream(sock.getOutputStream()));
            //System.out.println("Sending filename:");
                fileToSend = new File(filepath);
                dos.writeUTF(fileToSend.getName()); //the files name to be sent
                dos.flush();
                //System.out.println("filname sent");

                //-------------------------sending file----------------------------
                //fileToSend = new File("C:\\Users\\Faisal\\Documents\\GitHub\\Automated-network-backup-tool\\src\\com\\segmentationfault\\ClientFiles\\testfile.mp4");
                byte [] mybytearray  = new byte [(int)fileToSend.length()];
                fis = new FileInputStream(fileToSend);
                bis = new BufferedInputStream(fis);
                bis.read(mybytearray,0,mybytearray.length);
                os = sock.getOutputStream();
                os.write(mybytearray,0,mybytearray.length);
                os.flush();


            fis.close();
            bis.close();
            os.close();
            dos.close();
            dis.close();

        } catch (UnknownHostException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
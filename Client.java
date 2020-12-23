
import java.io.*;
import java.net.ServerSocket;
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
    String server_ip;
    File fileToRecieve;

    public Client(){

    }

    public void start(String ip, int lineNum) throws IOException, InterruptedException {
        server_ip = ip;
        List<String> allLines = Files.readAllLines(Paths.get("applicationInfo"));
        Thread t1 = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    runBackupProcess(lineNum);
                } catch (InterruptedException e) {
                    System.out.println("Run Backup failed");
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
        t1.setDaemon(true);
        t1.start();
    }


    public void runBackupProcess(int lineNum) throws InterruptedException, IOException {
        List<String> allLines = null;
        while(true){

            sock = new Socket(server_ip, 55588);
            //System.out.println("Connecting...");
            dis = new DataInputStream(new BufferedInputStream(sock.getInputStream()));
            //System.out.println("Connected.");

            try {
                allLines = Files.readAllLines(Paths.get("applicationInfo"));
                sendFile(allLines.get(lineNum));
                //sock.close();
                Runtime.getRuntime().addShutdownHook(new Thread(() -> {
                    try {
                        sock.close();
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                }));
            } catch (IOException e) {
                e.printStackTrace();
            }
            String counterNum = allLines.get(lineNum + 1);
            int time = Integer.parseInt(counterNum.split(" ")[1]);
            //int time = Integer.parseInt(allLines.get(0).split(" ")[1]);
            Thread.sleep(time);
        }
    }

    private void sendFile(String filepath){
        try {
            // send file
            //----------------sending file name------------------------------------
            dos = new DataOutputStream(new BufferedOutputStream(sock.getOutputStream()));
            //System.out.println("Sending filename:");
            fileToRecieve = new File(filepath);
            dos.writeUTF("FILE_TRANSFER "+ fileToRecieve.getName()); //the files name to be sent with the command
            dos.flush();
            //System.out.println("filename of ["+fileToSend.getName()+"] sent.");

            //-------------------------sending file----------------------------
            //fileToSend = new File("C:\\Users\\Faisal\\Documents\\GitHub\\Automated-network-backup-tool\\src\\com\\segmentationfault\\ClientFiles\\testfile.mp4");
            byte [] byte_data  = new byte [(int) fileToRecieve.length()];
            fis = new FileInputStream(fileToRecieve);
            bis = new BufferedInputStream(fis);
            bis.read(byte_data,0,byte_data.length);
            os = sock.getOutputStream();
            os.write(byte_data,0,byte_data.length);
            os.flush();
            //System.out.println("file-data of ["+fileToSend.getName()+"] sent.");


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

    public void startReceivingFile(String filepath){
        Thread t1 = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    receiveFile(filepath);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
        t1.setDaemon(true);
        t1.start();
    }

    public void receiveFile(String filepath) throws IOException {

        FileInputStream fis;
        BufferedInputStream bis;
        OutputStream os;
        sock = new Socket(server_ip, 55588);
        DataOutputStream dos;
        DataInputStream dis;
        String name;
        //System.out.println(filepath);
        //System.out.println("Connecting to Page...");
        //dis = new DataInputStream(new BufferedInputStream(sock.getInputStream()));
        dos = new DataOutputStream(new BufferedOutputStream(sock.getOutputStream()));
        //System.out.println("Connected to Page.");


        //System.out.println("Sending filename:");
        fileToRecieve = new File(filepath);
        String ext = getExtension(fileToRecieve);
        String simple_filename = getFileName(fileToRecieve);
        dos.writeUTF("REQUEST_BACKUP "+ simple_filename); //the files name to be sent with the command
        dos.flush();

        int bytesRead;
        int current;
        FileOutputStream fos;
        BufferedOutputStream bos;

        byte [] mybytearray  = new byte [6022386];
        InputStream is = sock.getInputStream();
        File dateFolder = new File("ReceivedClientFiles\\");
        dateFolder.mkdirs();
        fos = new FileOutputStream("ReceivedClientFiles\\"+fileToRecieve.getName());
        bos = new BufferedOutputStream(fos);
        bytesRead = is.read(mybytearray,0,mybytearray.length);
        current = bytesRead;

        do {
            bytesRead = is.read(mybytearray, current, (mybytearray.length-current));
            if(bytesRead >= 0){
                current += bytesRead;
            }
        }
        while(bytesRead > -1);

        bos.write(mybytearray, 0 , current);
        bos.flush();

        System.out.println("Recieved latest backup of file: "+fileToRecieve.getName());
        is.close();
        fos.close();
        bos.close();
        sock.close();
        //dis.close();
    }

    public static String getExtension(File f){
        String path = f.getName();
        if(path.lastIndexOf(".")!=-1){
            return path.substring(path.lastIndexOf(".")+1);
        }
        else{
            return null;
        }
    }

    public static String getFileName(File f){
        String path = f.getName();
        if(path.lastIndexOf(".")!=-1){
            return path.substring(0, path.indexOf("."));
        }
        else{
            return null;
        }
    }
}
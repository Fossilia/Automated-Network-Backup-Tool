package com.segmentationfault;

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
    String server_ip;
    File fileToSend;

    public Client(){}

    public void start(String ip) throws IOException, InterruptedException {
        server_ip = ip;
        Thread t1 = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    runBackupProcess();
                } catch (InterruptedException e) {
                    System.out.println("Run Backup failed");
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
        t1.start();
    }

    public void startSleeper(){

    }

    public void runBackupProcess() throws InterruptedException, IOException {
        List<String> allLines = null;
        sock = new Socket(server_ip, 55588);
        //System.out.println("Connecting...");
        dis = new DataInputStream(new BufferedInputStream(sock.getInputStream()));
        //System.out.println("Connected.");

        while(true){
            try {
                allLines = Files.readAllLines(Paths.get("applicationInfo")); //turn lines into list
                int counter = 1;
                String paths = "";
                String fullpaths = "";
                for (String line : allLines){
                    if (counter >= 3) { //if the current line is line 3 or more (those would be the path lines)
                        //sendFile(line); //send paths
                        File file = new File(line);
                        paths=paths+file.getName()+" ";
                        fullpaths=fullpaths+line+" ";
                    }
                    counter++;
                }
                sendFile(paths, fullpaths);
            } catch (IOException e) {
                e.printStackTrace();
            }
            int time = Integer.parseInt(allLines.get(0).split(" ")[1]);
            Thread.sleep(10000);
            sock.close();
        }
    }

    private void sendFile(String paths, String fullpaths){
        try {

            // send file
            //----------------sending file name------------------------------------
            dos = new DataOutputStream(new BufferedOutputStream(sock.getOutputStream()));
            //System.out.println("Sending filename:");
            //fileToSend = new File(filepath);
            dos.writeUTF("FILE_TRANSFER "+paths); //the files name to be sent with the command
            dos.flush();
            //System.out.println("filename of ["+fileToSend.getName()+"] sent.");
            System.out.println(paths+" | "+fullpaths);
            //String [] split_paths = paths.split(" ");
            String [] split_fullpaths = fullpaths.split(" ");

            for (int i = 0; i < split_fullpaths.length; i++) {
                System.out.println(split_fullpaths);
                fileToSend = new File(split_fullpaths[i]);
                //-------------------------sending file----------------------------
                //fileToSend = new File("C:\\Users\\Faisal\\Documents\\GitHub\\Automated-network-backup-tool\\src\\com\\segmentationfault\\ClientFiles\\testfile.mp4");
                byte [] byte_data  = new byte [(int)fileToSend.length()];
                fis = new FileInputStream(fileToSend);
                bis = new BufferedInputStream(fis);
                bis.read(byte_data,0,byte_data.length);
                os = sock.getOutputStream();
                os.write(byte_data,0,byte_data.length);
                os.flush();
                //System.out.println("file-data of ["+fileToSend.getName()+"] sent.");

                //closing streams
            }

            fis.close();
            bis.close();
            os.close();
            dos.close();
            dis.close();
            //sock.close();

        } catch (UnknownHostException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
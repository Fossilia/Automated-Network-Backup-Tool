package com.segmentationfault;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Calendar;
import java.util.Date;

public class Server {
    FileInputStream fis;
    BufferedInputStream bis;
    OutputStream os;
    ServerSocket servsock = null;
    Socket sock;
    DataOutputStream dos;
    DataInputStream dis;
    String name;
    String raw_message;
    String [] split_message;

    int bytesRead;
    int current;
    FileOutputStream fos;
    BufferedOutputStream bos;

    public Server(){}

    public void start(){
        /*try {
            fos = new FileOutputStream("ServerFiles\\test.txt");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }*/
        try {
            servsock = new ServerSocket(55588); //start server by opening a server socket
            while (true) {
                System.out.println("Waiting for client...");
                sock = servsock.accept();
                System.out.println("Connected to client: " + sock);
                File file = new File("ServerFiles\\"+sock.getInetAddress().toString().substring(1));
                //file.mkdir();

                System.out.println(sock.getInetAddress().toString().substring(1)+" ");
                //-----------------------receive file to be tested----------------------------
                dis = new DataInputStream(new BufferedInputStream(sock.getInputStream()));
                raw_message = dis.readUTF();
                split_message = raw_message.split(" ");
                //----------------------receive name of backup--------------------------------

                //if(filename.equals())
                if(split_message[0].equals("FILE_TRANSFER")){
                    System.out.println("File name recieved: "+ raw_message);
                    String filename = split_message[1];

                    // receive backup
                    byte [] byte_data  = new byte [602238600];
                    InputStream is = sock.getInputStream();
                    String addr = sock.getInetAddress().toString().substring(1);
                    String date = Calendar.getInstance().getTime().toString().replace(":", "-");
                    File dateFolder = new File("ServerFiles\\"+addr+"\\"+date);
                    dateFolder.mkdirs();
                    fos = new FileOutputStream(dateFolder.getPath()+"\\"+filename);
                    bos = new BufferedOutputStream(fos);
                    bytesRead = is.read(byte_data,0,byte_data.length);
                    current = bytesRead;

                    do {
                        bytesRead = is.read(byte_data, current, (byte_data.length-current));
                        if(bytesRead >= 0){
                            current += bytesRead;
                        }
                    }
                    while(bytesRead > -1);

                    bos.write(byte_data, 0 , current);
                    bos.flush();

                    System.out.println("Submission recieved from "+ filename);
                    is.close();
                }
                else if(split_message[0].equals("REQUEST_BACKUP")){
                    String backup_requested = split_message[1];
                    //recieve file
                }

                fos.close();
                bos.close();
                sock.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }  finally {
            assert servsock != null;
            try {
                servsock.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}

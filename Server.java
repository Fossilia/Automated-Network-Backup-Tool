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
        try {
            servsock = new ServerSocket(55588); //start server by opening a server socket
            while (true) {
                System.out.println("Waiting for client...");
                sock = servsock.accept();
                System.out.println("Connected to client: " + sock);

                dis = new DataInputStream(new BufferedInputStream(sock.getInputStream()));
                raw_message = dis.readUTF();
                split_message = raw_message.split(" ");

                //creating backups folder
                String addr = sock.getInetAddress().toString().substring(1);
                String date = Calendar.getInstance().getTime().toString().replace(":", "-");
                File dateFolder = new File("ServerFiles\\"+addr+"\\"+date);
                dateFolder.mkdirs();

                //if the client wants to transfer files
                if(split_message[0].equals("FILE_TRANSFER")){
                    //System.out.println(split_message.length);
                    for (int i = 1; i < split_message.length; i++) {
                        System.out.println(i);
                        System.out.println("File name recieved: "+ split_message[i]);
                        String filename = split_message[i];

                        // receive backup
                        byte [] byte_data  = new byte [602238600];
                        InputStream is = sock.getInputStream();

                        fos = new FileOutputStream(dateFolder.getPath()+"\\"+filename); //save the file
                        bos = new BufferedOutputStream(fos);
                        bytesRead = is.read(byte_data,0,byte_data.length);
                        current = bytesRead;

                        do {
                            bytesRead = is.read(byte_data, current, (byte_data.length-current)); //load bytes into file
                            if(bytesRead >= 0){
                                current += bytesRead;
                            }
                        }
                        while(bytesRead > -1);

                        bos.write(byte_data, 0 , current);
                        bos.flush();

                        System.out.println("file recieved");
                        is.close();
                    }

                }
                else if(split_message[0].equals("REQUEST_BACKUP")){
                    String backup_requested = split_message[1];
                    //recieve file
                }
                //-----------------------receive message from client----------------------------


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

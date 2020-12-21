package com.segmentationfault;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

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
                    byte [] mybytearray  = new byte [602238600];
                    InputStream is = sock.getInputStream();
                    fos = new FileOutputStream(filename);
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

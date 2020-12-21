package com.segmentationfault;

import java.awt.*;
import java.io.*;
import java.net.Socket;
import java.net.UnknownHostException;

public class Client {
    FileInputStream fis;
    BufferedInputStream bis;
    OutputStream os;
    Socket sock;
    DataOutputStream dos;
    DataInputStream dis;

    File fileToSend;
    String filename;

    public Client(){}

    public void start(){
        try {
            sock = new Socket("127.0.0.1", 55588);
            System.out.println("Connecting...");
            dis = new DataInputStream(new BufferedInputStream(sock.getInputStream()));
            System.out.println("Connected.");
            // send file

            //----------------sending file name------------------------------------
            dos = new DataOutputStream(new BufferedOutputStream(sock.getOutputStream()));
            //System.out.println("Sending filename:");
            filename = "FILE_TRANSFER testfile.mp4";
            dos.writeUTF(filename); //the files name to be sent
            dos.flush();
            //System.out.println("filname sent");

            //-------------------------sending file----------------------------
            fileToSend = new File("C:\\Users\\Faisal\\Documents\\GitHub\\Automated-network-backup-tool\\src\\com\\segmentationfault\\ClientFiles\\testfile.mp4");
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
            sock.close();
            dis.close();

        } catch (UnknownHostException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

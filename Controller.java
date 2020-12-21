package com.segmentationfault;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.*;

import static java.lang.Character.isDigit;
import static java.lang.Character.isISOControl;

public class Controller {
    private Scanner sc;
    private int[] counter;
    private FolderZipping folderZipping;
    private FileZipping fileZipping;
    private long time;
    private Client client;
    private String serverIP;

    public Controller(){
        sc = new Scanner(System.in);
        counter = new int[3];
        folderZipping = new FolderZipping();
        client = new Client();
    }

    public void startApplication() throws IOException, InterruptedException {
        File appInfo = new File("applicationInfo");
        if (appInfo.exists()){
            List<String> allLines = Files.readAllLines(Paths.get("applicationInfo"));
            String [] serverIP_split =  allLines.get(0).split(" ");
            serverIP = serverIP_split[serverIP_split.length-1];
            client.start(serverIP);
            System.out.println("Backup service started");
            displayMenu();
        }
        else{
            setApplicationEnvironment();
        }
    }

    private void displayMenu() throws IOException {
        System.out.println("Welcome to the Backup File Server Admin!");
        while (true) {
            System.out.println("What would you like to do?\n" +
                    "1. Add more files to back up on the server\n" +
                    "2. Request back up from the server\n" +
                    "3. Exit\n" +
                    "Enter the number beside the action you would like to perform:");
            String input = sc.nextLine();
            int choice = -1;
            while (!isNumber(input) || (choice = Integer.parseInt(input)) > 3 || (choice <= 0)) {
                System.out.println("Incorrect input, please try again!");
                input = sc.nextLine();
            }
            if (choice == 1) {
                displayFiles();
                addFileDirectories();
            } else if (choice == 2) {
                displayFiles();
                retrieveFile();
            } else break;
        }

    }

    private void displayFiles(){
        System.out.println("Here are the folders/files that you have currently sent for backup: ");
        int count = 1;
        try {
            List<String> allLines = Files.readAllLines(Paths.get("applicationInfo"));
            for (String line : allLines) {
                if (line.startsWith("C:")) System.out.println((count-2) +". " + line);
                count++;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void addFileDirectories() throws IOException {
        Path filePath = Paths.get("applicationInfo");
        String directory = "";
        while (true){
            System.out.println("Enter the ABSOLUTE PATH of the file/directory that you would like to back up (Enter exit to quit): ");
            directory = sc.nextLine();
            Path path = Paths.get(directory);
            if (directory.toLowerCase().equals("exit")) break;
            else {
                if (Files.exists(path)) {
                    File file = new File(directory);
                    if (file.isDirectory()) {
                        folderZipping.setDirectory(directory);
                        folderZipping.emptyFileList();
                        folderZipping.generateFileList(new File(directory));
                        String outputZipFile = directory + ".zip";
                        folderZipping.zipIt(outputZipFile);
                        System.out.println("Successfully created zip file " + outputZipFile);
                        Files.write(filePath, outputZipFile.getBytes(), StandardOpenOption.APPEND);

                    }
                    else {
                        if (!alreadyExists(directory)) {
                            FileZipping.zipFile(directory);
                            directory = directory+".zip\n";
                            Files.write(filePath, directory.getBytes(), StandardOpenOption.APPEND);
                        }
                        else{
                            System.out.println("Directory already exists");
                        }
                    }
                } else {
                    System.out.println("Directory does not exist, please try again");
                }
            }
        }
    }

    private boolean alreadyExists(String directory){
        try {
            List<String> allLines = Files.readAllLines(Paths.get("applicationInfo"));
            for (String line : allLines) {
                if (line.equals(directory)){
                    return true;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

    private void retrieveFile() {
        ArrayList<String> requestedFiles = new ArrayList<>();
        while (true) {
            System.out.println("Enter the number beside the file that you would like to retrieve (type 0 to exit): ");
            String input = sc.nextLine();
            int choice = 0;
            try {
                List<String> allLines = Files.readAllLines(Paths.get("applicationInfo"));
                if (!isNumber(input) || (choice = Integer.parseInt(input)) > (allLines.size() - 2) || choice < 0 || requestedFiles.contains(allLines.get(choice+1))) {
                    System.out.println("Incorrect input, please try again");
                }
                else{
                    if (choice == 0) break;
                    requestedFiles.add(allLines.get(choice+1));
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void setApplicationEnvironment() throws IOException, InterruptedException {
        try {
            File appInfoFile = new File("applicationInfo");
            appInfoFile.createNewFile();
        } catch (IOException e) {
            System.out.println("An error occurred.");
            e.printStackTrace();
        }
        System.out.println("Welcome to the Backup File Server Admin!");

        try{
            FileWriter myWriter = new FileWriter("applicationInfo");
            myWriter.write("Counter: \n");
            System.out.println("To start off, enter the IP address of the server that you would like to use: ");
            serverIP = sc.nextLine();
            myWriter.write("Server IP Address: " + serverIP+"\n");
            myWriter.close();
            addFileDirectories();
        }
        catch (IOException e) {
            System.out.println("An error occurred.");
            e.printStackTrace();
        }
        while(true) {
            System.out.println("How often would you like the backup to take place? Enter it in the following format: DD:HH:MM");
            String counterInput = sc.nextLine();
            String[] timeStampInputs = counterInput.split(":");
            if (timeStampInputs.length==3){
                if (isNumber(timeStampInputs[0]) && isNumber(timeStampInputs[1]) && isNumber(timeStampInputs[2])){
                    counter[0] = Integer.parseInt(timeStampInputs[0]);
                    counter[1] = Integer.parseInt(timeStampInputs[1]);
                    counter[2] = Integer.parseInt(timeStampInputs[2]);
                    // Converting days:hours:minutes
                    long days = counter[0] / 86400000;
                    long hours = counter[1] / 3600000;
                    long minutes = counter[2] / 1000;
                    time = days + hours + minutes;
                    String newCounterLine = "Counter: " + time;
                    List<String> allLines = Files.readAllLines(Paths.get("applicationInfo"));
                    allLines.set(0, newCounterLine);
                    Files.write(Paths.get("applicationInfo"), allLines, StandardCharsets.UTF_8);
                    break;
                }
                else{
                    System.out.println("Incorrect input, please try again");
                }
            }
            else{
                System.out.println("Incorrect input, please try again");
            }
        }
        client.start(serverIP);
        System.out.println("Backup service started");
        /*while(true){
            displayFiles();
        }*/
    }

    private boolean isNumber(String s)
    {
        for (int i = 0; i < s.length(); i++)
            if (isDigit(s.charAt(i)) == false)
                return false;
        return true;
    }

    public long getTime(){
        return time;
    }
    /*private void backUp(){
        try {
            List<String> allLines = Files.readAllLines(Paths.get("applicationInfo"));
            for (String line : allLines) {
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }*/

    private static void getAvailableDevices() throws IOException {
          /*Vector<String> Available_Devices=new Vector<>();
        String myip= InetAddress.getLocalHost().getHostAddress();
        String mynetworkips=new String();

        for(int i=myip.length();i>0;--i) {
            if(myip.charAt(i-1)=='.'){ mynetworkips=myip.substring(0,i); break; }
        }

        System.out.println("My Device IP: " + myip+"\n\n");

        for(int i=1;i<=254;++i){
            if ((i % 26) == 0) System.out.println("Done " + ((i / 26) * 10) + "% of the scanning");
            try {
                InetAddress addr=InetAddress.getByName(mynetworkips + new Integer(i).toString());
                if (addr.isReachable(1)){
                    System.out.println("Available: " + addr.getHostAddress());
                    Available_Devices.add(addr.getHostAddress());
                }
                else{
                    System.out.println("Not Available: " + addr.getHostAddress());
                }
            }catch (IOException ioex){}
        }
        System.out.println("\nAll Connected devices(" + Available_Devices.size() +"):");
        for(int i=0;i<Available_Devices.size();++i) System.out.println(Available_Devices.get(i));*/
    }

}

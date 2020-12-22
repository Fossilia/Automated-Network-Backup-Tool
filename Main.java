package com.segmentationfault;

public class Main {

    public static void main(String[]args) throws InterruptedException {
        Sleeper sleeper = new Sleeper(1000);
        sleeper.startSleeper();
        while (true){
            Thread.sleep(100);
            System.out.println("HELLO");
        }


    }

}

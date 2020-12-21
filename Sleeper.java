package com.segmentationfault;

public class Sleeper {

    private int time;

    public Sleeper(int time){
        this.time = time;

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
            System.out.println("BACKUP");
            Thread.sleep(time);

        }
    }
}

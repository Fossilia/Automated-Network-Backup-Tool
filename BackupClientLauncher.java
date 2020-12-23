

import java.io.IOException;

public class BackupClientLauncher {

    public static void main(String[] args) {
        Controller controller = new Controller();
        try {
            controller.startApplication();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}

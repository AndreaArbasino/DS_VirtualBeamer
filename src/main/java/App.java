import model.LocalController;
import view.GUI;

import java.net.UnknownHostException;
import java.util.Scanner;

public class App 
{
    public static void main( String[] args ) throws UnknownHostException {
        System.setProperty("java.net.preferIPv4Stack", "true");

        GUI gui = new GUI();
        gui.start();

        /*System.out.println("Insert username:");
        Scanner scanner = new Scanner(System.in);
        LocalController localController = new LocalController(scanner.nextLine());*/
    }

}

package org.example;

import model.LocalController;

import java.net.UnknownHostException;
import java.util.Scanner;

public class App 
{
    public static void main( String[] args ) throws UnknownHostException {
        System.setProperty("java.net.preferIPv4Stack", "true");

        //TODO: start GUI here

        System.out.println("Insert username:");
        Scanner scanner = new Scanner(System.in);
        LocalController localController = new LocalController(scanner.nextLine());
    }

}

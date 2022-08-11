package org.example;

import network.DiscoverSender;

import static Utilities.StaticUtilities.DEFAULT_DISCOVER_PORT;
import static Utilities.StaticUtilities.DEFAULT_IP;

/**
 * Hello world!
 *
 */
public class App 
{
    public static void main( String[] args ) {

        DiscoverSender discoverSender1 = new DiscoverSender(DEFAULT_IP, DEFAULT_DISCOVER_PORT);
        Thread thread1 = new Thread(discoverSender1);
        thread1.start();
    }




}

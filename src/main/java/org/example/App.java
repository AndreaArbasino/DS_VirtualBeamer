package org.example;
import network.DiscoverSender;

import java.net.UnknownHostException;

import static Utilities.StaticUtilities.DEFAULT_DISCOVER_PORT;
import static Utilities.StaticUtilities.DEFAULT_IP;

/**
 * Hello world!
 *
 */
public class App 
{
    public static void main( String[] args ) throws UnknownHostException {
        DiscoverSender discoverSender1 = new DiscoverSender (DEFAULT_IP, DEFAULT_DISCOVER_PORT);
        Thread thread1 = new Thread(discoverSender1);
        thread1.start();
    }

}

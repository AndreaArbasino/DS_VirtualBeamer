package org.example;

import network.NodeNetworkInterface;

import java.net.UnknownHostException;

/**
 * Hello world!
 */
public class App 
{
    public static void main( String[] args ) throws UnknownHostException {
        System.setProperty("java.net.preferIPv4Stack", "true");

        NodeNetworkInterface nodeNetworkInterface = new NodeNetworkInterface();
        nodeNetworkInterface.run();
    }

}

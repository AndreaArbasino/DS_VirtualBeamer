package org.example;

import network.NodeNetworkInterface;

import java.net.UnknownHostException;

/**
 * Hello world!
 */
public class App 
{
    public static void main( String[] args ) throws UnknownHostException {

        NodeNetworkInterface nodeNetworkInterface = new NodeNetworkInterface();
        nodeNetworkInterface.run();
    }

}

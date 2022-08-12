package org.example;

import ElementsOfNetwork.Member;
import network.NodeNetworkInterface;

import java.net.UnknownHostException;

/**
 * Hello world!
 */
public class App 
{
    public static void main( String[] args ) throws UnknownHostException {

        Member member = new Member("pippo", "pluto"); //ovviamente da cambiare
        NodeNetworkInterface nodeNetworkInterface = new NodeNetworkInterface(member);
        nodeNetworkInterface.run();
    }

}

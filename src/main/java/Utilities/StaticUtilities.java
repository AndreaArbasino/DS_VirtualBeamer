package Utilities;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;

public class StaticUtilities {

    //Multicast "available" addresses are 224.0.0.1 - 239.255.255.255
    public static final String DEFAULT_IP = "232.0.0.2";
    public static final String DISCOVER_CONTENT = "hello";
    public static final int DEFAULT_DISCOVER_PORT = 42000;
    public static final int DEFAULT_PRESENTATION_PORT = 42001;
    public static final int DEFAULT_ELECTION_PORT = 42002;
    public static final int DEFAULT_TCP_PORT = 6969;

    public static final int DEFAULT_DISCOVER_RECEIVED_BYTES = 1000;


    public static NetworkInterface getLocalNetworkInterface() {
        Enumeration<NetworkInterface> interfaces;
        try {
            interfaces = NetworkInterface.getNetworkInterfaces();
        } catch (SocketException e) {
            throw new RuntimeException("NetworkInterface not found", e);
        }
        while (interfaces.hasMoreElements()) {
            NetworkInterface networkInterface = interfaces.nextElement();
            Enumeration<InetAddress> addresses = networkInterface.getInetAddresses();
            while (addresses.hasMoreElements()) {
                InetAddress address = addresses.nextElement();
                if (address.isLoopbackAddress()) continue;
                if (address.getHostAddress().contains(":")) continue;
                return networkInterface;
            }
        }
        throw new RuntimeException("NetworkInterface not found");
    }




}

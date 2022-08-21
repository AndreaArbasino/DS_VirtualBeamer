package utilities;

public class StaticUtilities {

    //Multicast "available" addresses are 224.0.0.1 - 239.255.255.255
    public static final String DEFAULT_DISCOVER_IP = "230.0.0.0";
    public static final String DISCOVER_CONTENT = "hello";
    public static final int DEFAULT_DISCOVER_PORT = 42000;
    public static final int DEFAULT_INFO_MESSAGE_PORT = 42001; //SHOULD BE USELESS, it is sent in unicast as a response

    public static final int DEFAULT_PRESENTATION_PORT = 42002;
    public static final int DEFAULT_ELECTION_PORT = 42003;
    public static final int DEFAULT_TCP_PORT = 6969;

    public static final int DEFAULT_DISCOVER_RECEIVED_BYTES = 1000;

}

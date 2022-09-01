package utilities;

import java.awt.image.BufferedImage;

public class StaticUtilities {

    //Multicast "available" addresses are 224.0.0.1 - 239.255.255.255
    public static final String DEFAULT_DISCOVER_IP = "230.0.0.0";
    public static final int DEFAULT_MULTICAST_PORT = 42000;
    public static final int DEFAULT_UNICAST_PORT = 42001;

    public static final int DEFAULT_IMAGE_PORT = 42002;

    public static final int MIN_RANDOM_TIME = 0;
    public static final int MAX_RANDOM_TIME = 500;

    public static final int DATAGRAM_DATA_SIZE = 65507;

    public static final int DEFAULT_RECEIVED_BYTES = 10000;

    public static final int HEADER_SIZE = 8;
    public static final int MAX_PACKETS = 255;
    public static final int SESSION_START = 128;
    public static final int SESSION_END = 64;
    public static final int DATAGRAM_MAX_SIZE = DATAGRAM_DATA_SIZE - HEADER_SIZE;
    public static final int MAX_SESSION_NUMBER = 255;

    public static final String IMAGE_OUTPUT_FORMAT = "jpg";
    public static final int COLOUR_OUTPUT = BufferedImage.TYPE_INT_RGB;

    public static double SCALING = 0.5;


}

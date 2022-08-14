package messages;

import java.io.Serial;

import static utilities.StaticUtilities.DEFAULT_DISCOVER_PORT;
import static utilities.StaticUtilities.DISCOVER_CONTENT;

public class DiscoverMessage extends Message{

    @Serial
    private static final long serialVersionUID = -4342675411482342214L;

    private String string;
    private int port;

    public DiscoverMessage() {
        this.string = DISCOVER_CONTENT;
        this.port = DEFAULT_DISCOVER_PORT;
    }

    public String getString() {
        return string;
    }

    public int getPort() {
        return port;
    }
}

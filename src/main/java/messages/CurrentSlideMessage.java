package messages;

public class CurrentSlideMessage extends Message{

    private int slideNumber;

    public CurrentSlideMessage(int slideNumber) {
        this.slideNumber = slideNumber;
    }

    public int getSlideNumber() {
        return slideNumber;
    }
}

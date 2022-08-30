package messages;

public class TotalNumberOfSlidesMessage extends Message{

    private int totalNumberOfSlides;

    public TotalNumberOfSlidesMessage(int totalNumberOfSlides) {
        this.totalNumberOfSlides = totalNumberOfSlides;
    }

    public int getTotalNumberOfSlides() {
        return totalNumberOfSlides;
    }
}

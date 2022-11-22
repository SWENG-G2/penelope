package sweng.penelope.xml;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class XMLConfiguration {
    private String author;
    private String title;
    private Long itemId;
    private int numSlides = 0;

    public XMLConfiguration(String author, String title, Long itemId) {
        this.author = author;
        this.title = title;
        this.itemId = itemId;
    }

    public String getNumSlidesString() {
        return Integer.toString(numSlides);
    }

    public void incrementNumSlides() {
        numSlides++;
    }

    public void decrementNumSlides() {
        numSlides--;
    }
}

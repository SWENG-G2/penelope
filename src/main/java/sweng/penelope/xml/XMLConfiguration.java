package sweng.penelope.xml;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class XMLConfiguration {
    private String author;
    private String title;
    private Long itemId;

    public XMLConfiguration(String author, String title, Long itemId) {
        this.author = author;
        this.title = title;
        this.itemId = itemId;
    }
}

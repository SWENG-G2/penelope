package sweng.penelope.xml;

import java.util.Iterator;
import java.util.Objects;

import org.dom4j.Element;

public class CampusXML extends CommonXML {
    public CampusXML(XMLConfiguration xmlConfiguration) {
        super(xmlConfiguration);
    }

    private String formatDescription(String description) {
        // Display max 50 chars
        String formattedDescription = description.substring(0, Math.min(description.length(), 50));

        if (description.length() > 50)
            formattedDescription += "...";

        return formattedDescription;
    }

    public void addDuck(String name, String description, Long id, String imageURL) {
        Element duckSlide = presentation.addElement("slide").addAttribute(WIDTH, "2000")
                .addAttribute(HEIGHT, "1000")
                .addAttribute("title", Long.toString(id));

        // Title
        duckSlide.addElement("text").addAttribute(FONT_NAME, "def").addAttribute(FONT_SIZE, "22")
                .addAttribute(COLOUR, "#000000").addAttribute(X_COORDINATE, "700")
                .addAttribute(Y_COORDINATE, "100")
                .addText(name);

        // Description
        duckSlide.addElement("text").addAttribute(FONT_NAME, "def").addAttribute(FONT_SIZE, "12")
                .addAttribute(COLOUR, "#000000").addAttribute(X_COORDINATE, "700")
                .addAttribute(Y_COORDINATE, "400")
                .addText(formatDescription(description));

        // Image
        duckSlide.addElement("image").addAttribute("url", imageURL).addAttribute(WIDTH, "500")
                .addAttribute(HEIGHT, "500").addAttribute(X_COORDINATE, "100")
                .addAttribute(Y_COORDINATE, "100");

        incrementNumSlides();
    }

    public void removeDuck(Long id) throws SlideNotFoundException {
        Iterator<Element> ducksIterator = presentation.elementIterator("slide");
        String idString = Long.toString(id);
        while (ducksIterator.hasNext()) {
            Element duck = ducksIterator.next();

            if (Objects.equals(duck.attributeValue("title"), idString)) {
                presentation.remove(duck);
                decrementNumSlides();
                return;
            }
        }

        throw new SlideNotFoundException(idString);
    }
}

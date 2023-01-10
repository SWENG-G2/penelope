package sweng.penelope.xml;

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

    public void addBird(String name, String description, Long id, String imageURL) {
        Element duckSlide = presentation.addElement("slide").addAttribute(WIDTH, "1920")
                .addAttribute(HEIGHT, "200")
                .addAttribute("title", Long.toString(id));

        // Rectangle
        duckSlide.addElement("rectangle").addAttribute(WIDTH, "1720").addAttribute(HEIGHT, "160")
                .addAttribute(COLOUR, LIGHT_GRAY).addAttribute("borderWidth", "5")
                .addAttribute("borderColour", DARK_GRAY).addAttribute(X_COORDINATE, "100")
                .addAttribute(Y_COORDINATE, "20");

        // Title
        duckSlide.addElement("text").addAttribute(FONT_NAME, "def").addAttribute(FONT_SIZE, FONT_SIZE_TITLE_SM)
                .addAttribute(COLOUR, BLACK).addAttribute(X_COORDINATE, "610") // 480 (image) + 100 (rectangle) + 30
                .addAttribute(Y_COORDINATE, "30")
                .addText(name);

        // Description
        duckSlide.addElement("text").addAttribute(FONT_NAME, "def").addAttribute(FONT_SIZE, FONT_SIZE_BODY)
                .addAttribute(COLOUR, BLACK).addAttribute(X_COORDINATE, "610")
                .addAttribute(Y_COORDINATE, "68") // 28 (FONT_SIZE_TITLE_SM) + 2*20 (FONT_SIZE_BODY)
                .addText(formatDescription(description));

        // Image
        duckSlide.addElement("image").addAttribute("url", imageURL).addAttribute(WIDTH, "480") // 480 = 100 * (1920/200) * (100/200)
                .addAttribute(HEIGHT, "100").addAttribute(X_COORDINATE, "120")
                .addAttribute(Y_COORDINATE, "40");

        incrementNumSlides();
    }
}

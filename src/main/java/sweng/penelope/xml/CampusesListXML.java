package sweng.penelope.xml;

import org.dom4j.Element;

public class CampusesListXML extends CommonXML {
    public CampusesListXML(XMLConfiguration xmlConfiguration) {
        super(xmlConfiguration);
    }

    public void addCampus(String name, Long id) {
        Element campusSlide = presentation.addElement("slide").addAttribute(WIDTH, "1920")
                .addAttribute(HEIGHT, "400")
                .addAttribute("title", Long.toString(id));

        // Campus Name
        campusSlide.addElement("text").addAttribute(FONT_NAME, "def").addAttribute(FONT_SIZE, FONT_SIZE_TITLE)
                .addAttribute(COLOUR, BLACK).addAttribute(X_COORDINATE, "20")
                .addAttribute(Y_COORDINATE, "100")
                .addText(name);

        // Bottom border
        campusSlide.addElement("line").addAttribute("thickness", "10")
                .addAttribute(FROM_X, "200").addAttribute(FROM_Y, "400").addAttribute(TO_X, "1720")
                .addAttribute(TO_Y, "400")
                .addAttribute(COLOUR, DARK_GRAY);

        incrementNumSlides();
    }
}

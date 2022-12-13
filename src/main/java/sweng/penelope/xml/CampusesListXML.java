package sweng.penelope.xml;

import org.dom4j.Element;

public class CampusesListXML extends CommonXML {
    public CampusesListXML(XMLConfiguration xmlConfiguration) {
        super(xmlConfiguration);
    }

    public void addCampus(String name, Long id) {
        Element campusSlide = presentation.addElement("slide").addAttribute(WIDTH, "1920")
                .addAttribute(HEIGHT, "120")
                .addAttribute("title", Long.toString(id));

        // Campus Name
        campusSlide.addElement("text").addAttribute(FONT_NAME, "def").addAttribute(FONT_SIZE, FONT_SIZE_TITLE)
                .addAttribute(COLOUR, BLACK).addAttribute(X_COORDINATE, "100")
                .addAttribute(Y_COORDINATE, "45")
                .addText(name);

        // Bottom border
        campusSlide.addElement("line").addAttribute("thickness", "5")
                .addAttribute(FROM_X, "100").addAttribute(FROM_Y, "120").addAttribute(TO_X, "1820")
                .addAttribute(TO_Y, "120")
                .addAttribute(COLOUR, DARK_GRAY);

        incrementNumSlides();
    }
}

package sweng.penelope.xml;

import org.dom4j.Element;

public class CampusesListXML extends CommonXML {
    public CampusesListXML(XMLConfiguration xmlConfiguration) {
        super(xmlConfiguration);
    }

    public void addCampus(String name, Long id) {
        Element campusSlide = presentation.addElement("slide").addAttribute(WIDTH, "1920")
                .addAttribute(HEIGHT, "580")
                .addAttribute("title", Long.toString(id));

        // Campus Name
        campusSlide.addElement("text").addAttribute(FONT_NAME, "def").addAttribute(FONT_SIZE, "22")
                .addAttribute(COLOUR, "#000000").addAttribute(X_COORDINATE, "200")
                .addAttribute(Y_COORDINATE, "100")
                .addText(name);

        // Botto border
        campusSlide.addElement("circle").addAttribute("thickness", "10")
                .addAttribute(FROM_X, "200").addAttribute(FROM_Y, "580").addAttribute(TO_X, "1720")
                .addAttribute(TO_Y, "580")
                .addAttribute(COLOUR, DARK_GRAY);

        incrementNumSlides();
    }
}

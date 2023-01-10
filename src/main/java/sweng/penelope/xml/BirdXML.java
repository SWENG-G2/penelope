package sweng.penelope.xml;

import java.nio.file.Path;

import org.dom4j.Element;
import org.springframework.core.env.Environment;

public class BirdXML extends CommonXML {
    private static final String CONTENT_WIDTH = "1920";
    private static final String HERO_SLIDE_HEIGHT = "2200";
    private static final String HERO_IMAGE_BACKGROUND_DIMENSION = "1880";
    private static final String HERO_IMAGE_DIMENSION = "1870";
    private static final String FONT = "def";

    // Attributes
    private static final String URL = "url";

    private String birdName;

    public BirdXML(XMLConfiguration xmlConfiguration) {
        super(xmlConfiguration);

        birdName = xmlConfiguration.getTitle();
    }

    /**
     * Add a slide to the presentation.
     * 
     * @param width  Slide width
     * @param height Slide height
     * @param title  Slide title
     */
    public void addSlide(String width, String height, String title) {
        presentation.addElement("slide").addAttribute(WIDTH, width).addAttribute(HEIGHT, height)
                .addAttribute("title", title);

        incrementNumSlides();
    }

    public void addHeroSlide(String audioURL, String imageURL) {
        // Create slide
        Element heroSlide = presentation.addElement("slide").addAttribute(WIDTH, CONTENT_WIDTH)
                .addAttribute(HEIGHT, HERO_SLIDE_HEIGHT)
                .addAttribute("title", "heroSlide");
        // Add Title container
        heroSlide.addElement("rectangle").addAttribute(WIDTH, CONTENT_WIDTH).addAttribute(HEIGHT, "200")
                .addAttribute(X_COORDINATE, "0").addAttribute(Y_COORDINATE, "0")
                .addAttribute(COLOUR, "#E89266FF"); // Hero title container colour
        // Title text
        heroSlide.addElement("text").addAttribute(X_COORDINATE, "20").addAttribute(Y_COORDINATE, "20")
                .addAttribute(COLOUR, BLACK).addAttribute(FONT_NAME, FONT).addAttribute(FONT_SIZE, "22")
                .addText(birdName);
        // Title audio
        heroSlide.addElement("audio").addAttribute(URL, audioURL).addAttribute("loop", "false")
                .addAttribute(X_COORDINATE, "1880").addAttribute(Y_COORDINATE, "20");

        // Image background shape
        heroSlide.addElement("circle").addAttribute("radius", HERO_IMAGE_BACKGROUND_DIMENSION)
                .addAttribute(X_COORDINATE, "20").addAttribute(Y_COORDINATE, "220")
                .addAttribute(COLOUR, "#8A8178FF"); // Image background colour
        // Image
        heroSlide.addElement("image").addAttribute(URL, imageURL).addAttribute(WIDTH, HERO_IMAGE_DIMENSION)
                .addAttribute(HEIGHT, HERO_IMAGE_DIMENSION).addAttribute(X_COORDINATE, "25")
                .addAttribute(Y_COORDINATE, "225");
    }
}
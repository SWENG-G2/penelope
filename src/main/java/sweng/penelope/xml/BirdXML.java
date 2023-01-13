package sweng.penelope.xml;

import java.nio.file.Path;

import org.dom4j.Element;
import org.springframework.core.env.Environment;

public class BirdXML extends CommonXML {
        private static final String HERO_SLIDE_HEIGHT = "485";
        private static final String HERO_IMAGE_CIRCLE_RADIUS = "175";
        private static final String HERO_IMAGE_WIDTH = "1700";

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
                Element heroSlide = presentation.addElement("slide").addAttribute(WIDTH, SLIDE_WIDTH)
                                .addAttribute(HEIGHT, HERO_SLIDE_HEIGHT)
                                .addAttribute("title", "heroSlide");
                // Add Title container
                heroSlide.addElement("rectangle").addAttribute(WIDTH, SLIDE_WIDTH).addAttribute(HEIGHT, "100")
                                .addAttribute(X_COORDINATE, "0").addAttribute(Y_COORDINATE, "0")
                                .addAttribute(COLOUR, "#E89266FF"); // Hero title container colour
                // Title text
                heroSlide.addElement("text").addAttribute(X_COORDINATE, "20").addAttribute(Y_COORDINATE, "25")
                                .addAttribute(COLOUR, BLACK).addAttribute(FONT_NAME, FONT)
                                .addAttribute(FONT_SIZE, FONT_SIZE_TITLE_MD)
                                .addText(birdName);
                // Title audio
                heroSlide.addElement("audio").addAttribute(URL, audioURL).addAttribute("loop", "false")
                                .addAttribute(X_COORDINATE, END_OF_PARENT)
                                .addAttribute(Y_COORDINATE, "0");

                // Image
                heroSlide.addElement("image").addAttribute(URL, imageURL).addAttribute(WIDTH, HERO_IMAGE_WIDTH)
                                .addAttribute(HEIGHT, "360")
                                .addAttribute(X_COORDINATE, CENTER_IN_PARENT)
                                .addAttribute(Y_COORDINATE, "115");

                // Image background shape
                heroSlide.addElement("circle").addAttribute("radius", HERO_IMAGE_CIRCLE_RADIUS)
                                .addAttribute(X_COORDINATE, CENTER_IN_PARENT)
                                .addAttribute(Y_COORDINATE, PAD_CLIENT_SIDE + "120")
                                .addAttribute(COLOUR, TRANSPARENT)
                                .addAttribute(BORDER_WIDTH, "15")
                                .addAttribute(BORDER_COLOUR, DARK_GRAY);
        }

        public void addAboutMe(String aboutMeVideoURL, String aboutMe) {
                // Create slide
                Element aboutMeSlide = presentation.addElement("slide").addAttribute(WIDTH, SLIDE_WIDTH)
                                .addAttribute(HEIGHT, WRAP_CONTENT_CLIENT_SIDE)
                                .addAttribute("title", "About me");

                // Add description
                aboutMeSlide.addElement("text").addAttribute(X_COORDINATE, "20").addAttribute(Y_COORDINATE, "0")
                                .addAttribute(COLOUR, BLACK).addAttribute(FONT_NAME, FONT)
                                .addAttribute(FONT_SIZE, FONT_SIZE_BODY)
                                .addText(aboutMe);
        }

        public void addDiet(String dietImageURL, String diet) {
                // Create slide
                Element dietSlide = presentation.addElement("slide").addAttribute(WIDTH, SLIDE_WIDTH)
                                .addAttribute(HEIGHT, WRAP_CONTENT_CLIENT_SIDE)
                                .addAttribute("title", "Diet");
                // Add diet
                dietSlide.addElement("text").addAttribute(X_COORDINATE, "20").addAttribute(Y_COORDINATE, "0")
                                .addAttribute(COLOUR, BLACK).addAttribute(FONT_NAME, FONT)
                                .addAttribute(FONT_SIZE, FONT_SIZE_BODY)
                                .addText(diet);
        }
}
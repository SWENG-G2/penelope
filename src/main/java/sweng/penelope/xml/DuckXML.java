package sweng.penelope.xml;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;
import java.util.Objects;

import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;

import lombok.Getter;

public class DuckXML {
    @Getter
    private Document document;

    private Element presentation;
    private Element info;
    private int numSlides = 0;

    /**
     * DuckXML Constructor
     * 
     * @param title  Presentation title (duck's name)
     * @param author Author name
     */
    public DuckXML(String title, String author) {
        // Create document
        document = DocumentHelper.createDocument();
        presentation = document.addElement("presentation");
        info = presentation.addElement("info");

        // Title
        info.addElement("title").addText(title);
        // Author
        info.addElement("author").addText(author);
        // Date
        info.addElement("date").addText(new SimpleDateFormat("yyyy-MM-DD").format(new Date()));
        // numSlides
        info.addElement("numSlides").addText(Integer.toString(numSlides));
    }

    /**
     * Add a slide to the presentation.
     * 
     * @param width  Slide width
     * @param height Slide height
     * @param title  Slide title
     */
    public void addSlide(String width, String height, String title) {
        presentation.addElement("slide").addAttribute("width", width).addAttribute("height", height)
                .addAttribute("title", title);

        // Keep track of how many slides are in presentation
        numSlides++;
        info.element("numSlides").setText(Integer.toString(numSlides));
    }

    /**
     * 
     * @param slideTitle
     * @return
     * @throws SlideNotFoundException
     */
    private Element getSlide(String slideTitle) throws SlideNotFoundException {
        Iterator<Element> elementsInPresentation = presentation.elementIterator("slide");

        while (elementsInPresentation.hasNext()) {
            Element slide = elementsInPresentation.next();
            if (Objects.equals(slide.attributeValue("title"), slideTitle))
                return slide;
        }

        throw new SlideNotFoundException(slideTitle);
    }

    /**
     * Adds text to the desired slide.
     * 
     * @param slideTitle Title of the slide to add text to
     * @param fontSize   Text size
     * @param colour     Text colour (HEX RGBA, including #. e.g. #FF0A0BFF)
     * @param x          X coordinate of the text within the slide
     * @param y          Y coordinate of the text within the slide
     * @param text       Text content
     * @throws SlideNotFoundException If the desired slide is not found.
     */
    public void addText(String slideTitle, String fontSize, String colour, String x, String y, String text)
            throws SlideNotFoundException {
        Element slide = getSlide(slideTitle);

        slide.addElement("text").addAttribute("fontName", "def").addAttribute("fontSize", fontSize)
                .addAttribute("colour", colour).addAttribute("xCoordinate", x)
                .addAttribute("yCoordinate", y).addText(text);
    }

    /**
     * Adds an image to the desired slide.
     * 
     * @param slideTitle Title of the slide to add text to
     * @param url        Image blob url
     * @param width      Image width
     * @param height     Image height
     * @param x          X coordinate of the image within the slide
     * @param y          Y coordinate of the image within the slide
     * @throws SlideNotFoundException
     */
    public void addImage(String slideTitle, String url, String width, String height, String x, String y)
            throws SlideNotFoundException {
        Element slide = getSlide(slideTitle);

        slide.addElement("image").addAttribute("url", url).addAttribute("width", width)
                .addAttribute("heigth", height).addAttribute("xCoordinate", x)
                .addAttribute("yCoordinate", y);

    }

    /**
     * Adds audio to the desired slide.
     * 
     * @param slideTitle Title of the slide to add text to
     * @param url        Audio blob url
     * @param x          X coordinate of audio button within the slide
     * @param y          Y coordinate of audio button within the slide
     * @throws SlideNotFoundException
     */
    public void addAudio(String slideTitle, String url, String x, String y) throws SlideNotFoundException {
        Element slide = getSlide(slideTitle);

        slide.addElement("audio").addAttribute("url", url).addAttribute("xCoordinate", x)
                .addAttribute("yCoordinate", y);
    }

    /**
     * Returns presentation as string
     * 
     * @return The content of the presentation as a string;
     */
    public String dumpPresentation() {
        return document.asXML();
    }
}

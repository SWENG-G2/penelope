package sweng.penelope.xml;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;
import java.util.Objects;

import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.SAXReader;
import org.dom4j.io.XMLWriter;
import org.springframework.core.env.Environment;

public class CampusXML extends CommonXML {
    public CampusXML(Environment environment, XMLConfiguration xmlConfiguration) {
        super("campus", environment, xmlConfiguration);
    }

    public void addDuck(String name, String description, Long id, String imageURL) {
        Element duckSlide = presentation.addElement("slide").addAttribute("width", "2000")
                .addAttribute("height", "1000")
                .addAttribute("title", Long.toString(id));

        // Title
        duckSlide.addElement("text").addAttribute("fontName", "def").addAttribute("fontSize", "22")
                .addAttribute("colour", "#000000").addAttribute("xCoordinate", "700")
                .addAttribute("yCoordinate", "100")
                .addText(name);

        // Description
        duckSlide.addElement("text").addAttribute("fontName", "def").addAttribute("fontSize", "12")
                .addAttribute("colour", "#000000").addAttribute("xCoordinate", "700")
                .addAttribute("yCoordinate", "400")
                .addText(description.substring(0, Math.min(description.length(), 50)) + "..."); // Display max 50 chars

        // Image
        duckSlide.addElement("image").addAttribute("url", imageURL).addAttribute("width", "500")
                .addAttribute("heigth", "500").addAttribute("xCoordinate", "100")
                .addAttribute("yCoordinate", "100");

        numSlides++;

        info.element("numSlides").setText(Integer.toString(numSlides));
    }

    public void removeDuck(Long id) throws SlideNotFoundException {
        Iterator<Element> ducksIterator = presentation.elementIterator("slide");
        String idString = Long.toString(id);
        while (ducksIterator.hasNext()) {
            Element duck = ducksIterator.next();

            if (Objects.equals(duck.attributeValue("title"), idString)) {
                presentation.remove(duck);
                return;
            }
        }

        throw new SlideNotFoundException(idString);
    }

    public void write() throws IOException {
        // Write campus xml
        OutputFormat format = OutputFormat.createPrettyPrint();
        BufferedWriter bufferedWriter = Files.newBufferedWriter(filePath,
                StandardCharsets.UTF_8);
        XMLWriter xmlWriter = new XMLWriter(bufferedWriter, format);
        xmlWriter.write(document);
        xmlWriter.close();
    }
}

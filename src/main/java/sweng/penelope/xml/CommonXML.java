package sweng.penelope.xml;

import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.SAXReader;
import org.dom4j.io.XMLWriter;
import org.springframework.core.env.Environment;
import org.xml.sax.SAXException;

public class CommonXML {
    protected Document document;
    protected Element presentation;
    protected Element info;
    protected int numSlides = 0;

    // XML Attributes
    protected static final String Y_COORDINATE = "yCoordinate";
    protected static final String X_COORDINATE = "xCoordinate";
    protected static final String COLOUR = "colour";
    protected static final String WIDTH = "width";
    protected static final String HEIGHT = "height";
    protected static final String FONT_NAME = "fontName";
    protected static final String FONT_SIZE = "fontSize";

    private XMLConfiguration xmlConfiguration;

    protected CommonXML(XMLConfiguration xmlConfiguration) {
        this.xmlConfiguration = xmlConfiguration;

        createDocument();
    }

    protected String numSlidesString() {
        return Integer.toString(numSlides);
    }

    private void createDocument() {
        document = DocumentHelper.createDocument();
        presentation = document.addElement("presentation");
        info = presentation.addElement("info");

        // Title
        info.addElement("title").addText(xmlConfiguration.getTitle());
        // Author
        info.addElement("author").addText(xmlConfiguration.getAuthor());
        // Date
        info.addElement("date").addText(new SimpleDateFormat("yyyy-MM-DD").format(new Date()));
        // numSlides
        info.addElement("numSlides").addText(numSlidesString());
    }

    public byte[] getBytes() {
        OutputFormat format = OutputFormat.createPrettyPrint();
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        XMLWriter xmlWriter;
        try {
            xmlWriter = new XMLWriter(byteArrayOutputStream, format);
            xmlWriter.write(document);
            xmlWriter.close();

            return byteArrayOutputStream.toByteArray();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }

    protected void incrementNumSlides() {
        numSlides++;

        info.element("numSlides").setText(numSlidesString());
    }

    protected void decrementNumSlides() {
        numSlides--;

        info.element("numSlides").setText(numSlidesString());
    }
}

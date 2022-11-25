package sweng.penelope.xml;

import java.io.BufferedWriter;
import java.io.IOException;
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
    protected boolean folderExists = false;
    protected Path folderPath;
    protected Path filePath;
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

    protected CommonXML(String folderName, Environment environment, XMLConfiguration xmlConfiguration)
            throws XMLInitialisationException {
        // Check directory structure
        String baseFolder = environment.getProperty("storage.base-folder");
        Path basePath = Paths.get(baseFolder);
        Path folder = Paths.get(folderName);
        folderPath = basePath.resolve(folder);
        filePath = folderPath.resolve(String.format("%d.xml", xmlConfiguration.getItemId()));

        this.xmlConfiguration = xmlConfiguration;

        try {
            if (!Files.exists(folderPath))
                Files.createDirectories(folderPath);

            if (!Files.exists(filePath))
                createDocument();
            else
                loadDocument();

        } catch (Exception exception) {
            exception.printStackTrace();
            throw new XMLInitialisationException(exception.getCause().toString(), xmlConfiguration.getTitle());
        }
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

    private void loadDocument() throws SAXException, DocumentException {
        SAXReader saxReader = new SAXReader();
        saxReader.setFeature("http://apache.org/xml/features/disallow-doctype-decl", true);
        document = saxReader.read(filePath.toFile());

        presentation = document.getRootElement();
        info = presentation.element("info");
        numSlides = Integer.parseInt(info.elementText("numSlides"));
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

    protected void incrementNumSlides() {
        numSlides++;

        info.element("numSlides").setText(numSlidesString());
    }

    protected void decrementNumSlides() {
        numSlides--;

        info.element("numSlides").setText(numSlidesString());
    }
}

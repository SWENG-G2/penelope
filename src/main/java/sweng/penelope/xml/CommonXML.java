package sweng.penelope.xml;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.springframework.core.env.Environment;

import lombok.Getter;

public class CommonXML {
    @Getter
    protected boolean initialised = false;

    protected boolean folderExists = false;
    protected Path folderPath;
    protected Path filePath;
    protected Document document;
    protected Element presentation;
    protected Element info;
    protected int numSlides = 0;

    private XMLConfiguration xmlConfiguration;

    protected CommonXML(String folderName, Environment environment, XMLConfiguration xmlConfiguration) {
        // Check directory structure
        String baseFolder = environment.getProperty("storage.base-folder");
        Path basePath = Paths.get(baseFolder);
        Path folder = Paths.get(folderName);
        folderPath = basePath.resolve(folder);
        filePath = folderPath.resolve(String.format("%d.xml", xmlConfiguration.getItemId()));
        try {
            if (!Files.exists(folderPath))
                Files.createDirectories(folderPath);

            if (!Files.exists(filePath))
                createDocument();
            else
                loadDocument();

        } catch (IOException ioException) {
            ioException.printStackTrace();
            return;
        }

        this.xmlConfiguration = xmlConfiguration;
        initialised = true;
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
        info.addElement("numSlides").addText(xmlConfiguration.getNumSlidesString());
    }

    private void loadDocument() {
        try {
            SAXReader saxReader = new SAXReader();
            saxReader.setFeature("http://apache.org/xml/features/disallow-doctype-decl", true);
            document = saxReader.read(filePath.toFile());

            presentation = document.getRootElement();
            info = presentation.element("info");
            numSlides = Integer.parseInt(info.elementText("numSlides"));
        } catch (Exception e) {
            e.printStackTrace();
            initialised = false;
        }
    }
}

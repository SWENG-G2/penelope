package sweng.penelope.xml;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

import java.nio.file.Path;
import java.nio.file.Paths;

import org.junit.jupiter.api.Test;

import sweng.penelope.entities.Campus;

class BirdXMLTests {
    private static final String PRESENTATION_TITLE = "The test presentation.";
    private static final String PRESENTATION_AUTHOR = "Calypso";
    private static final String TEST_CAMPUS_NAME = "FortKnox";
    private static final Long ITEM_ID = 69L;

    private BirdXML slideDuckXML() {
        // Dummy configuration
        XMLConfiguration xmlConfiguration = new XMLConfiguration(PRESENTATION_AUTHOR, PRESENTATION_TITLE, ITEM_ID);

        // Dummy campus
        Campus campus = new Campus();
        campus.setName(TEST_CAMPUS_NAME);


        BirdXML birdXML = null;
        try {
            birdXML = new BirdXML(xmlConfiguration);
            birdXML.addHeroSlide("audioURL", "imageURL");
        } catch (Exception e) {
            e.printStackTrace();
        }

        return birdXML;
    }

    @Test
    void canCreateXML() {
        BirdXML birdXML = slideDuckXML();

        assertNotEquals(null, birdXML);
    }
}

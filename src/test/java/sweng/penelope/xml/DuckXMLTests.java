package sweng.penelope.xml;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

import java.nio.file.Path;
import java.nio.file.Paths;

import org.junit.jupiter.api.Test;

import sweng.penelope.entities.Campus;

class DuckXMLTests {
    private static final String PRESENTATION_TITLE = "The test presentation.";
    private static final String PRESENTATION_AUTHOR = "Calypso";
    private static final String TEST_CAMPUS_NAME = "FortKnox";
    private static final Long ITEM_ID = 69L;

    private DuckXML slideDuckXML() {
        // Dummy configuration
        XMLConfiguration xmlConfiguration = new XMLConfiguration(PRESENTATION_AUTHOR, PRESENTATION_TITLE, ITEM_ID);
        // Dummy environment
        TestEnvironment testEnvironment = new TestEnvironment();
        // assertNotEquals(null, null);
        // Dummy campus
        Campus campus = new Campus();
        campus.setName(TEST_CAMPUS_NAME);
        // Dummy path
        Path ducksPath = Paths.get("ducks");
        Path ducksCampusPath = Paths.get(campus.getName());
        Path destinationPath = ducksPath.resolve(ducksCampusPath);

        DuckXML duckXML = null;
        try {
            duckXML = new DuckXML(testEnvironment, xmlConfiguration, destinationPath);
            duckXML.addHeroSlide("audioURL", "imageURL");
        } catch (Exception e) {
            e.printStackTrace();
        }

        return duckXML;
    }

    @Test
    void canCreateXML() {
        DuckXML duckXML = slideDuckXML();

        assertNotEquals(null, duckXML);
    }

    @Test
    void canWriteXML() {
        DuckXML duckXML = slideDuckXML();

        assertDoesNotThrow(() -> {
            duckXML.write();
        });
    }
}

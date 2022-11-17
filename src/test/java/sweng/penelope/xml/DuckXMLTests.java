package sweng.penelope.xml;

import static org.junit.jupiter.api.Assertions.assertThrowsExactly;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static sweng.penelope.TestUtils.println;
import static sweng.penelope.TestUtils.testEnd;
import static sweng.penelope.TestUtils.testStart;

import org.junit.jupiter.api.Test;

class DuckXMLTests {
    private static final String PRESENTATION_TITLE = "The test presentation.";
    private static final String PRESENTATION_AUTHOR = "Calypso";
    private static final String SLIDE_TITLE = "The test slide";
    private static final String SLIDE_TITLE2 = "The second test slide";
    private static final String SLIDE_WIDTH = "1000";
    private static final String SLIDE_HEIGHT = "1000";
    private static final String DUMPING = "Dumping DuckXML";

    private DuckXML slideDuckXML() {
        DuckXML duckXML = new DuckXML(PRESENTATION_TITLE, PRESENTATION_AUTHOR);
        duckXML.addSlide(SLIDE_WIDTH, SLIDE_HEIGHT, SLIDE_TITLE);

        return duckXML;
    }

    @Test
    void canCreateXML() {
        testStart("canCreateXML");
        println("Creating DuckXML");
        DuckXML duckXML = new DuckXML(PRESENTATION_TITLE, PRESENTATION_AUTHOR);

        println(DUMPING);
        String duckXmlString = duckXML.dumpPresentation();

        assertTrue(duckXmlString.contains(PRESENTATION_TITLE));
        assertTrue(duckXmlString.contains(PRESENTATION_AUTHOR));
        testEnd("canCreateXML");
    }

    @Test
    void canAddSlide() {
        testStart("canAddSlide");
        println("Adding slide");
        DuckXML duckXML = slideDuckXML();

        println(DUMPING);
        String duckXmlString = duckXML.dumpPresentation();

        assertTrue(duckXmlString.contains(SLIDE_TITLE));
        testEnd("canAddSlide");
    }

    @Test
    void canAddMultipleSlides() {
        testStart("canAddMultipleSlides");
        println("Adding first slide");
        DuckXML duckXML = slideDuckXML();

        println("Adding second slide");
        duckXML.addSlide(SLIDE_WIDTH, SLIDE_HEIGHT, SLIDE_TITLE2);

        println(DUMPING);
        String duckXmlString = duckXML.dumpPresentation();

        assertTrue(duckXmlString.contains(SLIDE_TITLE));
        assertTrue(duckXmlString.contains(SLIDE_TITLE2));
        testEnd("canAddMultipleSlides");
    }

    @Test
    void canAddText() throws SlideNotFoundException {
        testStart("canAddText");
        DuckXML duckXML = slideDuckXML();

        println("Adding text");
        duckXML.addText(SLIDE_TITLE, "22", "#00000000", "0", "0", "Test text");

        println(DUMPING);
        String duckXmlString = duckXML.dumpPresentation();

        assertTrue(duckXmlString.contains("Test text"));
        testEnd("canAddText");
    }

    @Test
    void canAddImage() throws SlideNotFoundException {
        testStart("canAddImage");
        DuckXML duckXML = slideDuckXML();

        println("Adding image");
        duckXML.addImage(SLIDE_TITLE, "imageURL", "800", "800", "200", "200");

        println(DUMPING);
        String duckXmlString = duckXML.dumpPresentation();

        assertTrue(duckXmlString.contains("imageURL"));
        testEnd("canAddImage");
    }

    @Test
    void canAddAudio() throws SlideNotFoundException {
        testStart("canAddAudio");
        DuckXML duckXML = slideDuckXML();

        println("Adding audio");
        duckXML.addAudio(SLIDE_TITLE, "audioURL", "0", "0");

        println(DUMPING);
        String duckXMLString = duckXML.dumpPresentation();

        assertTrue(duckXMLString.contains("audioURL"));
        testEnd("canAddAudio");
    }

    @Test
    void cannotAddToMissingSlide() {
        testStart("cannotAddToMissingSlide");
        DuckXML duckXML = slideDuckXML();

        println("Adding text");
        assertThrowsExactly(SlideNotFoundException.class, () -> {
            duckXML.addText(SLIDE_TITLE2, "22", "#00000000", "0", "0", "Test");
        });
        println("Adding image");
        assertThrowsExactly(SlideNotFoundException.class, () -> {
            duckXML.addImage(SLIDE_TITLE2, "imageURL", "800", "800", "200", "200");
        });
        println("Adding audio");
        assertThrowsExactly(SlideNotFoundException.class, () -> {
            duckXML.addAudio(SLIDE_TITLE2, "audioURL", "950", "0");
        });

        testEnd("cannotAddToMissingSlide");
    }
}

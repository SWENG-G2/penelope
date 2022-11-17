package sweng.penelope.xml;

import java.util.Locale;

public class SlideNotFoundException extends Exception {

    /**
     * Exception thrown when a requested slide does not exist within a presentation.
     * 
     * @param slideTitle The title of the requested slide.
     */
    public SlideNotFoundException(String slideTitle) {
        super(String.format(Locale.getDefault(), "Slide \"%s\" not found%n", slideTitle));
    }
}

package sweng.penelope.xml;

import java.util.Locale;

public class XMLInitialisationException extends Exception {

    /**
     * Exception thrown when an xml document cannot be initialised
     * 
     * @param cause             What caused the exception.
     * @param presentationTitle The title of the presentation.
     */
    public XMLInitialisationException(String cause, String presentationTitle) {
        super(String.format(Locale.getDefault(), "Could not initialise XML for the \"%s\" presentation. Cause: %s%n",
                presentationTitle, cause));
    }
}

package sweng.penelope;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

public class Responses {
    /**
     * Shorthand for successful response.
     * 
     * @param message The message to display as response body
     * @return {@link ResponseEntity}
     */
    public ResponseEntity<String> ok(String message) {
        return new ResponseEntity<>(message, HttpStatus.OK);
    }

    /**
     * Shorthand for response to unauthorised request.
     * 
     * @return {@link ResponseEntity}
     */
    public ResponseEntity<String> unauthorised() {
        return new ResponseEntity<>("You shall not pass!", HttpStatus.UNAUTHORIZED);
    }

    /**
     * Shorthand for resource not found response.
     * 
     * @param message The message to display as response body
     * @return {@link ResponseEntity}
     */
    public ResponseEntity<String> notFound(String message) {
        return new ResponseEntity<>(message,
                HttpStatus.NOT_FOUND);
    }

    /**
     * Shorthand for internal server error response.
     * @param message The message to display as response body
     * @return {@link ResponseEntity}
     */
    public ResponseEntity<String> internalServerError(String message) {
        return new ResponseEntity<>(message, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}

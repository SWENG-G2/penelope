package sweng.penelope;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

public class Responses {
    /**
     * Shortend for successful response.
     * 
     * @param message The message to display as response body
     * @return ResponseEntity<String>
     */
    public ResponseEntity<String> ok(String message) {
        return new ResponseEntity<>(message, HttpStatus.OK);
    }

    /**
     * Shortend for response to unauthorised request.
     * 
     * @return ResponseEntity<String>
     */
    public ResponseEntity<String> unauthorised() {
        return new ResponseEntity<>("You shall not pass!", HttpStatus.UNAUTHORIZED);
    }

    /**
     * Shortend for resource not found response.
     * 
     * @param message The message to display as response body
     * @return ResponseEntity<String>
     */
    public ResponseEntity<String> notFound(String message) {
        return new ResponseEntity<>(message,
                HttpStatus.NOT_FOUND);
    }
}

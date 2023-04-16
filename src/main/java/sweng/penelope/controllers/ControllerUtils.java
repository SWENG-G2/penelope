package sweng.penelope.controllers;

import org.springframework.security.core.Authentication;

import springfox.documentation.service.ApiKey;

public class ControllerUtils {
    private ControllerUtils() {
        throw new IllegalStateException("ControllerUtils is a utility class.");
    }

    /**
     * Retrieves author's human friendly name from an {@link ApiKey}.
     * 
     * @param authentication   {@link Authentication} autowired.
     * @return {@link String} the author's email.
     */
    public static final String getAuthorName(Authentication authentication) {
        String email = authentication.getPrincipal().toString().split("=")[0];

        return email;
    }
}

package sweng.penelope.controllers;

import org.springframework.security.core.Authentication;

import sweng.penelope.entities.ApiKey;
import sweng.penelope.repositories.ApiKeyRepository;

public class ControllerUtils {
    private ControllerUtils() {
        throw new IllegalStateException("ControllerUtils is a utility class.");
    }

    /**
     * Retrieves author's human friendly name from an {@link ApiKey}.
     * 
     * @param authentication   {@link Authentication} autowired.
     * @param apiKeyRepository {@link ApiKeyRepository}.
     * @return {@link String} the author name.
     */
    public static final String getAuthorName(Authentication authentication, ApiKeyRepository apiKeyRepository) {
        String publicKey = authentication.getPrincipal().toString().split("_")[0];
        // The API Key should be there or auth would have blocked request
        ApiKey authorKey = apiKeyRepository.findById(publicKey).orElseThrow();

        return authorKey.getOwnerName();
    }
}

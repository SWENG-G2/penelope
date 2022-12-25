package sweng.penelope.auth;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import sweng.penelope.entities.ApiKey;
import sweng.penelope.entities.Campus;
import sweng.penelope.repositories.ApiKeyRepository;

@Service
public class ApiKeyAuthenticationManager implements AuthenticationManager {

    @Autowired
    private ApiKeyRepository apiKeyRepository;

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        String[] principal = authentication.getPrincipal().toString().split("_");

        if (principal.length > 1) {
            Optional<ApiKey> requestKey = apiKeyRepository.findById(principal[0]);

            if (requestKey.isPresent()) {
                ApiKey apiKey = requestKey.get();
                if (!apiKey.getAdmin()) {
                    for (Campus campus : apiKey.getCampuses()) {
                        if (campus.getId().toString().equals(principal[1])) {
                            authentication.setAuthenticated(true);
                            break;
                        }
                    }
                    throw new UnauthorisedException();
                } else
                    authentication.setAuthenticated(true);
            } else
                throw new UsernameNotFoundException("Could not find apikey");
        }
        return authentication;
    }

}

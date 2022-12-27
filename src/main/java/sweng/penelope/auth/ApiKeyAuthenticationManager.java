package sweng.penelope.auth;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.Optional;

import javax.xml.bind.DatatypeConverter;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AuthorizationServiceException;
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

    private Authentication verifyCredentials(Authentication authentication, ApiKey apiKey)
            throws AuthorizationServiceException {
        MessageDigest messageDigest;
        String principal = authentication.getPrincipal().toString();
        byte[] suppliedKey = DatatypeConverter.parseHexBinary(authentication.getCredentials().toString());
        try {
            messageDigest = MessageDigest.getInstance("SHA256");
            byte[] correctKey = messageDigest.digest((principal + apiKey.getSecret()).getBytes());
            if (Arrays.equals(correctKey, suppliedKey))
                authentication.setAuthenticated(true);
            else
                throw new BadCredentialsException("Hash mismatch");
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            throw new AuthorizationServiceException("Hashing algorithm error");
        }
        return authentication;
    }

    private Authentication handleNonAdminKey(Authentication authentication, ApiKey apiKey, String campusId) {
        if (authentication.getPrincipal().toString().contains("_admin"))
            throw new UnauthorisedException();
        for (Campus campus : apiKey.getCampuses()) {
            if (campus.getId().toString().equals(campusId)) {
                return verifyCredentials(authentication, apiKey);
            }
        }
        throw new UnauthorisedException();
    }

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        if (authentication.getCredentials() == null || authentication.getPrincipal() == null)
            throw new BadCredentialsException("Authentication headers are missing");

        String[] principal = authentication.getPrincipal().toString().split("_");

        if (principal.length > 1) {

            Optional<ApiKey> requestKey = apiKeyRepository.findById(principal[0]);

            if (requestKey.isPresent()) {
                ApiKey apiKey = requestKey.get();
                if (Boolean.FALSE.equals(apiKey.getAdmin())) {
                    return handleNonAdminKey(authentication, apiKey, principal[1]);
                } else {
                    return verifyCredentials(authentication, apiKey);
                }
            } else
                throw new UsernameNotFoundException("Could not find apikey");
        }
        return authentication;
    }

}

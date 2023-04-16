package sweng.penelope.auth;

import java.time.Duration;
import java.time.ZoneId;
import java.time.ZonedDateTime;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AuthorizationServiceException;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import sweng.penelope.repositories.CampusRepository;
import sweng.penelope.repositories.DataManagerRepository;

@Service
public class UserAuthenticationManager implements AuthenticationManager {

    @Autowired
    private DataManagerRepository dataManagerRepository;

    @Autowired
    private CampusRepository campusRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        try {
            String[] credentials = authentication.getPrincipal().toString().split("=");
            String username = credentials[0];
            String password = credentials[1];
            String timestamp = credentials[2];

            String claim = authentication.getCredentials().toString();

            // Compare timestamp
            ZonedDateTime now = ZonedDateTime.now(ZoneId.of("Europe/London"));
            ZonedDateTime sentAt = ZonedDateTime.parse(timestamp);
            Duration delta = Duration.between(now, sentAt);
            if (delta.getSeconds() > 60)
                throw new UnauthorisedException();

            // Verify username, password and claim
            dataManagerRepository.findById(username).ifPresentOrElse(dataManager -> {
                if (!passwordEncoder.matches(password, dataManager.getPassword()))
                    throw new UnauthorisedException();

                if (!claim.equals("admin")) {
                    if (!dataManager.isSysadmin()) {
                        campusRepository.findById(Long.parseLong(claim)).ifPresentOrElse(campus -> {
                            if (!dataManager.getCampuses().contains(campus)) {
                                throw new UnauthorisedException();
                            }
                        }, () -> {
                            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
                        });
                    }
                } else if (!dataManager.isSysadmin())
                    throw new UnauthorisedException();
            }, () -> {
                throw new UnauthorisedException();
            });
        } catch (Exception e) {
            e.printStackTrace();
            throw new AuthorizationServiceException("Could not decrypt credentials");
        }

        authentication.setAuthenticated(true);
        return authentication;
    }

}

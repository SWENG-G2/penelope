package sweng.penelope.auth;

import org.springframework.security.core.AuthenticationException;

public class UnauthorisedException extends AuthenticationException {

    public UnauthorisedException() {
        super("User does not have access to this resource");
    }

}

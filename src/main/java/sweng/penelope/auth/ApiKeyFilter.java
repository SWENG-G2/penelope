package sweng.penelope.auth;

import javax.servlet.http.HttpServletRequest;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.web.authentication.preauth.AbstractPreAuthenticatedProcessingFilter;

public class ApiKeyFilter extends AbstractPreAuthenticatedProcessingFilter {

    private final String principal;
    private final String credentials;

    public ApiKeyFilter(AuthenticationManager authenticationManager, String principal, String credentials) {
        super.setAuthenticationManager(authenticationManager);

        this.principal = principal;
        this.credentials = credentials;
    }

    @Override
    protected Object getPreAuthenticatedPrincipal(HttpServletRequest request) {
        return request.getHeader(principal);
    }

    @Override
    protected Object getPreAuthenticatedCredentials(HttpServletRequest request) {
        return request.getHeader(credentials);
    }
}

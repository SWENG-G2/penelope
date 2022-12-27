package sweng.penelope.auth;

import javax.servlet.http.HttpServletRequest;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.web.authentication.preauth.AbstractPreAuthenticatedProcessingFilter;

public class ApiKeyFilter extends AbstractPreAuthenticatedProcessingFilter {

    private final String principalHeader;
    private final String credentialsHeader;

    public ApiKeyFilter(AuthenticationManager authenticationManager, String principalHeader, String credentialsHeader) {
        super.setAuthenticationManager(authenticationManager);

        this.principalHeader = principalHeader;
        this.credentialsHeader = credentialsHeader;
    }

    @Override
    protected Object getPreAuthenticatedPrincipal(HttpServletRequest request) {
        String requestURI = request.getRequestURI();

        String principal = request.getHeader(principalHeader);
        if (requestURI.contains("birds")) {
            // Always /api/{campusId}/birds/*
            // So by splitting campusId is always at index 3 (index 0 is empty)
            String campusId = requestURI.split("/")[3];

            principal += "_" + campusId;
        } else if (requestURI.contains("campus")) {
            principal += "_campus";
        }
        return principal;
    }

    @Override
    protected Object getPreAuthenticatedCredentials(HttpServletRequest request) {
        return request.getHeader(credentialsHeader);
    }
}

package com.projet.jee.filter;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.security.SecureRandom;
import java.util.Base64;

/**
 * CSRF Protection Filter.
 * Generates and validates CSRF tokens for state-changing requests (POST, PUT, DELETE).
 */
public class CSRFFilter implements Filter {
    private static final String CSRF_TOKEN_ATTRIBUTE = "csrfToken";
    private static final String CSRF_TOKEN_PARAMETER = "csrfToken";
    private SecureRandom secureRandom;

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        secureRandom = new SecureRandom();
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;
        HttpSession session = httpRequest.getSession(true);

        // Generate CSRF token if doesn't exist
        if (session.getAttribute(CSRF_TOKEN_ATTRIBUTE) == null) {
            String csrfToken = generateCSRFToken();
            session.setAttribute(CSRF_TOKEN_ATTRIBUTE, csrfToken);
        }

        String method = httpRequest.getMethod();

        // Validate CSRF token for state-changing requests (POST, PUT, DELETE)
        if ("POST".equalsIgnoreCase(method) || "PUT".equalsIgnoreCase(method) ||
                "DELETE".equalsIgnoreCase(method)) {

            String sessionToken = (String) session.getAttribute(CSRF_TOKEN_ATTRIBUTE);
            String requestToken = httpRequest.getParameter(CSRF_TOKEN_PARAMETER);

            // Skip CSRF validation for AJAX JSON requests (use custom header instead)
            String contentType = httpRequest.getContentType();
            if (contentType != null && contentType.contains("application/json")) {
                requestToken = httpRequest.getHeader("X-CSRF-Token");
            }

            if (sessionToken == null || !sessionToken.equals(requestToken)) {
                httpResponse.sendError(HttpServletResponse.SC_FORBIDDEN,
                        "Invalid CSRF token");
                return;
            }
        }

        chain.doFilter(request, response);
    }

    private String generateCSRFToken() {
        byte[] bytes = new byte[24];
        secureRandom.nextBytes(bytes);
        return Base64.getEncoder().encodeToString(bytes);
    }

    @Override
    public void destroy() {
    }
}

package com.projet.jee.filter;

import com.projet.jee.service.AuthenticationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

/**
 * Filter to protect secured pages - requires authentication.
 * Redirects unauthenticated users to login page.
 */
public class AuthenticationFilter implements Filter {
    private static final Logger logger = LoggerFactory.getLogger(AuthenticationFilter.class);
    private AuthenticationService authService;

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        authService = new AuthenticationService();
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;
        HttpSession session = httpRequest.getSession(false);

        String requestURI = httpRequest.getRequestURI();
        String contextPath = httpRequest.getContextPath();

        // Check if user is authenticated
        boolean isAuthenticated = authService.isAuthenticated(session);

        if (!isAuthenticated) {
            logger.warn("Accès non autorisé à: {}", requestURI);

            // Save the requested URL to redirect after login
            httpRequest.getSession(true).setAttribute("redirectAfterLogin", requestURI);

            // Redirect to login page
            httpResponse.sendRedirect(contextPath + "/login");
            return;
        }

        // User is authenticated, continue
        chain.doFilter(request, response);
    }

    @Override
    public void destroy() {
    }
}

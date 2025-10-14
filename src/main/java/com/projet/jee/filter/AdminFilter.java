package com.projet.jee.filter;

import com.projet.jee.service.AuthenticationService;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

/**
 * Filter to protect admin-only routes.
 * Ensures only administrators can access admin pages.
 */
public class AdminFilter implements Filter {
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

        // Check if user is authenticated and is an admin
        if (!authService.isAuthenticated(session)) {
            // Not authenticated, redirect to login
            httpResponse.sendRedirect(httpRequest.getContextPath() + "/login");
            return;
        }

        if (!authService.isAdmin(session)) {
            // Authenticated but not admin, send 403 Forbidden
            httpResponse.sendError(HttpServletResponse.SC_FORBIDDEN,
                    "Accès refusé. Cette page est réservée aux administrateurs.");
            return;
        }

        // User is admin, allow access
        chain.doFilter(request, response);
    }

    @Override
    public void destroy() {
    }
}

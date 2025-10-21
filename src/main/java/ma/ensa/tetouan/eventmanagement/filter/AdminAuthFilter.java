package ma.ensa.tetouan.eventmanagement.filter;

import ma.ensa.tetouan.eventmanagement.model.Administrateur;
import ma.ensa.tetouan.eventmanagement.model.User;
import ma.ensa.tetouan.eventmanagement.util.ServletUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

/**
 * Filter to protect admin routes - only allow ADMINISTRATEUR role
 */
@WebFilter(filterName = "AdminAuthFilter", urlPatterns = "/admin/*")
public class AdminAuthFilter implements Filter {

    private static final Logger logger = LoggerFactory.getLogger(AdminAuthFilter.class);

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        logger.info("AdminAuthFilter initialized");
    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain chain)
            throws IOException, ServletException {

        HttpServletRequest request = (HttpServletRequest) servletRequest;
        HttpServletResponse response = (HttpServletResponse) servletResponse;

        HttpSession session = request.getSession(false);
        User currentUser = null;

        if (session != null) {
            currentUser = (User) session.getAttribute(ServletUtil.ATTR_CURRENT_USER);
        }

        // Check if user is logged in and is administrator
        if (currentUser == null) {
            logger.warn("Unauthorized access attempt to admin area - not logged in");
            ServletUtil.setErrorMessage(request.getSession(true),
                "Vous devez être connecté en tant qu'administrateur pour accéder à cette page");
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }

        if (!(currentUser instanceof Administrateur)) {
            logger.warn("Unauthorized access attempt to admin area by user: {} ({})",
                    currentUser.getNom(), currentUser.getRole());
            ServletUtil.setErrorMessage(session,
                "Accès refusé. Cette zone est réservée aux administrateurs.");
            response.sendRedirect(request.getContextPath() + "/");
            return;
        }

        // User is admin, allow access
        logger.debug("Admin access granted to: {}", currentUser.getNom());
        chain.doFilter(request, response);
    }

    @Override
    public void destroy() {
        logger.info("AdminAuthFilter destroyed");
    }
}

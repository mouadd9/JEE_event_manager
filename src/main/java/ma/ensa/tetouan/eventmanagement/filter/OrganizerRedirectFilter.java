package ma.ensa.tetouan.eventmanagement.filter;

import ma.ensa.tetouan.eventmanagement.model.Organisateur;
import ma.ensa.tetouan.eventmanagement.model.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

/**
 * Filtre pour rediriger les organisateurs connectés vers leur dashboard
 * lorsqu'ils essaient d'accéder aux pages publiques (home, browse, etc.)
 * 
 * Les organisateurs doivent rester dans leur espace dédié et ne pas 
 * interagir avec les pages publiques destinées aux participants.
 *
 * @author ENSA Tétouan
 */
@WebFilter(filterName = "OrganizerRedirectFilter", urlPatterns = "/*")
public class OrganizerRedirectFilter implements Filter {

    private static final Logger logger = LoggerFactory.getLogger(OrganizerRedirectFilter.class);

    // Pages publiques dont les organisateurs doivent être redirigés
    private static final List<String> PUBLIC_PAGES_TO_REDIRECT = Arrays.asList(
        "/",
        "/home",
        "/index.jsp",
        "/events/browse",
        "/events/details"
    );

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        logger.info("OrganizerRedirectFilter initialisé");
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;

        String requestURI = httpRequest.getRequestURI();
        String contextPath = httpRequest.getContextPath();
        String path = requestURI.substring(contextPath.length());

        // Vérifier si l'utilisateur est connecté
        HttpSession session = httpRequest.getSession(false);
        if (session != null && session.getAttribute("currentUser") != null) {
            User currentUser = (User) session.getAttribute("currentUser");
            
            // Vérifier si c'est un organisateur
            boolean isOrganizer = currentUser instanceof Organisateur;
            
            if (isOrganizer) {
                // Vérifier si l'organisateur essaie d'accéder à une page publique
                if (shouldRedirectOrganizer(path)) {
                    logger.debug("Organisateur '{}' redirigé de '{}' vers le dashboard", 
                                currentUser.getNom(), path);
                    httpResponse.sendRedirect(contextPath + "/organizer/dashboard");
                    return;
                }
            }
        }

        // Continuer la chaîne de filtres
        chain.doFilter(request, response);
    }

    @Override
    public void destroy() {
        logger.info("OrganizerRedirectFilter détruit");
    }

    /**
     * Vérifie si un organisateur doit être redirigé depuis cette page.
     *
     * @param path Le chemin de la page
     * @return true si l'organisateur doit être redirigé
     */
    private boolean shouldRedirectOrganizer(String path) {
        // Rediriger depuis les pages publiques exactes
        for (String publicPage : PUBLIC_PAGES_TO_REDIRECT) {
            if (path.equals(publicPage)) {
                return true;
            }
        }
        
        // Ne pas rediriger depuis les pages organisateur, login, logout, assets, etc.
        if (path.startsWith("/organizer/") || 
            path.startsWith("/login") || 
            path.startsWith("/logout") ||
            path.startsWith("/register") ||
            path.startsWith("/assets/") ||
            path.startsWith("/css/") ||
            path.startsWith("/js/") ||
            path.startsWith("/images/") ||
            path.startsWith("/uploads/") ||
            path.startsWith("/webjars/")) {
            return false;
        }

        return false;
    }
}

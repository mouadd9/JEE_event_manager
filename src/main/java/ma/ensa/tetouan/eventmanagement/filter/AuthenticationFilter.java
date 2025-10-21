package ma.ensa.tetouan.eventmanagement.filter;

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
 * Filtre d'authentification pour protéger les ressources nécessitant une connexion.
 * Redirige vers la page de login si l'utilisateur n'est pas authentifié.
 *
 * @author ENSA Tétouan
 */
@WebFilter(filterName = "AuthenticationFilter", urlPatterns = "/*")
public class AuthenticationFilter implements Filter {

    private static final Logger logger = LoggerFactory.getLogger(AuthenticationFilter.class);

    // URLs publiques accessibles sans authentification
    private static final List<String> PUBLIC_URLS = Arrays.asList(
        "/login",
        "/register",
        "/logout",
        "/index.jsp",
        "/",
        "/home",
        "/events/browse",
        "/events/details"
    );

    // Préfixes de ressources publiques
    private static final List<String> PUBLIC_RESOURCES = Arrays.asList(
        "/assets/",
        "/css/",
        "/js/",
        "/images/",
        "/uploads/",
        "/webjars/"
    );

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        logger.info("AuthenticationFilter initialisé");
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;

        String requestURI = httpRequest.getRequestURI();
        String contextPath = httpRequest.getContextPath();
        String path = requestURI.substring(contextPath.length());

        logger.debug("AuthenticationFilter: vérification de l'accès à {}", path);

        // Vérifier si la ressource est publique
        if (isPublicResource(path)) {
            logger.debug("Accès public autorisé pour: {}", path);
            chain.doFilter(request, response);
            return;
        }

        // Vérifier l'authentification
        HttpSession session = httpRequest.getSession(false);
        boolean isLoggedIn = (session != null && session.getAttribute("currentUser") != null);

        if (isLoggedIn) {
            logger.debug("Utilisateur authentifié, accès autorisé à: {}", path);
            chain.doFilter(request, response);
        } else {
            logger.warn("Accès non autorisé à {} - redirection vers /login", path);

            // Sauvegarder l'URL demandée pour redirection après login
            session = httpRequest.getSession(true);
            session.setAttribute("redirectAfterLogin", requestURI);

            // Rediriger vers la page de login
            httpResponse.sendRedirect(contextPath + "/login");
        }
    }

    @Override
    public void destroy() {
        logger.info("AuthenticationFilter détruit");
    }

    /**
     * Vérifie si la ressource est accessible publiquement.
     *
     * @param path Le chemin de la ressource
     * @return true si la ressource est publique
     */
    private boolean isPublicResource(String path) {
        // Vérifier les URLs publiques exactes
        if (PUBLIC_URLS.contains(path)) {
            return true;
        }

        // Vérifier les préfixes de ressources publiques
        for (String prefix : PUBLIC_RESOURCES) {
            if (path.startsWith(prefix)) {
                return true;
            }
        }

        return false;
    }
}

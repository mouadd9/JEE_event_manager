package ma.ensa.tetouan.eventmanagement.filter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

/**
 * Filtre pour définir l'encodage UTF-8 pour toutes les requêtes et réponses.
 * Doit s'exécuter avant tous les autres filtres.
 *
 * @author ENSA Tétouan
 */
@WebFilter(filterName = "CharacterEncodingFilter", urlPatterns = "/*")
public class CharacterEncodingFilter implements Filter {

    private static final Logger logger = LoggerFactory.getLogger(CharacterEncodingFilter.class);
    private static final String ENCODING = "UTF-8";

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        logger.info("CharacterEncodingFilter initialisé avec l'encodage: {}", ENCODING);
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        HttpServletRequest httpRequest = (HttpServletRequest) request;

        // Définir l'encodage de la requête
        if (request.getCharacterEncoding() == null) {
            request.setCharacterEncoding(ENCODING);
        }

        // Définir l'encodage de la réponse
        response.setCharacterEncoding(ENCODING);

        logger.debug("Encodage {} appliqué pour la requête: {}", ENCODING, httpRequest.getRequestURI());

        // Continuer la chaîne de filtres
        chain.doFilter(request, response);
    }

    @Override
    public void destroy() {
        logger.info("CharacterEncodingFilter détruit");
    }
}

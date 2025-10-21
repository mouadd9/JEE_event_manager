package ma.ensa.tetouan.eventmanagement.util;

import ma.ensa.tetouan.eventmanagement.model.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

/**
 * Classe utilitaire pour les servlets.
 * Fournit des méthodes communes pour la gestion des sessions, redirections, et messages.
 *
 * @author ENSA Tétouan
 */
public class ServletUtil {

    private static final Logger logger = LoggerFactory.getLogger(ServletUtil.class);

    // Constantes pour les attributs de session
    public static final String ATTR_CURRENT_USER = "currentUser";
    public static final String ATTR_SUCCESS_MESSAGE = "successMessage";
    public static final String ATTR_ERROR_MESSAGE = "errorMessage";
    public static final String ATTR_WARNING_MESSAGE = "warningMessage";
    public static final String ATTR_INFO_MESSAGE = "infoMessage";

    /**
     * Récupère l'utilisateur connecté depuis la session.
     *
     * @param request La requête HTTP
     * @return L'utilisateur connecté, ou null si non connecté
     */
    public static User getLoggedUser(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session != null) {
            return (User) session.getAttribute(ATTR_CURRENT_USER);
        }
        return null;
    }

    /**
     * Vérifie si un utilisateur est connecté.
     *
     * @param request La requête HTTP
     * @return true si un utilisateur est connecté, false sinon
     */
    public static boolean isLoggedIn(HttpServletRequest request) {
        return getLoggedUser(request) != null;
    }

    /**
     * Vérifie l'authentification et redirige vers login si nécessaire.
     *
     * @param request  La requête HTTP
     * @param response La réponse HTTP
     * @return true si l'utilisateur est connecté, false sinon
     * @throws IOException Si erreur de redirection
     */
    public static boolean requireLogin(HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        if (!isLoggedIn(request)) {
            logger.warn("Tentative d'accès non autorisé - redirection vers /login");
            setErrorMessage(request.getSession(true), "Vous devez être connecté pour accéder à cette page");
            redirect(response, request.getContextPath() + "/login");
            return false;
        }
        return true;
    }

    /**
     * Vérifie si l'utilisateur a le rôle requis.
     *
     * @param session      La session HTTP
     * @param requiredRole Le rôle requis
     * @return true si l'utilisateur a le rôle, false sinon
     */
    public static boolean requireRole(HttpSession session, String requiredRole) {
        User user = (User) session.getAttribute(ATTR_CURRENT_USER);
        if (user == null) {
            return false;
        }
        return user.getRole().equalsIgnoreCase(requiredRole);
    }

    /**
     * Vérifie si l'utilisateur a le rôle requis et redirige sinon.
     *
     * @param request      La requête HTTP
     * @param response     La réponse HTTP
     * @param requiredRole Le rôle requis
     * @return true si l'utilisateur a le rôle, false sinon
     * @throws IOException Si erreur de redirection
     */
    public static boolean requireRole(HttpServletRequest request, HttpServletResponse response,
                                      String requiredRole) throws IOException {
        HttpSession session = request.getSession(false);
        if (session == null || !requireRole(session, requiredRole)) {
            logger.warn("Accès refusé - rôle requis: {}", requiredRole);
            setErrorMessage(request.getSession(true),
                           "Vous n'avez pas les permissions nécessaires pour accéder à cette page");
            redirect(response, request.getContextPath() + "/");
            return false;
        }
        return true;
    }

    /**
     * Définit un message de succès dans la session.
     *
     * @param session La session HTTP
     * @param message Le message de succès
     */
    public static void setSuccessMessage(HttpSession session, String message) {
        if (session != null) {
            session.setAttribute(ATTR_SUCCESS_MESSAGE, message);
            logger.debug("Message de succès défini: {}", message);
        }
    }

    /**
     * Définit un message d'erreur dans la session.
     *
     * @param session La session HTTP
     * @param message Le message d'erreur
     */
    public static void setErrorMessage(HttpSession session, String message) {
        if (session != null) {
            session.setAttribute(ATTR_ERROR_MESSAGE, message);
            logger.debug("Message d'erreur défini: {}", message);
        }
    }

    /**
     * Définit un message d'avertissement dans la session.
     *
     * @param session La session HTTP
     * @param message Le message d'avertissement
     */
    public static void setWarningMessage(HttpSession session, String message) {
        if (session != null) {
            session.setAttribute(ATTR_WARNING_MESSAGE, message);
            logger.debug("Message d'avertissement défini: {}", message);
        }
    }

    /**
     * Définit un message d'information dans la session.
     *
     * @param session La session HTTP
     * @param message Le message d'information
     */
    public static void setInfoMessage(HttpSession session, String message) {
        if (session != null) {
            session.setAttribute(ATTR_INFO_MESSAGE, message);
            logger.debug("Message d'information défini: {}", message);
        }
    }

    /**
     * Récupère et supprime un message de succès de la session.
     *
     * @param session La session HTTP
     * @return Le message de succès, ou null
     */
    public static String getAndClearSuccessMessage(HttpSession session) {
        if (session != null) {
            String message = (String) session.getAttribute(ATTR_SUCCESS_MESSAGE);
            session.removeAttribute(ATTR_SUCCESS_MESSAGE);
            return message;
        }
        return null;
    }

    /**
     * Récupère et supprime un message d'erreur de la session.
     *
     * @param session La session HTTP
     * @return Le message d'erreur, ou null
     */
    public static String getAndClearErrorMessage(HttpSession session) {
        if (session != null) {
            String message = (String) session.getAttribute(ATTR_ERROR_MESSAGE);
            session.removeAttribute(ATTR_ERROR_MESSAGE);
            return message;
        }
        return null;
    }

    /**
     * Redirige vers un chemin spécifié.
     *
     * @param response La réponse HTTP
     * @param path     Le chemin de redirection
     * @throws IOException Si erreur de redirection
     */
    public static void redirect(HttpServletResponse response, String path) throws IOException {
        logger.debug("Redirection vers: {}", path);
        response.sendRedirect(path);
    }

    /**
     * Forward vers une JSP.
     *
     * @param request  La requête HTTP
     * @param response La réponse HTTP
     * @param jspPath  Le chemin de la JSP
     * @throws ServletException Si erreur de forward
     * @throws IOException      Si erreur de forward
     */
    public static void forward(HttpServletRequest request, HttpServletResponse response, String jspPath)
            throws ServletException, IOException {
        logger.debug("Forward vers: {}", jspPath);
        request.getRequestDispatcher(jspPath).forward(request, response);
    }

    /**
     * Récupère un paramètre de requête en tant qu'entier.
     *
     * @param request      La requête HTTP
     * @param paramName    Le nom du paramètre
     * @param defaultValue La valeur par défaut
     * @return La valeur du paramètre ou la valeur par défaut
     */
    public static int getIntParameter(HttpServletRequest request, String paramName, int defaultValue) {
        String value = request.getParameter(paramName);
        if (value != null && !value.trim().isEmpty()) {
            try {
                return Integer.parseInt(value);
            } catch (NumberFormatException e) {
                logger.warn("Paramètre {} invalide: {}", paramName, value);
            }
        }
        return defaultValue;
    }

    /**
     * Récupère un paramètre de requête en tant que long.
     *
     * @param request      La requête HTTP
     * @param paramName    Le nom du paramètre
     * @param defaultValue La valeur par défaut
     * @return La valeur du paramètre ou la valeur par défaut
     */
    public static Long getLongParameter(HttpServletRequest request, String paramName, Long defaultValue) {
        String value = request.getParameter(paramName);
        if (value != null && !value.trim().isEmpty()) {
            try {
                return Long.parseLong(value);
            } catch (NumberFormatException e) {
                logger.warn("Paramètre {} invalide: {}", paramName, value);
            }
        }
        return defaultValue;
    }

    /**
     * Récupère un paramètre de requête en tant que chaîne.
     *
     * @param request      La requête HTTP
     * @param paramName    Le nom du paramètre
     * @param defaultValue La valeur par défaut
     * @return La valeur du paramètre ou la valeur par défaut
     */
    public static String getStringParameter(HttpServletRequest request, String paramName, String defaultValue) {
        String value = request.getParameter(paramName);
        return (value != null && !value.trim().isEmpty()) ? value.trim() : defaultValue;
    }

    /**
     * Récupère le chemin de redirection après login, ou null.
     *
     * @param session La session HTTP
     * @return Le chemin de redirection, ou null
     */
    public static String getAndClearRedirectAfterLogin(HttpSession session) {
        if (session != null) {
            String redirect = (String) session.getAttribute("redirectAfterLogin");
            session.removeAttribute("redirectAfterLogin");
            return redirect;
        }
        return null;
    }

    /**
     * Obtient le dashboard URL basé sur le rôle de l'utilisateur.
     *
     * @param user        L'utilisateur
     * @param contextPath Le context path de l'application
     * @return L'URL du dashboard
     */
    public static String getDashboardUrl(User user, String contextPath) {
        if (user == null) {
            return contextPath + "/login";
        }

        String role = user.getRole();
        switch (role.toUpperCase()) {
            case "ORGANISATEUR":
                return contextPath + "/organizer/dashboard";
            case "PARTICIPANT":
                return contextPath + "/participant/dashboard";
            case "ADMINISTRATEUR":
                return contextPath + "/admin/dashboard";
            default:
                return contextPath + "/";
        }
    }
}

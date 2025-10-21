package ma.ensa.tetouan.eventmanagement.listener;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.annotation.WebListener;
import javax.servlet.http.HttpSessionEvent;
import javax.servlet.http.HttpSessionListener;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Écouteur de session pour suivre la création et la destruction des sessions.
 * Compte le nombre d'utilisateurs actifs sur la plateforme.
 *
 * @author ENSA Tétouan
 */
@WebListener
public class SessionListener implements HttpSessionListener {

    private static final Logger logger = LoggerFactory.getLogger(SessionListener.class);
    private static final AtomicInteger activeUsers = new AtomicInteger(0);

    @Override
    public void sessionCreated(HttpSessionEvent se) {
        int currentCount = activeUsers.incrementAndGet();
        String sessionId = se.getSession().getId();

        logger.info("Nouvelle session créée: {} - Utilisateurs actifs: {}", sessionId, currentCount);

        // Définir le timeout de session (30 minutes)
        se.getSession().setMaxInactiveInterval(30 * 60);
    }

    @Override
    public void sessionDestroyed(HttpSessionEvent se) {
        int currentCount = activeUsers.decrementAndGet();
        String sessionId = se.getSession().getId();

        Object currentUser = se.getSession().getAttribute("currentUser");
        if (currentUser != null) {
            logger.info("Session détruite pour utilisateur: {} - Session: {} - Utilisateurs actifs: {}",
                       currentUser, sessionId, currentCount);
        } else {
            logger.info("Session détruite: {} - Utilisateurs actifs: {}", sessionId, currentCount);
        }
    }

    /**
     * Récupère le nombre d'utilisateurs actifs.
     *
     * @return Le nombre d'utilisateurs actifs
     */
    public static int getActiveUsersCount() {
        return activeUsers.get();
    }
}

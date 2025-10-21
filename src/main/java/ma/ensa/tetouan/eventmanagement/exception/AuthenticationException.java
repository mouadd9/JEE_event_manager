package ma.ensa.tetouan.eventmanagement.exception;

/**
 * Exception levée lors d'un échec d'authentification.
 *
 * @author ENSA Tétouan
 */
public class AuthenticationException extends BusinessException {

    private static final long serialVersionUID = 1L;

    public AuthenticationException(String message) {
        super(message, "AUTH_ERROR");
    }

    public AuthenticationException(String message, Throwable cause) {
        super(message, cause, "AUTH_ERROR");
    }
}

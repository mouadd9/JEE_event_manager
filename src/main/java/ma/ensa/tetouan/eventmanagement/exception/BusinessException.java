package ma.ensa.tetouan.eventmanagement.exception;

/**
 * Exception de base pour toutes les exceptions métier de l'application.
 *
 * @author ENSA Tétouan
 */
public class BusinessException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    private String errorCode;

    /**
     * Constructeur avec message
     *
     * @param message Le message d'erreur
     */
    public BusinessException(String message) {
        super(message);
    }

    /**
     * Constructeur avec message et cause
     *
     * @param message Le message d'erreur
     * @param cause La cause de l'exception
     */
    public BusinessException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * Constructeur avec message et code d'erreur
     *
     * @param message Le message d'erreur
     * @param errorCode Le code d'erreur
     */
    public BusinessException(String message, String errorCode) {
        super(message);
        this.errorCode = errorCode;
    }

    /**
     * Constructeur avec message, cause et code d'erreur
     *
     * @param message Le message d'erreur
     * @param cause La cause de l'exception
     * @param errorCode Le code d'erreur
     */
    public BusinessException(String message, Throwable cause, String errorCode) {
        super(message, cause);
        this.errorCode = errorCode;
    }

    public String getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(String errorCode) {
        this.errorCode = errorCode;
    }
}

package ma.ensa.tetouan.eventmanagement.exception;

/**
 * Exception levée lors d'une transition d'état invalide pour un événement.
 *
 * @author ENSA Tétouan
 */
public class InvalidEventStateException extends BusinessException {

    private static final long serialVersionUID = 1L;

    public InvalidEventStateException(String message) {
        super(message, "INVALID_EVENT_STATE");
    }

    public InvalidEventStateException(String currentState, String targetState) {
        super("Transition d'état invalide: impossible de passer de " + currentState + " à " + targetState,
              "INVALID_EVENT_STATE");
    }
}

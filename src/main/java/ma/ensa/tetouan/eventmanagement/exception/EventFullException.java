package ma.ensa.tetouan.eventmanagement.exception;

/**
 * Exception levée lorsqu'un événement a atteint sa capacité maximale.
 *
 * @author ENSA Tétouan
 */
public class EventFullException extends BusinessException {

    private static final long serialVersionUID = 1L;

    public EventFullException(String message) {
        super(message, "EVENT_FULL");
    }

    public EventFullException(Long evenementId) {
        super("L'événement avec l'ID " + evenementId + " a atteint sa capacité maximale", "EVENT_FULL");
    }
}

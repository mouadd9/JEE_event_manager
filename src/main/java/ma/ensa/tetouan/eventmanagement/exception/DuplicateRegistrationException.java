package ma.ensa.tetouan.eventmanagement.exception;

/**
 * Exception levée lorsqu'un participant est déjà inscrit à un événement.
 *
 * @author ENSA Tétouan
 */
public class DuplicateRegistrationException extends BusinessException {

    private static final long serialVersionUID = 1L;

    public DuplicateRegistrationException(String message) {
        super(message, "DUPLICATE_REGISTRATION");
    }

    public DuplicateRegistrationException(Long participantId, Long evenementId) {
        super("Le participant " + participantId + " est déjà inscrit à l'événement " + evenementId,
              "DUPLICATE_REGISTRATION");
    }
}

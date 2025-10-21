package ma.ensa.tetouan.eventmanagement.exception;

/**
 * Exception levée lorsqu'une ressource demandée n'est pas trouvée.
 *
 * @author ENSA Tétouan
 */
public class ResourceNotFoundException extends BusinessException {

    private static final long serialVersionUID = 1L;

    public ResourceNotFoundException(String message) {
        super(message, "RESOURCE_NOT_FOUND");
    }

    public ResourceNotFoundException(String resourceType, Long resourceId) {
        super(resourceType + " avec l'ID " + resourceId + " introuvable", "RESOURCE_NOT_FOUND");
    }

    public ResourceNotFoundException(String resourceType, String identifier) {
        super(resourceType + " '" + identifier + "' introuvable", "RESOURCE_NOT_FOUND");
    }
}

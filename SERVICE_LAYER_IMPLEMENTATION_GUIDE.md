# Service Layer Implementation Guide

## Overview
This document provides the complete specification for implementing the Service Layer of the Event Management System.

## Architecture

```
Service Layer
├── Exception Handling (Custom Exceptions)
├── Business Logic (Service Classes)
├── Transaction Management (via TransactionUtil)
├── Validation (Input & Business Rules)
└── DAO Integration (Data Access)
```

---

## Exception Classes Created ✅

1. **BusinessException.java** - Base exception with error codes
2. **AuthenticationException.java** - Authentication failures
3. **EventFullException.java** - Event capacity reached
4. **DuplicateRegistrationException.java** - Duplicate registration attempt
5. **InvalidEventStateException.java** - Invalid state transitions
6. **ResourceNotFoundException.java** - Entity not found

---

## Utility Classes Created ✅

**PasswordUtil.java**
- `hashPassword(String plainPassword)` - SHA-256 with salt
- `verifyPassword(String plainPassword, String storedPassword)`
- `isStrongPassword(String password)` - Validation
- `generateTemporaryPassword(int length)` - Random passwords

---

## Services to Implement

### 1. UserService

**Interface Methods:**
```java
- User register(User user)
- User authenticate(String email, String password)
- User updateProfile(Long userId, User updatedData)
- void changePassword(Long userId, String oldPassword, String newPassword)
- void deactivateAccount(Long userId)
- List<User> getAllUsers(int page, int pageSize)
- User getUserById(Long userId)
- Optional<User> getUserByEmail(String email)
- long getTotalUsers()
```

**Business Rules:**
- Email must be unique
- Password must be strong (8+ chars, upper, lower, digit, special)
- Hash password before storage
- Validate email format
- Only active users can authenticate
- Update last login on successful auth

**Implementation Pattern:**
```java
public class UserServiceImpl implements UserService {
    private final UserDAO userDAO;
    private static final Logger logger = LoggerFactory.getLogger(UserServiceImpl.class);

    public UserServiceImpl() {
        this.userDAO = new UserDAOImpl();
    }

    @Override
    public User register(User user) {
        // 1. Validate input
        validateUserForRegistration(user);

        // 2. Check email uniqueness
        if (userDAO.existsByEmail(user.getEmail())) {
            throw new BusinessException("Email déjà utilisé");
        }

        // 3. Hash password
        user.setMotDePasse(PasswordUtil.hashPassword(user.getMotDePasse()));

        // 4. Set defaults
        user.setStatut(StatutUtilisateur.ACTIF);
        user.setDateInscription(LocalDateTime.now());

        // 5. Save
        return userDAO.save(user);
    }
}
```

---

### 2. EvenementService

**Key Methods:**
```java
- Evenement createEvenement(Evenement evenement, Long organisateurId)
- Evenement updateEvenement(Long evenementId, Evenement updatedData, Long organisateurId)
- void deleteEvenement(Long evenementId, Long organisateurId)
- Evenement publishEvenement(Long evenementId, Long organisateurId)
- Evenement annulerEvenement(Long evenementId, Long organisateurId)
- Evenement getEvenementById(Long evenementId)
- List<Evenement> getAllPublishedEvents(int page, int pageSize)
- List<Evenement> searchEvenements(String keyword, LocalDate startDate, LocalDate endDate, Long categorieId)
- List<Evenement> getUpcomingEvents(int page, int pageSize)
- void incrementViews(Long evenementId)
```

**Business Rules:**
- Only organisateurs can create events
- Only owner can modify/delete event
- BROUILLON → PUBLIE (publish)
- PUBLIE → ANNULE (cancel)
- Cannot delete event with accepted inscriptions
- Validate dates (end > start, future dates)
- Validate capacity > 0

**State Transitions:**
```
BROUILLON → PUBLIE (publishEvenement)
PUBLIE → ANNULE (annulerEvenement)
PUBLIE → TERMINE (automatic after date)
```

---

### 3. InscriptionService

**Key Methods:**
```java
- Inscription registerToEvent(Long participantId, Long evenementId)
- Inscription acceptInscription(Long inscriptionId, Long organisateurId)
- Inscription refuseInscription(Long inscriptionId, Long organisateurId)
- void cancelInscription(Long inscriptionId, Long participantId)
- List<Inscription> getInscriptionsByParticipant(Long participantId)
- List<Inscription> getInscriptionsByEvenement(Long evenementId)
- int getAvailableSeats(Long evenementId)
```

**Business Rules:**
- Event must be PUBLIE
- Check not already registered
- Check capacity:
  - If available → ACCEPTEE automatically
  - If full → EN_ATTENTE (waitlist)
- Update event.placesDisponibles
- Update event.nombreInscriptions
- Notify participant by email

**Registration Flow:**
```
1. Validate event is PUBLIE
2. Check duplicate registration
3. Check capacity
4. Create inscription with appropriate status
5. Update event statistics
6. Send confirmation email
```

---

### 4. EvaluationService

**Key Methods:**
```java
- Evaluation addEvaluation(Long participantId, Long evenementId, int note, String texte)
- Evaluation updateEvaluation(Long evaluationId, Long participantId, int note, String texte)
- void deleteEvaluation(Long evaluationId, Long participantId)
- List<Evaluation> getEvaluationsByEvenement(Long evenementId)
- Double getAverageRating(Long evenementId)
- boolean canEvaluate(Long participantId, Long evenementId)
```

**Business Rules:**
- Must have attended event (ACCEPTEE inscription)
- Event must be TERMINE
- One evaluation per participant per event
- Note must be 1-5
- Update event.noteMoyenne after each evaluation

---

### 5. CommentaireService

**Key Methods:**
```java
- Commentaire addCommentaire(Long participantId, Long evenementId, String texte)
- Commentaire updateCommentaire(Long commentaireId, Long participantId, String texte)
- void deleteCommentaire(Long commentaireId, Long participantId)
- List<Commentaire> getCommentairesByEvenement(Long evenementId)
- void reportCommentaire(Long commentaireId)
- void moderateCommentaire(Long commentaireId, boolean approved)
```

**Business Rules:**
- Only participants can comment
- Auto-hide after 3 reports
- Admin can moderate
- Owner can edit/delete own comments

---

### 6. CategorieService

**Key Methods:**
```java
- Categorie createCategorie(Categorie categorie)
- Categorie updateCategorie(Long categorieId, Categorie updatedData)
- void deleteCategorie(Long categorieId)
- List<Categorie> getAllCategories()
- Categorie getCategorieById(Long categorieId)
```

**Business Rules:**
- Name must be unique
- Cannot delete if has events
- Active/inactive flag

---

### 7. EmailService (Optional but Recommended)

**Key Methods:**
```java
- void sendRegistrationConfirmation(Inscription inscription)
- void sendEventReminder(Inscription inscription)
- void sendCancellationNotification(Inscription inscription)
- void sendPasswordResetEmail(User user, String temporaryPassword)
```

**Email Templates:**
- Registration confirmation
- Event reminder (1 day before)
- Cancellation notice
- Password reset

---

## Implementation Checklist

### For Each Service:

1. **Create Interface**
   - Define all public methods
   - Add JavaDoc with @param and @return
   - Specify exceptions with @throws

2. **Create Implementation**
   - Inject DAOs via constructor
   - Add logger
   - Implement all methods
   - Add validation methods
   - Handle exceptions properly

3. **Validation Methods**
   ```java
   private void validateEvenement(Evenement evenement) {
       if (evenement == null) {
           throw new IllegalArgumentException("L'événement ne peut pas être null");
       }
       if (evenement.getTitre() == null || evenement.getTitre().trim().isEmpty()) {
           throw new BusinessException("Le titre est obligatoire");
       }
       // ... more validations
   }
   ```

4. **Exception Handling**
   ```java
   try {
       // Business logic
   } catch (PersistenceException e) {
       logger.error("Erreur de persistance", e);
       throw new BusinessException("Erreur lors de l'enregistrement", e);
   }
   ```

5. **Logging**
   ```java
   logger.info("Création d'un événement par l'organisateur {}", organisateurId);
   logger.debug("Détails de l'événement: {}", evenement);
   logger.error("Erreur lors de la création", exception);
   ```

---

## Service Dependencies

```
UserService (base)
    ↓
OrganisateurService, ParticipantService, AdministrateurService
    ↓
EvenementService → CategorieService
    ↓
InscriptionService → EmailService
    ↓
CommentaireService, EvaluationService
```

---

## Testing Strategy

### Unit Tests:
```java
@Test
public void testRegisterUser_Success() {
    // Given
    User user = new Participant("John", "john@test.com", "Password123!");

    // When
    User registered = userService.register(user);

    // Then
    assertNotNull(registered.getId());
    assertTrue(PasswordUtil.verifyPassword("Password123!", registered.getMotDePasse()));
}

@Test(expected = BusinessException.class)
public void testRegisterUser_DuplicateEmail() {
    // Test duplicate email
}
```

---

## Security Considerations

1. **Password Security**
   - Always hash passwords
   - Use salt
   - Never log passwords
   - Implement password strength validation

2. **Authorization**
   - Check ownership before modifications
   - Validate user roles
   - Implement proper access control

3. **Input Validation**
   - Sanitize all inputs
   - Validate email format
   - Check for SQL injection attempts
   - Limit string lengths

4. **Error Messages**
   - Don't reveal sensitive information
   - Generic messages for auth failures
   - Detailed logs for debugging

---

## Performance Optimization

1. **Caching Strategy**
   - Cache frequently accessed categories
   - Cache published events list
   - Invalidate on updates

2. **Lazy Loading**
   - Load relationships only when needed
   - Use DTO for list views

3. **Batch Operations**
   - Batch email sending
   - Bulk inscription processing

---

## Next Steps

1. Implement UserService (template for others)
2. Implement EvenementService (most complex)
3. Implement InscriptionService (critical business logic)
4. Implement remaining services
5. Create integration tests
6. Document API endpoints

---

**Version**: 1.0
**Date**: 2025
**Author**: ENSA Tétouan

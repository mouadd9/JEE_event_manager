# Updated Merge Plan - Event Management System

## Your Specifications:
- **Evenement**: Long ID, no inheritance, Lombok, validation, capacity + image + coordinates, categories, relationships to reviews/comments, use enum
- **Utilisateur**: no inheritance, Lombok, motDePasseHash, validation, include userType
- **Organisateur**: same as Utilisateur, use JOINED strategy, extend Utilisateur, add relationship to events
- **Participant**: same as Organisateur, add Personal Info, add relationships to inscriptions/comments/reviews

## Phase 1: Resolve Configuration Conflicts ✅

### 1.1 Merge pom.xml
- Remove conflict markers
- Add Lombok dependency
- Include Hibernate Core and Validator
- Keep all dependencies from both branches

### 1.2 Merge persistence.xml
- Use JTA transactions (`transaction-type="JTA"`)
- Use datasource: `java:jboss/datasources/EventDS`
- List ALL entity classes explicitly (French names)
- Keep Hibernate settings (show_sql, format_sql)
- Set `hibernate.hbm2ddl.auto` to `validate`

### 1.3 Merge web.xml
- Keep Branch A's configuration (context params, resource-ref, CDI listener)
- Keep metadata-complete=false for annotation scanning

### 1.4 Merge beans.xml
- Use version 3.0 from Branch A
- Keep `bean-discovery-mode="all"`

## Phase 2: Unified Entity Model (French + Lombok)

### 2.1 Delete Branch B entities
Remove: `Event.java`, `User.java`, `Organizer.java`, `Comment.java`, `Review.java`

### 2.2 Create Utilisateur (base user class)
- Keep French name: `Utilisateur`
- **NO inheritance** (remove BaseEntity)
- Add Lombok annotations: `@Getter`, `@Setter`, `@NoArgsConstructor`, `@AllArgsConstructor`
- Remove manual getters/setters
- Keep validation annotations (`@NotBlank`, `@Email`)
- Add timestamps: `createdAt`, `updatedAt` with `@PrePersist`/`@PreUpdate`
- Keep inheritance strategy: `JOINED`
- Use `motDePasseHash` field name
- Include `userType` enum field

### 2.3 Create Participant
- Keep French table name: `participant`
- **Extend Utilisateur** with JOINED strategy
- Add Lombok annotations
- Add Personal Info fields: `dateNaissance`, `preferences`, `telephone`
- Add relationships: `@OneToMany` to Inscription, Commentaire, Evaluation

### 2.4 Create Organisateur
- Keep French table name: `organisateur`
- **Extend Utilisateur** with JOINED strategy
- Add Lombok annotations
- Add business fields: `description`, `entreprise`, `siret`, `siteWeb`
- Add relationship: `@OneToMany` to Evenement

### 2.5 Create Evenement (merged from Event + Evenement)
**Critical merge** - combine both branches:
- Keep French name: `Evenement`
- Use **Long ID** (not Integer)
- **NO inheritance** (remove BaseEntity)
- Use Lombok annotations
- Add validation annotations
- **Combine ALL fields**:
  - From Branch A: titre, description, dateDebut, dateFin, statut, lieu, capacite, imageUrl, organisateur, categories
  - From Branch B: latitude, longitude
  - Add timestamps: createdAt, updatedAt
- Use `StatutEvenement` enum (BROUILLON, PUBLIE, ANNULE)
- Add relationships: `@OneToMany` to Commentaire, Evaluation, Inscription
- Match database schema exactly

### 2.6 Resolve Inscription conflict
- Keep French table name: `inscription`
- Use Lombok
- **NO inheritance** (remove BaseEntity)
- Keep ALL fields: participant, evenement, dateInscription, statut, typeBillet, quantite
- Use `StatutInscription` enum
- Match database schema

### 2.7 Update Commentaire & Evaluation
- Convert to Lombok (remove manual getters/setters)
- **NO inheritance** (remove BaseEntity)
- Keep all validation logic
- Add direct timestamps instead of inheriting

### 2.8 Update Categorie
- Convert to Lombok
- **NO inheritance** (remove BaseEntity)
- Keep ManyToMany with Evenement

### 2.9 Delete duplicate enums
- Delete `enums/EventStatus.java` (keep `model/StatutEvenement.java`)

## Phase 3: Data Access Layer - Repository Pattern

### 3.1 Create Repository interfaces (French naming)
- `UtilisateurRepository` (base user repository)
- `ParticipantRepository extends UtilisateurRepository`
- `OrganisateurRepository extends UtilisateurRepository`
- `EvenementRepository` (merge EventRepository + EvenementDAO logic)
- `InscriptionRepository`
- `CommentaireRepository`
- `EvaluationRepository`
- `CategorieRepository`

### 3.2 Implement Repositories
- Create `DAO/impl/` package for implementations
- Use `@ApplicationScoped` for CDI
- Inject EntityManager with JTA support
- Migrate logic from existing DAOs to repository implementations
- Remove old DAO files after migration

### 3.3 Update EntityManagerProducer
- Change to JTA: `@PersistenceContext(unitName = "default")`
- Remove RESOURCE_LOCAL transaction handling

## Phase 4: Service Layer Integration

### 4.1 Merge EvenementService
- Combine `EvenementService` (Branch A) + `EventService`/`EventServiceImp` (Branch B)
- Use `@Stateless` EJB for automatic JTA
- Inject `EvenementRepository`
- Keep all methods from both branches:
  - Branch A: filtering, search, findByStatut
  - Branch B: CRUD, publish, unpublish, cancel
- Use French method names

### 4.2 Update InscriptionService
- Switch from manual transactions to JTA (remove `em.getTransaction()` calls)
- Use `@Stateless` EJB
- Inject `InscriptionRepository`
- Keep all validation logic

### 4.3 Create/Update OrganisateurService
- Merge `OrganizerService` from Branch B
- Use French naming
- Use Repository pattern

### 4.4 Update UtilisateurService
- Use Repository pattern
- Support both Participant and Organisateur

### 4.5 Update CommentaireService & EvaluationService
- Use Repository pattern
- Switch to JTA

### 4.6 Update CategorieService
- Use Repository pattern
- Switch to JTA

## Phase 5: DTOs and Mappers

### 5.1 Merge EventDto with EvenementDetailDTO
- Create comprehensive `EvenementDTO` with ALL fields:
  - Basic info, dates, location (with coordinates)
  - Organizer info, categories
  - Stats: inscriptions count, capacity available

### 5.2 Create EvenementMapper
- Merge logic from `EventMapper` + any existing mappers
- Map between `Evenement` entity and `EvenementDTO`
- Handle all relationships properly

### 5.3 Keep existing DTOs
- `InscriptionDTO`, `InscriptionRequest`
- `CommentaireDTO`, `CommentaireRequest`
- `EvaluationDTO`, `EvaluationRequest`

## Phase 6: Servlet Layer Integration

### 6.1 Update OrganizerServlet
- Rename to `OrganisateurServlet`
- Update to use merged `EvenementService`
- Use French entity names
- Update JSP paths if needed

### 6.2 Keep Participant Servlets
- `CatalogueServlet` - update to use merged service
- `InscriptionServlet` - update to use repositories
- `ParticipantDashboardServlet`
- `ParticipantProfilServlet`
- `CommentaireServlet`
- `EvaluationServlet`

### 6.3 Keep Common Servlets
- `LoginServlet`, `LogoutServlet`, `RegisterServlet`



### 7.1 Update all imports
- Change from `Event` to `Evenement`
- Change from `User/Organizer` to `Utilisateur/Organisateur`
- Update enum imports

### 7.2 Verify JSP files
- Update entity references to French names
- Verify all EL expressions work with new entity structure

### 7.3 Clean up
- Delete unused files (Branch B entities, old DAOs)
- Remove conflict markers completely
- Ensure no compilation errors

### 7.4 Git commit
- Resolve merge conflicts
- Commit with message: "Merge participant and organizer branches - unified French entities with Lombok and Repository pattern"

## Key Changes from Original Plan:
1. **Evenement**: Use Long ID instead of Integer
2. **All entities**: NO inheritance (remove BaseEntity completely)
3. **Utilisateur**: Include userType field
4. **Organisateur**: Extend Utilisateur with JOINED strategy + relationship to events
5. **Participant**: Extend Utilisateur with JOINED strategy + personal info + relationships
6. **All entities**: Use Lombok annotations
7. **All entities**: Keep validation annotations

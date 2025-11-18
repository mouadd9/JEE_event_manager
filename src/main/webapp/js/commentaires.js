if (typeof API_BASE === "undefined") {
  var API_BASE = window.location.origin + "/jee-event-manager";
}

// ========== Chargement Commentaires ==========
async function chargerCommentaires(evenementId) {
  const container = document.getElementById("commentairesSection");
  if (!container) return;

  try {
    container.innerHTML = `
            <div class="text-center py-3">
                <div class="spinner-border spinner-border-sm text-primary"></div>
                <p class="text-muted small mt-2">Chargement des commentaires...</p>
            </div>
        `;

    const response = await fetch(
      `${API_BASE}/api/commentaires?evenementId=${evenementId}`,
      {
        method: "GET",
        credentials: "include",
        headers: {
          Accept: "application/json",
        },
      }
    );

    if (!response.ok) {
      throw new Error(`Erreur ${response.status}`);
    }

    const result = await response.json();

    if (result.success) {
      afficherCommentaires(result.data || [], evenementId);
    } else {
      container.innerHTML = `
                <div class="alert alert-warning">
                    <i class="bi bi-exclamation-triangle me-2"></i>
                    Impossible de charger les commentaires
                </div>
            `;
    }
  } catch (error) {
    console.error("Erreur chargerCommentaires:", error);
    container.innerHTML = `
            <div class="alert alert-danger">
                <i class="bi bi-x-circle me-2"></i>
                Erreur lors du chargement des commentaires
            </div>
        `;
  }
}

// ========== Affichage Commentaires ==========
function afficherCommentaires(commentaires, evenementId) {
  const container = document.getElementById("commentairesSection");
  if (!container) return;

  // Vérifier si l'utilisateur est inscrit (pour afficher le formulaire)
  const dashboardData = window.dashboardAPI?.getDashboardData();
  let event = dashboardData?.evenementsDisponibles?.find(
    (e) => e.evenementId == evenementId
  );

  // Si non trouvé dans les événements disponibles, chercher dans les inscriptions
  if (!event) {
    const inscriptionsActives =
      window.dashboardAPI?.getInscriptionsActives?.() || [];
    const inscriptionsPassees =
      window.dashboardAPI?.getInscriptionsPassees?.() || [];
    const inscriptionsAnnulees =
      window.dashboardAPI?.getInscriptionsAnnulees?.() || [];

    const inscription =
      inscriptionsActives.find((i) => i.evenementId == evenementId) ||
      inscriptionsPassees.find((i) => i.evenementId == evenementId) ||
      inscriptionsAnnulees.find((i) => i.evenementId == evenementId);

    if (inscription) {
      event = { statutInscription: inscription.statut };
    }
  }

  const isInscrit = event?.statutInscription === "ACCEPTEE";

  let html = "";

  // Formulaire d'ajout (si inscrit)
  if (isInscrit) {
    html += `
            <div class="card mb-3">
                <div class="card-body">
                    <h6 class="card-title mb-3">Ajouter un commentaire</h6>
                    <form id="formCommentaire" data-event-id="${evenementId}">
                        <div class="mb-3">
                            <textarea class="form-control" id="commentaireTexte" 
                                      rows="3" maxlength="1000" 
                                      placeholder="Partagez votre avis sur cet événement..." 
                                      required></textarea>
                            <div class="form-text">
                                <span id="commentaireCount">0</span>/1000 caractères
                            </div>
                        </div>
                        <button type="submit" class="btn btn-primary btn-sm">
                            <i class="bi bi-send me-1"></i>Publier
                        </button>
                    </form>
                </div>
            </div>
        `;
  }

  // Liste des commentaires
  if (commentaires.length === 0) {
    html += `
            <div class="text-center py-4">
                <i class="bi bi-chat-square-dots text-muted" style="font-size: 3rem;"></i>
                <p class="text-muted mt-2">Aucun commentaire pour le moment</p>
                ${
                  isInscrit
                    ? '<p class="text-muted small">Soyez le premier à commenter !</p>'
                    : ""
                }
            </div>
        `;
  } else {
    html += '<div class="commentaires-list">';
    commentaires.forEach((commentaire) => {
      html += creerCommentaireHTML(commentaire);
    });
    html += "</div>";
  }

  container.innerHTML = html;

  // Attacher les event listeners
  attacherCommentaireListeners(evenementId);
}

// ========== Création HTML Commentaire ==========
function creerCommentaireHTML(commentaire) {
  const date = new Date(commentaire.horodatage);
  const tempsEcoule = getTempsEcoule(date);

  // Vérifier si c'est le commentaire de l'utilisateur connecté
  const dashboardData = window.dashboardAPI?.getDashboardData();
  const participantId = dashboardData?.participant?.id;
  const isOwner = participantId && participantId === commentaire.participantId;

  return `
        <div class="commentaire-item border-bottom py-3" data-commentaire-id="${
          commentaire.commentaireId
        }">
            <div class="d-flex justify-content-between align-items-start mb-2">
                <div class="d-flex align-items-center">
                    <div class="avatar-circle bg-primary text-white me-2">
                        ${
                          commentaire.participantNom
                            ? commentaire.participantNom.charAt(0).toUpperCase()
                            : "U"
                        }
                    </div>
                    <div>
                        <div class="fw-semibold">${escapeHtml(
                          commentaire.participantNom || "Utilisateur"
                        )}</div>
                        <div class="text-muted small">${tempsEcoule}</div>
                    </div>
                </div>
                ${
                  isOwner
                    ? `
                    <button class="btn btn-sm btn-outline-danger btn-delete-commentaire" 
                            data-commentaire-id="${commentaire.commentaireId}">
                        <i class="bi bi-trash"></i>
                    </button>
                `
                    : ""
                }
            </div>
            <p class="mb-0">${escapeHtml(commentaire.texte)}</p>
        </div>
    `;
}

// ========== Event Listeners ==========
function attacherCommentaireListeners(evenementId) {
  // Formulaire d'ajout
  const form = document.getElementById("formCommentaire");
  const textarea = document.getElementById("commentaireTexte");
  const counter = document.getElementById("commentaireCount");

  // Compteur de caractères
  textarea?.addEventListener("input", function () {
    if (counter) {
      counter.textContent = this.value.length;
    }
  });

  // Soumission du formulaire
  form?.addEventListener("submit", async function (e) {
    e.preventDefault();

    const texte = textarea.value.trim();
    if (!texte) {
      window.dashboardAPI?.showToast(
        "Le commentaire ne peut pas être vide",
        "warning"
      );
      return;
    }

    await ajouterCommentaire(evenementId, texte);
  });

  // Boutons de suppression
  document.querySelectorAll(".btn-delete-commentaire").forEach((btn) => {
    btn.addEventListener("click", async function () {
      const commentaireId = this.dataset.commentaireId;
      if (confirm("Êtes-vous sûr de vouloir supprimer ce commentaire ?")) {
        await supprimerCommentaire(commentaireId, evenementId);
      }
    });
  });
}

// ========== Ajouter Commentaire ==========
async function ajouterCommentaire(evenementId, texte) {
  const form = document.getElementById("formCommentaire");
  const submitBtn = form?.querySelector('button[type="submit"]');

  if (submitBtn) {
    submitBtn.disabled = true;
    submitBtn.innerHTML =
      '<span class="spinner-border spinner-border-sm me-1"></span>Publication...';
  }

  try {
    const response = await fetch(
      `${API_BASE}/api/commentaires?evenementId=${evenementId}`,
      {
        method: "POST",
        credentials: "include",
        headers: {
          "Content-Type": "application/json",
          Accept: "application/json",
        },
        body: JSON.stringify({ texte }),
      }
    );

    const result = await response.json();

    if (response.ok && result.success) {
      window.dashboardAPI?.showToast(
        "Commentaire publié avec succès ! ✓",
        "success"
      );

      // Recharger les commentaires
      await chargerCommentaires(evenementId);

      // Mettre à jour immédiatement les statistiques locales (nombre de commentaires)
      window.dashboardAPI?.incrementCommentaires?.();
    } else {
      window.dashboardAPI?.showToast(
        result.message || "Erreur lors de la publication",
        "error"
      );
    }
  } catch (error) {
    console.error("Erreur ajouterCommentaire:", error);
    window.dashboardAPI?.showToast("Erreur de connexion au serveur", "error");
  } finally {
    if (submitBtn) {
      submitBtn.disabled = false;
      submitBtn.innerHTML = '<i class="bi bi-send me-1"></i>Publier';
    }
  }
}

// ========== Supprimer Commentaire ==========
async function supprimerCommentaire(commentaireId, evenementId) {
  try {
    const response = await fetch(
      `${API_BASE}/api/commentaires/${commentaireId}`,
      {
        method: "DELETE",
        credentials: "include",
        headers: {
          Accept: "application/json",
        },
      }
    );

    const result = await response.json();

    if (response.ok && result.success) {
      window.dashboardAPI?.showToast("Commentaire supprimé", "success");

      // Recharger les commentaires
      await chargerCommentaires(evenementId);
    } else {
      window.dashboardAPI?.showToast(
        result.message || "Erreur lors de la suppression",
        "error"
      );
    }
  } catch (error) {
    console.error("Erreur supprimerCommentaire:", error);
    window.dashboardAPI?.showToast("Erreur de connexion au serveur", "error");
  }
}

// ========== Utilitaires ==========
function getTempsEcoule(date) {
  const maintenant = new Date();
  const diffMs = maintenant - date;
  const diffMin = Math.floor(diffMs / 60000);
  const diffHours = Math.floor(diffMs / 3600000);
  const diffDays = Math.floor(diffMs / 86400000);

  if (diffMin < 1) return "À l'instant";
  if (diffMin < 60) return `Il y a ${diffMin} min`;
  if (diffHours < 24) return `Il y a ${diffHours}h`;
  if (diffDays < 7) return `Il y a ${diffDays}j`;

  return date.toLocaleDateString("fr-FR", {
    day: "numeric",
    month: "short",
    year: "numeric",
  });
}

function escapeHtml(text) {
  if (!text) return "";
  const div = document.createElement("div");
  div.textContent = text;
  return div.innerHTML;
}

// ========== CSS Inline pour les avatars ==========
const commentairesStyle = document.createElement("style");
commentairesStyle.textContent = `
    .avatar-circle {
        width: 40px;
        height: 40px;
        border-radius: 50%;
        display: flex;
        align-items: center;
        justify-content: center;
        font-weight: bold;
        font-size: 1.1rem;
    }
    
    .commentaire-item:last-child {
        border-bottom: none !important;
    }
    
    .commentaires-list {
        max-height: 500px;
        overflow-y: auto;
    }
`;
document.head.appendChild(commentairesStyle);

// ========== Ouverture Modal Commentaire (Post-Événement) ==========
function openAddCommentModal(eventId) {
  const modal = document.getElementById("modalAddComment");
  if (!modal) {
    console.error("Modal commentaire non trouvée");
    return;
  }

  // Récupérer les informations de l'événement
  const dashboardData = window.dashboardAPI?.getDashboardData();
  const event = dashboardData?.evenementsDisponibles?.find(
    (e) => e.evenementId == eventId
  );

  if (!event) {
    console.error("Événement non trouvé");
    return;
  }

  // Remplir les informations de l'événement
  const eventInfo = document.getElementById("commentEventInfo");
  if (eventInfo) {
    eventInfo.innerHTML = `
            <div class="card bg-light">
                <div class="card-body">
                    <h6 class="card-title mb-2">${escapeHtml(event.titre)}</h6>
                    <p class="card-text small text-muted mb-0">
                        <i class="bi bi-calendar3 me-1"></i>
                        ${new Date(event.dateDebut).toLocaleDateString(
                          "fr-FR",
                          {
                            day: "numeric",
                            month: "long",
                            year: "numeric",
                          }
                        )}
                    </p>
                </div>
            </div>
        `;
  }

  // Réinitialiser le formulaire
  const form = document.getElementById("formComment");
  if (form) {
    form.reset();
    document.getElementById("commentEventId").value = eventId;
    document.getElementById("commentCharCount").textContent = "0";
  }

  // Afficher le modal
  const bootstrapModal = new bootstrap.Modal(modal);
  bootstrapModal.show();
}

// ========== Export ==========
window.chargerCommentaires = chargerCommentaires;
window.openAddCommentModal = openAddCommentModal;

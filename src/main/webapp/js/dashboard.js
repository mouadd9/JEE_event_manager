const LOCAL_ENDPOINTS = {
  dashboard: `${
    window.location.origin + "/jee-event-manager"
  }/participant/dashboard`,
  inscriptions: `${
    window.location.origin + "/jee-event-manager"
  }/participant/inscriptions`,
  profil: `${window.location.origin + "/jee-event-manager"}/participant/profil`,
  evenements: `${window.location.origin + "/jee-event-manager"}/catalogue`,
};

// État global
let dashboardData = null;
let currentSection = "evenements";
let filteredEvents = [];
let inscriptionsActives = [];
let inscriptionsPassees = [];
let inscriptionsAnnulees = [];

// ========== Initialisation ==========
document.addEventListener("DOMContentLoaded", function () {
  console.log("Dashboard initialized");

  // Charger les données du dashboard
  loadDashboard();

  // Initialiser la navigation
  initNavigation();

  // Initialiser les filtres
  initFilters();

  // Initialiser les event listeners
  initEventListeners();
});

// ========== Chargement Dashboard ==========
async function loadDashboard(forceReload = false) {
  try {
    showLoading("eventsLoading");

    // Si forceReload, recharger depuis le serveur
    if (forceReload) {
      console.log("Rechargement complet du dashboard...");
      window.location.reload();
      return;
    }

    // Utiliser les données déjà chargées depuis le serveur
    if (window.serverData) {
      dashboardData = window.serverData;
      updateDashboardUI();
    } else {
      showToast("Erreur: Données non disponibles", "error");
    }
  } catch (error) {
    console.error("Erreur loadDashboard:", error);
    showToast("Impossible de charger le dashboard", "error");
    hideLoading("eventsLoading");
  }
}

// ========== Mise à jour UI ==========
function updateDashboardUI() {
  if (!dashboardData) return;

  // Mettre à jour le nom du participant
  const participantName = dashboardData.participant?.nom || "Participant";
  document.getElementById("userName").textContent = participantName;
  document.getElementById("welcomeName").textContent = participantName;

  // Mettre à jour les statistiques
  updateStatistics();

  // Afficher les événements disponibles
  displayEvents(dashboardData.evenementsDisponibles || []);

  // Charger les catégories pour le filtre
  loadCategories();
}

// ========== Statistiques ==========
function updateStatistics() {
  const stats = dashboardData.statistiques || {};

  document.getElementById("statInscriptionsActives").textContent =
    stats.nombreInscriptionsActives || 0;
  document.getElementById("statEvenementsParticipes").textContent =
    stats.nombreEvenementsParticipes || 0;
  document.getElementById("statCommentaires").textContent =
    stats.nombreCommentaires || 0;
  document.getElementById("statEvaluations").textContent =
    stats.nombreEvaluations || 0;
}

// Incrémenter une statistique locale et mettre à jour l'UI
function incrementStatistic(fieldName) {
  if (!dashboardData) return;
  if (!dashboardData.statistiques) {
    dashboardData.statistiques = {};
  }
  const currentValue = parseInt(dashboardData.statistiques[fieldName] || 0, 10);
  dashboardData.statistiques[fieldName] = currentValue + 1;
  updateStatistics();
}

// ========== Charger statistiques dynamiquement ==========
async function refreshStatistics() {
  try {
    const response = await fetch(`${API_BASE}/participant/inscriptions`, {
      method: "GET",
      credentials: "include",
      headers: {
        Accept: "application/json",
      },
    });

    if (!response.ok) return;

    const result = await response.json();

    if (result.success && result.data) {
      const inscriptions = result.data;
      const now = new Date();

      const actives = inscriptions.filter(
        (i) =>
          (i.statut === "ACCEPTEE" || i.statut === "EN_ATTENTE") &&
          new Date(i.evenementDateDebut) > now
      ).length;

      const passees = inscriptions.filter(
        (i) => i.statut === "ACCEPTEE" && new Date(i.evenementDateFin) < now
      ).length;

      document.getElementById("statInscriptionsActives").textContent = actives;
      document.getElementById("statEvenementsParticipes").textContent = passees;
    }
  } catch (error) {
    console.error("Erreur refreshStatistics:", error);
  }
}

// Rafraîchir les statistiques au chargement
setTimeout(refreshStatistics, 500);

// ========== Affichage Événements ==========
function displayEvents(events) {
  const grid = document.getElementById("eventsGrid");
  const loading = document.getElementById("eventsLoading");
  const empty = document.getElementById("eventsEmpty");

  hideLoading("eventsLoading");

  if (!events || events.length === 0) {
    grid.innerHTML = "";
    empty.classList.remove("d-none");
    return;
  }

  empty.classList.add("d-none");
  filteredEvents = events;

  grid.innerHTML = events.map((event) => createEventCard(event)).join("");

  // Attacher les event listeners
  attachEventCardListeners();
}

// ========== Création Carte Événement ==========
function createEventCard(event) {
  const dateDebut = new Date(event.dateDebut);
  const dateFormatted = dateDebut.toLocaleDateString("fr-FR", {
    day: "numeric",
    month: "short",
    year: "numeric",
  });

  const timeFormatted = dateDebut.toLocaleTimeString("fr-FR", {
    hour: "2-digit",
    minute: "2-digit",
  });

  // Déterminer le badge d'inscription
  let inscriptionBadge = "";
  if (event.statutInscription) {
    const statutLabels = {
      ACCEPTEE: '<span class="badge badge-inscrit">✓ Inscrit</span>',
      EN_ATTENTE: '<span class="badge bg-warning">⏳ En attente</span>',
      ANNULEE: '<span class="badge bg-secondary">✗ Annulée</span>',
    };
    inscriptionBadge = statutLabels[event.statutInscription] || "";
  }

  // Note moyenne
  const rating = event.noteMoyenne || 0;
  const stars =
    "★".repeat(Math.floor(rating)) + "☆".repeat(5 - Math.floor(rating));

  // Capacité
  const capaciteClass =
    event.capaciteDisponible <= 10 ? "text-danger" : "text-success";

  return `
        <div class="col-lg-4 col-md-6">
            <div class="event-card" data-event-id="${event.evenementId}">
                <div class="event-card-image">
                    ${
                      event.imageUrl
                        ? `
                        <img src="${escapeHtml(
                          event.imageUrl
                        )}" alt="${escapeHtml(event.titre)}" 
                             onerror="this.src='https://images.unsplash.com/photo-1492684223066-81342ee5ff30?w=800'; this.style.display='block';">
                    `
                        : `
                        <div style="width:100%; height:200px; background:#f0f0f0; display:flex; align-items:center; justify-content:center;">
                            <i class="bi bi-calendar-event" style="font-size:2rem; color:#999;"></i>
                        </div>
                    `
                    }
                </div>
                <div class="event-card-body">
                    <div class="d-flex justify-content-between align-items-start mb-2">
                        <h5 class="event-card-title mb-0">${escapeHtml(
                          event.titre
                        )}</h5>
                        ${inscriptionBadge}
                    </div>
                    
                    <div class="event-card-info">
                        <i class="bi bi-calendar3"></i>
                        ${dateFormatted} à ${timeFormatted}
                    </div>
                    
                    <div class="event-card-info">
                        <i class="bi bi-geo-alt"></i>
                        ${escapeHtml(event.lieu)}
                    </div>
                    
                    <div class="event-card-info">
                        <i class="bi bi-people"></i>
                        <span class="${capaciteClass}">
                            ${event.nombreInscrits}/${event.capacite} inscrits
                        </span>
                    </div>
                    
                    ${
                      event.description
                        ? `
                        <p class="text-muted small mt-2 mb-0" style="display: -webkit-box; -webkit-line-clamp: 2; -webkit-box-orient: vertical; overflow: hidden;">
                            ${escapeHtml(event.description)}
                        </p>
                    `
                        : ""
                    }
                </div>
                <div class="event-card-footer">
                    <div class="d-flex justify-content-between align-items-center mb-2">
                        <div class="event-rating">
                            <span class="stars">${stars}</span>
                            <span class="text-muted small">(${
                              event.nombreEvaluations || 0
                            })</span>
                        </div>
                        <div>
                            ${
                              !event.statutInscription &&
                              event.capaciteDisponible > 0
                                ? `
                                <button class="btn btn-primary btn-sm btn-inscrire" data-event-id="${event.evenementId}">
                                    <i class="bi bi-ticket-perforated me-1"></i>S'inscrire
                                </button>
                            `
                                : event.capaciteDisponible === 0
                                ? `
                                <span class="badge bg-danger">Complet</span>
                            `
                                : ""
                            }
                            <button class="btn btn-outline-primary btn-sm btn-details" data-event-id="${
                              event.evenementId
                            }">
                                <i class="bi bi-info-circle"></i>
                            </button>
                        </div>
                    </div>
                    ${
                      event.statutInscription === "ACCEPTEE"
                        ? `
                        <div class="d-flex gap-2">
                            <button class="btn btn-outline-primary btn-comment btn-sm flex-fill btn-add-comment" 
                                    data-evenement-id="${event.evenementId}"
                                    data-evenement-titre="${escapeHtml(
                                      event.titre
                                    )}"
                                    onclick="event.stopPropagation()">
                                <i class="bi bi-chat-dots me-1"></i>Commenter
                            </button>
                            <button class="btn btn-outline-primary btn-evaluate btn-sm flex-fill btn-add-evaluation" 
                                    data-evenement-id="${event.evenementId}"
                                    data-evenement-titre="${escapeHtml(
                                      event.titre
                                    )}"
                                    onclick="event.stopPropagation()">
                                <i class="bi bi-star me-1"></i>Évaluer
                            </button>
                        </div>
                    `
                        : ""
                    }
                </div>
            </div>
        </div>
    `;
}

// ========== Event Listeners Cartes ==========
function attachEventCardListeners() {
  // Boutons S'inscrire
  document.querySelectorAll(".btn-inscrire").forEach((btn) => {
    btn.addEventListener("click", function (e) {
      e.stopPropagation();
      const eventId = this.dataset.eventId;
      openInscriptionModal(eventId);
    });
  });

  // Boutons Détails
  document.querySelectorAll(".btn-details").forEach((btn) => {
    btn.addEventListener("click", function (e) {
      e.stopPropagation();
      const eventId = this.dataset.eventId;
      openEventDetailsModal(eventId);
    });
  });

  // Boutons Commenter
  document.querySelectorAll(".btn-add-comment").forEach((btn) => {
    btn.addEventListener("click", function (e) {
      e.stopPropagation();
      const eventId = this.dataset.evenementId || this.dataset.eventId;
      if (!eventId) return;
      if (typeof window.openAddCommentModal === "function") {
        window.openAddCommentModal(eventId);
      } else {
        console.warn("openAddCommentModal non disponible");
      }
    });
  });

  // Boutons Évaluer
  document.querySelectorAll(".btn-add-evaluation").forEach((btn) => {
    btn.addEventListener("click", function (e) {
      e.stopPropagation();
      const eventId = this.dataset.evenementId || this.dataset.eventId;
      if (!eventId) return;
      if (typeof window.openAddEvaluationModal === "function") {
        window.openAddEvaluationModal(eventId);
      } else {
        console.warn("openAddEvaluationModal non disponible");
      }
    });
  });

  // Clic sur la carte entière
  document.querySelectorAll(".event-card").forEach((card) => {
    card.addEventListener("click", function () {
      const eventId = this.dataset.eventId;
      openEventDetailsModal(eventId);
    });
  });
}

// ========== Navigation ==========
function initNavigation() {
  // Navigation principale
  document.querySelectorAll("[data-section]").forEach((link) => {
    link.addEventListener("click", function (e) {
      e.preventDefault();
      const section = this.dataset.section;
      navigateToSection(section);
    });
  });
}

function navigateToSection(section) {
  currentSection = section;

  // Mettre à jour les liens actifs
  document.querySelectorAll("[data-section]").forEach((link) => {
    link.classList.remove("active");
    if (link.dataset.section === section) {
      link.classList.add("active");
    }
  });

  // Afficher la section correspondante
  document.querySelectorAll(".content-section").forEach((sec) => {
    sec.classList.add("d-none");
  });

  const targetSection = document.getElementById(`section-${section}`);
  if (targetSection) {
    targetSection.classList.remove("d-none");
  }

  // Charger les données spécifiques
  if (section === "inscriptions") {
    loadInscriptions();
  } else if (section === "calendrier") {
    // Initialiser le calendrier si pas déjà fait
    if (window.calendarAPI && !window.calendarAPI.getEvents().length) {
      // Le calendrier s'initialisera automatiquement via l'observer
      setTimeout(() => {
        if (window.calendarAPI && window.calendarAPI.refresh) {
          window.calendarAPI.refresh();
        }
      }, 100);
    }
  } else if (section === "profil") {
    // Initialiser le module profil si disponible
    if (window.initProfilModule) {
      window.initProfilModule();
    }
  }
}

// ========== Chargement Inscriptions ==========
async function loadInscriptions() {
  try {
    // Charger les inscriptions depuis le serveur
    const response = await fetch(`${API_BASE}/participant/inscriptions`, {
      method: "GET",
      credentials: "include",
      headers: {
        Accept: "application/json",
      },
    });

    if (!response.ok) {
      console.error("Erreur chargement inscriptions:", response.status);
      return;
    }

    const result = await response.json();

    if (result.success && result.data) {
      const inscriptions = result.data;

      // Séparer par statut et date
      const now = new Date();
      inscriptionsActives = inscriptions.filter(
        (i) =>
          (i.statut === "ACCEPTEE" || i.statut === "EN_ATTENTE") &&
          new Date(i.evenementDateDebut) > now
      );
      inscriptionsPassees = inscriptions.filter(
        (i) => i.statut === "ACCEPTEE" && new Date(i.evenementDateFin) < now
      );
      inscriptionsAnnulees = inscriptions.filter((i) => i.statut === "ANNULEE");

      // Mettre à jour les compteurs
      document.getElementById("countActives").textContent =
        inscriptionsActives.length;
      document.getElementById("countPassees").textContent =
        inscriptionsPassees.length;
      document.getElementById("countAnnulees").textContent =
        inscriptionsAnnulees.length;

      // Afficher les inscriptions
      displayInscriptions("actives", inscriptionsActives);
      displayInscriptions("passees", inscriptionsPassees);
      displayInscriptions("annulees", inscriptionsAnnulees);

      // Mettre à jour les statistiques
      updateStatistiquesFromInscriptions(
        inscriptionsActives.length,
        inscriptionsPassees.length
      );
    }
  } catch (error) {
    console.error("Erreur loadInscriptions:", error);
  }
}

// ========== Mise à jour statistiques depuis inscriptions ==========
function updateStatistiquesFromInscriptions(actives, passees) {
  document.getElementById("statInscriptionsActives").textContent = actives;
  document.getElementById("statEvenementsParticipes").textContent = passees;
}

// ========== Affichage Inscriptions ==========
function displayInscriptions(type, inscriptions) {
  const containerId =
    type === "actives"
      ? "listInscriptionsActives"
      : type === "passees"
      ? "listInscriptionsPassees"
      : "listInscriptionsAnnulees";

  const container = document.getElementById(containerId);

  if (!inscriptions || inscriptions.length === 0) {
    container.innerHTML = `
            <div class="col-12 text-center py-5">
                <i class="bi bi-inbox text-muted" style="font-size: 3rem;"></i>
                <p class="text-muted mt-3">Aucune inscription ${type}</p>
            </div>
        `;
    return;
  }

  container.innerHTML = inscriptions
    .map((inscription) => createInscriptionCard(inscription, type))
    .join("");

  // Attacher les listeners
  attachInscriptionListeners();
}

// ========== Création Carte Inscription ==========
function createInscriptionCard(inscription, type) {
  const dateInscription = new Date(inscription.dateInscription);
  const dateEvent = new Date(inscription.evenementDateDebut);
  const dateEventFin = new Date(inscription.evenementDateFin);
  const now = new Date();

  const statutBadges = {
    ACCEPTEE: "badge-status-ACCEPTEE",
    EN_ATTENTE: "badge-status-EN_ATTENTE",
    ANNULEA: "badge-status-ANNULEA",
  };

  const canCancel =
    type === "actives" && inscription.statut === "ACCEPTEE" && dateEvent > now;

  // Vérifier si l'événement est dans la fenêtre de 7 jours post-événement
  const limiteSeptJours = new Date(dateEventFin);
  limiteSeptJours.setDate(limiteSeptJours.getDate() + 7);

  const isInPostEventWindow =
    type === "passees" &&
    inscription.statut === "ACCEPTEE" &&
    dateEventFin < now &&
    now < limiteSeptJours;

  return `
        <div class="col-12">
            <div class="inscription-card">
                <div class="inscription-card-header">
                    <div>
                        <h5 class="inscription-card-title">${escapeHtml(
                          inscription.evenementTitre
                        )}</h5>
                        <span class="badge ${
                          statutBadges[inscription.statut]
                        }">${inscription.statut}</span>
                        ${
                          isInPostEventWindow
                            ? '<span class="badge bg-info ms-2">Fenêtre post-événement</span>'
                            : ""
                        }
                    </div>
                    <div class="text-end">
                        <small class="text-muted">Inscrit le ${dateInscription.toLocaleDateString(
                          "fr-FR"
                        )}</small>
                    </div>
                </div>
                
                <div class="inscription-card-info">
                    <div class="inscription-info-item">
                        <i class="bi bi-calendar3"></i>
                        ${dateEvent.toLocaleDateString(
                          "fr-FR"
                        )} à ${dateEvent.toLocaleTimeString("fr-FR", {
    hour: "2-digit",
    minute: "2-digit",
  })}
                    </div>
                    <div class="inscription-info-item">
                        <i class="bi bi-geo-alt"></i>
                        ${escapeHtml(inscription.evenementLieu)}
                    </div>
                    <div class="inscription-info-item">
                        <i class="bi bi-ticket"></i>
                        ${inscription.typeBillet} × ${inscription.quantite}
                    </div>
                </div>
                
                <div class="d-flex gap-2 flex-wrap">
                    <button class="btn btn-outline-primary btn-sm btn-voir-details" 
                            data-event-id="${inscription.evenementId}">
                        <i class="bi bi-info-circle me-1"></i>Voir détails
                    </button>
                    ${
                      canCancel
                        ? `
                        <button class="btn btn-outline-danger btn-sm btn-annuler-inscription" 
                                data-inscription-id="${inscription.inscriptionId}">
                            <i class="bi bi-x-circle me-1"></i>Annuler
                        </button>
                    `
                        : ""
                    }
                </div>
            </div>
        </div>
    `;
}

// ========== Listeners Inscriptions ==========
function attachInscriptionListeners() {
  // Boutons voir détails
  document.querySelectorAll(".btn-voir-details").forEach((btn) => {
    btn.addEventListener("click", function () {
      const eventId = this.dataset.eventId;
      openEventDetailsModal(eventId);
    });
  });

  // Boutons annuler
  document.querySelectorAll(".btn-annuler-inscription").forEach((btn) => {
    btn.addEventListener("click", function () {
      const inscriptionId = this.dataset.inscriptionId;
      confirmCancelInscription(inscriptionId);
    });
  });
}

// ========== Filtres ==========
function initFilters() {
  const searchInput = document.getElementById("searchEvents");
  const categorieSelect = document.getElementById("filterCategorie");
  const dateInput = document.getElementById("filterDate");
  const clearBtn = document.getElementById("btnClearFilters");

  // Recherche avec debounce
  let searchTimeout;
  searchInput?.addEventListener("input", function () {
    clearTimeout(searchTimeout);
    searchTimeout = setTimeout(() => applyFilters(), 300);
  });

  categorieSelect?.addEventListener("change", applyFilters);
  dateInput?.addEventListener("change", applyFilters);

  clearBtn?.addEventListener("click", function () {
    searchInput.value = "";
    categorieSelect.value = "";
    dateInput.value = "";
    applyFilters();
  });
}

function applyFilters() {
  if (!dashboardData || !dashboardData.evenementsDisponibles) return;

  const searchTerm =
    document.getElementById("searchEvents")?.value.toLowerCase() || "";
  const categorie = document.getElementById("filterCategorie")?.value || "";
  const date = document.getElementById("filterDate")?.value || "";

  let filtered = dashboardData.evenementsDisponibles;

  // Filtre recherche
  if (searchTerm) {
    filtered = filtered.filter(
      (event) =>
        event.titre.toLowerCase().includes(searchTerm) ||
        event.lieu.toLowerCase().includes(searchTerm) ||
        (event.description &&
          event.description.toLowerCase().includes(searchTerm))
    );
  }

  // Filtre catégorie
  if (categorie) {
    filtered = filtered.filter(
      (event) => event.categories && event.categories.includes(categorie)
    );
  }

  // Filtre date
  if (date) {
    filtered = filtered.filter((event) => {
      const eventDate = new Date(event.dateDebut).toISOString().split("T")[0];
      return eventDate === date;
    });
  }

  displayEvents(filtered);
}

// ========== Chargement Catégories ==========
async function loadCategories() {
  // Extraire les catégories uniques des événements
  if (!dashboardData || !dashboardData.evenementsDisponibles) return;

  const categories = new Set();
  dashboardData.evenementsDisponibles.forEach((event) => {
    if (event.categories) {
      event.categories.forEach((cat) => categories.add(cat));
    }
  });

  const select = document.getElementById("filterCategorie");
  if (select) {
    Array.from(categories)
      .sort()
      .forEach((cat) => {
        const option = document.createElement("option");
        option.value = cat;
        option.textContent = cat;
        select.appendChild(option);
      });
  }
}

// ========== Event Listeners Globaux ==========
function initEventListeners() {
  // Bouton refresh
  document
    .getElementById("btnRefreshEvents")
    ?.addEventListener("click", function () {
      loadDashboard();
    });
}

// ========== Utilitaires ==========
function showLoading(elementId) {
  const element = document.getElementById(elementId);
  if (element) element.classList.remove("d-none");
}

function hideLoading(elementId) {
  const element = document.getElementById(elementId);
  if (element) element.classList.add("d-none");
}

function showToast(message, type = "info") {
  const toast = document.getElementById("toastNotification");
  const toastBody = document.getElementById("toastMessage");

  if (toast && toastBody) {
    toastBody.textContent = message;

    // Changer la couleur selon le type
    toast.classList.remove("bg-success", "bg-danger", "bg-warning", "bg-info");
    if (type === "success") toast.classList.add("bg-success", "text-white");
    else if (type === "error") toast.classList.add("bg-danger", "text-white");
    else if (type === "warning") toast.classList.add("bg-warning");

    const bsToast = new bootstrap.Toast(toast);
    bsToast.show();
  }
}

function escapeHtml(text) {
  if (!text) return "";
  const div = document.createElement("div");
  div.textContent = text;
  return div.innerHTML;
}

// ========== Exports pour autres modules ==========
window.dashboardAPI = {
  getDashboardData: () => dashboardData,
  getInscriptionsActives: () => inscriptionsActives,
  getInscriptionsPassees: () => inscriptionsPassees,
  getInscriptionsAnnulees: () => inscriptionsAnnulees,
  refreshDashboard: loadDashboard,
  showToast: showToast,
  navigateToSection: navigateToSection,
   incrementCommentaires: () => incrementStatistic("nombreCommentaires"),
   incrementEvaluations: () => incrementStatistic("nombreEvaluations"),
};

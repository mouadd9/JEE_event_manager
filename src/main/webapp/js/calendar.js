/**
 * Gestion du calendrier interactif pour le dashboard participant
 * Affiche tous les événements auxquels le participant est inscrit
 */

let calendar = null;
let calendarEvents = [];

// ========== Initialisation ==========
document.addEventListener("DOMContentLoaded", function () {
  console.log("Calendar module initialized");
  
  // Initialiser le calendrier quand la section est affichée
  const calendarSection = document.getElementById("section-calendrier");
  if (calendarSection) {
    // Vérifier si la section est déjà visible au chargement
    const isVisible = !calendarSection.classList.contains("d-none");
    if (isVisible && !calendar) {
      // Petit délai pour s'assurer que FullCalendar est chargé
      setTimeout(() => {
        initCalendar();
      }, 100);
    }
    
    // Observer les changements de visibilité
    const observer = new MutationObserver(function (mutations) {
      mutations.forEach(function (mutation) {
        if (mutation.type === "attributes" && mutation.attributeName === "class") {
          const isVisible = !calendarSection.classList.contains("d-none");
          if (isVisible && !calendar) {
            // Petit délai pour s'assurer que FullCalendar est chargé
            setTimeout(() => {
              initCalendar();
            }, 100);
          }
        }
      });
    });
    
    observer.observe(calendarSection, {
      attributes: true,
      attributeFilter: ["class"]
    });
  }
  
  // Bouton refresh
  const btnRefresh = document.getElementById("btnRefreshCalendar");
  if (btnRefresh) {
    btnRefresh.addEventListener("click", function () {
      refreshCalendar();
    });
  }
  
  // Écouter les mises à jour d'inscriptions
  document.addEventListener("inscriptionUpdated", function (event) {
    console.log("Inscription mise à jour, rafraîchissement du calendrier...");
    refreshCalendar();
  });
});

// ========== Initialisation du calendrier ==========
function initCalendar() {
  const calendarEl = document.getElementById("calendar");
  if (!calendarEl) {
    console.error("Élément calendrier non trouvé");
    return;
  }

  // Charger les événements depuis les données du serveur
  loadCalendarEvents();

  // Configuration FullCalendar
  calendar = new FullCalendar.Calendar(calendarEl, {
    initialView: "dayGridMonth",
    locale: "fr",
    headerToolbar: {
      left: "prev,next today",
      center: "title",
      right: "dayGridMonth,timeGridWeek,timeGridDay,listWeek"
    },
    buttonText: {
      today: "Aujourd'hui",
      month: "Mois",
      week: "Semaine",
      day: "Jour",
      list: "Liste"
    },
    firstDay: 1, // Lundi
    height: "auto",
    events: calendarEvents,
    eventDisplay: "block",
    eventTimeFormat: {
      hour: "2-digit",
      minute: "2-digit",
      hour12: false
    },
    eventClick: function (info) {
      handleEventClick(info);
    },
    eventDidMount: function (info) {
      customizeEventStyle(info);
    },
    eventContent: function (info) {
      return customizeEventContent(info);
    },
    datesSet: function (dateInfo) {
      // Optionnel: charger plus d'événements si nécessaire
      console.log("Calendrier mis à jour:", dateInfo);
    }
  });

  calendar.render();
  console.log("Calendrier initialisé avec", calendarEvents.length, "événements");
}

// ========== Chargement des événements ==========
function loadCalendarEvents() {
  calendarEvents = [];

  // Récupérer les inscriptions depuis serverData
  if (window.serverData && window.serverData.inscriptions) {
    const inscriptions = window.serverData.inscriptions;
    
    inscriptions.forEach(function (inscription) {
      if (shouldDisplayInCalendar(inscription)) {
        const event = buildCalendarEvent(inscription);
        if (event) {
          calendarEvents.push(event);
        }
      }
    });
  }

  // Si pas de données dans serverData, charger depuis l'API
  if (calendarEvents.length === 0) {
    loadCalendarEventsFromAPI();
  }
}

// ========== Chargement depuis l'API ==========
async function loadCalendarEventsFromAPI() {
  try {
    const response = await fetch(`${API_BASE}/participant/inscriptions`, {
      method: "GET",
      credentials: "include",
      headers: {
        Accept: "application/json"
      }
    });

    if (!response.ok) {
      console.error("Erreur chargement inscriptions:", response.status);
      return;
    }

    const result = await response.json();

    if (result.success && result.data) {
      calendarEvents = [];
      
      result.data.forEach(function (inscription) {
        if (shouldDisplayInCalendar(inscription)) {
          const event = buildCalendarEvent(inscription);
          if (event) {
            calendarEvents.push(event);
          }
        }
      });

      // Mettre à jour le calendrier si déjà initialisé
      if (calendar) {
        calendar.removeAllEvents();
        calendar.addEventSource(calendarEvents);
        calendar.refetchEvents();
      }
    }
  } catch (error) {
    console.error("Erreur loadCalendarEventsFromAPI:", error);
  }
}

// ========== Couleur selon le statut ==========
function getEventColor(statut, isPast = false) {
  if (isPast) {
    return "#dc3545"; // Rouge pour les événements passés
  }

  const colors = {
    ACCEPTEE: "#10b981", // Vert
    EN_ATTENTE: "#ffc107", // Jaune/Orange
    ANNULEE: "#6c757d", // Gris
    REFUSEE: "#dc3545" // Rouge
  };
  return colors[statut] || "#6c757d";
}

function getEventTextColor(isPast = false) {
  return isPast ? "#ffffff" : "#ffffff";
}

function shouldDisplayInCalendar(inscription) {
  return (
    (inscription.statut === "ACCEPTEE" ||
      inscription.statut === "EN_ATTENTE") &&
    Boolean(inscription.evenementDateDebut)
  );
}

function buildCalendarEvent(inscription) {
  if (!inscription.evenementDateDebut) return null;

  const startDate = new Date(inscription.evenementDateDebut);
  const endDate = inscription.evenementDateFin
    ? new Date(inscription.evenementDateFin)
    : null;
  const now = new Date();
  const isPast = endDate ? endDate < now : startDate < now;

  const color = getEventColor(inscription.statut, isPast);

  return {
    id: inscription.evenementId,
    title: inscription.evenementTitre || "Événement",
    start: inscription.evenementDateDebut,
    end: inscription.evenementDateFin || null,
    allDay: false,
    extendedProps: {
      inscriptionId: inscription.inscriptionId,
      statut: inscription.statut,
      lieu: inscription.evenementLieu || "",
      typeBillet: inscription.typeBillet || "STANDARD",
      quantite: inscription.quantite || 1,
      description: inscription.evenementDescription || "",
      isPast,
    },
    backgroundColor: color,
    borderColor: color,
    textColor: getEventTextColor(isPast),
  };
}

// ========== Personnalisation du style des événements ==========
function customizeEventStyle(info) {
  const statut = info.event.extendedProps.statut;
  const isPast = info.event.extendedProps.isPast;
  
  // Ajouter une classe CSS selon le statut
  if (statut === "ACCEPTEE") {
    info.el.classList.add("fc-event-accepted");
  } else if (statut === "EN_ATTENTE") {
    info.el.classList.add("fc-event-pending");
  } else if (statut === "ANNULEE") {
    info.el.classList.add("fc-event-cancelled");
  }

  if (isPast) {
    info.el.classList.add("fc-event-past");
  }
  
  // Tooltip avec informations supplémentaires
  const title = info.event.title;
  const lieu = info.event.extendedProps.lieu || "Lieu non spécifié";
  const typeBillet = info.event.extendedProps.typeBillet || "STANDARD";
  const quantite = info.event.extendedProps.quantite || 1;
  
  info.el.setAttribute(
    "title",
    `${title}\nLieu: ${lieu}\nType: ${typeBillet} (${quantite} place${quantite > 1 ? "s" : ""})`
  );
}

// ========== Personnalisation du contenu des événements ==========
function customizeEventContent(info) {
  const timeText = info.timeText;
  const title = info.event.title;
  const statut = info.event.extendedProps.statut;
  const isPast = info.event.extendedProps.isPast;
  
  // Icône selon le statut ou si passé
  let icon = "";
  if (isPast) {
    icon = "⌛";
  } else if (statut === "ACCEPTEE") {
    icon = "✓";
  } else if (statut === "EN_ATTENTE") {
    icon = "⏳";
  }
  
  return {
    html: `
      <div class="fc-event-main-frame">
        <div class="fc-event-time">${timeText}</div>
        <div class="fc-event-title-container">
          <div class="fc-event-title">${icon ? icon + " " : ""}${title}</div>
        </div>
      </div>
    `
  };
}

// ========== Gestion du clic sur un événement ==========
function handleEventClick(info) {
  const event = info.event;
  const evenementId = event.id;
  
  // Ouvrir les détails de l'événement
  if (window.dashboardAPI && window.dashboardAPI.openEventDetailsModal) {
    window.dashboardAPI.openEventDetailsModal(evenementId);
  } else if (window.openEventDetailsModal) {
    window.openEventDetailsModal(evenementId);
  } else {
    // Fallback: rediriger vers la page de détails
    window.location.href = `${window.location.origin}/jee-event-manager/event-details?id=${evenementId}`;
  }
  
  info.jsEvent.preventDefault();
}

// ========== Rafraîchir le calendrier ==========
async function refreshCalendar() {
  console.log("Rafraîchissement du calendrier...");
  
  // Recharger les événements depuis l'API
  await loadCalendarEventsFromAPI();
  
  // Mettre à jour le calendrier
  if (calendar) {
    calendar.refetchEvents();
  }
  
  // Afficher une notification
  if (window.dashboardAPI && window.dashboardAPI.showToast) {
    window.dashboardAPI.showToast("Calendrier mis à jour", "success");
  }
}


// Exposer les fonctions publiques
window.calendarAPI = {
  refresh: refreshCalendar,
  getEvents: function () {
    return calendarEvents;
  },
  addEvent: function (event) {
    if (calendar) {
      calendar.addEvent(event);
      calendarEvents.push(event);
    }
  },
  removeEvent: function (eventId) {
    if (calendar) {
      const event = calendar.getEventById(eventId);
      if (event) {
        event.remove();
      }
      calendarEvents = calendarEvents.filter(e => e.id !== eventId);
    }
  }
};


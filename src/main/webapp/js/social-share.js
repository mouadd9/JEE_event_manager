/**
 * Gestion du partage social et des invitations d'événements
 */

// Variables globales
let currentEventId = null;
let currentEventTitle = null;
let currentEventUrl = null;
let referralCode = null;

// ========== Initialisation ==========
document.addEventListener("DOMContentLoaded", function () {
  // Récupérer les informations de l'événement depuis la page
  const urlParams = new URLSearchParams(window.location.search);
  currentEventId = urlParams.get("id");
  
  // Récupérer le titre de l'événement depuis la page
  const eventTitleElement = document.querySelector(".event-title");
  if (eventTitleElement) {
    currentEventTitle = eventTitleElement.textContent.trim();
  }
  
  // Construire l'URL complète de l'événement
  currentEventUrl = window.location.href;
  
  console.log("Social Share initialized:", {
    eventId: currentEventId,
    title: currentEventTitle,
    url: currentEventUrl
  });
});

// ========== Partage sur Facebook ==========
function shareOnFacebook() {
  if (!currentEventUrl || !currentEventTitle) {
    showNotification("Erreur: Informations de l'événement manquantes", "error");
    return;
  }
  
  const shareUrl = `https://www.facebook.com/sharer/sharer.php?u=${encodeURIComponent(currentEventUrl)}`;
  openShareWindow(shareUrl, "Partage Facebook");
}

// ========== Partage sur Twitter ==========
function shareOnTwitter() {
  if (!currentEventUrl || !currentEventTitle) {
    showNotification("Erreur: Informations de l'événement manquantes", "error");
    return;
  }
  
  const text = `Découvrez cet événement: ${currentEventTitle}`;
  const shareUrl = `https://twitter.com/intent/tweet?text=${encodeURIComponent(text)}&url=${encodeURIComponent(currentEventUrl)}`;
  openShareWindow(shareUrl, "Partage Twitter");
}

// ========== Partage sur LinkedIn ==========
function shareOnLinkedIn() {
  if (!currentEventUrl || !currentEventTitle) {
    showNotification("Erreur: Informations de l'événement manquantes", "error");
    return;
  }
  
  const shareUrl = `https://www.linkedin.com/sharing/share-offsite/?url=${encodeURIComponent(currentEventUrl)}`;
  openShareWindow(shareUrl, "Partage LinkedIn");
}

// ========== Partage sur WhatsApp ==========
function shareOnWhatsApp() {
  if (!currentEventUrl || !currentEventTitle) {
    showNotification("Erreur: Informations de l'événement manquantes", "error");
    return;
  }
  
  const text = `Découvrez cet événement: ${currentEventTitle}\n${currentEventUrl}`;
  
  // Détecter si on est sur mobile ou desktop
  const isMobile = /Android|webOS|iPhone|iPad|iPod|BlackBerry|IEMobile|Opera Mini/i.test(navigator.userAgent);
  
  if (isMobile) {
    // Sur mobile, utiliser le protocole whatsapp://
    const shareUrl = `whatsapp://send?text=${encodeURIComponent(text)}`;
    window.location.href = shareUrl;
  } else {
    // Sur desktop, utiliser WhatsApp Web
    const shareUrl = `https://web.whatsapp.com/send?text=${encodeURIComponent(text)}`;
    openShareWindow(shareUrl, "Partage WhatsApp");
  }
}

// ========== Ouvrir une fenêtre de partage ==========
function openShareWindow(url, title) {
  const width = 600;
  const height = 400;
  const left = (screen.width - width) / 2;
  const top = (screen.height - height) / 2;
  
  window.open(
    url,
    title,
    `width=${width},height=${height},left=${left},top=${top},toolbar=0,location=0,menubar=0,scrollbars=1,resizable=1`
  );
}

// ========== Copier le lien d'invitation ==========
function copyInviteLink() {
  const inviteLinkInput = document.getElementById("inviteLink");
  
  if (!inviteLinkInput) {
    showNotification("Erreur: Lien d'invitation introuvable", "error");
    return;
  }
  
  // Sélectionner le texte
  inviteLinkInput.select();
  inviteLinkInput.setSelectionRange(0, 99999); // Pour mobile
  
  try {
    // Copier dans le presse-papiers
    document.execCommand("copy");
    
    // Utiliser l'API Clipboard moderne si disponible
    if (navigator.clipboard) {
      navigator.clipboard.writeText(inviteLinkInput.value);
    }
    
    showNotification("Lien copié dans le presse-papiers !", "success");
    
    // Réinitialiser la sélection
    inviteLinkInput.setSelectionRange(0, 0);
  } catch (err) {
    console.error("Erreur lors de la copie:", err);
    showNotification("Erreur lors de la copie du lien", "error");
  }
}

// ========== Générer un code de parrainage ==========
async function generateReferralCode() {
  if (!currentEventId) {
    showNotification("Erreur: ID de l'événement manquant", "error");
    return;
  }
  
  const btnGenerate = document.getElementById("btnGenerateReferral");
  if (btnGenerate) {
    btnGenerate.disabled = true;
    btnGenerate.innerHTML = '<span class="spinner-border spinner-border-sm me-2"></span>Génération...';
  }
  
  try {
    // Appeler l'API pour générer un code de parrainage
    const response = await fetch(`${getApiBase()}/api/referral/generate`, {
      method: "POST",
      credentials: "include",
      headers: {
        "Content-Type": "application/json",
        Accept: "application/json"
      },
      body: JSON.stringify({
        eventId: currentEventId
      })
    });
    
    if (response.ok) {
      const result = await response.json();
      
      if (result.success && result.data && result.data.referralCode) {
        referralCode = result.data.referralCode;
        displayReferralCode(referralCode);
        showNotification("Code de parrainage généré avec succès !", "success");
      } else {
        // Si l'API n'est pas disponible, générer un code localement
        referralCode = generateLocalReferralCode();
        displayReferralCode(referralCode);
        showNotification("Code de parrainage généré !", "success");
      }
    } else {
      // Si l'API n'est pas disponible, générer un code localement
      referralCode = generateLocalReferralCode();
      displayReferralCode(referralCode);
      showNotification("Code de parrainage généré !", "success");
    }
  } catch (error) {
    console.error("Erreur lors de la génération du code:", error);
    // Générer un code localement en cas d'erreur
    referralCode = generateLocalReferralCode();
    displayReferralCode(referralCode);
    showNotification("Code de parrainage généré !", "success");
  } finally {
    if (btnGenerate) {
      btnGenerate.disabled = false;
      btnGenerate.innerHTML = '<i class="fas fa-gift me-2"></i>Générer un code de parrainage';
    }
  }
}

// ========== Générer un code de parrainage local ==========
function generateLocalReferralCode() {
  // Générer un code unique basé sur l'ID de l'événement et un timestamp
  const timestamp = Date.now().toString(36).toUpperCase();
  const eventHash = currentEventId.toString().padStart(4, "0");
  const randomPart = Math.random().toString(36).substring(2, 6).toUpperCase();
  
  // Format: EVT-{eventId}-{timestamp}-{random}
  return `EVT-${eventHash}-${timestamp.substring(0, 4)}-${randomPart}`;
}

// ========== Afficher le code de parrainage ==========
function displayReferralCode(code) {
  const container = document.getElementById("referralCodeContainer");
  const display = document.getElementById("referralCodeDisplay");
  
  if (container && display) {
    display.textContent = code;
    container.style.display = "block";
    
    // Mettre à jour le lien d'invitation avec le code
    const inviteLinkInput = document.getElementById("inviteLink");
    if (inviteLinkInput) {
      const baseUrl = currentEventUrl.split("?")[0];
      const newUrl = `${baseUrl}?id=${currentEventId}&ref=${code}`;
      inviteLinkInput.value = newUrl;
    }
  }
}

// ========== Copier le code de parrainage ==========
function copyReferralCode() {
  if (!referralCode) {
    showNotification("Aucun code de parrainage généré", "warning");
    return;
  }
  
  try {
    // Utiliser l'API Clipboard moderne
    if (navigator.clipboard) {
      navigator.clipboard.writeText(referralCode);
      showNotification("Code de parrainage copié !", "success");
    } else {
      // Fallback pour les navigateurs plus anciens
      const textArea = document.createElement("textarea");
      textArea.value = referralCode;
      textArea.style.position = "fixed";
      textArea.style.opacity = "0";
      document.body.appendChild(textArea);
      textArea.select();
      document.execCommand("copy");
      document.body.removeChild(textArea);
      showNotification("Code de parrainage copié !", "success");
    }
  } catch (err) {
    console.error("Erreur lors de la copie:", err);
    showNotification("Erreur lors de la copie du code", "error");
  }
}

// ========== Vérifier si un code de parrainage est présent dans l'URL ==========
function checkReferralCodeInUrl() {
  const urlParams = new URLSearchParams(window.location.search);
  const refCode = urlParams.get("ref");
  
  if (refCode) {
    // Enregistrer le code dans le localStorage pour utilisation ultérieure
    if (currentEventId) {
      localStorage.setItem(`referral_${currentEventId}`, refCode);
    }
    
    // Afficher une notification que l'utilisateur a été invité
    showNotification(
      `🎉 Vous avez été invité avec le code de parrainage: ${refCode}`,
      "success"
    );
    
    // Optionnel: Afficher le code dans la section d'invitation
    if (document.getElementById("referralCodeContainer")) {
      referralCode = refCode;
      displayReferralCode(refCode);
    }
  }
}

// ========== Obtenir l'URL de base de l'API ==========
function getApiBase() {
  if (typeof API_BASE !== "undefined") {
    return API_BASE;
  }
  return window.location.origin + "/jee-event-manager";
}

// ========== Afficher une notification ==========
function showNotification(message, type = "info") {
  // Créer un élément de notification toast
  const toast = document.createElement("div");
  toast.className = `alert alert-${type === "error" ? "danger" : type === "success" ? "success" : "info"} alert-dismissible fade show position-fixed`;
  toast.style.cssText = "top: 20px; right: 20px; z-index: 9999; min-width: 300px;";
  toast.setAttribute("role", "alert");
  
  toast.innerHTML = `
    ${message}
    <button type="button" class="btn-close" data-bs-dismiss="alert" aria-label="Close"></button>
  `;
  
  document.body.appendChild(toast);
  
  // Supprimer automatiquement après 3 secondes
  setTimeout(() => {
    if (toast.parentNode) {
      toast.remove();
    }
  }, 3000);
}

// Vérifier le code de parrainage au chargement
document.addEventListener("DOMContentLoaded", function () {
  setTimeout(checkReferralCodeInUrl, 500);
});

// Exposer les fonctions globalement
window.shareOnFacebook = shareOnFacebook;
window.shareOnTwitter = shareOnTwitter;
window.shareOnLinkedIn = shareOnLinkedIn;
window.shareOnWhatsApp = shareOnWhatsApp;
window.copyInviteLink = copyInviteLink;
window.generateReferralCode = generateReferralCode;
window.copyReferralCode = copyReferralCode;


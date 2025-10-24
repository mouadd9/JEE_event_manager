<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fn" uri="jakarta.tags.functions" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<html>
<head>
  <title>Détails: <c:out value="${event.titre}"/></title>

  <link rel="stylesheet" href="https://unpkg.com/leaflet@1.9.4/dist/leaflet.css" integrity="sha256-p4NxAoJBhIIN+hmNHrzRCf9tD/miZyoHS5obTRR9BMY=" crossorigin=""/>
  <script src="https://unpkg.com/leaflet@1.9.4/dist/leaflet.js" integrity="sha256-20nQCchB9co0qIjJZRGuk2/Z9VM+kNiyxNV1lvTlZBo=" crossorigin=""></script>
  <script src='https://cdn.jsdelivr.net/npm/fullcalendar@6.1.14/index.global.min.js'></script>

  <meta name="viewport" content="width=device-width, initial-scale=1.0">
  <style>
    /* Professional Redesign CSS */
    :root {
      --color-bg: #1a1a1e;
      --color-bg-secondary: #25252a;
      --color-text: #dadada;
      --color-text-secondary: #9a9a9a;
      --color-border: #3a3a42;
      --color-primary: #3d8bfd;
      --color-primary-hover: #5a9eff;
      --color-green: #28a745;
      --color-orange: #fd7e14;
      --color-red: #dc3545;
      --color-yellow: #ffc107;
      --font-family-sans: -apple-system, BlinkMacSystemFont, "Segoe UI", Roboto, Helvetica, Arial, sans-serif, "Apple Color Emoji", "Segoe UI Emoji";
      --border-radius: 6px;
    }
    body {
      font-family: var(--font-family-sans);
      background-color: var(--color-bg);
      color: var(--color-text);
      margin: 0;
      padding: 0;
      line-height: 1.6;
    }

    /* Navigation */
    .nav {
      background-color: var(--color-bg-secondary);
      border-bottom: 1px solid var(--color-border);
      padding: 0 20px;
      display: flex;
      justify-content: space-between;
      align-items: center;
    }
    .nav-links {
      overflow: hidden;
    }
    .nav a {
      float: left;
      color: var(--color-text-secondary);
      text-align: center;
      padding: 18px 16px;
      text-decoration: none;
      font-size: 16px;
      font-weight: 500;
      transition: color 0.2s, border-bottom-color 0.2s;
      border-bottom: 3px solid transparent;
    }
    .nav a:hover {
      color: var(--color-text);
    }
    .nav a.active {
      color: var(--color-primary);
      border-bottom: 3px solid var(--color-primary);
    }
    .nav-user {
      color: var(--color-text);
      font-weight: 500;
      padding: 18px 0;
    }

    /* Main Content Area */
    .main-content {
      max-width: 900px;
      margin: 30px auto;
      padding: 0 20px;
    }

    /* Detail Header */
    .detail-header {
      border-bottom: 1px solid var(--color-border);
      padding-bottom: 20px;
      margin-bottom: 30px;
      display: flex;
      justify-content: space-between;
      align-items: center;
      flex-wrap: wrap;
      gap: 15px;
    }
    .detail-header h1 {
      margin: 0;
      color: var(--color-text);
      font-size: 28px;
      font-weight: 600;
      flex-grow: 1;
      display: flex;
      align-items: center;
      flex-wrap: wrap;
    }
    .status-badge {
      font-size: 14px;
      font-weight: 500;
      padding: 5px 10px;
      border-radius: 4px;
      margin-left: 15px;
      border: 1px solid;
      background-color: rgba(255,255,255, 0.05);
    }
    .status-badge.publie { color: var(--color-green); border-color: var(--color-green); }
    .status-badge.brouillon { color: var(--color-orange); border-color: var(--color-orange); }
    .status-badge.annule { color: var(--color-red); border-color: var(--color-red); }

    .detail-actions {
      white-space: nowrap;
    }
    .detail-actions form {
      display: inline-block !important;
      margin-left: 8px;
    }
    .detail-actions a, .detail-actions button {
      text-decoration: none;
      padding: 9px 15px;
      border: 1px solid var(--color-border);
      border-radius: var(--border-radius);
      cursor: pointer;
      color: var(--color-text);
      font-size: 14px;
      font-weight: 500;
      transition: all 0.2s;
      background-color: var(--color-bg-secondary);
    }
    .detail-actions a:hover, .detail-actions button:hover {
      border-color: var(--color-text-secondary);
    }
    /* Button Colors */
    .btn-edit { background-color: var(--color-primary); color: white; border-color: var(--color-primary); }
    .btn-edit:hover { background-color: var(--color-primary-hover); border-color: var(--color-primary-hover); }
    .btn-publish { color: var(--color-green); }
    .btn-publish:hover { border-color: var(--color-green); background-color: rgba(40, 167, 69, 0.1); }
    .btn-unpublish { color: var(--color-yellow); }
    .btn-unpublish:hover { border-color: var(--color-yellow); background-color: rgba(255, 193, 7, 0.1); }
    .btn-cancel { color: var(--color-orange); }
    .btn-cancel:hover { border-color: var(--color-orange); background-color: rgba(253, 126, 20, 0.1); }
    .btn-delete { color: var(--color-red); }
    .btn-delete:hover { border-color: var(--color-red); background-color: rgba(220, 53, 69, 0.1); }

    /* Detail Body */
    .detail-body {
      line-height: 1.7;
    }
    .detail-body h2 {
      font-size: 22px;
      font-weight: 600;
      border-bottom: 1px solid var(--color-border);
      padding-bottom: 10px;
      margin-top: 40px;
      margin-bottom: 20px;
      color: var(--color-text);
    }
    .detail-body p {
      margin-bottom: 15px;
      color: var(--color-text-secondary);
      font-size: 16px;
    }
    .detail-body p.description {
      color: var(--color-text);
      font-size: 17px;
      white-space: pre-wrap; /* Preserves line breaks in description */
    }
    .detail-body strong {
      color: var(--color-text);
      font-weight: 500;
    }

    /* Map and Calendar */
    #map, #calendar {
      height: 450px;
      width: 100%;
      border-radius: var(--border-radius);
      background-color: var(--color-bg-secondary);
      border: 1px solid var(--color-border);
    }
    #calendar {
      height: 600px;
      padding: 20px;
      box-sizing: border-box;
      background-color: var(--color-bg-secondary);
    }
    /* FullCalendar Dark Theme */
    :root {
      --fc-border-color: var(--color-border);
      --fc-page-bg-color: var(--color-bg-secondary);
      --fc-neutral-bg-color: var(--color-bg-secondary);
      --fc-list-even-row-bg-color: var(--color-bg-secondary);
      --fc-list-odd-row-bg-color: var(--color-bg);
      --fc-daygrid-event-dot-width: 8px;
      --fc-list-event-dot-width: 8px;
      --fc-event-bg-color: var(--color-primary);
      --fc-event-border-color: var(--color-primary);
      --fc-event-text-color: #fff;
    }
    .fc {
      color: var(--color-text);
    }
    .fc .fc-toolbar-title {
      color: var(--color-text);
      font-size: 1.5em;
    }
    .fc .fc-button-primary {
      background-color: var(--color-bg-secondary);
      border-color: var(--color-border);
      color: var(--color-text);
    }
    .fc .fc-button-primary:hover {
      background-color: var(--color-border);
    }
    .fc .fc-button-primary:not(:disabled).fc-button-active,
    .fc .fc-button-primary:not(:disabled):active {
      background-color: var(--color-border);
      border-color: var(--color-border);
    }
    .fc .fc-daygrid-day-number {
      color: var(--color-text-secondary);
    }
    .fc .fc-day-today {
      background-color: rgba(61, 139, 253, 0.1) !important;
    }
    .fc-theme-standard td, .fc-theme-standard th {
      border-color: var(--color-border);
    }
    .fc .fc-col-header-cell-cushion {
      color: var(--color-text);
      font-weight: 500;
    }

  </style>
</head>
<body>

<div class="nav">
  <div class="nav-links">
    <a href="${pageContext.request.contextPath}/organizer/dashboard">Tableau de Bord</a>
    <a href="${pageContext.request.contextPath}/organizer/events/new">Nouvel Événement</a>
  </div>
  <div class="nav-user">
    <c:out value="${organizer.firstName}"/> <c:out value="${organizer.lastName}"/>
  </div>
</div>

<div class="main-content">

  <div class="detail-header">
    <h1>
      <c:out value="${event.titre}"/>
      <c:choose>
        <c:when test="${event.statut == 'PUBLIE'}">
          <span class="status-badge publie">Publié</span>
        </c:when>
        <c:when test="${event.statut == 'BROUILLON'}">
          <span class="status-badge brouillon">Non Publié</span>
        </c:when>
        <c:when test="${event.statut == 'ANNULE'}">
          <span class="status-badge annule">Annulé</span>
        </c:when>
      </c:choose>
    </h1>

    <div class="detail-actions">
      <a href="${pageContext.request.contextPath}/organizer/events/edit?id=${event.id}" class="btn-edit">Modifier</a>

      <c:choose>
        <c:when test="${event.statut == 'PUBLIE'}">
          <form action="${pageContext.request.contextPath}/organizer/events" method="POST">
            <input type="hidden" name="action" value="unpublish">
            <input type="hidden" name="eventId" value="${event.id}">
            <button type="submit" class="btn-unpublish">Dépublier</button>
          </form>
        </c:when>
        <c:when test="${event.statut == 'BROUILLON'}">
          <form action="${pageContext.request.contextPath}/organizer/events" method="POST">
            <input type="hidden" name="action" value="publish">
            <input type="hidden" name="eventId" value="${event.id}">
            <button type="submit" class="btn-publish">Publier</button>
          </form>
        </c:when>
      </c:choose>

      <c:if test="${event.statut != 'ANNULE'}">
        <form action="${pageContext.request.contextPath}/organizer/events" method="POST">
          <input type="hidden" name="action" value="cancel">
          <input type="hidden" name="eventId" value="${event.id}">
          <button type="submit" class="btn-cancel">Annuler</button>
        </form>
      </c:if>

      <form action="${pageContext.request.contextPath}/organizer/events" method="POST" onsubmit="return confirm('Êtes-vous sûr de vouloir supprimer cet événement ?');">
        <input type="hidden" name="action" value="delete">
        <input type="hidden" name="eventId" value="${event.id}">
        <button type="submit" class="btn-delete">Supprimer</button>
      </form>
    </div>
  </div>

  <div class="detail-body">

    <p>
      <strong>Période:</strong>
      De <fmt:formatDate value="${event.dateDebutAsDate}" pattern="dd/MM/yyyy 'à' HH:mm"/>
      à <fmt:formatDate value="${event.dateFinAsDate}" pattern="dd/MM/yyyy 'à' HH:mm"/>
    </p>
    <p><strong>Lieu:</strong> <c:out value="${event.lieu}"/></p>

    <p><strong>Participants:</strong> 0</p>
    <h2>Description</h2>
    <p class="description"><c:out value="${event.description}"/></p>
    <h2>Localisation</h2>
    <div id="map"></div>

    <h2>Commentaires</h2>
    <div id="calendar"></div>

  </div>
</div>

<script>
  // --- Map Display ---
  <c:if test="${event.latitude != null && event.longitude != null}">
  const lat = ${event.latitude};
  const lon = ${event.longitude};
  const map = L.map('map').setView([lat, lon], 15);
  L.tileLayer('https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png', {
    maxZoom: 19,
    attribution: '© OpenStreetMap'
  }).addTo(map);
  L.marker([lat, lon]).addTo(map)
          .bindPopup('<c:out value="${event.titre}"/>')
          .openPopup();
  </c:if>
  <c:if test="${event.latitude == null || event.longitude == null}">
  document.getElementById('map').innerHTML = "<p style='padding: 20px; color: var(--color-text-secondary);'>Aucune localisation GPS n'a été fournie pour cet événement.</p>";
  </c:if>

</script>

</body>
</html>
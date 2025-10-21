<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>

<html>
<head>
  <title>Modifier l'Événement</title>

  <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/leaflet/1.9.4/leaflet.min.css" crossorigin=""/>

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
    .container {
      max-width: 900px;
      margin: 0 auto;
      padding: 20px;
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

    /* Form Container */
    .form-container {
      background-color: var(--color-bg-secondary);
      padding: 30px;
      border-radius: var(--border-radius);
      border: 1px solid var(--color-border);
      margin-top: 30px;
    }
    h1 {
      color: var(--color-text);
      border-bottom: 1px solid var(--color-border);
      padding-bottom: 15px;
      margin-top: 0;
      font-weight: 600;
    }
    .form-group {
      margin-bottom: 25px;
    }
    .form-group label {
      display: block;
      margin-bottom: 8px;
      font-weight: 500;
      color: var(--color-text-secondary);
    }
    .form-group input[type="text"],
    .form-group textarea {
      width: 100%;
      padding: 12px;
      box-sizing: border-box;
      background-color: var(--color-bg);
      border: 1px solid var(--color-border);
      color: var(--color-text);
      border-radius: var(--border-radius);
      font-size: 15px;
    }
    .form-group textarea {
      min-height: 150px;
      resize: vertical;
    }
    .form-group input:focus, .form-group textarea:focus {
      outline: none;
      border-color: var(--color-primary);
      box-shadow: 0 0 0 3px rgba(61, 139, 253, 0.3);
    }

    /* JS Map */
    #map-select {
      height: 400px;
      width: 100%;
      border-radius: var(--border-radius);
      background-color: var(--color-bg);
      border: 1px solid var(--color-border);
    }

    /* Date Time Inputs */
    .datetime-row {
      display: grid;
      grid-template-columns: 1fr 1fr;
      gap: 15px;
    }
    .datetime-input {
      display: flex;
      flex-direction: column;
    }
    .datetime-input label {
      margin-bottom: 8px;
      font-weight: 500;
      color: var(--color-text-secondary);
    }
    .datetime-input input[type="datetime-local"] {
      padding: 12px;
      background-color: var(--color-bg);
      border: 1px solid var(--color-border);
      color: var(--color-text);
      border-radius: var(--border-radius);
      font-size: 15px;
    }
    .datetime-input input[type="datetime-local"]:focus {
      outline: none;
      border-color: var(--color-primary);
      box-shadow: 0 0 0 3px rgba(61, 139, 253, 0.3);
    }

    /* Submit Button */
    .btn-submit {
      background-color: var(--color-primary);
      color: white;
      padding: 12px 20px;
      border: none;
      border-radius: var(--border-radius);
      cursor: pointer;
      font-size: 16px;
      font-weight: 500;
      transition: background-color 0.2s;
    }
    .btn-submit:hover {
      background-color: var(--color-primary-hover);
    }
  </style>
</head>
<body>

<div class="nav">
  <div class="nav-links">
    <a href="${pageContext.request.contextPath}/organizer/dashboard">Tableau de Bord</a>
    <a href="${pageContext.request.contextPath}/organizer/events/new" class="active">Nouvel Événement</a>
  </div>
  <div class="nav-user">
    <c:out value="${organizer.firstName}"/> <c:out value="${organizer.lastName}"/>
  </div>
</div>

<div class="container">
  <div class="form-container">
    <h1>Modifier l'Événement</h1>

    <form action="${pageContext.request.contextPath}/organizer/events" method="POST">
      <input type="hidden" name="action" value="update">
      <input type="hidden" name="id" value="${event.id}">

      <fmt:formatDate value="${event.dateDebutAsDate}" pattern="yyyy-MM-dd'T'HH:mm" var="debutStr"/>
      <fmt:formatDate value="${event.dateFinAsDate}" pattern="yyyy-MM-dd'T'HH:mm" var="finStr"/>

      <input type="hidden" id="dateDebut" name="dateDebut" value="${debutStr}">
      <input type="hidden" id="dateFin" name="dateFin" value="${finStr}">
      <input type="hidden" id="latitude" name="latitude" value="${event.latitude}">
      <input type="hidden" id="longitude" name="longitude" value="${event.longitude}">

      <div class="form-group">
        <label for="titre">Titre</label>
        <input type="text" id="titre" name="titre" value="<c:out value="${event.titre}"/>" required>
      </div>

      <div class="form-group">
        <label for="lieu">Nom du Lieu (ex: "Palais des Congrès")</label>
        <input type="text" id="lieu" name="lieu" value="<c:out value="${event.lieu}"/>">
      </div>

      <div class="form-group">
        <label for="map-select">Localisation (Cliquez sur la carte)</label>
        <div id="map-select"></div>
      </div>

      <div class="form-group">
        <label>Période de l'Événement</label>
        <div class="datetime-row">
          <div class="datetime-input">
            <label for="dateDebutInput">Date de Début</label>
            <input type="datetime-local" id="dateDebutInput" value="${debutStr}" required>
          </div>
          <div class="datetime-input">
            <label for="dateFinInput">Date de Fin</label>
            <input type="datetime-local" id="dateFinInput" value="${finStr}" required>
          </div>
        </div>
      </div>

      <div class="form-group">
        <label for="description">Description</label>
        <textarea id="description" name="description" rows="5"><c:out value="${event.description}"/></textarea>
      </div>

      <button type="submit" class="btn-submit">Mettre à Jour</button>
    </form>
  </div>
</div>

<script src="https://cdnjs.cloudflare.com/ajax/libs/leaflet/1.9.4/leaflet.min.js"></script>
<script>
  // Wait for window to fully load
  window.addEventListener('load', function() {

    // --- Date Time Inputs ---
    const debutInput = document.getElementById('dateDebut');
    const finInput = document.getElementById('dateFin');
    const dateDebutInput = document.getElementById('dateDebutInput');
    const dateFinInput = document.getElementById('dateFinInput');

    function formatForBackend(datetimeLocalValue) {
      if (!datetimeLocalValue) return "";
      return datetimeLocalValue;
    }

    // Link visible inputs to hidden inputs
    dateDebutInput.addEventListener('change', function() {
      debutInput.value = formatForBackend(this.value);
    });

    dateFinInput.addEventListener('change', function() {
      finInput.value = formatForBackend(this.value);
    });

    // --- Map (Leaflet) ---
    const latInput = document.getElementById('latitude');
    const lonInput = document.getElementById('longitude');

    // Use event's coordinates if they exist, otherwise default to Tétouan
    const startLat = parseFloat(latInput.value) || 35.5785;
    const startLon = parseFloat(lonInput.value) || -5.3684;
    const startZoom = (latInput.value && lonInput.value) ? 15 : 12;

    try {
      const map = L.map('map-select').setView([startLat, startLon], startZoom);

      L.tileLayer('https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png', {
        maxZoom: 19,
        attribution: '© OpenStreetMap'
      }).addTo(map);

      // Add the initial marker
      let marker = L.marker([startLat, startLon]).addTo(map);

      map.on('click', function(e) {
        const lat = e.latlng.lat;
        const lon = e.latlng.lng;

        if (marker) {
          marker.setLatLng(e.latlng);
        } else {
          marker = L.marker(e.latlng).addTo(map);
        }

        latInput.value = lat;
        lonInput.value = lon;
      });

      // Force map to resize
      setTimeout(function() {
        map.invalidateSize();
      }, 100);

    } catch (error) {
      console.error('Error initializing map:', error);
    }
  });
</script>

</body>
</html>
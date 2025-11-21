<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="jakarta.tags.core" prefix="c"%>
<!DOCTYPE html>
<html lang="fr">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Profil Admin - EventManager</title>
    
    <!-- Bootstrap CSS -->
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
    <!-- Font Awesome -->
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.0/css/all.min.css">
    <!-- Google Fonts -->
    <link href="https://fonts.googleapis.com/css2?family=Neue+Plak:wght@400;600;700&display=swap" rel="stylesheet">
    <%@ include file="/WEB-INF/jspf/theme-head.jspf" %>
    <!-- Theme CSS -->
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/theme.css">
    
    <style>
        body {
            font-family: 'Neue Plak', -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, sans-serif;
            background-color: #f8f9fa;
        }
        
        .admin-header {
            background: linear-gradient(135deg, #8c65a7 0%, #7d24bd 100%);
            color: white;
            padding: 2rem 0;
            margin-bottom: 2rem;
        }
        
        .profile-card {
            background: white;
            border-radius: 15px;
            padding: 2rem;
            box-shadow: 0 2px 10px rgba(0,0,0,0.1);
            margin-bottom: 2rem;
        }
        
        .profile-avatar {
            width: 120px;
            height: 120px;
            border-radius: 50%;
            background: linear-gradient(135deg, #8c65a7 0%, #7d24bd 100%);
            display: flex;
            align-items: center;
            justify-content: center;
            color: white;
            font-size: 3rem;
            margin: 0 auto 1.5rem;
        }
        
        .info-group {
            margin-bottom: 1.5rem;
            padding-bottom: 1.5rem;
            border-bottom: 1px solid #e9ecef;
        }
        
        .info-group:last-child {
            border-bottom: none;
            margin-bottom: 0;
            padding-bottom: 0;
        }
        
        .info-label {
            color: #6c757d;
            font-size: 0.85rem;
            text-transform: uppercase;
            letter-spacing: 0.5px;
            margin-bottom: 0.5rem;
        }
        
        .info-value {
            color: #2d1038;
            font-size: 1.1rem;
            font-weight: 500;
        }
        
        .badge-role {
            background: linear-gradient(135deg, #8c65a7 0%, #7d24bd 100%);
            padding: 0.5rem 1rem;
            border-radius: 20px;
            font-size: 0.9rem;
        }
        
        .nav-pills .nav-link {
            color: #6c757d;
            border-radius: 10px;
        }
        
        .nav-pills .nav-link.active {
            background: #8c65a7;
        }
        
        .form-label {
            color: #2d1038 !important;
            font-weight: 500;
        }
        
        .form-control {
            color: #2d1038 !important;
            background-color: #fff !important;
        }
        
        .form-control::placeholder {
            color: #6c757d !important;
            opacity: 0.7;
        }
        
        .form-control:focus {
            color: #2d1038 !important;
        }
        
        .btn-primary {
            background: linear-gradient(135deg, #8c65a7 0%, #7d24bd 100%);
            border: none;
            padding: 0.75rem 2rem;
            border-radius: 10px;
            font-weight: 600;
            transition: transform 0.3s;
        }
        
        .btn-primary:hover {
            transform: translateY(-2px);
            box-shadow: 0 5px 15px rgba(140, 101, 167, 0.3);
        }
        
        h4, h5 {
            color: #2d1038 !important;
        }
    </style>
</head>
<body>
    <!-- Admin Header -->
    <div class="admin-header">
        <div class="container">
            <div class="d-flex justify-content-between align-items-center">
                <div>
                    <h1><i class="fas fa-user-circle me-2"></i>Mon Profil</h1>
                    <p class="mb-0">Gérer vos informations personnelles</p>
                </div>
                <div>
                    <span class="me-3"><i class="fas fa-user me-2"></i>${sessionScope.userName}</span>
                    <a href="${pageContext.request.contextPath}/admin/profil" class="btn btn-outline-light me-2">
                        <i class="fas fa-user-circle me-2"></i>Profil
                    </a>
                    <a href="${pageContext.request.contextPath}/logout" class="btn btn-outline-light">
                        <i class="fas fa-sign-out-alt me-2"></i>Déconnexion
                    </a>
                </div>
            </div>
        </div>
    </div>
    
    <div class="container">
        <!-- Navigation Tabs -->
        <ul class="nav nav-pills mb-4">
            <li class="nav-item">
                <a class="nav-link" href="${pageContext.request.contextPath}/admin/dashboard">
                    <i class="fas fa-chart-line me-2"></i>Dashboard
                </a>
            </li>
            <li class="nav-item">
                <a class="nav-link" href="${pageContext.request.contextPath}/admin/users">
                    <i class="fas fa-users me-2"></i>Utilisateurs
                </a>
            </li>
            <li class="nav-item">
                <a class="nav-link" href="${pageContext.request.contextPath}/admin/events">
                    <i class="fas fa-calendar me-2"></i>Événements
                </a>
            </li>
        </ul>
        
        <!-- Alert Messages -->
        <div id="alertContainer"></div>
        
        <div class="row">
            <!-- Profile Information -->
            <div class="col-md-4">
                <div class="profile-card text-center">
                    <div class="profile-avatar">
                        <i class="fas fa-user-shield"></i>
                    </div>
                    <h4 id="profileName">${sessionScope.userName}</h4>
                    <p class="text-muted mb-3" id="profileEmail" style="color: #2d1038 !important;">${sessionScope.userEmail}</p>
                    <span class="badge badge-role">
                        <i class="fas fa-shield-alt me-1"></i>Administrateur
                    </span>
                </div>
                
                <div class="profile-card">
                    <h5 class="mb-3"><i class="fas fa-info-circle me-2"></i>Informations</h5>
                    <div class="info-group">
                        <div class="info-label">Type de compte</div>
                        <div class="info-value">ADMIN</div>
                    </div>
                    <div class="info-group">
                        <div class="info-label">Statut</div>
                        <div class="info-value text-success">
                            <i class="fas fa-check-circle me-1"></i>Actif
                        </div>
                    </div>
                </div>
            </div>
            
            <!-- Edit Forms -->
            <div class="col-md-8">
                <!-- Edit Profile Form -->
                <div class="profile-card">
                    <h4 class="mb-4"><i class="fas fa-edit me-2"></i>Modifier le profil</h4>
                    <form id="editProfileForm">
                        <div class="row">
                            <div class="col-md-6 mb-3">
                                <label for="nom" class="form-label">Nom complet</label>
                                <input type="text" class="form-control" id="nom" name="nom" 
                                       value="${sessionScope.userName}" required>
                            </div>
                            <div class="col-md-6 mb-3">
                                <label for="email" class="form-label">Email</label>
                                <input type="email" class="form-control" id="email" name="email" 
                                       value="${sessionScope.userEmail}" required>
                            </div>
                        </div>
                        <button type="submit" class="btn btn-primary">
                            <i class="fas fa-save me-2"></i>Enregistrer les modifications
                        </button>
                    </form>
                </div>
                
                <!-- Change Password Form -->
                <div class="profile-card">
                    <h4 class="mb-4"><i class="fas fa-lock me-2"></i>Changer le mot de passe</h4>
                    <form id="changePasswordForm">
                        <div class="mb-3">
                            <label for="currentPassword" class="form-label">Mot de passe actuel</label>
                            <input type="password" class="form-control" id="currentPassword" 
                                   name="currentPassword" required>
                        </div>
                        <div class="row">
                            <div class="col-md-6 mb-3">
                                <label for="newPassword" class="form-label">Nouveau mot de passe</label>
                                <input type="password" class="form-control" id="newPassword" 
                                       name="newPassword" minlength="8" required>
                                <small class="form-text text-muted">Minimum 8 caractères</small>
                            </div>
                            <div class="col-md-6 mb-3">
                                <label for="confirmPassword" class="form-label">Confirmer le mot de passe</label>
                                <input type="password" class="form-control" id="confirmPassword" 
                                       name="confirmPassword" minlength="8" required>
                            </div>
                        </div>
                        <button type="submit" class="btn btn-primary">
                            <i class="fas fa-key me-2"></i>Changer le mot de passe
                        </button>
                    </form>
                </div>
            </div>
        </div>
    </div>
    
    <!-- Bootstrap JS -->
    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>
    
    <script>
        const contextPath = '${pageContext.request.contextPath}';
        console.log('Context path set to:', contextPath);
        console.log('Context path length:', contextPath.length);
        
        // Show alert message
        function showAlert(message, type = 'success') {
            const alertContainer = document.getElementById('alertContainer');
            const alert = document.createElement('div');
            alert.className = `alert alert-${type} alert-dismissible fade show`;
            const iconClass = type === 'success' ? 'check-circle' : 'exclamation-circle';
            alert.innerHTML = `
                <i class="fas fa-${iconClass} me-2"></i>
                ${message}
                <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
            `;
            alertContainer.appendChild(alert);
            
            setTimeout(() => {
                alert.remove();
            }, 5000);
        }
        
        // Handle profile edit form
        document.getElementById('editProfileForm').addEventListener('submit', async (e) => {
            e.preventDefault();
            
            const formData = {
                nom: document.getElementById('nom').value,
                email: document.getElementById('email').value
            };
            
            try {
                const response = await fetch(contextPath + '/admin/profil', {
                    method: 'PUT',
                    headers: {
                        'Content-Type': 'application/json'
                    },
                    body: JSON.stringify(formData)
                });
                
                const data = await response.json();
                
                if (response.ok && data.success) {
                    showAlert(data.message || 'Profil mis à jour avec succès', 'success');
                    
                    // Update displayed info
                    document.getElementById('profileName').textContent = formData.nom;
                    document.getElementById('profileEmail').textContent = formData.email;
                    
                    // Reload page after 2 seconds to update session
                    setTimeout(() => {
                        window.location.reload();
                    }, 2000);
                } else {
                    showAlert(data.message || 'Erreur lors de la mise à jour', 'danger');
                }
            } catch (error) {
                console.error('Error:', error);
                showAlert('Erreur lors de la mise à jour du profil', 'danger');
            }
        });
        
        // Handle password change form
        document.getElementById('changePasswordForm').addEventListener('submit', async (e) => {
            e.preventDefault();
            console.log('Password change form submitted');
            
            const currentPassword = document.getElementById('currentPassword').value;
            const newPassword = document.getElementById('newPassword').value;
            const confirmPassword = document.getElementById('confirmPassword').value;
            
            console.log('Current password length:', currentPassword.length);
            console.log('New password length:', newPassword.length);
            console.log('Confirm password length:', confirmPassword.length);
            
            // Validate passwords match
            if (newPassword !== confirmPassword) {
                showAlert('Les mots de passe ne correspondent pas', 'danger');
                return;
            }
            
            const formData = {
                currentPassword: currentPassword,
                newPassword: newPassword,
                confirmPassword: confirmPassword
            };
            
            console.log('Sending request to:', contextPath + '/admin/mot-de-passe');
            console.log('Full URL will be:', window.location.origin + contextPath + '/admin/mot-de-passe');
            
            try {
                const response = await fetch(contextPath + '/admin/mot-de-passe', {
                    method: 'PUT',
                    headers: {
                        'Content-Type': 'application/json'
                    },
                    body: JSON.stringify(formData)
                });
                
                console.log('Response status:', response.status);
                console.log('Response ok:', response.ok);
                
                const data = await response.json();
                console.log('Response data:', data);
                
                if (response.ok && data.success) {
                    showAlert(data.message || 'Mot de passe modifié avec succès', 'success');
                    
                    // Reset form
                    document.getElementById('changePasswordForm').reset();
                } else {
                    showAlert(data.message || 'Erreur lors du changement de mot de passe', 'danger');
                }
            } catch (error) {
                console.error('Error details:', error);
                showAlert('Erreur lors du changement de mot de passe: ' + error.message, 'danger');
            }
        });
    </script>
</body>
</html>

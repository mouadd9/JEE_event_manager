<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

</main>
<!-- Main Content End -->

<!-- Footer -->
<footer class="bg-dark text-white mt-5">
    <div class="container py-4">
        <div class="row">
            <!-- About Section -->
            <div class="col-md-4 mb-3">
                <h5><i class="fas fa-calendar-alt"></i> EventManagement</h5>
                <p class="text-muted small">
                    Plateforme de gestion d'événements développée par ENSA Tétouan.
                    Découvrez, organisez et participez aux meilleurs événements.
                </p>
            </div>

            <!-- Quick Links -->
            <div class="col-md-4 mb-3">
                <h6>Liens Rapides</h6>
                <ul class="list-unstyled small">
                    <li><a href="${pageContext.request.contextPath}/" class="text-muted text-decoration-none">
                        <i class="fas fa-home"></i> Accueil
                    </a></li>
                    <li><a href="${pageContext.request.contextPath}/events/browse" class="text-muted text-decoration-none">
                        <i class="fas fa-calendar"></i> Événements
                    </a></li>
                    <li><a href="${pageContext.request.contextPath}/about" class="text-muted text-decoration-none">
                        <i class="fas fa-info-circle"></i> À propos
                    </a></li>
                    <li><a href="${pageContext.request.contextPath}/contact" class="text-muted text-decoration-none">
                        <i class="fas fa-envelope"></i> Contact
                    </a></li>
                    <li><a href="${pageContext.request.contextPath}/privacy" class="text-muted text-decoration-none">
                        <i class="fas fa-shield-alt"></i> Confidentialité
                    </a></li>
                </ul>
            </div>

            <!-- Contact & Social -->
            <div class="col-md-4 mb-3">
                <h6>Nous Contacter</h6>
                <p class="text-muted small mb-2">
                    <i class="fas fa-map-marker-alt"></i> ENSA Tétouan, Maroc<br>
                    <i class="fas fa-envelope"></i> contact@eventmanagement.ma<br>
                    <i class="fas fa-phone"></i> +212 5XX-XXXXXX
                </p>
                <div class="mt-3">
                    <h6 class="small">Suivez-nous</h6>
                    <a href="#" class="text-white me-3" title="Facebook">
                        <i class="fab fa-facebook fa-lg"></i>
                    </a>
                    <a href="#" class="text-white me-3" title="Twitter">
                        <i class="fab fa-twitter fa-lg"></i>
                    </a>
                    <a href="#" class="text-white me-3" title="Instagram">
                        <i class="fab fa-instagram fa-lg"></i>
                    </a>
                    <a href="#" class="text-white" title="LinkedIn">
                        <i class="fab fa-linkedin fa-lg"></i>
                    </a>
                </div>
            </div>
        </div>

        <hr class="border-secondary">

        <!-- Copyright -->
        <div class="row">
            <div class="col-md-12 text-center">
                <p class="text-muted small mb-0">
                    &copy; <fmt:formatDate value="<%= new java.util.Date() %>" pattern="yyyy"/> EventManagement - ENSA Tétouan.
                    Tous droits réservés.
                </p>
            </div>
        </div>
    </div>
</footer>

<!-- Bootstrap 5.3 JS Bundle (includes Popper) -->
<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>

<!-- jQuery 3.7 -->
<script src="https://code.jquery.com/jquery-3.7.0.min.js"></script>

<!-- Custom JavaScript -->
<script>
    // Auto-dismiss alerts after 5 seconds
    $(document).ready(function() {
        setTimeout(function() {
            $('.alert-dismissible').fadeOut('slow', function() {
                $(this).remove();
            });
        }, 5000);
    });

    // Confirmation dialogs for delete/cancel actions
    $('.confirm-action').on('click', function(e) {
        if (!confirm('Êtes-vous sûr de vouloir effectuer cette action ?')) {
            e.preventDefault();
            return false;
        }
    });

    // Add active class to current nav item
    $(document).ready(function() {
        var path = window.location.pathname;
        $('.navbar-nav .nav-link').each(function() {
            var href = $(this).attr('href');
            if (href && path.indexOf(href) !== -1 && href !== '/') {
                $(this).addClass('active');
            }
        });
    });
</script>

</body>
</html>

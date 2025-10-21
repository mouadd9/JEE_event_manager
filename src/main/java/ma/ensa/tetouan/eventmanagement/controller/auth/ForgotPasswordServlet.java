package ma.ensa.tetouan.eventmanagement.controller.auth;

import ma.ensa.tetouan.eventmanagement.dao.UserDAO;
import ma.ensa.tetouan.eventmanagement.dao.UserDAOImpl;
import ma.ensa.tetouan.eventmanagement.model.User;
import ma.ensa.tetouan.eventmanagement.service.EmailServiceImpl;
import ma.ensa.tetouan.eventmanagement.util.PasswordUtil;
import ma.ensa.tetouan.eventmanagement.util.ServletUtil;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Optional;

@WebServlet(name = "ForgotPasswordServlet", urlPatterns = "/forgot-password")
public class ForgotPasswordServlet extends HttpServlet {
    private UserDAO userDAO;
    private EmailServiceImpl emailService;

    @Override
    public void init() throws ServletException {
        userDAO = new UserDAOImpl();
    emailService = new EmailServiceImpl();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        ServletUtil.forward(request, response, "/WEB-INF/views/auth/forgot-password.jsp");
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String email = request.getParameter("email");
        if (email == null || email.isEmpty()) {
            ServletUtil.setErrorMessage(request.getSession(), "Veuillez entrer votre adresse email.");
            ServletUtil.forward(request, response, "/WEB-INF/views/auth/forgot-password.jsp");
            return;
        }
        Optional<User> userOpt = userDAO.findByEmail(email);
        if (!userOpt.isPresent()) {
            ServletUtil.setErrorMessage(request.getSession(), "Aucun compte trouvé avec cet email.");
            ServletUtil.forward(request, response, "/WEB-INF/views/auth/forgot-password.jsp");
            return;
        }
        User user = userOpt.get();
        // Générer un nouveau mot de passe temporaire
        // Utiliser la bonne méthode utilitaire pour générer un mot de passe temporaire fort
        String tempPassword = PasswordUtil.generateTemporaryPassword(10);
        String hashed = PasswordUtil.hashPassword(tempPassword);
        user.setMotDePasse(hashed);
        userDAO.update(user);
        // Envoyer le mot de passe temporaire par email
        String subject = "Réinitialisation de votre mot de passe";
        String body = "Bonjour " + user.getNom() + ",\n\n" +
                "Votre nouveau mot de passe temporaire est : " + tempPassword + "\n" +
                "Merci de vous connecter et de le modifier dès que possible.\n\n" +
                "Ceci est un message automatique.\n";
    emailService.sendEmail(user.getEmail(), subject, body);
        ServletUtil.setSuccessMessage(request.getSession(), "Un nouveau mot de passe a été envoyé à votre adresse email.");
        ServletUtil.redirect(response, request.getContextPath() + "/login");
    }
}

package com.projet.jee.servlet;

import com.google.gson.Gson;
import com.projet.jee.model.Commentaire;
import com.projet.jee.service.AuthenticationService;
import com.projet.jee.service.CommentaireService;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Servlet for managing comments on events.
 * Supports GET (retrieve) and POST (add/update/delete) operations.
 */
public class CommentServlet extends HttpServlet {
    private CommentaireService commentaireService;
    private AuthenticationService authService;
    private Gson gson;

    @Override
    public void init() throws ServletException {
        commentaireService = new CommentaireService();
        authService = new AuthenticationService();
        gson = new Gson();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String evenementIdStr = request.getParameter("evenementId");

        if (evenementIdStr == null) {
            sendJsonError(response, "ID événement manquant");
            return;
        }

        try {
            Long evenementId = Long.parseLong(evenementIdStr);
            List<Commentaire> comments = commentaireService.getCommentsByEvent(evenementId);

            response.setContentType("application/json");
            response.setCharacterEncoding("UTF-8");
            PrintWriter out = response.getWriter();
            out.print(gson.toJson(comments));
            out.flush();

        } catch (NumberFormatException e) {
            sendJsonError(response, "ID événement invalide");
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        Long userId = authService.getCurrentUserId(request.getSession());
        if (userId == null) {
            sendJsonError(response, "Non authentifié");
            return;
        }

        String action = request.getParameter("action");

        try {
            if ("add".equals(action)) {
                addComment(request, response, userId);
            } else if ("update".equals(action)) {
                updateComment(request, response, userId);
            } else if ("delete".equals(action)) {
                deleteComment(request, response, userId);
            } else {
                sendJsonError(response, "Action non reconnue");
            }
        } catch (Exception e) {
            sendJsonError(response, e.getMessage());
        }
    }

    private void addComment(HttpServletRequest request, HttpServletResponse response, Long userId)
            throws IOException {
        String evenementIdStr = request.getParameter("evenementId");
        String contenu = request.getParameter("contenu");

        Long evenementId = Long.parseLong(evenementIdStr);
        Commentaire comment = commentaireService.addComment(userId, evenementId, contenu);

        Map<String, Object> result = new HashMap<>();
        result.put("success", true);
        result.put("comment", comment);
        sendJsonResponse(response, result);
    }

    private void updateComment(HttpServletRequest request, HttpServletResponse response, Long userId)
            throws IOException {
        String commentIdStr = request.getParameter("commentId");
        String contenu = request.getParameter("contenu");

        Long commentId = Long.parseLong(commentIdStr);
        Commentaire updated = commentaireService.updateComment(commentId, userId, contenu);

        Map<String, Object> result = new HashMap<>();
        result.put("success", true);
        result.put("comment", updated);
        sendJsonResponse(response, result);
    }

    private void deleteComment(HttpServletRequest request, HttpServletResponse response, Long userId)
            throws IOException {
        String commentIdStr = request.getParameter("commentId");
        Long commentId = Long.parseLong(commentIdStr);

        commentaireService.deleteComment(commentId, userId);

        Map<String, Object> result = new HashMap<>();
        result.put("success", true);
        sendJsonResponse(response, result);
    }

    private void sendJsonResponse(HttpServletResponse response, Object data) throws IOException {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        PrintWriter out = response.getWriter();
        out.print(gson.toJson(data));
        out.flush();
    }

    private void sendJsonError(HttpServletResponse response, String message) throws IOException {
        Map<String, Object> error = new HashMap<>();
        error.put("success", false);
        error.put("error", message);
        sendJsonResponse(response, error);
    }
}

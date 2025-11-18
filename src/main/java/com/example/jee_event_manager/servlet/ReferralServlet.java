package com.example.jee_event_manager.servlet;

import com.example.jee_event_manager.util.GsonUtil;
import com.example.jee_event_manager.util.JsonResponse;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * Servlet pour gérer les codes de parrainage pour les événements
 */
@WebServlet(name = "ReferralServlet", urlPatterns = {"/api/referral/*"})
public class ReferralServlet extends HttpServlet {
    
    private static final SecureRandom random = new SecureRandom();
    
    /**
     * POST /api/referral/generate - Générer un code de parrainage pour un événement
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        
        try {
            // Récupérer l'ID du participant depuis la session
            Long participantId = getParticipantIdFromSession(request);
            
            // Récupérer l'ID de l'événement depuis le body
            String pathInfo = request.getPathInfo();
            if (pathInfo == null || !pathInfo.equals("/generate")) {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                response.getWriter().write(GsonUtil.toJson(
                    JsonResponse.error("Endpoint invalide")
                ));
                return;
            }
            
            // Lire le body JSON
            StringBuilder body = new StringBuilder();
            String line;
            while ((line = request.getReader().readLine()) != null) {
                body.append(line);
            }
            
            @SuppressWarnings("unchecked")
            Map<String, Object> requestData = (Map<String, Object>) GsonUtil.fromJson(body.toString(), Map.class);
            Object eventIdObj = requestData.get("eventId");
            
            if (eventIdObj == null) {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                response.getWriter().write(GsonUtil.toJson(
                    JsonResponse.error("ID d'événement manquant")
                ));
                return;
            }
            
            Long eventId;
            try {
                if (eventIdObj instanceof Number) {
                    eventId = ((Number) eventIdObj).longValue();
                } else {
                    eventId = Long.parseLong(eventIdObj.toString());
                }
            } catch (NumberFormatException e) {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                response.getWriter().write(GsonUtil.toJson(
                    JsonResponse.error("ID d'événement invalide")
                ));
                return;
            }
            
            // Générer un code de parrainage unique
            String referralCode = generateReferralCode(eventId, participantId);
            
            // Stocker le code dans la session (ou en base de données si nécessaire)
            HttpSession session = request.getSession(true);
            String sessionKey = "referral_" + eventId + (participantId != null ? "_" + participantId : "");
            session.setAttribute(sessionKey, referralCode);
            session.setAttribute(sessionKey + "_created", LocalDateTime.now().toString());
            
            // Retourner le code
            Map<String, Object> data = new HashMap<>();
            data.put("referralCode", referralCode);
            data.put("eventId", eventId);
            data.put("inviteUrl", buildInviteUrl(request, eventId, referralCode));
            
            response.getWriter().write(GsonUtil.toJson(
                JsonResponse.success("Code de parrainage généré avec succès", data)
            ));
            
        } catch (Exception e) {
            e.printStackTrace();
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.getWriter().write(GsonUtil.toJson(
                JsonResponse.error("Erreur lors de la génération du code: " + e.getMessage())
            ));
        }
    }
    
    /**
     * GET /api/referral/verify?code=XXX&eventId=YYY - Vérifier un code de parrainage
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        
        try {
            String pathInfo = request.getPathInfo();
            if (pathInfo == null || !pathInfo.equals("/verify")) {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                response.getWriter().write(GsonUtil.toJson(
                    JsonResponse.error("Endpoint invalide")
                ));
                return;
            }
            
            String code = request.getParameter("code");
            String eventIdStr = request.getParameter("eventId");
            
            if (code == null || eventIdStr == null) {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                response.getWriter().write(GsonUtil.toJson(
                    JsonResponse.error("Code et ID d'événement requis")
                ));
                return;
            }
            
            Long eventId = Long.parseLong(eventIdStr);
            
            // Vérifier le code dans la session
            HttpSession session = request.getSession(false);
            boolean isValid = false;
            
            if (session != null) {
                // Chercher dans toutes les clés de session
                java.util.Enumeration<String> attributeNames = session.getAttributeNames();
                while (attributeNames.hasMoreElements()) {
                    String key = attributeNames.nextElement();
                    if (key.startsWith("referral_" + eventId + "_") && 
                        code.equals(session.getAttribute(key))) {
                        isValid = true;
                        break;
                    }
                }
            }
            
            Map<String, Object> data = new HashMap<>();
            data.put("valid", isValid);
            data.put("code", code);
            data.put("eventId", eventId);
            
            response.getWriter().write(GsonUtil.toJson(
                JsonResponse.success("Vérification du code", data)
            ));
            
        } catch (Exception e) {
            e.printStackTrace();
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.getWriter().write(GsonUtil.toJson(
                JsonResponse.error("Erreur lors de la vérification: " + e.getMessage())
            ));
        }
    }
    
    /**
     * Générer un code de parrainage unique
     */
    private String generateReferralCode(Long eventId, Long participantId) {
        // Format: EVT-{eventId}-{timestamp}-{random}
        String timestamp = Long.toString(System.currentTimeMillis(), 36).toUpperCase();
        String randomPart = generateRandomString(4).toUpperCase();
        String eventPart = String.format("%04d", eventId);
        
        if (participantId != null) {
            String participantStr = participantId.toString();
            String participantPart = participantStr.length() > 3 
                ? participantStr.substring(participantStr.length() - 3)
                : String.format("%03d", participantId);
            return String.format("EVT-%s-%s-%s-%s", eventPart, 
                timestamp.length() > 4 ? timestamp.substring(0, 4) : timestamp, 
                participantPart, randomPart);
        } else {
            return String.format("EVT-%s-%s-%s", eventPart, 
                timestamp.length() > 4 ? timestamp.substring(0, 4) : timestamp, 
                randomPart);
        }
    }
    
    /**
     * Générer une chaîne aléatoire
     */
    private String generateRandomString(int length) {
        String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < length; i++) {
            sb.append(chars.charAt(random.nextInt(chars.length())));
        }
        return sb.toString();
    }
    
    /**
     * Construire l'URL d'invitation
     */
    private String buildInviteUrl(HttpServletRequest request, Long eventId, String referralCode) {
        String scheme = request.getScheme();
        String serverName = request.getServerName();
        int serverPort = request.getServerPort();
        String contextPath = request.getContextPath();
        
        String url = scheme + "://" + serverName;
        if (serverPort != 80 && serverPort != 443) {
            url += ":" + serverPort;
        }
        url += contextPath + "/event-details?id=" + eventId + "&ref=" + referralCode;
        
        return url;
    }
    
    /**
     * Récupérer l'ID du participant depuis la session
     */
    private Long getParticipantIdFromSession(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session == null) return null;
        
        Object participantId = session.getAttribute("participantId");
        if (participantId instanceof Long) {
            return (Long) participantId;
        }
        
        Object userId = session.getAttribute("userId");
        if (userId instanceof Long) {
            return (Long) userId;
        }
        
        return null;
    }
}


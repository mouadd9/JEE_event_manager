package com.example.jee_event_manager.service;

import jakarta.ejb.Stateless;
import jakarta.servlet.http.Part;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

@Stateless
public class FileUploadService {
    
    private static final String UPLOAD_DIR = "uploads/events/";
    private static final List<String> ALLOWED_TYPES = Arrays.asList("image/jpeg", "image/png", "image/webp");
    private static final long MAX_FILE_SIZE = 5 * 1024 * 1024; // 5MB
    
    /**
     * Sauvegarde une image d'événement et retourne l'URL relative
     */
    public String saveEventImage(Part filePart) throws IOException {
        if (filePart == null || filePart.getSize() == 0) {
            return null;
        }
        
        // Validation du type de fichier
        String contentType = filePart.getContentType();
        if (!ALLOWED_TYPES.contains(contentType)) {
            throw new IllegalArgumentException("Type de fichier non supporté. Utilisez JPG, PNG ou WEBP.");
        }
        
        // Validation de la taille
        if (filePart.getSize() > MAX_FILE_SIZE) {
            throw new IllegalArgumentException("Fichier trop volumineux. Taille maximale: 5MB.");
        }
        
        // Créer le répertoire s'il n'existe pas
        Path uploadPath = Paths.get(UPLOAD_DIR);
        if (!Files.exists(uploadPath)) {
            Files.createDirectories(uploadPath);
        }
        
        // Générer un nom de fichier unique
        String originalFileName = filePart.getSubmittedFileName();
        String fileExtension = getFileExtension(originalFileName);
        String uniqueFileName = UUID.randomUUID().toString() + fileExtension;
        
        // Sauvegarder le fichier
        Path filePath = uploadPath.resolve(uniqueFileName);
        try (InputStream inputStream = filePart.getInputStream()) {
            Files.copy(inputStream, filePath, StandardCopyOption.REPLACE_EXISTING);
        }
        
        // Retourner l'URL relative
        return UPLOAD_DIR + uniqueFileName;
    }
    
    /**
     * Supprime une image d'événement
     */
    public void deleteEventImage(String imageUrl) {
        if (imageUrl == null || imageUrl.trim().isEmpty()) {
            return;
        }
        
        try {
            Path filePath = Paths.get(imageUrl);
            if (Files.exists(filePath)) {
                Files.delete(filePath);
            }
        } catch (IOException e) {
            // Log l'erreur mais ne pas faire échouer l'opération
            System.err.println("Erreur lors de la suppression de l'image: " + imageUrl + " - " + e.getMessage());
        }
    }
    
    /**
     * Valide un fichier image
     */
    public boolean isValidImageFile(Part filePart) {
        if (filePart == null || filePart.getSize() == 0) {
            return false;
        }
        
        String contentType = filePart.getContentType();
        return ALLOWED_TYPES.contains(contentType) && filePart.getSize() <= MAX_FILE_SIZE;
    }
    
    /**
     * Extrait l'extension d'un nom de fichier
     */
    private String getFileExtension(String fileName) {
        if (fileName == null || fileName.lastIndexOf('.') == -1) {
            return ".jpg"; // Extension par défaut
        }
        return fileName.substring(fileName.lastIndexOf('.'));
    }
    
    /**
     * Obtient le type MIME à partir de l'extension
     */
    public String getMimeTypeFromExtension(String fileName) {
        String extension = getFileExtension(fileName).toLowerCase();
        switch (extension) {
            case ".jpg":
            case ".jpeg":
                return "image/jpeg";
            case ".png":
                return "image/png";
            case ".webp":
                return "image/webp";
            default:
                return "image/jpeg";
        }
    }
}

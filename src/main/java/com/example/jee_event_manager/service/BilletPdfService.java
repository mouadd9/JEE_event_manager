package com.example.jee_event_manager.service;

import com.example.jee_event_manager.model.Billet;
import com.example.jee_event_manager.model.Evenement;
import com.example.jee_event_manager.model.Inscription;
import com.example.jee_event_manager.model.Participant;
import com.itextpdf.io.image.ImageDataFactory;
import com.itextpdf.kernel.colors.ColorConstants;
import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.*;
import com.itextpdf.layout.properties.HorizontalAlignment;
import com.itextpdf.layout.properties.TextAlignment;
import com.itextpdf.layout.properties.UnitValue;
import jakarta.enterprise.context.ApplicationScoped;

import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.format.DateTimeFormatter;
import java.util.UUID;
import java.util.logging.Logger;

@ApplicationScoped
public class BilletPdfService {
    
    private static final Logger logger = Logger.getLogger(BilletPdfService.class.getName());
    
    // Configuration des chemins
    private static final String UPLOAD_DIR = "uploads/billets/";
    private static final String LOGO_PATH = "src/main/webapp/images/logo.png"; // À créer
    
    /**
     * Génère un billet PDF pour une inscription
     */
    public Billet genererBillet(Inscription inscription) {
        logger.info("Génération du billet pour l'inscription ID: " + inscription.getId());
        
        try {
            // Générer un numéro de billet unique
            String numeroBillet = genererNumeroBillet();
            
            // Créer le billet
            Billet billet = new Billet();
            billet.setNumeroBillet(numeroBillet);
            billet.setTypeBillet(inscription.getTypeBillet());
            billet.setInscription(inscription);
            
            // Générer le PDF
            byte[] pdfContent = genererPdfContent(billet);
            
            // Sauvegarder le fichier
            String cheminFichier = sauvegarderPdf(numeroBillet, pdfContent);
            billet.setCheminFichier(cheminFichier);
            
            logger.info("Billet généré avec succès: " + numeroBillet);
            return billet;
        } catch (IOException e) {
            logger.severe("Erreur lors de la génération du billet: " + e.getMessage());
            throw new RuntimeException("Erreur lors de la génération du billet", e);
        }
    }
    
    /**
     * Génère le contenu PDF du billet
     */
    private byte[] genererPdfContent(Billet billet) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        PdfWriter writer = new PdfWriter(baos);
        PdfDocument pdfDoc = new PdfDocument(writer);
        Document document = new Document(pdfDoc);
        
        // Configuration des polices
        PdfFont fontTitle = PdfFontFactory.createFont();
        PdfFont fontHeader = PdfFontFactory.createFont();
        PdfFont fontContent = PdfFontFactory.createFont();
        
        Evenement event = billet.getInscription().getEvenement();
        Participant participant = billet.getInscription().getParticipant();
        
        // En-tête avec logo (si disponible)
        try {
            if (Files.exists(Paths.get(LOGO_PATH))) {
                Image logo = new Image(ImageDataFactory.create(LOGO_PATH));
                logo.setWidth(80);
                logo.setHorizontalAlignment(HorizontalAlignment.CENTER);
                document.add(logo);
            }
        } catch (Exception e) {
            logger.warning("Logo non trouvé, utilisation du texte EventHub");
            Paragraph logoText = new Paragraph("EventHub")
                .setFont(fontTitle)
                .setFontSize(24)
                .setBold()
                .setTextAlignment(TextAlignment.CENTER)
                .setFontColor(ColorConstants.BLUE);
            document.add(logoText);
        }
        
        // Titre principal
        Paragraph title = new Paragraph("Billet d'entrée")
            .setFont(fontTitle)
            .setFontSize(20)
            .setBold()
            .setTextAlignment(TextAlignment.CENTER)
            .setMarginBottom(20);
        document.add(title);
        
        // Informations de l'événement
        Table eventTable = new Table(2);
        eventTable.setWidth(UnitValue.createPercentValue(100));
        
        // Nom de l'événement
        eventTable.addCell(createHeaderCell("Événement:"));
        eventTable.addCell(createContentCell(event.getTitre()));
        
        // Date et heure
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd MMMM yyyy");
        DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm");
        
        eventTable.addCell(createHeaderCell("Date:"));
        eventTable.addCell(createContentCell(event.getDateDebut().format(dateFormatter)));
        
        eventTable.addCell(createHeaderCell("Heure:"));
        eventTable.addCell(createContentCell(
            event.getDateDebut().format(timeFormatter) + " - " + 
            event.getDateFin().format(timeFormatter)
        ));
        
        // Lieu
        eventTable.addCell(createHeaderCell("Lieu:"));
        eventTable.addCell(createContentCell(event.getLieu()));
        
        // Type de billet
        eventTable.addCell(createHeaderCell("Type de billet:"));
        Paragraph typeParagraph = new Paragraph(billet.getTypeBillet())
            .setFont(fontContent)
            .setFontSize(12);
        
        // Couleur selon le type
        switch (billet.getTypeBillet().toUpperCase()) {
            case "VIP":
                typeParagraph.setFontColor(ColorConstants.YELLOW);
                typeParagraph.setBold();
                break;
            case "PREMIUM":
                typeParagraph.setFontColor(ColorConstants.MAGENTA);
                typeParagraph.setBold();
                break;
            default:
                typeParagraph.setFontColor(ColorConstants.BLACK);
        }
        
        Cell typeCell = new Cell().add(typeParagraph);
        typeCell.setPadding(8);
        eventTable.addCell(typeCell);
        
        document.add(eventTable);
        
        // Espacement
        document.add(new Paragraph("\n"));
        
        // Informations du participant
        Table participantTable = new Table(2);
        participantTable.setWidth(UnitValue.createPercentValue(100));
        
        participantTable.addCell(createHeaderCell("Participant:"));
        participantTable.addCell(createContentCell(participant.getNom()));
        
        participantTable.addCell(createHeaderCell("Email:"));
        participantTable.addCell(createContentCell(participant.getEmail()));
        
        document.add(participantTable);
        
        // Espacement
        document.add(new Paragraph("\n"));
        
        // Numéro de billet
        Paragraph ticketNumber = new Paragraph("Numéro de billet: " + billet.getNumeroBillet())
            .setFont(fontHeader)
            .setFontSize(16)
            .setBold()
            .setTextAlignment(TextAlignment.CENTER)
            .setFontColor(ColorConstants.DARK_GRAY)
            .setMarginBottom(20);
        document.add(ticketNumber);
        
        // Code QR (simulation avec texte)
        Paragraph qrCode = new Paragraph("QR Code: " + billet.getNumeroBillet())
            .setFont(fontContent)
            .setFontSize(10)
            .setTextAlignment(TextAlignment.CENTER)
            .setFontColor(ColorConstants.GRAY)
            .setMarginBottom(20);
        document.add(qrCode);
        
        // Mentions légales
        Paragraph disclaimer = new Paragraph("Billet gratuit — non transférable")
            .setFont(fontContent)
            .setFontSize(10)
            .setItalic()
            .setTextAlignment(TextAlignment.CENTER)
            .setFontColor(ColorConstants.RED)
            .setMarginTop(30);
        document.add(disclaimer);
        
        // Date de génération
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
        Paragraph generationDate = new Paragraph("Généré le: " + billet.getDateGeneration().format(dateTimeFormatter))
            .setFont(fontContent)
            .setFontSize(8)
            .setTextAlignment(TextAlignment.CENTER)
            .setFontColor(ColorConstants.GRAY)
            .setMarginTop(10);
        document.add(generationDate);
        
        document.close();
        return baos.toByteArray();
    }
    
    /**
     * Crée une cellule d'en-tête
     */
    private Cell createHeaderCell(String text) {
        Cell cell = new Cell();
        try {
            cell.add(new Paragraph(text)
                .setFont(PdfFontFactory.createFont())
                .setFontSize(12)
                .setBold()
                .setFontColor(ColorConstants.DARK_GRAY));
        } catch (IOException e) {
            cell.add(new Paragraph(text).setFontSize(12).setBold());
        }
        cell.setPadding(8);
        cell.setBackgroundColor(ColorConstants.LIGHT_GRAY);
        return cell;
    }
    
    /**
     * Crée une cellule de contenu
     */
    private Cell createContentCell(String text) {
        Cell cell = new Cell();
        try {
            cell.add(new Paragraph(text != null ? text : "")
                .setFont(PdfFontFactory.createFont())
                .setFontSize(12));
        } catch (IOException e) {
            cell.add(new Paragraph(text != null ? text : "").setFontSize(12));
        }
        cell.setPadding(8);
        return cell;
    }
    
    /**
     * Génère un numéro de billet unique
     */
    private String genererNumeroBillet() {
        String timestamp = String.valueOf(System.currentTimeMillis());
        String random = UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        return "EVT-" + timestamp.substring(timestamp.length() - 6) + "-" + random;
    }
    
    /**
     * Sauvegarde le PDF sur le disque
     */
    private String sauvegarderPdf(String numeroBillet, byte[] pdfContent) throws IOException {
        // Créer le répertoire s'il n'existe pas
        Path uploadPath = Paths.get(UPLOAD_DIR);
        if (!Files.exists(uploadPath)) {
            Files.createDirectories(uploadPath);
        }
        
        // Générer le nom de fichier
        String fileName = "billet_" + numeroBillet + ".pdf";
        Path filePath = uploadPath.resolve(fileName);
        
        // Sauvegarder le fichier
        try (FileOutputStream fos = new FileOutputStream(filePath.toFile())) {
            fos.write(pdfContent);
        }
        
        logger.info("PDF sauvegardé: " + filePath.toString());
        return filePath.toString();
    }
    
    /**
     * Lit le contenu d'un fichier PDF
     */
    public byte[] lirePdf(String cheminFichier) throws IOException {
        Path filePath = Paths.get(cheminFichier);
        if (!Files.exists(filePath)) {
            throw new IOException("Fichier PDF non trouvé: " + cheminFichier);
        }
        return Files.readAllBytes(filePath);
    }
    
    /**
     * Supprime un fichier PDF
     */
    public boolean supprimerPdf(String cheminFichier) {
        try {
            Path filePath = Paths.get(cheminFichier);
            if (Files.exists(filePath)) {
                Files.delete(filePath);
                logger.info("PDF supprimé: " + cheminFichier);
                return true;
            }
        } catch (IOException e) {
            logger.warning("Erreur lors de la suppression du PDF: " + e.getMessage());
        }
        return false;
    }
}

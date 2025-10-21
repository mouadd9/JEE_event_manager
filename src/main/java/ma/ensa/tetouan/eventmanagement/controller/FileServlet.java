package ma.ensa.tetouan.eventmanagement.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URLDecoder;

/**
 * Servlet pour servir les fichiers uploadés (images de profil, etc.)
 *
 * @author ENSA Tétouan
 */
@WebServlet(name = "FileServlet", urlPatterns = {"/uploads/*"})
public class FileServlet extends HttpServlet {

    private static final Logger logger = LoggerFactory.getLogger(FileServlet.class);
    private static final int BUFFER_SIZE = 10240; // 10KB

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        // Get the file path from the request
        String requestedFile = request.getPathInfo();
        if (requestedFile == null || requestedFile.equals("/")) {
            response.sendError(HttpServletResponse.SC_NOT_FOUND);
            return;
        }

        // Decode the file name (might contain spaces and special characters)
        requestedFile = URLDecoder.decode(requestedFile, "UTF-8");

        // Get the uploads directory
        String uploadsPath = getServletContext().getRealPath("/uploads");
        
        // Construct the absolute file path
        File file = new File(uploadsPath, requestedFile);

        // Security check: ensure the file is within the uploads directory
        if (!file.getCanonicalPath().startsWith(new File(uploadsPath).getCanonicalPath())) {
            logger.warn("Tentative d'accès à un fichier en dehors du répertoire uploads: {}", requestedFile);
            response.sendError(HttpServletResponse.SC_FORBIDDEN);
            return;
        }

        // Check if file exists and is a file (not a directory)
        if (!file.exists() || !file.isFile()) {
            logger.debug("Fichier non trouvé: {}", file.getAbsolutePath());
            response.sendError(HttpServletResponse.SC_NOT_FOUND);
            return;
        }

        // Get content type
        String contentType = getServletContext().getMimeType(file.getName());
        if (contentType == null) {
            contentType = "application/octet-stream";
        }

        // Set content type and length
        response.setContentType(contentType);
        response.setContentLength((int) file.length());

        // Set cache headers (cache for 1 hour)
        response.setHeader("Cache-Control", "public, max-age=3600");

        logger.debug("Serving file: {} (type: {}, size: {} bytes)", file.getName(), contentType, file.length());

        // Stream the file content
        try (FileInputStream in = new FileInputStream(file);
             OutputStream out = response.getOutputStream()) {

            byte[] buffer = new byte[BUFFER_SIZE];
            int bytesRead;

            while ((bytesRead = in.read(buffer)) != -1) {
                out.write(buffer, 0, bytesRead);
            }
        } catch (IOException e) {
            logger.error("Erreur lors de la lecture du fichier: {}", file.getAbsolutePath(), e);
            if (!response.isCommitted()) {
                response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            }
        }
    }
}

package es.udc.fireproject.backend.util;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import org.springframework.web.multipart.MultipartFile;

public class FileUploadUtil {

  private FileUploadUtil() {
  }

  public static void saveFile(String uploadDir, String fileName,
      MultipartFile multipartFile) throws IOException {
    Path uploadPath = Paths.get(uploadDir).toAbsolutePath().normalize();

    if (!Files.exists(uploadPath)) {
      Files.createDirectories(uploadPath);
    }

    // Sanitize filename to prevent path traversal
    String safeFileName = sanitizeFileName(fileName);

    try (InputStream inputStream = multipartFile.getInputStream()) {
      Path filePath = uploadPath.resolve(safeFileName).normalize();

      // Ensure the resolved path is still under the upload directory
      if (!filePath.startsWith(uploadPath)) {
        throw new IOException("Cannot store file outside the designated upload directory: " + safeFileName);
      }

      Files.copy(inputStream, filePath, StandardCopyOption.REPLACE_EXISTING);
    } catch (IOException ioe) {
      throw new IOException("Could not save image file: " + safeFileName, ioe);
    }
  }

  public static void deleteFile(String uploadDir, String fileName) throws IOException {
    Path uploadPath = Paths.get(uploadDir).toAbsolutePath().normalize();
    String safeFileName = sanitizeFileName(fileName);
    Path filePath = uploadPath.resolve(safeFileName).normalize();
    if (!filePath.startsWith(uploadPath)) {
      throw new IOException("Cannot delete file outside the designated upload directory: " + safeFileName);
    }
    Files.deleteIfExists(filePath);
  }

  private static String sanitizeFileName(String original) {
    if (original == null) {
      throw new IllegalArgumentException("Filename is null");
    }
    String cleaned = org.springframework.util.StringUtils.cleanPath(original);
    String fileName = Paths.get(cleaned).getFileName().toString();
    if (fileName.contains("..") || fileName.contains("/") || fileName.contains("\\")) {
      throw new IllegalArgumentException("Invalid filename: " + original);
    }
    return fileName;
  }

}

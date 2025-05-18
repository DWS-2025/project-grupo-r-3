package com.example.unitalk.services;

import com.example.unitalk.exceptions.StorageException;
import org.apache.commons.io.FilenameUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Objects;

@Service
public class FileStorageService {
    private final Path rootLocation;
    public static Logger log = LoggerFactory.getLogger(FileStorageService.class);

    public FileStorageService() {
        
        this.rootLocation = Paths.get("files").toAbsolutePath().normalize();
        try {
            if (!Files.exists(rootLocation)) {
                Files.createDirectories(rootLocation);
                log.info("Directory created: {}", rootLocation);
            } else {
                log.info("Directory already exists: {}", rootLocation);
            }
        } catch (IOException e) {
            throw new StorageException("Could not initialize storage directory", e);
        }
    }

    public String storeFile(MultipartFile file) throws StorageException {
        try {
            if (file.isEmpty()) {
                throw new StorageException("Failed to store empty file.");
            }

            
            String originalFileName = Objects.requireNonNull(file.getOriginalFilename());
            String sanitizedFileName = FilenameUtils.getName(originalFileName);

            
            if (!sanitizedFileName.toLowerCase().endsWith(".pdf")) {
                throw new StorageException("Only PDF files are allowed.");
            }

            
            if (!sanitizedFileName.matches("[a-zA-Z0-9-_. ]+")) {
                throw new StorageException("Illegal file name. Use only alphanumeric characters, spaces, hyphens, or underscores.");
            }

            
            Path destinationFile = this.rootLocation.resolve(sanitizedFileName).normalize();
            if (!destinationFile.startsWith(this.rootLocation)) {
                throw new StorageException("Path traversal attempt detected.");
            }

            
            if (Files.exists(destinationFile)) {
                throw new StorageException("File already exists.");
            }

            
            Files.copy(file.getInputStream(), destinationFile, StandardCopyOption.REPLACE_EXISTING);
            log.info("File stored successfully: {}", sanitizedFileName);
            return sanitizedFileName;
        } catch (StorageException e) {
            throw e;
        } catch (Exception e) {
            throw new StorageException("Failed to store file", e);
        }
    }

    public Resource loadFileAsResource(String fileName) throws StorageException {
        try {
            
            String sanitizedFileName = FilenameUtils.getName(fileName);
            Path filePath = this.rootLocation.resolve(sanitizedFileName).normalize();

            
            if (!filePath.startsWith(this.rootLocation)) {
                throw new StorageException("Path traversal attempt detected.");
            }

            Resource resource = new UrlResource(filePath.toUri());
            if (resource.exists() && resource.isReadable()) {
                log.info("File loaded successfully: {}", sanitizedFileName);
                return resource;
            } else {
                throw new StorageException("File not found or not readable: " + sanitizedFileName);
            }
        } catch (StorageException e) {
            throw e;
        } catch (Exception e) {
            throw new StorageException("Error loading file: " + fileName, e);
        }
    }

    public void deleteFile(String fileName) throws StorageException {
        try {
            
            String sanitizedFileName = FilenameUtils.getName(fileName);
            Path filePath = this.rootLocation.resolve(sanitizedFileName).normalize();

            
            if (!filePath.startsWith(this.rootLocation)) {
                throw new StorageException("Path traversal attempt detected.");
            }

            if (Files.exists(filePath)) {
                Files.delete(filePath);
                log.info("File deleted successfully: {}", sanitizedFileName);
            } else {
                throw new StorageException("File not found: " + sanitizedFileName);
            }
        } catch (StorageException e) {
            throw e;
        } catch (Exception e) {
            throw new StorageException("Error deleting file: " + fileName, e);
        }
    }
}

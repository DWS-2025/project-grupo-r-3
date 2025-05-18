package com.example.unitalk.services;

import com.example.unitalk.exceptions.StorageException;
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
import java.util.Objects;

@Service
public class FileStorageService {
    private final Path rootLocation;
    public static Logger log = LoggerFactory.getLogger(FileStorageService.class);



    public FileStorageService() {
        this.rootLocation = Paths.get("files");

    }

    public String storeFile(MultipartFile file) throws StorageException{
        try {
            if (!Files.exists(rootLocation)) {
                Files.createDirectories(rootLocation);
                log.info("Directory not found. Creating: {}", rootLocation);
            } else {
                log.info("Directory already exists: {}", rootLocation);
            }
            if (file.isEmpty()) {
                throw new StorageException("Failed to store empty file.");
            }

            String originalFileName = Objects.requireNonNull(file.getOriginalFilename());
            Path destinationFile = this.rootLocation.resolve(Paths.get(originalFileName));

            if (!originalFileName.toLowerCase().endsWith(".pdf")) {
                throw new StorageException("Only PDF files are allowed.");
            }

            if(Files.exists(rootLocation.resolve(originalFileName))) {
                throw new StorageException("File already exist.");
            }
            if (!originalFileName.matches("[a-zA-Z0-9-_. ]+")){
                throw new StorageException("Illegal file name. Try with a valid name.");
            }

            var filePath = destinationFile.normalize().toAbsolutePath().toString();
            var pathFolder = this.rootLocation.toAbsolutePath().toString();
            log.info("path folder: {}, file path: {}", pathFolder,filePath);

            if(filePath.startsWith(pathFolder)) {
                Files.copy(file.getInputStream(), destinationFile);
                return originalFileName;
            }else{
                throw new StorageException("Failed to store file.");
            }
        } catch (StorageException e){
            throw e;
        }
        catch (Exception e) {
            throw new StorageException("The file is not legitimate", e);
        }
    }
    public Resource loadFileAsResource(String fileName) {
        try {
            var filepath = this.rootLocation.resolve(fileName).normalize().toAbsolutePath().toString();
            var pathFolder = this.rootLocation.toAbsolutePath().toString();
            log.info("path folder: {}, file path: {}", pathFolder,filepath);

            if(!filepath.startsWith(pathFolder)) {
                throw new StorageException("Failed to store file.");
            }
            Path filePath = this.rootLocation.resolve(fileName);
            Resource resource = new UrlResource(filePath.toUri());
            if (resource.exists() || resource.isReadable()) {
                return resource;
            } else {
                throw new RuntimeException("File not found " + fileName);
            }
        } catch (StorageException e){
            throw e;
        }catch (Exception e) {
            throw new RuntimeException("Error loading file " + fileName, e);
        }
    }

    public void deleteFile(String fileName) throws IOException {
        Path filePath = rootLocation.resolve(fileName).normalize().toAbsolutePath();
        String filePathStr = filePath.toString();
        String rootPathStr = rootLocation.toAbsolutePath().toString();

        if (!filePathStr.startsWith(rootPathStr)) {
            throw new StorageException("Cannot delete file outside of storage directory");
        }

        if (Files.exists(filePath)) {
            Files.delete(filePath);
        }
    }
}

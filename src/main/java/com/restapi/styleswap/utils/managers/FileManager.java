package com.restapi.styleswap.utils.managers;

import com.restapi.styleswap.exception.ApiException;
import com.restapi.styleswap.exception.ResourceNotFoundException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

@Component
public class FileManager {

    private final Path rootDirectory;

    public FileManager(@Value("${image.upload.dir}") String imageDirectory) {
        this.rootDirectory = Paths.get(imageDirectory);
        createDirectoryIfNeeded(rootDirectory);
    }

    public String saveFile(MultipartFile file) {
        if (file.isEmpty())
            throw new ApiException(HttpStatus.BAD_REQUEST, "Image file must not be empty");

        String fileName = UUID.randomUUID() + "_" + file.getOriginalFilename();
        try {
            Files.write(rootDirectory.resolve(fileName), file.getBytes());
        } catch (IOException e) {
            throw new ApiException(HttpStatus.INTERNAL_SERVER_ERROR, "Could not save file");
        }
        return fileName;
    }

    public Resource loadFile(String fileName) throws IOException {
        Path filePath = rootDirectory.resolve(fileName);

        if (!Files.exists(filePath))
            throw new ResourceNotFoundException("File",fileName);

        Resource resource = new UrlResource(filePath.toUri());
        if (resource.exists() && resource.isReadable()) {
            return resource;
        } else {
            throw new ApiException(HttpStatus.INTERNAL_SERVER_ERROR,
                            "Could not read the file: " + fileName);
        }
    }

    public void deleteFile(String fileName) {
        Path filePath = rootDirectory.resolve(fileName);
        try {
            if (Files.exists(filePath))
                Files.delete(filePath);
            else
                throw new ResourceNotFoundException("File", fileName);
        } catch (IOException e) {
            throw new ApiException(HttpStatus.INTERNAL_SERVER_ERROR, "Could not delete file");
        }
    }

    private static void createDirectoryIfNeeded(Path directoryPath) {
        if (!Files.exists(directoryPath))
            try {
                Files.createDirectories(directoryPath);
            } catch (IOException e) {
                throw new ApiException(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
            }
    }
}
package com.restapi.styleswap.service.impl;
import com.restapi.styleswap.entity.Clothe;
import com.restapi.styleswap.exception.ApiException;
import com.restapi.styleswap.exception.ResourceNotFoundException;
import com.restapi.styleswap.service.ImageService;
import com.restapi.styleswap.utils.Constant;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.UUID;


@Service
public class ImageServiceImpl implements ImageService {

    @Value("${image.upload.dir}")
    private String imageDirectory;

    @Override
    public String saveImage(MultipartFile file){
        if(file.isEmpty()) throw new ApiException( HttpStatus.BAD_REQUEST,"Image file must not be empty");

        String imageName = UUID.randomUUID()+ "_" + file.getOriginalFilename();
        Path directoryPath = Paths.get(imageDirectory);

        if (!Files.exists(directoryPath)) {
            try {
                Files.createDirectories(directoryPath);
            } catch (IOException e) {
                throw new ApiException(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
            }
        }

        Path filePath = directoryPath.resolve(imageName);
        try {
            Files.write(filePath, file.getBytes());
        } catch (IOException e) {
            throw new ApiException(HttpStatus.CONFLICT , e.getMessage());
        }

        return imageName;
    }

    public void updateImages(Clothe clothe, List<MultipartFile> newImages, List<String> deletedImages) {
        if (deletedImages != null && !deletedImages.isEmpty()) {
            clothe.getImages().removeAll(deletedImages);
            deletedImages.forEach(this::deleteImage);
        }

        if(newImages == null) return;
        else if(clothe.getImages().size() + newImages.size() > 5)
            throw new ApiException(HttpStatus.BAD_REQUEST, Constant.IMAGES_VALIDATION_FAILED);
        else if (newImages != null && !newImages.isEmpty()) {
            var newImageNames = newImages.stream().map(this::saveImage).toList();
            clothe.getImages().addAll(newImageNames);
        }
    }

    @Override
    public Resource getImage(String imageName) throws IOException {
        Path filePath = Paths.get(imageDirectory).resolve(imageName);
        if (Files.exists(filePath)) {
            Resource resource = new UrlResource(filePath.toUri());
            if (resource.exists() || resource.isReadable()) {
                return resource;
            } else {
                throw new ApiException(HttpStatus.INTERNAL_SERVER_ERROR,
                                    "Could not read the file: " + imageName);
            }
        } else {
            throw new ResourceNotFoundException("File",imageName);
        }
    }

    @Override
    public void deleteImage(String imageName) {
        Path filePath = Paths.get(imageDirectory).resolve(imageName);

        if (Files.exists(filePath)) {
            try {
                Files.delete(filePath);
            } catch (IOException e) {
                throw new ApiException(HttpStatus.CONFLICT,
                        "Could not delete the file: " + imageName);
            }
        } else {
            throw new ResourceNotFoundException("File", imageName);
        }
    }
}

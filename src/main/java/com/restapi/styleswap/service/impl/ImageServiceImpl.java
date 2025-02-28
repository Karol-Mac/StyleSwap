package com.restapi.styleswap.service.impl;
import com.restapi.styleswap.entity.Clothe;
import com.restapi.styleswap.exception.ApiException;
import com.restapi.styleswap.exception.ResourceNotFoundException;
import com.restapi.styleswap.service.ImageService;
import com.restapi.styleswap.utils.ClotheUtils;
import com.restapi.styleswap.utils.Constant;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
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

    private final ClotheUtils clotheUtils;

    public ImageServiceImpl(ClotheUtils clotheUtils) {
        this.clotheUtils = clotheUtils;
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
    @PreAuthorize("@clotheUtils.isOwner(#clotheId, #email)")
    public void deleteImages(long clotheId, List<String> files, String email) {
        Clothe clothe = clotheUtils.getClotheFromDB(clotheId);

        validIfAllImagesBelongToClothe(files, clothe);

        files.forEach(file -> {
            deleteImage(file);
            clothe.getImages().remove(file);
        });

        clotheUtils.saveClotheInDB(clothe);
    }

    @Override
    @PreAuthorize("@clotheUtils.isOwner(#clotheId, #email)")
    public void uploadImages(long clotheId, List<MultipartFile> files, String email) {
        Clothe clothe = clotheUtils.getClotheFromDB(clotheId);

        if((clothe.getImages().size()+ files.size()) > 5)
            throw new ApiException(HttpStatus.BAD_REQUEST, Constant.IMAGES_VALIDATION_FAILED);

        List<String> imageNames = files.stream().map(this::saveImage).toList();

        clothe.getImages().addAll(imageNames);
        clotheUtils.saveClotheInDB(clothe);
    }

    private String saveImage(MultipartFile file) {
        if(file.isEmpty())
            throw new ApiException( HttpStatus.BAD_REQUEST, "Image file must not be empty");

        Path directoryPath = Paths.get(imageDirectory);
        createDirectoryIfNeeded(directoryPath);

        return saveFileOnDisk(file, directoryPath);
    }

    private static String saveFileOnDisk(MultipartFile file, Path directoryPath) {
        String imageName = UUID.randomUUID()+ "_" + file.getOriginalFilename();
        try {
            Files.write(directoryPath.resolve(imageName), file.getBytes());
        } catch (IOException e) {
            throw new ApiException(HttpStatus.CONFLICT , e.getMessage());
        }
        return imageName;
    }

    private static void createDirectoryIfNeeded(Path directoryPath) {
        if (!Files.exists(directoryPath))
            try {
                Files.createDirectories(directoryPath);
            } catch (IOException e) {
                throw new ApiException(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
            }
    }

    private void deleteImage(String imageName) {
        Path filePath = Paths.get(imageDirectory).resolve(imageName);
        if (Files.exists(filePath)) {
            try {
                Files.delete(filePath);
            } catch (IOException e) {
                throw new ApiException(HttpStatus.CONFLICT,
                        "Could not delete the file: " + imageName);
            }
        }
        else throw new ResourceNotFoundException("File", imageName);
    }

    private static void validIfAllImagesBelongToClothe(List<String> files,
                                                       Clothe clothe) {
        String errosMessage = "Image %s doesn't belong to clothe with id: " + clothe.getId();
        for (String file : files)
            if (!clothe.getImages().contains(file))
                throw new ApiException(HttpStatus.BAD_REQUEST ,errosMessage.formatted(file));
    }
}
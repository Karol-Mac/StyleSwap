package com.restapi.styleswap.service.impl;
import com.restapi.styleswap.entity.Clothe;
import com.restapi.styleswap.exception.ApiException;
import com.restapi.styleswap.service.ImageService;
import com.restapi.styleswap.utils.ClotheUtils;
import com.restapi.styleswap.utils.Constant;
import com.restapi.styleswap.utils.managers.FileManager;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@Service
public class ImageServiceImpl implements ImageService {

    private final ClotheUtils clotheUtils;
    private final FileManager fileManager;

    public ImageServiceImpl(ClotheUtils clotheUtils, FileManager fileManager) {
        this.clotheUtils = clotheUtils;
        this.fileManager = fileManager;
    }

    @Override
    public Resource getImage(String imageName) throws IOException {
        return fileManager.loadFile(imageName);
    }

    @Override
    @PreAuthorize("@clotheUtils.isOwner(#clotheId, #email)")
    public void deleteImages(long clotheId, List<String> files, String email) {
        Clothe clothe = clotheUtils.getClotheFromDB(clotheId);

        validIfAllImagesBelongToClothe(files, clothe);

        files.forEach(file -> {
            fileManager.deleteFile(file);
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

        List<String> imageNames = files.stream()
                                    .map(fileManager::saveFile)
                                    .toList();

        clothe.getImages().addAll(imageNames);
        clotheUtils.saveClotheInDB(clothe);
    }

    private void validIfAllImagesBelongToClothe(List<String> files,
                                                       Clothe clothe) {
        String errorMessage = "Image %s doesn't belong to clothe with id: " + clothe.getId();
        for (String file : files)
            if (!clothe.getImages().contains(file))
                throw new ApiException(HttpStatus.BAD_REQUEST ,errorMessage.formatted(file));
    }
}
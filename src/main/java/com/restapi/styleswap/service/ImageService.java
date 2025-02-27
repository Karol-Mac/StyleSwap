package com.restapi.styleswap.service;

import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

public interface ImageService {

    Resource getImage(String imageName) throws IOException;

    void updateImages(Long clotheId, List<MultipartFile> newImages, List<String> deletedImages);

    void deleteImage(String imageName);

    void saveImage(long clotheId, List<MultipartFile> files, String email);
}

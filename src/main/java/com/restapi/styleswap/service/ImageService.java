package com.restapi.styleswap.service;

import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

public interface ImageService {

    Resource getImage(String imageName) throws IOException;

    void deleteImages(long clotheId, List<String> imageNames, String email);

    void uploadImages(long clotheId, List<MultipartFile> files, String email);
}

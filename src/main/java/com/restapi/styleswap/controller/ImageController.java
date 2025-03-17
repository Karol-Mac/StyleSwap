package com.restapi.styleswap.controller;

import com.restapi.styleswap.service.ImageService;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/api")
public class ImageController {

    private final ImageService imageService;

    public ImageController(ImageService imageService){
        this.imageService = imageService;
    }

    @GetMapping("/images/{imageName}")
    public ResponseEntity<Resource> getImage(@PathVariable String imageName) throws IOException{
        Resource image = imageService.getImage(imageName);
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION,
                                "attachment; filename=\"" + image.getFilename() + "\"")
                .body(image);
    }

    @PostMapping("/clothes/{id}/images")
    public ResponseEntity<String> uploadImage(@PathVariable long id,
                                              @RequestParam("files") List<MultipartFile> files,
                                              Principal principal) {

        imageService.uploadImages(id, files, principal.getName());
        return ResponseEntity.ok("Images uploaded successfully");
    }

    @DeleteMapping(value = "/clothes/{id}/images")
    public ResponseEntity<Void> deleteImage(@PathVariable Long id,
                                              @RequestParam(name = "deletedImages") List<String> deletedImages,
                                              Principal principal) {

        imageService.deleteImages(id, deletedImages, principal.getName());
        return ResponseEntity.noContent().build();
    }
}
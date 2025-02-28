package com.restapi.styleswap.service;

import com.restapi.styleswap.entity.Clothe;
import com.restapi.styleswap.exception.ApiException;
import com.restapi.styleswap.exception.ResourceNotFoundException;
import com.restapi.styleswap.service.impl.ImageServiceImpl;
import com.restapi.styleswap.utils.ClotheUtils;
import com.restapi.styleswap.utils.Constant;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.when;

public class ImageServiceTest {

    private static final String EMAIL = "user@example.com";

    @Mock
    private ClotheUtils clotheUtils;

    @InjectMocks
    private ImageServiceImpl imageService;

    @Mock
    private MultipartFile file1;

    @Mock
    private MultipartFile file2;

    @Mock
    private Clothe clothe;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        ReflectionTestUtils.setField(imageService, "imageDirectory", "test-images");
    }

//    @Test
//    void saveImage_savesImageSuccessfully() {
//        MultipartFile file = mock(MultipartFile.class);
//        when(file.isEmpty()).thenReturn(false);
//        when(file.getOriginalFilename()).thenReturn("image.jpg");
//        when(file.getBytes()).thenReturn(new byte[0]);
//
//        String result = imageService.saveImage(file);
//
//        assertNotNull(result);
//        assertTrue(result.contains("image.jpg"));
//    }
//
//    @Test
//    void saveImage_throwsExceptionWhenFileIsEmpty() {
//        MultipartFile file = mock(MultipartFile.class);
//        when(file.isEmpty()).thenReturn(true);
//
//        ApiException exception = assertThrows(ApiException.class, () -> imageService.saveImage(file));
//        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatus());
//        assertEquals("Image file must not be empty", exception.getMessage());
//    }

    @Test
    void getImage_returnsImageSuccessfully() throws IOException {
        Path filePath = Paths.get("imageDirectory").resolve("image.jpg");
        Files.createFile(filePath);
        Resource resource = imageService.getImage("image.jpg");

        assertNotNull(resource);
        assertTrue(resource.exists());
        Files.delete(filePath);
    }

    @Test
    void getImage_throwsExceptionWhenFileNotFound() {
        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, () -> imageService.getImage("nonexistent.jpg"));
        assertEquals("File not found with name nonexistent.jpg", exception.getMessage());
        assertEquals("nonexistent.jpg", exception.getFieldName());
    }

    @Test
    void testDeleteImages_ShouldDeleteFiles() {
        List<String> files = List.of("image1.jpg", "image2.jpg");

        when(clothe.getImages()).thenReturn(new ArrayList<>(files));
        when(clotheUtils.getClotheFromDB(1L)).thenReturn(clothe);

        imageService.deleteImages(1L, files, EMAIL);

        verify(clothe, times(2)).getImages();
        verify(clotheUtils, times(1)).saveClotheInDB(clothe);
    }

    @Test
    void deleteImages_throwsExceptionWhenImageNotBelongToClothe() {
        when(clotheUtils.getClotheFromDB(1L)).thenReturn(clothe);
        when(clothe.getImages()).thenReturn(List.of("image1.jpg"));

        ApiException exception = assertThrows(
                ApiException.class, () -> imageService.deleteImages(1L, List.of("image2.jpg"), EMAIL));

        assertEquals("Image image2.jpg doesn't belong to clothe with id: 1", exception.getMessage());
        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatus());
    }

    @Test
    void testUploadImages_ShouldSaveImages() throws IOException {

        when(clothe.getImages()).thenReturn(new ArrayList<>());
        when(file1.getOriginalFilename()).thenReturn("image1.jpg");
        when(file2.getOriginalFilename()).thenReturn("image2.jpg");
        when(file1.isEmpty()).thenReturn(false);
        when(file2.isEmpty()).thenReturn(false);
        when(file1.getBytes()).thenReturn(new byte[10]);
        when(file2.getBytes()).thenReturn(new byte[10]);
        when(clotheUtils.getClotheFromDB(1L)).thenReturn(clothe);

        imageService.uploadImages(1L, List.of(file1, file2), EMAIL);

        assertEquals(2, clothe.getImages().size());
        verify(clotheUtils, times(1)).saveClotheInDB(clothe);
    }

    @Test
    void uploadImages_throwsExceptionWhenExceedingImageLimit() {
        when(clotheUtils.getClotheFromDB(1L)).thenReturn(clothe);
        when(clothe.getImages()).thenReturn(List.of(
                "image1.jpg", "image2.jpg", "image3.jpg", "image4.jpg", "image5.jpg"));

        ApiException exception = assertThrows(
                ApiException.class, () -> imageService.uploadImages(1L, List.of(file1), EMAIL));

        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatus());
        assertEquals(Constant.IMAGES_VALIDATION_FAILED, exception.getMessage());
    }
}

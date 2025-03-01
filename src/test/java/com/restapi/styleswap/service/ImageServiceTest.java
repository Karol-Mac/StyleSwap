package com.restapi.styleswap.service;

import com.restapi.styleswap.entity.Clothe;
import com.restapi.styleswap.exception.ApiException;
import com.restapi.styleswap.exception.ResourceNotFoundException;
import com.restapi.styleswap.service.impl.ImageServiceImpl;
import com.restapi.styleswap.utils.ClotheUtils;
import com.restapi.styleswap.utils.Constant;
import com.restapi.styleswap.utils.managers.FileManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpStatus;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Path;
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

    @Mock
    private FileManager fileManager;

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
    }

    @Test
    void getImage_returnsImageSuccessfully() throws IOException {

        when(fileManager.loadFile("image.jpg")).thenReturn(
                new UrlResource(Path.of("image.jpg").toUri()));

        Resource resource = imageService.getImage("image.jpg");

        assertNotNull(resource);
    }

    @Test
    void getImage_throwsResourceNotFoundExceptionWhenFileNotFound() throws IOException {
        when(fileManager.loadFile("nonexistent.jpg")).thenThrow(
                new ResourceNotFoundException("File", "nonexistent.jpg"));

        var exception = assertThrows(ResourceNotFoundException.class,
                            () -> imageService.getImage("nonexistent.jpg"));

        assertEquals("File not found with name nonexistent.jpg", exception.getMessage());
        assertEquals("nonexistent.jpg", exception.getFieldName());
    }

    @Test
    void getImage_throwsApiExceptionWhenResourceNotReadable() throws IOException {
        when(fileManager.loadFile("nonexistent.jpg")).thenThrow(
                new ApiException(HttpStatus.INTERNAL_SERVER_ERROR,
                                "Could not read the file: nonexistent.jpg"));

        var exception = assertThrows(ApiException.class,
                () -> imageService.getImage("nonexistent.jpg"));

        assertEquals("Could not read the file: nonexistent.jpg", exception.getMessage());
    }

    @Test
    void testDeleteImages_ShouldDeleteFiles() {
        List<String> files = List.of("image1.jpg", "image2.jpg");

        when(clotheUtils.getClotheFromDB(1L)).thenReturn(clothe);
        when(clothe.getImages()).thenReturn(new ArrayList<>(files));
        doNothing().when(fileManager).deleteFile(anyString());

        imageService.deleteImages(1L, files, EMAIL);

        verify(clothe, times(4)).getImages();
        verify(clotheUtils, times(1)).saveClotheInDB(clothe);
        verify(fileManager, times(2)).deleteFile(anyString());
    }

    @Test
    void deleteImages_throwsApiExceptionWhenImageNotBelongToClothe() {
        when(clotheUtils.getClotheFromDB(1L)).thenReturn(clothe);
        when(clothe.getImages()).thenReturn(List.of("image1.jpg"));

        ApiException exception = assertThrows(
                ApiException.class, () -> imageService.deleteImages(1L, List.of("image2.jpg"), EMAIL));

        assertEquals("Image image2.jpg doesn't belong to clothe with id: 0", exception.getMessage());
        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatus());
    }

    @Test
    void deleteImages_throwsResourceNotFoundExceptionWhenFileNotFound() {
        when(clotheUtils.getClotheFromDB(1L)).thenReturn(clothe);
        when(clothe.getImages()).thenReturn(List.of("image2.jpg"));
        doThrow(new ResourceNotFoundException("File", "image2.jpg"))
            .when(fileManager).deleteFile("image2.jpg");

        var exception = assertThrows(ResourceNotFoundException.class,
                    () -> imageService.deleteImages(1L, List.of("image2.jpg"), EMAIL));

        assertEquals("File not found with name image2.jpg", exception.getMessage());
        assertEquals("image2.jpg", exception.getFieldName());
    }

    @Test
    void testUploadImages_ShouldSaveImages() {
        when(clotheUtils.getClotheFromDB(1L)).thenReturn(clothe);
        when(clothe.getImages()).thenReturn(new ArrayList<>());
        when(fileManager.saveFile(file1)).thenReturn("image1.jpg");

        imageService.uploadImages(1L, List.of(file1, file2), EMAIL);

        assertEquals(2, clothe.getImages().size());
        verify(clotheUtils, times(1)).saveClotheInDB(clothe);
        verify(fileManager, times(2)).saveFile(any());
    }

    @Test
    void uploadImages_throwsApiExceptionWhenExceedingImageLimit() {
        when(clotheUtils.getClotheFromDB(1L)).thenReturn(clothe);
        when(clothe.getImages()).thenReturn(List.of(
                "image1.jpg", "image2.jpg", "image3.jpg", "image4.jpg", "image5.jpg"));

        ApiException exception = assertThrows(
                ApiException.class, () -> imageService.uploadImages(1L, List.of(file1), EMAIL));

        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatus());
        assertEquals(Constant.IMAGES_VALIDATION_FAILED, exception.getMessage());
    }

    @Test
    void uploadImages_throwsApiExceptionWhenFileIsEmpty() {
        String message = "Image file must not be empty";

        when(clotheUtils.getClotheFromDB(1L)).thenReturn(clothe);
        when(clothe.getImages()).thenReturn(new ArrayList<>());
        when(fileManager.saveFile(file1))
                .thenThrow(new ApiException(HttpStatus.BAD_REQUEST, message));

        ApiException exception = assertThrows(
                ApiException.class, () -> imageService.uploadImages(1L, List.of(file1), EMAIL));

        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatus());
        assertEquals(message, exception.getMessage());
    }

    @Test
    void uploadImages_throwsApiExceptionWhenCannotSaveAFile() {
        String message = "Could not save file";

        when(clotheUtils.getClotheFromDB(1L)).thenReturn(clothe);
        when(clothe.getImages()).thenReturn(new ArrayList<>());
        when(fileManager.saveFile(file1))
                .thenThrow(new ApiException(HttpStatus.INTERNAL_SERVER_ERROR, message));

        ApiException exception = assertThrows(
                ApiException.class, () -> imageService.uploadImages(1L, List.of(file1), EMAIL));

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, exception.getStatus());
        assertEquals(message, exception.getMessage());
    }
}

package com.restapi.styleswap.service;

import com.restapi.styleswap.entity.Category;
import com.restapi.styleswap.entity.Clothe;
import com.restapi.styleswap.entity.User;
import com.restapi.styleswap.exception.ResourceNotFoundException;
import com.restapi.styleswap.payload.ClotheDto;
import com.restapi.styleswap.payload.ClotheResponse;
import com.restapi.styleswap.repository.CategoryRepository;
import com.restapi.styleswap.repository.ClotheRepository;
import com.restapi.styleswap.service.impl.ClothesServiceImpl;
import com.restapi.styleswap.utils.ClotheUtils;
import com.restapi.styleswap.utils.UserUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.web.multipart.MultipartFile;

import java.security.Principal;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class ClothesServiceTest {

    @Mock
    private ClotheRepository clotheRepository;

    @Mock
    private CategoryRepository categoryRepository;

    @Mock
    private ClotheUtils clotheUtils;

    @Mock
    private ImageService imageService;

    @Mock
    private UserUtils userUtils;

    @InjectMocks
    private ClothesServiceImpl clothesService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void getAllClothesByCategory_returnsClothes() {
        long categoryId = 1L;
        int pageNo = 0;
        int pageSize = 10;
        String sortBy = "name";
        String direction = "asc";

        Category category = new Category();
        category.setId(categoryId);

        Clothe clothe = new Clothe();
        clothe.setId(1L);
        clothe.setName("T-Shirt");

        Page<Clothe> clothesPage = new PageImpl<>(Collections.singletonList(clothe));
        when(categoryRepository.existsById(categoryId)).thenReturn(true);
        when(clotheRepository.findByCategoryIdAndIsAvailableTrue(categoryId, PageRequest.of(pageNo, pageSize, Sort.by(sortBy).ascending()))).thenReturn(clothesPage);
        when(clotheUtils.getClotheResponse(pageNo, pageSize, clothesPage)).thenReturn(new ClotheResponse());

        ClotheResponse response = clothesService.getAllClothesByCategory(categoryId, pageNo, pageSize, sortBy, direction);

        assertNotNull(response);
    }

    @Test
    void getAllClothesByCategory_returnsEmptyResponseWhenNoClothes() {
        long categoryId = 1L;
        int pageNo = 0;
        int pageSize = 10;
        String sortBy = "name";
        String direction = "asc";

        Category category = new Category();
        category.setId(categoryId);

        Page<Clothe> emptyPage = new PageImpl<>(Collections.emptyList());
        when(categoryRepository.existsById(categoryId)).thenReturn(true);
        when(clotheRepository
                .findByCategoryIdAndIsAvailableTrue(categoryId,
                                                    PageRequest.of(pageNo, pageSize, Sort.by(sortBy).ascending())))
                .thenReturn(emptyPage);
        when(clotheUtils.getClotheResponse(pageNo, pageSize, emptyPage)).thenReturn(new ClotheResponse());

        ClotheResponse response = clothesService.getAllClothesByCategory(categoryId, pageNo,
                                                                            pageSize, sortBy, direction);

        assertNotNull(response);
        assertNull(response.getClothes());
    }

    @Test
    void getAllClothesByCategory_throwsResourceNotFoundException() {
        long categoryId = 1L;
        int pageNo = 0;
        int pageSize = 10;
        String sortBy = "name";
        String direction = "asc";

        when(categoryRepository.findById(categoryId)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> clothesService.getAllClothesByCategory(categoryId, pageNo, pageSize, sortBy, direction));
    }

    @Test
    void getClotheById_returnsClothe() {
        long clotheId = 1L;
        Clothe clothe = new Clothe();
        clothe.setId(clotheId);
        clothe.setName("T-Shirt");

        when(clotheRepository.findById(clotheId)).thenReturn(Optional.of(clothe));
        when(clotheUtils.mapToDto(clothe)).thenReturn(new ClotheDto());

        ClotheDto result = clothesService.getClotheById(clotheId, Optional.empty());

        assertNotNull(result);
    }

    @Test
    void getClotheById_incrementsViewsForNonOwner() {
        long clotheId = 1L;
        Clothe clothe = new Clothe();
        clothe.setId(clotheId);
        clothe.setName("T-Shirt");
        clothe.setViews(0);

        Principal principal = mock(Principal.class);
        when(principal.getName()).thenReturn("otherUser@example.com");

        when(clotheRepository.findById(clotheId)).thenReturn(Optional.of(clothe));
        when(clotheUtils.isOwner(clotheId, principal.getName())).thenReturn(false);
        when(clotheUtils.mapToDto(clothe)).thenReturn(new ClotheDto());

        ClotheDto result = clothesService.getClotheById(clotheId, Optional.of(principal));

        assertNotNull(result);
        assertEquals(1, clothe.getViews());
        verify(clotheRepository, times(1)).save(clothe);
    }

    @Test
    void getClotheById_doesNotIncrementViewsForOwner() {
        long clotheId = 1L;
        Clothe clothe = new Clothe();
        clothe.setId(clotheId);
        clothe.setName("T-Shirt");
        clothe.setViews(0);

        Principal principal = mock(Principal.class);
        when(principal.getName()).thenReturn("owner@example.com");

        when(clotheRepository.findById(clotheId)).thenReturn(Optional.of(clothe));
        when(clotheUtils.isOwner(clotheId, principal.getName())).thenReturn(true);
        when(clotheUtils.mapToDto(clothe)).thenReturn(new ClotheDto());

        ClotheDto result = clothesService.getClotheById(clotheId, Optional.of(principal));

        assertNotNull(result);
        assertEquals(0, clothe.getViews());
        verify(clotheRepository, never()).save(clothe);
    }

    @Test
    void getClotheById_throwsResourceNotFoundException() {
        long clotheId = 1L;

        when(clotheRepository.findById(clotheId)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> clothesService.getClotheById(clotheId, Optional.empty()));
    }

    @Test
    void addClothe_addsAndReturnsClothe() {
        ClotheDto clotheDto = new ClotheDto();
        clotheDto.setName("T-Shirt");
        List<MultipartFile> images = Collections.emptyList();
        String email = "user@example.com";

        Clothe clothe = new Clothe();
        clothe.setName("T-Shirt");
        clothe.setUser(new User(email));

        when(userUtils.getUser(email)).thenReturn(new User(email));
        when(clotheUtils.mapToEntity(clotheDto)).thenReturn(clothe);
        when(clotheRepository.save(clothe)).thenReturn(clothe);
        when(clotheUtils.mapToDto(clothe)).thenReturn(clotheDto);
        when(imageService.saveImage(any(MultipartFile.class))).thenReturn("image.jpg");

        ClotheDto result = clothesService.addClothe(clotheDto, images, email);

        assertNotNull(result);
        assertEquals("T-Shirt", result.getName());
    }

    @Test
    void updateClothe_updatesAndReturnsClothe() {
        long clotheId = 1L;
        ClotheDto clotheDto = new ClotheDto();
        clotheDto.setName("Updated T-Shirt");
        List<MultipartFile> newImages = Collections.emptyList();
        List<String> deletedImages = Collections.emptyList();
        String email = "user@example.com";

        Clothe clothe = new Clothe();
        clothe.setId(clotheId);
        clothe.setName("T-Shirt");

        when(clotheUtils.getClotheFromDB(clotheId)).thenReturn(clothe);
        when(clotheRepository.save(clothe)).thenReturn(clothe);
        when(clotheUtils.mapToDto(clothe)).thenReturn(clotheDto);
        doNothing().when(imageService).updateImages(clothe, newImages, deletedImages);

        ClotheDto result = clothesService.updateClothe(clotheId, clotheDto, newImages, deletedImages, email);

        assertNotNull(result);
        assertEquals("Updated T-Shirt", result.getName());
    }

    @Test
    void deleteClothe_setsAvailableToFalse() {
        long clotheId = 1L;
        String email = "user@example.com";

        Clothe clothe = new Clothe();
        clothe.setId(clotheId);
        clothe.setName("T-Shirt");
        clothe.setAvailable(true);

        when(clotheUtils.getClotheFromDB(clotheId)).thenReturn(clothe);

        clothesService.deleteClothe(clotheId, email);

        assertFalse(clothe.isAvailable());
        verify(clotheRepository, times(1)).save(clothe);
    }
}
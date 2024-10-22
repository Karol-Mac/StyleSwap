package com.restapi.vinted.repository;

import com.restapi.vinted.entity.Clothe;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ClotheRepositoryTest {

    @Mock
    private ClotheRepository clotheRepository;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void findByCategoryIdReturnsPagedClothes() {
        Pageable pageable = PageRequest.of(0, 10);
        Clothe clothe = new Clothe();
        Page<Clothe> clothePage = new PageImpl<>(List.of(clothe), pageable, 1);
        when(clotheRepository.findByCategoryId(1L, pageable)).thenReturn(clothePage);

        Page<Clothe> result = clotheRepository.findByCategoryId(1L, pageable);

        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        assertEquals(clothe, result.getContent().get(0));
    }

    @Test
    void findByCategoryIdReturnsEmptyPageWhenNoClothesFound() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<Clothe> clothePage = new PageImpl<>(Collections.emptyList(), pageable, 0);
        when(clotheRepository.findByCategoryId(1L, pageable)).thenReturn(clothePage);

        Page<Clothe> result = clotheRepository.findByCategoryId(1L, pageable);

        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    void findByUserIdReturnsPagedClothes() {
        Pageable pageable = PageRequest.of(0, 10);
        Clothe clothe = new Clothe();
        Page<Clothe> clothePage = new PageImpl<>(List.of(clothe), pageable, 1);
        when(clotheRepository.findByUserId(1L, pageable)).thenReturn(clothePage);

        Page<Clothe> result = clotheRepository.findByUserId(1L, pageable);

        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        assertEquals(clothe, result.getContent().get(0));
    }

    @Test
    void findByUserIdReturnsEmptyPageWhenNoClothesFound() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<Clothe> clothePage = new PageImpl<>(Collections.emptyList(), pageable, 0);
        when(clotheRepository.findByUserId(1L, pageable)).thenReturn(clothePage);

        Page<Clothe> result = clotheRepository.findByUserId(1L, pageable);

        assertNotNull(result);
        assertTrue(result.isEmpty());
    }
}
package com.restapi.styleswap.service;

import com.restapi.styleswap.payload.ClotheDto;
import com.restapi.styleswap.payload.ClotheResponse;
import org.springframework.web.multipart.MultipartFile;

import java.security.Principal;
import java.util.List;
import java.util.Optional;

public interface ClothesService {

    ClotheResponse getAllClothesByCategory(long categoryId, int pageNo, int pageSize, String sortBy, String direction);

    ClotheDto getClotheById(long clotheId, Optional<Principal> principal);

    ClotheDto addClothe(ClotheDto clotheDto, List<MultipartFile> images, String email);

    ClotheResponse getMyClothes(int pageNo, int pageSize, String sortBy, String direction, String email);

    ClotheDto updateClothe(long id, ClotheDto clotheDto,
                           List<MultipartFile> newImages, List<String> deletedImages, String email);
    void deleteClothe(long id, String email);
}

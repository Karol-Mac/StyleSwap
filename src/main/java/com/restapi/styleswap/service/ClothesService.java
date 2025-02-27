package com.restapi.styleswap.service;

import com.restapi.styleswap.payload.ClotheDto;
import com.restapi.styleswap.payload.ClotheResponse;

import java.security.Principal;
import java.util.Optional;

public interface ClothesService {

    ClotheResponse getAllClothesByCategory(long categoryId, int pageNo, int pageSize, String sortBy, String direction);

    ClotheDto getClotheById(long clotheId, Optional<Principal> principal);

    ClotheDto addClothe(ClotheDto clotheDto, String email);

    ClotheResponse getMyClothes(int pageNo, int pageSize, String sortBy, String direction, String email);

    ClotheDto updateClothe(long id, ClotheDto clotheDto, String email);

    void deleteClothe(long id, String email);
}

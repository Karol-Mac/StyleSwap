package com.restapi.styleswap.service;

import com.restapi.styleswap.payload.ClotheDto;

import java.util.List;

public interface StorageService {
    List<ClotheDto> getStorage(String email);

    void addClothe(long clotheId, String email);

    void removeClothe(long clotheId, String name);

//    void buyAllClothes(String name);
}
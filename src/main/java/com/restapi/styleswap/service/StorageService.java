package com.restapi.styleswap.service;

import com.restapi.styleswap.payload.ClotheDto;

import java.util.List;

public interface StorageService {
    List<ClotheDto> getStorage(String email);

    void addClothe(int clotheId, String email);

    void removeClothe(int clotheId, String name);

//    void buyAllClothes(String name);
}

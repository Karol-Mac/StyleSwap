package com.restapi.vinted.service.impl;

import com.restapi.vinted.entity.Storage;
import com.restapi.vinted.exception.ApiException;
import com.restapi.vinted.exception.ResourceNotFoundException;
import com.restapi.vinted.payload.ClotheDto;
import com.restapi.vinted.repository.StorageRepository;
import com.restapi.vinted.service.OrderService;
import com.restapi.vinted.service.StorageService;
import com.restapi.vinted.utils.ClotheUtils;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class StorageServiceImpl implements StorageService {

    private final StorageRepository storageRepository;
    private final ClotheUtils clotheUtils;
    private final OrderService orderService;

    public StorageServiceImpl(StorageRepository storageRepository, ClotheUtils clotheUtils,
                              OrderService orderService) {
        this.storageRepository = storageRepository;
        this.clotheUtils = clotheUtils;
        this.orderService = orderService;
    }

    @Override
    public List<ClotheDto> getStorage(String email) {
        var storage = getStorageFromDB(email);

        return storage.getClothes()
                .stream()
                .map(clotheUtils::mapToDto)
                .toList();
    }

    @Override
    public void addClothe(int clotheId, String email) {
        var storage = getStorageFromDB(email);

        var clothe = clotheUtils.getClotheFromDB(clotheId);

        if(clotheUtils.isOwner(clothe.getId(), email))
                throw new ApiException(HttpStatus.BAD_REQUEST, "You can't add your own clothe to storage");

        storage.getClothes().add(clothe);
        storageRepository.save(storage);
    }

    @Override
    public void removeClothe(int clotheId, String email) {
        var storage = getStorageFromDB(email);

        var clothe = clotheUtils.getClotheFromDB(clotheId);

        if (!storage.getClothes().remove(clothe))
            throw new ResourceNotFoundException("Clothe", "id", clotheId);

        storageRepository.save(storage);
    }


    @Override
    public void buyAllClothes(String email) {
        var storage = getStorageFromDB(email);

        storage.getClothes().forEach(clothe -> orderService.createOrder(clothe.getId(), email));
    }

    // FIXME: this might ba a bug - change loadData.sql file - every user should has a storage
    private Storage getStorageFromDB(String email) {
        return storageRepository.findByUserEmail(email)
                .orElseThrow(() -> new ApiException(HttpStatus.INTERNAL_SERVER_ERROR, "unexpected one"));
    }
}